<template>
  <div class="admin-page">
    <div class="tabs-card">
      <div class="tabs">
        <button v-for="(t, i) in tabs" :key="i" class="tab" :class="{ active: tab === i }" @click="tab = i">
          <span :class="'mdi ' + t.icon"></span> {{ t.name }}
        </button>
      </div>

      <div class="tab-content">
        <div v-if="tab === 0">
          <table class="data-table">
            <thead><tr><th>ID</th><th>用户名</th><th>邮箱</th><th>角色</th><th>状态</th><th>操作</th></tr></thead>
            <tbody>
              <tr v-for="u in users" :key="u.id">
                <td>{{ u.id }}</td><td>{{ u.username }}</td><td>{{ u.email || '-' }}</td>
                <td><span class="tag" :class="u.role === 'ADMIN' ? 'admin' : 'user'">{{ u.role === 'ADMIN' ? '管理员' : '用户' }}</span></td>
                <td><span class="tag" :class="u.enabled ? 'on' : 'off'">{{ u.enabled ? '启用' : '禁用' }}</span></td>
                <td><button v-if="u.role !== 'ADMIN'" class="action-btn" :class="u.enabled ? 'danger' : 'success'" @click="toggleUser(u)"><span class="mdi" :class="u.enabled ? 'mdi-account-off' : 'mdi-account-check'"></span></button></td>
              </tr>
            </tbody>
          </table>
        </div>

        <div v-if="tab === 1">
          <div class="panel-header"><button class="add-btn" @click="showTypeDialog = true"><span class="mdi mdi-plus"></span> 添加类型</button></div>
          <table class="data-table">
            <thead><tr><th>ID</th><th>图标</th><th>名称</th><th>描述</th><th>操作</th></tr></thead>
            <tbody>
              <tr v-for="t in deviceTypes" :key="t.id">
                <td>{{ t.id }}</td><td><span :class="'mdi ' + t.icon" style="font-size: 20px; color: #667eea;"></span></td><td>{{ t.name }}</td><td>{{ t.description || '-' }}</td>
                <td><button class="action-btn" @click="editType(t)"><span class="mdi mdi-pencil"></span></button><button class="action-btn danger" @click="deleteType(t.id)"><span class="mdi mdi-delete"></span></button></td>
              </tr>
            </tbody>
          </table>
        </div>

        <div v-if="tab === 2">
          <table class="data-table">
            <thead><tr><th>用户</th><th>标题</th><th>状态</th><th>操作</th></tr></thead>
            <tbody>
              <tr v-for="f in feedbacks" :key="f.id">
                <td>{{ f.username }}</td><td>{{ f.title }}</td>
                <td><span class="tag" :class="f.status === 'PENDING' ? 'pending' : 'done'">{{ f.status === 'PENDING' ? '待处理' : '已回复' }}</span></td>
                <td><button class="action-btn" @click="openReplyDialog(f)"><span class="mdi mdi-reply"></span></button></td>
              </tr>
            </tbody>
          </table>
        </div>

        <div v-if="tab === 3">
          <table class="data-table">
            <thead><tr><th>用户</th><th>操作</th><th>详情</th><th>IP</th><th>时间</th></tr></thead>
            <tbody>
              <tr v-for="l in logs" :key="l.id">
                <td>{{ l.username || '-' }}</td><td><span class="tag default">{{ l.operation }}</span></td><td>{{ l.details || '-' }}</td><td>{{ l.ipAddress || '-' }}</td><td class="time-cell">{{ formatDate(l.createdAt) }}</td>
              </tr>
            </tbody>
          </table>
        </div>
      </div>
    </div>

    <v-dialog v-model="showTypeDialog" max-width="450">
      <div class="dialog-box">
        <div class="dialog-header"><h3>{{ editingType.id ? '编辑' : '添加' }}设备类型</h3><button class="close-btn" @click="showTypeDialog = false"><span class="mdi mdi-close"></span></button></div>
        <div class="dialog-body">
          <div class="field"><label>名称</label><input v-model="editingType.name" type="text"></div>
          <div class="field"><label>描述</label><input v-model="editingType.description" type="text"></div>
          <div class="field"><label>图标</label><input v-model="editingType.icon" type="text" placeholder="mdi-lightbulb"></div>
          <div class="field"><label>参数模板</label><textarea v-model="editingType.paramTemplate" rows="3"></textarea></div>
        </div>
        <div class="dialog-footer"><button class="btn-cancel" @click="showTypeDialog = false">取消</button><button class="btn-confirm" @click="saveType">保存</button></div>
      </div>
    </v-dialog>

    <v-dialog v-model="showReplyDialog" max-width="450">
      <div class="dialog-box">
        <div class="dialog-header"><h3>回复反馈</h3><button class="close-btn" @click="showReplyDialog = false"><span class="mdi mdi-close"></span></button></div>
        <div class="dialog-body">
          <div class="preview-box"><div class="preview-title">{{ replyingFeedback ? replyingFeedback.title : '' }}</div><div class="preview-content">{{ replyingFeedback ? replyingFeedback.content : '' }}</div></div>
          <div class="field"><label>回复</label><textarea v-model="replyContent" rows="3"></textarea></div>
        </div>
        <div class="dialog-footer"><button class="btn-cancel" @click="showReplyDialog = false">取消</button><button class="btn-confirm" @click="submitReply">回复</button></div>
      </div>
    </v-dialog>

    <v-snackbar v-model="snackbar.show" :color="snackbar.color" timeout="2000">{{ snackbar.text }}</v-snackbar>
  </div>
