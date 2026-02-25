<template>
  <div class="share-page">
    <div class="share-container">
      <div v-if="!snapshot && !error" class="loading">
        <div class="spinner"></div>
        <p>加载中...</p>
      </div>

      <div v-else-if="error" class="error-card">
        <span class="mdi mdi-link-off"></span>
        <h3>{{ error }}</h3>
        <p>请检查链接是否正确或联系分享者</p>
      </div>

      <div v-else class="snapshot-card">
        <div class="card-header">
          <span class="mdi mdi-share-variant"></span>
          <h2>设备状态快照</h2>
        </div>
        <div class="card-body">
          <div class="info-item">
            <span class="mdi mdi-tag"></span>
            <div class="info-content">
              <span class="label">设备名称</span>
              <span class="value">{{ snapshot.deviceName }}</span>
            </div>
          </div>
          <div class="info-item">
            <span class="mdi mdi-shape"></span>
            <div class="info-content">
              <span class="label">设备类型</span>
              <span class="value">{{ snapshot.typeName }}</span>
            </div>
          </div>
          <div class="info-item">
            <span class="mdi mdi-wifi"></span>
            <div class="info-content">
              <span class="label">连接状态</span>
              <span class="status-badge" :class="snapshot.status">{{ snapshot.status === 'online' ? '在线' : '离线' }}</span>
            </div>
          </div>
          <div class="info-item">
            <span class="mdi mdi-power"></span>
            <div class="info-content">
              <span class="label">电源状态</span>
              <span class="status-badge" :class="snapshot.powerState ? 'on' : 'off'">{{ snapshot.powerState ? '开启' : '关闭' }}</span>
            </div>
          </div>
          <div v-if="snapshot.currentValue !== null" class="info-item">
            <span class="mdi mdi-gauge"></span>
            <div class="info-content">
              <span class="label">当前数值</span>
              <span class="value highlight">{{ snapshot.currentValue }}</span>
            </div>
          </div>
          <div class="info-item">
            <span class="mdi mdi-clock-outline"></span>
            <div class="info-content">
              <span class="label">快照时间</span>
              <span class="value small">{{ snapshot.snapshotTime }}</span>
            </div>
          </div>
        </div>
        <div class="card-footer">
          <a href="/" class="home-btn">
            <span class="mdi mdi-home"></span> 访问智能家居系统
          </a>
        </div>
      </div>

      <div class="copyright">© 2024 智能家居控制系统</div>
    </div>
  </div>
</template>

<script>
import axios from 'axios'
export default {
  data: function() { return { snapshot: null, error: '' } },
  mounted: function() {
    var self = this
    axios.get('/api/share/' + this.$route.params.uuid).then(function(r) {
      if (r.data.success) self.snapshot = r.data.data
      else self.error = r.data.message
    }).catch(function(e) {
      self.error = (e.response && e.response.data && e.response.data.message) || '链接无效或已过期'
    })
  }
}
</script>

<style scoped>
.share-page {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  padding: 20px;
  position: relative;
}
.share-container { width: 100%; max-width: 500px; }
.loading { text-align: center; color: white; }
.spinner { width: 56px; height: 56px; border: 4px solid rgba(255,255,255,0.2); border-top-color: white; border-radius: 50%; animation: spin 1s linear infinite; margin: 0 auto 20px; }
@keyframes spin { to { transform: rotate(360deg); } }
.loading p { font-size: 16px; }
.error-card { background: white; border-radius: 28px; padding: 60px 40px; text-align: center; box-shadow: 0 25px 50px rgba(0,0,0,0.15); }
.error-card .mdi { font-size: 72px; color: #EF4444; }
.error-card h3 { font-size: 22px; color: #1E293B; margin: 20px 0 10px; font-weight: 700; }
.error-card p { color: #64748B; font-size: 15px; }
.snapshot-card { background: white; border-radius: 28px; overflow: hidden; box-shadow: 0 25px 50px rgba(0,0,0,0.15); }
.card-header { background: linear-gradient(135deg, #6366f1 0%, #8b5cf6 100%); padding: 40px; text-align: center; color: white; }
.card-header .mdi { font-size: 48px; margin-bottom: 16px; display: block; }
.card-header h2 { font-size: 24px; margin: 0; font-weight: 700; }
.card-body { padding: 32px; }
.info-item { display: flex; align-items: center; gap: 18px; padding: 18px 0; border-bottom: 1px solid #F1F5F9; }
.info-item:last-child { border-bottom: none; }
.info-item > .mdi { font-size: 26px; color: #6366f1; width: 36px; text-align: center; }
.info-content { flex: 1; }
.label { display: block; font-size: 12px; color: #94A3B8; margin-bottom: 6px; text-transform: uppercase; letter-spacing: 0.5px; font-weight: 600; }
.value { font-size: 17px; font-weight: 600; color: #1E293B; }
.value.highlight { font-size: 28px; color: #6366f1; font-weight: 800; }
.value.small { font-size: 14px; font-weight: 500; }
.status-badge { display: inline-block; padding: 6px 14px; border-radius: 8px; font-size: 13px; font-weight: 600; }
.status-badge.online, .status-badge.on { background: linear-gradient(135deg, #D1FAE5 0%, #A7F3D0 100%); color: #059669; }
.status-badge.offline, .status-badge.off { background: #F1F5F9; color: #64748B; }
.card-footer { padding: 24px 32px; border-top: 1px solid #F1F5F9; }
.home-btn { display: flex; align-items: center; justify-content: center; gap: 10px; padding: 16px; background: linear-gradient(135deg, #f8fafc 0%, #f1f5f9 100%); border: 2px solid #E2E8F0; border-radius: 14px; color: #6366f1; text-decoration: none; font-weight: 600; transition: all 0.2s; }
.home-btn:hover { background: linear-gradient(135deg, #f5f3ff 0%, #ede9fe 100%); border-color: #6366f1; }
.copyright { text-align: center; margin-top: 28px; color: rgba(255,255,255,0.7); font-size: 13px; }
</style>
