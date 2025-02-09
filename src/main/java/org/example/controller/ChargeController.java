package org.example.controller;

import lombok.extern.slf4j.Slf4j;
import org.example.dto.ChargeDTO;
import org.example.dto.SetChargeStatusDTO;
import org.example.entity.ChargeManagement;
import org.example.service.ChargeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@Slf4j
@RestController
@RequestMapping("/charge")
public class ChargeController {

    private final ChargeService chargeService;

    @Autowired
    public ChargeController(ChargeService chargeService) {
        this.chargeService = chargeService;
    }

    /**
     * 根据书籍 ID 获取收费信息
     * @param bookId 书籍 ID
     * @return ResponseEntity<ChargeManagement>
     */
    @GetMapping("/book/{bookId}")
    public ResponseEntity<ChargeManagement> getChargeInfo(@PathVariable int bookId) {
        Optional<ChargeManagement> chargeInfo = chargeService.getChargeInfoByBookId(bookId);
        return chargeInfo.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    /**
     * 更新书籍收费信息
     */
    @PutMapping("/update")
    public ResponseEntity<String> updateChargeDetails(@RequestBody ChargeDTO chargeDTO) {
        chargeService.updateChargeDetails(chargeDTO);
        return ResponseEntity.ok("updateChargeDetails successfully");
    }

    /**
     * 设置书籍收费状态（使用 DTO）
     */
    @PutMapping("/setChargeStatus")
    public ResponseEntity<String> setBookChargeStatus(@RequestBody SetChargeStatusDTO setChargeStatusDTO) {
        chargeService.setBookChargeStatus(setChargeStatusDTO);
        return ResponseEntity.ok("setBookChargeStatus successfully");
    }
}
