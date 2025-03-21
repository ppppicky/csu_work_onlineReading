package org.example.config;

import lombok.extern.slf4j.Slf4j;
import org.example.filter.JwtFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@Slf4j
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http, StringRedisTemplate redisTemplate) throws Exception {
        log.info("Loading security configuration...");

        http.csrf().disable()
                .authorizeRequests()
                // 放行 Swagger 和静态资源
                .antMatchers("/doc.html", "/swagger-ui/**", "/webjars/**",
                        "/v2/api-docs", "/v3/api-docs", "/swagger-resources/**", "/favicon.ico").permitAll()
                .antMatchers("/front/**").permitAll()

                // 用户认证相关
                .antMatchers(HttpMethod.POST, "/user/login", "/user/register").permitAll()
                .antMatchers(HttpMethod.POST, "/manager/login", "/manager/register").permitAll()

                // 公开数据接口
                .antMatchers(HttpMethod.GET,
                        "/book/list", "/book/{bookId}", "/book/{bookId}/toc",  // 书籍信息
                        "/charge/book/**",  // 收费信息
                        "/vipPrice/{vipType}",  // 会员价格
                        "/advertisement/list", "/advertisement/playAd"  // 广告
                ).permitAll()

                // 支付宝回调（需要 POST 放行）
                .antMatchers(HttpMethod.POST, "/alipay/notify").permitAll()
                .antMatchers("/alipay/**").permitAll()

                // 其他所有请求需要认证
                .anyRequest().authenticated()
                .and()
                .addFilterBefore(new JwtFilter(redisTemplate), UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}