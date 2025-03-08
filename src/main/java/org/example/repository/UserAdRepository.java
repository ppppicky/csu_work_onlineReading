package org.example.repository;

import org.example.entity.Advert;
import org.example.entity.UserAdRecord;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.Random;

@Repository
@EnableJpaRepositories
public interface UserAdRepository extends JpaRepository<UserAdRecord,Integer> {


    Optional<UserAdRecord> findByUserId(Integer userId);
}
