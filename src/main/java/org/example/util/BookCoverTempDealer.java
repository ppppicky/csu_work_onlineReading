package org.example.util;


import lombok.extern.slf4j.Slf4j;
import net.coobird.thumbnailator.Thumbnails;
import org.example.dto.CoverTempDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Component
@Slf4j
public class BookCoverTempDealer {

    private static final String REDIS_COVER_KEY_PREFIX = "cover:temp:";
    private static final long EXPIRATION_MINUTES = 30;
    @Autowired
    private RedisTemplate<String, byte[]> redisTemplate; // 修改为 byte[] 类型


    /**
     * 保存临时图书封面数据
     * @param file
     * @return 返回一个包含临时封面数据和临时键的CoverTempDTO对象
     * @throws IOException
     */
    public CoverTempDTO saveTempCover(MultipartFile file) throws IOException {
        String tempKey = UUID.randomUUID().toString();
        String redisKey = REDIS_COVER_KEY_PREFIX + tempKey;
        byte[] imageData = file.getBytes();
        //      String previewUrl = "/covers/temp/" + tempKey + file.getOriginalFilename();
        ByteArrayInputStream inputStream = new ByteArrayInputStream(imageData);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

            // 生成图片缩略图
            Thumbnails.of(inputStream)
                    .size(600, 1000)
                    .toOutputStream(outputStream);
        byte[] thumbImage= outputStream.toByteArray();

        //封装临时封面数据
        CoverTempDTO dto = new CoverTempDTO();
        dto.setImageData(thumbImage);
        dto.setTempKey(tempKey);
        //  dto.setPreviewUrl(previewUrl); // 预览接口路径
        // 存储到 Redis,过期时间为 30 分钟
        redisTemplate.opsForValue().set(redisKey, thumbImage, EXPIRATION_MINUTES, TimeUnit.MINUTES);
        return dto;

    }


    /**
     * 根据临时键获取临时图书封面数据
     * @param tempKey
     * @return 返回一个包含临时封面数据和临时键的 CoverTempDTO 对象
     */
    public CoverTempDTO getTempCover(String tempKey) {
        // 从 Redis 中获取对应的图片
        String redisKey = REDIS_COVER_KEY_PREFIX + tempKey;
        byte[] imageData = redisTemplate.opsForValue().get(redisKey);
        if (imageData == null) {
            log.warn("预览信息不存在或已过期");
        }

        CoverTempDTO dto = new CoverTempDTO();
        dto.setImageData(imageData);
        dto.setTempKey(tempKey);
        // dto.setPreviewUrl("/covers/temp/" + tempKey); // 可根据需要生成预览 URL
        return dto;
    }


    public void deleteTempCover(String previewUrl) {
        redisTemplate.delete(REDIS_COVER_KEY_PREFIX + previewUrl);
    }
}


