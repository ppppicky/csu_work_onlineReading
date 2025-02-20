package org.example.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.example.dto.CoverTempDTO;
import org.example.service.CoverTempService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Component
public class BookCoverTempDealer {

    private static final String REDIS_KEY_PREFIX = "cover:temp:";
    private static final long EXPIRATION_MINUTES = 30;
    private final String STATICDIR = System.getProperty("user.dir") + "/src/main/resources/static";
    private final ObjectMapper objectMapper = new ObjectMapper();
    @Autowired
    private RedisTemplate<String, byte[]> redisTemplate; // 修改为 byte[] 类型


    public CoverTempDTO saveTempCover(MultipartFile file) throws IOException {
        String tempKey = UUID.randomUUID().toString();
        String redisKey = REDIS_KEY_PREFIX + tempKey;
        byte[] imageData = file.getBytes();
  //      String previewUrl = "/covers/temp/" + tempKey + file.getOriginalFilename();

        CoverTempDTO dto = new CoverTempDTO();
        dto.setImageData(imageData);
        dto.setTempKey(tempKey);
      //  dto.setPreviewUrl(previewUrl); // 预览接口路径
        // 存储到 Redis
        redisTemplate.opsForValue().set(redisKey, imageData, EXPIRATION_MINUTES, TimeUnit.MINUTES);
        return dto;

    }


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

//    @Override
//    public String moveToPermanent(String tempKey) {
////        String tmp = STATICDIR + previewUrl;
////        try {
////            String permanentPath = tmp.replace(STATICDIR + "/covers/temp/", STATICDIR + "/covers/");
////            File tempFile = new File(tmp);
////            File newFile = new File(permanentPath);
////            if (tempFile.renameTo(newFile)) {
////                return permanentPath;
////            }
////        } catch (Exception e) {
////            e.printStackTrace();
////            log.info("文件移动失败" + e.getLocalizedMessage());
////        }
////        throw new RuntimeException();
//        byte[] imageData = redisTemplate.opsForValue().get(REDIS_KEY_PREFIX + tempKey);
//        if (imageData == null) {
//            throw new IllegalArgumentException("封面数据不存在或已过期");
//        }
//
//        // 保存到永久目录
//        String filename = UUID.randomUUID() + ".jpg";
//        String permanentPath = "/covers/permanent/" + filename;
//        File permanentFile = new File(STATICDIR + permanentPath);
//        try {
//            Files.write(permanentFile.toPath(), imageData);
//        } catch (IOException e) {
//            log.error("封面保存失败: {}", e.getMessage());
//            throw new RuntimeException("封面保存失败");
//        }
//
//        // 删除 Redis 中的临时数据
//        redisTemplate.delete(REDIS_KEY_PREFIX + tempKey);
//
//        return permanentPath;
//    }


    public void deleteTempCover(String previewUrl) {
        redisTemplate.delete(REDIS_KEY_PREFIX + previewUrl);
    }
}


