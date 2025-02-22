package org.example.controller;

import cn.hutool.json.JSONObject;
import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.DefaultAlipayClient;
import com.alipay.api.internal.util.AlipaySignature;
import com.alipay.api.request.AlipayTradePagePayRequest;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.example.config.AliPayConfig;
import org.example.dto.AliPay;
import org.example.entity.Orders;
import org.example.entity.Users;
import org.example.repository.OrdersRepository;
import org.example.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;

@Slf4j
@RestController
@RequestMapping("/alipay")
@Api(tags = "支付宝支付")
public class AliPayController {

    private static final String GATEWAY_URL = "https://openapi-sandbox.dl.alipaydev.com/gateway.do";
    private static final String FORMAT = "JSON";
    private static final String CHARSET = "UTF-8";
    private static final String SIGN_TYPE = "RSA2";

    @Resource
    private AliPayConfig aliPayConfig;

    @Resource
    private OrdersRepository ordersRepository;

    @Resource
    private UserRepository userRepository;

    /**
     * 生成支付宝支付请求
     */
    @GetMapping("/pay")
    @ApiOperation(value = "生成支付宝支付请求")
    public ResponseEntity<String> pay(AliPay aliPay, HttpServletResponse httpResponse) throws IOException {
        log.info("========= 生成支付宝支付请求 =========");
        // **1. 查询订单**
        Optional<Orders> optionalOrder = ordersRepository.findByOrderId(aliPay.getTraceNo());
        if (!optionalOrder.isPresent()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Order does not exist");
        }

        Orders order = optionalOrder.get();

        // **2. 订单状态校验**
        if ("CANCELLED".equals(order.getState())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("The order has been canceled and cannot be paid.");
        }
        if ("PAID".equals(order.getState())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("The order has already been paid and cannot be paid again.");
        }

        // **3. 生成支付请求**
        AlipayClient alipayClient = new DefaultAlipayClient(GATEWAY_URL, aliPayConfig.getAppId(),
                aliPayConfig.getAppPrivateKey(), FORMAT, CHARSET, aliPayConfig.getAlipayPublicKey(), SIGN_TYPE);

        AlipayTradePagePayRequest request = new AlipayTradePagePayRequest();
        request.setNotifyUrl(aliPayConfig.getNotifyUrl());
        request.setReturnUrl(aliPayConfig.getReturnUrl());

        JSONObject bizContent = new JSONObject();
        bizContent.set("out_trade_no", aliPay.getTraceNo());
        bizContent.set("total_amount", aliPay.getTotalAmount());
        bizContent.set("subject", aliPay.getSubject());
        bizContent.set("product_code", "FAST_INSTANT_TRADE_PAY");
        request.setBizContent(bizContent.toString());

        // **4. 发送请求**
        String form = "";
        try {
            form = alipayClient.pageExecute(request).getBody();
        } catch (AlipayApiException e) {
            e.printStackTrace();
        }

        httpResponse.setContentType("text/html;charset=" + CHARSET);
        httpResponse.getWriter().write(form);
        httpResponse.getWriter().flush();
        httpResponse.getWriter().close();

        return ResponseEntity.ok("Payment request has been generated.");
    }

    /**
     * 支付宝支付异步回调
     */
    @ApiOperation(value = "支付宝支付异步回调")
    @PostMapping("/notify")
    public String payNotify(HttpServletRequest request) {
        log.info("========= 接收到支付宝异步通知 ========");

        try {
            // **1. 获取支付宝返回的参数**
            Map<String, String> params = new HashMap<>();
            Map<String, String[]> requestParams = request.getParameterMap();
            for (String name : requestParams.keySet()) {
                params.put(name, request.getParameter(name));
            }

            // **2. 验签**
            boolean signVerified = AlipaySignature.rsaCheckV1(
                    params,
                    aliPayConfig.getAlipayPublicKey(),
                    CHARSET,
                    SIGN_TYPE
            );

            if (!signVerified) {
                log.info("验签失败，回调数据可能被篡改！");
                return "fail";
            }

            log.info("========= 验签成功，处理订单 =========");

            String outTradeNo = params.get("out_trade_no");  // 商户订单号
            String alipayTradeNo = params.get("trade_no");   // 支付宝交易号
            String tradeStatus = params.get("trade_status"); // 交易状态
            BigDecimal totalAmount = new BigDecimal(params.get("total_amount"));

            // **3. 确保交易成功**
            if (!"TRADE_SUCCESS".equals(tradeStatus)) {
                System.out.println("交易状态非成功状态：" + tradeStatus);
                return "fail";
            }

            // **4. 查询订单**
            Optional<Orders> optionalOrder = ordersRepository.findByOrderId(outTradeNo);
            if (!optionalOrder.isPresent()) {
                System.out.println("订单不存在：" + outTradeNo);
                return "fail";
            }

            Orders order = optionalOrder.get();

            // **5. 再次校验订单状态**
            if ("CANCELLED".equals(order.getState())) {
                System.out.println("订单已取消，无法支付");
                return "fail";
            }
            if ("PAID".equals(order.getState())) {
                System.out.println("订单已支付，重复回调");
                return "success";
            }

            // **6. 更新订单状态**
            order.setAlipayNo(alipayTradeNo);
            order.setPayTime(LocalDateTime.now());
            order.setState("PAID");
            order.setPaymentMethod("ALIPAY");
            ordersRepository.save(order);

            // **7. 更新用户余额**
            Optional<Users> optionalUser = userRepository.findById(order.getUserId());
            if (optionalUser.isPresent()) {
                Users user = optionalUser.get();
                user.setUserCredit(user.getUserCredit().add(totalAmount));
                userRepository.save(user);
                System.out.println("用户余额更新成功，用户ID：" + user.getUserId() + "，新余额：" + user.getUserCredit());
            }

            return "success";

        } catch (Exception e) {
            e.printStackTrace();
            return "fail";
        }
    }

    @GetMapping("/result")
    public ResponseEntity<String> getPaymentResult(@RequestParam("out_trade_no") String orderId) {
        Optional<Orders> orderOpt = ordersRepository.findByOrderId(orderId);
        if (orderOpt.isPresent()) {
            Orders order = orderOpt.get();
            if ("PAID".equals(order.getState())) {
                return ResponseEntity.ok("<h1>支付成功</h1><p>订单号：" + orderId + "</p>");
            } else {
                return ResponseEntity.ok("<h1>支付失败</h1><p>订单号：" + orderId + "</p>");
            }
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("<h1>订单不存在</h1>");
    }
}
