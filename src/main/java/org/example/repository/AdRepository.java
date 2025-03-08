package org.example.repository;

import org.example.entity.Advert;
import org.example.entity.UserAdRecord;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Random;

@Repository
public interface AdRepository extends ElasticsearchRepository<Advert,Integer> {
//    @Query("SELECT COUNT(a) FROM Advert a")
//    long countAll();

//    @Query("SELECT a FROM Advert a")
//    List<Advert> findWithOffset(Pageable pageable);
//
//    default Advert findRandomAd() {
//        long count = countAll();
//        if (count == 0) return null;
//        int randomOffset = new Random().nextInt((int) count);
//        return findWithOffset(PageRequest.of(randomOffset, 1)).get(0);
//    }

}
