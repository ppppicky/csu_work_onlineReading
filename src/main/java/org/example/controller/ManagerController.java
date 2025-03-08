//package org.example.controller;
//
//import io.swagger.annotations.Api;
//import io.swagger.annotations.ApiOperation;
//import lombok.extern.slf4j.Slf4j;
//import org.example.dto.UserDTO;
//import org.example.entity.Manager;
//import org.example.service.ManagerService;
//import org.example.util.JwtUtil;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.*;
//
//import java.util.List;
//
//@Slf4j
//@RestController
//@Api(value = "管理员控制器", tags = "管理员相关接口")
//@RequestMapping("/manager")
//public class ManagerController {
//    @Autowired
//    private ManagerService managerService;
//
//
//    /**
//     * 管理员注册
//     * @param manager
//     * @return
//     */
//    @PostMapping("/register")
//    @ApiOperation(value = "管理员注册")
//    public ResponseEntity<String> register(@RequestBody Manager manager) {
//        if (managerService.findManager(manager.getManagerName()) != null) {
//            log.info("管理员已存在");
//            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("管理员已存在");
//        }
//        managerService.register(manager);
//        log.info("注册成功");
//        return ResponseEntity.ok("注册成功");
//    }
//
//    /**
//     * 管理员登录（返回 JWT Token）
//     * @param manager
//     * @return
//     */
//    @PostMapping("/login")
//    @ApiOperation(value = "管理员登录")
//    public ResponseEntity<String> login(@RequestBody Manager manager) {
//        Manager existingManager = managerService.login(manager.getManagerName(), manager.getManagerPassword());
//        if (existingManager == null) {
//            log.info("用户名或密码错误");
//            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("用户名或密码错误");
//        }
//
//        // 生成 JWT Token
//        String token = JwtUtil.generateToken(existingManager.getManagerName());
//
//        log.info("管理员登录成功，Token: {}", token);
//        return ResponseEntity.ok(token);
//    }
//
//
//    /**
//     * 管理员退出登录
//     *
//     * @return
//     */
//    @PostMapping("/logout")
//    @ApiOperation(value = "管理员退出登录")
//    public ResponseEntity<String> logout() {
//        return ResponseEntity.ok("退出成功"); // 由前端清除 token
//    }
//
//
//    /**
//     * 获取用户列表，支持用户名模糊查询
//     * @param username 用户名（可选）
//     * @param page 页码
//     * @param size 每页大小
//     * @return 用户列表
//     */
//    @GetMapping("/users")
//    @ApiOperation(value = "获取用户列表")
//    public ResponseEntity<List<UserDTO>> getUserList(
//            @RequestParam(required = false) String username,
//            @RequestParam(defaultValue = "0") int page,
//            @RequestParam(defaultValue = "10") int size) {
//        log.info("获取用户列表");
//
//        List<UserDTO> users = managerService.getUsers(username, page, size);
//        return ResponseEntity.ok(users);
//    }
//
//    /**
//     * 启用/禁用用户账号
//     * @param username 用户名
//     * @param status 账号状态（1 启用，0 禁用）
//     * @return 操作结果
//     */
//    @PostMapping("/user/status")
//    @ApiOperation(value = "启用/禁用用户账号")
//    public ResponseEntity<String> updateUserStatus(
//            @RequestParam String username,
//            @RequestParam Integer status) {
//        log.info("启用/禁用用户账号");
//
//        if (status != 1 && status != 0) {
//            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid status value");
//        }
//
//        boolean result = managerService.updateUserStatus(username, status);
//        if (result) {
//            return ResponseEntity.ok("User status updated successfully");
//        } else {
//            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
//        }
//    }
//
//}
