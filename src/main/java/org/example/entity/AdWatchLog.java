package org.example.entity;

import lombok.Data;
import javax.persistence.*;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "ad_watch_log")
public class AdWatchLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "log_id")
    private Integer logId; // 记录ID

    private Integer userId; // 用户ID

    private Integer adId; // 关联广告表

    private Integer bookId; // 书籍ID

    private Integer chapterId; // 章节ID

    private LocalDateTime watchTime = LocalDateTime.now(); // 观看时间（默认当前时间）
}
