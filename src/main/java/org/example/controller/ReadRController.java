package org.example.controller;

import lombok.extern.slf4j.Slf4j;
import org.example.dto.ReadRecordDTO;
import org.example.util.GlobalException;
import org.example.service.ReadRecordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;

@RestController
@RequestMapping("/read")
@Slf4j
public class ReadRController {
    @Autowired
    ReadRecordService readRecordService;

    /**
     * 保存阅读进度
     * @param dto
     * @param session
     * @return
     */
    @PostMapping("/save")
    public ResponseEntity<?> save(@Valid @RequestBody ReadRecordDTO dto,HttpSession session) {
        try {
            readRecordService.processNewRecord(dto);
            return ResponseEntity.ok().build();
        } catch (GlobalException.UserNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (GlobalException.BookNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (GlobalException.InvalidPageException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            log.error( e.getLocalizedMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }
}
