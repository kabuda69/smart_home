-- =====================================================
-- 智能家居系统初始化数据
-- 密码使用明文存储
-- =====================================================

-- 1. 用户数据（3个用户）
-- admin/admin123 (管理员), user1/123456, user2/123456
INSERT IGNORE INTO users (id, username, password, role, email, enabled, created_at) VALUES
(1, 'admin', 'admin123', 'ADMIN', 'admin@smarthome.com', true, NOW()),
(2, 'user1', '123456', 'USER', 'user1@example.com', true, NOW()),
(3, 'user2', '123456', 'USER', 'user2@example.com', true, NOW());

-- 2. 设备类型数据（8种类型）
INSERT IGNORE INTO device_types (id, name, param_template, description, icon) VALUES
(1, '智能灯', '{"brightness": {"min": 0, "max": 100}}', '可调节亮度的智能灯', 'mdi-lightbulb'),
(2, '空调', '{"temperature": {"min": 16, "max": 30}}', '智能空调', 'mdi-air-conditioner'),
(3, '温度传感器', '{"unit": "celsius"}', '温度监测传感器', 'mdi-thermometer'),
(4, '湿度传感器', '{"unit": "percent"}', '湿度监测传感器', 'mdi-water-percent'),
(5, '智能插座', '{}', '可远程控制的智能插座', 'mdi-power-socket'),
(6, '门窗传感器', '{}', '门窗开关状态传感器', 'mdi-door'),
(7, '烟雾报警器', '{"threshold": {"min": 0, "max": 100}}', '烟雾浓度检测报警器', 'mdi-smoke-detector'),
(8, '智能窗帘', '{"position": {"min": 0, "max": 100}}', '电动智能窗帘', 'mdi-blinds');

-- 3. 设备数据
-- admin(id=1)的设备：6个
INSERT IGNORE INTO devices (id, name, type_id, user_id, status, power_state, current_value, threshold_min, threshold_max, created_at, updated_at) VALUES
(1, '办公室灯', 1, 1, 'online', true, 90, 0, 100, NOW(), NOW()),
(2, '办公室空调', 2, 1, 'online', true, 24, 18, 28, NOW(), NOW()),
(3, '办公室温度传感器', 3, 1, 'online', true, 24, 15, 35, NOW(), NOW()),
(4, '会议室灯', 1, 1, 'online', false, 0, 0, 100, NOW(), NOW()),
(5, '门禁传感器', 6, 1, 'online', true, 0, NULL, NULL, NOW(), NOW()),
(6, '烟雾报警器', 7, 1, 'online', true, 3, 0, 50, NOW(), NOW());

-- user1(id=2)的设备：6个
INSERT IGNORE INTO devices (id, name, type_id, user_id, status, power_state, current_value, threshold_min, threshold_max, created_at, updated_at) VALUES
(7, '客厅灯', 1, 2, 'online', true, 80, 0, 100, NOW(), NOW()),
(8, '卧室灯', 1, 2, 'online', true, 50, 0, 100, NOW(), NOW()),
(9, '客厅空调', 2, 2, 'online', true, 26, 18, 28, NOW(), NOW()),
(10, '客厅温度传感器', 3, 2, 'online', true, 25, 15, 35, NOW(), NOW()),
(11, '厨房烟雾报警器', 7, 2, 'online', true, 5, 0, 50, NOW(), NOW()),
(12, '卧室窗帘', 8, 2, 'online', true, 100, 0, 100, NOW(), NOW());

-- user2(id=3)的设备：3个
INSERT IGNORE INTO devices (id, name, type_id, user_id, status, power_state, current_value, threshold_min, threshold_max, created_at, updated_at) VALUES
(13, '书房灯', 1, 3, 'online', true, 70, 0, 100, NOW(), NOW()),
(14, '书房空调', 2, 3, 'offline', false, 24, 18, 28, NOW(), NOW()),
(15, '书房温度传感器', 3, 3, 'online', true, 23, 15, 35, NOW(), NOW());

