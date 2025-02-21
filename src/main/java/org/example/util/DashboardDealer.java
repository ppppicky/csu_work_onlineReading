//package org.example.util;
//
//public class DashboardDealer {
//
//
//
//        private final ReadRecordRepository readRecordRepository;
//        private final BoughtBookRepository boughtBookRepository;
//        private final UsersRepository usersRepository;
//        private final ChargeManagementRepository chargeManagementRepository;
//        private final RedisTemplate<String, Object> redisTemplate;
//
//        private static final String BOOK_READING_STATS_KEY = "dashboard:bookReadingStats";
//        private static final String USER_BEHAVIOR_STATS_KEY = "dashboard:userBehaviorStats";
//        private static final String CHARGE_STATS_KEY = "dashboard:chargeStats";
//
//        /**
//         * 获取书籍阅读情况统计
//         */
//        public BookReadingStatsDTO getBookReadingStats() {
//            return (BookReadingStatsDTO) redisTemplate.opsForValue().get(BOOK_READING_STATS_KEY);
//        }
//
//        /**
//         * 获取用户行为分析数据
//         */
//        public UserBehaviorStatsDTO getUserBehaviorStats() {
//            return (UserBehaviorStatsDTO) redisTemplate.opsForValue().get(USER_BEHAVIOR_STATS_KEY);
//        }
//
//        /**
//         * 获取收费情况统计
//         */
//        public ChargeStatsDTO getChargeStats() {
//            return (ChargeStatsDTO) redisTemplate.opsForValue().get(CHARGE_STATS_KEY);
//        }
//
//        /**
//         * 定时任务 - 计算统计数据并存入 Redis
//         */
//        @Scheduled(fixedRate = 60000)  // 每 1 分钟更新一次数据
//        public void updateDashboardStats() {
//            redisTemplate.opsForValue().set(BOOK_READING_STATS_KEY, calculateBookReadingStats(), 5, TimeUnit.MINUTES);
//            redisTemplate.opsForValue().set(USER_BEHAVIOR_STATS_KEY, calculateUserBehaviorStats(), 5, TimeUnit.MINUTES);
//            redisTemplate.opsForValue().set(CHARGE_STATS_KEY, calculateChargeStats(), 5, TimeUnit.MINUTES);
//        }
//
//        private BookReadingStatsDTO calculateBookReadingStats() {
//            long totalReads = readRecordRepository.count();
//            List<BookReadCountDTO> bookReads = readRecordRepository.getTopReadBooks();
//            return new BookReadingStatsDTO(totalReads, bookReads);
//        }
//
//        private UserBehaviorStatsDTO calculateUserBehaviorStats() {
//            long activeUsers = usersRepository.countActiveUsers();
//            long newUsers = usersRepository.countNewUsersInLastMonth();
//            return new UserBehaviorStatsDTO(activeUsers, newUsers);
//        }
//
//        private ChargeStatsDTO calculateChargeStats() {
//            BigDecimal totalRevenue = chargeManagementRepository.getTotalRevenue();
//            long vipUsers = usersRepository.countVipUsers();
//            return new ChargeStatsDTO(totalRevenue, vipUsers);
//        }
//    }
//
//}
