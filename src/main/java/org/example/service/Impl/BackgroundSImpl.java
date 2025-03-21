package org.example.service.Impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import io.netty.util.concurrent.FailedFuture;
import lombok.extern.slf4j.Slf4j;
import org.example.dto.BackgroundDTO;
import org.example.entity.BackgroundResource;
import org.example.entity.BackgroundType;
import org.example.entity.Users;
import org.example.repository.BackgroundRepo;
import org.example.repository.UserRepository;
import org.example.service.BackgroundService;
import org.example.util.BackgroundDealer;
import org.example.util.GlobalException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.RenderedImage;
import java.io.ByteArrayOutputStream;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Slf4j
@Service
public class BackgroundSImpl implements BackgroundService {
    @Autowired
    private BackgroundRepo backgroundRepo;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    // @Qualifier("stringRedisTemplate")
    private RedisTemplate<String, String> redisTemplate;

    @Autowired
    @Qualifier("ioThreadPool")
    private Executor backgroundThreadPool;

    @Autowired
    BackgroundDealer fileDealer;

    private static final String TEMP_BG_PREFIX = "temp_bg:";
    private static final long TEMP_EXPIRE_TIME = 6000; // 30 分钟过期
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Async("ioThreadPool")
    @Override
    public CompletableFuture<BackgroundDTO> uploadTemporary(MultipartFile file) {
        BackgroundDTO tmpBackground = new BackgroundDTO();
        try {
            String tempObjectName = fileDealer.saveTemporary(file);
            tmpBackground.setId(UUID.randomUUID().toString());
            tmpBackground.setStorageKey(tempObjectName);
            tmpBackground.setResourceType(determineResourceType(file.getContentType()));

            // 存储元数据到 Redis（如文件类型、大小等）
            String jsonData = objectMapper.writeValueAsString(tmpBackground);
            redisTemplate.opsForValue().set(
                    TEMP_BG_PREFIX + tmpBackground.getId(),
                    jsonData,
                    30, TimeUnit.MINUTES  // 30分钟后自动过期
            );
            //  log.info("文件上传完成: {}", tempObjectName);
            log.info("io____" + backgroundThreadPool.toString());

        } catch (Exception e) {
            log.error("临时资源上传失败 | ID={}, Error={}", tmpBackground.getId(), e.getMessage());
        }
        return CompletableFuture.completedFuture(tmpBackground);
    }

    @Async("ioThreadPool")
    @Override
    public CompletableFuture<BackgroundDTO> uploadTemporary(String gradient) {
        objectMapper.registerModule(new JavaTimeModule());
        BackgroundDTO tmpBackground = new BackgroundDTO();
        try {
            Image image = fileDealer.generateGradientImage(gradient, 1600, 900);
            //     tmpBackground.setId(UUID.randomUUID().toString());
            tmpBackground.setResourceType(BackgroundType.GRADIENT);

            try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
                ImageIO.write((RenderedImage) image, "jpg", baos);
                byte[] imageData = baos.toByteArray();
                String tempObjectName = fileDealer.saveTemporary(
                        imageData,
                        "gradient.jpg",
                        "image/jpeg"
                );
                tmpBackground.setStorageKey(tempObjectName);
                tmpBackground.setResourceType(BackgroundType.GRADIENT);
                tmpBackground.setId(UUID.randomUUID().toString());
                String jsonData = objectMapper.writeValueAsString(tmpBackground);
                redisTemplate.opsForValue().set(
                        TEMP_BG_PREFIX + tmpBackground.getId(),
                        jsonData,
                        TEMP_EXPIRE_TIME, TimeUnit.MINUTES
                );
                log.info("渐变背景上传完成: {}", tempObjectName);
            }

         //   log.info("io____" + backgroundThreadPool.toString());
        } catch (Exception e) {
            log.error("背景上传失败: {}", e.getMessage());
        }
        return CompletableFuture.completedFuture(tmpBackground);
    }

    @Async("ioThreadPool")
    @Override
    public CompletableFuture<String> confirmSave(String resourceId, Integer userId) throws Exception {
        // 从 Redis 获取元数据
        String jsonData = redisTemplate.opsForValue().get(TEMP_BG_PREFIX + resourceId);
        if (jsonData == null) {
            return CompletableFuture.completedFuture("ERROR: 临时文件不存在");
        }
        BackgroundDTO tmp = objectMapper.readValue(jsonData, BackgroundDTO.class);

        // 移动文件到永久存储
        try {
            String permanentKey = fileDealer.moveToPermanent(tmp.getStorageKey());
            // 清理 Redis 记录
            redisTemplate.delete(TEMP_BG_PREFIX + resourceId);
            Users users = userRepository.findById(userId)
                    .orElseThrow(() -> new GlobalException.UserNotFoundException("user not existed"));

            // 保存到数据库
            BackgroundResource resource = new BackgroundResource();
            resource.setStorageKey(permanentKey);
            resource.setUser(users);
            resource.setResourceType(tmp.getResourceType());
            resource.setCreateTime(LocalDateTime.now());
            //resource.setUser(userRepository.findById(userId).orElseThrow());
            backgroundRepo.save(resource);

     //       log.info("背景保存成功: {},resourceId={}, userId={}", permanentKey, resourceId, userId);
     //       log.info("io____" + backgroundThreadPool.toString());
            return CompletableFuture.completedFuture("SUCCESS: 文件保存成功");
        } catch (Exception e) {
            return CompletableFuture.completedFuture("ERROR: 文件移动失败");
        }


    }


    private BackgroundType determineResourceType(String mimeType) {
        if (mimeType.equals("image/gif")) {
            return BackgroundType.GIF;
        } else if (mimeType.startsWith("video/")) {
            return BackgroundType.VIDEO;
        } else if (mimeType.startsWith("image/")) {
            return BackgroundType.IMAGE;
        } else {
            return BackgroundType.GRADIENT;
        }
    }

    @Override
    public List<String> getUserBackgroundsUrl(Users user) {

        List<BackgroundResource> resources = backgroundRepo.findByUser(user)
                .orElse(Collections.emptyList());
        List<String> urls = resources.stream().map(
                (resource) -> {
                    try {
                        return fileDealer.getPresignedUrl(resource.getStorageKey());
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                }
        ).collect(Collectors.toList());
        return urls;
    }

    @Override
    public String getBackgroundUrl(Integer resourceId) throws Exception {
        BackgroundResource resource = backgroundRepo.findById(resourceId)
                .orElseThrow(() -> new IllegalArgumentException("背景资源不存在"));
        return fileDealer.getPresignedUrl(resource.getStorageKey());

    }

    private String getFileExtension(BackgroundType resourceType) {
        switch (resourceType) {
            case IMAGE:
                return ".jpg";
            case GIF:
                return ".gif";
            case VIDEO:
                return ".mp4";
            case GRADIENT:
                return ".png";
            default:
                return ".bin";
        }
    }
}
