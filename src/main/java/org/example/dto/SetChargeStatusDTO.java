package org.example.dto;

import lombok.Data;

@Data
public class SetChargeStatusDTO {
    private Integer bookId;  // 书籍ID
    private Byte isCharge;   // 是否收费 (1: 收费, 0: 免费)
}
