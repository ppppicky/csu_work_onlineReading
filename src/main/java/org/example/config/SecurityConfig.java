//package org.example.config;
//
//import lombok.extern.slf4j.Slf4j;
//import org.example.filterFromDB.JwtFilter;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.data.redis.core.StringRedisTemplate;
//import org.springframework.http.HttpMethod;
//import org.springframework.security.config.annotation.web.builders.HttpSecurity;
//import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
//import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
//import org.springframework.security.crypto.password.PasswordEncoder;
//import org.springframework.security.web.SecurityFilterChain;
//import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
//
//@Configuration
//@EnableWebSecurity
//@Slf4j
//public class SecurityConfig {
//
//    @Bean
//    public SecurityFilterChain securityFilterChain(HttpSecurity http, StringRedisTemplate redisTemplate) throws Exception {
//        log.info("Security config loaded");
//        http
//                .csrf().disable()
//                .authorizeRequests()
//                .antMatchers("/doc.html/**", "/swagger-ui/**", "/swagger-resources", "/swagger-resources/**",
//                        "/webjars/**", "/v2/api-docs", "/v3/api-docs", "/favicon.ico").permitAll()
//                .antMatchers("/front/**", "/index.html").permitAll()
//                .antMatchers("/manager/login", "/manager/register", "/user/login", "/user/register").permitAll()
//                .antMatchers("/advertisement/list", "/advertisement/playAd", "/alipay/notify").permitAll()
//                .antMatchers(HttpMethod.GET, "/book/list", "/book/{bookId}", "/book/{bookId}/toc").permitAll()
//                .anyRequest().authenticated()
//                .and()
//                .addFilterBefore(new JwtFilter(redisTemplate), UsernamePasswordAuthenticationFilter.class);
//
//        return http.build();
//    }
//
//    @Bean
//    public PasswordEncoder passwordEncoder() {
//        return new BCryptPasswordEncoder();
//    }
//}
