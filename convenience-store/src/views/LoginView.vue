<template>
  <div class="login-page">
    <!-- ===== 左侧：品牌展示 + 动态背景 ===== -->
    <div class="login-left">
      <FloatingChars :count="15" />
      <div class="brand-content">
        <div class="brand-icon">
          <span class="icon-text">便</span>
        </div>
        <h1 class="brand-title">便利店管理系统</h1>
        <p class="brand-subtitle">智能 · 高效 · 便捷</p>
        <div class="brand-features">
          <div class="feature-item">
            <svg viewBox="0 0 24 24" width="20" height="20" fill="none" stroke="currentColor" stroke-width="2">
              <path d="M22 11.08V12a10 10 0 1 1-5.93-9.14"/><polyline points="22 4 12 14.01 9 11.01"/>
            </svg>
            <span>商品管理一目了然</span>
          </div>
          <div class="feature-item">
            <svg viewBox="0 0 24 24" width="20" height="20" fill="none" stroke="currentColor" stroke-width="2">
              <rect x="2" y="3" width="20" height="14" rx="2"/><line x1="8" y1="21" x2="16" y2="21"/><line x1="12" y1="17" x2="12" y2="21"/>
            </svg>
            <span>订单追踪高效快捷</span>
          </div>
          <div class="feature-item">
            <svg viewBox="0 0 24 24" width="20" height="20" fill="none" stroke="currentColor" stroke-width="2">
              <path d="M12 22s8-4 8-10V5l-8-3-8 3v7c0 6 8 10 8 10z"/>
            </svg>
            <span>数据安全可靠</span>
          </div>
        </div>
      </div>
    </div>

    <!-- ===== 右侧：表单区域 ===== -->
    <div class="login-right">
      <!-- 角色切换 -->
      <div class="role-toggle">
        <button
          :class="['toggle-btn', { active: isUser }]"
          @click="switchRole('user')"
        >
          <svg viewBox="0 0 24 24" width="16" height="16" fill="none" stroke="currentColor" stroke-width="2">
            <path d="M20 21v-2a4 4 0 0 0-4-4H8a4 4 0 0 0-4 4v2"/><circle cx="12" cy="7" r="4"/>
          </svg>
          用户
        </button>
        <button
          :class="['toggle-btn', { active: !isUser }]"
          @click="switchRole('admin')"
        >
          <svg viewBox="0 0 24 24" width="16" height="16" fill="none" stroke="currentColor" stroke-width="2">
            <path d="M12 22s8-4 8-10V5l-8-3-8 3v7c0 6 8 10 8 10z"/>
          </svg>
          管理员
        </button>
      </div>

      <transition name="fade-slide" mode="out-in">
        <!-- ===== 登录模式 ===== -->
        <div key="login" v-if="formMode === 'login'" class="form-container">
          <h2 class="form-title">{{ isUser ? '用户登录' : '管理员登录' }}</h2>
          <p class="form-subtitle">{{ isUser ? '欢迎回来，请登录您的账号' : '管理员入口' }}</p>

          <form @submit.prevent="handleLogin" class="login-form">
            <div class="input-group">
              <label class="input-label">用户名</label>
              <div class="input-wrapper">
                <svg class="input-icon" viewBox="0 0 24 24" width="18" height="18" fill="none" stroke="#999" stroke-width="2">
                  <path d="M20 21v-2a4 4 0 0 0-4-4H8a4 4 0 0 0-4 4v2"/><circle cx="12" cy="7" r="4"/>
                </svg>
                <input v-model="username" type="text" placeholder="请输入用户名" required />
              </div>
            </div>
            <div class="input-group">
              <label class="input-label">密码</label>
              <div class="input-wrapper">
                <svg class="input-icon" viewBox="0 0 24 24" width="18" height="18" fill="none" stroke="#999" stroke-width="2">
                  <rect x="3" y="11" width="18" height="11" rx="2" ry="2"/><path d="M7 11V7a5 5 0 0 1 10 0v4"/>
                </svg>
                <input v-model="password" type="password" placeholder="请输入密码" required />
              </div>
            </div>
            <button type="submit" class="submit-btn" :class="{ loading }">
              <span v-if="!loading">登 录</span>
              <span v-else class="spinner"></span>
            </button>
            <p v-if="error" class="error-msg">
              <svg viewBox="0 0 24 24" width="16" height="16" fill="none" stroke="currentColor" stroke-width="2">
                <circle cx="12" cy="12" r="10"/><line x1="12" y1="8" x2="12" y2="12"/><line x1="12" y1="16" x2="12.01" y2="16"/>
              </svg>
              {{ error }}
            </p>
          </form>

          <!-- 用户专属链接 -->
          <div v-if="isUser" class="form-links">
            <button class="link-btn" @click="formMode = 'register'">没有账号？<span>立即注册</span></button>
            <button class="link-btn" @click="formMode = 'forgot'"><span>忘记密码？</span></button>
          </div>

          <div class="hint-box">
            <p class="hint-title">测试账号</p>
            <div class="hint-tags">
              <template v-if="isUser">
                <span class="hint-tag">user1 / user123</span>
                <span class="hint-tag">user2 / user123</span>
              </template>
              <template v-else>
                <span class="hint-tag">admin / admin123</span>
              </template>
            </div>
          </div>
        </div>

        <!-- ===== 注册模式 ===== -->
        <div key="register" v-else-if="formMode === 'register'" class="form-container">
          <h2 class="form-title">用户注册</h2>
          <p class="form-subtitle">创建您的账号，开始使用</p>
          <form @submit.prevent="handleRegister" class="login-form">
            <div class="input-group">
              <label class="input-label">用户名</label>
              <div class="input-wrapper">
                <svg class="input-icon" viewBox="0 0 24 24" width="18" height="18" fill="none" stroke="#999" stroke-width="2">
                  <path d="M20 21v-2a4 4 0 0 0-4-4H8a4 4 0 0 0-4 4v2"/><circle cx="12" cy="7" r="4"/>
                </svg>
                <input v-model="regUsername" type="text" placeholder="请设置用户名" required />
              </div>
            </div>
            <div class="input-group">
              <label class="input-label">显示名称</label>
              <div class="input-wrapper">
                <svg class="input-icon" viewBox="0 0 24 24" width="18" height="18" fill="none" stroke="#999" stroke-width="2">
                  <path d="M20 21v-2a4 4 0 0 0-4-4H8a4 4 0 0 0-4 4v2"/><circle cx="12" cy="7" r="4"/>
                </svg>
                <input v-model="regName" type="text" placeholder="请输入您的姓名" required />
              </div>
            </div>
            <div class="input-group">
              <label class="input-label">密码</label>
              <div class="input-wrapper">
                <svg class="input-icon" viewBox="0 0 24 24" width="18" height="18" fill="none" stroke="#999" stroke-width="2">
                  <rect x="3" y="11" width="18" height="11" rx="2" ry="2"/><path d="M7 11V7a5 5 0 0 1 10 0v4"/>
                </svg>
                <input v-model="regPassword" type="password" placeholder="请设置密码" required />
              </div>
            </div>
            <div class="input-group">
              <label class="input-label">确认密码</label>
              <div class="input-wrapper">
                <svg class="input-icon" viewBox="0 0 24 24" width="18" height="18" fill="none" stroke="#999" stroke-width="2">
                  <rect x="3" y="11" width="18" height="11" rx="2" ry="2"/><path d="M7 11V7a5 5 0 0 1 10 0v4"/>
                </svg>
                <input v-model="regConfirm" type="password" placeholder="请再次输入密码" required />
              </div>
            </div>
            <button type="submit" class="submit-btn" :class="{ loading: regLoading }">
              <span v-if="!regLoading">注 册</span>
              <span v-else class="spinner"></span>
            </button>
            <p v-if="regError" class="error-msg">
              <svg viewBox="0 0 24 24" width="16" height="16" fill="none" stroke="currentColor" stroke-width="2">
                <circle cx="12" cy="12" r="10"/><line x1="12" y1="8" x2="12" y2="12"/><line x1="12" y1="16" x2="12.01" y2="16"/>
              </svg>
              {{ regError }}
            </p>
            <p v-if="regSuccess" class="success-msg">
              <svg viewBox="0 0 24 24" width="16" height="16" fill="none" stroke="currentColor" stroke-width="2">
                <path d="M22 11.08V12a10 10 0 1 1-5.93-9.14"/><polyline points="22 4 12 14.01 9 11.01"/>
              </svg>
              {{ regSuccess }}
            </p>
          </form>
          <div class="form-links">
            <button class="link-btn" @click="resetToLogin">已有账号？<span>去登录</span></button>
          </div>
        </div>

        <!-- ===== 忘记密码模式 ===== -->
        <div key="forgot" v-else-if="formMode === 'forgot'" class="form-container">
          <h2 class="form-title">重置密码</h2>
          <p class="form-subtitle">输入用户名和新密码</p>
          <form @submit.prevent="handleForgot" class="login-form">
            <div class="input-group">
              <label class="input-label">用户名</label>
              <div class="input-wrapper">
                <svg class="input-icon" viewBox="0 0 24 24" width="18" height="18" fill="none" stroke="#999" stroke-width="2">
                  <path d="M20 21v-2a4 4 0 0 0-4-4H8a4 4 0 0 0-4 4v2"/><circle cx="12" cy="7" r="4"/>
                </svg>
                <input v-model="forgotUsername" type="text" placeholder="请输入用户名" required />
              </div>
            </div>
            <div class="input-group">
              <label class="input-label">新密码</label>
              <div class="input-wrapper">
                <svg class="input-icon" viewBox="0 0 24 24" width="18" height="18" fill="none" stroke="#999" stroke-width="2">
                  <rect x="3" y="11" width="18" height="11" rx="2" ry="2"/><path d="M7 11V7a5 5 0 0 1 10 0v4"/>
                </svg>
                <input v-model="forgotPassword" type="password" placeholder="请设置新密码" required />
              </div>
            </div>
            <div class="input-group">
              <label class="input-label">确认新密码</label>
              <div class="input-wrapper">
                <svg class="input-icon" viewBox="0 0 24 24" width="18" height="18" fill="none" stroke="#999" stroke-width="2">
                  <rect x="3" y="11" width="18" height="11" rx="2" ry="2"/><path d="M7 11V7a5 5 0 0 1 10 0v4"/>
                </svg>
                <input v-model="forgotConfirm" type="password" placeholder="请再次输入新密码" required />
              </div>
            </div>
            <button type="submit" class="submit-btn" :class="{ loading: forgotLoading }">
              <span v-if="!forgotLoading">重 置</span>
              <span v-else class="spinner"></span>
            </button>
            <p v-if="forgotError" class="error-msg">
              <svg viewBox="0 0 24 24" width="16" height="16" fill="none" stroke="currentColor" stroke-width="2">
                <circle cx="12" cy="12" r="10"/><line x1="12" y1="8" x2="12" y2="12"/><line x1="12" y1="16" x2="12.01" y2="16"/>
              </svg>
              {{ forgotError }}
            </p>
            <p v-if="forgotSuccess" class="success-msg">
              <svg viewBox="0 0 24 24" width="16" height="16" fill="none" stroke="currentColor" stroke-width="2">
                <path d="M22 11.08V12a10 10 0 1 1-5.93-9.14"/><polyline points="22 4 12 14.01 9 11.01"/>
              </svg>
              {{ forgotSuccess }}
            </p>
          </form>
          <div class="form-links">
            <button class="link-btn" @click="resetToLogin">想起密码了？<span>去登录</span></button>
          </div>
        </div>
      </transition>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue';