-- 4. 通知偏好数据（每个用户2条：POPUP和LOG）
INSERT IGNORE INTO notification_preferences (id, user_id, notification_type, enabled) VALUES
(1, 1, 'POPUP', true),
(2, 1, 'LOG', true),
(3, 2, 'POPUP', true),
(4, 2, 'LOG', true),
(5, 3, 'POPUP', true),
(6, 3, 'LOG', false);

-- 5. 场景数据
-- admin的场景：工作模式、会议模式
INSERT IGNORE INTO scenes (id, user_id, name, description, is_active, created_at) VALUES
(1, 1, '工作模式', '开启办公室灯光和空调', true, NOW()),
(2, 1, '会议模式', '开启会议室灯光', false, NOW()),
(3, 1, '离开模式', '关闭所有设备', false, NOW());

-- user1的场景：回家模式、离家模式、睡眠模式
INSERT IGNORE INTO scenes (id, user_id, name, description, is_active, created_at) VALUES
(4, 2, '回家模式', '回家时自动开启灯光和空调', false, NOW()),
(5, 2, '离家模式', '离家时关闭所有设备', false, NOW()),
(6, 2, '睡眠模式', '睡觉时调暗灯光关闭窗帘', false, NOW());

-- user2的场景
INSERT IGNORE INTO scenes (id, user_id, name, description, is_active, created_at) VALUES
(7, 3, '学习模式', '工作时开启书房灯和空调', false, NOW());

-- 6. 场景动作数据
-- admin工作模式：开办公室灯、开空调
INSERT IGNORE INTO scene_actions (id, scene_id, device_id, action_type, action_value, sort_order) VALUES
(1, 1, 1, 'POWER_ON', NULL, 0),
(2, 1, 1, 'SET_VALUE', '90', 1),
(3, 1, 2, 'POWER_ON', NULL, 2),
(4, 1, 2, 'SET_VALUE', '24', 3);

-- admin会议模式：开会议室灯
INSERT IGNORE INTO scene_actions (id, scene_id, device_id, action_type, action_value, sort_order) VALUES
(5, 2, 4, 'POWER_ON', NULL, 0),
(6, 2, 4, 'SET_VALUE', '100', 1);

-- admin离开模式：关闭所有
INSERT IGNORE INTO scene_actions (id, scene_id, device_id, action_type, action_value, sort_order) VALUES
(7, 3, 1, 'POWER_OFF', NULL, 0),
(8, 3, 2, 'POWER_OFF', NULL, 1),
(9, 3, 4, 'POWER_OFF', NULL, 2);

-- user1回家模式：开客厅灯(亮度80)、开空调(26度)
INSERT IGNORE INTO scene_actions (id, scene_id, device_id, action_type, action_value, sort_order) VALUES
(10, 4, 7, 'POWER_ON', NULL, 0),
(11, 4, 7, 'SET_VALUE', '80', 1),
(12, 4, 9, 'POWER_ON', NULL, 2),
(13, 4, 9, 'SET_VALUE', '26', 3);

-- user1离家模式：关闭客厅灯、卧室灯、空调
INSERT IGNORE INTO scene_actions (id, scene_id, device_id, action_type, action_value, sort_order) VALUES
(14, 5, 7, 'POWER_OFF', NULL, 0),
(15, 5, 8, 'POWER_OFF', NULL, 1),
(16, 5, 9, 'POWER_OFF', NULL, 2);

-- user1睡眠模式：关客厅灯、卧室灯调暗、关窗帘
INSERT IGNORE INTO scene_actions (id, scene_id, device_id, action_type, action_value, sort_order) VALUES
(17, 6, 7, 'POWER_OFF', NULL, 0),
(18, 6, 8, 'SET_VALUE', '20', 1),
(19, 6, 12, 'SET_VALUE', '0', 2);

