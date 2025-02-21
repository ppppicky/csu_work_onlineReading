package org.example.dto;


import lombok.Data;
import java.math.BigDecimal;
import java.util.Map;

@Data
public class EnhancedDashboardStatsDTO {
    // 实时指标
    private Integer activeReaders;        // 5分钟内活跃读者
    private Integer concurrentUsers;      // 当前并发用户数
    private Integer adWatchingUsers;      // 正在观看广告用户数

    // 商业指标
    private BigDecimal dailyRevenue;      // 当日营收
    private BigDecimal vipRevenueRatio;   // VIP收入占比
    private Map<String, Integer> paymentMethodDistribution; // 支付方式分布

    // 内容指标
    private Integer newBooksToday;        // 今日新增书籍
    private Integer updatedChapters;      // 今日更新章节
    private Integer avgChapterLength;     // 平均章节字数

    // 用户行为
    private Double readToPurchaseRate;    // 阅读后购买转化率
    private Double adToUnlockRate;        // 广告解锁成功率
    private Integer avgReadDuration;      // 平均阅读时长（分钟）

    // 系统健康
    private Integer pendingOrders;        // 待处理订单数
    private Integer dbConnectionPool;     // 数据库连接池使用率
}