package org.example.controller;

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

@RestController
@RequestMapping("/dashboard")
public class DashboardController {

    @Autowired
    private StarBookRepository starBookRepository;

    @Autowired
    DashboardService dashboardService;

    /**
     * 获取收藏最多的书籍（前 10）
     */
    @GetMapping("/top-starred-books")
    public List<BookInfoDTO> getTopStarredBooks() {
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
    public PurchaseStatsDTO getPurchaseStats() {
        return dashboardService.getPurchaseStats();
    }

    /**
     * 获取书籍阅读情况统计
     */
    @GetMapping("/bookReadingStats")
    public ResponseEntity<BookReadingStatsDTO> getBookReadingStats() {
        return ResponseEntity.ok(dashboardService.getBookReadingStats());
    }

    /**
     * 获取用户行为分析数据
     */
    @GetMapping("/userBehaviorStats")
    public ResponseEntity<UserBehaviorStatsDTO> getUserBehaviorStats() {
        return ResponseEntity.ok(dashboardService.getUserBehaviorStats());
    }

    /**
     * 获取收费情况统计
     */
    @GetMapping("/chargeStats")
    public ResponseEntity<ChargeStatsDTO> getChargeStats() {
        return ResponseEntity.ok(dashboardService.getChargeStats());
    }

    @GetMapping("/onlineUsers")
    public Map<String, Object> getOnlineUsers() {
        Map<String, Object> response = new HashMap<>();
        response.put("onlineUserCount", OnlineUserListener.getOnlineUsers());
        return response;
    }
    /**
     * 获取书籍阅读情况
     */
    @GetMapping("/bookStats")
    public ResponseEntity<Map<String, Object>> getBookStats() {
        return ResponseEntity.ok(dashboardService.getReadingStats());
    }

    /**
     * 获取用户行为分析数据
     */
    @GetMapping("/userStats")
    public ResponseEntity<Map<String, Object>> getUserStats() {
        return ResponseEntity.ok(dashboardService.getBehaviorStats());
    }

    /**
     * 获取收费情况统计
     */
    @GetMapping("/revenueStats")
    public ResponseEntity<Map<String, Object>> getRevenueStats() {
        return ResponseEntity.ok(dashboardService.getRevenueStats());
    }
//    @GetMapping("/charge-analysis")
//    public ChargeAnalysisDTO getChargeAnalysis() {
//        return dashboardService.getChargeAnalysis();
//    }
}