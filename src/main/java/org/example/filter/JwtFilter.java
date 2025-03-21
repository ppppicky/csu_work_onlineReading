package org.example.filter;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import lombok.extern.slf4j.Slf4j;
import org.example.util.JwtUtil;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Collections;
import java.util.Set;
import java.util.concurrent.TimeUnit;

@Slf4j
public class JwtFilter extends OncePerRequestFilter {

    private static final String[] PUBLIC_ENDPOINTS = {
            "/doc.html", "/swagger-ui/**", "/webjars/**", "/v2/api-docs", "/v3/api-docs", "/swagger-resources/**", "/favicon.ico", "/front/**",
            "/manager/login", "/manager/register", "/user/login", "/user/register",
            "/book/list", "/book/{bookId}", "/book/{bookId}/toc",
            "/charge/book/**", "/vipPrice/{vipType}", "/advertisement/list", "/advertisement/playAd",
            "/alipay/**"
    };

    private final StringRedisTemplate redisTemplate;

    private static final String BLACKLIST_PREFIX = "JWT_BLACKLIST_";  // 黑名单前缀
    private static final String USER_TOKENS_PREFIX = "USER_TOKENS_";  // 用户 Token 列表
    private static final int MAX_DEVICES = 3; // 允许的最大设备数

    private static final int TOKEN_REQUEST_LIMIT = 100; // 允许的最大请求次数
    private static final int TOKEN_EXPIRY_SECONDS = 60; // 统计周期 (秒)

    public JwtFilter(StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        try {
            String token = extractToken(request);

            // 放行公共接口
            if (isPublicEndpoint(request)) {
                filterChain.doFilter(request, response);
                return;
            }

            if (token == null) {
                sendError(response, "Missing authorization token", HttpStatus.UNAUTHORIZED);
                return;
            }

            Claims claims = validateToken(token);
            String username = claims.getSubject();

            checkBlacklist(token);
            checkTokenFrequency(token);  // 新增：检测 token 访问频率
            checkUserTokenSet(username, token);

            setSecurityContext(username);
            filterChain.doFilter(request, response);
        } catch (JwtException | AuthenticationException e) {
            log.warn("JWT 验证失败: {}", e.getMessage());
            sendError(response, e.getMessage(), HttpStatus.UNAUTHORIZED);
        } catch (Exception e) {
            log.error("JWT 过滤器异常", e.getMessage());
            sendError(response, "Internal server error", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * 提取 Authorization 头中的 JWT Token
     */
    private String extractToken(HttpServletRequest request) {
        String header = request.getHeader("Authorization");
        return (header != null && header.startsWith("Bearer ")) ? header.substring(7) : null;
    }

    /**
     * 验证 Token 是否有效
     */
    private Claims validateToken(String token) {
        try {
            if (!JwtUtil.validateToken(token)) {
                throw new AuthenticationException("Invalid token");
            }
            return JwtUtil.parseToken(token);
        } catch (ExpiredJwtException e) {
            handleExpiredToken(token);
            throw new AuthenticationException("Token expired");
        } catch (JwtException | IllegalArgumentException e) {
            throw new AuthenticationException("Invalid token format");
        }
    }

    /**
     * 处理过期 Token：将其加入黑名单
     */
    private void handleExpiredToken(String token) {
        String username = JwtUtil.parseToken(token).getSubject();
        String tokenSetKey = USER_TOKENS_PREFIX + username;

        redisTemplate.opsForSet().remove(tokenSetKey, token);
        redisTemplate.delete("TOKEN_" + token);
        redisTemplate.opsForValue().set(BLACKLIST_PREFIX + token, "expired", 1, TimeUnit.DAYS);
        log.info("过期 Token 加入黑名单: {}", token);
    }

    /**
     * 检查 Token 是否在黑名单中
     */
    private void checkBlacklist(String token) {
        if (Boolean.TRUE.equals(redisTemplate.hasKey(BLACKLIST_PREFIX + token))) {
            throw new AuthenticationException("Token revoked");
        }
    }

    /**
     * 频率检测：检查 Token 在短时间内的访问次数，防止被滥用
     */
    private void checkTokenFrequency(String token) {
        String tokenKey = "TOKEN_REQUESTS_" + token;

        // 递增请求计数
        Long requestCount = redisTemplate.opsForValue().increment(tokenKey);

        if (requestCount == 1) {
            // 第一次请求时设置过期时间
            redisTemplate.expire(tokenKey, TOKEN_EXPIRY_SECONDS, TimeUnit.SECONDS);
        }

        if (requestCount != null && requestCount > TOKEN_REQUEST_LIMIT) {
            log.warn("Token 访问过于频繁，疑似泄露，加入黑名单: {}", token);

            // 加入黑名单并删除计数
            redisTemplate.opsForValue().set(BLACKLIST_PREFIX + token, "leaked", 1, TimeUnit.DAYS);
            redisTemplate.delete(tokenKey);

            throw new AuthenticationException("Token 被封禁，访问过于频繁");
        }
    }

    /**
     * 检查用户 Token 是否在允许的 Token 列表中
     */
    private void checkUserTokenSet(String username, String token) {
        String tokenSetKey = USER_TOKENS_PREFIX + username;
        Set<String> userTokens = redisTemplate.opsForSet().members(tokenSetKey);

        if (userTokens == null || !userTokens.contains(token)) {
            throw new AuthenticationException("Token 已失效或被移除");
        }
    }

    /**
     * 设置 Spring Security 上下文
     */
    private void setSecurityContext(String username) {
        UserDetails userDetails = new org.springframework.security.core.userdetails.User(username, "", Collections.emptyList());
        UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

    /**
     * 判断是否是公共接口（无需 Token 验证）
     */
    private boolean isPublicEndpoint(HttpServletRequest request) {
        String uri = request.getRequestURI();
        for (String pattern : PUBLIC_ENDPOINTS) {
            if (uri.startsWith(pattern.replace("/**", ""))) {
                return true;
            }
        }
        return false;
    }

    /**
     * 发送错误响应
     */
    private void sendError(HttpServletResponse response, String message, HttpStatus status) throws IOException {
        response.setStatus(status.value());
        response.setContentType("application/json");
        response.getWriter().write(String.format("{\"error\": \"%s\"}", message));
        response.getWriter().flush();
    }

    private static class AuthenticationException extends RuntimeException {
        public AuthenticationException(String message) {
            super(message);
        }
    }
}
