<template>
  <div class="feedback-page">
    <div class="page-toolbar">
      <button class="add-btn" @click="showDialog = true"><span class="mdi mdi-plus"></span> 提交反馈</button>
    </div>

    <div v-if="feedbacks.length === 0" class="empty-card">
      <span class="mdi mdi-message-text-outline"></span>
      <h3>暂无反馈</h3>
      <p>有任何建议或问题，欢迎提交反馈</p>
    </div>

    <div v-else class="feedback-grid">
      <div v-for="fb in feedbacks" :key="fb.id" class="feedback-card">
        <div class="feedback-header">
          <h4>{{ fb.title }}</h4>
          <span class="status-tag" :class="fb.status === 'PENDING' ? 'pending' : 'done'">{{ fb.status === 'PENDING' ? '待处理' : '已回复' }}</span>
        </div>
        <div class="feedback-time">{{ formatDate(fb.createdAt) }}</div>
        <div class="feedback-content">{{ fb.content }}</div>
        <div v-if="fb.adminReply" class="reply-box">
          <div class="reply-label"><span class="mdi mdi-reply"></span> 管理员回复</div>
          <div class="reply-content">{{ fb.adminReply }}</div>
        </div>
      </div>
    </div>

    <v-dialog v-model="showDialog" max-width="450">
      <div class="dialog-box">
        <div class="dialog-header"><h3>提交反馈</h3><button class="close-btn" @click="showDialog = false"><span class="mdi mdi-close"></span></button></div>
        <div class="dialog-body">
          <div class="field"><label>标题</label><input v-model="newFeedback.title" type="text" placeholder="简要描述"></div>
          <div class="field"><label>内容</label><textarea v-model="newFeedback.content" rows="4" placeholder="详细描述..."></textarea></div>
        </div>
        <div class="dialog-footer"><button class="btn-cancel" @click="showDialog = false">取消</button><button class="btn-confirm" :disabled="!newFeedback.title || !newFeedback.content" @click="submitFeedback">提交</button></div>
      </div>
    </v-dialog>

    <v-snackbar v-model="snackbar.show" :color="snackbar.color" timeout="2000">{{ snackbar.text }}</v-snackbar>
  </div>
</template>

<script>
export default {
  data: function() { return { feedbacks: [], showDialog: false, newFeedback: { title: '', content: '' }, snackbar: { show: false, text: '', color: 'success' } } },
  mounted: function() { this.loadFeedbacks() },
  methods: {
    loadFeedbacks: function() { var self = this; this.$axios.get('/api/feedbacks').then(function(r) { if (r.data.success) self.feedbacks = r.data.data }) },
    submitFeedback: function() { var self = this; this.$axios.post('/api/feedbacks', this.newFeedback).then(function(r) { if (r.data.success) { self.showDialog = false; self.newFeedback = { title: '', content: '' }; self.loadFeedbacks(); self.showMsg('提交成功', 'success') } }) },
    formatDate: function(d) { return d ? new Date(d).toLocaleString('zh-CN') : '-' },
    showMsg: function(t, c) { this.snackbar = { show: true, text: t, color: c } }
  }
}
</script>

<style scoped>
.feedback-page { padding: 0 32px 32px; max-width: 1000px; margin: 0 auto; }
.page-toolbar { display: flex; justify-content: flex-end; margin-bottom: 20px; }
.add-btn { display: flex; align-items: center; gap: 6px; padding: 10px 20px; background: linear-gradient(135deg, #667eea 0%, #764ba2 100%); border: none; border-radius: 8px; color: #fff; font-size: 14px; cursor: pointer; }

.empty-card { background: #fff; border-radius: 12px; padding: 60px; text-align: center; }
.empty-card .mdi { font-size: 60px; color: #ddd; }
.empty-card h3 { font-size: 18px; color: #333; margin: 16px 0 8px; }
.empty-card p { color: #888; }

.feedback-grid { display: grid; grid-template-columns: repeat(auto-fill, minmax(380px, 1fr)); gap: 20px; }
.feedback-card { background: #fff; border-radius: 12px; padding: 20px; }
.feedback-header { display: flex; justify-content: space-between; align-items: flex-start; margin-bottom: 8px; }
.feedback-header h4 { font-size: 16px; color: #333; margin: 0; }
.status-tag { padding: 3px 10px; border-radius: 4px; font-size: 12px; }
.status-tag.pending { background: #fff3e0; color: #ef6c00; }
.status-tag.done { background: #e8f5e9; color: #2e7d32; }
.feedback-time { font-size: 12px; color: #888; margin-bottom: 12px; }
.feedback-content { color: #666; line-height: 1.6; margin-bottom: 14px; }
.reply-box { background: #e8f5e9; border-left: 3px solid #4caf50; border-radius: 8px; padding: 14px; }
.reply-label { font-size: 12px; color: #2e7d32; margin-bottom: 8px; display: flex; align-items: center; gap: 4px; }
.reply-content { color: #1b5e20; }

.dialog-box { background: #fff; border-radius: 12px; overflow: hidden; }
.dialog-header { display: flex; justify-content: space-between; align-items: center; padding: 18px 20px; border-bottom: 1px solid #f0f0f0; }
.dialog-header h3 { font-size: 16px; color: #333; margin: 0; }
.close-btn { width: 30px; height: 30px; border: none; background: #f5f5f5; border-radius: 6px; color: #666; cursor: pointer; }
.dialog-body { padding: 20px; }
.field { margin-bottom: 16px; }
.field label { display: block; font-size: 13px; color: #333; margin-bottom: 6px; }
.field input, .field textarea { width: 100%; padding: 10px 12px; border: 1px solid #ddd; border-radius: 8px; font-size: 14px; font-family: inherit; resize: vertical; }
.field input:focus, .field textarea:focus { border-color: #667eea; outline: none; }
.dialog-footer { display: flex; justify-content: flex-end; gap: 10px; padding: 16px 20px; border-top: 1px solid #f0f0f0; }
.btn-cancel { padding: 10px 20px; background: #f5f5f5; border: none; border-radius: 8px; color: #666; font-size: 14px; cursor: pointer; }
.btn-confirm { padding: 10px 20px; background: linear-gradient(135deg, #667eea 0%, #764ba2 100%); border: none; border-radius: 8px; color: #fff; font-size: 14px; cursor: pointer; }
.btn-confirm:disabled { opacity: 0.5; }
</style>
