package org.example.dto;

import lombok.Data;
import org.example.entity.BackgroundType;

@Data
public class BackgroundDTO {
    private String id;

    private BackgroundType resourceType; // IMAGE/GIF/VIDEO

    private String  storageKey;

}
