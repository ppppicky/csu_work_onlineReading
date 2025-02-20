package org.example.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.example.entity.BackgroundType;

import java.time.LocalDateTime;

@Data
public class BackgroundDTO {
    private String id;// 生成 UUID 作为 Redis Key

    private BackgroundType resourceType; // IMAGE/GIF/VIDEO

 //   private String storagePath;
    private byte[] storagePath;
    private Long fileSize;

  //  private String thumbnailPath;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;
}
