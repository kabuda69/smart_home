<template>
  <div class="settings-page">
    <div class="settings-grid">
      <div class="settings-card">
        <div class="card-header"><span class="mdi mdi-bell-cog"></span><h3>通知设置</h3></div>
        <div class="card-body">
          <div v-for="pref in preferences" :key="pref.id" class="pref-item">
            <div class="pref-info">
              <span class="mdi" :class="pref.notificationType === 'POPUP' ? 'mdi-message-alert' : 'mdi-file-document'"></span>
              <div><div class="pref-name">{{ pref.notificationType === 'POPUP' ? '弹窗通知' : '日志记录' }}</div><div class="pref-desc">{{ pref.notificationType === 'POPUP' ? '收到警报时显示弹窗' : '将警报记录到日志' }}</div></div>
            </div>
            <label class="switch"><input type="checkbox" :checked="pref.enabled" @change="updatePref(pref)"><span class="slider"></span></label>
          </div>
        </div>
      </div>

      <div class="settings-card">
        <div class="card-header"><span class="mdi mdi-account-circle"></span><h3>账户信息</h3></div>
        <div class="card-body">
          <div class="user-profile">
            <div class="avatar">{{ user ? user.username.charAt(0).toUpperCase() : 'U' }}</div>
            <div class="info-row"><span class="label">用户名</span><span class="value">{{ user ? user.username : '' }}</span></div>
            <div class="info-row"><span class="label">角色</span><span class="role-tag" :class="user && user.role === 'ADMIN' ? 'admin' : 'user'">{{ user && user.role === 'ADMIN' ? '管理员' : '用户' }}</span></div>
          </div>
        </div>
      </div>
    </div>

    <div class="settings-card full">
      <div class="card-header"><span class="mdi mdi-history"></span><h3>操作日志</h3><button class="refresh-btn" @click="loadLogs"><span class="mdi mdi-refresh"></span></button></div>
      <div class="card-body">
        <div class="log-filter">
          <span class="filter-label">时间段：</span>
          <input type="datetime-local" v-model="logStartTime" class="time-input" step="1">
          <span class="filter-separator">至</span>
          <input type="datetime-local" v-model="logEndTime" class="time-input" step="1">
          <button class="filter-btn" @click="searchLogs"><span class="mdi mdi-magnify"></span> 查询</button>
          <button class="filter-btn reset" @click="resetTimeFilter"><span class="mdi mdi-refresh"></span> 重置</button>
        </div>
        <table class="data-table">
          <thead><tr><th>操作</th><th>详情</th><th>时间</th></tr></thead>
          <tbody>
            <tr v-for="log in logs" :key="log.id">
              <td><span class="op-tag" :class="getOpClass(log.operation)">{{ log.operation }}</span></td>
              <td>{{ log.details || '-' }}</td>
              <td class="time-cell">{{ formatDate(log.createdAt) }}</td>
            </tr>
            <tr v-if="logs.length === 0"><td colspan="3" class="empty-cell">暂无日志</td></tr>
          </tbody>
        </table>
      </div>
    </div>

    <v-snackbar v-model="snackbar.show" :color="snackbar.color" timeout="2000">{{ snackbar.text }}</v-snackbar>
  </div>
</template>

<script>
import { mapState } from 'vuex'
export default {
  data: function() { 
    return { 
      preferences: [], 
      logs: [], 
      snackbar: { show: false, text: '', color: 'success' },
      logStartTime: '',
      logEndTime: '',
      logPage: 0,
      logTotalPages: 1
    } 
  },
  computed: { ...mapState(['user']) },
  mounted: function() { 
    this.loadPrefs(); 
    this.initDefaultTimeRange();
    this.loadLogs() 
  },
  methods: {
    loadPrefs: function() { var self = this; this.$axios.get('/api/users/preferences').then(function(r) { if (r.data.success) self.preferences = r.data.data }) },
    updatePref: function(p) { var self = this; p.enabled = !p.enabled; this.$axios.put('/api/users/preferences', { type: p.notificationType, enabled: p.enabled }).then(function() { self.showMsg('已保存', 'success') }).catch(function() { p.enabled = !p.enabled; self.showMsg('保存失败', 'error') }) },
    loadLogs: function() { 
      var self = this; 
      var params = { page: this.logPage, size: 20 };
      if (this.logStartTime && this.logEndTime) {
        params.startTime = this.logStartTime;
        params.endTime = this.logEndTime;
      }
      this.$axios.get('/api/logs', { params: params }).then(function(r) { 
        if (r.data.success) {
          self.logs = r.data.data.content;
          self.logTotalPages = r.data.data.totalPages;
        } 
      }) 
    },
    initDefaultTimeRange: function() {
      var now = new Date();
      var sevenDaysAgo = new Date(now.getTime() - 7 * 24 * 60 * 60 * 1000);
      this.logEndTime = this.formatDateTimeLocal(now);
      this.logStartTime = this.formatDateTimeLocal(sevenDaysAgo);
    },
    formatDateTimeLocal: function(date) {
      var year = date.getFullYear();
      var month = String(date.getMonth() + 1).padStart(2, '0');
      var day = String(date.getDate()).padStart(2, '0');
      var hours = String(date.getHours()).padStart(2, '0');
      var minutes = String(date.getMinutes()).padStart(2, '0');
      var seconds = String(date.getSeconds()).padStart(2, '0');
      return year + '-' + month + '-' + day + 'T' + hours + ':' + minutes + ':' + seconds;
    },
    searchLogs: function() {
      this.logPage = 0;
      this.loadLogs();
    },
    resetTimeFilter: function() {
      this.initDefaultTimeRange();
      this.logPage = 0;
      this.loadLogs();
    },
    formatDate: function(d) { return d ? new Date(d).toLocaleString('zh-CN') : '-' },
    getOpClass: function(op) { if (op.indexOf('LOGIN') !== -1) return 'info'; if (op.indexOf('COMMAND') !== -1) return 'primary'; if (op.indexOf('ALERT') !== -1) return 'warning'; return 'default' },
    showMsg: function(t, c) { this.snackbar = { show: true, text: t, color: c } }
  }
}
</script>

