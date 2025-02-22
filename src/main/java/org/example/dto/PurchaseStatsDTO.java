package org.example.dto;



import lombok.Data;
import java.math.BigDecimal;

@Data
public class PurchaseStatsDTO {
    private Long totalPurchases;          // 总购买量
    private BigDecimal totalRevenue;      // 总收入
    private String mostPurchasedBook;     // 最畅销书籍名称
    private Integer mostPurchasedCount;   // 最畅销书籍购买次数

    public PurchaseStatsDTO(Long totalPurchases, BigDecimal totalRevenue, String mostPurchasedBookName, int mostPurchasedCount) {
   this.setTotalPurchases(totalPurchases);
   this.setMostPurchasedCount(mostPurchasedCount);
   this.setMostPurchasedBook(mostPurchasedBookName);
   this.setTotalRevenue(totalRevenue);
    }
}
