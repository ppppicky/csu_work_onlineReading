package org.example.repository;

import org.example.entity.BookChapter;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ChapterRepo extends JpaRepository<BookChapter,Integer> {
    List<BookChapter> findByBookId(Integer bookId);
    Optional<BookChapter> findByBookIdAndChapterNum(Integer bookId, Integer chapterNum);

    void deleteByBookId(Integer bookId);
}
