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
          <p class="page-date">{{ currentDate }}</p>
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
      currentDate: ''
    }
  },
  computed: {
    ...mapState(['user', 'unreadAlertCount']),
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
        var socket = new SockJS('/ws')
        this.stompClient = Stomp.over(socket)
        this.stompClient.debug = null
        this.stompClient.connect({}, function() {
          self.connected = true
          self.stompClient.subscribe('/topic/alerts/' + self.user.id, function(msg) {
            try {
              var alert = JSON.parse(msg.body)
              self.snackbar = { show: true, text: alert.message || '收到新警报', color: 'warning' }
              self.$store.dispatch('fetchAlerts')
            } catch (e) { /* ignore */ }
          })
          self.stompClient.subscribe('/topic/devices/' + self.user.id, function(msg) {
            try {
              self.$store.commit('UPDATE_DEVICE', JSON.parse(msg.body))
            } catch (e) { /* ignore */ }
          })
        }, function() {
          self.connected = false
          setTimeout(function() { self.connectWebSocket() }, 5000)
        })
      } catch (e) { /* ignore */ }
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
}

.page-title {
  font-size: 22px;
  font-weight: 600;
  color: #1a1a1a;
  margin: 0 0 4px 0;
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
</style>