import { useRouter } from 'vue-router';
import { dataManager } from '../utils/dataManager';
import FloatingChars from '../components/FloatingChars.vue';

const router = useRouter();
const isUser = ref(true);

// Login fields
const username = ref('');
const password = ref('');
const error = ref('');
const loading = ref(false);

// Register fields
const regUsername = ref('');
const regName = ref('');
const regPassword = ref('');
const regConfirm = ref('');
const regError = ref('');
const regSuccess = ref('');
const regLoading = ref(false);

// Forgot fields
const forgotUsername = ref('');
const forgotPassword = ref('');
const forgotConfirm = ref('');
const forgotError = ref('');
const forgotSuccess = ref('');
const forgotLoading = ref(false);

const formMode = ref('login');

const switchRole = (role) => {
  isUser.value = role === 'user';
  formMode.value = 'login';
  error.value = '';
};

const resetToLogin = () => {
  formMode.value = 'login';
  error.value = '';
  regError.value = '';
  regSuccess.value = '';
  forgotError.value = '';
  forgotSuccess.value = '';
};

const handleLogin = async () => {
  error.value = '';
  loading.value = true;
  try {
    const user = await dataManager.login(username.value, password.value);
    if (user && user.id) {
      const expectedRole = isUser.value ? 'user' : 'admin';
      if (user.role !== expectedRole) {
        error.value = isUser.value ? '请使用用户账号登录' : '请使用管理员账号登录';
        loading.value = false;
        return;
      }
      await dataManager.setCurrentUser(user);
      router.push(user.role === 'admin' ? '/admin' : '/user');
    } else {
      error.value = '用户名或密码错误';
    }
  } catch {
    error.value = '登录失败，请检查网络连接';
  } finally {
    loading.value = false;
  }
};

