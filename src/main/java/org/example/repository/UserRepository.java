package org.example.repository;

import org.example.entity.Users;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserRepository extends JpaRepository<Users, Integer> {

    // 根据用户名查找用户（应返回 Users 而非 List）
    Users findByUserName(String userName);

    // 根据 VIP 状态查询用户（Byte 类型）
    List<Users> findByIsVip(Byte isVip);

    // 分页查询所有用户
    Page<Users> findAll(Pageable pageable);

    // 根据用户名进行模糊查询
    Page<Users> findByUserNameContaining(String username, Pageable pageable);

    @Query("SELECT COUNT(u) FROM Users u WHERE u.userRegTime > CURRENT_DATE - 30")
    long countNewUsersInLastMonth();

    @Query("SELECT COUNT(u) FROM Users u WHERE u.isVip = 1")
    long countVipUsers();
    @Query("SELECT COUNT(u) FROM Users u WHERE u.status = 1")
    long countActiveUsers();
}
