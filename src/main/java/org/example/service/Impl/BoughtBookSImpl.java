package org.example.service.Impl;

import org.example.entity.BoughtBook;
import org.example.entity.Book;
import org.example.repository.BoughtBookRepository;
import org.example.repository.BookRepository;
import org.example.service.BoughtBookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class BoughtBookSImpl implements BoughtBookService {

    private final BoughtBookRepository boughtBookRepository;
    private final BookRepository bookRepository;

    @Autowired
    public BoughtBookSImpl(BoughtBookRepository boughtBookRepository, BookRepository bookRepository) {
        this.boughtBookRepository = boughtBookRepository;
        this.bookRepository = bookRepository;
    }

    /**
     * 根据用户 ID 查询已购书籍
     */
    @Override
    public List<Book> getBoughtBooksByUserId(Integer userId) {
        List<BoughtBook> boughtBooks = boughtBookRepository.findByUserId(userId);
        if (boughtBooks.isEmpty()) {
            return Collections.emptyList(); // 返回空列表
        }

        List<Integer> bookIds = boughtBooks.stream()
                .map(BoughtBook::getBookId)
                .collect(Collectors.toList());

        return bookRepository.findByBookIdIn(bookIds);
    }

    /**
     * 判断用户是否已购买指定书籍
     */
    @Override
    public boolean hasUserBoughtBook(Integer userId, Integer bookId) {
        return boughtBookRepository.findByUserIdAndBookId(userId, bookId).isPresent();
    }
}
