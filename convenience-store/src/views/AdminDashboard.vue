<template>
  <div class="layout">
    <header class="topbar">
      <div class="topbar-inner">
        <div class="topbar-left">
          <div class="logo" @click="$router.push('/admin')">
            <span class="logo-icon">便</span>
            <span class="logo-text">管理后台</span>
          </div>
          <nav class="topbar-nav">
            <router-link to="/admin/users" class="nav-link" active-class="active">用户</router-link>
            <router-link to="/admin/products" class="nav-link" active-class="active">商品</router-link>
            <router-link to="/admin/categories" class="nav-link" active-class="active">分类</router-link>
            <router-link to="/admin/inventory" class="nav-link" active-class="active">出入库</router-link>
            <router-link to="/admin/purchases" class="nav-link" active-class="active">订单</router-link>
            <router-link to="/admin/carousels" class="nav-link" active-class="active">轮播</router-link>
            <router-link to="/admin/sales" class="nav-link" active-class="active">报表</router-link>
            <router-link to="/admin/announcements" class="nav-link" active-class="active">公告</router-link>
          </nav>
        </div>
        <div class="topbar-right">
          <span class="user-greeting">
            <svg viewBox="0 0 24 24" width="16" height="16" fill="none" stroke="currentColor" stroke-width="2"><path d="M20 21v-2a4 4 0 0 0-4-4H8a4 4 0 0 0-4 4v2"/><circle cx="12" cy="7" r="4"/></svg>
            {{ user.name }}
          </span>
          <button class="logout-btn" @click="logout">退出</button>
        </div>
      </div>
    </header>
    <main class="main-content">
      <router-view></router-view>
    </main>
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
  if (!currentUser || currentUser.role !== 'admin') { router.push('/'); return; }
  user.value = currentUser;
});

const logout = async () => {
  await dataManager.logout();
  router.push('/');
};
</script>

<style scoped>
.layout { min-height: 100vh; display: flex; flex-direction: column; background: linear-gradient(135deg, #0f0c29 0%, #1a1a3e 25%, #16213e 50%, #1a1a3e 75%, #0f0c29 100%); background-size: 400% 400%; animation: bgShift 18s ease infinite; position: relative; }
.layout::before { content: ''; position: fixed; inset: 0; pointer-events: none; background: radial-gradient(circle at 15% 25%, rgba(255,80,0,0.06) 0%, transparent 45%), radial-gradient(circle at 85% 75%, rgba(255,106,0,0.04) 0%, transparent 45%), radial-gradient(circle at 50% 50%, rgba(255,140,0,0.02) 0%, transparent 50%); z-index: 0; }
@keyframes bgShift { 0%{background-position:0% 50%} 50%{background-position:100% 50%} 100%{background-position:0% 50%} }
.topbar { background: #1a1a2e; position: sticky; top: 0; z-index: 100; box-shadow: 0 2px 8px rgba(0,0,0,0.15); }
.topbar-inner { max-width: 1200px; margin: 0 auto; height: 60px; display: flex; align-items: center; justify-content: space-between; padding: 0 24px; }
.topbar-left { display: flex; align-items: center; gap: 24px; overflow-x: auto; }
.logo { display: flex; align-items: center; gap: 8px; cursor: pointer; flex-shrink: 0; }
.logo-icon { width: 34px; height: 34px; background: linear-gradient(135deg, #ff5000, #ff6a00); color: #fff; border-radius: 8px; display: flex; align-items: center; justify-content: center; font-size: 18px; font-weight: bold; }
.logo-text { font-size: 16px; font-weight: 600; color: #fff; white-space: nowrap; }
.topbar-nav { display: flex; gap: 2px; }
.nav-link { padding: 8px 14px; font-size: 13px; color: rgba(255,255,255,0.7); border-radius: 6px; transition: all 0.2s; white-space: nowrap; }
.nav-link:hover { color: #fff; background: rgba(255,255,255,0.1); }
.nav-link.active { color: #fff; background: rgba(255,80,0,0.7); font-weight: 600; }
.topbar-right { display: flex; align-items: center; gap: 16px; flex-shrink: 0; }
.user-greeting { display: flex; align-items: center; gap: 6px; font-size: 14px; color: rgba(255,255,255,0.85); font-weight: 500; }
.logout-btn { padding: 7px 18px; font-size: 13px; color: #fff; background: rgba(255,255,255,0.15); border-radius: 6px; transition: all 0.2s; font-weight: 500; }
.logout-btn:hover { background: rgba(255,255,255,0.25); }
.main-content { flex: 1; max-width: 1200px; margin: 0 auto; padding: 24px; width: 100%; position: relative; z-index: 1; }
</style>
