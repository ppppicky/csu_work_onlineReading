//package org.example.controller;
//
//import io.swagger.annotations.Api;
//import io.swagger.annotations.ApiOperation;
//import lombok.extern.slf4j.Slf4j;
//import org.example.entity.Users;
//import org.example.service.UserService;
//import org.example.util.JwtUtil;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.data.redis.core.RedisTemplate;
//import org.springframework.data.redis.core.StringRedisTemplate;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.*;
//
//import java.util.concurrent.TimeUnit;
//
//@Slf4j
//@RestController
//@RequestMapping("/user")
//@Api(tags = "用户端")
//public class UserController {
//    @Autowired
//    private UserService userService;
//
//    /**
//     * 用户注册
//     */
//    @PostMapping("/register")
//    @ApiOperation(value = "用户注册")
//    public ResponseEntity<String> register(@RequestBody Users user) {
//        log.info("用户注册");
//        if (userService.findUser(user.getUserName())) {
//            log.info("User already exists");
//            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("User already exists");
//        }
//        userService.register(user);
//        log.info("Register successfully");
//        return ResponseEntity.ok("Register successfully");
//    }
//
//    @Autowired
//    private StringRedisTemplate redisTemplate;
//
//    @PostMapping("/login")
//    @ApiOperation(value = "用户登录")
//    public ResponseEntity<String> login(@RequestBody Users user) {
//        log.info("用户登录");
//        if (!userService.findUser(user.getUserName())) {
//            log.info("User doesn't exist");
//            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("User doesn't exist");
//        }
//        try {
//            Users loggedInUser = userService.login(user.getUserName(), user.getUserPassword());
//
//            // 生成新的 Token
//            String newToken = JwtUtil.generateToken(loggedInUser.getUserName());
//
//            // 🚀 将 Token 存入 Redis，保证同一账号只有一个 Token
//            String redisKey = "USER_TOKEN_" + loggedInUser.getUserName();
//            redisTemplate.opsForValue().set(redisKey, newToken, 1, TimeUnit.DAYS);  // 过期时间 1 天
//
//            log.info("Login successfully, Token: {}", newToken);
//            return ResponseEntity.ok(newToken);
//        } catch (RuntimeException e) {
//            log.error("Login failed: " + e.getMessage());
//            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
//        }
//    }
//
//
//    /**
//     * 用户登出（JWT 无需服务端操作，前端删除 Token 即可）
//     */
//    @PostMapping("/logout")
//    @ApiOperation(value = "用户登出")
//    public ResponseEntity<String> logout() {
//        log.info("用户登出");
//        return ResponseEntity.ok("Logout successfully");
//    }
//
//    /**
//     * 获取用户个人信息（解析 Token）
//     */
//    @GetMapping("/info")
//    @ApiOperation(value = "获取用户个人信息")
//    public ResponseEntity<?> getUserInfo(@RequestHeader("Authorization") String token) {
//        log.info("获取用户个人信息");
//
//        // 解析 Token 获取用户名
//        if (token == null || !token.startsWith("Bearer ")) {
//            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Token missing or invalid");
//        }
//        token = token.substring(7);
//        if (!JwtUtil.validateToken(token)) {
//            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid token");
//        }
//
//        String userName = JwtUtil.parseToken(token).getSubject();
//        Users user = userService.getUserByUserName(userName);
//
//        if (user == null) {
//            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
//        }
//        return ResponseEntity.ok(user);
//    }
//
//}
//
//
