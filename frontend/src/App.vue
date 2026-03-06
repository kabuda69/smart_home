<template>
  <div id="app-root">
    <!-- 已登录布局 -->
    <div v-if="isLoggedIn" class="app-layout">
      <!-- 侧边栏 -->
      <aside class="sidebar">
        <div class="sidebar-inner">
          <!-- Logo -->
          <div class="sidebar-brand">
            <div class="brand-logo">
              <span class="mdi mdi-home-automation"></span>
            </div>
            <div class="brand-text">
              <span class="brand-name">智能家居</span>
              <span class="brand-sub">Smart Home</span>
            </div>
          </div>

          <!-- 用户信息 -->
          <div class="sidebar-user">
            <div class="user-avatar">{{ userInitial }}</div>
            <div class="user-info">
              <span class="user-name">{{ userName }}</span>
              <span class="user-role">{{ userRoleText }}</span>
            </div>
          </div>

          <!-- 导航菜单 -->
          <nav class="sidebar-nav">
            <div class="nav-label">主菜单</div>
            <router-link
              v-for="item in menuItems"
              :key="item.to"
              :to="item.to"
              class="nav-item"
              active-class="active"
            >
              <span :class="'nav-icon mdi ' + item.icon"></span>
              <span class="nav-text">{{ item.title }}</span>
            </router-link>
          </nav>

          <!-- 退出按钮 -->
          <div class="sidebar-footer">
            <button class="logout-btn" @click="logout">
              <span class="mdi mdi-logout"></span>
              <span>退出登录</span>
            </button>
          </div>
        </div>
      </aside>

      <!-- 主内容区 -->
      <main class="main-content">
        <div class="page-header">
          <h1 class="page-title">{{ currentPageTitle }}</h1>
          <div class="header-right">
            <span class="online-status">
              <span class="status-dot online"></span>
              在线设备: {{ onlineDeviceCount }}
            </span>
            <p class="page-date">{{ currentDate }}</p>
          </div>
        </div>
        <div class="page-body">
          <router-view></router-view>
        </div>
      </main>
    </div>

    <!-- 未登录页面 -->
    <div v-else class="guest-layout">
      <router-view></router-view>
    </div>

    <!-- 全局提示 -->
    <v-snackbar v-model="snackbar.show" :color="snackbar.color" top right timeout="3000">
      {{ snackbar.text }}
    </v-snackbar>

    <!-- 告警弹窗 -->
    <div v-if="showAlertDialog" class="alert-overlay">
      <div class="alert-dialog">
        <div class="alert-header">
          <div class="alert-icon-wrapper">
            <span class="mdi mdi-alert-circle alert-icon"></span>
          </div>
          <h3>设备告警</h3>
          <p class="alert-subtitle">请立即处理！</p>
        </div>
        <div class="alert-body">
          <div class="alert-detail">
            <span class="detail-label">设备名称:</span>
            <span class="detail-value">{{ currentAlert.deviceName || '-' }}</span>
          </div>
          <div class="alert-detail">
            <span class="detail-label">设备 ID:</span>
            <span class="detail-value">{{ currentAlert.deviceId || '-' }}</span>
          </div>
          <div class="alert-detail">
            <span class="detail-label">告警类型:</span>
            <span class="detail-value alert-type">{{ getAlertTypeText(currentAlert.alertType || currentAlert.type) }}</span>
          </div>
          <div class="alert-detail">
            <span class="detail-label">告警时间:</span>
            <span class="detail-value">{{ currentAlert.time ? new Date(currentAlert.time).toLocaleString('zh-CN') : '-' }}</span>
          </div>
          <div class="alert-detail full-width">
            <span class="detail-label">告警内容:</span>
            <span class="detail-value alert-message">{{ currentAlert.message || '-' }}</span>
          </div>
        </div>
        <div class="alert-footer">
          <button class="btn-confirm" @click="closeAlertDialog">
            <span class="mdi mdi-check-circle"></span>
            我知道了
          </button>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import { mapState } from 'vuex'
import SockJS from 'sockjs-client'
import Stomp from 'stompjs'

