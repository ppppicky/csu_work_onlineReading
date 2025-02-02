//package org.example.service.Impl;
//
//import org.example.entity.users;
//import org.example.mapper.UserMapper;
//import org.example.repository.UserRepository;
//import org.example.service.UserService;
//import org.mindrot.jbcrypt.BCrypt;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.data.domain.Page;
//import org.springframework.data.domain.PageRequest;
//import org.springframework.stereotype.Service;
//import org.springframework.data.domain.Pageable;
//import java.math.BigDecimal;
//
//@Service
//public class UserSImpl implements UserService {
//    private final UserRepository userRepository;
//    private final UserMapper userMapper;
//
//    @Autowired
//    public UserSImpl(UserRepository ur, UserMapper um) {
//        userRepository = ur;
//        userMapper = um;
//    }
//
//    @Override
//    public void register(users users) {
//        users.setUserPassword(BCrypt.hashpw(users.getUserPassword(), BCrypt.gensalt()));
//        userRepository.save(users);
//    }
//
//    @Override
//    public users login(String userName, String password) {
//        users users = (users) userRepository.findByName(userName);
//        if(users ==null)throw new RuntimeException("用户不存在");
//        if (!BCrypt.checkpw(password, users.getUserPassword())) {
//            throw new RuntimeException("密码错误！");
//        }
//        return users;
//    }
//
//    @Override
//    public Page<users> getAllUsers(int page, int size) {
//        Pageable pageable = PageRequest.of(page, size);
//        return userRepository.findAll(pageable);
//    }
//
//    @Override
//    public void updateCredit(int userId, BigDecimal userCredit) {
//        userMapper.updateUserCredit(userId, userCredit);
//    }
//
//    @Override
//    public void logout(int userId) {
//
//    }
//
//    @Override
//    public boolean findUser(String userName) {
//        return userRepository.findByName(userName)
//                == null ? false : true;
//    }
//}
