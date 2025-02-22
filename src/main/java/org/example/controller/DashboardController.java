package org.example.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.example.Listener.OnlineUserListener;
import org.example.dto.*;
import org.example.entity.Book;
import org.example.repository.StarBookRepository;
import org.example.service.DashboardService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("/dashboard")
@Api(tags = "大屏看板")
public class DashboardController {

    @Autowired
    private StarBookRepository starBookRepository;

    @Autowired
    DashboardService dashboardService;

    /**
     * 获取收藏最多的书籍（前 10）
     */
    @GetMapping("/top-starred-books")
    @ApiOperation(value = "获取收藏最多的书籍（前 10）")
    public List<BookInfoDTO> getTopStarredBooks() {
        log.info("获取收藏最多的书籍");
        List<Object[]> result = starBookRepository.findTopStarredBooks(PageRequest.of(0, 10));
        return result.stream().map(obj -> {
            Book book = (Book) obj[0];
            Long starCount = (Long) obj[1];
            BookInfoDTO dto = new BookInfoDTO();
            BeanUtils.copyProperties(book, dto);
            dto.setBookPage(Math.toIntExact(starCount)); // 用 bookPage 暂存收藏次数
            return dto;
        }).collect(Collectors.toList());
    }

    // DashboardController.java 新增接口
    @GetMapping("/purchase-stats")
    @ApiOperation(value = "获取书籍购买情况统计")
    public PurchaseStatsDTO getPurchaseStats() {
        log.info("获取书籍购买情况统计");
        return dashboardService.getPurchaseStats();
    }

    /**
     * 获取书籍阅读情况统计
     */
    @GetMapping("/bookReadingStats")
    @ApiOperation(value = "获取书籍阅读情况统计")
    public ResponseEntity<BookReadingStatsDTO> getBookReadingStats() {
        log.info("获取书籍阅读情况统计");
        return ResponseEntity.ok(dashboardService.getBookReadingStats());
    }

    /**
     * 获取用户行为分析数据
     */
    @GetMapping("/userBehaviorStats")
    @ApiOperation(value = "获取用户行为分析数据")
    public ResponseEntity<UserBehaviorStatsDTO> getUserBehaviorStats() {
        log.info("获取用户行为分析数据");
        return ResponseEntity.ok(dashboardService.getUserBehaviorStats());
    }

    /**
     * 获取收费情况统计
     */
    @GetMapping("/chargeStats")
    @ApiOperation(value = "获取收费情况统计")
    public ResponseEntity<ChargeStatsDTO> getChargeStats() {
        log.info("获取收费情况统计");
        return ResponseEntity.ok(dashboardService.getChargeStats());
    }

    @GetMapping("/onlineUsers")
    @ApiOperation(value = "获取在线用户数")
    public Map<String, Object> getOnlineUsers() {
        log.info("获取在线用户数");
        Map<String, Object> response = new HashMap<>();
        response.put("onlineUserCount", OnlineUserListener.getOnlineUsers());
        return response;
    }
    /**
     * 获取书籍阅读情况
     */
    @GetMapping("/bookStats")
    @ApiOperation(value = "获取书籍阅读情况")
    public ResponseEntity<Map<String, Object>> getBookStats() {
        log.info("获取书籍阅读情况");
        return ResponseEntity.ok(dashboardService.getReadingStats());
    }

    /**
     * 获取用户行为分析数据
     */
    @GetMapping("/userStats")
    @ApiOperation(value = "获取用户行为分析数据")
    public ResponseEntity<Map<String, Object>> getUserStats() {
        log.info("获取用户行为分析数据");
        return ResponseEntity.ok(dashboardService.getBehaviorStats());
    }

    /**
     * 获取收费情况统计
     */
    @GetMapping("/revenueStats")
    @ApiOperation(value = "获取收费情况统计")
    public ResponseEntity<Map<String, Object>> getRevenueStats() {
        log.info("获取收费情况统计");
        return ResponseEntity.ok(dashboardService.getRevenueStats());
    }
//    @GetMapping("/charge-analysis")
//    public ChargeAnalysisDTO getChargeAnalysis() {
//        return dashboardService.getChargeAnalysis();
//    }
}