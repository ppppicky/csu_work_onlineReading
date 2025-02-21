package org.example.service;

import org.example.entity.Advertisement;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.util.List;
import java.util.Optional;

public interface AdvertisementService {

    Integer getAdWatchCount(String redisKey);

    Advertisement getRandomActiveAd();

    byte[] getAdVideo(Integer adId);

    Optional<Advertisement> getAdById(int adId);

    void incrementAdWatchCount(String redisKey);

    int saveAdInfo(Advertisement advertisement);

    void uploadAdVideo(int adId, MultipartFile file) throws IOException;

    List<Advertisement> getAllAds();
}