const handleRegister = async () => {
  regError.value = '';
  regSuccess.value = '';
  if (regPassword.value !== regConfirm.value) {
    regError.value = '两次密码输入不一致';
    return;
  }
  if (regPassword.value.length < 6) {
    regError.value = '密码长度不能少于6位';
    return;
  }
  regLoading.value = true;
  try {
    const account = await dataManager.register(regUsername.value, regPassword.value, regName.value);
    if (account) {
      regSuccess.value = '注册成功！即将跳转登录...';
      setTimeout(() => {
        username.value = regUsername.value;
        resetToLogin();
      }, 1500);
    } else {
      regError.value = '用户名已存在，请换一个';
    }
  } catch {
    regError.value = '注册失败，请重试';
  } finally {
    regLoading.value = false;
  }
};

const handleForgot = async () => {
  forgotError.value = '';
  forgotSuccess.value = '';
  if (forgotPassword.value !== forgotConfirm.value) {
    forgotError.value = '两次密码输入不一致';
    return;
  }
  if (forgotPassword.value.length < 6) {
    forgotError.value = '密码长度不能少于6位';
    return;
  }
  forgotLoading.value = true;
  try {
    const ok = await dataManager.forgotPassword(forgotUsername.value, forgotPassword.value);
    if (ok) {
      forgotSuccess.value = '密码重置成功！请使用新密码登录';
      setTimeout(() => {
        username.value = forgotUsername.value;
        password.value = forgotPassword.value;
        resetToLogin();
      }, 1500);
    } else {
      forgotError.value = '该用户名不存在';
    }
  } catch {
    forgotError.value = '重置失败，请重试';
  } finally {
    forgotLoading.value = false;
  }
};
</script>

