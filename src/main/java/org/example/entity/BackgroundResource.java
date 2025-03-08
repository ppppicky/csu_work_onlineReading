package org.example.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import javax.persistence.*;
import java.time.LocalDateTime;

// 背景资源实体
@Entity
@Table(name = "background_resource")
@Data
public class BackgroundResource {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer backgroundId;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private Users user;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private BackgroundType resourceType; // IMAGE/GIF/VIDEO/ GRADIENT

   @Column(nullable = false) // 修改字段名和数据库列名
   private String storageKey; // 存储 MinIO 对象键（如 "permanent/uuid.jpg"）

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime = LocalDateTime.now();

  //  private Long fileSize;
}