</template>

<script>
export default {
  data: function() {
    return {
      tab: 0, users: [], deviceTypes: [], feedbacks: [], logs: [],
      showTypeDialog: false, showReplyDialog: false,
      editingType: { name: '', description: '', icon: '', paramTemplate: '{}' },
      replyingFeedback: null, replyContent: '',
      snackbar: { show: false, text: '', color: 'success' },
      tabs: [{ name: '用户', icon: 'mdi-account-group' }, { name: '设备类型', icon: 'mdi-shape' }, { name: '反馈', icon: 'mdi-message-text' }, { name: '日志', icon: 'mdi-file-document' }]
    }
  },
  watch: { tab: function() { this.loadTabData() } },
  mounted: function() { this.loadTabData() },
  methods: {
    loadTabData: function() { if (this.tab === 0) this.loadUsers(); else if (this.tab === 1) this.loadTypes(); else if (this.tab === 2) this.loadFeedbacks(); else this.loadLogs() },
    loadUsers: function() { var self = this; this.$axios.get('/api/admin/users').then(function(r) { if (r.data.success) self.users = r.data.data }) },
    loadTypes: function() { var self = this; this.$axios.get('/api/admin/device-types').then(function(r) { if (r.data.success) self.deviceTypes = r.data.data }) },
    loadFeedbacks: function() { var self = this; this.$axios.get('/api/admin/feedbacks', { params: { page: 0, size: 20 } }).then(function(r) { if (r.data.success) self.feedbacks = r.data.data.content }) },
    loadLogs: function() { var self = this; this.$axios.get('/api/admin/logs', { params: { page: 0, size: 20 } }).then(function(r) { if (r.data.success) self.logs = r.data.data.content }) },
    toggleUser: function(u) { var self = this; this.$axios.put('/api/admin/users/' + u.id + '/status', { enabled: !u.enabled }).then(function() { self.loadUsers(); self.showMsg(u.enabled ? '已禁用' : '已启用', 'success') }) },
    editType: function(t) { this.editingType = Object.assign({}, t); this.showTypeDialog = true },
    saveType: function() { var self = this; var p = this.editingType.id ? this.$axios.put('/api/admin/device-types/' + this.editingType.id, this.editingType) : this.$axios.post('/api/admin/device-types', this.editingType); p.then(function() { self.showTypeDialog = false; self.editingType = { name: '', description: '', icon: '', paramTemplate: '{}' }; self.loadTypes(); self.showMsg('已保存', 'success') }) },
    deleteType: function(id) { var self = this; if (confirm('确定删除？')) this.$axios.delete('/api/admin/device-types/' + id).then(function() { self.loadTypes(); self.showMsg('已删除', 'success') }) },
    openReplyDialog: function(f) { this.replyingFeedback = f; this.replyContent = ''; this.showReplyDialog = true },
    submitReply: function() { var self = this; this.$axios.put('/api/admin/feedbacks/' + this.replyingFeedback.id + '/reply', { reply: this.replyContent }).then(function() { self.showReplyDialog = false; self.loadFeedbacks(); self.showMsg('已回复', 'success') }) },
    formatDate: function(d) { return d ? new Date(d).toLocaleString('zh-CN') : '-' },
    showMsg: function(t, c) { this.snackbar = { show: true, text: t, color: c } }
  }
}
</script>

