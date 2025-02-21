package org.example.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
@AllArgsConstructor
public class ChargeStatsDTO {
    private BigDecimal totalRevenue; // 总收入
    private long vipUsers; // VIP 用户数量
}
