package org.example.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.crypto.password.PasswordEncoder;

import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf().disable() // 关闭 CSRF（如果不使用跨站请求保护）
                .authorizeRequests()
                .antMatchers("/advertisement/upload").hasRole("ADMIN") // 仅管理员可上传广告
                .antMatchers("/advertisement/list").permitAll() // 允许所有用户获取广告列表
                .antMatchers(HttpMethod.GET,"/advertisement/playAd/**").permitAll() // 允许所有用户观看广告
                .antMatchers("/alipay/pay").hasRole("USER") // 仅普通用户可发起支付
                .antMatchers("/alipay/notify").permitAll() // 允许所有用户访问支付回调
                .antMatchers(HttpMethod.GET,"/alipay/**").hasRole("USER") // 仅普通用户可查询支付结果
                .antMatchers("/type/add").hasRole("ADMIN") // 仅管理员可添加书籍类型
                .antMatchers("/user/register", "/user/login", "/manager/login", "/manager/register").permitAll() // 允许未登录用户访问
                .antMatchers("/manager/**", "/chapter/update/**", "/chapter/updatename/**", "/chapter/create/**").hasRole("ADMIN") // 管理员访问
                .antMatchers("/user/**").hasRole("USER") // 普通用户访问
                .antMatchers(HttpMethod.GET, "/book/**").permitAll()  // 允许所有用户访问 GET 方法
                .antMatchers(HttpMethod.POST, "/book/**").hasRole("ADMIN")  // 其他方法仅限管理员
                .antMatchers(HttpMethod.PUT, "/book/**").hasRole("ADMIN")
                .antMatchers(HttpMethod.DELETE, "/book/**").hasRole("ADMIN")
                .antMatchers(HttpMethod.GET, "/chapter/**").permitAll()  // 允许所有用户访问 GET 方法
                .antMatchers(HttpMethod.POST, "/chapter/**").hasRole("ADMIN")  // 其他方法仅限管理员
                .antMatchers(HttpMethod.PUT, "/chapter/**").hasRole("ADMIN")
                // VipPriceController 接口安全设置
                .antMatchers(HttpMethod.GET, "/{vipType}").permitAll()  // 允许所有用户访问 GET 方法
                // ReadRecordController 接口安全设置
                .antMatchers(HttpMethod.POST, "/record/save").permitAll()  // 保存阅读进度允许所有用户访问
                .antMatchers(HttpMethod.GET, "/record/**").hasRole("USER")  // 获取用户所有阅读记录需要用户角色
               // ReadController 接口安全设置
                .antMatchers(HttpMethod.GET, "/read/font/list").permitAll()  // 获取可用字体列表允许所有用户访问
                .antMatchers(HttpMethod.POST, "/read/font/add").hasRole("ADMIN")  // 上传新字体仅限管理员
                .antMatchers(HttpMethod.POST, "/read/background/**").hasRole("USER")  // 上传背景图片/视频需要用户角色
                 .antMatchers(HttpMethod.GET, "/read/background/**").hasRole("USER")  // 获取指定用户所有背景需要用户角色
              .antMatchers(HttpMethod.GET, "/read/**").hasRole("USER")  // 获取用户阅读设置需要用户角色
                .antMatchers(HttpMethod.POST, "/read/setting").hasRole("USER")  // 保存用户阅读设置需要用户角色


                .anyRequest().authenticated() // 其他请求需要登录
                .and()
                .formLogin() // 使用默认的表单登录
                .loginPage("/login") // 指定登录页面（可选）
                .defaultSuccessUrl("/user/info", true) // 登录成功后的跳转页面
                .and()
                .logout()
                .logoutUrl("/logout")
                .logoutSuccessUrl("/login?logout") // 退出后跳转
                .invalidateHttpSession(true) // 让Session失效
                .and()
                .sessionManagement()
                .maximumSessions(1) // 只允许一个 Session
                .expiredUrl("/login?expired"); // Session 过期时跳转页面

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(); // 使用 BCrypt 加密密码
    }
}

