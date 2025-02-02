package org.example.controller;

import org.example.entity.Book;
import org.example.service.BookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpSession;
import java.io.File;
import java.io.IOException;

@RestController
@RequestMapping("/book")
public class BookController {
    @Autowired
    private BookService bookService;

    /**
     *
     * @param file
     * @param session
     * @return
     */
    @PostMapping("/add")
    public ResponseEntity<String> addBook(@RequestParam("file") MultipartFile file,@RequestParam("bookTypeId")int typeId, HttpSession session){
        try {
            // 将上传的文件保存为临时文件
            File tempFile = File.createTempFile("epub-", ".epub");
            file.transferTo(tempFile);

            // 调用服务层添加书籍
            bookService.addBook(tempFile,typeId);

            // 返回成功信息
            return ResponseEntity.ok("Book added successfully!");
        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body("Error");
        }

    }
}
