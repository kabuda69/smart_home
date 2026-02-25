<template>
  <div class="dashboard">
    <!-- 欢迎卡片 -->
    <div class="welcome-card">
      <div class="welcome-content">
        <div class="welcome-text">
          <span class="greeting-badge">{{ greeting }}</span>
          <h2 class="welcome-name">{{ userName }}</h2>
          <p class="welcome-desc">欢迎使用智能家居控制系统，今天也是美好的一天</p>
        </div>
        <div class="welcome-illustration">
          <div class="illustration-circle">
            <span class="mdi mdi-home-automation"></span>
          </div>
        </div>
      </div>
    </div>

    <!-- 统计卡片 -->
    <div class="stats-row">
      <div class="stat-card">
        <div class="stat-icon blue">
          <span class="mdi mdi-devices"></span>
        </div>
        <div class="stat-info">
          <span class="stat-value">{{ stats.totalDevices || 0 }}</span>
          <span class="stat-label">设备总数</span>
        </div>
      </div>
      <div class="stat-card">
        <div class="stat-icon green">
          <span class="mdi mdi-wifi"></span>
        </div>
        <div class="stat-info">
          <span class="stat-value">{{ stats.onlineDevices || 0 }}</span>
          <span class="stat-label">在线设备</span>
        </div>
      </div>
      <div class="stat-card clickable" @click="$router.push('/alerts')">
        <div class="stat-icon orange">
          <span class="mdi mdi-bell-ring"></span>
        </div>
        <div class="stat-info">
          <span class="stat-value">{{ stats.unreadAlerts || 0 }}</span>
          <span class="stat-label">未读警报</span>
        </div>
        <span class="mdi mdi-chevron-right stat-arrow"></span>
      </div>
      <div class="stat-card">
        <div class="stat-icon purple">
          <span class="mdi mdi-gesture-tap"></span>
        </div>
        <div class="stat-info">
          <span class="stat-value">{{ stats.totalCommands || 0 }}</span>
          <span class="stat-label">操作次数</span>
        </div>
      </div>
    </div>

    <!-- 主内容区 -->
    <div class="content-grid">
      <!-- 左侧：设备控制 -->
      <div class="main-section">
        <div class="section-card">
          <div class="section-header">
            <div class="header-title">
              <span class="mdi mdi-lightning-bolt"></span>
              <h3>快捷控制</h3>
            </div>
            <router-link to="/devices" class="view-all-link">
              查看全部 <span class="mdi mdi-arrow-right"></span>
            </router-link>
          </div>
          <div class="section-body">
            <div v-if="devices.length === 0" class="empty-state">
              <div class="empty-icon">
                <span class="mdi mdi-devices"></span>
              </div>
              <h4>暂无设备</h4>
              <p>添加您的第一个智能设备开始体验</p>
              <router-link to="/devices" class="add-device-btn">
                <span class="mdi mdi-plus"></span> 添加设备
              </router-link>
            </div>
            <div v-else class="device-grid">
              <div 
                v-for="device in devices.slice(0, 6)" 
                :key="device.id" 
                class="device-card"
                :class="{ active: device.powerState }"
              >
                <div class="device-header">
                  <div class="device-icon" :class="{ on: device.powerState }">
                    <span :class="'mdi ' + getDeviceIcon(device.typeName)"></span>
                  </div>
                  <label class="toggle-switch">
                    <input 
                      type="checkbox" 
                      :checked="device.powerState" 
                      @change="toggleDevice(device)"
                    >
                    <span class="toggle-slider"></span>
                  </label>
                </div>
                <div class="device-body">
                  <h4 class="device-name">{{ device.name }}</h4>
                  <span class="device-type">{{ device.typeName }}</span>
                </div>
                <div class="device-status">
                  <span class="status-dot" :class="device.status"></span>
                  <span class="status-text">{{ device.powerState ? '运行中' : '已关闭' }}</span>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>

      <!-- 右侧：场景和快捷操作 -->
      <div class="side-section">
        <!-- 场景模式 -->
        <div class="section-card">
          <div class="section-header">
            <div class="header-title">
              <span class="mdi mdi-palette"></span>
              <h3>场景模式</h3>
            </div>
          </div>
          <div class="section-body">
            <div v-if="scenes.length === 0" class="empty-state small">
              <p>暂无场景，去创建一个吧</p>
              <router-link to="/scenes" class="link-btn">创建场景</router-link>
            </div>
            <div v-else class="scene-list">
              <button 
                v-for="scene in scenes.slice(0, 4)" 
                :key="scene.id" 
                class="scene-item"
                :class="{ active: scene.isActive }"
                @click="activateScene(scene)"
              >
                <div class="scene-icon">
                  <span class="mdi" :class="getSceneIcon(scene.name)"></span>
                </div>
                <div class="scene-info">
                  <span class="scene-name">{{ scene.name }}</span>
                  <span class="scene-status">{{ scene.isActive ? '当前激活' : '点击激活' }}</span>
                </div>
                <span v-if="scene.isActive" class="mdi mdi-check-circle scene-check"></span>
              </button>
            </div>
          </div>
        </div>

        <!-- 快速操作 -->
        <div class="section-card">
          <div class="section-header">
            <div class="header-title">
              <span class="mdi mdi-flash"></span>
              <h3>快速操作</h3>
            </div>
          </div>
          <div class="section-body">
            <div class="quick-actions">
              <button 
                class="quick-btn on" 
                @click="allDevicesOn" 
                :disabled="devices.length === 0"
              >
                <span class="mdi mdi-power"></span>
                <span>全部开启</span>
              </button>
              <button 
                class="quick-btn off" 
                @click="allDevicesOff" 
                :disabled="devices.length === 0"
              >
                <span class="mdi mdi-power-off"></span>
                <span>全部关闭</span>
              </button>
            </div>
          </div>
        </div>
      </div>
    </div>

    <v-snackbar v-model="snackbar.show" :color="snackbar.color" timeout="2000">
      {{ snackbar.text }}
    </v-snackbar>
  </div>
