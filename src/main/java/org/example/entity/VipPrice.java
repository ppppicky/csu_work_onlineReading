package org.example.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Entity

public class VipPrice {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id; // 主键 ID

    @Column(nullable = false, unique = true)
    private String vipType; // 会员类型 ("vip_year"年会员, "vip_season"季会员, "vip_month"月会员)

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal price;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updateTime; // 更新时间
}
