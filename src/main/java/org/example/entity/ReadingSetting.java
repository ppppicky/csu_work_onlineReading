package org.example.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import javax.persistence.*;
import javax.validation.constraints.Pattern;
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
    // BackgroundType:SOLID_COLOR, IMAGE, GRADIENT, GIF, VIDEO
    @Enumerated(EnumType.STRING)
    private BackgroundType backgroundType = BackgroundType.SOLID_COLOR;
    private String solidColor = "#FFFFFF";

    @ManyToOne
    @JoinColumn(name="background_id")
    private BackgroundResource backgroundResource;

    private Double lineSpacing = 1.5;

    @Pattern(regexp = "day|night|auto|eye-friendly", message = "主题模式不合法")
    private String themeMode = "day";//白天黑夜护眼等

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updateTime;
}

