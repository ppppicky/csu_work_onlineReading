package org.example.vo;

import lombok.Data;

@Data
public class AdvertVO {
    private String advertId;

    private String videoUrl;    // 临时访问URL

    public AdvertVO( String id, String videoUrl) {
        this.setAdvertId(id);
        this.setVideoUrl(videoUrl);
    }

    public AdvertVO() {

    }
}
