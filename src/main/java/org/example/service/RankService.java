package org.example.service;

import lombok.extern.slf4j.Slf4j;
import org.example.dto.BookInfoDTO;
import org.example.entity.Book;
import org.example.repository.BookRepository;
import org.example.repository.BoughtBookRepository;
import org.example.repository.StarBookRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;
import java.util.stream.Collectors;

@Service

@Slf4j
public class RankService {
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;
    @Autowired
    private BoughtBookRepository boughtBookRepository;
    @Autowired
    private StarBookRepository starBookRepository;
    @Autowired
    private BookRepository bookRepository;

    public void updateRanking(Integer bookId, String category, String type) {
        redisTemplate.opsForZSet().incrementScore(category + ":" + type, bookId.toString(), 1);
    }

    public List<BookInfoDTO> getRanking(String category, String type, int limit) {
        List<Book>books= redisTemplate.opsForZSet().reverseRange(category + ":" + type, 0, limit - 1)
                .stream().map(id ->
                        bookRepository.findById(Integer.parseInt(id.toString())).orElseGet(null))
                .collect(Collectors.toList());
        List<BookInfoDTO> dtos=books.stream().map((book ->{
            BookInfoDTO dto=new BookInfoDTO();
            dto.setBookId(book.getBookId());
            dto.setBookName(book.getBookName());
            dto.setAuthor(book.getAuthor());
            try {
                File coverFile = new File(book.getBookCover());
                if (coverFile.exists()) {
                    dto.setBookCover(Files.readAllBytes(coverFile.toPath()));
                } else {
                    dto.setBookCover(null);
                }
            } catch (IOException e) {
                log.error(e.getMessage());
            }
            return dto;
        })) .collect(Collectors.toList());
        return dtos;

    }

    @Transactional
    public void refreshDailyRanking() {
        resetDailyRanking();
        boughtBookRepository.findAll().forEach(book -> updateRanking(book.getBookId(), "bestseller", "daily"));
        starBookRepository.findAll().forEach(book -> updateRanking(book.getBookId(), "collection", "daily"));
    }

    @Transactional
    public void refreshWeeklyRanking() {
        resetWeeklyRanking();
        boughtBookRepository.findAll().forEach(book -> updateRanking(book.getBookId(), "bestseller", "weekly"));
        starBookRepository.findAll().forEach(book -> updateRanking(book.getBookId(), "collection", "weekly"));
    }

    @Transactional
    public void refreshMonthlyRanking() {
        resetMonthlyRanking();
        boughtBookRepository.findAll().forEach(book -> updateRanking(book.getBookId(), "bestseller", "monthly"));
        starBookRepository.findAll().forEach(book -> updateRanking(book.getBookId(), "collection", "monthly"));
    }

    public void refreshTotalRanking() {
        boughtBookRepository.findAll().forEach(book -> updateRanking(book.getBookId(), "bestseller", "total"));
        starBookRepository.findAll().forEach(book -> updateRanking(book.getBookId(), "collection", "total"));
    }

    private void resetDailyRanking() {
        redisTemplate.delete("bestseller:daily");
        redisTemplate.delete("collection:daily");
    }

    private void resetWeeklyRanking() {
        redisTemplate.delete("bestseller:weekly");
        redisTemplate.delete("collection:weekly");
    }

    private void resetMonthlyRanking() {
        redisTemplate.delete("bestseller:monthly");
        redisTemplate.delete("collection:monthly");
    }
}