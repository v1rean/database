<template>
  <div class="page"><h2 class="page-title">轮播图管理</h2>
    <div class="card" style="margin-bottom:16px;">
      <div class="add-row">
        <input v-model="form.title" placeholder="标题" class="inp" />
        <input v-model="form.imageUrl" placeholder="图片路径（如 /images/carousel/1.jpg）" class="inp" style="flex:2;" />
        <input v-model="form.linkUrl" placeholder="链接" class="inp" />
        <input v-model.number="form.sortOrder" type="number" placeholder="排序" class="inp" style="width:80px;" />
        <button class="btn btn-primary" @click="addCarousel">添加</button>
      </div>
    </div>
    <div class="card"><table class="table">
      <thead><tr><th>ID</th><th>标题</th><th>图片路径</th><th>排序</th><th>状态</th><th>操作</th></tr></thead>
      <tbody>
        <tr v-for="c in list" :key="c.id">
          <td>{{ c.id }}</td><td><input v-model="c.title" class="ei" /></td>
          <td><input v-model="c.imageUrl" class="ei" style="width:220px;" /></td>
          <td><input v-model.number="c.sortOrder" type="number" class="ei" style="width:60px;" /></td>
          <td><label class="toggle"><input type="checkbox" v-model="c.active" /><span class="slider"></span></label></td>
          <td><button class="btn btn-sm" @click="update(c)">保存</button><button class="btn btn-sm btn-danger" @click="del(c.id)" style="margin-left:6px;">删除</button></td>
        </tr>
      </tbody>
    </table></div>
    <p style="color:rgba(255,255,255,0.5);font-size:12px;margin-top:8px;">图片文件请放入 public/images/carousel/ 目录</p>
  </div>
</template>
<script setup>
import { ref, onMounted } from 'vue'; import { dataManager } from '../utils/dataManager';
const list = ref([]);
const form = ref({ title: '', imageUrl: '', linkUrl: '/user/products', sortOrder: 1, active: true });
const load = async () => { list.value = await dataManager.getAllCarousels(); };
const addCarousel = async () => { await dataManager.addCarousel(form.value); form.value = { title: '', imageUrl: '', linkUrl: '/user/products', sortOrder: 1, active: true }; load(); };
const update = async (c) => { await dataManager.updateCarousel(c); };
const del = async (id) => { if (confirm('确定删除？')) { await dataManager.deleteCarousel(id); load(); } };
onMounted(load);
</script>
<style scoped>
.page-title { font-size:20px; font-weight:700; color:#fff; margin-bottom:20px; }
.card { background:#fff; border-radius:10px; padding:20px; }
.add-row { display:flex; gap:10px; flex-wrap:wrap; }
.inp { height:38px; padding:0 12px; border:1.5px solid #e0e0e0; border-radius:8px; font-size:13px; flex:1; min-width:100px; }
.table { width:100%; border-collapse:collapse; }
.table th { text-align:left; padding:10px; font-size:13px; font-weight:600; color:#999; border-bottom:2px solid #f0f0f0; }
.table td { padding:10px; font-size:14px; border-bottom:1px solid #f5f5f5; }
.ei { height:32px; padding:0 8px; border:1.5px solid #e0e0e0; border-radius:6px; font-size:13px; width:100%; }
.ei:focus { border-color:#ff5000; }
.toggle { position:relative; display:inline-block; width:34px; height:20px; }
.toggle input { opacity:0; width:0; height:0; }
.slider { position:absolute; inset:0; cursor:pointer; background:#ddd; border-radius:20px; transition:0.3s; }
.slider::before { content:''; position:absolute; left:3px; bottom:3px; width:14px; height:14px; background:#fff; border-radius:50%; transition:0.3s; }
.toggle input:checked+.slider { background:#ff5000; }
.toggle input:checked+.slider::before { transform:translateX(14px); }
.btn { padding:8px 18px; border-radius:6px; font-size:13px; border:1.5px solid #e0e0e0; background:#fff; color:#333; cursor:pointer; }
.btn-sm { padding:5px 12px; font-size:12px; }
.btn-primary { background:linear-gradient(135deg,#ff5000,#ff6a00); color:#fff; border-color:transparent; }
.btn-danger { color:#ff4d4f; border-color:#ffd9d9; }
.btn-danger:hover { background:#fff2f0; }
</style>
