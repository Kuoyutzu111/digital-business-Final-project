package com.paper.factory.paper_system.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.password.NoOpPasswordEncoder; // 不加密
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf().disable() // 禁用 CSRF 以便簡化測試
            .authorizeHttpRequests()
                .requestMatchers("/frontend/login.html", "/css/**", "/js/**").permitAll() // 允許訪問
                .requestMatchers("/login").permitAll() // 允許訪問 /login
                .anyRequest().authenticated() // 其他請求需要認證
            .and()
            .formLogin()
                .loginPage("/frontend/login.html") // 自定義登錄頁面
                .loginProcessingUrl("/login") // 指定 /login 作為登錄提交的URL
                .defaultSuccessUrl("/frontend/main.html", true) // 登錄成功後跳轉到主頁
                .failureUrl("/frontend/login.html?error=true") // 登錄失敗時跳轉
                .permitAll()
            .and()
            .logout()
                .logoutUrl("/logout")
                .logoutSuccessUrl("/frontend/login.html") // 登出後跳轉到登錄頁面
                .permitAll();
        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        // 不加密密碼，Spring Security 默認不推薦這種方式，僅用於測試
        return NoOpPasswordEncoder.getInstance();
    }
}
