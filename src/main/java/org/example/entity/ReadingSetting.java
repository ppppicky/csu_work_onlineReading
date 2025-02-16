package org.example.entity;

import lombok.Data;

import javax.persistence.*;
import java.time.LocalDateTime;

// 用户个性化设置实体
@Entity
@Data
public class ReadingSetting {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "user_id", nullable = false)
    private Users user;

    @ManyToOne
    @JoinColumn(name = "font_id")
    private FontResource fontResource;

    private Integer fontSize = 16;

    // 背景设置
    @Enumerated(EnumType.STRING)
    private BackgroundType backgroundType = BackgroundType.SOLID_COLOR;// BackgroundType:SOLID_COLOR, IMAGE, GRADIENT, GIF, VIDEO

    private String solidColor = "#FFFFFF";

    @ManyToOne
    @JoinColumn(name="background_id")
    private BackgroundResource backgroundResource;

    private Double lineSpacing = 1.5;

    private String themeMode = "day";//白天黑夜护眼等

    private LocalDateTime updateTime;
}