export default {
  name: 'App',
  data: function() {
    return {
      stompClient: null,
      connected: false,
      snackbar: { show: false, text: '', color: 'primary' },
      currentDate: '',
      showAlertDialog: false,
      currentAlert: {},
      lastAlertId: null  // 记录上次弹窗的告警 ID，避免重复弹窗
    }
  },
  computed: {
    ...mapState(['user', 'unreadAlertCount', 'devices']),
    isLoggedIn: function() {
      return !!this.user
    },
    userName: function() {
      return this.user ? this.user.username : ''
    },
    userInitial: function() {
      return this.user ? this.user.username.charAt(0).toUpperCase() : 'U'
    },
    userRoleText: function() {
      return this.user && this.user.role === 'ADMIN' ? '管理员' : '普通用户'
    },
    currentPageTitle: function() {
      var titles = {
        '/dashboard': '控制面板',
        '/devices': '设备管理',
        '/scenes': '场景模式',
        '/alerts': '消息中心',
        '/statistics': '数据分析',
        '/feedback': '意见反馈',
        '/settings': '系统设置',
        '/admin': '管理后台'
      }
      return titles[this.$route.path] || '智能家居'
    },
    menuItems: function() {
      var items = [
        { title: '控制面板', icon: 'mdi-view-dashboard', to: '/dashboard' },
        { title: '设备管理', icon: 'mdi-devices', to: '/devices' },
        { title: '场景模式', icon: 'mdi-palette', to: '/scenes' },
        { title: '消息中心', icon: 'mdi-bell', to: '/alerts' },
        { title: '数据分析', icon: 'mdi-chart-areaspline', to: '/statistics' },
        { title: '意见反馈', icon: 'mdi-message-reply-text', to: '/feedback' },
        { title: '系统设置', icon: 'mdi-cog', to: '/settings' }
      ]
      if (this.user && this.user.role === 'ADMIN') {
        items.push({ title: '管理后台', icon: 'mdi-shield-crown', to: '/admin' })
      }
      return items
    },
    onlineDeviceCount: function() {
      return this.devices.filter(function(d) { return d.powerState === true }).length
    }
  },
  watch: {
    isLoggedIn: function(val) {
      if (val) {
        this.connectWebSocket()
        this.$store.dispatch('fetchAlerts')
      } else {
        this.disconnectWebSocket()
      }
    }
  },
  mounted: function() {
    this.updateDate()
    if (this.isLoggedIn) {
      this.connectWebSocket()
      this.$store.dispatch('fetchAlerts')
    }
  },
  beforeDestroy: function() {
    this.disconnectWebSocket()
  },
  methods: {
    updateDate: function() {
      var options = { month: 'long', day: 'numeric', weekday: 'long' }
      this.currentDate = new Date().toLocaleDateString('zh-CN', options)
    },
    connectWebSocket: function() {
      var self = this
      if (this.connected || !this.user) return
      try {
        console.log('开始连接 WebSocket...')
        var socket = new SockJS('/ws')
        this.stompClient = Stomp.over(socket)
        this.stompClient.debug = null
        this.stompClient.connect({}, function() {
          console.log('WebSocket 连接成功')
          self.connected = true
          self.stompClient.subscribe('/topic/alerts/' + self.user.id, function(msg) {
            console.log('收到告警消息:', msg.body)
            try {
              var alert = JSON.parse(msg.body)
              self.handleNewAlert(alert)
            } catch (e) { 
              console.error('解析告警消息失败:', e)
            }
          })
          self.stompClient.subscribe('/topic/devices/' + self.user.id, function(msg) {
            try {
              console.log('收到设备更新:', msg.body)
              self.$store.commit('UPDATE_DEVICE', JSON.parse(msg.body))
            } catch (e) { /* ignore */ }
          })
        }, function() {
          console.log('WebSocket 连接失败，5 秒后重试')
          self.connected = false
          setTimeout(function() { self.connectWebSocket() }, 5000)
        })
      } catch (e) { 
        console.error('WebSocket 连接异常:', e)
      }
    },
    handleNewAlert: function(alert) {
      console.log('====== 收到告警 ======')
      console.log('告警详情:', alert)
      
      // 兼容后端发送的字段：type 或 alertType
      var alertType = alert.alertType || alert.type
      console.log('告警类型:', alertType)
      console.log('上次告警 ID:', this.lastAlertId)
      console.log('当前告警 ID:', alert.id)
      
      // 只处理阈值超限的告警（THRESHOLD_EXCEEDED）
      if (alertType !== 'THRESHOLD_EXCEEDED') {
        console.log('非阈值超限告警，不弹窗')
        this.$store.dispatch('fetchAlerts')
        return
      }
      
      // 如果是相同的告警 ID，不重复弹窗
      if (this.lastAlertId === alert.id) {
        console.log('重复告警，不弹窗')
        return
      }
      
      console.log('开始播放提示音...')
      this.playAlertSound()
      
      console.log('设置弹窗显示...')
      this.currentAlert = alert
      this.currentAlert.alertType = alertType  // 确保 alertType 字段存在
      this.showAlertDialog = true
      this.lastAlertId = alert.id
      
      console.log('弹窗已显示，告警 ID:', alert.id)
      this.$store.dispatch('fetchAlerts')
    },
    getAlertTypeText: function(type) {
      var typeMap = {
        'THRESHOLD_EXCEEDED': '阈值超限',
        'DEVICE_OFFLINE': '设备离线',
        'DEVICE_ERROR': '设备故障'
      }
      return typeMap[type] || type || '-'
    },
    playAlertSound: function() {
      try {
        var audioContext = new (window.AudioContext || window.webkitAudioContext)()
        var oscillator = audioContext.createOscillator()
        var gainNode = audioContext.createGain()
        oscillator.connect(gainNode)
        gainNode.connect(audioContext.destination)
        oscillator.frequency.value = 800
        oscillator.type = 'sine'
        gainNode.gain.setValueAtTime(0.3, audioContext.currentTime)
        gainNode.gain.exponentialRampToValueAtTime(0.01, audioContext.currentTime + 0.5)
        oscillator.start(audioContext.currentTime)
        oscillator.stop(audioContext.currentTime + 0.5)
      } catch (e) {
        console.log('无法播放提示音')
      }
    },
    closeAlertDialog: function() {
      this.showAlertDialog = false
      this.lastAlertId = null  // 清空记录，允许下次弹窗
      console.log('关闭告警弹窗，已清空记录，等待下次告警')
    },
    disconnectWebSocket: function() {
      if (this.stompClient && this.connected) {
        try { this.stompClient.disconnect() } catch (e) { /* ignore */ }
        this.connected = false
        this.stompClient = null
      }
    },
    logout: function() {
      this.disconnectWebSocket()
      this.$store.dispatch('logout')
      this.$router.push('/login')
    }
  }
}
</script>