</template>

<script>
import { mapState } from 'vuex'

export default {
  name: 'Dashboard',
  data: function() {
    return {
      stats: {},
      scenes: [],
      snackbar: { show: false, text: '', color: 'success' }
    }
  },
  computed: {
    ...mapState(['devices', 'user']),
    userName: function() {
      return this.user ? this.user.username : ''
    },
    greeting: function() {
      var hour = new Date().getHours()
      if (hour < 6) return '夜深了'
      if (hour < 9) return '早上好'
      if (hour < 12) return '上午好'
      if (hour < 14) return '中午好'
      if (hour < 18) return '下午好'
      if (hour < 22) return '晚上好'
      return '夜深了'
    }
  },
  mounted: function() {
    this.loadData()
  },
  methods: {
    loadData: function() {
      var self = this
      this.$store.dispatch('fetchDevices')
      this.$axios.get('/api/statistics').then(function(res) {
        if (res.data.success) {
          self.stats = res.data.data
        }
      })
      this.$axios.get('/api/scenes').then(function(res) {
        if (res.data.success) {
          self.scenes = res.data.data
        }
      })
    },
    getDeviceIcon: function(typeName) {
      var icons = {
        '智能灯': 'mdi-lightbulb',
        '空调': 'mdi-air-conditioner',
        '温度传感器': 'mdi-thermometer',
        '湿度传感器': 'mdi-water-percent',
        '智能插座': 'mdi-power-socket',
        '门窗传感器': 'mdi-door',
        '烟雾报警器': 'mdi-smoke-detector',
        '智能窗帘': 'mdi-blinds'
      }
      return icons[typeName] || 'mdi-devices'
    },
    getSceneIcon: function(name) {
      if (name.indexOf('回家') !== -1) return 'mdi-home'
      if (name.indexOf('离家') !== -1) return 'mdi-home-export-outline'
      if (name.indexOf('睡眠') !== -1) return 'mdi-weather-night'
      if (name.indexOf('工作') !== -1) return 'mdi-briefcase'
      return 'mdi-palette'
    },
    toggleDevice: function(device) {
      var self = this
      var cmd = device.powerState ? 'POWER_OFF' : 'POWER_ON'
      this.$axios.post('/api/devices/command', {
        deviceId: device.id,
        commandType: cmd
      }).then(function() {
        self.$store.dispatch('fetchDevices')
        self.snackbar = {
          show: true,
          text: device.name + (cmd === 'POWER_ON' ? ' 已开启' : ' 已关闭'),
          color: 'success'
        }
      })
    },
    activateScene: function(scene) {
      var self = this
      if (scene.isActive) return
      this.$axios.post('/api/scenes/' + scene.id + '/activate').then(function(res) {
        if (res.data.success) {
          self.scenes.forEach(function(s) {
            s.isActive = (s.id === scene.id)
          })
          self.$store.dispatch('fetchDevices')
          self.snackbar = {
            show: true,
            text: '已切换到「' + scene.name + '」',
            color: 'success'
          }
        }
      })
    },
    allDevicesOn: function() {
      var self = this
      var offDevices = this.devices.filter(function(d) { return !d.powerState })
      if (offDevices.length === 0) {
        self.snackbar = { show: true, text: '所有设备已开启', color: 'info' }
        return
      }
      Promise.all(offDevices.map(function(d) {
        return self.$axios.post('/api/devices/command', {
          deviceId: d.id,
          commandType: 'POWER_ON'
        })
      })).then(function() {
        self.$store.dispatch('fetchDevices')
        self.snackbar = { show: true, text: '已开启所有设备', color: 'success' }
      })
    },
    allDevicesOff: function() {
      var self = this
      var onDevices = this.devices.filter(function(d) { return d.powerState })
      if (onDevices.length === 0) {
        self.snackbar = { show: true, text: '所有设备已关闭', color: 'info' }
        return
      }
      Promise.all(onDevices.map(function(d) {
        return self.$axios.post('/api/devices/command', {
          deviceId: d.id,
          commandType: 'POWER_OFF'
        })
      })).then(function() {
        self.$store.dispatch('fetchDevices')
        self.snackbar = { show: true, text: '已关闭所有设备', color: 'success' }
      })
    }
  }
}
</script>

