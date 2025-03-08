package org.example.repository;

import org.example.index.BookIndex;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface BookIndexRepository extends ElasticsearchRepository<BookIndex,Integer> {
}
