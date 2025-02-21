package org.example.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Mapper
public interface DashboardDao {

    /**
     * 统计最受欢迎书籍（按阅读次数）
     */
    @Select("SELECT b.book_id, b.book_name, COUNT(r.read_id) AS read_count " +
            "FROM read_record r JOIN book b ON r.book_id = b.book_id " +
            "GROUP BY b.book_id, b.book_name ORDER BY read_count DESC LIMIT 10")
    List<Map<String, Object>> getPopularBooks();

    /**
     * 统计用户活跃情况（每日活跃用户数）
     */
    @Select("SELECT DATE(last_read_time) AS date, COUNT(DISTINCT user_id) AS active_users " +
            "FROM read_record WHERE last_read_time >= DATE_SUB(CURDATE(), INTERVAL 7 DAY) " +
            "GROUP BY DATE(last_read_time)")
    List<Map<String, Object>> getDailyActiveUsers();

    /**
     * 统计书籍收入排行
     */
    @Select("SELECT b.book_id, b.book_name, COUNT(bb.bought_id) AS purchase_count, " +
            "SUM(cm.charge_money) AS total_revenue FROM bought_book bb " +
            "JOIN book b ON bb.book_id = b.book_id " +
            "JOIN charge_management cm ON b.book_id = cm.book_id " +
            "GROUP BY b.book_id, b.book_name ORDER BY total_revenue DESC")
    List<Map<String, Object>> getRevenueStatis();

    default Map<String, Object> getBookReadingStats() {
        Map<String, Object> result = new HashMap<>();
        result.put("popularBooks", getPopularBooks());
        result.put("dailyActiveUsers", getDailyActiveUsers());
        return result;
    }

    default Map<String, Object> getUserBehaviorStats() {
        Map<String, Object> result = new HashMap<>();
        result.put("activeUsers", getDailyActiveUsers());
        return result;
    }

    default Map<String, Object> getRevenueStats() {
        Map<String, Object> result = new HashMap<>();
        result.put("revenue", getRevenueStatis());
        return result;
    }

}
