package org.example.dto;

import cn.hutool.core.io.resource.InputStreamResource;
import lombok.Data;

@Data
public class CoverTempDTO {

 //private String previewUrl;
private String tempKey;
    InputStreamResource image;
  private byte[] imageData;
}