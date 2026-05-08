<template>
  <div class="dashboard">
    <header>
      <h1>欢迎, {{ user.name }}</h1>
      <button @click="logout">退出登录</button>
    </header>
    <nav>
      <router-link to="/user/products">商品浏览</router-link>
      <router-link to="/user/purchases">我的购买记录</router-link>
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
  if (!currentUser) {
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
