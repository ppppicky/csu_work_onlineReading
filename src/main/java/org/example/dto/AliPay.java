package org.example.dto;

import lombok.Data;

@Data
public class AliPay {
    private String traceNo;        // 商户订单号（由我们系统生成的唯一订单号，对应数据库中的 order_id）
    private double totalAmount;    // 订单金额（单位：元）
    private String subject;        // 订单名称（例如：“用户充值100元”）
    private String alipayTraceNo;  // 支付宝交易号（支付宝返回的唯一交易号）
}

