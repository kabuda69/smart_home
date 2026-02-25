<template>
  <div class="scenes-page">
    <div class="page-toolbar">
      <button class="add-btn" @click="openCreateDialog">
        <span class="mdi mdi-plus"></span> 创建场景
      </button>
    </div>

    <!-- 加载中 -->
    <div v-if="loading" class="loading-card">
      <div class="loading-spinner"></div>
      <p>加载中...</p>
    </div>

    <!-- 空状态 -->
    <div v-else-if="scenes.length === 0" class="empty-card">
      <span class="mdi mdi-palette"></span>
      <h3>暂无场景</h3>
      <p>创建场景来一键控制多个设备</p>
    </div>

    <!-- 场景列表 -->
    <div v-else class="scenes-grid">
      <div v-for="scene in scenes" :key="scene.id" class="scene-card" :class="{ active: scene.isActive }">
        <div class="scene-header">
          <div class="scene-icon" :class="{ active: scene.isActive }">
            <span class="mdi" :class="scene.isActive ? 'mdi-check' : 'mdi-home'"></span>
          </div>
          <div class="scene-info">
            <h4>{{ scene.name }}</h4>
            <span v-if="scene.isActive" class="active-badge">当前激活</span>
          </div>
          <div class="scene-menu">
            <button @click="openEditDialog(scene)"><span class="mdi mdi-pencil"></span></button>
            <button class="danger" @click="deleteScene(scene)"><span class="mdi mdi-delete"></span></button>
          </div>
        </div>
        <p v-if="scene.description" class="scene-desc">{{ scene.description }}</p>
        <div class="scene-actions">
          <div class="actions-title">场景动作</div>
          <div v-if="scene.actions && scene.actions.length > 0" class="actions-list">
            <div v-for="action in scene.actions" :key="action.id" class="action-row">
              <span class="action-device">{{ action.deviceName }}</span>
              <span class="action-type" :class="getActionClass(action.actionType)">{{ formatAction(action) }}</span>
            </div>
          </div>
          <div v-else class="no-actions">暂无动作</div>
        </div>
        <button class="activate-btn" :class="{ active: scene.isActive }" :disabled="scene.isActive" @click="activateScene(scene)">
          <span class="mdi" :class="scene.isActive ? 'mdi-check-circle' : 'mdi-play'"></span>
          {{ scene.isActive ? '已激活' : '激活场景' }}
        </button>
      </div>
    </div>

    <v-dialog v-model="showDialog" max-width="480" persistent>
      <div class="dialog-box">
        <div class="dialog-header"><h3>{{ editingScene.id ? '编辑场景' : '创建场景' }}</h3><button class="close-btn" @click="closeDialog"><span class="mdi mdi-close"></span></button></div>
        <div class="dialog-body">
          <div class="field"><label>场景名称</label><input v-model="editingScene.name" type="text" placeholder="如: 回家模式"></div>
          <div class="field"><label>描述</label><input v-model="editingScene.description" type="text" placeholder="可选"></div>
          <div class="actions-section">
            <div class="section-title"><span>场景动作</span><button class="add-action" @click="addAction" :disabled="devices.length === 0"><span class="mdi mdi-plus"></span> 添加</button></div>
            <div v-for="(action, idx) in editingScene.actions" :key="idx" class="action-edit-row">
              <select v-model="action.deviceId"><option v-for="d in devices" :key="d.id" :value="d.id">{{ d.name }}</option></select>
              <select v-model="action.actionType"><option value="POWER_ON">开启</option><option value="POWER_OFF">关闭</option><option value="SET_VALUE">设值</option></select>
              <input v-if="action.actionType === 'SET_VALUE'" v-model="action.actionValue" type="number" placeholder="值" class="value-input">
              <button class="remove-action" @click="removeAction(idx)"><span class="mdi mdi-close"></span></button>
            </div>
          </div>
        </div>
        <div class="dialog-footer"><button class="btn-cancel" @click="closeDialog">取消</button><button class="btn-confirm" :disabled="!editingScene.name" @click="saveScene">保存</button></div>
      </div>
    </v-dialog>

    <v-snackbar v-model="snackbar.show" :color="snackbar.color" timeout="2000">{{ snackbar.text }}</v-snackbar>
  </div>
</template>

