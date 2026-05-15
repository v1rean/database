<template>
  <div class="page"><h2 class="page-title">通知公告管理</h2>
    <div class="card" style="margin-bottom:16px;">
      <div class="add-col">
        <input v-model="form.title" placeholder="公告标题" class="inp" />
        <textarea v-model="form.content" placeholder="公告内容" class="ta" rows="3"></textarea>
        <button class="btn btn-primary" @click="addAnn">发布公告</button>
      </div>
    </div>
    <div class="card"><table class="table">
      <thead><tr><th>ID</th><th>标题</th><th>内容</th><th>状态</th><th>时间</th><th>操作</th></tr></thead>
      <tbody>
        <tr v-for="a in list" :key="a.id">
          <td>{{ a.id }}</td><td><input v-model="a.title" class="ei" /></td>
          <td><input v-model="a.content" class="ei" style="width:300px;" /></td>
          <td><label class="toggle"><input type="checkbox" v-model="a.active" /><span class="slider"></span></label></td>
          <td style="font-size:12px;color:#999;">{{ a.createdAt }}</td>
          <td><button class="btn btn-sm" @click="update(a)">保存</button><button class="btn btn-sm btn-danger" @click="del(a.id)" style="margin-left:6px;">删除</button></td>
        </tr>
      </tbody>
    </table></div>
  </div>
</template>
<script setup>
import { ref, onMounted } from 'vue'; import { dataManager } from '../utils/dataManager';
const list = ref([]);
const form = ref({ title: '', content: '' });
const load = async () => { list.value = await dataManager.getAllAnnouncements(); };
const addAnn = async () => {
  if (!form.value.title) return;
  await dataManager.addAnnouncement({ ...form.value, active: true, createdAt: new Date().toLocaleString('zh-CN', { hour12: false }) });
  form.value = { title: '', content: '' }; load();
};
const update = async (a) => { await dataManager.updateAnnouncement(a); };
const del = async (id) => { if (confirm('确定删除？')) { await dataManager.deleteAnnouncement(id); load(); } };
onMounted(load);
</script>
<style scoped>
.page-title { font-size:20px; font-weight:700; color:#fff; margin-bottom:20px; }
.card { background:#fff; border-radius:10px; padding:20px; }
.add-col { display:flex; flex-direction:column; gap:10px; }
.inp { height:38px; padding:0 12px; border:1.5px solid #e0e0e0; border-radius:8px; font-size:13px; }
.ta { padding:10px 12px; border:1.5px solid #e0e0e0; border-radius:8px; font-size:13px; resize:vertical; font-family:inherit; }
.ta:focus { border-color:#ff5000; }
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
