package com.example.smart_home;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * 智能家居系统主应用类
 * 启动整个Spring Boot应用
 */
@SpringBootApplication
@EnableScheduling // 启用定时任务支持
public class SmartHomeApplication {

    /**
     * 应用程序入口方法
     * @param args 命令行参数
     */
    public static void main(String[] args) {
        SpringApplication.run(SmartHomeApplication.class, args);
    }
}
