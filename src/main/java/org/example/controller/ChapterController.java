package org.example.controller;

import com.sun.org.apache.regexp.internal.RE;
import io.swagger.annotations.*;
import lombok.extern.slf4j.Slf4j;
import org.example.dto.BookInfoDTO;
import org.example.dto.ChapterDTO;
import org.example.service.ChapterService;
import org.example.util.GlobalException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController

@Api(tags = "章节管理接口")
@RequestMapping("/chapter")
public class ChapterController {
    @Autowired
    ChapterService chapterService;
/**
     * @param chapterId
     * @return
     */
    @ApiOperation(value = "获取章节内容", notes = "根据章节 ID 获取章节内容，并支持分页")
    @ApiResponses({
            @ApiResponse(code = 200, message = "返回章节内容"),
            @ApiResponse(code = 404, message = "章节未找到")
    })
    @GetMapping("/{chapterId}")
    public String readChapter(@PathVariable Integer chapterId
        //@RequestParam(defaultValue = "0") int page,
       // @RequestParam(defaultValue = "1000") int pageSize
                              ){
            return chapterService.getChapterContent(chapterId);//分页

       // return chapterService.getChapterContent(chapterId);//不分页
    }



    @ApiOperation(value = "更新章节内容", notes = "需要管理员权限")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "chapterId", value = "章节ID", required = true, dataType = "integer", paramType = "path")
    })
    @ApiResponses({
            @ApiResponse(code = 200, message = "成功更新章节内容", response = String.class),
            @ApiResponse(code = 400, message = "非法参数"),
            @ApiResponse(code = 404, message = "未找到章节")
    })
   // @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/update/{chapterId}")
    public ResponseEntity<String> updateChapterContent(
            @PathVariable Integer chapterId,
            @RequestBody String newContent) {

        chapterService.updateChapterContent(chapterId,newContent);
        return ResponseEntity.ok("update successfully");
    }

    @ApiOperation(value = "更新章节名称", notes = "需要管理员权限")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "chapterId", value = "章节ID", required = true, dataType = "integer", paramType = "path")
    })
    @ApiResponses({
            @ApiResponse(code = 200, message = "成功更新章节名称", response = String.class),
            @ApiResponse(code = 400, message = "非法参数"),
            @ApiResponse(code = 404, message = "未找到章节")
    })
    @PutMapping("/updatename/{chapterId}")
    public ResponseEntity<String> updateChapterName(
            @PathVariable Integer chapterId, @RequestBody String newName) {
        chapterService.updateChapterName(chapterId,newName);
        return ResponseEntity.ok("update successfully");
    }

    @ApiOperation(value = "创建章节", notes = "根据书籍ID创建新章节")
    @ApiImplicitParam(name = "bookId", value = "书籍ID", required = true, dataType = "integer", paramType = "path")
    @ApiResponses({
            @ApiResponse(code = 200, message = "成功创建章节", response = String.class),
            @ApiResponse(code = 400, message = "非法参数"),
            @ApiResponse(code = 404, message = "未找到书籍")
    })
    @PostMapping("/create/{bookId}")
    public ResponseEntity<String> createChapter(
            @PathVariable Integer bookId, @RequestBody ChapterDTO chapterDTO) {
        try {
            chapterService.createChapter(bookId, chapterDTO);
            return ResponseEntity.ok().body("create chapter successfully");
        }
            catch (GlobalException.BookNotFoundException e) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
            } catch (GlobalException.InvalidPageException e) {
                return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

}
