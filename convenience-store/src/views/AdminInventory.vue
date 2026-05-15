<template>
  <div class="page"><h2 class="page-title">出入库管理</h2>
    <div class="card" style="margin-bottom:16px;">
      <h3 style="font-size:15px;font-weight:600;margin-bottom:12px;">新增出入库记录</h3>
      <div class="add-row">
        <select v-model="form.productId" class="sel"><option value="">选择商品</option><option v-for="p in products" :key="p.id" :value="p.id">{{ p.name }}（库存:{{ p.stock }}）</option></select>
        <select v-model="form.type" class="sel"><option value="入库">入库</option><option value="出库">出库</option></select>
        <input v-model.number="form.quantity" type="number" min="1" placeholder="数量" class="inp" />
        <input v-model="form.note" placeholder="备注" class="inp" />
        <button class="btn btn-primary" @click="addLog">提交</button>
      </div>
    </div>
    <div class="card"><table class="table">
      <thead><tr><th>ID</th><th>商品</th><th>类型</th><th>数量</th><th>库存变化</th><th>操作人</th><th>备注</th><th>时间</th></tr></thead>
      <tbody>
        <tr v-for="l in logs" :key="l.id">
          <td>{{ l.id }}</td><td>{{ l.productName }}</td>
          <td><span :class="['tag', l.type==='入库'?'in':'out']">{{ l.type }}</span></td>
          <td>{{ l.quantity }}</td><td>{{ l.stockBefore }} → {{ l.stockAfter }}</td>
          <td>{{ l.operator }}</td><td style="color:#999;font-size:12px;">{{ l.note }}</td>
          <td style="font-size:12px;color:#999;">{{ l.createdAt }}</td>
        </tr>
      </tbody>
    </table></div>
  </div>
</template>
<script setup>
import { ref, onMounted } from 'vue'; import { dataManager } from '../utils/dataManager';
const logs = ref([]); const products = ref([]);
const form = ref({ productId: '', type: '入库', quantity: 1, note: '' });
const load = async () => {
  logs.value = await dataManager.getAllInventory();
  products.value = await dataManager.getProducts();
};
const addLog = async () => {
  if (!form.value.productId) return;
  const p = products.value.find(x => x.id === form.value.productId);
  const user = await dataManager.getCurrentUser();
  const log = {
    productId: p.id, productName: p.name, type: form.value.type,
    quantity: form.value.quantity, stockBefore: p.stock,
    stockAfter: form.value.type === '入库' ? p.stock + form.value.quantity : p.stock - form.value.quantity,
    operator: user ? user.name : '', note: form.value.note,
    createdAt: new Date().toLocaleString('zh-CN', { hour12: false })
  };
  await dataManager.addInventory(log);
  p.stock = log.stockAfter;
  await dataManager.updateProduct(p);
  form.value = { productId: '', type: '入库', quantity: 1, note: '' };
  load();
};
onMounted(load);
</script>
<style scoped>
.page-title { font-size:20px; font-weight:700; color:#fff; margin-bottom:20px; }
.card { background:#fff; border-radius:10px; padding:20px; }
.add-row { display:flex; gap:10px; flex-wrap:wrap; }
.sel,.inp { height:38px; padding:0 12px; border:1.5px solid #e0e0e0; border-radius:8px; font-size:13px; }
.sel { min-width:160px; }
.inp { flex:1; min-width:100px; }
.table { width:100%; border-collapse:collapse; }
.table th { text-align:left; padding:10px; font-size:13px; font-weight:600; color:#999; border-bottom:2px solid #f0f0f0; }
.table td { padding:10px; font-size:14px; border-bottom:1px solid #f5f5f5; }
.tag { padding:2px 10px; border-radius:4px; font-size:12px; }
.tag.in { background:#f6ffed; color:#52c41a; }
.tag.out { background:#fff2f0; color:#ff4d4f; }
.btn { padding:8px 18px; border-radius:6px; font-size:13px; border:1.5px solid #e0e0e0; background:#fff; color:#333; cursor:pointer; }
.btn-primary { background:linear-gradient(135deg,#ff5000,#ff6a00); color:#fff; border-color:transparent; }
</style>