<script>
import { mapState } from 'vuex'
export default {
  data: function() {
    return {
      scenes: [],
      showDialog: false,
      editingScene: { name: '', description: '', actions: [] },
      snackbar: { show: false, text: '', color: 'success' },
      loading: true
    }
  },
  computed: {
    ...mapState(['devices'])
  },
  mounted: function() {
    this.$store.dispatch('fetchDevices')
    this.loadScenes()
  },
  methods: {
    loadScenes: function() {
      var self = this
      self.loading = true
      this.$axios.get('/api/scenes').then(function(r) {
        console.log('Scenes response:', r.data)
        if (r.data.success) {
          self.scenes = r.data.data || []
        }
        self.loading = false
      }).catch(function(err) {
        console.error('Load scenes error:', err)
        self.loading = false
        self.showMsg('加载场景失败', 'error')
      })
    },
    formatAction: function(a) {
      var map = { 'POWER_ON': '开启', 'POWER_OFF': '关闭', 'SET_VALUE': '设为 ' + a.actionValue }
      return map[a.actionType] || a.actionType
    },
    getActionClass: function(t) {
      var map = { 'POWER_ON': 'green', 'POWER_OFF': 'gray', 'SET_VALUE': 'blue' }
      return map[t] || 'gray'
    },
    openCreateDialog: function() {
      this.editingScene = { name: '', description: '', actions: [] }
      this.showDialog = true
    },
    openEditDialog: function(s) {
      var actions = []
      if (s.actions && s.actions.length > 0) {
        actions = s.actions.map(function(a) {
          return { deviceId: a.deviceId, actionType: a.actionType, actionValue: a.actionValue }
        })
      }
      this.editingScene = { id: s.id, name: s.name, description: s.description, actions: actions }
      this.showDialog = true
    },
    closeDialog: function() {
      this.showDialog = false
    },
    addAction: function() {
      var firstDeviceId = this.devices.length > 0 ? this.devices[0].id : null
      this.editingScene.actions.push({ deviceId: firstDeviceId, actionType: 'POWER_ON', actionValue: '' })
    },
    removeAction: function(i) {
      this.editingScene.actions.splice(i, 1)
    },
    saveScene: function() {
      var self = this
      var data = {
        name: this.editingScene.name,
        description: this.editingScene.description,
        actions: this.editingScene.actions.filter(function(a) { return a.deviceId })
      }
      var promise
      if (this.editingScene.id) {
        promise = this.$axios.put('/api/scenes/' + this.editingScene.id, data)
      } else {
        promise = this.$axios.post('/api/scenes', data)
      }
      promise.then(function(r) {
        if (r.data.success) {
          self.closeDialog()
          self.loadScenes()
          self.showMsg('已保存', 'success')
        }
      }).catch(function(err) {
        console.error('Save scene error:', err)
        self.showMsg('保存失败', 'error')
      })
    },
    activateScene: function(s) {
      var self = this
      if (s.isActive) return
      this.$axios.post('/api/scenes/' + s.id + '/activate').then(function(r) {
        if (r.data.success) {
          self.loadScenes()
          self.$store.dispatch('fetchDevices')
          self.showMsg('已激活: ' + s.name, 'success')
        }
      }).catch(function(err) {
        console.error('Activate scene error:', err)
        self.showMsg('激活失败', 'error')
      })
    },
    deleteScene: function(s) {
      var self = this
      if (!confirm('确定删除场景「' + s.name + '」？')) return
      this.$axios.delete('/api/scenes/' + s.id).then(function() {
        self.loadScenes()
        self.showMsg('已删除', 'success')
      }).catch(function(err) {
        console.error('Delete scene error:', err)
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
.scenes-page { padding: 0 32px 32px; max-width: 1200px; margin: 0 auto; }
.page-toolbar { display: flex; justify-content: flex-end; margin-bottom: 20px; }
.add-btn { display: flex; align-items: center; gap: 6px; padding: 10px 20px; background: linear-gradient(135deg, #667eea 0%, #764ba2 100%); border: none; border-radius: 8px; color: #fff; font-size: 14px; cursor: pointer; }

.loading-card { background: #fff; border-radius: 12px; padding: 60px; text-align: center; }
.loading-spinner { width: 40px; height: 40px; border: 3px solid #f0f0f0; border-top-color: #667eea; border-radius: 50%; animation: spin 1s linear infinite; margin: 0 auto 16px; }
@keyframes spin { to { transform: rotate(360deg); } }
.loading-card p { color: #888; }

.empty-card { background: #fff; border-radius: 12px; padding: 60px; text-align: center; }
.empty-card .mdi { font-size: 60px; color: #ddd; }
.empty-card h3 { font-size: 18px; color: #333; margin: 16px 0 8px; }
.empty-card p { color: #888; }

.scenes-grid { display: grid; grid-template-columns: repeat(auto-fill, minmax(340px, 1fr)); gap: 20px; }
.scene-card { background: #fff; border-radius: 12px; padding: 20px; border: 2px solid transparent; transition: all 0.2s; }
.scene-card:hover { box-shadow: 0 4px 20px rgba(0,0,0,0.08); }
.scene-card.active { border-color: #667eea; background: linear-gradient(180deg, #f5f3ff 0%, #fff 100%); }

.scene-header { display: flex; align-items: center; gap: 12px; margin-bottom: 12px; }
.scene-icon { width: 44px; height: 44px; background: #f5f5f5; border-radius: 10px; display: flex; align-items: center; justify-content: center; }
.scene-icon .mdi { font-size: 22px; color: #666; }
.scene-icon.active { background: linear-gradient(135deg, #667eea 0%, #764ba2 100%); }
.scene-icon.active .mdi { color: #fff; }
.scene-info { flex: 1; }
.scene-info h4 { font-size: 16px; color: #333; margin-bottom: 4px; }
.active-badge { display: inline-block; padding: 2px 8px; background: #667eea; color: #fff; border-radius: 4px; font-size: 11px; }
.scene-menu { display: flex; gap: 4px; }
.scene-menu button { width: 30px; height: 30px; border: none; background: #f5f5f5; border-radius: 6px; color: #666; cursor: pointer; }
.scene-menu button:hover { background: #667eea; color: #fff; }
.scene-menu button.danger:hover { background: #e53935; }

.scene-desc { color: #888; font-size: 14px; margin-bottom: 14px; }
.scene-actions { background: #f8f9fa; border-radius: 10px; padding: 14px; margin-bottom: 14px; }
.actions-title { font-size: 12px; color: #888; margin-bottom: 10px; text-transform: uppercase; }
.action-row { display: flex; justify-content: space-between; align-items: center; padding: 8px 0; border-bottom: 1px solid #eee; }
.action-row:last-child { border-bottom: none; }
.action-device { font-size: 14px; color: #333; }
.action-type { padding: 3px 10px; border-radius: 4px; font-size: 12px; }
.action-type.green { background: #e8f5e9; color: #2e7d32; }
.action-type.gray { background: #f5f5f5; color: #666; }
.action-type.blue { background: #e3f2fd; color: #1976d2; }
.no-actions { color: #888; font-size: 14px; text-align: center; padding: 10px; }

.activate-btn { width: 100%; padding: 12px; border: none; border-radius: 8px; font-size: 14px; cursor: pointer; display: flex; align-items: center; justify-content: center; gap: 8px; background: #f5f5f5; color: #555; transition: all 0.2s; }
.activate-btn:hover:not(:disabled) { background: linear-gradient(135deg, #667eea 0%, #764ba2 100%); color: #fff; }
.activate-btn.active { background: #e0e0e0; color: #999; cursor: default; }

.dialog-box { background: #fff; border-radius: 12px; overflow: hidden; }
.dialog-header { display: flex; justify-content: space-between; align-items: center; padding: 18px 20px; border-bottom: 1px solid #f0f0f0; }
.dialog-header h3 { font-size: 16px; color: #333; margin: 0; }
.close-btn { width: 30px; height: 30px; border: none; background: #f5f5f5; border-radius: 6px; color: #666; cursor: pointer; }
.dialog-body { padding: 20px; max-height: 60vh; overflow-y: auto; }
.field { margin-bottom: 16px; }
.field label { display: block; font-size: 13px; color: #333; margin-bottom: 6px; }
.field input { width: 100%; padding: 10px 12px; border: 1px solid #ddd; border-radius: 8px; font-size: 14px; }
.field input:focus { border-color: #667eea; outline: none; }
.actions-section { margin-top: 20px; padding-top: 20px; border-top: 1px solid #eee; }
.section-title { display: flex; justify-content: space-between; align-items: center; margin-bottom: 14px; font-size: 14px; color: #333; }
.add-action { display: flex; align-items: center; gap: 4px; padding: 6px 12px; background: #e3f2fd; border: none; border-radius: 6px; color: #1976d2; font-size: 12px; cursor: pointer; }
.action-edit-row { display: flex; gap: 8px; margin-bottom: 8px; align-items: center; }
.action-edit-row select { flex: 1; padding: 8px; border: 1px solid #ddd; border-radius: 6px; font-size: 13px; }
.value-input { width: 60px; padding: 8px; border: 1px solid #ddd; border-radius: 6px; font-size: 13px; }
.remove-action { width: 30px; height: 30px; border: none; background: #ffebee; border-radius: 6px; color: #c62828; cursor: pointer; }
.dialog-footer { display: flex; justify-content: flex-end; gap: 10px; padding: 16px 20px; border-top: 1px solid #f0f0f0; }
.btn-cancel { padding: 10px 20px; background: #f5f5f5; border: none; border-radius: 8px; color: #666; font-size: 14px; cursor: pointer; }
.btn-confirm { padding: 10px 20px; background: linear-gradient(135deg, #667eea 0%, #764ba2 100%); border: none; border-radius: 8px; color: #fff; font-size: 14px; cursor: pointer; }
.btn-confirm:disabled { opacity: 0.5; }
</style>
