package org.example.service.Impl;

import org.example.config.AdvertisementConfig;
import org.example.entity.Advertisement;
import org.example.repository.AdvertisementRepository;
import org.example.service.AdvertisementService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.concurrent.TimeUnit;

@Service
public class AdvertisementServiceImpl implements AdvertisementService {

    @Autowired
    private AdvertisementRepository advertisementRepository;

    @Autowired
    private RedisTemplate<String, byte[]> redisTemplate;

    @Autowired
    private RedisTemplate<String, Integer> integerRedisTemplate; // 用于存储广告观看次数

    @Autowired
    private AdvertisementConfig advertisementConfig; // 读取广告观看次数上限

    /**
     * 获取广告观看次数
     */
    @Override
    public Integer getAdWatchCount(String redisKey) {
        return Optional.ofNullable(integerRedisTemplate.opsForValue().get(redisKey)).orElse(0);
    }

    /**
     * 获取一个随机可用的广告
     */
    @Override
    public Advertisement getRandomActiveAd() {
        List<Advertisement> ads = advertisementRepository.findByIsActive((byte) 1);
        return ads.isEmpty() ? null : ads.get(new Random().nextInt(ads.size()));
    }

    /**
     * 从 Redis 获取广告视频
     */
    @Override
    public byte[] getAdVideo(Integer adId) {
        String redisKey = "ad_video:" + adId;
        return redisTemplate.opsForValue().get(redisKey);
    }

    /**
     * 增加广告观看次数
     */
    @Override
    public void incrementAdWatchCount(String redisKey) {
        integerRedisTemplate.opsForValue().increment(redisKey, 1);
        integerRedisTemplate.expire(redisKey, 1, TimeUnit.DAYS);  // 设置 24 小时过期
    }

    /**
     * 存储广告信息到数据库
     */
    @Override
    public int saveAdInfo(Advertisement advertisement) {
        Advertisement savedAd = advertisementRepository.save(advertisement);
        return savedAd.getAdId();
    }

    /**
     * 上传广告视频到 Redis
     */
    @Override
    public void uploadAdVideo(int adId, MultipartFile file) throws IOException {
        String redisKey = "ad_video:" + adId;
        redisTemplate.opsForValue().set(redisKey, file.getBytes());
        redisTemplate.expire(redisKey, 7, TimeUnit.DAYS); // 设定 7 天有效期
    }

    /**
     * 获取所有广告信息
     */
    @Override
    public List<Advertisement> getAllAds() {
        return advertisementRepository.findAll();
    }

    @Override
    public Optional<Advertisement> getAdById(int adId) {
        return advertisementRepository.findById(adId);
    }
}
