package org.example.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.example.Listener.OnlineUserListener;
import org.example.dto.BookInfoDTO;
import org.example.service.RankService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/dashboard")
@Api(tags = "大屏看板")
public class DashboardController {


    @Autowired
    private RankService rankingService;
    @Autowired
    private OnlineUserListener onlineUserListener;

    /**
     * 获取在线用户数
     */
    @GetMapping("/onlineUsers")
    @ApiOperation(value = "获取在线用户数")
    public Map<String, Object> getOnlineUsers() {
        log.info("获取在线用户数");
        Map<String, Object> response = new HashMap<>();
        response.put("onlineUserCount", onlineUserListener.getOnlineUsers());
        return response;
    }


    @GetMapping("/top/{category}/{type}")
    @ApiOperation(value = "获取排行榜")
    public ResponseEntity<List<BookInfoDTO>> getTopRanking(@PathVariable String category, @PathVariable String type, @RequestParam(defaultValue = "10") int limit) {
        try{
            return ResponseEntity.ok(rankingService.getRanking(category, type, limit));
        }catch (Exception e){
            log.error(e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }
    @PostMapping("/updateRank")
    @ApiOperation(value = "更新排行榜")
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
