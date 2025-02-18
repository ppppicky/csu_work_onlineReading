package org.example.controller;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.extern.slf4j.Slf4j;
import org.example.dto.ReadRecordDTO;
import org.example.entity.Users;
import org.example.service.ReadRecordService;
import org.example.util.GlobalException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import java.util.List;

@Slf4j
@RestController
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
            @AuthenticationPrincipal Users users) {
        try {
            return ResponseEntity.ok().body(readService.getAllRecordsByUserId(users.getUserId()));
        } catch (Exception e) {
            log.error("获取用户阅读记录失败: {}", e.getLocalizedMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @ApiOperation(value = "获取书籍阅读进度", notes = "根据用户 ID 和书籍 ID 获取该用户的阅读进度")
    @ApiResponses({
            @ApiResponse(code = 200, message = "成功获取进度", response = ReadRecordDTO.class),
            @ApiResponse(code = 404, message = "记录未找到"),
            @ApiResponse(code = 500, message = "服务器内部错误")
    })
    @GetMapping("/{bookId}")
    public ResponseEntity<ReadRecordDTO> getRecord(
            @ApiParam(value = "书籍ID", required = true)@PathVariable Integer bookId, @AuthenticationPrincipal Users users
            //@RequestParam("userId")Integer userId
    ) {
        try {
            return ResponseEntity.ok().body(readService.getLastRecordByUserId(users.getUserId(), bookId));
        } catch (Exception e) {
            log.error("获取书籍阅读进度失败: {}", e.getLocalizedMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}



