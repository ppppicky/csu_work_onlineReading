package org.example.entity;

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

    @Column(nullable = false)
    private String storagePath;

    private String thumbnailPath;//资源缩略图路径，方便预览

    @Column(nullable = false)
    private LocalDateTime createTime = LocalDateTime.now();

    private Long fileSize;
}