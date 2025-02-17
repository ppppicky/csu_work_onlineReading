//package org.example.config;
//
//import com.alibaba.fastjson.JSON;
//import lombok.extern.slf4j.Slf4j;
//import org.example.common.BaseContext;
//import org.springframework.util.AntPathMatcher;
//
//import javax.servlet.*;
//import javax.servlet.annotation.WebFilter;
//import javax.servlet.http.HttpServletRequest;
//import javax.servlet.http.HttpServletResponse;
//import java.io.IOException;
//
//@Slf4j
//@WebFilter(filterName = "loginCheckFilter", urlPatterns = "/*")
//public class LoginCheckFilter implements Filter {
//    //路径匹配
//    public static final AntPathMatcher PATH_MATCHER = new AntPathMatcher();
//
//    @Override
//    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
//
//        //强转
//        HttpServletRequest request = (HttpServletRequest) servletRequest;
//        HttpServletResponse response = (HttpServletResponse) servletResponse;
//
//        //1.获取本次请求的URI
//        String requestURI = request.getRequestURI();
//        log.info("拦截到请求：{}",requestURI);
//
//        //定义不需要处理的请求
//        String[] urls = new String[]{
//                "/user/**",
//                "/manager/**",
//                "/backend/**",
//                "/front/**",
//                "/common/**",
//                "/forbidden_word/**"
//                //放行用户
//               // "/user/login",
//             //   "/user/sendMsg",
//        };
//
//
//        //2.判断本次请求是否需要处理
//        boolean check = check(urls, requestURI);
//
//        //3.如果不需要处理，则直接放行
//        if (check) {
//            log.info("本次请求：{}，不需要处理",requestURI);
//            filterChain.doFilter(request,response);
//            return;
//        }
//
//        //4.判断后端用户是否登录
//        if (request.getSession().getAttribute("Manager") != null) {
//            log.info("管理员已登录，id为{}",request.getSession().getAttribute("Manager"));
//
////            long id = Thread.currentThread().getId();
////            log.info("--doFilter的线程id为：{}", id);
//            Long empId=(Long)request.getSession().getAttribute("Manager");
//            BaseContext.setCurrentId(empId);
//            filterChain.doFilter(request,response);
//            return;
//        }
//        //判断前端用户是否登录
//        if(request.getSession().getAttribute("Users") != null){
//            log.info(" 用户已登录，用户id为：{}",request.getSession().getAttribute("Users"));
//            Long userId = (Long)request.getSession().getAttribute("Users");
//            BaseContext.setCurrentId(userId);
//            filterChain.doFilter(request,response);
//            return;
//        }
//
//        //5.如果未登录则返回未登录结果,通过输出流方式向客户端页面响应数据
//        log.info("用户未登录");
//       // log.info("用户id{}",request.getSession().getAttribute("employee"));
//        response.getWriter().write(JSON.toJSONString(HttpServletResponse.SC_BAD_REQUEST));
//        return;
//
//
//    }
//
//    public boolean check(String[] urls, String requestURI){
//        for (String url : urls) {
//            boolean match = PATH_MATCHER.match(url, requestURI);
//            if (match) {
//                //匹配
//                return true;
//            }
//        }
//        //不匹配
//        return false;
//    }
//}