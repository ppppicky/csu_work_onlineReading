package org.example.service.Impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.extern.slf4j.Slf4j;
import org.example.dto.CoverTempDTO;
import org.example.service.CoverTempService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Service
@Slf4j
public class CoverTempSImpl implements CoverTempService {

    private static final String REDIS_KEY_PREFIX = "cover:temp:";
    private static final long EXPIRATION_MINUTES = 30;
    private final String STATICDIR = System.getProperty("user.dir") + "/src/main/resources/static";
    private final ObjectMapper objectMapper = new ObjectMapper();
    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    @Override
    public CoverTempDTO saveTempCover(MultipartFile file) throws IOException {
        String tempKey = UUID.randomUUID().toString();
        String redisKey = REDIS_KEY_PREFIX + tempKey;
        byte[] imageData = file.getBytes();
        String previewUrl = "/covers/temp/" + tempKey + file.getOriginalFilename();
        ;
        // redisTemplate.opsForValue().set(redisKey, imageData, EXPIRATION_MINUTES, TimeUnit.MINUTES);

        // redisTemplate.expire(redisKey, EXPIRATION_MINUTES, TimeUnit.MINUTES);

        CoverTempDTO dto = new CoverTempDTO();
        dto.setImageData(imageData);
        //  dto.setTempKey(tempKey);


        dto.setPreviewUrl(previewUrl); // 预览接口路径
        File tmp = new File(STATICDIR + previewUrl);

        try (InputStream is = file.getInputStream();
             OutputStream os = new FileOutputStream(tmp)) {
            byte[] buffer = new byte[8192];
            int bytesRead;
            while ((bytesRead = is.read(buffer)) != -1) {
                os.write(buffer, 0, bytesRead);
            }
        } catch (IOException e) {
            throw new RuntimeException("文件存储失败", e);
        }
        log.info("path          " + tmp.getAbsolutePath());
        String jsonData = objectMapper.writeValueAsString(dto);
        //   redisTemplate.opsForValue().set(TEMP_BG_PREFIX + tmpBackground.getId(), jsonData, TEMP_EXPIRE_TIME, TimeUnit.MINUTES);

        redisTemplate.opsForValue().set(REDIS_KEY_PREFIX + previewUrl, jsonData, EXPIRATION_MINUTES, TimeUnit.MINUTES);
        return dto;

    }


    @Override
    public void saveTempCoverData(CoverTempDTO tempDTO, String tempCoverKey) throws IOException {

        String redisKey = REDIS_KEY_PREFIX + tempDTO.getPreviewUrl();
        File tmpFile = new File(STATICDIR + tempDTO.getPreviewUrl());

        // 保存到文件系统临时目录
        try (OutputStream os = new FileOutputStream(tmpFile)) {
            os.write(tempDTO.getImageData());
        }

        // 序列化并存入Redis
        ObjectMapper objectMapper = new ObjectMapper();
        String jsonData = objectMapper.writeValueAsString(tempDTO);
        redisTemplate.opsForValue().set(redisKey, jsonData, CoverTempSImpl.EXPIRATION_MINUTES, TimeUnit.MINUTES);
    }

    @Override
    public CoverTempDTO getTempCover(String previewUrl) {

        objectMapper.registerModule(new JavaTimeModule());
        String jsonData = redisTemplate.opsForValue().get(REDIS_KEY_PREFIX + previewUrl);
        if (jsonData == null) {
            throw new IllegalArgumentException("预览信息不存在或已过期");
        }
        CoverTempDTO tmp = new CoverTempDTO();
        try {
            tmp = objectMapper.readValue(jsonData, CoverTempDTO.class);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            log.error("JSON 解析失败: {}", e.getMessage());
        }

        return tmp;
    }

    @Override
    public String moveToPermanent(String previewUrl) {
        String tmp = STATICDIR + previewUrl;
        try {
            String permanentPath = tmp.replace(STATICDIR + "/covers/temp/", STATICDIR + "/covers/");
            File tempFile = new File(tmp);
            File newFile = new File(permanentPath);
            if (tempFile.renameTo(newFile)) {
                return permanentPath;
            }
        } catch (Exception e) {
            e.printStackTrace();
            log.info("文件移动失败" + e.getLocalizedMessage());
        }
        throw new RuntimeException();
    }

    @Override
    public void deleteTempCover(String previewUrl) {
        redisTemplate.delete(REDIS_KEY_PREFIX + previewUrl);
    }
}
