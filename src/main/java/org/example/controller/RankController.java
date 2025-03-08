package org.example.controller;

import com.sun.org.apache.regexp.internal.RE;
import lombok.extern.slf4j.Slf4j;
import org.example.dto.BookInfoDTO;
import org.example.service.RankService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/ranking")
@Slf4j
public class RankController {
    @Autowired
    private RankService rankingService;

    @GetMapping("/top/{category}/{type}")
    public ResponseEntity<List<BookInfoDTO>> getTopRanking(@PathVariable String category, @PathVariable String type, @RequestParam(defaultValue = "10") int limit) {
        try{
            return ResponseEntity.ok(rankingService.getRanking(category, type, limit));
        }catch (Exception e){
            log.error(e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }
    @PostMapping("/up")
    public ResponseEntity<String> update(){
       try{
           rankingService.refreshDailyRanking();
           rankingService.refreshTotalRanking();
           rankingService.refreshMonthlyRanking();
           rankingService.refreshWeeklyRanking();
           return ResponseEntity.ok("success");

       }catch (Exception e){
           log.error(e.getMessage());
           return ResponseEntity.badRequest().build();
       }
    }
}