<style scoped>
.settings-page { padding: 0 32px 32px; max-width: 1000px; margin: 0 auto; }
.settings-grid { display: grid; grid-template-columns: repeat(2, 1fr); gap: 20px; margin-bottom: 20px; }
.settings-card { background: #fff; border-radius: 12px; overflow: hidden; }
.settings-card.full { grid-column: 1 / -1; }
.card-header { display: flex; align-items: center; gap: 10px; padding: 18px 20px; border-bottom: 1px solid #f0f0f0; }
.card-header .mdi { font-size: 22px; color: #667eea; }
.card-header h3 { font-size: 15px; color: #333; margin: 0; flex: 1; }
.refresh-btn { width: 30px; height: 30px; border: none; background: #f5f5f5; border-radius: 6px; color: #666; cursor: pointer; }
.card-body { padding: 20px; }

.log-filter {
  display: flex;
  align-items: center;
  gap: 10px;
  margin-bottom: 20px;
  padding: 15px;
  background: #f8f9fa;
  border-radius: 8px;
  flex-wrap: wrap;
}

.filter-label {
  font-size: 14px;
  color: #666;
  font-weight: 500;
}

.time-input {
  padding: 8px 12px;
  border: 1px solid #ddd;
  border-radius: 6px;
  font-size: 14px;
  width: 180px;
}

.filter-separator {
  color: #999;
  font-size: 14px;
}

.filter-btn {
  padding: 8px 16px;
  border: none;
  border-radius: 6px;
  font-size: 14px;
  cursor: pointer;
  display: flex;
  align-items: center;
  gap: 5px;
  transition: all 0.3s ease;
}

.filter-btn:not(.reset) {
  background: #667eea;
  color: #fff;
}

.filter-btn:not(.reset):hover {
  background: #5a6fd8;
  transform: translateY(-1px);
}

.filter-btn.reset {
  background: #f5f5f5;
  color: #666;
}

.filter-btn.reset:hover {
  background: #e9ecef;
  transform: translateY(-1px);
}

.pagination {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 10px;
  margin-top: 20px;
}

.page-btn {
  width: 32px;
  height: 32px;
  border: 1px solid #ddd;
  background: #fff;
  border-radius: 6px;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: all 0.3s ease;
}

.page-btn:hover:not(:disabled) {
  background: #f8f9fa;
  border-color: #667eea;
}

.page-btn:disabled {
  opacity: 0.5;
  cursor: not-allowed;
}

.page-info {
  font-size: 14px;
  color: #666;
}

.pref-item { display: flex; align-items: center; justify-content: space-between; padding: 14px 0; border-bottom: 1px solid #f0f0f0; }
.pref-item:last-child { border-bottom: none; }
.pref-info { display: flex; align-items: center; gap: 12px; }
.pref-info .mdi { font-size: 22px; color: #666; }
.pref-name { font-weight: 500; color: #333; margin-bottom: 2px; }
.pref-desc { font-size: 12px; color: #888; }

.switch { position: relative; width: 44px; height: 24px; }
.switch input { opacity: 0; width: 0; height: 0; }
.slider { position: absolute; cursor: pointer; inset: 0; background: #ccc; border-radius: 24px; transition: 0.3s; }
.slider:before { content: ''; position: absolute; width: 18px; height: 18px; left: 3px; bottom: 3px; background: #fff; border-radius: 50%; transition: 0.3s; }
.switch input:checked + .slider { background: #667eea; }
.switch input:checked + .slider:before { transform: translateX(20px); }

.user-profile { text-align: center; }
.avatar { width: 64px; height: 64px; background: linear-gradient(135deg, #667eea 0%, #764ba2 100%); border-radius: 16px; display: flex; align-items: center; justify-content: center; margin: 0 auto 18px; color: #fff; font-size: 26px; font-weight: 600; }
.info-row { display: flex; justify-content: space-between; padding: 12px 0; border-bottom: 1px solid #f0f0f0; }
.info-row:last-child { border-bottom: none; }
.label { color: #888; }
.value { font-weight: 500; color: #333; }
.role-tag { padding: 4px 10px; border-radius: 6px; font-size: 12px; }
.role-tag.admin { background: #e3f2fd; color: #1976d2; }
.role-tag.user { background: #f5f5f5; color: #666; }

.data-table { width: 100%; border-collapse: collapse; }
.data-table th { text-align: left; padding: 10px; background: #f5f5f5; color: #666; font-size: 13px; }
.data-table td { padding: 10px; border-bottom: 1px solid #f0f0f0; font-size: 14px; }
.time-cell { color: #888; font-size: 13px; }
.op-tag { padding: 3px 8px; border-radius: 4px; font-size: 12px; }
.op-tag.info { background: #e3f2fd; color: #1976d2; }
.op-tag.primary { background: #e8f5e9; color: #2e7d32; }
.op-tag.warning { background: #fff3e0; color: #ef6c00; }
.op-tag.default { background: #f5f5f5; color: #666; }
.empty-cell { text-align: center; color: #888; }

@media (max-width: 768px) { 
  .settings-grid { grid-template-columns: 1fr; }
  .log-filter {
    flex-direction: column;
    align-items: flex-start;
  }
  .time-input {
    width: 100%;
    margin-bottom: 10px;
  }
  .filter-btn {
    width: 100%;
    justify-content: center;
  }
}
</style>
