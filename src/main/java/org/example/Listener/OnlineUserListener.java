package org.example.Listener;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.util.Set;
import java.util.concurrent.TimeUnit;

@Slf4j
@Component
public class OnlineUserListener {

    private static final String ONLINE_USERS_KEY = "ONLINE_USERS";

    @Autowired
    private StringRedisTemplate redisTemplate;

    /**
     * 记录用户上线状态
     */
    public void userLoggedIn(String username) {
        redisTemplate.opsForSet().add(ONLINE_USERS_KEY, username);
        redisTemplate.expire(ONLINE_USERS_KEY, 1, TimeUnit.DAYS);
        log.info("用户 {} 已上线", username);
    }

    /**
     * 记录用户下线状态
     */
    public void userLoggedOut(String username) {
        Long removed = redisTemplate.opsForSet().remove(ONLINE_USERS_KEY, username);
        if (removed != null && removed > 0) {
            log.info("用户 {} 已从在线列表移除", username);
        } else {
            log.warn("尝试移除用户 {} 但其不在在线列表中", username);
        }
    }

    /**
     * 获取当前在线用户数
     */
    public int getOnlineUsers() {
        Set<String> onlineUsers = redisTemplate.opsForSet().members(ONLINE_USERS_KEY);
        return onlineUsers != null ? onlineUsers.size() : 0;
    }
}
