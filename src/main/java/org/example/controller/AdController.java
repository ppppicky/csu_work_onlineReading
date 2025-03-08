package org.example.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.example.entity.Advert;
import org.example.service.AdService;
import org.example.service.BookIndexService;
import org.example.service.BookService;
import org.example.vo.AdvertVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("/ad")
@Api(value = "广告控制器",tags = "广告管理")
public class AdController {
    @Autowired
    private AdService adService;
    @Autowired
    private BookIndexService indexService;
    @Autowired
    private BookService bookService;


    @PostMapping("/playAd/{userId}/{bookId}")
    @ApiOperation("用户观看广告并解锁章节")
    public ResponseEntity<?> playAd(
            @PathVariable(name = "userId") Integer userId, @PathVariable(name = "bookId") Integer bookId) throws IOException {
        if (!adService.canWatchAd(userId)) {
            return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS)
                    .body("Maximum number of ads reached today");
        }
        bookService.getBook(bookId);
        List<String> keywords = indexService.getKeywords(bookId);
        Advert advert = adService.getMatchAdvert(keywords);
        AdvertVO advertVO = new AdvertVO();
        advertVO.setAdvertId(advert.getId());
        advertVO.setVideoUrl(advert.getVideoUrl());
        adService.recordAdWatch(userId, advertVO.getAdvertId());
        return ResponseEntity.ok(advertVO);
    }

    @PostMapping("/upload")
    @ApiOperation("上传广告视频到MinIO")
    public ResponseEntity<String> uploadAd(
            @RequestParam("file") MultipartFile file, @RequestParam String keywords) {
        try {
            adService.upload(keywords, file);

            return ResponseEntity.ok("upload successfully");
        } catch (Exception e) {
            log.error("广告上传失败", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("error");
        }
    }

    @GetMapping("/allAds")
    @ApiOperation(tags = "获取所有广告",value = "获取所有广告")
    public ResponseEntity<List< AdvertVO>> getAllAds(){
      try {
          List<AdvertVO> advertVO = adService.getAllAds().stream().map(
                  advert -> new AdvertVO(advert.getId(), advert.getVideoUrl())
          ).collect(Collectors.toList());
          return ResponseEntity.ok(advertVO);
      }catch (Exception e){
          return ResponseEntity.notFound().build();
      }
    }
    @DeleteMapping("/delete/{id}")
    @ApiOperation("删除指定广告")
    public ResponseEntity<String> deleteAd(
            @PathVariable String id){
        try {
            adService.deleteAd(id);
            return ResponseEntity.ok("delete successfully");
        }catch (Exception e){
            return ResponseEntity.badRequest().body("fail to delete");

        }
    }
}
