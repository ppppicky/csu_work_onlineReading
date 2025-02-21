//package org.example.config;
//
//import org.example.entity.Manager;
//import org.example.entity.Users;
//import org.example.repository.ManagerRepository;
//import org.example.repository.UserRepository;
//import org.example.service.ManagerService;
//import org.example.service.UserService;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.security.core.authority.SimpleGrantedAuthority;
//import org.springframework.security.core.userdetails.User;
//import org.springframework.security.core.userdetails.UserDetails;
//import org.springframework.security.core.userdetails.UserDetailsService;
//import org.springframework.security.core.userdetails.UsernameNotFoundException;
//import org.springframework.stereotype.Service;
//
//import java.util.Collections;
//import java.util.Optional;
//
//@Service
//public class CustomUserDetailsService implements UserDetailsService {
//    @Autowired
//    private UserService userService;
//
//    @Autowired
//    private ManagerService managerService;
//
//    @Override
//    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
//        Users user = userService.getUserByUserName(username);
//        if (user != null) {
//            return User.withUsername(user.getUserName())
//                    .password(user.getUserPassword()) // 这里假设密码已经加密
//                    .roles("USER") // 用户权限
//                    .build();
//        }
//
//        Manager manager = managerService.findManager(username);
//        if (manager != null) {
//            return User.withUsername(manager.getManagerName())
//                    .password(manager.getManagerPassword()) // 这里假设密码已经加密
//                    .roles("ADMIN") // 管理员权限
//                    .build();
//        }
//
//        throw new UsernameNotFoundException("User not found");
//    }
//}