<style scoped>
.dashboard {
  padding: 0 32px 32px;
  max-width: 1400px;
  margin: 0 auto;
}

/* 欢迎卡片 */
.welcome-card {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border-radius: 20px;
  padding: 32px;
  margin-bottom: 24px;
  color: #fff;
  position: relative;
  overflow: hidden;
}

.welcome-card::before {
  content: '';
  position: absolute;
  top: -50%;
  right: -20%;
  width: 400px;
  height: 400px;
  background: rgba(255, 255, 255, 0.1);
  border-radius: 50%;
}

.welcome-content {
  display: flex;
  justify-content: space-between;
  align-items: center;
  position: relative;
  z-index: 1;
}

.greeting-badge {
  display: inline-block;
  background: rgba(255, 255, 255, 0.2);
  padding: 6px 14px;
  border-radius: 20px;
  font-size: 13px;
  margin-bottom: 12px;
}

.welcome-name {
  font-size: 28px;
  font-weight: 700;
  margin: 0 0 8px 0;
}

.welcome-desc {
  font-size: 14px;
  opacity: 0.9;
  margin: 0;
}

.illustration-circle {
  width: 100px;
  height: 100px;
  background: rgba(255, 255, 255, 0.15);
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
}

.illustration-circle .mdi {
  font-size: 48px;
}

