package org.example.controller;

import io.swagger.annotations.Api;
import org.example.service.BookTypeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpSession;

@RestController
@Api(tags = "图书类型管理接口")
@RequestMapping("/type")
public class BookTypeController {
    @Autowired
    BookTypeService bookTypeService;

    /**
     *
     * @param typeName
     * @param session
     * @return
     */
    @PostMapping("/add")
    ResponseEntity<String> addBookType(@RequestBody String typeName , HttpSession session){
        try {
            bookTypeService.addType(typeName);
            return ResponseEntity.ok("booktype add successfully");
        }catch (Exception e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("booktype existed");
        }
    }

}
