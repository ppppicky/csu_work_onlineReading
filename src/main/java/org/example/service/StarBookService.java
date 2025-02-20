package org.example.service;

import org.example.entity.Book;

import java.util.List;

public interface StarBookService {
    List<Book> getStarBooksByUserId(Integer userId);
    
    boolean hasUserStarBook(Integer userId, Integer bookId);

    String toggleStarBook(Integer userId, Integer bookId);
}
