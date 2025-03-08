package org.example.service.Impl;

import lombok.extern.slf4j.Slf4j;
import org.example.entity.Book;
import org.example.index.BookIndex;
import org.example.repository.BookIndexRepository;
import org.example.service.BookIndexService;
import org.example.util.KeywordNLPExtractor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class BookIndexSImpl implements BookIndexService {
  @Autowired
  private BookIndexRepository indexRepository;
  @Autowired
  private KeywordNLPExtractor extractor;

    @Override
    public void createIndex(Book book) {
        BookIndex bookIndex = new BookIndex();
        bookIndex.setId(book.getBookId());
        bookIndex.setBookName(book.getBookName());
        bookIndex.setAuthor(book.getAuthor());
        bookIndex.setKeywords(extractor.extractKeywords(book.getBookName()+"_"+book.getBookDesc()));
        log.info("bookIndex   "+bookIndex.getKeywords());
        indexRepository.save(bookIndex);

    }

    @Override
    public List<String> getKeywords(Integer bookId) {
        BookIndex index= indexRepository.findById(bookId)
                .orElse(new BookIndex());
        return index.getKeywords();
    }
}
