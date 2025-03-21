package org.example.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.example.entity.Users;
import org.example.service.UserService;
import org.example.util.JwtUtil;
import org.example.Listener.OnlineUserListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.Set;
import java.util.concurrent.TimeUnit;

@Slf4j
@RestController
@RequestMapping("/user")
@Api(tags = "用户端")
public class UserController {
    @Autowired
    private UserService userService;

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Autowired
    private OnlineUserListener onlineUserListener;

    private static final int MAX_DEVICES = 3; // 允许的最大设备数量


    /**
     * 用户注册
     */
    @PostMapping("/register")
    @ApiOperation(value = "用户注册")
    public ResponseEntity<String> register(@RequestBody Users user) {
        log.info("用户注册");
        if (userService.findUser(user.getUserName())) {
            log.info("User already exists");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("User already exists");
        }
        userService.register(user);
        log.info("Register successfully");
        return ResponseEntity.ok("Register successfully");
    }



    /**
     * 用户登录（支持多设备）
     */
    @PostMapping("/login")
    @ApiOperation(value = "用户登录")
    public ResponseEntity<String> login(@RequestBody Users user, HttpServletRequest request) {
        log.info("用户登录: {}", user.getUserName());

        if (!userService.findUser(user.getUserName())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("用户不存在");
        }

        try {
            Users loggedInUser = userService.login(user.getUserName(), user.getUserPassword());

            // 获取设备信息
            String userIp = request.getRemoteAddr();
            String userAgent = request.getHeader("User-Agent");
            String deviceInfo = userAgent + "_" + userIp;

            // 生成 Token
            String newToken = JwtUtil.generateToken(loggedInUser.getUserName(), deviceInfo);
            log.info("JWT 生成成功: {}", newToken);

            String tokenSetKey = "USER_TOKENS_" + loggedInUser.getUserName();
            String onlineUserKey = "ONLINE_USER_" + loggedInUser.getUserName();

            // 获取当前用户的已登录 Token
            Set<String> existingTokens = redisTemplate.opsForSet().members(tokenSetKey);

            // 如果超过设备限制，删除最早的 Token
            if (existingTokens != null && existingTokens.size() >= MAX_DEVICES) {
                String oldestToken = existingTokens.iterator().next();
                redisTemplate.opsForSet().remove(tokenSetKey, oldestToken);
                redisTemplate.delete("TOKEN_" + oldestToken);
                log.warn("设备超限，移除最早的 Token: {}", oldestToken);
            }

            // 存储新 Token 并设置过期时间
            redisTemplate.opsForSet().add(tokenSetKey, newToken);
            redisTemplate.expire(tokenSetKey, 1, TimeUnit.DAYS);
            redisTemplate.opsForValue().set("TOKEN_" + newToken, deviceInfo, 1, TimeUnit.DAYS);

            // 记录用户在线状态
            onlineUserListener.userLoggedIn(loggedInUser.getUserName());

            return ResponseEntity.ok(newToken);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    /**
     * 用户登出（仅移除 Token 和在线状态）
     */
    @PostMapping("/logout")
    @ApiOperation(value = "用户登出")
    public ResponseEntity<String> logout(@RequestHeader(value = "Authorization", required = false) String token) {
        log.info("用户请求登出，Token: {}", token);

        if (token == null || token.trim().isEmpty() || !token.startsWith("Bearer ")) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Token 缺失或格式错误");
        }

        token = token.substring(7).trim();

        try {
            String username = JwtUtil.parseToken(token).getSubject();
            String tokenSetKey = "USER_TOKENS_" + username;

            // 删除 Token
            Long removed = redisTemplate.opsForSet().remove(tokenSetKey, token);
            redisTemplate.delete("TOKEN_" + token);

            if (removed != null && removed > 0) {
                log.info("用户 {} 登出成功，Token 已移除", username);
            } else {
                log.warn("Token {} 不在记录中，可能已失效", token);
            }

            // 如果没有剩余 Token，则移除在线状态
            if (Boolean.FALSE.equals(redisTemplate.hasKey(tokenSetKey))) {
                onlineUserListener.userLoggedOut(username);
            }

            return ResponseEntity.ok("Logout successfully");
        } catch (Exception e) {
            log.error("Token 解析失败，可能已过期", e);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid token");
        }
    }

    /**
     * 获取用户个人信息（解析 Token）
     */
    @GetMapping("/info")
    @ApiOperation(value = "获取用户个人信息")
    public ResponseEntity<?> getUserInfo(@RequestHeader("Authorization") String token) {
        log.info("获取用户个人信息");

        if (token == null || !token.startsWith("Bearer ")) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Token 缺失或格式错误");
        }
        token = token.substring(7);
        if (!JwtUtil.validateToken(token)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("无效 Token");
        }

        String userName = JwtUtil.parseToken(token).getSubject();
        Users user = userService.getUserByUserName(userName);

        if (user == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("用户不存在");
        }
        return ResponseEntity.ok(user);
    }

}