-- user2学习模式：开书房灯、开空调
INSERT IGNORE INTO scene_actions (id, scene_id, device_id, action_type, action_value, sort_order) VALUES
(20, 7, 13, 'POWER_ON', NULL, 0),
(21, 7, 13, 'SET_VALUE', '70', 1),
(22, 7, 14, 'POWER_ON', NULL, 2);

-- 7. 控制指令历史数据
INSERT IGNORE INTO commands (id, device_id, user_id, command_type, command_value, executed_at, status) VALUES
(1, 1, 1, 'POWER_ON', NULL, DATE_SUB(NOW(), INTERVAL 2 DAY), 'SUCCESS'),
(2, 1, 1, 'SET_VALUE', '90', DATE_SUB(NOW(), INTERVAL 2 DAY), 'SUCCESS'),
(3, 2, 1, 'POWER_ON', NULL, DATE_SUB(NOW(), INTERVAL 2 DAY), 'SUCCESS'),
(4, 2, 1, 'SET_VALUE', '24', DATE_SUB(NOW(), INTERVAL 2 DAY), 'SUCCESS'),
(5, 7, 2, 'POWER_ON', NULL, DATE_SUB(NOW(), INTERVAL 1 DAY), 'SUCCESS'),
(6, 7, 2, 'SET_VALUE', '80', DATE_SUB(NOW(), INTERVAL 1 DAY), 'SUCCESS'),
(7, 9, 2, 'POWER_ON', NULL, DATE_SUB(NOW(), INTERVAL 1 DAY), 'SUCCESS'),
(8, 10, 2, 'SET_VALUE', '25', NOW(), 'SUCCESS'),
(9, 13, 3, 'POWER_ON', NULL, DATE_SUB(NOW(), INTERVAL 3 DAY), 'SUCCESS'),
(10, 13, 3, 'SET_VALUE', '70', DATE_SUB(NOW(), INTERVAL 3 DAY), 'SUCCESS');

-- 8. 状态历史数据
INSERT IGNORE INTO status_history (id, device_id, status_value, power_state, recorded_at) VALUES
(1, 1, 0, false, DATE_SUB(NOW(), INTERVAL 3 DAY)),
(2, 1, 90, true, DATE_SUB(NOW(), INTERVAL 2 DAY)),
(3, 2, 0, false, DATE_SUB(NOW(), INTERVAL 3 DAY)),
(4, 2, 24, true, DATE_SUB(NOW(), INTERVAL 2 DAY)),
(5, 3, 22, true, DATE_SUB(NOW(), INTERVAL 2 DAY)),
(6, 3, 24, true, NOW()),
(7, 7, 0, false, DATE_SUB(NOW(), INTERVAL 2 DAY)),
(8, 7, 80, true, DATE_SUB(NOW(), INTERVAL 1 DAY)),
(9, 10, 24, true, DATE_SUB(NOW(), INTERVAL 1 DAY)),
(10, 10, 25, true, NOW()),
(11, 13, 0, false, DATE_SUB(NOW(), INTERVAL 3 DAY)),
(12, 13, 70, true, DATE_SUB(NOW(), INTERVAL 3 DAY));

-- 9. 警报数据
INSERT IGNORE INTO alerts (id, device_id, user_id, threshold_value, actual_value, message, alert_type, is_read, created_at) VALUES
(1, 3, 1, 35, 36, '办公室温度传感器 数值(36)超过最大阈值(35)', 'THRESHOLD_EXCEEDED', false, DATE_SUB(NOW(), INTERVAL 1 DAY)),
(2, 6, 1, 50, 55, '烟雾报警器 数值(55)超过最大阈值(50)', 'THRESHOLD_EXCEEDED', false, NOW()),
(3, 10, 2, 35, 36, '客厅温度传感器 数值(36)超过最大阈值(35)', 'THRESHOLD_EXCEEDED', true, DATE_SUB(NOW(), INTERVAL 5 DAY)),
(4, 11, 2, 50, 55, '厨房烟雾报警器 数值(55)超过最大阈值(50)', 'THRESHOLD_EXCEEDED', false, DATE_SUB(NOW(), INTERVAL 1 DAY)),
(5, 15, 3, 35, 37, '书房温度传感器 数值(37)超过最大阈值(35)', 'THRESHOLD_EXCEEDED', false, DATE_SUB(NOW(), INTERVAL 2 DAY));

