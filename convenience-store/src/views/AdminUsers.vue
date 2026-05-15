<template>
  <div class="page"><h2 class="page-title">用户管理</h2>
    <div class="card"><table class="table">
      <thead><tr><th>ID</th><th>用户名</th><th>名称</th><th>角色</th><th>个性推荐</th><th>操作</th></tr></thead>
      <tbody>
        <tr v-for="u in users" :key="u.id">
          <td>{{ u.id }}</td><td>{{ u.username }}</td><td>{{ u.name }}</td>
          <td><span :class="['tag', u.role==='admin'?'admin':'user']">{{ u.role==='admin'?'管理员':'用户' }}</span></td>
          <td>{{ u.recommendEnabled ? '开' : '关' }}</td>
          <td><button class="btn btn-sm btn-danger" @click="delUser(u.id)" :disabled="u.role==='admin'">删除</button></td>
        </tr>
      </tbody>
    </table></div>
  </div>
</template>
<script setup>
import { ref, onMounted } from 'vue'; import { dataManager } from '../utils/dataManager';
const users = ref([]);
const load = async () => { users.value = await dataManager.getAllUsers(); };
const delUser = async (id) => { if (confirm('确定删除该用户？')) { await dataManager.deleteUser(id); load(); } };
onMounted(load);
</script>
<style scoped>
.page { /*unused*/ }
.page-title { font-size: 20px; font-weight: 700; color: #fff; margin-bottom: 20px; }
.card { background: #fff; border-radius: 10px; padding: 20px; }
.table { width: 100%; border-collapse: collapse; }
.table th { text-align: left; padding: 10px; font-size: 13px; font-weight: 600; color: #999; border-bottom: 2px solid #f0f0f0; }
.table td { padding: 12px 10px; font-size: 14px; border-bottom: 1px solid #f5f5f5; }
.tag { padding: 2px 10px; border-radius: 4px; font-size: 12px; }
.tag.admin { background: #fff5ef; color: #ff5000; }
.tag.user { background: #e6f7ff; color: #1890ff; }
.btn { padding: 8px 18px; border-radius: 6px; font-size: 13px; border: 1.5px solid #e0e0e0; background: #fff; color: #333; transition: all 0.2s; }
.btn-sm { padding: 5px 12px; font-size: 12px; }
.btn-danger { color: #ff4d4f; border-color: #ffd9d9; }
.btn-danger:hover { background: #fff2f0; }
.btn:disabled { opacity: 0.4; cursor: not-allowed; }
</style>
