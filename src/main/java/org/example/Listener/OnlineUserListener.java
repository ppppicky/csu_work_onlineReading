package org.example.Listener;
import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;
import org.springframework.stereotype.Component;
import java.util.concurrent.atomic.AtomicInteger;

@Component
public class OnlineUserListener implements HttpSessionListener {

    private static final AtomicInteger onlineUsers = new AtomicInteger(0);

    @Override
    public void sessionCreated(HttpSessionEvent se) {
        // 当会话创建时，在线用户数+1
        onlineUsers.incrementAndGet();
    }

    @Override
    public void sessionDestroyed(HttpSessionEvent se) {
        // 当会话销毁时，在线用户数-1
        onlineUsers.decrementAndGet();
    }

    // 获取当前在线用户数
    public static int getOnlineUsers() {
        return onlineUsers.get();
    }
}
