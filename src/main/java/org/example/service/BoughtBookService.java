package org.example.service;

import org.example.entity.Book;

import java.util.List;

public interface BoughtBookService {
    /**
     * 根据用户 ID 查询已购书籍
     */
    List<Book> getBoughtBooksByUserId(Integer userId);

    /**
     * 判断用户是否已购买指定书籍
     */
    boolean hasUserBoughtBook(Integer userId, Integer bookId);
}
