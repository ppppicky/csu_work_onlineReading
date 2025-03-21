package org.example.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.example.entity.Manager;
import org.example.service.ManagerService;
import org.example.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.concurrent.TimeUnit;

@Slf4j
@RestController
@Api(value = "管理员控制器", tags = "管理员相关接口")
@RequestMapping("/manager")
public class ManagerController {

    @Autowired
    private ManagerService managerService;

    @Autowired
    private StringRedisTemplate redisTemplate;

    private static final String BLACKLIST_PREFIX = "JWT_BLACKLIST_";
    private static final String ADMIN_LAST_IP_PREFIX = "ADMIN_LAST_IP_";
    private static final String ADMIN_AGENT_PREFIX = "ADMIN_AGENT_";
    private static final String ADMIN_ACCESS_COUNT_PREFIX = "ADMIN_ACCESS_COUNT_";
    private static final int MAX_REQUEST_LIMIT = 100;

    /**
     * 管理员注册
     */
    @PostMapping("/register")
    @ApiOperation(value = "管理员注册")
    public ResponseEntity<String> register(@RequestBody Manager manager) {
        if (managerService.findManager(manager.getManagerName()) != null) {
            log.info("管理员已存在");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("管理员已存在");
        }
        managerService.register(manager);
        log.info("注册成功");
        return ResponseEntity.ok("注册成功");
    }

    @PostMapping("/login")
    @ApiOperation(value = "管理员登录")
    public ResponseEntity<String> login(@RequestBody Manager manager, HttpServletRequest request) {
        Manager existingManager = managerService.login(manager.getManagerName(), manager.getManagerPassword());
        if (existingManager == null) {
            log.info("用户名或密码错误");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("用户名或密码错误");
        }

        // 获取 User-Agent 和 IP
        String userIp = request.getRemoteAddr();
        String userAgent = request.getHeader("User-Agent");

        // 生成 JWT Token
        String newToken = JwtUtil.generateToken(existingManager.getManagerName(), userAgent);
        log.info("管理员 JWT 生成成功: {}", newToken);

        // 存入 Redis，确保同一管理员只有一个有效 Token
        String redisKey = "ADMIN_TOKEN_" + existingManager.getManagerName();
        redisTemplate.opsForValue().set(redisKey, newToken, 1, TimeUnit.DAYS);

        // 记录 IP 和 User-Agent
        String ipKey = "ADMIN_LAST_IP_" + existingManager.getManagerName();
        String agentKey = "ADMIN_AGENT_" + existingManager.getManagerName();

        redisTemplate.opsForValue().set(ipKey, userIp, 1, TimeUnit.DAYS);
        redisTemplate.opsForValue().set(agentKey, userAgent, 1, TimeUnit.DAYS);

        return ResponseEntity.ok(newToken);
    }


    /**
     * 管理员退出登录
     */
    @PostMapping("/logout")
    @ApiOperation(value = "管理员退出登录")
    public ResponseEntity<String> logout(@RequestHeader(value = "Authorization", required = false) String token) {
        log.info("管理员退出登录");

        if (token == null || token.trim().isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Token missing");
        }

        if (!token.startsWith("Bearer ")) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid token format");
        }

        token = token.substring(7).trim();

        try {
            String username = JwtUtil.parseToken(token).getSubject();
            log.info("解析出管理员用户名: {}", username);

            String redisKey = "ADMIN_TOKEN_" + username;
            redisTemplate.delete(redisKey);

            // 清除 IP、User-Agent 记录
            redisTemplate.delete(ADMIN_LAST_IP_PREFIX + username);
            redisTemplate.delete(ADMIN_AGENT_PREFIX + username);

            return ResponseEntity.ok("管理员退出成功");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid token");
        }
    }

    /**
     * 监听异常行为，检测是否存在 JWT 泄露风险
     */
    private boolean detectAdminAnomaly(String token, String username, String currentIp, String currentUserAgent) {
        String lastIp = redisTemplate.opsForValue().get(ADMIN_LAST_IP_PREFIX + username);
        String lastUserAgent = redisTemplate.opsForValue().get(ADMIN_AGENT_PREFIX + username);
        String redisKey = ADMIN_ACCESS_COUNT_PREFIX + token;

        Long count = redisTemplate.opsForValue().increment(redisKey);

        if (count == 1) {
            redisTemplate.expire(redisKey, 1, TimeUnit.MINUTES);
        }

        if (lastIp != null && !lastIp.equals(currentIp)) {
            log.warn("管理员 IP 变更异常，管理员: {}，IP: {} -> {}", username, lastIp, currentIp);
            return true;
        }

        if (lastUserAgent != null && !lastUserAgent.equals(currentUserAgent)) {
            log.warn("管理员 User-Agent 变更异常，管理员: {}，User-Agent: {} -> {}", username, lastUserAgent, currentUserAgent);
            return true;
        }

        if (count > MAX_REQUEST_LIMIT) {
            log.warn("短时间内管理员请求过多，可能存在攻击风险，Token: {}，请求次数: {}", token, count);
            return true;
        }

        redisTemplate.opsForValue().set(ADMIN_LAST_IP_PREFIX + username, currentIp, 1, TimeUnit.DAYS);
        redisTemplate.opsForValue().set(ADMIN_AGENT_PREFIX + username, currentUserAgent, 1, TimeUnit.DAYS);
        return false;
    }


    /**
     * 启用/禁用用户账号
     * @param username 用户名
     * @param status 账号状态（1 启用，0 禁用）
     * @return 操作结果
     */
    @PostMapping("/user/status")
    @ApiOperation(value = "启用/禁用用户账号")
    public ResponseEntity<String> updateUserStatus(
            @RequestParam String username,
            @RequestParam Integer status) {
        log.info("启用/禁用用户账号");

        if (status != 1 && status != 0) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid status value");
        }

        boolean result = managerService.updateUserStatus(username, status);
        if (result) {
            return ResponseEntity.ok("User status updated successfully");
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
        }
    }
}
