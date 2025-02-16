package org.example.dto;

import lombok.Data;

import javax.persistence.Column;

@Data
public class FontVO {
    private String fontName;

    private String storagePath;
}
