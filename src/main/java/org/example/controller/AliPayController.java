package org.example.controller;

import cn.hutool.core.thread.ThreadFactoryBuilder;
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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
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
import java.util.concurrent.*;

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

    @Autowired
    @Qualifier("paymentThreadPool")
    private Executor paymentExecutor;  // 高优先级线程池

    @Autowired
    @Qualifier("creditUpdateThreadPool")
    private Executor creditUpdateExecutor; // 低优先级线程池
    /**
     * 生成支付宝支付请求
     */
    @GetMapping("/pay")
    @ApiOperation(value = "生成支付宝支付请求")
    public void pay(AliPay aliPay, HttpServletResponse httpResponse) throws IOException {
        log.info("========= 生成支付宝支付请求 =========");

        Optional<Orders> optionalOrder = ordersRepository.findByOrderId(aliPay.getTraceNo());
        if (!optionalOrder.isPresent()) {
            httpResponse.setStatus(HttpStatus.NOT_FOUND.value());
            httpResponse.getWriter().write("Order does not exist");
            return;
        }

        Orders order = optionalOrder.get();
        // **2. 订单状态校验**
        if ("CANCELLED".equals(order.getState())) {
            httpResponse.setStatus(HttpStatus.BAD_REQUEST.value());
            httpResponse.getWriter().write("The order has been canceled and cannot be paid.");
            return;
        }
        if ("PAID".equals(order.getState())) {
            httpResponse.setStatus(HttpStatus.BAD_REQUEST.value());
            httpResponse.getWriter().write("The order has already been paid and cannot be paid again.");
            return;
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
            log.error("支付宝请求失败", e);
            httpResponse.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
            httpResponse.getWriter().write("Payment request failed");
            return;
        }

        // **5. 直接写入响应流**
        httpResponse.setContentType("text/html;charset=" + CHARSET);
        httpResponse.getWriter().write(form);
        httpResponse.getWriter().flush();
    }

    /**
     * 支付宝支付异步回调，采用线程池异步执行订单更新
     */
    @PostMapping("/notify")
    @ApiOperation(value = "支付宝支付异步回调")
    public String payNotify(HttpServletRequest request) {
        log.info("========= 接收到支付宝异步通知 ========");

        // 获取支付宝返回的参数
        Map<String, String> params = new HashMap<>();
        request.getParameterMap().forEach((key, values) -> params.put(key, values[0]));

        try {
            // 提交到支付线程池前检查队列容量（降级）
            ThreadPoolExecutor executor = (ThreadPoolExecutor) paymentExecutor;
            if (executor.getQueue().remainingCapacity() < 10) { // 队列剩余容量阈值
                log.warn("支付线程池队列即将满载，拒绝处理新回调");
                return "failure"; // 通知支付宝重试
            }

            paymentExecutor.execute(() -> processPaymentCallback(params));
        } catch (RejectedExecutionException e) {
            log.error("支付线程池拒绝任务，回调参数：{}", params);
            return "failure"; // 支付宝将重试
        }

        return "success";
    }

    /**
     * 处理支付宝支付回调
     */
    private void processPaymentCallback(Map<String, String> params) {
        try {
            // 验签
            boolean signVerified = AlipaySignature.rsaCheckV1(
                    params, aliPayConfig.getAlipayPublicKey(), CHARSET, SIGN_TYPE);
            if (!signVerified) {
                log.error("验签失败，回调数据可能被篡改！");
                return;
            }

            String outTradeNo = params.get("out_trade_no");
            String alipayTradeNo = params.get("trade_no");
            String tradeStatus = params.get("trade_status");
            BigDecimal totalAmount = new BigDecimal(params.get("total_amount"));

            if (!"TRADE_SUCCESS".equals(tradeStatus)) {
                log.warn("交易状态非成功状态：{}", tradeStatus);
                return;
            }

            Optional<Orders> optionalOrder = ordersRepository.findByOrderId(outTradeNo);
            if (!optionalOrder.isPresent()) {
                log.warn("订单不存在：{}", outTradeNo);
                return;
            }

            Orders order = optionalOrder.get();

            if ("CANCELLED".equals(order.getState())) {
                log.warn("订单已取消，无法支付：{}", outTradeNo);
                return;
            }
            if ("PAID".equals(order.getState())) {
                log.info("订单已支付，重复回调：{}", outTradeNo);
                return;
            }

            order.setAlipayNo(alipayTradeNo);
            order.setPayTime(LocalDateTime.now());
            order.setState("PAID");
            order.setPaymentMethod("ALIPAY");
            ordersRepository.save(order);

            // 提交到低优先级线程池更新余额
            creditUpdateExecutor.execute(() -> updateUserCredit(order.getUserId(), order.getTotal()));

        } catch (Exception e) {
            log.error("处理支付宝回调失败", e);
        }
    }

    /**
     * 异步更新用户余额
     */
    private void updateUserCredit(Integer userId, BigDecimal amount) {
        Optional<Users> optionalUser = userRepository.findById(userId);
        if (optionalUser.isPresent()) {
            Users user = optionalUser.get();
            user.setUserCredit(user.getUserCredit().add(amount));
            userRepository.save(user);
            log.info("用户余额更新成功，用户ID：{}，新余额：{}", user.getUserId(), user.getUserCredit());
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
