<template>
  <div class="register-page">
    <div class="register-box">
      <div class="form-header">
        <div class="header-icon">
          <span class="mdi mdi-account-plus"></span>
        </div>
        <h2>创建账号</h2>
        <p>加入智能家居，开启智慧生活</p>
      </div>

      <form @submit.prevent="register">
        <div class="form-item">
          <label>用户名</label>
          <div class="input-box">
            <span class="mdi mdi-account"></span>
            <input v-model="form.username" type="text" placeholder="至少3个字符" required minlength="3">
          </div>
        </div>

        <div class="form-item">
          <label>密码</label>
          <div class="input-box">
            <span class="mdi mdi-lock"></span>
            <input v-model="form.password" type="password" placeholder="至少6个字符" required minlength="6">
          </div>
        </div>

        <div class="form-item">
          <label>邮箱 <span class="optional">(可选)</span></label>
          <div class="input-box">
            <span class="mdi mdi-email"></span>
            <input v-model="form.email" type="email" placeholder="your@email.com">
          </div>
        </div>

        <div v-if="error" class="error-msg">
          <span class="mdi mdi-alert-circle"></span> {{ error }}
        </div>

        <button type="submit" class="submit-btn" :disabled="loading">
          <span v-if="loading" class="mdi mdi-loading mdi-spin"></span>
          <span v-else>注 册</span>
        </button>
      </form>

      <div class="form-footer">
        已有账号？<router-link to="/login">立即登录</router-link>
      </div>
    </div>
  </div>
</template>

<script>
export default {
  data: function() {
    return { loading: false, error: '', form: { username: '', password: '', email: '' } }
  },
  methods: {
    register: function() {
      var self = this
      this.loading = true
      this.error = ''
      this.$store.dispatch('register', this.form).then(function(res) {
        if (res.success) self.$router.push('/dashboard')
        else self.error = res.message
        self.loading = false
      }).catch(function(e) {
        self.error = (e.response && e.response.data && e.response.data.message) || '注册失败'
        self.loading = false
      })
    }
  }
}
</script>

<style scoped>
.register-page {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  padding: 20px;
}

.register-box {
  width: 100%;
  max-width: 400px;
  background: #fff;
  border-radius: 16px;
  padding: 40px;
  box-shadow: 0 20px 60px rgba(0,0,0,0.2);
}

.form-header {
  text-align: center;
  margin-bottom: 30px;
}

.header-icon {
  width: 64px;
  height: 64px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border-radius: 16px;
  display: flex;
  align-items: center;
  justify-content: center;
  margin: 0 auto 16px;
}

.header-icon .mdi {
  font-size: 32px;
  color: #fff;
}

.form-header h2 {
  font-size: 24px;
  color: #1a1a1a;
  margin-bottom: 8px;
}

.form-header p {
  color: #888;
  font-size: 14px;
}

.form-item {
  margin-bottom: 20px;
}

.form-item label {
  display: block;
  font-size: 14px;
  color: #333;
  margin-bottom: 8px;
  font-weight: 500;
}

.optional {
  color: #999;
  font-weight: 400;
}

.input-box {
  display: flex;
  align-items: center;
  background: #f5f5f5;
  border: 2px solid #eee;
  border-radius: 10px;
  padding: 0 14px;
  transition: all 0.2s;
}

.input-box:focus-within {
  border-color: #667eea;
  background: #fff;
}

.input-box .mdi {
  font-size: 20px;
  color: #999;
}

.input-box input {
  flex: 1;
  border: none;
  background: transparent;
  padding: 14px 12px;
  font-size: 14px;
  outline: none;
}

.error-msg {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 12px 14px;
  background: #ffebee;
  border-radius: 8px;
  color: #c62828;
  font-size: 13px;
  margin-bottom: 20px;
}

.submit-btn {
  width: 100%;
  padding: 14px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border: none;
  border-radius: 10px;
  color: #fff;
  font-size: 15px;
  font-weight: 500;
  cursor: pointer;
}

.submit-btn:disabled {
  opacity: 0.7;
}

.form-footer {
  text-align: center;
  margin-top: 24px;
  color: #888;
  font-size: 14px;
}

.form-footer a {
  color: #667eea;
  text-decoration: none;
  font-weight: 500;
}
</style>