<style scoped>
/* ===== 布局 ===== */
.login-page {
  min-height: 100vh;
  display: flex;
}

/* ===== 左侧 ===== */
.login-left {
  flex: 1;
  position: relative;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, #ff5000 0%, #ff6a00 30%, #ff8c00 60%, #ff5000 100%);
  background-size: 400% 400%;
  animation: gradientShift 12s ease infinite;
  overflow: hidden;
}

@keyframes gradientShift {
  0% { background-position: 0% 50%; }
  50% { background-position: 100% 50%; }
  100% { background-position: 0% 50%; }
}

/* 粒子 */
.bg-particles {
  position: absolute;
  inset: 0;
  pointer-events: none;
}

.particle {
  position: absolute;
  bottom: -20px;
  background: rgba(255, 255, 255, 0.3);
  border-radius: 50%;
  animation: floatUp linear infinite;
}

@keyframes floatUp {
  0% {
    transform: translateY(0) scale(0);
    opacity: 0;
  }
  10% {
    opacity: 1;
    transform: translateY(0) scale(1);
  }
  90% {
    opacity: 1;
  }
  100% {
    transform: translateY(-100vh) scale(0.5);
    opacity: 0;
  }
}

.brand-content {
  position: relative;
  z-index: 1;
  text-align: center;
  color: #fff;
  padding: 40px;
}

