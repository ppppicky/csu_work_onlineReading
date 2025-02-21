package org.example.config;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Getter
@Configuration
public class AdvertisementConfig {

    @Value("${advertisement.daily-limit}")
    private int dailyLimit;  // 读取广告每日观看限制
}
