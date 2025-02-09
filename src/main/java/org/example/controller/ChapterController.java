package org.example.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.example.service.ChapterService;
import org.springframework.beans.factory.annotation.Autowired;
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
}
