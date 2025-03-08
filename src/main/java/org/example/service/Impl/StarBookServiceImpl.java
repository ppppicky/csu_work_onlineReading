package org.example.service.Impl;

import org.example.entity.Book;
import org.example.entity.StarBook;
import org.example.repository.BookRepository;
import org.example.repository.StarBookRepository;
import org.example.service.StarBookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class StarBookServiceImpl implements StarBookService {


    @Autowired
    StarBookRepository starBookRepository;

    @Autowired
    BookRepository bookRepository;

    @Autowired
    public StarBookServiceImpl(StarBookRepository starBookRepository, BookRepository bookRepository) {
        this.starBookRepository = starBookRepository;
        this.bookRepository = bookRepository;
    }

    /**
     * 根据用户ID获取用户收藏的书籍列表
     * @param userId
     * @return
     */
    @Override
    public List<Book> getStarBooksByUserId(Integer userId) {
        List<StarBook> starBooks = starBookRepository.findByUserId(userId);
        if (starBooks.isEmpty()){
            return Collections.emptyList();
        }
        List<Integer> bookIds = starBooks.stream()
                .map(StarBook::getBookId)
                .collect(Collectors.toList());
        return bookRepository.findByBookIdIn(bookIds);
    }

    /**
     *  检查用户是否收藏指定书籍
     * @param userId
     * @param bookId
     * @return
     */
    @Override
    public boolean hasUserStarBook(Integer userId, Integer bookId) {
        return starBookRepository.findByUserIdAndBookId(userId, bookId).isPresent();
    }

    /**
     * 切换收藏状态
     * @param userId
     * @param bookId
     * @return
     */
    @Override
    public String toggleStarBook(Integer userId, Integer bookId) {
        if (!bookRepository.existsByBookId(bookId)){
            return "The book doesn't exist";
        }
        Optional<StarBook> starBookOptional = starBookRepository.findByUserIdAndBookId(userId, bookId);
        if (starBookOptional.isPresent()){
            // 如果已收藏，则取消收藏
            starBookRepository.delete(starBookOptional.get());
            return "Unstarred";
        }else {
            // 如果未收藏，则添加收藏
            StarBook starBook = new StarBook();
            starBook.setUserId(userId);
            starBook.setBookId(bookId);
            starBook.setStarTime(LocalDateTime.now());
            starBookRepository.save(starBook);
            return "Starred";
        }
    }


}
