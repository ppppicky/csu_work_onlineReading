package org.example.repository;

import org.example.entity.ReadingSetting;
import org.example.entity.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SettingRepo extends JpaRepository<ReadingSetting,Integer> {
    Optional<ReadingSetting> findByUser(Users user);
}