<style>
/* 重置和基础样式 */
* {
  margin: 0;
  padding: 0;
  box-sizing: border-box;
}

html, body {
  height: 100%;
  font-family: 'Inter', -apple-system, BlinkMacSystemFont, 'Segoe UI', sans-serif;
  background: #f5f6fa;
}

#app-root {
  height: 100%;
}

/* 主布局 - 使用 Flexbox */
.app-layout {
  display: flex;
  height: 100vh;
  width: 100%;
}

/* 侧边栏 */
.sidebar {
  width: 240px;
  min-width: 240px;
  max-width: 240px;
  height: 100vh;
  background: #fff;
  border-right: 1px solid #eaeaea;
  flex-shrink: 0;
}

.sidebar-inner {
  display: flex;
  flex-direction: column;
  height: 100%;
  padding: 16px;
  overflow-y: auto;
}

/* Logo */
.sidebar-brand {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 8px;
  margin-bottom: 20px;
}

.brand-logo {
  width: 40px;
  height: 40px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border-radius: 10px;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
}

.brand-logo .mdi {
  font-size: 20px;
  color: #fff;
}

.brand-text {
  display: flex;
  flex-direction: column;
}

.brand-name {
  font-size: 16px;
  font-weight: 600;
  color: #1a1a1a;
}

.brand-sub {
  font-size: 10px;
  color: #999;
  text-transform: uppercase;
  letter-spacing: 1px;
}

/* 用户信息 */
.sidebar-user {
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 12px;
  background: #f8f9fa;
  border-radius: 10px;
  margin-bottom: 20px;
}

.user-avatar {
  width: 36px;
  height: 36px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border-radius: 8px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #fff;
  font-weight: 600;
  font-size: 14px;
  flex-shrink: 0;
}

.user-info {
  display: flex;
  flex-direction: column;
  min-width: 0;
  flex: 1;
}

