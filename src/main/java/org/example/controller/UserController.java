package org.example.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.example.entity.Users;
import org.example.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;

@Slf4j
@RestController
@RequestMapping("/user")
@Api(tags = "用户端")
public class UserController {
    @Autowired
    private UserService userService;

    /**
     * 用户注册
     * @param user
     * @param session
     * @return
     */
    @PostMapping("/register")
    @ApiOperation(value = "用户注册")
    public ResponseEntity<String>register(@RequestBody Users user, HttpSession session){
//        log.info(user.getUserName());
        if( userService.findUser(user.getUserName())){
            log.info("User already exists");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("User already exists");
        }
        userService.register(user);
        log.info("register successfully");
        return ResponseEntity.ok("register successfully");
    }


    /**
     * 用户登录
     * @param user
     * @param session
     * @return
     */
    @PostMapping("/login")
    @ApiOperation(value = "用户登录")
    public ResponseEntity<String> login(@RequestBody Users user, HttpSession session) {
        if (!userService.findUser(user.getUserName())) {
            log.info("User doesn't exist");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("User doesn't exist");
        }
        try {
            Users loggedInUser = userService.login(user.getUserName(), user.getUserPassword());
            session.setAttribute("user", loggedInUser);
            log.info("Login successfully");
            return ResponseEntity.ok("Login successfully");
        } catch (RuntimeException e) {
            log.error("Login failed: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }


    /**
     * 用户登出
     * @param session
     * @return
     */
    @PostMapping("/logout")
    @ApiOperation(value = "用户登出")
    public ResponseEntity<String> logout(HttpSession session) {
        session.removeAttribute("user");
        return ResponseEntity.ok("logout successfully");
    }

    /**
     * 获取用户个人信息
     * @param userName 用户名
     * @return 用户详细信息
     */
    @GetMapping("/info/{userName}")
    @ApiOperation(value = "获取用户个人信息")
    public ResponseEntity<?> getUserInfo(@PathVariable String userName) {
        Users user = userService.getUserByUserName(userName);
        if (user == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
        }
        return ResponseEntity.ok(user);
    }


}

