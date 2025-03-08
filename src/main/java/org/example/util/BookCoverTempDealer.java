package org.example.util;


import net.coobird.thumbnailator.Thumbnails;
import org.example.dto.CoverTempDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Component
public class BookCoverTempDealer {

    private static final String REDIS_KEY_PREFIX = "cover:temp:";
    private static final long EXPIRATION_MINUTES = 30;
    @Autowired
    private RedisTemplate<String, byte[]> redisTemplate; // 修改为 byte[] 类型


    public CoverTempDTO saveTempCover(MultipartFile file) throws IOException {
        String tempKey = UUID.randomUUID().toString();
        String redisKey = REDIS_KEY_PREFIX + tempKey;
        byte[] imageData = file.getBytes();
        //      String previewUrl = "/covers/temp/" + tempKey + file.getOriginalFilename();
        ByteArrayInputStream inputStream = new ByteArrayInputStream(imageData);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            // 生成图片缩略图
            Thumbnails.of(inputStream)
                    .size(600, 1000)
                    .toOutputStream(outputStream);
        byte[] thumbImage= outputStream.toByteArray();
        CoverTempDTO dto = new CoverTempDTO();
        dto.setImageData(thumbImage);
        dto.setTempKey(tempKey);
        //  dto.setPreviewUrl(previewUrl); // 预览接口路径
        // 存储到 Redis
        redisTemplate.opsForValue().set(redisKey, thumbImage, EXPIRATION_MINUTES, TimeUnit.MINUTES);
        return dto;

    }
//  Thumbnails.of(image)
//            .size(200, 200)
//                    .toFile(new File(outputPath));
//} catch (Exception e) {
//        e.printStackTrace();

    public void saveTempCoverData(CoverTempDTO tempDTO, String tempCoverKey) throws IOException {

        //  String redisKey = REDIS_KEY_PREFIX + tempDTO.getPreviewUrl();
        String tempKey = UUID.randomUUID().toString();
        String redisKey = REDIS_KEY_PREFIX + tempKey;
        redisTemplate.opsForValue().set(redisKey, tempDTO.getImageData(), EXPIRATION_MINUTES, TimeUnit.MINUTES);
    }

    public CoverTempDTO getTempCover(String tempKey) {

        String redisKey = REDIS_KEY_PREFIX + tempKey;
        byte[] imageData = redisTemplate.opsForValue().get(redisKey);
        if (imageData == null) {
            throw new IllegalArgumentException("预览信息不存在或已过期");
        }

        CoverTempDTO dto = new CoverTempDTO();
        dto.setImageData(imageData);
        dto.setTempKey(tempKey);
        // dto.setPreviewUrl("/covers/temp/" + tempKey); // 可根据需要生成预览 URL
        return dto;
    }


    public void deleteTempCover(String previewUrl) {
        redisTemplate.delete(REDIS_KEY_PREFIX + previewUrl);
    }
}


