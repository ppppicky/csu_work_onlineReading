//package org.example.filterFromDB;
//
//import io.jsonwebtoken.Claims;
//import org.example.util.JwtUtil;
//import org.springframework.data.redis.core.StringRedisTemplate;
//import org.springframework.http.HttpStatus;
//import org.springframework.web.filterFromDB.OncePerRequestFilter;
//
//import javax.servlet.FilterChain;
//import javax.servlet.ServletException;
//import javax.servlet.http.HttpServletRequest;
//import javax.servlet.http.HttpServletResponse;
//import java.io.IOException;
//import java.util.Arrays;
//import java.util.List;
//
//public class JwtFilter extends OncePerRequestFilter {
//
//    private final StringRedisTemplate redisTemplate;
//
//    //通过构造函数注入 RedisTemplate
//    public JwtFilter(StringRedisTemplate redisTemplate) {
//        this.redisTemplate = redisTemplate;
//    }
//
//    // 不需要 Token 的接口（公共接口）
//    private static final List<String> PUBLIC_URLS = Arrays.asList(
////            "/doc.html", "/swagger-ui/", "/swagger-resources/", "/webjars/",
////            "/v2/api-docs", "/v3/api-docs", "/favicon.ico",
//            "/front/", "/manager/login", "/manager/register",
//            "/user/login", "/user/register",
//            "/advertisement/list", "/advertisement/playAd",
//            "/alipay/notify", "/book/list", "/book/{bookId}", "/book/{bookId}/toc"
//    );
//
//    @Override
//    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
//            throws ServletException, IOException {
//
//        String path = request.getRequestURI();
//        String token = request.getHeader("Authorization");
//
//        // 允许访问的公共 URL
//        if (PUBLIC_URLS.stream().anyMatch(path::startsWith)) {
//            filterChain.doFilter(request, response);
//            return;
//        }
//
//        //  用户访问 `index.html`，如果已经登录，则跳转到主页
//        if (path.equals("/front/index.html") && token != null && token.startsWith("Bearer ")) {
//            response.sendRedirect("/front/home.html"); // 主页
//            return;
//        }
//
//        //  检查 Token 是否存在
//        if (token == null || !token.startsWith("Bearer ")) {
//            // API 请求时，返回 401，而不是重定向
//            if (path.startsWith("/api")) {
//                response.setStatus(HttpStatus.UNAUTHORIZED.value());
//                response.getWriter().write("{\"error\": \"Token missing or invalid\"}");
//            } else {
//                response.sendRedirect("/front/index.html");
//            }
//            return;
//        }
//
//        // 解析 Token
//        token = token.substring(7);
//        if (!JwtUtil.validateToken(token)) {
//            //  API 请求时，返回 401，而不是重定向
//            if (path.startsWith("/api")) {
//                response.setStatus(HttpStatus.UNAUTHORIZED.value());
//                response.getWriter().write("{\"error\": \"Invalid token\"}");
//            } else {
//                response.sendRedirect("/front/index.html");
//            }
//            return;
//        }
//
//        //  解析 Token 并存入 Request
//        Claims claims = JwtUtil.parseToken(token);
//        request.setAttribute("user", claims.getSubject());
//
//        filterChain.doFilter(request, response);
//    }
//
//}