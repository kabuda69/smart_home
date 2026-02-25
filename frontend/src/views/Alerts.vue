<template>
  <div class="alerts-page">
    <!-- 顶部统计和操作 -->
    <div class="alerts-header">
      <div class="stats-cards">
        <div class="stat-card red">
          <span class="mdi mdi-alert-circle"></span>
          <div class="stat-info">
            <span class="stat-num">{{ unreadCount }}</span>
            <span class="stat-label">未读</span>
          </div>
        </div>
        <div class="stat-card orange">
          <span class="mdi mdi-bell-ring"></span>
          <div class="stat-info">
            <span class="stat-num">{{ alerts.length }}</span>
            <span class="stat-label">总数</span>
          </div>
        </div>
        <div class="stat-card green">
          <span class="mdi mdi-check-circle"></span>
          <div class="stat-info">
            <span class="stat-num">{{ readCount }}</span>
            <span class="stat-label">已处理</span>
          </div>
        </div>
      </div>
      <button class="mark-all-btn" @click="markAllRead" :disabled="unreadCount === 0">
        <span class="mdi mdi-check-all"></span> 全部已读
      </button>
    </div>

    <!-- 警报列表 -->
    <div class="alerts-content">
      <div v-if="loading" class="loading-state">
        <div class="spinner"></div>
        <p>加载中...</p>
      </div>

      <div v-else-if="alerts.length === 0" class="empty-state">
        <span class="mdi mdi-check-circle"></span>
        <h3>暂无警报</h3>
        <p>所有设备运行正常</p>
      </div>

      <div v-else class="alerts-list">
        <div 
          v-for="alert in alerts" 
          :key="alert.id" 
          class="alert-item"
          :class="{ unread: !alert.isRead }"
        >
          <div class="alert-indicator" :class="alert.isRead ? 'read' : 'unread'"></div>
          <div class="alert-icon">
            <span class="mdi mdi-alert"></span>
          </div>
          <div class="alert-body">
            <div class="alert-title">{{ alert.deviceName }}</div>
            <div class="alert-message">{{ alert.message }}</div>
            <div class="alert-time">
              <span class="mdi mdi-clock-outline"></span>
              {{ formatDate(alert.createdAt) }}
            </div>
          </div>
          <div class="alert-actions">
            <button 
              v-if="!alert.isRead" 
              class="action-btn check" 
              @click="markRead(alert.id)"
              title="标记已读"
            >
              <span class="mdi mdi-check"></span>
            </button>
            <button 
              class="action-btn delete" 
              @click="deleteAlert(alert.id)"
              title="删除"
            >
              <span class="mdi mdi-delete"></span>
            </button>
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
export default {
  data: function() {
    return {
      alerts: [],
      loading: true,
      snackbar: { show: false, text: '', color: 'success' }
    }
  },
  computed: {
    unreadCount: function() {
      return this.alerts.filter(function(a) { return !a.isRead }).length
    },
    readCount: function() {
      return this.alerts.filter(function(a) { return a.isRead }).length
    }
  },
  mounted: function() {
    this.loadAlerts()
  },
  methods: {
    loadAlerts: function() {
      var self = this
      self.loading = true
      this.$axios.get('/api/alerts', { params: { page: 0, size: 50 } }).then(function(r) {
        if (r.data.success) {
          var data = r.data.data
          if (data && data.content) {
            self.alerts = data.content
          } else if (Array.isArray(data)) {
            self.alerts = data
          } else {
            self.alerts = []
          }
        }
        self.loading = false
      }).catch(function(err) {
        console.error('Load alerts error:', err)
        self.alerts = []
        self.loading = false
      })
    },
    formatDate: function(d) {
      if (!d) return '-'
      return new Date(d).toLocaleString('zh-CN')
    },
    markRead: function(id) {
      var self = this
      this.$axios.put('/api/alerts/' + id + '/read').then(function() {
        self.loadAlerts()
        self.$store.dispatch('fetchAlerts')
        self.showMsg('已标记为已读', 'success')
      }).catch(function() {
        self.showMsg('操作失败', 'error')
      })
    },
    markAllRead: function() {
      var self = this
      this.$axios.put('/api/alerts/read-all').then(function() {
        self.loadAlerts()
        self.$store.dispatch('fetchAlerts')
        self.showMsg('已全部标记为已读', 'success')
      }).catch(function() {
        self.showMsg('操作失败', 'error')
      })
    },
    deleteAlert: function(id) {
      var self = this
      if (!confirm('确定删除这条警报？')) return
      this.$axios.delete('/api/alerts/' + id).then(function() {
        self.loadAlerts()
        self.$store.dispatch('fetchAlerts')
        self.showMsg('已删除', 'success')
      }).catch(function() {
        self.showMsg('删除失败', 'error')
      })
    },
    showMsg: function(t, c) {
      this.snackbar = { show: true, text: t, color: c }
    }
  }
}
</script>

