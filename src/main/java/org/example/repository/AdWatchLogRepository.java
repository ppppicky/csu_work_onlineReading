package org.example.repository;

import org.example.entity.AdWatchLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AdWatchLogRepository extends JpaRepository<AdWatchLog, Integer> {
}
