//package org.example.controller;
//
//import org.example.entity.users;
//import org.example.service.UserService;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.*;
//
//import java.util.HashMap;
//import java.util.Map;
//
//@RestController
//@RequestMapping("/users")
//public class UserController {
//    @Autowired
//    private UserService userService;
//
//    @PostMapping("/register")
//    public ResponseEntity<String> register(@RequestBody users user) {
//        if (user.getUserPassword().length() < 6) { // 假设密码最小长度为6
//            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("注册失败：密码长度过短");
//        } else if (userService.findUser(user.getUserName())) {
//            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("注册失败：用户已存在！");
//        }
//        userService.register(user);
//        return ResponseEntity.ok("注册成功");
//    }
//
//    @RequestMapping("/login")
//    public ResponseEntity<Map<String, Object>> loginUser(@RequestParam String userName, @RequestParam String password) {
//        Map<String, Object> response = new HashMap<>();
//
//        try {
//            users users = userService.login(userName, password);
//            response.put("status", "success");
//            response.put("user", users);
//            return ResponseEntity.ok(response);
//        } catch (Exception e) {
//            response.put("status", "error");
//            response.put("message", e.getMessage());
//            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
//
//        }
//    }
//
//}
