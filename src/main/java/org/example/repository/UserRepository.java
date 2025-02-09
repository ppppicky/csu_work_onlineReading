package org.example.repository;

import org.example.entity.Users;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserRepository extends JpaRepository<Users,Integer> {

   Users findByUserName(String userName);
    List<Users> findByIsVIP(boolean isVIP);

    Page<Users> findAll(Pageable pageable);
}