.brand-icon {
  width: 80px;
  height: 80px;
  background: rgba(255, 255, 255, 0.2);
  backdrop-filter: blur(10px);
  border-radius: 20px;
  display: flex;
  align-items: center;
  justify-content: center;
  margin: 0 auto 24px;
  animation: pulseGlow 3s ease-in-out infinite;
}

@keyframes pulseGlow {
  0%, 100% { box-shadow: 0 0 0 0 rgba(255, 255, 255, 0.3); }
  50% { box-shadow: 0 0 30px 10px rgba(255, 255, 255, 0.15); }
}

.icon-text {
  font-size: 36px;
  font-weight: bold;
}

.brand-title {
  font-size: 32px;
  font-weight: 800;
  margin-bottom: 12px;
  letter-spacing: 1px;
  text-shadow: 0 2px 10px rgba(0, 0, 0, 0.15);
}

.brand-subtitle {
  font-size: 16px;
  opacity: 0.85;
  margin-bottom: 40px;
  letter-spacing: 4px;
}

.brand-features {
  display: flex;
  flex-direction: column;
  gap: 14px;
  align-items: center;
}

.feature-item {
  display: flex;
  align-items: center;
  gap: 10px;
  font-size: 14px;
  opacity: 0.9;
  animation: fadeInUp 0.6s ease both;
}

.feature-item:nth-child(2) { animation-delay: 0.15s; }
.feature-item:nth-child(3) { animation-delay: 0.3s; }

@keyframes fadeInUp {
  from { opacity: 0; transform: translateY(16px); }
  to { opacity: 0.9; transform: translateY(0); }
}

/* ===== 右侧 ===== */
.login-right {
  width: 460px;
  min-width: 460px;
  background: #fff;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 48px 44px;
  position: relative;
}

/* ===== 角色切换 ===== */
.role-toggle {
  display: flex;
  background: #f5f5f5;
  border-radius: 10px;
  padding: 4px;
  margin-bottom: 32px;
  width: 100%;
  max-width: 320px;
}

.toggle-btn {
  flex: 1;
  height: 40px;
  border-radius: 8px;
  font-size: 14px;
  font-weight: 600;
  color: #999;
  background: transparent;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 6px;
  transition: all 0.3s ease;
}

