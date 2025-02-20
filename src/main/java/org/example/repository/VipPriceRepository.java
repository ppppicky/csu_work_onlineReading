package org.example.repository;

import org.example.entity.VipPrice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface VipPriceRepository extends JpaRepository<VipPrice, Integer> {
    Optional<VipPrice> findByVipType(String vipType);
}
