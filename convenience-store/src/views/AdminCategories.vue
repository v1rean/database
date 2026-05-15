<template>
  <div class="page"><h2 class="page-title">商品分类管理</h2>
    <div class="card" style="margin-bottom:16px;">
      <div class="add-row"><input v-model="newName" placeholder="分类名称" /><input v-model="newDesc" placeholder="描述" /><button class="btn btn-primary" @click="addCat">添加</button></div>
    </div>
    <div class="card"><table class="table">
      <thead><tr><th>ID</th><th>名称</th><th>描述</th><th>操作</th></tr></thead>
      <tbody>
        <tr v-for="c in cats" :key="c.id">
          <td>{{ c.id }}</td>
          <td><input v-model="c.name" class="edit-input" /></td>
          <td><input v-model="c.description" class="edit-input" /></td>
          <td><button class="btn btn-sm" @click="updateCat(c)">保存</button><button class="btn btn-sm btn-danger" @click="delCat(c.id)" style="margin-left:6px;">删除</button></td>
        </tr>
      </tbody>
    </table></div>
  </div>
</template>
<script setup>
import { ref, onMounted } from 'vue'; import { dataManager } from '../utils/dataManager';
const cats = ref([]); const newName = ref(''); const newDesc = ref('');
const load = async () => { cats.value = await dataManager.getAllCategories(); };
const addCat = async () => { if (!newName.value) return; await dataManager.addCategory({ name: newName.value, description: newDesc.value }); newName.value=''; newDesc.value=''; load(); };
const updateCat = async (c) => { await dataManager.updateCategory(c); };
const delCat = async (id) => { if (confirm('确定删除？')) { await dataManager.deleteCategory(id); load(); } };
onMounted(load);
</script>
<style scoped>
.page-title { font-size: 20px; font-weight: 700; color: #fff; margin-bottom: 20px; }
.card { background: #fff; border-radius: 10px; padding: 20px; }
.add-row { display: flex; gap: 10px; }
.add-row input { flex:1; height:38px; padding:0 12px; border:1.5px solid #e0e0e0; border-radius:8px; font-size:13px; }
.table { width:100%; border-collapse:collapse; }
.table th { text-align:left; padding:10px; font-size:13px; font-weight:600; color:#999; border-bottom:2px solid #f0f0f0; }
.table td { padding:10px; font-size:14px; border-bottom:1px solid #f5f5f5; }
.edit-input { width:100%; height:34px; padding:0 10px; border:1.5px solid #e0e0e0; border-radius:6px; font-size:13px; }
.edit-input:focus { border-color:#ff5000; }
.btn { padding:8px 18px; border-radius:6px; font-size:13px; border:1.5px solid #e0e0e0; background:#fff; color:#333; transition:all 0.2s; cursor:pointer; }
.btn-sm { padding:5px 12px; font-size:12px; }
.btn-primary { background:linear-gradient(135deg,#ff5000,#ff6a00); color:#fff; border-color:transparent; }
.btn-danger { color:#ff4d4f; border-color:#ffd9d9; }
.btn-danger:hover { background:#fff2f0; }
</style>
