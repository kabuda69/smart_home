package com.example.smart_home.config;

import com.example.smart_home.security.JwtAuthFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import java.util.Arrays;

/**
 * 安全配置类
 * 配置Spring Security相关设置，包括认证、授权、CORS等
 */
@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SecurityConfig {
    
    @Autowired
    private JwtAuthFilter jwtAuthFilter;
    
    /**
     * 密码编码器
     * 使用明文密码（不加密）
     * @return 密码编码器
     */
    @SuppressWarnings("deprecation")
    @Bean
    public PasswordEncoder passwordEncoder() {
        // 使用明文密码（不加密）
        return NoOpPasswordEncoder.getInstance();
    }
    
    /**
     * 安全过滤器链
     * 配置HTTP安全规则，包括接口放行、授权等
     * @param http HTTP安全对象
     * @return 安全过滤器链
     * @throws Exception 异常
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.cors().and().csrf().disable()
            .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            .and()
            .authorizeRequests()
            .antMatchers("/api/auth/**", "/ws/**").permitAll()// 放行所有方法
            .antMatchers(org.springframework.http.HttpMethod.GET, "/api/share/**").permitAll() // 仅放行 GET
            .antMatchers(org.springframework.http.HttpMethod.GET, "/api/device-types", "/api/device-types/**").permitAll()// 仅放行 GET
            .antMatchers("/api/admin/**").hasRole("ADMIN")// 需 ADMIN 角色
            .anyRequest().authenticated()// 其他所有请求需认证
            .and()
            .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }
    
    /**
     * CORS配置源
     * 配置跨域资源共享
     * @return CORS配置源
     */
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOrigins(Arrays.asList("http://localhost:8081"));//允许的源
        config.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));//允许的HTTP方法
        config.setAllowedHeaders(Arrays.asList("*"));//允许的请求头
        config.setAllowCredentials(true);//是否允许发送 Cookie/凭证
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();//将配置应用到所有路径
        source.registerCorsConfiguration("/**", config);
        return source;
    }
}
