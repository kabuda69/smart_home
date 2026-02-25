<template>
  <div class="login-page">
    <div class="login-box">
      <div class="login-left">
        <div class="brand">
          <div class="brand-icon">
            <span class="mdi mdi-home-automation"></span>
          </div>
          <h1>智能家居</h1>
          <p>Smart Home Control System</p>
        </div>
        <div class="features">
          <div class="feature">
            <span class="mdi mdi-shield-check"></span>
            <div>
              <h4>安全可靠</h4>
              <p>全方位安全防护</p>
            </div>
          </div>
          <div class="feature">
            <span class="mdi mdi-lightning-bolt"></span>
            <div>
              <h4>快速响应</h4>
              <p>毫秒级设备控制</p>
            </div>
          </div>
          <div class="feature">
            <span class="mdi mdi-cellphone-link"></span>
            <div>
              <h4>多端同步</h4>
              <p>随时随地管理</p>
            </div>
          </div>
        </div>
      </div>
      <div class="login-right">
        <div class="form-box">
          <h2>欢迎登录</h2>
          <p class="subtitle">请输入您的账号信息</p>
          
          <form @submit.prevent="login">
            <div class="form-item">
              <label>用户名</label>
              <div class="input-box">
                <span class="mdi mdi-account"></span>
                <input v-model="form.username" type="text" placeholder="请输入用户名" required>
              </div>
            </div>
            
            <div class="form-item">
              <label>密码</label>
              <div class="input-box">
                <span class="mdi mdi-lock"></span>
                <input v-model="form.password" type="password" placeholder="请输入密码" required>
              </div>
            </div>

            <div v-if="error" class="error-msg">
              <span class="mdi mdi-alert-circle"></span> {{ error }}
            </div>

            <button type="submit" class="submit-btn" :disabled="loading">
              <span v-if="loading" class="mdi mdi-loading mdi-spin"></span>
              <span v-else>登 录</span>
            </button>
          </form>

          <div class="form-footer">
            还没有账号？<router-link to="/register">立即注册</router-link>
          </div>
          
          <div class="demo-info">
            <p>演示账号</p>
            <div class="demo-tags">
              <span @click="fillDemo('admin', 'admin123')">admin / admin123</span>
              <span @click="fillDemo('user1', '123456')">user1 / 123456</span>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
export default {
  data: function() {
    return { loading: false, error: '', form: { username: '', password: '' } }
  },
  methods: {
    login: function() {
      var self = this
      this.loading = true
      this.error = ''
      this.$store.dispatch('login', this.form).then(function(res) {
        if (res.success) self.$router.push('/dashboard')
        else self.error = res.message
        self.loading = false
      }).catch(function(e) {
        self.error = (e.response && e.response.data && e.response.data.message) || '登录失败'
        self.loading = false
      })
    },
    fillDemo: function(u, p) {
      this.form.username = u
      this.form.password = p
    }
  }
}
</script>

<style scoped>
.login-page {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  padding: 20px;
}

.login-box {
  display: flex;
  width: 100%;
  max-width: 900px;
  background: #fff;
  border-radius: 20px;
  overflow: hidden;
  box-shadow: 0 20px 60px rgba(0,0,0,0.2);
}

.login-left {
  flex: 1;
  background: linear-gradient(135deg, #1a1a2e 0%, #16213e 100%);
  padding: 50px 40px;
  color: #fff;
}

.brand {
  margin-bottom: 50px;
}

.brand-icon {
  width: 60px;
  height: 60px;
  background: rgba(255,255,255,0.1);
  border-radius: 16px;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-bottom: 20px;
}

.brand-icon .mdi {
  font-size: 30px;
}

.brand h1 {
  font-size: 28px;
  margin-bottom: 8px;
}

.brand p {
  color: rgba(255,255,255,0.6);
  font-size: 13px;
  text-transform: uppercase;
  letter-spacing: 2px;
}

.features {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.feature {
  display: flex;
  align-items: center;
  gap: 14px;
  padding: 16px;
  background: rgba(255,255,255,0.05);
  border-radius: 12px;
}

.feature .mdi {
  font-size: 24px;
  color: #a5b4fc;
}

.feature h4 {
  font-size: 14px;
  margin-bottom: 2px;
}

.feature p {
  font-size: 12px;
  color: rgba(255,255,255,0.6);
}

.login-right {
  flex: 1;
  padding: 50px 40px;
  display: flex;
  align-items: center;
}

.form-box {
  width: 100%;
}

.form-box h2 {
  font-size: 26px;
  color: #1a1a1a;
  margin-bottom: 8px;
}

.subtitle {
  color: #888;
  margin-bottom: 30px;
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
  transition: all 0.2s;
}

.submit-btn:hover {
  opacity: 0.9;
  transform: translateY(-1px);
}

.submit-btn:disabled {
  opacity: 0.7;
  cursor: not-allowed;
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

.demo-info {
  margin-top: 30px;
  padding-top: 20px;
  border-top: 1px solid #eee;
  text-align: center;
}

.demo-info p {
  font-size: 12px;
  color: #999;
  margin-bottom: 10px;
}

.demo-tags {
  display: flex;
  gap: 10px;
  justify-content: center;
}

.demo-tags span {
  padding: 6px 12px;
  background: #f5f5f5;
  border-radius: 6px;
  font-size: 12px;
  color: #666;
  cursor: pointer;
  transition: all 0.2s;
}

.demo-tags span:hover {
  background: #e8eaf6;
  color: #667eea;
}

@media (max-width: 768px) {
  .login-left { display: none; }
  .login-box { max-width: 400px; }
  .login-right { padding: 40px 30px; }
}
</style>
