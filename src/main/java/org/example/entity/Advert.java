package org.example.entity;

import lombok.Data;
import org.springframework.data.elasticsearch.annotations.Document;

import javax.persistence.*;

@Data
@Document(indexName = "advert")
public class Advert {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private String id;
  //  private String videoKey;    // MinIO对象名称（唯一标识）
    private String keywords;

    @Transient  // 不持久化到数据库
    private String videoUrl;    // 临时访问URL
}