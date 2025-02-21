package org.example.service.Impl;

import lombok.extern.slf4j.Slf4j;
import org.example.dto.*;
import org.example.entity.Book;
import org.example.entity.BoughtBook;
import org.example.entity.ChargeManagement;
import org.example.mapper.DashboardDao;
import org.example.repository.*;
import org.example.service.DashboardService;
import org.example.util.GlobalException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
public class DashboardSImpl implements DashboardService {

    // DashboardService.java 新增方法
    @Autowired
    private BoughtBookRepository boughtBookRepository;

    @Autowired
    BookRepository bookRepository;
    @Autowired
    private ChargeRepository chargeManagementRepository;
    @Autowired
    private  ReadRepository readRecordRepository;

    @Autowired
    private  UserRepository usersRepository;

    @Autowired
    private DashboardDao dashboardDao;

    @Autowired
    @Qualifier( "objectRedisTemplate")
    private  RedisTemplate<String, Object> redisTemplate;

    private static final String BOOK_READING_STATS_KEY = "dashboard:bookReadingStats";
    private static final String USER_BEHAVIOR_STATS_KEY = "dashboard:userBehaviorStats";
    private static final String CHARGE_STATS_KEY = "dashboard:chargeStats";

    /**
     * 获取书籍阅读情况统计
     */
    @Override
    public BookReadingStatsDTO getBookReadingStats() {
        return null;
 //       return (BookReadingStatsDTO) redisTemplate.opsForValue().get(BOOK_READING_STATS_KEY);
    }

    /**
     * 获取用户行为分析数据
     */
    @Override
    public UserBehaviorStatsDTO getUserBehaviorStats() {
       // return (UserBehaviorStatsDTO) redisTemplate.opsForValue().get(USER_BEHAVIOR_STATS_KEY);
        return calculateUserBehaviorStats();
    }

    /**
     * 获取收费情况统计
     */
    @Override
    public ChargeStatsDTO getChargeStats() {
     //   return (ChargeStatsDTO) redisTemplate.opsForValue().get(CHARGE_STATS_KEY);
        return calculateChargeStats();
    }

    /**
     * 定时任务 - 计算统计数据并存入 Redis
     */
    @Override // 计算书籍阅读统计并存储到 Redis
    @Scheduled(fixedRate = 60000)  // 每 1 分钟更新一次数据
    public void updateDashboardStats() {
   //     redisTemplate.opsForValue().set(BOOK_READING_STATS_KEY, calculateBookReadingStats(), 5, TimeUnit.MINUTES);
//        redisTemplate.opsForValue().set(USER_BEHAVIOR_STATS_KEY, calculateUserBehaviorStats(), 5, TimeUnit.MINUTES);
//        redisTemplate.opsForValue().set(CHARGE_STATS_KEY, calculateChargeStats(), 5, TimeUnit.MINUTES);
    }

//    private BookReadingStatsDTO calculateBookReadingStats() {
//        long totalReads = readRecordRepository.count();
//        List<BookReadCountDTO> bookReads = readRecordRepository.getTopReadBooks();
//        return new BookReadingStatsDTO(totalReads, bookReads);
//    }

    private UserBehaviorStatsDTO calculateUserBehaviorStats() {
        long activeUsers = usersRepository.countActiveUsers(); // 统计活跃用户数量
        long newUsers = usersRepository.countNewUsersInLastMonth();  // 统计最近一个月的新用户数量
        return new UserBehaviorStatsDTO(activeUsers, newUsers);
    }

    private ChargeStatsDTO calculateChargeStats() {
        BigDecimal totalRevenue = chargeManagementRepository.getTotalRevenue(); // 计算总收入
        long vipUsers = usersRepository.countVipUsers();   // 统计 VIP 用户数量
        return new ChargeStatsDTO(totalRevenue, vipUsers);
    }
    @Override
    public PurchaseStatsDTO getPurchaseStats() {
        // 统计总购买量和总收入
        List<BoughtBook> allPurchases = boughtBookRepository.findAll();
        log.info("aaaaaaaaaa"+allPurchases.toString());
        Long totalPurchases = (long) allPurchases.size();
        BigDecimal totalRevenue = allPurchases.stream()
                .map(purchase -> {
                //    Book book =  purchase.getBookId();

                    ChargeManagement charge = chargeManagementRepository.findByBook(bookRepository.findById(purchase.getBookId()).get())
                            .orElseThrow(()-> new GlobalException.BookNotFoundException("book not existed"));
                    return (charge != null && charge.getChargeMoney() != null)
                            ? charge.getChargeMoney() : BigDecimal.ZERO;
                })
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // 查询最畅销书籍
        Map<Integer, Long> bookPurchaseCount = allPurchases.stream()
                .collect(Collectors.groupingBy(BoughtBook::getBookId, Collectors.counting()));
        Map.Entry<Integer, Long> mostPurchased = bookPurchaseCount.entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .orElse(null);

        String mostPurchasedBookName = (mostPurchased != null) ?
                bookRepository.findById(mostPurchased.getKey()).map(Book::getBookName).orElse("未知书籍") : "无数据";

        return new PurchaseStatsDTO(totalPurchases, totalRevenue, mostPurchasedBookName, mostPurchased.getValue().intValue());
    }
    public Map<String, Object> getReadingStats() {
        return dashboardDao.getBookReadingStats();
    }

    /**
     * 获取用户行为分析数据
     */
    public Map<String, Object> getBehaviorStats() {
        return dashboardDao.getUserBehaviorStats();
    }

    /**
     * 获取收费情况统计数据
     */
    public Map<String, Object> getRevenueStats() {
        return dashboardDao.getRevenueStats();
    }
//    public ChargeAnalysisDTO getChargeAnalysis() {
//        // 统计收费书籍占比
//        List<Book> allBooks = bookRepository.findAll();
//        long totalBooks = allBooks.size();
//        long chargeBooks = allBooks.stream()
//                .filter(book -> book.getIsCharge() == 1)
//                .count();
//        double chargeRatio = (totalBooks > 0) ? (chargeBooks * 100.0 / totalBooks) : 0.0;
//
//        // 平均收费金额
//        List<ChargeManagement> charges = chargeManagementRepository.findAll();
//        BigDecimal totalCharge = charges.stream()
//                .map(ChargeManagement::getChargeMoney)
//                .filter(Objects::nonNull)
//                .reduce(BigDecimal.ZERO, BigDecimal::add);
//        BigDecimal avgCharge = charges.isEmpty() ? BigDecimal.ZERO :
//                totalCharge.divide(BigDecimal.valueOf(charges.size()), 2, RoundingMode.HALF_UP);
//
//        // VIP免费书籍数量
//        Integer vipFree = chargeManagementRepository.countByIsVipFree((byte) 1);
//
//        return new ChargeAnalysisDTO(chargeRatio, avgCharge, vipFree, 0); // 免费章节使用需额外实现
    }