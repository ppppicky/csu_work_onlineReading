package org.example.service;

import org.example.dto.BookReadingStatsDTO;
import org.example.dto.ChargeStatsDTO;
import org.example.dto.PurchaseStatsDTO;
import org.example.dto.UserBehaviorStatsDTO;

import java.util.Map;

public interface DashboardService {
    PurchaseStatsDTO getPurchaseStats();

    void updateDashboardStats();

    ChargeStatsDTO getChargeStats();

    BookReadingStatsDTO getBookReadingStats();

    UserBehaviorStatsDTO getUserBehaviorStats();
    //  public ChargeAnalysisDTO getChargeAnalysis()

    /**
     * 获取书籍阅读统计数据
     */
    public Map<String, Object> getReadingStats();

    /**
     * 获取用户行为分析数据
     */
    public Map<String, Object> getBehaviorStats();

    /**
     * 获取收费情况统计数据
     */
    public Map<String, Object> getRevenueStats();
}


