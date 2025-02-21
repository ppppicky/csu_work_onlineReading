package org.example.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UserBehaviorStatsDTO {
    private long activeUsers; // 活跃用户数量
    private long newUsers; // 新增用户数量
}