<style scoped>
.admin-page { padding: 0 32px 32px; max-width: 1200px; margin: 0 auto; }
.tabs-card { background: #fff; border-radius: 12px; overflow: hidden; }
.tabs { display: flex; border-bottom: 1px solid #eee; }
.tab { flex: 1; padding: 14px; background: none; border: none; font-size: 14px; color: #666; cursor: pointer; display: flex; align-items: center; justify-content: center; gap: 8px; }
.tab:hover { background: #f8f9fa; }
.tab.active { color: #667eea; border-bottom: 2px solid #667eea; background: #f5f3ff; }
.tab .mdi { font-size: 18px; }
.tab-content { padding: 20px; }
.panel-header { display: flex; justify-content: flex-end; margin-bottom: 14px; }
.add-btn { display: flex; align-items: center; gap: 6px; padding: 8px 16px; background: #667eea; border: none; border-radius: 8px; color: #fff; font-size: 13px; cursor: pointer; }

.data-table { width: 100%; border-collapse: collapse; }
.data-table th { text-align: left; padding: 10px; background: #f5f5f5; color: #666; font-size: 13px; }
.data-table td { padding: 10px; border-bottom: 1px solid #f0f0f0; font-size: 14px; }
.time-cell { color: #888; font-size: 13px; }
.tag { padding: 3px 8px; border-radius: 4px; font-size: 12px; }
.tag.admin { background: #e3f2fd; color: #1976d2; }
.tag.user { background: #f5f5f5; color: #666; }
.tag.on { background: #e8f5e9; color: #2e7d32; }
.tag.off { background: #ffebee; color: #c62828; }
.tag.pending { background: #fff3e0; color: #ef6c00; }
.tag.done { background: #e8f5e9; color: #2e7d32; }
.tag.default { background: #f5f5f5; color: #666; }
.action-btn { width: 30px; height: 30px; border: none; background: #f5f5f5; border-radius: 6px; color: #666; cursor: pointer; margin-right: 4px; }
.action-btn:hover { background: #667eea; color: #fff; }
.action-btn.danger:hover { background: #e53935; }
.action-btn.success:hover { background: #4caf50; }

.dialog-box { background: #fff; border-radius: 12px; overflow: hidden; }
.dialog-header { display: flex; justify-content: space-between; align-items: center; padding: 18px 20px; border-bottom: 1px solid #f0f0f0; }
.dialog-header h3 { font-size: 16px; color: #333; margin: 0; }
.close-btn { width: 30px; height: 30px; border: none; background: #f5f5f5; border-radius: 6px; color: #666; cursor: pointer; }
.dialog-body { padding: 20px; }
.field { margin-bottom: 16px; }
.field label { display: block; font-size: 13px; color: #333; margin-bottom: 6px; }
.field input, .field textarea { width: 100%; padding: 10px 12px; border: 1px solid #ddd; border-radius: 8px; font-size: 14px; font-family: inherit; }
.field input:focus, .field textarea:focus { border-color: #667eea; outline: none; }
.preview-box { background: #f5f5f5; border-radius: 8px; padding: 14px; margin-bottom: 16px; }
.preview-title { font-weight: 600; color: #333; margin-bottom: 8px; }
.preview-content { color: #666; font-size: 14px; }
.dialog-footer { display: flex; justify-content: flex-end; gap: 10px; padding: 16px 20px; border-top: 1px solid #f0f0f0; }
.btn-cancel { padding: 10px 20px; background: #f5f5f5; border: none; border-radius: 8px; color: #666; font-size: 14px; cursor: pointer; }
.btn-confirm { padding: 10px 20px; background: linear-gradient(135deg, #667eea 0%, #764ba2 100%); border: none; border-radius: 8px; color: #fff; font-size: 14px; cursor: pointer; }
</style>
