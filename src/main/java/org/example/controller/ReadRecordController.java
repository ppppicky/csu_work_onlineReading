package org.example.controller;

import io.swagger.annotations.*;
import lombok.extern.slf4j.Slf4j;
import org.example.dto.ReadRecordDTO;
import org.example.service.ReadRecordService;
import org.example.util.GlobalException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import java.util.Collections;
import java.util.List;

@Slf4j
@RestController
@Api(value = "阅读记录控制器",tags = "阅读记录管理接口")
@RequestMapping("/record")
public class ReadRecordController {
    @Autowired
    ReadRecordService readService;

    /**
     * 保存阅读进度
     *
     * @param dto
     * @param session
     * @return
     */
    @ApiOperation(value = "保存用户阅读进度", notes = "保存当前用户的阅读进度信息")
    @ApiResponses({
            @ApiResponse(code = 200, message = "成功保存"),
            @ApiResponse(code = 400, message = "请求参数错误"),
            @ApiResponse(code = 404, message = "用户或书籍未找到"),
            @ApiResponse(code = 500, message = "服务器内部错误")
    })
    @PostMapping("/save")
    public ResponseEntity<?> save(
            @ApiParam(value = "阅读记录信息", required = true) @Valid @RequestBody ReadRecordDTO dto, HttpSession session) {
        log.info("保存阅读进度: {}", dto);
        try {
            readService.processNewRecord(dto);
            return ResponseEntity.ok().build();
        } catch (GlobalException.UserNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (GlobalException.BookNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (GlobalException.InvalidPageException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            log.error("保存阅读进度失败: {}", e.getLocalizedMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("服务器内部错误");
        }
    }

    @ApiOperation(value = "获取用户的所有阅读记录", notes = "根据用户 ID 获取该用户的所有阅读记录")
    @ApiResponses({
            @ApiResponse(code = 200, message = "成功获取记录", response = ReadRecordDTO.class, responseContainer = "List"),
            @ApiResponse(code = 500, message = "服务器内部错误")
    })
    @GetMapping("/allRecords/{userId}")
    public ResponseEntity<List<ReadRecordDTO>> getUsersAllRecord(
            //  @AuthenticationPrincipal Users users)
            @PathVariable Integer userId) {
        log.info("获取用户的所有阅读记录: {}", userId);
        try {
            return ResponseEntity.ok().body(readService.getAllRecordsByUserId(userId));
        } catch (Exception e) {
            if (e.getMessage().equals("book not been read yet")) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Collections.singletonList(new ReadRecordDTO()));
            }
            log.error("获取书籍阅读进度失败: {}", e.getLocalizedMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    @ApiOperation(value = "获取书籍阅读进度", notes = "根据用户 ID 和书籍 ID 获取该用户的阅读进度")
    @ApiResponses({
            @ApiResponse(code = 200, message = "成功检索阅读记录", response = ReadRecordDTO.class),
            @ApiResponse(code = 404, message = "指定的书籍或用户不存在或未读过该项目", response = ReadRecordDTO.class),
            @ApiResponse(code = 500, message = "服务器错误", response = Void.class)
    })
    @GetMapping("/{bookId}/{userId}")
    public ResponseEntity<ReadRecordDTO> getRecord(
            @ApiParam(value = "书籍ID", required = true) @PathVariable Integer bookId, @PathVariable Integer userId
            //  @AuthenticationPrincipal Users users
            //   @RequestParam("userId")Integer userId
    ) {
        log.info("获取书籍阅读进度: {}", bookId);
        try {
            return ResponseEntity.ok().body(readService.getLastRecordByUserId(userId, bookId));
        } catch (Exception e) {
            if (e.getMessage().equals("book not been read yet")) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ReadRecordDTO());
            }
            log.error("获取书籍阅读进度失败: {}", e.getLocalizedMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }
}



