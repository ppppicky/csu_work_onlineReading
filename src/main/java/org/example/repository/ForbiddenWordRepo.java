package org.example.repository;

import org.example.entity.ForbiddenWord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ForbiddenWordRepo extends JpaRepository<ForbiddenWord,Integer> {
    //void deleteByWord(String word);
   Optional< ForbiddenWord> findByWord(String word);

}
