package org.example.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.example.entity.Book;
import org.example.service.BoughtBookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/boughtBooks")
@Api(tags = "已购书籍管理")
public class BoughtBookController {

    @Autowired
    BoughtBookService boughtBookService;

    /**
     * 查询用户已购书籍列表
     */
    @GetMapping("/user/{userId}")
    @ApiOperation(value = "查询用户已购书籍列表")
    public ResponseEntity<List<Book>> getBoughtBooksByUserId(@PathVariable Integer userId) {
        log.info("查询用户已购书籍列表，用户ID：{}", userId);
        List<Book> books = boughtBookService.getBoughtBooksByUserId(userId);
        return books.isEmpty() ? ResponseEntity.notFound().build() : ResponseEntity.ok(books);
    }

    /**
     * 判断用户是否已购买指定书籍
     */
    @GetMapping("/user/{userId}/book/{bookId}")
    @ApiOperation(value = "检查用户是否已购买指定书籍")
    public ResponseEntity<Boolean> hasUserBoughtBook(@PathVariable Integer userId, @PathVariable Integer bookId) {
        log.info("检查用户是否已购买指定书籍，用户ID：{}，书籍ID：{}", userId, bookId);
        return ResponseEntity.ok(boughtBookService.hasUserBoughtBook(userId, bookId));
    }
}
