package org.example.dto;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class UserDTO {
    private Integer userId;
    private String userName;
    private Byte userSex;
    private Byte isVip;
    private LocalDateTime vipTime;
    private BigDecimal userCredit;
    private LocalDateTime userRegTime;
    private Byte status;
}
