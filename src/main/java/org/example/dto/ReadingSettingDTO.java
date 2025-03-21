package org.example.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.entity.BackgroundType;

import javax.validation.constraints.Pattern;
import java.time.LocalDateTime;

@Data


//@Builder
public class ReadingSettingDTO {

    private String fontFamily;
    private Integer fontSize;
    private BackgroundType backgroundType;
    private String solidColor;
    private Integer backgroundId;
    private Double lineSpacing = 1.5;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updateTime;

    @Pattern(regexp = "day|night|auto|eye-friendly", message = "主题模式不合法")
    private String themeMode = "day";//白天黑夜护眼等
    public ReadingSettingDTO(){

    }

}

