package org.example.entity;

import lombok.Data;
import javax.persistence.*;

@Data
@Entity
@Table(name = "advertisement")
public class Advertisement {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer adId; // 广告ID

    private String adType; // 广告类型（VIDEO ）

//    private Integer adDuration; // 广告播放时长（秒）

//    private Integer unlockChapter; // 观看广告可解锁的章节数

    private Byte isActive = 1; // 是否启用（1=启用, 0=禁用）

    private String url;

}
