package com.synapse.spaced_repetition_api.config;

import com.synapse.spaced_repetition_api.filter.JwtFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
public class SecurityConfig {
    @Autowired
    private JwtFilter jwtFilter; // Filter bảo vệ cửa

    @Autowired
    private DaoAuthenticationProvider authenticationProvider; // Lấy từ ApplicationConfig

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/auth/**","/bank/**").permitAll()
                        .anyRequest().authenticated()//cần sửa
                );

        //ĐĂNG KÝ class kiểm tra Username/Password, dùng ở login, KHÔNG THỰC HIỆN LOGIC
        //Ở sau jwtFilter
        http.authenticationProvider(authenticationProvider);

        //Nếu người dùng gửi một cái Token hợp lệ, họ phải được vào luôn.
        //Chúng ta không muốn hệ thống bắt họ điền lại Username/Password nữa.
        http.addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