-- 10. 日志数据
INSERT IGNORE INTO logs (id, user_id, operation, details, ip_address, created_at) VALUES
(1, 1, 'USER_LOGIN', '管理员登录', '127.0.0.1', DATE_SUB(NOW(), INTERVAL 5 DAY)),
(2, 2, 'USER_LOGIN', '用户登录', '192.168.1.100', DATE_SUB(NOW(), INTERVAL 3 DAY)),
(3, 2, 'DEVICE_COMMAND', '客厅灯 - 开启设备', NULL, DATE_SUB(NOW(), INTERVAL 3 DAY)),
(4, 2, 'DEVICE_COMMAND', '客厅灯 - 设置数值: 80', NULL, DATE_SUB(NOW(), INTERVAL 3 DAY)),
(5, 2, 'SCENE_CREATE', '创建场景: 回家模式', NULL, DATE_SUB(NOW(), INTERVAL 2 DAY)),
(6, 2, 'SCENE_ACTIVATE', '激活场景: 回家模式', NULL, DATE_SUB(NOW(), INTERVAL 2 DAY)),
(7, 2, 'ALERT_TRIGGERED', '客厅温度传感器 数值(36)超过最大阈值(35)', NULL, DATE_SUB(NOW(), INTERVAL 5 DAY)),
(8, 3, 'USER_LOGIN', '用户登录', '192.168.1.101', DATE_SUB(NOW(), INTERVAL 4 DAY)),
(9, 3, 'DEVICE_BINDING', '绑定设备: 书房灯', NULL, DATE_SUB(NOW(), INTERVAL 4 DAY)),
(10, 1, 'USER_LOGIN', '管理员登录', '127.0.0.1', NOW());

-- 11. 用户反馈数据
INSERT IGNORE INTO feedbacks (id, user_id, title, content, status, admin_reply, created_at, updated_at) VALUES
(1, 2, '希望增加定时功能', '希望能够设置设备定时开关的功能，比如早上7点自动开灯', 'PROCESSED', '感谢您的建议，我们会在下个版本中考虑添加定时功能。', DATE_SUB(NOW(), INTERVAL 7 DAY), DATE_SUB(NOW(), INTERVAL 5 DAY)),
(2, 2, '空调控制有延迟', '控制空调时感觉有1-2秒的延迟，希望能优化', 'PENDING', NULL, DATE_SUB(NOW(), INTERVAL 2 DAY), DATE_SUB(NOW(), INTERVAL 2 DAY)),
(3, 3, '界面很好用', '系统界面设计很简洁，使用起来很方便，点赞！', 'PROCESSED', '感谢您的认可，我们会继续努力！', DATE_SUB(NOW(), INTERVAL 3 DAY), DATE_SUB(NOW(), INTERVAL 1 DAY));

-- 12. 共享快照数据
INSERT IGNORE INTO shared_snapshots (id, user_id, device_id, status_data, link_uuid, expire_time, created_at) VALUES
(1, 1, 1, '{"deviceName":"办公室灯","typeName":"智能灯","status":"online","powerState":true,"currentValue":90,"snapshotTime":"2024-01-15T10:30:00"}', 'abc12345-1234-1234-1234-123456789abc', DATE_ADD(NOW(), INTERVAL 24 HOUR), NOW()),
(2, 2, 10, '{"deviceName":"客厅温度传感器","typeName":"温度传感器","status":"online","powerState":true,"currentValue":25,"snapshotTime":"2024-01-15T11:00:00"}', 'def67890-5678-5678-5678-567890123def', DATE_ADD(NOW(), INTERVAL 12 HOUR), NOW());
