<template>
  <div class="dashboard">
    <header>
      <h1>管理员面板 - {{ user.name }}</h1>
      <button @click="logout">退出登录</button>
    </header>
    <nav>
      <router-link to="/admin/products">商品管理</router-link>
      <router-link to="/admin/purchases">购买记录管理</router-link>
    </nav>
    <router-view></router-view>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import { useRouter } from 'vue-router';
import { dataManager } from '../utils/dataManager';

const user = ref({});
const router = useRouter();

onMounted(async () => {
  const currentUser = await dataManager.getCurrentUser();
  if (!currentUser || currentUser.role !== 'admin') {
    router.push('/');
    return;
  }
  user.value = currentUser;
});

const logout = async () => {
  await dataManager.logout();
  router.push('/');
};
</script>

<style scoped>
.dashboard { max-width: 1200px; margin: 0 auto; padding: 20px; }
header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}
nav { margin-bottom: 30px; }
nav a {
  margin-right: 20px;
  padding: 10px 20px;
  background: #667eea;
  color: white;
  text-decoration: none;
  border-radius: 5px;
}
nav a:hover { background: #5568d3; }
button {
  padding: 10px 20px;
  background: #f44336;
  color: white;
  border: none;
  border-radius: 5px;
  cursor: pointer;
}
</style>
