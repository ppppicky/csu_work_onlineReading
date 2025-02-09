package org.example.controller;

import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.example.entity.Manager;
import org.example.service.ManagerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;

@Slf4j
@RestController
@Api(value = "管理员控制器", tags = "管理员相关接口")
@RequestMapping("/manager")
public class ManagerController {
    @Autowired
    private ManagerService managerService;

    /**
     *
     * @param manager
     * @param session
     * @return
     */
    @PostMapping("/register")
        public ResponseEntity<String>register(@RequestBody Manager manager, HttpSession session){
       if( managerService.findManager(manager.getManagerName())){
           log.info("用户已存在");
           return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("用户已存在");
       }
        managerService.register(manager);
        log.info("注册成功");
        return ResponseEntity.ok("注册成功");
    }

    /**
     *
     * @param manager
     * @param session
     * @return
     */
    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody Manager manager, HttpSession session) {
        if (!managerService.findManager(manager.getManagerName())) {
            log.info("管理员不存在");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("用户不存在");
        }
        if (managerService.login(manager.getManagerName(), manager.getManagerPassword())== null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("密码错误");
        }
        session.setAttribute("manager", manager);
        log.info("登陆成功");
        return ResponseEntity.ok("登录成功");

    }

    /**
     * 管理员退出登录
     * @param session
     * @return
     */
    @PostMapping("/logout")
    public ResponseEntity<String> logout(HttpSession session) {
        session.removeAttribute("manager");
        return ResponseEntity.ok("退出成功");
    }
}
