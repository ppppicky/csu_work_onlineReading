package org.example.controller;

import cn.hutool.core.io.resource.InputStreamResource;
import io.swagger.annotations.*;
import lombok.extern.slf4j.Slf4j;
import org.example.dto.BookInfoDTO;
import org.example.dto.BookChapterCombinationDTO;
import org.example.dto.ChapterDTO;
import org.example.dto.CoverTempDTO;
import org.example.repository.ForbiddenWordRepo;
import org.example.service.BookService;
import org.example.util.BookCoverTempDealer;
import org.example.util.GlobalException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpSession;
import java.io.ByteArrayInputStream;
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
    BookCoverTempDealer coverTempDealer;
//    /**
//     * 添加书籍
//     * @param file
//     * @param typeName
//     * @param isVip
//     * @param session
//     * @return
//     */
//    @ApiOperation(value = "添加书籍", notes = "上传EPUB文件并添加书籍到系统")
//    @ApiResponses({
//            @ApiResponse(code = 200, message = "书籍添加成功",response = String.class),
//            @ApiResponse(code = 500, message = "服务器内部错误")
//    })
//    @PostMapping("/add")
//    public ResponseEntity<String> addBook(
//            @ApiParam(value = "上传的EPUB文件", required = true) @RequestParam("file") MultipartFile file,
//            @ApiParam(value = "书籍类型名称", required = true) @RequestParam("typeName") String typeName,
//            @ApiParam(value = "是否为VIP书籍 (0: 否, 1: 是)", required = true) @RequestParam("isVIP") byte isVip,HttpSession session) {
//        try {
//            // 将上传的文件保存为临时文件
//            File tempFile = File.createTempFile("epub-", ".epub");
//            file.transferTo(tempFile);
//            bookService.addBook(tempFile, typeName, isVip);
//            return ResponseEntity.ok("Book added successfully!");
//        } catch (IOException e) {
//            e.printStackTrace();
//            return ResponseEntity.status(500).body("Error:" + e.getMessage());
//        }
//    }
@ApiOperation(value = "解析EPUB书籍", notes = "上传EPUB文件并返回书籍元数据和章节目录")
@ApiResponses({
        @ApiResponse(code = 200, message = "解析成功", response = BookChapterCombinationDTO.class),
        @ApiResponse(code = 400, message = "解析失败")
})
@PostMapping("/parse")
public ResponseEntity<BookChapterCombinationDTO> parseBook(
        @ApiParam(value = "上传的EPUB文件", required = true)
        @RequestParam("file") MultipartFile file) {
    try {
        File tempFile = File.createTempFile("epub-", ".epub");
        file.transferTo(tempFile);
        BookChapterCombinationDTO result = bookService.parseEpub(tempFile);
        return ResponseEntity.ok(result);
    } catch (Exception e) {
        return ResponseEntity.badRequest().build();
    }
}

    /**
     * 获取图书目录信息
     * @param bookId
     * @return 章节目录列表
     */
    @ApiOperation(value = "获取书籍目录", notes = "根据书籍 ID 获取章节目录")
    @ApiResponses({
            @ApiResponse(code = 200, message = "返回章节目录列表",response = ChapterDTO.class),
            @ApiResponse(code = 404, message = "书籍未找到")
    })
    @GetMapping("/{bookId}/toc")
    public List<ChapterDTO> getTOC(@PathVariable Integer bookId) {
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
    ResponseEntity<String> deleteBook(
            @ApiParam(value = "书籍ID", required = true) @PathVariable Integer bookId, HttpSession session) {
        try {

            bookService.deleteBook(bookId);
            return ResponseEntity.ok("delete successfully");

        } catch (GlobalException.BookNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Deletion failed: " + e.getMessage());
        }
    }

    @ApiOperation(value = "获取书籍信息", notes = "根据书籍 ID 获取书籍详细信息")
    @ApiResponses({
            @ApiResponse(code = 200, message = "返回书籍详细信息", response = BookInfoDTO.class),
            @ApiResponse(code = 404, message = "书籍未找到")
    })
    @GetMapping("/{bookId}")
    ResponseEntity<BookInfoDTO>getBookInfo(
            @ApiParam(value = "书籍ID", required = true) @PathVariable Integer bookId, HttpSession session){
        BookInfoDTO bookInfoDTO=new BookInfoDTO();
      try {
          return ResponseEntity.ok().body(bookService.getBook(bookId));
      }catch (Exception e){
          return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
      }
    }
    /**
     * 查看书籍列表（支持分页 & 模糊查询）
     * @param page 当前页码
     * @param size 每页大小
     * @param keyword 模糊查询关键词（可选）
     * @return 书籍列表（分页）
     */
    @GetMapping("/list")
    @ApiOperation(value = "查看书籍列表（支持模糊查询）")
    public ResponseEntity<Page<BookInfoDTO>> getBooksList(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String keyword) {

        Pageable pageable = PageRequest.of(page, size);
        Page<BookInfoDTO> books = bookService.getBooksList(keyword, pageable);
        return ResponseEntity.ok(books);
    }
    @GetMapping("/list/{typeName}")
    ResponseEntity<Page<BookInfoDTO>> getBooksByType(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @PathVariable("typeName") String typeName) {

        Pageable pageable = PageRequest.of(page, size);
        Page<BookInfoDTO> books = bookService.getBooksByType(pageable, typeName);
        return ResponseEntity.ok(books);
    }

    @ApiOperation(value = "更新书籍信息", notes = "根据书籍 ID 更新书籍详细信息")
    @ApiResponses({
            @ApiResponse(code = 200, message = "更新成功", response = String.class),
            @ApiResponse(code = 404, message = "书籍未找到")
    })
    @PostMapping("/update")
    ResponseEntity<String> updateBook(
            @ApiParam(value = "书籍详细信息", required = true)@RequestBody BookInfoDTO bookInfoDTO, HttpSession session){
       try {
           bookService.updateBook(bookInfoDTO);
           return ResponseEntity.ok("update successfully");
       }catch (Exception e){
           return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
       }
    }

    @ApiOperation(value = "创建书籍", notes = "创建一本新书籍")
    @ApiResponses({
            @ApiResponse(code = 200, message = "创建成功", response = String.class),
            @ApiResponse(code = 404, message = "书籍类型未找到")
    })
    @PostMapping("/create")
    ResponseEntity<String> createBook(
            @ApiParam(value = "书籍详细信息", required = true) @RequestBody BookChapterCombinationDTO combinationDTO,HttpSession session){
        try {
            bookService.createBook(combinationDTO);
            return ResponseEntity.ok("update successfully");
        }catch (Exception e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }


    @ApiOperation(value = "上传封面图片", notes = "上传封面图片并返回临时存储信息")
    @ApiResponses({
            @ApiResponse(code = 200, message = "上传成功", response = CoverTempDTO.class),
            @ApiResponse(code = 400, message = "文件上传失败")
    })
    @PostMapping(value = "/uploadCover", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<CoverTempDTO> uploadCover(
            @ApiParam(value = "封面图片文件", required = true)@RequestParam("file") MultipartFile file) throws IOException {
        //return ResponseEntity.ok(coverTempService.saveTempCover(file));
        return ResponseEntity.ok(coverTempDealer.saveTempCover(file));
    }

    @ApiOperation(value = "预览封面图片", notes = "根据临时存储url预览封面图片")
    @ApiResponses({
            @ApiResponse(code = 200, message = "预览成功", response = CoverTempDTO.class),
            @ApiResponse(code = 404, message = "封面图片未找到")
    })
    @GetMapping("/coverPreview")
    public  ResponseEntity<InputStreamResource>previewCover(
            @ApiParam(value = "rediskey", required = true) @RequestParam("tempKey") String tempKey) {
        CoverTempDTO cover=coverTempDealer.getTempCover(tempKey);
      //  CoverTempDTO cover = coverTempService.getTempCover(tempKey);
        if (cover.getImageData() == null) {
            return ResponseEntity.notFound().build();
        }
        ByteArrayInputStream inputStream = new ByteArrayInputStream(cover.getImageData());
        InputStreamResource resource = new InputStreamResource(inputStream);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.IMAGE_JPEG); // 确保是正确的图片格式
        return new ResponseEntity<>(resource, headers, HttpStatus.OK);
  //      return ResponseEntity.ok(cover);
    }

}
