package org.example.task;

import org.example.service.RankService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
class RankingUpdateTask {
    @Autowired
    private RankService rankService;

    @Scheduled(cron = "0 0 0 * * ?")
    public void updateDailyRanking() {
        rankService.refreshDailyRanking();
    }

    @Scheduled(cron = "0 0 0 * * MON")
    public void updateWeeklyRanking() {
        rankService.refreshWeeklyRanking();
    }

    @Scheduled(cron = "0 0 0 1 * ?")
    public void updateMonthlyRanking() {
        rankService.refreshMonthlyRanking();
    }

    @Scheduled(cron = "0 0 0 * * ?")
    public void updateTotalRanking() {
        rankService.refreshTotalRanking();
    }
}

