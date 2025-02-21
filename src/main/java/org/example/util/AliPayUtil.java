package org.example.util;

import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.DefaultAlipayClient;
import com.alipay.api.request.AlipayTradeCloseRequest;
import lombok.extern.slf4j.Slf4j;
import org.example.config.AliPayConfig;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class AliPayUtil {

    /**
     * 关闭支付宝交易
     * @param outTradeNo 商户订单号
     * @param aliPayConfig 支付宝配置信息
     * @return 是否关闭成功
     */
    public boolean closeOrder(String outTradeNo, AliPayConfig aliPayConfig) throws AlipayApiException {
        // 创建支付宝客户端
        AlipayClient alipayClient = new DefaultAlipayClient(
                "https://openapi-sandbox.dl.alipaydev.com/gateway.do", // 支付宝网关
                aliPayConfig.getAppId(),
                aliPayConfig.getAppPrivateKey(),
                "json",
                "UTF-8",
                aliPayConfig.getAlipayPublicKey(),
                "RSA2"
        );

        // 组装请求
        AlipayTradeCloseRequest request = new AlipayTradeCloseRequest();
        request.setBizContent("{\"out_trade_no\":\"" + outTradeNo + "\"}");

        // 执行请求
        String response = alipayClient.execute(request).getBody();
        log.info("支付宝订单 {} 关闭结果: {}", outTradeNo, response);

        return response.contains("\"code\":\"10000\"");
    }
}