/* 统计卡片 */
.stats-row {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 20px;
  margin-bottom: 24px;
}

.stat-card {
  background: #fff;
  border-radius: 16px;
  padding: 20px;
  display: flex;
  align-items: center;
  gap: 16px;
  transition: all 0.2s ease;
}

.stat-card.clickable {
  cursor: pointer;
}

.stat-card.clickable:hover {
  transform: translateY(-2px);
  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.1);
}

.stat-icon {
  width: 56px;
  height: 56px;
  border-radius: 14px;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
}

.stat-icon .mdi {
  font-size: 26px;
  color: #fff;
}

.stat-icon.blue { background: linear-gradient(135deg, #4285f4 0%, #1a73e8 100%); }
.stat-icon.green { background: linear-gradient(135deg, #34a853 0%, #1e8e3e 100%); }
.stat-icon.orange { background: linear-gradient(135deg, #fbbc04 0%, #f9ab00 100%); }
.stat-icon.purple { background: linear-gradient(135deg, #9c27b0 0%, #7b1fa2 100%); }

.stat-info {
  flex: 1;
}

.stat-value {
  display: block;
  font-size: 28px;
  font-weight: 700;
  color: #1a1a1a;
  line-height: 1.2;
}

.stat-label {
  font-size: 13px;
  color: #888;
}

.stat-arrow {
  color: #ccc;
  font-size: 20px;
}

/* 内容网格 */
.content-grid {
  display: grid;
  grid-template-columns: 1fr 360px;
  gap: 24px;
}

/* 卡片通用样式 */
.section-card {
  background: #fff;
  border-radius: 16px;
  overflow: hidden;
}

.section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 20px 24px;
  border-bottom: 1px solid #f0f0f0;
}

.header-title {
  display: flex;
  align-items: center;
  gap: 10px;
}

.header-title .mdi {
  font-size: 22px;
  color: #667eea;
}

.header-title h3 {
  font-size: 16px;
  font-weight: 600;
  color: #1a1a1a;
  margin: 0;
}

.view-all-link {
  display: flex;
  align-items: center;
  gap: 4px;
  color: #667eea;
  text-decoration: none;
  font-size: 13px;
  font-weight: 500;
}

.view-all-link:hover {
  text-decoration: underline;
}

.section-body {
  padding: 24px;
}

/* 空状态 */
.empty-state {
  text-align: center;
  padding: 40px 20px;
}

.empty-state.small {
  padding: 20px;
}

.empty-icon {
  width: 80px;
  height: 80px;
  background: #f5f5f5;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  margin: 0 auto 16px;
}

.empty-icon .mdi {
  font-size: 36px;
  color: #ccc;
}

.empty-state h4 {
  font-size: 16px;
  color: #333;
  margin: 0 0 8px 0;
}

.empty-state p {
  font-size: 14px;
  color: #888;
  margin: 0 0 20px 0;
}

.add-device-btn {
  display: inline-flex;
  align-items: center;
  gap: 6px;
  padding: 10px 20px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: #fff;
  border-radius: 10px;
  text-decoration: none;
  font-size: 14px;
  font-weight: 500;
}

.link-btn {
  color: #667eea;
  text-decoration: none;
  font-size: 14px;
}

/* 设备网格 */
.device-grid {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 16px;
}

.device-card {
  background: #f8f9fa;
  border-radius: 14px;
  padding: 16px;
  transition: all 0.2s ease;
  border: 2px solid transparent;
}

.device-card.active {
  background: #e8f5e9;
  border-color: #4caf50;
}

.device-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: 14px;
}

.device-icon {
  width: 44px;
  height: 44px;
  background: #fff;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
}

.device-icon .mdi {
  font-size: 22px;
  color: #666;
}

.device-icon.on {
  background: #4caf50;
}

.device-icon.on .mdi {
  color: #fff;
}

/* 开关样式 */
.toggle-switch {
  position: relative;
  width: 48px;
  height: 26px;
  cursor: pointer;
}

.toggle-switch input {
  opacity: 0;
  width: 0;
  height: 0;
}

.toggle-slider {
  position: absolute;
  inset: 0;
  background: #ddd;
  border-radius: 26px;
  transition: 0.3s;
}

.toggle-slider::before {
  content: '';
  position: absolute;
  width: 20px;
  height: 20px;
  left: 3px;
  bottom: 3px;
  background: #fff;
  border-radius: 50%;
  transition: 0.3s;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.2);
}

.toggle-switch input:checked + .toggle-slider {
  background: #4caf50;
}

.toggle-switch input:checked + .toggle-slider::before {
  transform: translateX(22px);
}

.device-body {
  margin-bottom: 12px;
}

.device-name {
  font-size: 15px;
  font-weight: 600;
  color: #1a1a1a;
  margin: 0 0 4px 0;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.device-type {
  font-size: 12px;
  color: #888;
}

.device-status {
  display: flex;
  align-items: center;
  gap: 6px;
}

.status-dot {
  width: 8px;
  height: 8px;
  border-radius: 50%;
  background: #ddd;
}

.status-dot.online {
  background: #4caf50;
}

.status-text {
  font-size: 12px;
  color: #888;
}

/* 侧边栏 */
.side-section {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

/* 场景列表 */
.scene-list {
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.scene-item {
  display: flex;
  align-items: center;
  gap: 14px;
  width: 100%;
  padding: 14px 16px;
  background: #f8f9fa;
  border: 2px solid transparent;
  border-radius: 12px;
  cursor: pointer;
  text-align: left;
  transition: all 0.2s ease;
}

.scene-item:hover {
  border-color: #667eea;
}

.scene-item.active {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: #fff;
}

.scene-icon {
  width: 40px;
  height: 40px;
  background: #fff;
  border-radius: 10px;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
}

.scene-item.active .scene-icon {
  background: rgba(255, 255, 255, 0.2);
}

.scene-icon .mdi {
  font-size: 20px;
  color: #667eea;
}

.scene-item.active .scene-icon .mdi {
  color: #fff;
}

.scene-info {
  flex: 1;
  min-width: 0;
}

.scene-name {
  display: block;
  font-size: 14px;
  font-weight: 500;
  color: #1a1a1a;
}

.scene-item.active .scene-name {
  color: #fff;
}

.scene-status {
  font-size: 12px;
  color: #888;
}

.scene-item.active .scene-status {
  color: rgba(255, 255, 255, 0.8);
}

.scene-check {
  color: #fff;
  font-size: 20px;
}

/* 快速操作 */
.quick-actions {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.quick-btn {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 10px;
  width: 100%;
  padding: 14px;
  border: none;
  border-radius: 12px;
  font-size: 14px;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.2s ease;
}

.quick-btn.on {
  background: #e8f5e9;
  color: #2e7d32;
}

.quick-btn.on:hover:not(:disabled) {
  background: #c8e6c9;
}

.quick-btn.off {
  background: #ffebee;
  color: #c62828;
}

.quick-btn.off:hover:not(:disabled) {
  background: #ffcdd2;
}

.quick-btn:disabled {
  opacity: 0.5;
  cursor: not-allowed;
}

/* 响应式 */
@media (max-width: 1200px) {
  .stats-row {
    grid-template-columns: repeat(2, 1fr);
  }
  .content-grid {
    grid-template-columns: 1fr;
  }
}

@media (max-width: 768px) {
  .dashboard {
    padding: 0 16px 16px;
  }
  .stats-row {
    grid-template-columns: 1fr;
  }
  .device-grid {
    grid-template-columns: 1fr;
  }
  .welcome-card {
    padding: 24px;
  }
  .welcome-name {
    font-size: 22px;
  }
  .illustration-circle {
    display: none;
  }
}
</style>
