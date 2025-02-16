package org.example.repository;

import org.example.entity.BackgroundResource;
import org.example.entity.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@RedisHash
public interface BackgroundRepo extends JpaRepository<BackgroundResource,Integer> {
    Optional<List<BackgroundResource>> findByUser(Users user);

    Optional<BackgroundResource> findByStoragePath(String backgroundUrl);
}