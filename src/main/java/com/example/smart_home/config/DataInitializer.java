package com.example.smart_home.config;

import com.example.smart_home.entity.DeviceType;
import com.example.smart_home.entity.User;
import com.example.smart_home.repository.DeviceTypeRepository;
import com.example.smart_home.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements CommandLineRunner {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private DeviceTypeRepository deviceTypeRepository;
    
    @Override
    public void run(String... args) {
        // 创建管理员账户（明文密码）
        if (!userRepository.existsByUsername("admin")) {
            User admin = new User();
            admin.setUsername("admin");
            admin.setPassword("admin123");  // 明文密码
            admin.setRole("ADMIN");
            admin.setEmail("admin@smarthome.com");
            userRepository.save(admin);
        }
        
        // 创建默认设备类型
        createDeviceTypeIfNotExists("智能灯", "{\"brightness\": {\"min\": 0, \"max\": 100}}", "可调节亮度的智能灯", "mdi-lightbulb");
        createDeviceTypeIfNotExists("空调", "{\"temperature\": {\"min\": 16, \"max\": 30}}", "智能空调", "mdi-air-conditioner");
        createDeviceTypeIfNotExists("温度传感器", "{\"unit\": \"celsius\"}", "温度监测传感器", "mdi-thermometer");
        createDeviceTypeIfNotExists("湿度传感器", "{\"unit\": \"percent\"}", "湿度监测传感器", "mdi-water-percent");
        createDeviceTypeIfNotExists("智能插座", "{}", "可远程控制的智能插座", "mdi-power-socket");
        createDeviceTypeIfNotExists("门窗传感器", "{}", "门窗开关状态传感器", "mdi-door");
        createDeviceTypeIfNotExists("烟雾报警器", "{\"threshold\": {\"min\": 0, \"max\": 100}}", "烟雾浓度检测报警器", "mdi-smoke-detector");
        createDeviceTypeIfNotExists("智能窗帘", "{\"position\": {\"min\": 0, \"max\": 100}}", "电动智能窗帘", "mdi-blinds");
    }
    
    private void createDeviceTypeIfNotExists(String name, String paramTemplate, String description, String icon) {
        if (!deviceTypeRepository.existsByName(name)) {
            DeviceType type = new DeviceType();
            type.setName(name);
            type.setParamTemplate(paramTemplate);
            type.setDescription(description);
            type.setIcon(icon);
            deviceTypeRepository.save(type);
        }
    }
}
