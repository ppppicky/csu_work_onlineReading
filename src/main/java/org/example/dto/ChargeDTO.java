package org.example.dto;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class ChargeDTO {
    private Integer cmId;
    private Integer bookId;
    private Integer freeChapter;
    private BigDecimal chargeMoney;
    private Byte isVipFree; // 1: 会员免费, 0: 会员不免费
}

