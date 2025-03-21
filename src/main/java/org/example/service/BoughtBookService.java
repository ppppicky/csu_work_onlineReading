package org.example.service;

import org.example.entity.Book;

import java.util.List;

public interface BoughtBookService {

    List<Book> getBoughtBooksByUserId(Integer userId);


    boolean hasUserBoughtBook(Integer userId, Integer bookId);
}