<style scoped>
.alerts-page {
  padding: 0 24px 24px;
  max-width: 1000px;
  margin: 0 auto;
}

/* 头部 */
.alerts-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
  gap: 20px;
}

.stats-cards {
  display: flex;
  gap: 12px;
}

.stat-card {
  display: flex;
  align-items: center;
  gap: 10px;
  background: #fff;
  padding: 12px 16px;
  border-radius: 10px;
  min-width: 100px;
}

.stat-card .mdi {
  font-size: 24px;
  width: 40px;
  height: 40px;
  border-radius: 8px;
  display: flex;
  align-items: center;
  justify-content: center;
}

.stat-card.red .mdi { background: #ffebee; color: #e53935; }
.stat-card.orange .mdi { background: #fff3e0; color: #ff9800; }
.stat-card.green .mdi { background: #e8f5e9; color: #4caf50; }

.stat-info {
  display: flex;
  flex-direction: column;
}

.stat-num {
  font-size: 20px;
  font-weight: 700;
  color: #1a1a1a;
  line-height: 1;
}

.stat-label {
  font-size: 12px;
  color: #888;
}

.mark-all-btn {
  display: flex;
  align-items: center;
  gap: 6px;
  padding: 10px 16px;
  background: #fff;
  border: 1px solid #e0e0e0;
  border-radius: 8px;
  color: #666;
  font-size: 13px;
  cursor: pointer;
  transition: all 0.15s;
}

.mark-all-btn:hover:not(:disabled) {
  border-color: #667eea;
  color: #667eea;
}

.mark-all-btn:disabled {
  opacity: 0.5;
  cursor: not-allowed;
}

/* 内容区 */
.alerts-content {
  background: #fff;
  border-radius: 12px;
  overflow: hidden;
}

/* 加载状态 */
.loading-state {
  padding: 60px;
  text-align: center;
}

.spinner {
  width: 36px;
  height: 36px;
  border: 3px solid #f0f0f0;
  border-top-color: #667eea;
  border-radius: 50%;
  animation: spin 0.8s linear infinite;
  margin: 0 auto 12px;
}

@keyframes spin {
  to { transform: rotate(360deg); }
}

.loading-state p {
  color: #888;
  font-size: 14px;
}

/* 空状态 */
.empty-state {
  padding: 60px;
  text-align: center;
}

.empty-state .mdi {
  font-size: 56px;
  color: #4caf50;
}

.empty-state h3 {
  font-size: 18px;
  color: #333;
  margin: 16px 0 8px;
}

.empty-state p {
  color: #888;
  font-size: 14px;
}

/* 警报列表 */
.alerts-list {
  padding: 8px;
}

.alert-item {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 14px;
  border-radius: 10px;
  margin-bottom: 8px;
  background: #fafafa;
  transition: all 0.15s;
}

.alert-item:last-child {
  margin-bottom: 0;
}

.alert-item.unread {
  background: #fffde7;
}

.alert-item:hover {
  background: #f5f5f5;
}

.alert-item.unread:hover {
  background: #fff9c4;
}

.alert-indicator {
  width: 8px;
  height: 8px;
  border-radius: 50%;
  flex-shrink: 0;
}

.alert-indicator.unread {
  background: #ff9800;
}

.alert-indicator.read {
  background: #e0e0e0;
}

.alert-icon {
  width: 40px;
  height: 40px;
  background: #fff3e0;
  border-radius: 10px;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
}

.alert-icon .mdi {
  font-size: 20px;
  color: #ff9800;
}

.alert-body {
  flex: 1;
  min-width: 0;
}

.alert-title {
  font-size: 14px;
  font-weight: 600;
  color: #1a1a1a;
  margin-bottom: 4px;
}

.alert-message {
  font-size: 13px;
  color: #666;
  margin-bottom: 6px;
  line-height: 1.4;
}

.alert-time {
  display: flex;
  align-items: center;
  gap: 4px;
  font-size: 12px;
  color: #999;
}

.alert-time .mdi {
  font-size: 14px;
}

.alert-actions {
  display: flex;
  gap: 6px;
  flex-shrink: 0;
}

.action-btn {
  width: 32px;
  height: 32px;
  border: none;
  border-radius: 8px;
  background: #fff;
  color: #666;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: all 0.15s;
}

.action-btn .mdi {
  font-size: 16px;
}

.action-btn.check:hover {
  background: #e8f5e9;
  color: #4caf50;
}

.action-btn.delete:hover {
  background: #ffebee;
  color: #e53935;
}

/* 响应式 */
@media (max-width: 768px) {
  .alerts-page {
    padding: 0 16px 16px;
  }
  
  .alerts-header {
    flex-direction: column;
    align-items: stretch;
  }
  
  .stats-cards {
    justify-content: space-between;
  }
  
  .stat-card {
    flex: 1;
    min-width: auto;
  }
}
</style>
