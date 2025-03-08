package org.example.service;

import io.minio.errors.*;
import org.example.entity.Advert;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.List;

public interface AdService {
    public Advert getMatchAdvert(List<String> keywords);

    // 修改次数检查逻辑
    boolean canWatchAd(Integer userId);

    // 记录观看行为
    void recordAdWatch(Integer userId, String advertId);

    public void upload(String keyword, MultipartFile file) throws Exception;

    void deleteAd(String adId) throws Exception;

    List<Advert> getAllAds();
}
