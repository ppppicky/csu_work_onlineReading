package org.example.service.Impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.extern.slf4j.Slf4j;
import org.example.dto.BackgroundDTO;
import org.example.entity.BackgroundResource;
import org.example.entity.BackgroundType;
import org.example.entity.Users;
import org.example.repository.BackgroundRepo;
import org.example.repository.UserRepository;
import org.example.service.BackgroundService;
import org.example.util.BackgroundFileDealer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.RenderedImage;
import java.io.File;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
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
    private RedisTemplate<String, String> redisTemplate;

    private BackgroundFileDealer backgroundFileDealer = new BackgroundFileDealer();
    private static final String TEMP_BG_PREFIX = "temp_bg:";
    private static final long TEMP_EXPIRE_TIME=6000; // 30 分钟过期
    private final String TEMP_DIR = System.getProperty("user.dir") + "/src/main/resources/static/backgrounds/tmp/";
    private final String PERM_DIR = System.getProperty("user.dir") + "/src/main/resources/static/backgrounds/";
    private final ObjectMapper objectMapper = new ObjectMapper();


    @Override
    public BackgroundDTO uploadTemporary(MultipartFile file) {
        objectMapper.registerModule(new JavaTimeModule());
        BackgroundDTO tmpBackground = new BackgroundDTO();
        try {
            BackgroundType resourceType = determineResourceType(file.getContentType());
            String tempPath = backgroundFileDealer.saveTemporary(file);
            String thumbnailPath = backgroundFileDealer.generateThumbnail(tempPath,resourceType);
            tmpBackground.setFileSize(file.getSize());
            tmpBackground.setId(UUID.randomUUID().toString());
            tmpBackground.setResourceType(resourceType);
            tmpBackground.setStoragePath(tempPath);
            tmpBackground.setThumbnailPath(thumbnailPath);
            tmpBackground.setCreateTime(LocalDateTime.now());

            // 存入 Redis，30 分钟后自动删除
            String jsonData = objectMapper.writeValueAsString(tmpBackground);
            redisTemplate.opsForValue().set(TEMP_BG_PREFIX + tmpBackground.getId(), jsonData, TEMP_EXPIRE_TIME, TimeUnit.MINUTES);
        } catch (Exception e) {
            log.error("背景上传失败: {}", e.getMessage());
        }
        return tmpBackground;
    }
    @Override
    public BackgroundDTO uploadTemporary(String gradient) {
        objectMapper.registerModule(new JavaTimeModule());
        BackgroundDTO tmpBackground = new BackgroundDTO();
        try {
            Image image=backgroundFileDealer.generateGradientImage(gradient,1600,900);
         String tempPath = TEMP_DIR + UUID.randomUUID()+"_gradient.png";
            File file = new File(tempPath);
        ImageIO.write((RenderedImage) image, "png", file);
            //String tempPath = backgroundFileDealer.saveTemporary(file);
            String thumbnailPath = backgroundFileDealer.generateThumbnail(tempPath,BackgroundType.GRADIENT);
            tmpBackground.setId(UUID.randomUUID().toString());
            tmpBackground.setResourceType(BackgroundType.GRADIENT);
            tmpBackground.setStoragePath(tempPath);
            tmpBackground.setThumbnailPath(thumbnailPath);
            tmpBackground.setCreateTime(LocalDateTime.now());

            // 存入 Redis，30 分钟后自动删除
            String jsonData = objectMapper.writeValueAsString(tmpBackground);
            redisTemplate.opsForValue().set(TEMP_BG_PREFIX + tmpBackground.getId(), jsonData, TEMP_EXPIRE_TIME, TimeUnit.MINUTES);
        } catch (Exception e) {
            log.error("背景上传失败: {}", e.getMessage());
        }
        return tmpBackground;
    }
    @Override
    public void confirmSave(String resourceId, Integer userId) {
        objectMapper.registerModule(new JavaTimeModule());
        String jsonData = redisTemplate.opsForValue().get(TEMP_BG_PREFIX + resourceId);
log.info(jsonData);
        if (jsonData == null) {
            throw new IllegalArgumentException("预览信息不存在或已过期");
        }
        BackgroundDTO tmp = new BackgroundDTO();
        try {
            tmp= objectMapper.readValue(jsonData, BackgroundDTO.class);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            log.error("JSON 解析失败: {}", e.getMessage());
        }

        Users users = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("用户不存在"));
        String finalPath = backgroundFileDealer.moveToPermanent(tmp.getStoragePath());
            BackgroundResource resource = new BackgroundResource();
log.info("dddddd"+finalPath);
            resource.setResourceType(tmp.getResourceType());
            resource.setFileSize(tmp.getFileSize());
            resource.setUser(users);
            resource.setStoragePath(finalPath); // 修改这里
            resource.setThumbnailPath(tmp.getThumbnailPath());
            resource.setCreateTime(LocalDateTime.now());

            backgroundRepo.save(resource);

        redisTemplate.delete(TEMP_BG_PREFIX + resourceId); // 确认保存后删除 Redis 记录

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
    public List<BackgroundDTO> getUserBackgrounds(Users user) {
        List<BackgroundResource> resources = backgroundRepo.findByUser(user)
                .orElseThrow(() -> new IllegalArgumentException("no background"));

        List<BackgroundDTO> backgroundDTOS = resources.stream().map((BackgroundResource resource) -> {
            BackgroundDTO dto = new BackgroundDTO();
            dto.setId(String.valueOf(resource.getBackgroundId()));
            dto.setResourceType(resource.getResourceType());
            dto.setStoragePath(resource.getStoragePath());
            dto.setThumbnailPath(resource.getThumbnailPath());
            dto.setFileSize(resource.getFileSize());
            dto.setCreateTime(resource.getCreateTime());
            return dto;
        }).collect(Collectors.toList());

        return backgroundDTOS;
    }

    @Override
    public BackgroundDTO getBackground(Integer resourceId) {
        BackgroundResource backgroundResource = backgroundRepo.findById(resourceId)
                .orElseThrow(() -> new IllegalArgumentException("background not existed"));
        BackgroundDTO backgroundDTO = new BackgroundDTO();
        backgroundDTO.setResourceType(backgroundResource.getResourceType());
        backgroundDTO.setFileSize(backgroundResource.getFileSize());
        backgroundDTO.setId(String.valueOf(backgroundResource.getBackgroundId()));
        backgroundDTO.setStoragePath(backgroundResource.getStoragePath());
        backgroundDTO.setThumbnailPath(backgroundResource.getThumbnailPath());
        return backgroundDTO;
    }



}