//
//@Configuration
//@EnableWebSecurity
//@EnableGlobalMethodSecurity(prePostEnabled = true) // 适用于 Spring Security 5.4 及以下
//public class SecurityConfig extends WebSecurityConfigurerAdapter {
//
//    private final UserDetailsService userDetailsService;
//    private final ContentFilter contentFilter;
//
//    public SecurityConfig(UserDetailsService userDetailsService, ContentFilter contentFilter) {
//        this.userDetailsService = userDetailsService;
//        this.contentFilter = contentFilter;
//    }
//
//    // 认证管理
//    @Override
//    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
//        auth.userDetailsService(userDetailsService)
//                .passwordEncoder(passwordEncoder());
//    }
//
//    // 安全配置
//    @Override
//    protected void configure(HttpSecurity http) throws Exception {
//        http
//                // 禁用 CSRF（REST API 不需要）
//                .csrf().disable()
//
//                // 启用 CORS
//                .cors().and()
//
//                // 访问控制
//                .authorizeRequests()
//                .antMatchers("/user/**", "/book/**", "/chapter/**").permitAll()
//                .antMatchers("/admin/**", "/chapter/admin/**").hasRole("ADMIN")
//                .anyRequest().authenticated()
//                .and()
//
//                // 添加内容安全过滤器
//                .addFilterBefore(new ContentSecurityFilter(contentFilter),
//                        UsernamePasswordAuthenticationFilter.class)
//
//                // 处理未认证和权限不足的异常
//                .exceptionHandling()
//                .authenticationEntryPoint(new CustomAuthenticationEntryPoint())
//                .accessDeniedHandler(new CustomAccessDeniedHandler())
//                .and()
//
//                // 开启 HTTP Basic 认证
//                .httpBasic();
//    }
//
//    // 提供 AuthenticationManager 供外部使用
//    @Bean
//    @Override
//    public AuthenticationManager authenticationManagerBean() throws Exception {
//        return super.authenticationManagerBean();
//    }
//
//    // 密码编码器
//    @Bean
//    public PasswordEncoder passwordEncoder() {
//        return new BCryptPasswordEncoder();
//    }
//
//    // CORS 配置
//    @Bean
//    public CorsFilter corsFilter() {
//        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
//        CorsConfiguration config = new CorsConfiguration();
//
//        config.addAllowedOrigin("*"); // 可调整为指定的可信域名
//        config.addAllowedMethod("*");
//        config.addAllowedHeader("*");
//        config.setMaxAge(3600L);
//
//        source.registerCorsConfiguration("/**", config);
//        return new CorsFilter(source);
//    }
//
//    // 内容安全过滤器
//    private static class ContentSecurityFilter extends OncePerRequestFilter {
//        private final ContentFilter contentFilter;
//
//        public ContentSecurityFilter(ContentFilter contentFilter) {
//            this.contentFilter = contentFilter;
//        }
//
//        @Override
//        protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
//                throws ServletException, IOException {
//            ContentCachingResponseWrapper responseWrapper = new ContentCachingResponseWrapper(response);
//
//            filterChain.doFilter(request, responseWrapper);
//
//            byte[] responseArray = responseWrapper.getContentAsByteArray();
//            String content = new String(responseArray, response.getCharacterEncoding());
//
//            String filteredContent = contentFilter.filter(content);
//
//            responseWrapper.resetBuffer();
//            responseWrapper.getWriter().write(filteredContent);
//            responseWrapper.copyBodyToResponse();
//        }
//    }
//
//    // 认证失败处理（未登录）
//    private static class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {
//        @Override
//        public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException)
//                throws IOException {
//            response.setContentType("application/json;charset=UTF-8");
//            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
//            response.getWriter().write("{\"error\": \"Unauthorized\", \"message\": \"" + authException.getMessage() + "\"}");
//        }
//    }
//
//    // 权限不足处理（无权限访问）
//    private static class CustomAccessDeniedHandler implements AccessDeniedHandler {
//        @Override
//        public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException)
//                throws IOException {
//            response.setContentType("application/json;charset=UTF-8");
//            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
//            response.getWriter().write("{\"error\": \"Forbidden\", \"message\": \"" + accessDeniedException.getMessage() + "\"}");
//        }
//    }
//}



//@Configuration
//@EnableWebSecurity
//public class SecurityConfig {
//
//    private final UserDetailsServiceImpl userDetailsService;
//
//    public SecurityConfig(UserDetailsServiceImpl userDetailsService) {
//        this.userDetailsService = userDetailsService;
//    }
//
//    @Bean
//    public PasswordEncoder passwordEncoder() {
//        return new BCryptPasswordEncoder();
//    }
//
//    @Bean
//    public AuthenticationManager authenticationManager() {
//        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
//        authProvider.setUserDetailsService(userDetailsService);
//        authProvider.setPasswordEncoder(passwordEncoder());
//        return new ProviderManager(Collections.singletonList(authProvider));
//    }
//
//    @Bean
//    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
//        http
//                .cors().and()
//                .csrf().ignoringAntMatchers("/book/uploadCover", "/book/parse") // 允许文件上传
//                .and()
//                .authorizeRequests()
//                .antMatchers("/login", "/swagger-ui/**", "/v3/api-docs/**").permitAll() // 公开接口
//                .antMatchers("/book/delete/**").hasRole("ADMIN") // 只有管理员能删除书籍
//                .antMatchers("/book/**").hasAnyRole("USER", "ADMIN") // 书籍管理，用户和管理员都能访问
//                .anyRequest().authenticated()
//                .and()
//                .formLogin()
//                .loginProcessingUrl("/login") // 登录处理
//                .successHandler((request, response, authentication) -> {
//                    response.setContentType("application/json");
//                    response.getWriter().write("{\"message\": \"Login successful\"}");
//                })
//                .failureHandler((request, response, exception) -> {
//                    response.setContentType("application/json");
//                    response.setStatus(401);
//                    response.getWriter().write("{\"error\": \"Unauthorized\", \"message\": \"" + exception.getMessage() + "\"}");
//                })
//                .permitAll()
//                .and()
//                .logout()
//                .logoutUrl("/logout")
//                .logoutSuccessHandler((request, response, authentication) -> {
//                    response.setContentType("application/json");
//                    response.getWriter().write("{\"message\": \"Logout successful\"}");
//                })
//                .invalidateHttpSession(true)
//                .deleteCookies("JSESSIONID")
//                .permitAll()
//                .and()
//                .exceptionHandling()
//                .authenticationEntryPoint((request, response, authException) -> {
//                    response.setContentType("application/json");
//                    response.setStatus(401);
//                    response.getWriter().write("{\"error\": \"Unauthorized\", \"message\": \"" + authException.getMessage() + "\"}");
//                });
//
//        return http.build();
//    }
//}
