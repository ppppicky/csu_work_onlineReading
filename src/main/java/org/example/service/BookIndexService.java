package org.example.service;

import org.example.entity.Book;

import java.util.List;

public interface BookIndexService {
    public void createIndex(Book book);
    public List<String> getKeywords(Integer bookId);
}
