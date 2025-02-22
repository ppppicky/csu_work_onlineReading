package org.example.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.example.config.AdvertisementConfig;
import org.example.entity.AdWatchLog;
import org.example.entity.Advertisement;
import org.example.repository.ChapterRepo;
import org.example.service.AdWatchLogService;
import org.example.service.AdvertisementService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Slf4j
@RestController
@RequestMapping("/advertisement")
@Api(tags = "广告管理")
public class AdvertisementController {

    @Autowired
    private AdvertisementService advertisementService;

    @Autowired
    private AdWatchLogService adWatchLogService;

    @Autowired
    private ChapterRepo bookChapterRepository;

    @Autowired
    private AdvertisementConfig advertisementConfig; // 读取广告观看次数上限

    /**
     * 上传广告
     */
    @PostMapping("/upload")
    @ApiOperation("上传广告视频")
    public ResponseEntity<String> uploadAd(@RequestParam("file") MultipartFile file,
                                           @RequestParam String adType) throws IOException {
        log.info("上传广告视频");
        Advertisement advertisement = new Advertisement();
        advertisement.setAdType(adType);
//        advertisement.setAdDuration(adDuration);
        advertisement.setIsActive((byte) 1);

        int adId = advertisementService.saveAdInfo(advertisement);
        advertisementService.uploadAdVideo(adId, file);

        return ResponseEntity.ok("广告上传成功！ID: " + adId);
    }

    /**
     * 获取所有广告信息
     */
    @GetMapping("/list")
    @ApiOperation("获取所有广告信息")
    public ResponseEntity<List<Advertisement>> getAllAds() {
        log.info("获取所有广告信息");
        List<Advertisement> ads = advertisementService.getAllAds();
        return ResponseEntity.ok(ads);
    }

    /**
     * 用户点击 "看广告免费阅读"，后端检查观看次数限制、选择广告，并直接返回广告流
     */
    @GetMapping("/playAd")
    @ApiOperation("用户观看广告并解锁章节")
    public ResponseEntity<?> playAd(@RequestParam int userId) throws IOException {
        log.info("用户观看广告并解锁章节");
        // 1. 读取每日广告观看上限
        int dailyLimit = advertisementConfig.getDailyLimit();

        // 2. 检查每日观看限制
        String redisKey = "ad_watch_limit:" + userId + ":" + LocalDate.now();
        Integer watchCount = advertisementService.getAdWatchCount(redisKey);

        if (watchCount != null && watchCount >= dailyLimit) {
            return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS)
                    .body("今日广告观看次数已达上限！（限制：" + dailyLimit + " 次）");
        }

        // 3. 获取一个可用的广告
        Advertisement ad = advertisementService.getRandomActiveAd();
        if (ad == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("当前无可用广告");
        }

        // 4. 获取广告视频数据
        byte[] videoData = advertisementService.getAdVideo(ad.getAdId());
        if (videoData == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("广告视频未找到");
        }

        // 5. 直接返回广告 ID 和视频数据，前端存储 adId 以便后续 watchAd 记录观看行为
        return ResponseEntity.ok()
                .header("Content-Type", "video/mp4")
                .header("Content-Length", String.valueOf(videoData.length))
                .header("Ad-Id", String.valueOf(ad.getAdId()))  // 添加广告 ID 到响应头
                .body(videoData);
    }


    /**
     *  记录用户观看广告并解锁章节
     */
    @PostMapping("/watchAd")
    @ApiOperation("用户观看广告记录")
    public ResponseEntity<?> watchAd(@RequestParam int userId,
                                     @RequestParam int bookId,
                                     @RequestParam int chapterId,
                                     @RequestParam int adId) { // adId 由前端传递，确保用户观看正确的广告
        log.info("用户观看广告记录");
        // 1. 读取每日广告观看上限
        int dailyLimit = advertisementConfig.getDailyLimit();

        // 2. 检查每日观看限制
        String redisKey = "ad_watch_limit:" + userId + ":" + LocalDate.now();
        Integer watchCount = advertisementService.getAdWatchCount(redisKey);

        if (watchCount != null && watchCount >= dailyLimit) {
            return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS)
                    .body("今日广告观看次数已达上限！");
        }

        // 3. 检查章节 ID 是否存在
        boolean isValidChapter = bookChapterRepository.existsByBookIdAndChapterId(bookId, chapterId);
        if (!isValidChapter) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("章节 " + chapterId + " 不属于书籍 " + bookId);
        }

        // 4. 检查广告是否有效
        Optional<Advertisement> optionalAd = advertisementService.getAdById(adId);
        if (!optionalAd.isPresent() || optionalAd.get().getIsActive() == 0) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("广告不存在或已停用！");
        }

        // 5. 记录观看广告行为
        AdWatchLog log = new AdWatchLog();
        log.setUserId(userId);
        log.setAdId(adId);
        log.setBookId(bookId);
        log.setChapterId(chapterId);
        log.setWatchTime(LocalDateTime.now());
        adWatchLogService.save(log);

        // 6. 增加观看次数
        advertisementService.incrementAdWatchCount(redisKey);

        return ResponseEntity.ok("广告观看成功，已解锁 1 章节！");
    }



}

