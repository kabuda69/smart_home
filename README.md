# 智能家居模拟控制系统

基于 Spring Boot + Vue 的智能家居控制系统。

## 技术栈

- 后端: Spring Boot 2.7, Spring Security, Spring WebSocket, JPA, MySQL
- 前端: Vue, Vuetify, Chart.js, SockJS/STOMP
- 认证: JWT

## 功能模块

### 1. 用户认证
- 用户注册/登录
- JWT Token认证
- 角色权限控制（USER/ADMIN）

### 2. 设备管理
- 绑定设备（选择设备类型）
- 设备状态查看（在线/离线）
- 设备控制（开关、设置数值）
- 阈值设置（最小/最大阈值）
- 设备删除（级联删除关联数据）

### 3. 实时监控
- WebSocket推送设备状态更新
- 实时警报通知弹窗
- 设备状态变化即时同步
- 动态告警弹窗（居中显示，带动画效果和提示音）
- 点击"我知道了"后弹窗消失

### 4. 历史数据
- 查看设备状态历史记录
- 分页查询
- 时间范围查询

### 5. 警报通知
- 阈值超限自动触发警报
- 警报列表查看
- 标记已读/全部已读
- 根据通知偏好发送（弹窗/日志）

### 6. 场景模式
- 创建场景（回家模式/离家模式等）
- 配置场景动作（开启/关闭/设置数值）
- 一键激活场景
- 场景编辑/删除

### 7. 数据统计
- 设备总数/在线数统计
- 设备类型分布饼图
- 每月警报趋势柱状图
- 设备命令执行频率图
- 数据统计使用真实历史数据
- 在线设备数量实时同步（与控制面板保持一致）
- 告警类型分布饼图按设备名称统计

### 8. 设备分享
- 生成临时分享链接（24小时有效）
- 无需登录查看设备状态快照
- 自动清理过期快照

### 9. 用户反馈
- 提交反馈/建议
- 查看反馈状态
- 管理员回复

### 10. 通知偏好
- 弹窗通知开关
- 日志记录开关
- 个人操作日志查看
- 操作日志按时间段查询（系统设置内）

### 11. 管理后台（仅管理员）
- 用户管理（启用/禁用）
- 设备类型管理（增删改）
- 用户反馈管理（查看/回复）
- 系统日志查看

## 数据库设计 (12张表)

1. users - 用户表
2. devices - 设备表
3. device_types - 设备类型表
4. commands - 控制指令表
5. status_history - 状态历史表
6. alerts - 警报表
7. logs - 日志表
8. notification_preferences - 通知偏好表
9. shared_snapshots - 共享快照表
10. feedbacks - 用户反馈表
11. scenes - 场景表
12. scene_actions - 场景动作表

## 数据库视图 (3个)

- v_user_device_stats: 用户设备统计（总数、在线数）
- v_monthly_alert_stats: 每月警报统计
- v_device_command_frequency: 设备命令执行频率（30天内）

## 启动方式

### 1. 数据库配置

创建MySQL数据库，修改 `src/main/resources/application.properties`:

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/smart_home
spring.datasource.username=root
spring.datasource.password=你的密码
```

### 2. 启动后端

```bash
# 使用Maven
mvn spring-boot:run

# 或使用Maven Wrapper
./mvnw spring-boot:run
```

后端启动后会自动创建表结构和初始数据（管理员账号、设备类型）。

### 3. 启动前端

```bash
cd frontend
npm install
npm run serve
```

### 4. 访问系统

- 前端: http://localhost:8081
- 后端API: http://localhost:8080
- 默认管理员: admin / admin123

## 主要API

### 认证
- POST /api/auth/register - 用户注册
- POST /api/auth/login - 用户登录

### 设备
- GET /api/devices - 获取我的设备
- POST /api/devices - 绑定设备
- PUT /api/devices/{id} - 更新设备
- DELETE /api/devices/{id} - 删除设备
- POST /api/devices/command - 执行设备命令
- GET /api/devices/{id}/history - 获取设备历史

### 场景
- GET /api/scenes - 获取场景列表
- POST /api/scenes - 创建场景
- PUT /api/scenes/{id} - 更新场景
- DELETE /api/scenes/{id} - 删除场景
- POST /api/scenes/{id}/activate - 激活场景

### 警报
- GET /api/alerts - 获取警报列表
- GET /api/alerts/unread - 获取未读警报
- PUT /api/alerts/{id}/read - 标记已读
- PUT /api/alerts/read-all - 全部已读

### 统计
- GET /api/statistics - 获取统计数据

### 分享
- POST /api/share - 创建分享链接
- GET /api/share/{uuid} - 获取分享快照

### 反馈
- GET /api/feedbacks - 获取我的反馈
- POST /api/feedbacks - 提交反馈

### 设置
- GET /api/user/notification-preferences - 获取通知偏好
- PUT /api/user/notification-preferences - 更新通知偏好
- GET /api/logs - 获取我的操作日志

### 管理员
- GET /api/admin/users - 获取所有用户
- PUT /api/admin/users/{id}/status - 更新用户状态
- GET /api/admin/device-types - 获取设备类型
- POST /api/admin/device-types - 创建设备类型
- PUT /api/admin/device-types/{id} - 更新设备类型
- DELETE /api/admin/device-types/{id} - 删除设备类型
- GET /api/admin/feedbacks - 获取所有反馈
- PUT /api/admin/feedbacks/{id}/reply - 回复反馈
- GET /api/admin/logs - 获取所有日志

## 默认设备类型

- 智能灯
- 空调
- 温度传感器
- 湿度传感器
- 智能插座
- 门窗传感器
- 烟雾报警器
- 智能窗帘
