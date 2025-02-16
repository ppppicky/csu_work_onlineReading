package org.example.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.example.dto.ChapterVO;
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
     *添加书籍类型
     * @param typeName
     * @param session
     * @return
     */
    @ApiOperation(value = "添加书籍类型",tags = "添加书籍类型")
    @ApiResponses({
            @ApiResponse(code = 200, message = "booktype add successfully",response =String.class),
            @ApiResponse(code = 404, message = "booktype existed")
    })
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
