package org.example.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.extern.slf4j.Slf4j;
import org.example.dto.BookInfoDTO;
import org.example.dto.ChapterVO;
import org.example.repository.ForbiddenWordRepo;
import org.example.service.BookService;
import org.example.util.ContentFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpSession;
import java.io.File;
import java.io.IOException;
import java.util.List;

@Slf4j
@Api(value = "图书管理接口", tags = "图书管理接口")
@RestController
@RequestMapping("/book")
public class BookController {
    @Autowired
    private BookService bookService;

    @Autowired
    ForbiddenWordRepo forbiddenWordRepo;

    /**
     * 添加书籍
     * @param file
     * @param typeName
     * @param isVip
     * @param session
     * @return
     */
    @ApiOperation(value = "添加书籍", notes = "上传EPUB文件并添加书籍到系统")
    @ApiResponses({
            @ApiResponse(code = 200, message = "书籍添加成功",response = String.class),
            @ApiResponse(code = 500, message = "服务器内部错误")
    })
    @PostMapping("/add")
    public ResponseEntity<String> addBook(@RequestParam("file") MultipartFile file, @RequestParam("typeName") String typeName,
                                          @RequestParam("isVIP") byte isVip, HttpSession session) {
        try {
            // 将上传的文件保存为临时文件
            File tempFile = File.createTempFile("epub-", ".epub");
            file.transferTo(tempFile);
            bookService.addBook(tempFile, typeName, isVip);
            return ResponseEntity.ok("Book added successfully!");
        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body("Error:" + e.getMessage());
        }
    }

    /**
     * 获取图书目录信息
     * @param bookId
     * @return 章节目录列表
     */
    @ApiOperation(value = "获取书籍目录", notes = "根据书籍 ID 获取章节目录")
    @ApiResponses({
            @ApiResponse(code = 200, message = "返回章节目录列表",response = ChapterVO.class),
            @ApiResponse(code = 404, message = "书籍未找到")
    })
    @GetMapping("/{bookId}/toc")
    public List<ChapterVO> getTOC(@PathVariable Integer bookId) {
        return bookService.getBookTOC(bookId);
    }

    /**
     * 删除书籍
     * @param bookId
     * @param session
     * @return
     */
    @ApiOperation(value = "删除书籍", notes = "删除书籍及其相关章节")
    @ApiResponses({
            @ApiResponse(code = 200, message = "书籍删除成功",response = String.class),
            @ApiResponse(code = 500, message = "服务器内部错误",response = String.class)
    })
    @DeleteMapping("/delete/{bookId}")
    ResponseEntity<String> deleteBook(@PathVariable Integer bookId, HttpSession session) {
        try {
            bookService.deleteBook(bookId);
            return ResponseEntity.ok("delete successfully");
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Deletion failed: " + e.getMessage());
        }
    }

    @GetMapping("/{bookId}")
    ResponseEntity<BookInfoDTO>getBookInfo(@PathVariable Integer bookId, HttpSession session){
        BookInfoDTO bookInfoDTO=new BookInfoDTO();
      try {
          return ResponseEntity.ok().body(bookService.getBook(bookId));
      }catch (Exception e){
          return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
      }

    }
    @PostMapping("/update")
    ResponseEntity<String> updateBook(@RequestBody BookInfoDTO bookInfoDTO, HttpSession session){
       try {
           bookService.updateBook(bookInfoDTO);
           return ResponseEntity.ok("update successfully");
       }catch (Exception e){
           return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
       }
    }


}
