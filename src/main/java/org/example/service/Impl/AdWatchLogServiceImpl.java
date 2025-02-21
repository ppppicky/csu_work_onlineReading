package org.example.service.Impl;

import org.example.entity.AdWatchLog;
import org.example.repository.AdWatchLogRepository;
import org.example.service.AdWatchLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AdWatchLogServiceImpl implements AdWatchLogService {

    @Autowired
    private AdWatchLogRepository adWatchLogRepository;

    /**
     * 记录用户观看广告日志
     */
    @Override
    public void save(AdWatchLog log) {
        adWatchLogRepository.save(log);
    }
}
