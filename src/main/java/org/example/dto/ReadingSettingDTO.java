package org.example.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.example.entity.BackgroundType;
import org.example.entity.Users;

import javax.persistence.*;
import javax.validation.constraints.Pattern;
import java.time.LocalDateTime;

@Data
public class ReadingSettingDTO {

    private String fontFamily;

    private Integer fontSize ;

    private BackgroundType backgroundType ;
    private String solidColor;
    private Integer backgroundId;
    private Double lineSpacing = 1.5;

    @Pattern(regexp = "day|night|auto|eye-friendly", message = "主题模式不合法")
    private String themeMode = "day";//白天黑夜护眼等

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updateTime;
}

