package com.example.smart_home.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import javax.annotation.PostConstruct;
import java.security.Key;
import java.util.Date;

@Component
public class JwtUtil {
    @Value("${jwt.secret}")
    private String secret;
    
    @Value("${jwt.expiration}")
    private long expiration;
    
    private Key key;
    
    @PostConstruct
    public void init() {
        this.key = Keys.hmacShaKeyFor(secret.getBytes());
    }//初始化密钥

    //生成 JWT Token
    public String generateToken(Long userId, String username, String role) {
        return Jwts.builder()
                .setSubject(username)// 设置 Token 主题（Subject）为用户名
                .claim("userId", userId)// 添加自定义声明：用户ID
                .claim("role", role)// 添加自定义声明：用户角色
                .setIssuedAt(new Date())// 设置签发时间（当前时间）
                .setExpiration(new Date(System.currentTimeMillis() + expiration)) // 设置过期时间（当前时间 + 配置的有效期）
                .signWith(key, SignatureAlgorithm.HS256)// 使用 HMAC-SHA256 算法和密钥签名
                .compact();// 构建并压缩成紧凑的 JWT 字符串
    }

    //解析 Token
    public Claims parseToken(String token) {
        return Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token).getBody();//验证签名
    }
    
    public boolean validateToken(String token) {
        try {
            parseToken(token);
            return true;
        } catch (JwtException e) {
            return false;
        }
    }
    
    public String getUsername(String token) {
        return parseToken(token).getSubject();
    }// 获取用户名
    
    public Long getUserId(String token) {
        return parseToken(token).get("userId", Long.class);
    }// 获取用户ID
    
    public String getRole(String token) {
        return parseToken(token).get("role", String.class);
    }// 获取角色
}
