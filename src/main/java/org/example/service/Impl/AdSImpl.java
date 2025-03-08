package org.example.service.Impl;

import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.functionscore.FunctionScoreQueryBuilder;
import org.elasticsearch.index.query.functionscore.ScoreFunctionBuilders;
import org.example.entity.Advert;
import org.example.entity.UserAdRecord;
import org.example.repository.UserAdRepository;
import org.example.service.AdService;
import org.example.util.AdRecordRedisCounter;
import org.example.util.AdsMinioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.query.NativeSearchQuery;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Slf4j
public class AdSImpl implements AdService {
    @Autowired
    private UserAdRepository userAdRepo;


    @Autowired
    private ElasticsearchOperations esOperations;

    @Autowired
    private AdRecordRedisCounter adRecordRedisCounter;
    @Autowired
    private AdsMinioService adsMinioService;

    // 通过ES匹配广告（根据章节内容关键词）
    @Override
    public Advert getMatchAdvert(List<String> keywords) {

        String keywordQuery = String.join(" ", keywords);
        // 构建ES查询
        NativeSearchQuery query = new NativeSearchQueryBuilder()
                .withQuery(QueryBuilders.matchQuery("keywords", keywordQuery)).build();
        // 获取匹配的广告
        Advert advert = esOperations.search(query, Advert.class)
                .getSearchHits().stream()
                .findFirst().map(hit -> hit.getContent())
                .orElseGet(() -> this.findRandomAd());
        log.info("ismpl  getMatchAd:" + advert);
        // 生成广告视频 URL
        try {
            advert.setVideoUrl(adsMinioService.getPresignedUrl(advert.getId()));
        } catch (Exception e) {
            log.error("Failed to generate ad URL", e);
        }
        return advert;
    }

    private Advert findRandomAd() {
        NativeSearchQuery query = new NativeSearchQueryBuilder()
                .withQuery(QueryBuilders.functionScoreQuery(
                        QueryBuilders.matchAllQuery(),  // 先匹配所有广告
                        new FunctionScoreQueryBuilder.FilterFunctionBuilder[]{
                                new FunctionScoreQueryBuilder.FilterFunctionBuilder(
                                        ScoreFunctionBuilders.randomFunction()
                                )
                        }
                ))
                .withPageable(PageRequest.of(0, 1))  // 仅获取 1 条广告
                .build();

        return esOperations.search(query, Advert.class)
                .getSearchHits().stream()
                .findFirst()
                .map(hit -> hit.getContent())
                .orElse(null);
    }


    @Override
    // 修改次数检查逻辑
    public boolean canWatchAd(Integer userId) {
        return adRecordRedisCounter.canWatchAd(userId);
    }

    // 记录观看行为
    @Override
    public void recordAdWatch(Integer userId, String advertId) {
        UserAdRecord record = new UserAdRecord();
        record.setUserId(userId);
        record.setWatchTime(LocalDateTime.now());
        record.setAdvertId(advertId);
        userAdRepo.save(record);
        adRecordRedisCounter.decrementViews(userId);
       // log.info("adsmpl remaining   "+adRecordRedisCounter.getRemainingViews(userId));
    }

    @Override
    public void upload(String keyword, MultipartFile file) throws Exception {
        String objectId = "ad_" + UUID.randomUUID();
        adsMinioService.uploadFile(file, objectId);
        Advert ad = new Advert();
        ad.setKeywords(keyword);
        ad.setId(objectId);

        esOperations.save(ad);
    }

    @Override
    public List<Advert> getAllAds() {
        NativeSearchQuery query = new NativeSearchQueryBuilder()
                .withQuery(QueryBuilders.matchAllQuery())
                .build();
        return esOperations.search(query, Advert.class)
                .getSearchHits().stream()
                .map(hit -> {
                    Advert ad = hit.getContent();
                    try {
                        ad.setVideoUrl(adsMinioService.getPresignedUrl(ad.getId()));
                    } catch (Exception e) {
                        log.error("生成广告URL失败，广告ID: {}", ad.getId(), e);
                    }
                    return ad;
                })
                .collect(Collectors.toList());
    }

    @Override
    public void deleteAd(String adId) throws Exception {
        // 1. 删除ES中的记录
        esOperations.delete(adId, Advert.class);

        // 2. 删除MinIO中的视频文件
        adsMinioService.deleteFile(adId);
    }
}
