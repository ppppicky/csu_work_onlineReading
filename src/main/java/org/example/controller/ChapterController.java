package org.example.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.example.service.ChapterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController

@Api(tags = "章节管理接口")
@RequestMapping("/chapter")
public class ChapterController {
    @Autowired
    ChapterService chapterService;
/**
     * @param chapterId
     * @param pageSize  页大小（默认为1000）
     * @return
     */
    @ApiOperation(value = "获取章节内容", notes = "根据章节 ID 获取章节内容，并支持分页")
    @ApiResponses({
            @ApiResponse(code = 200, message = "返回章节内容"),
            @ApiResponse(code = 404, message = "章节未找到")
    })
    @GetMapping("/{chapterId}")
    public String readChapter(@PathVariable Integer chapterId,
                              @RequestParam(defaultValue = "1000") int pageSize){
       // return chapterService.getChapterContent(chapterId, pageSize);//分页

        return chapterService.getChapterContent(chapterId);//不分页
    }
    // 在ChapterController.java中添加
    @ApiOperation(value = "管理员修改章节内容", notes = "需要管理员权限")
   // @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/update/{chapterId}")
    public ResponseEntity<String> updateChapterContent(
            @PathVariable Integer chapterId,
            @RequestBody String newContent) {

        // 这里需要实现章节服务中的更新方法
        chapterService.updateChapterContent(chapterId,newContent);
        return ResponseEntity.ok("update successfully");
    }
    @PutMapping("/updatename/{chapterId}")
    public ResponseEntity<String> updateChapterName(
            @PathVariable Integer chapterId,
            @RequestBody String newName) {

        // 这里需要实现章节服务中的更新方法
        chapterService.updateChapterName(chapterId,newName);
        return ResponseEntity.ok("update successfully");
    }

}
