//package org.example.config;
//
//import com.alibaba.druid.filter.FilterChain;
//import org.example.util.ContentFilter;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
//import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
//import org.springframework.security.config.annotation.web.builders.HttpSecurity;
//import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
//import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
//import org.springframework.security.core.userdetails.UserDetailsService;
//import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
//import org.springframework.security.crypto.password.PasswordEncoder;
//import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
//import org.springframework.web.cors.CorsConfiguration;
//import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
//import org.springframework.web.filter.CorsFilter;
//import org.springframework.web.filter.OncePerRequestFilter;
//import org.springframework.web.util.ContentCachingResponseWrapper;
//
//import javax.servlet.ServletException;
//import javax.servlet.http.HttpServletRequest;
//import javax.servlet.http.HttpServletResponse;
//import java.io.IOException;
//
//@Configuration
//@EnableWebSecurity
//@EnableGlobalMethodSecurity(prePostEnabled = true)
//public class SecurityConfig extends WebSecurityConfigurerAdapter {
//
//    private final UserDetailsService userDetailsService;
//    private final ContentFilter contentFilter;
//
//    public SecurityConfig(UserDetailsService userDetailsService,
//                          ContentFilter contentFilter) {
//        this.userDetailsService = userDetailsService;
//        this.contentFilter = contentFilter;
//    }
//
//    @Override
//    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
//        auth.userDetailsService(userDetailsService)
//                .passwordEncoder(passwordEncoder());
//    }
//
//    @Override
//    protected void configure(HttpSecurity http) throws Exception {
//        http
//                // 禁用CSRF（REST API不需要）
//                .csrf().disable()
//                // 添加CORS过滤器
//                .cors().and()
//
//                // 授权配置
//                .authorizeRequests()
//                // 公开接口
//                .antMatchers(
//                        "/book/**",
//                        "/chapter/**"
//                ).permitAll()
//
//                // 管理员接口
//                .antMatchers(
//                        "/admin/**",
//                        "/chapter/admin/**"
//                ).hasRole("ADMIN")
//
//                // 其他请求需要认证
//                .anyRequest().authenticated()
//                .and()
//
//                // 添加内容过滤拦截器
//                .addFilterBefore(new ContentSecurityFilter(contentFilter),
//                        UsernamePasswordAuthenticationFilter.class)
//
//                // 基础认证配置
//                .httpBasic();
//    }
//
//    // CORS配置
//    @Bean
//    public CorsFilter corsFilter() {
//        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
//        CorsConfiguration config = new CorsConfiguration();
//
//        // 允许的源
//        config.addAllowedOrigin("*");
//        // 允许的方法
//        config.addAllowedMethod("*");
//        // 允许的头
//        config.addAllowedHeader("*");
//        // 预检请求缓存时间
//        config.setMaxAge(3600L);
//
//        source.registerCorsConfiguration("/**", config);
//        return new CorsFilter(source);
//    }
//
//    // 密码编码器
//    @Bean
//    public PasswordEncoder passwordEncoder() {
//        return new BCryptPasswordEncoder();
//    }
//
//    // 内容安全过滤器（示例）
//    private static class ContentSecurityFilter extends OncePerRequestFilter {
//        private final ContentFilter contentFilter;
//
//        public ContentSecurityFilter(ContentFilter contentFilter) {
//            this.contentFilter = contentFilter;
//        }
//
//        @Override
//        protected void doFilterInternal(HttpServletRequest request,
//                                        HttpServletResponse response,
//                                        FilterChain filterChain)
//                throws ServletException, IOException {
//            // 创建响应包装器进行内容过滤
//            ContentCachingResponseWrapper responseWrapper =
//                    new ContentCachingResponseWrapper(response);
//
//            filterChain.doFilter(request, responseWrapper);
//
//            // 获取响应内容并过滤
//            byte[] responseArray = responseWrapper.getContentAsByteArray();
//            String content = new String(responseArray,
//                    response.getCharacterEncoding());
//
//            String filteredContent = contentFilter.filter(content);
//
//            // 重新写入过滤后的内容
//            responseWrapper.resetBuffer();
//            responseWrapper.getWriter().write(filteredContent);
//            responseWrapper.copyBodyToResponse();
//        }
//    }
//}