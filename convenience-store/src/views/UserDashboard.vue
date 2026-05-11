<template>
  <div class="layout">
    <header class="topbar">
      <div class="topbar-inner">
        <div class="topbar-left">
          <div class="logo" @click="$router.push('/user/products')">
            <span class="logo-icon">便</span>
            <span class="logo-text">便利店</span>
          </div>
        </div>
        <div class="topbar-right">
          <div class="avatar-btn" @click="$router.push('/user/profile')" :title="'个人中心'">
            <div class="avatar">{{ user.name ? user.name[0] : '?' }}</div>
            <span class="avatar-name">{{ user.name }}</span>
          </div>
          <button class="logout-btn" @click="logout">退出</button>
        </div>
      </div>
    </header>
    <CategorySidebar />
    <FloatingChars :count="10" />
    <main class="main-content">
      <router-view></router-view>
    </main>
    <CartSidebar />
    <ToastManager />
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import { useRouter } from 'vue-router';
import { dataManager } from '../utils/dataManager';
import CategorySidebar from '../components/CategorySidebar.vue';
import CartSidebar from '../components/CartSidebar.vue';
import ToastManager from '../components/ToastManager.vue';
import FloatingChars from '../components/FloatingChars.vue';

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
.layout {
  min-height: 100vh;
  display: flex;
  flex-direction: column;
  background: linear-gradient(135deg, #fff5ef 0%, #fff0e6 25%, #fef9f4 50%, #fff5ef 75%, #ffe8d6 100%);
  background-size: 400% 400%;
  animation: bgShift 15s ease infinite;
  position: relative;
}

.layout::before {
  content: '';
  position: fixed;
  inset: 0;
  pointer-events: none;
  background:
    radial-gradient(circle at 20% 30%, rgba(255, 80, 0, 0.04) 0%, transparent 50%),
    radial-gradient(circle at 80% 70%, rgba(255, 106, 0, 0.03) 0%, transparent 50%),
    radial-gradient(circle at 40% 80%, rgba(255, 140, 0, 0.03) 0%, transparent 50%);
  z-index: 0;
}

@keyframes bgShift {
  0% { background-position: 0% 50%; }
  50% { background-position: 100% 50%; }
  100% { background-position: 0% 50%; }
}

.topbar {
  background: #fff;
  border-bottom: 1px solid #e8e8e8;
  position: sticky;
  top: 0;
  z-index: 100;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
}

.topbar-inner {
  max-width: 1200px;
  margin: 0 auto;
  height: 60px;
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 24px;
}

.topbar-left {
  display: flex;
  align-items: center;
  gap: 36px;
}

.logo {
  display: flex;
  align-items: center;
  gap: 8px;
  cursor: pointer;
}

.logo-icon {
  width: 34px;
  height: 34px;
  background: linear-gradient(135deg, #ff5000, #ff6a00);
  color: #fff;
  border-radius: 8px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 18px;
  font-weight: bold;
}

.logo-text {
  font-size: 18px;
  font-weight: 700;
  color: #ff5000;
}

.topbar-right {
  display: flex;
  align-items: center;
  gap: 16px;
}

.avatar-btn {
  display: flex;
  align-items: center;
  gap: 8px;
  cursor: pointer;
  padding: 4px 10px 4px 4px;
  border-radius: 24px;
  transition: background 0.2s;
}

.avatar-btn:hover {
  background: #fff5ef;
}

.avatar {
  width: 34px;
  height: 34px;
  background: linear-gradient(135deg, #ff5000, #ff6a00);
  color: #fff;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 15px;
  font-weight: 700;
}

.avatar-name {
  font-size: 13px;
  color: #333;
  font-weight: 500;
}

.logout-btn {
  padding: 7px 18px;
  font-size: 13px;
  color: #ff5000;
  background: #fff5ef;
  border-radius: 6px;
  transition: all 0.2s;
  font-weight: 500;
}

.logout-btn:hover {
  background: #ffe0cc;
}

.main-content {
  flex: 1;
  max-width: 1200px;
  margin: 0 auto;
  padding: 24px;
  width: 100%;
  position: relative;
  z-index: 1;
}
</style>