.user-name {
  font-weight: 500;
  color: #1a1a1a;
  font-size: 13px;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.user-role {
  font-size: 11px;
  color: #888;
}

/* 导航 */
.sidebar-nav {
  flex: 1;
  overflow-y: auto;
}

.nav-label {
  font-size: 10px;
  font-weight: 600;
  color: #999;
  text-transform: uppercase;
  letter-spacing: 0.5px;
  padding: 0 8px;
  margin-bottom: 8px;
}

.nav-item {
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 10px 12px;
  color: #666;
  text-decoration: none;
  border-radius: 8px;
  margin-bottom: 2px;
  font-size: 13px;
  transition: all 0.15s ease;
}

.nav-icon {
  font-size: 18px;
  width: 20px;
  text-align: center;
}

.nav-item:hover {
  background: #f5f5f5;
  color: #667eea;
}

.nav-item.active {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: #fff;
}

/* 退出按钮 */
.sidebar-footer {
  padding-top: 12px;
  border-top: 1px solid #f0f0f0;
  margin-top: auto;
}

.logout-btn {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
  width: 100%;
  padding: 10px;
  background: #fff5f5;
  border: none;
  border-radius: 8px;
  color: #e53935;
  font-size: 13px;
  cursor: pointer;
  transition: background 0.15s ease;
}

.logout-btn:hover {
  background: #ffebee;
}

/* 主内容区 */
.main-content {
  flex: 1;
  height: 100vh;
  overflow-y: auto;
  background: #f5f6fa;
}

.page-header {
  padding: 20px 24px;
  background: #f5f6fa;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.page-title {
  font-size: 22px;
  font-weight: 600;
  color: #1a1a1a;
  margin: 0 0 4px 0;
}

.header-right {
  display: flex;
  align-items: center;
  gap: 20px;
}

.online-status {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 14px;
  color: #666;
}

.page-date {
  font-size: 13px;
  color: #888;
  margin: 0;
}

.page-body {
  padding: 0;
}

/* 未登录页面 */
.guest-layout {
  height: 100vh;
  width: 100%;
}

/* 滚动条 */
::-webkit-scrollbar {
  width: 6px;
  height: 6px;
}

::-webkit-scrollbar-track {
  background: transparent;
}

::-webkit-scrollbar-thumb {
  background: #ddd;
  border-radius: 3px;
}

::-webkit-scrollbar-thumb:hover {
  background: #ccc;
}

/* Vuetify 覆盖 */
.v-application {
  background: transparent !important;
}

.v-application--wrap {
  min-height: auto !important;
}

/* 告警弹窗 - 居中显示 */
.alert-overlay {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background: rgba(0, 0, 0, 0.5);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 9999;
}

.alert-dialog {
  background: #fff;
  border-radius: 20px;
  overflow: hidden;
  box-shadow: 0 25px 50px -12px rgba(0, 0, 0, 0.5);
  animation: popIn 0.4s cubic-bezier(0.68, -0.55, 0.265, 1.55), pulse 2s ease-in-out infinite;
  border: 4px solid #ff5722;
  width: 90%;
  max-width: 400px;
}

@keyframes popIn {
  0% {
    transform: scale(0.5);
    opacity: 0;
  }
  100% {
    transform: scale(1);
    opacity: 1;
  }
}

@keyframes pulse {
  0%, 100% {
    box-shadow: 0 25px 50px -12px rgba(255, 87, 34, 0.5);
  }
  50% {
    box-shadow: 0 25px 50px -12px rgba(255, 87, 34, 0.8), 0 0 0 10px rgba(255, 87, 34, 0.1);
  }
}

/* 告警头部 */
.alert-header {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 12px;
  padding: 32px 24px;
  background: linear-gradient(135deg, #ff5722 0%, #e91e63 100%);
  color: #fff;
  text-align: center;
}

.alert-icon-wrapper {
  width: 80px;
  height: 80px;
  background: rgba(255, 255, 255, 0.25);
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  animation: pulse 1.5s ease-in-out infinite;
}

@keyframes pulse {
  0%, 100% {
    transform: scale(1);
  }
  50% {
    transform: scale(1.1);
  }
}

.alert-icon {
  font-size: 48px;
  animation: shake 0.5s ease-in-out infinite;
}

@keyframes shake {
  0%, 100% {
    transform: translateX(0);
  }
  25% {
    transform: translateX(-5px) rotate(-5deg);
  }
  75% {
    transform: translateX(5px) rotate(5deg);
  }
}

.alert-header h3 {
  margin: 0;
  font-size: 28px;
  font-weight: 700;
  letter-spacing: 1px;
}

.alert-subtitle {
  margin: 0;
  font-size: 16px;
  opacity: 0.95;
  font-weight: 500;
}

/* 告警内容区 */
.alert-body {
  padding: 32px 24px;
  background: #fffbf8;
}

.alert-detail {
  display: flex;
  margin-bottom: 20px;
  padding: 12px;
  background: #fff;
  border-radius: 12px;
  border-left: 4px solid #ff9800;
}

.alert-detail.full-width {
  flex-direction: column;
  gap: 8px;
}

.detail-label {
  font-size: 14px;
  color: #888;
  min-width: 90px;
  font-weight: 600;
}

.detail-value {
  font-size: 15px;
  color: #333;
  font-weight: 600;
}

.alert-type {
  color: #e91e63;
  font-size: 16px;
}

.alert-message {
  color: #ff5722;
  font-size: 16px;
  line-height: 1.5;
}

/* 告警底部 */
.alert-footer {
  padding: 24px;
  background: #fafafa;
  display: flex;
  justify-content: center;
}

.btn-confirm {
  padding: 14px 48px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border: none;
  border-radius: 12px;
  color: #fff;
  font-size: 16px;
  font-weight: 600;
  cursor: pointer;
  display: flex;
  align-items: center;
  gap: 8px;
  transition: all 0.3s ease;
  box-shadow: 0 4px 15px rgba(102, 126, 234, 0.4);
}

.btn-confirm:hover {
  transform: translateY(-2px);
  box-shadow: 0 6px 20px rgba(102, 126, 234, 0.6);
}

.btn-confirm:active {
  transform: translateY(0);
}
</style>
