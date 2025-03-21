package org.example.dto;

import lombok.Data;

@Data
public class SetChargeStatusDTO {
    private Integer bookId;
    private Byte isCharge;   // 1: 收费, 0: 免费
}
