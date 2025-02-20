package org.example.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.example.entity.Book;
import org.example.service.StarBookService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/starBooks")
@Api(tags = "收藏书籍管理")
public class StarBookController {
    private final StarBookService starBookService;

    public StarBookController(StarBookService starBookService) {
        this.starBookService = starBookService;
    }

    /**
     * 收藏 / 取消收藏书籍
     */
    @PostMapping("/toggle")
    @ApiOperation(value = "收藏或取消收藏书籍")
    public ResponseEntity<String> toggleStarBook(@RequestParam Integer userId, @RequestParam Integer bookId) {
        String result = starBookService.toggleStarBook(userId, bookId);
        return ResponseEntity.ok(result);
    }

    /**
     * 查询用户收藏书籍列表
     * @param userId
     * @return
     */
    @GetMapping("/user/{userId}")
    @ApiOperation(value = "查询用户收藏书籍列表")
    public ResponseEntity<List<Book>> getStarBooksByUserId(@PathVariable Integer userId) {
        List<Book> books = starBookService.getStarBooksByUserId(userId);
        return books.isEmpty() ? ResponseEntity.notFound().build() : ResponseEntity.ok(books);
    }


    /**
     * 检查用户是否收藏指定书籍
     * @param userId
     * @param bookId
     * @return
     */
    @GetMapping("/user/{userId}/book/{bookId}")
    @ApiOperation(value = "检查用户是否收藏指定书籍")
    public ResponseEntity<Boolean> hasUserStarBook(@PathVariable Integer userId, @PathVariable Integer bookId) {
        boolean hasStar = starBookService.hasUserStarBook(userId, bookId);
        return ResponseEntity.ok(hasStar);
    }

}
