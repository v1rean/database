<template>
  <div class="login-container">
    <div class="login-box">
      <h2>便利店管理系统</h2>
      <form @submit.prevent="handleLogin">
        <div class="form-group">
          <label>用户名</label>
          <input v-model="username" type="text" required />
        </div>
        <div class="form-group">
          <label>密码</label>
          <input v-model="password" type="password" required />
        </div>
        <button type="submit">登录</button>
        <p v-if="error" class="error">{{ error }}</p>
      </form>
      <div class="hint">
        <p>管理员账号: admin / admin123</p>
        <p>用户账号: user1 / user123 或 user2 / user123</p>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue';
import { useRouter } from 'vue-router';
import { dataManager } from '../utils/dataManager';

const username = ref('');
const password = ref('');
const error = ref('');
const router = useRouter();

const handleLogin = async () => {
  const user = await dataManager.login(username.value, password.value);
  if (user && user.id) {
    await dataManager.setCurrentUser(user);
    if (user.role === 'admin') {
      router.push('/admin');
    } else {
      router.push('/user');
    }
  } else {
    error.value = '用户名或密码错误';
  }
};
</script>

<style scoped>
.login-container {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 100vh;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
}
.login-box {
  background: white;
  padding: 40px;
  border-radius: 10px;
  box-shadow: 0 10px 40px rgba(0,0,0,0.1);
  width: 400px;
}
h2 { text-align: center; color: #333; margin-bottom: 30px; }
.form-group { margin-bottom: 20px; }
label { display: block; margin-bottom: 5px; color: #555; }
input {
  width: 100%;
  padding: 10px;
  border: 1px solid #ddd;
  border-radius: 5px;
  font-size: 14px;
}
button {
  width: 100%;
  padding: 12px;
  background: #667eea;
  color: white;
  border: none;
  border-radius: 5px;
  cursor: pointer;
  font-size: 16px;
}
button:hover { background: #5568d3; }
.error { color: red; text-align: center; margin-top: 10px; }
.hint {
  margin-top: 20px;
  padding: 15px;
  background: #f5f5f5;
  border-radius: 5px;
  font-size: 12px;
  color: #666;
}
</style>