.toggle-btn.active {
  background: linear-gradient(135deg, #ff5000, #ff6a00);
  color: #fff;
  box-shadow: 0 4px 12px rgba(255, 80, 0, 0.25);
}

.toggle-btn:not(.active):hover {
  color: #666;
}

/* ===== 表单容器 ===== */
.form-container {
  width: 100%;
  max-width: 360px;
}

.form-title {
  font-size: 22px;
  font-weight: 700;
  color: #1a1a1a;
  margin-bottom: 4px;
}

.form-subtitle {
  font-size: 13px;
  color: #bbb;
  margin-bottom: 28px;
}

/* ===== 表单 ===== */
.login-form {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.input-group {
  display: flex;
  flex-direction: column;
  gap: 6px;
}

.input-label {
  font-size: 13px;
  font-weight: 500;
  color: #333;
}

.input-wrapper {
  position: relative;
  display: flex;
  align-items: center;
}

.input-icon {
  position: absolute;
  left: 14px;
  pointer-events: none;
  flex-shrink: 0;
}

.input-wrapper input {
  width: 100%;
  height: 44px;
  padding: 0 14px 0 42px;
  border: 1.5px solid #e0e0e0;
  border-radius: 10px;
  font-size: 14px;
  color: #333;
  background: #fafafa;
  transition: all 0.25s ease;
}

.input-wrapper input:focus {
  border-color: #ff5000;
  background: #fff;
  box-shadow: 0 0 0 3px rgba(255, 80, 0, 0.1);
}

.input-wrapper input::placeholder {
  color: #bbb;
}

/* ===== 提交按钮 ===== */
.submit-btn {
  height: 46px;
  background: linear-gradient(135deg, #ff5000, #ff6a00);
  color: #fff;
  border-radius: 10px;
  font-size: 16px;
  font-weight: 600;
  letter-spacing: 2px;
  transition: all 0.3s ease;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-top: 4px;
  position: relative;
  overflow: hidden;
}

.submit-btn::before {
  content: '';
  position: absolute;
  top: 0;
  left: -100%;
  width: 100%;
  height: 100%;
  background: linear-gradient(90deg, transparent, rgba(255,255,255,0.2), transparent);
  transition: left 0.5s;
}

.submit-btn:hover::before {
  left: 100%;
}

.submit-btn:hover {
  box-shadow: 0 4px 16px rgba(255, 80, 0, 0.35);
  transform: translateY(-1px);
}

.submit-btn.loading {
  opacity: 0.8;
  pointer-events: none;
}

.spinner {
  width: 22px;
  height: 22px;
  border: 3px solid rgba(255, 255, 255, 0.3);
  border-top-color: #fff;
  border-radius: 50%;
  animation: spin 0.6s linear infinite;
}

@keyframes spin {
  to { transform: rotate(360deg); }
}

/* ===== 消息 ===== */
.error-msg {
  display: flex;
  align-items: center;
  gap: 6px;
  color: #ff4d4f;
  font-size: 13px;
  justify-content: center;
}

.success-msg {
  display: flex;
  align-items: center;
  gap: 6px;
  color: #52c41a;
  font-size: 13px;
  justify-content: center;
}

/* ===== 链接按钮 ===== */
.form-links {
  display: flex;
  justify-content: space-between;
  margin-top: 18px;
}

.link-btn {
  font-size: 13px;
  color: #999;
  background: transparent;
  transition: color 0.2s;
}

.link-btn span {
  color: #ff5000;
  font-weight: 500;
}

.link-btn:hover {
  color: #666;
}

.link-btn:hover span {
  text-decoration: underline;
}

/* ===== 提示账号 ===== */
.hint-box {
  margin-top: 24px;
  padding-top: 18px;
  border-top: 1px solid #f0f0f0;
}

.hint-title {
  font-size: 12px;
  color: #ccc;
  text-align: center;
  margin-bottom: 10px;
}

.hint-tags {
  display: flex;
  flex-wrap: wrap;
  gap: 6px;
  justify-content: center;
}

.hint-tag {
  font-size: 11px;
  padding: 4px 10px;
  background: #fff5ef;
  color: #ff5000;
  border-radius: 4px;
  border: 1px solid #ffe0cc;
}

/* ===== 过渡动画 ===== */
.fade-slide-enter-active,
.fade-slide-leave-active {
  transition: all 0.3s ease;
}

.fade-slide-enter-from {
  opacity: 0;
  transform: translateX(20px);
}

.fade-slide-leave-to {
  opacity: 0;
  transform: translateX(-20px);
}
</style>
