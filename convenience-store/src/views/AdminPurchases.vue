<template>
  <div class="page">
    <h2 class="page-title">订单管理</h2>

    <!-- 筛选栏 -->
    <div class="card filter-card">
      <div class="filter-row">
        <div class="filter-group">
          <label>开始日期</label>
          <input v-model="fStart" type="date" class="fi" />
        </div>
        <div class="filter-group">
          <label>结束日期</label>
          <input v-model="fEnd" type="date" class="fi" />
        </div>
        <div class="filter-group">
          <label>收银员/用户</label>
          <input v-model="fUsername" placeholder="输入用户名" class="fi" />
        </div>
        <button class="btn btn-primary" @click="doSearch">搜索</button>
        <button class="btn" @click="resetSearch">重置</button>
      </div>
    </div>

    <!-- 订单列表 -->
    <div class="card"><table class="table">
      <thead><tr><th>ID</th><th>用户</th><th>商品</th><th>总价</th><th>支付方式</th><th>日期</th><th>操作</th></tr></thead>
      <tbody>
        <tr v-for="p in purchases" :key="p.id">
          <td>{{ p.id }}</td>
          <td><span class="badge user">{{ p.username }}</span></td>
          <td>
            <span class="item-preview" @click="showDetail(p)">查看明细 ({{ p.items ? p.items.length : 0 }} 件) ›</span>
          </td>
          <td class="price">¥{{ p.total }}</td>
          <td><span class="badge pay">{{ p.paymentMethod || '-' }}</span></td>
          <td class="date">{{ p.date }}</td>
          <td>
            <button class="btn btn-sm btn-danger" @click="doRefund(p)" :disabled="isRefunded(p)">{{ isRefunded(p) ? '已退货' : '退货' }}</button>
            <button class="btn btn-sm" @click="delPurchase(p.id)" style="margin-left:4px;">删除</button>
          </td>
        </tr>
        <tr v-if="purchases.length === 0"><td colspan="7" class="empty-td">暂无订单</td></tr>
      </tbody>
    </table></div>

    <!-- 订单明细弹窗 -->
    <div v-if="detailOrder" class="modal-overlay" @click.self="detailOrder = null">
      <div class="modal-card">
        <div class="modal-header">
          <h3>订单 #{{ detailOrder.id }} 明细</h3>
          <button class="modal-close" @click="detailOrder = null">✕</button>
        </div>
        <div class="modal-body">
          <div class="detail-info">
            <span>用户：{{ detailOrder.username }}</span>
            <span>日期：{{ detailOrder.date }}</span>
            <span>支付：{{ detailOrder.paymentMethod || '-' }}</span>
          </div>
          <table class="detail-table">
            <thead><tr><th>商品</th><th>单价</th><th>数量</th><th>小计</th></tr></thead>
            <tbody>
              <tr v-for="item in detailOrder.items" :key="item.productId">
                <td>{{ item.name }}</td><td>¥{{ item.price }}</td>
                <td>{{ item.quantity }}</td><td class="price">¥{{ (item.price * item.quantity).toFixed(1) }}</td>
              </tr>
            </tbody>
            <tfoot><tr><td colspan="3" class="text-right">合计</td><td class="price">¥{{ detailOrder.total }}</td></tr></tfoot>
          </table>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import { dataManager } from '../utils/dataManager';

const purchases = ref([]);
const detailOrder = ref(null);
const fStart = ref(''); const fEnd = ref(''); const fUsername = ref('');

const load = async () => { purchases.value = await dataManager.getAllPurchases(); };

const doSearch = async () => {
  purchases.value = await dataManager.searchPurchases(fStart.value, fEnd.value, fUsername.value);
};

const resetSearch = () => {
  fStart.value = ''; fEnd.value = ''; fUsername.value = ''; load();
};

const showDetail = (p) => { detailOrder.value = p; };

const isRefunded = (p) => p.paymentMethod && p.paymentMethod.startsWith('[已退货]');

const doRefund = async (p) => {
  if (!confirm(`确定退货订单 #${p.id}？将恢复商品库存。`)) return;
  const user = await dataManager.getCurrentUser();
  const res = await dataManager.refundPurchase(p.id, user ? user.name : '管理员');
  alert(res.message);
  if (res.success) load();
};

const delPurchase = async (id) => {
  if (!confirm('确定删除该订单？')) return;
  await dataManager.deletePurchase(id);
  load();
};

onMounted(load);
</script>

<style scoped>
.page-title { font-size:20px; font-weight:700; color:#fff; margin-bottom:20px; }
.card { background:#fff; border-radius:10px; padding:20px; margin-bottom:16px; }

.filter-card { padding:16px 20px; }
.filter-row { display:flex; gap:12px; align-items:flex-end; flex-wrap:wrap; }
.filter-group { display:flex; flex-direction:column; gap:4px; }
.filter-group label { font-size:12px; color:#999; }
.fi { height:36px; padding:0 10px; border:1.5px solid #e0e0e0; border-radius:6px; font-size:13px; }
.fi:focus { border-color:#ff5000; }

.btn { padding:8px 18px; border-radius:6px; font-size:13px; border:1.5px solid #e0e0e0; background:#fff; color:#333; cursor:pointer; transition:all 0.2s; }
.btn-sm { padding:5px 12px; font-size:12px; }
.btn-primary { background:linear-gradient(135deg,#ff5000,#ff6a00); color:#fff; border-color:transparent; }
.btn-primary:hover { opacity:0.9; }
.btn-danger { color:#ff4d4f; border-color:#ffd9d9; }
.btn-danger:hover { background:#fff2f0; }
.btn:disabled { opacity:0.4; cursor:not-allowed; }

.table { width:100%; border-collapse:collapse; }
.table th { text-align:left; padding:10px; font-size:13px; font-weight:600; color:#999; border-bottom:2px solid #f0f0f0; }
.table td { padding:12px 10px; font-size:14px; border-bottom:1px solid #f5f5f5; }
.price { color:#ff5000; font-weight:600; }
.date { font-size:12px; color:#999; white-space:nowrap; }
.empty-td { text-align:center; color:#ccc; padding:30px !important; }

.badge { display:inline-block; padding:2px 8px; border-radius:4px; font-size:12px; }
.badge.user { background:#e6f7ff; color:#1890ff; }
.badge.pay { background:#f0f0f0; color:#666; }

.item-preview { color:#ff5000; cursor:pointer; font-size:13px; }
.item-preview:hover { text-decoration:underline; }

/* Modal */
.modal-overlay { position:fixed; inset:0; background:rgba(0,0,0,0.4); display:flex; align-items:center; justify-content:center; z-index:200; }
.modal-card { background:#fff; border-radius:14px; width:500px; max-width:90vw; max-height:80vh; overflow:auto; box-shadow:0 20px 60px rgba(0,0,0,0.15); }
.modal-header { display:flex; justify-content:space-between; align-items:center; padding:18px 22px; border-bottom:1px solid #f0f0f0; }
.modal-header h3 { font-size:16px; font-weight:700; color:#1a1a1a; }
.modal-close { width:28px; height:28px; border-radius:50%; background:#f5f5f5; color:#999; font-size:14px; display:flex; align-items:center; justify-content:center; }
.modal-close:hover { background:#ff4d4f; color:#fff; }
.modal-body { padding:18px 22px; }
.detail-info { display:flex; gap:16px; font-size:13px; color:#999; margin-bottom:16px; flex-wrap:wrap; }
.detail-table { width:100%; border-collapse:collapse; }
.detail-table th { text-align:left; padding:8px; font-size:12px; color:#999; border-bottom:2px solid #f0f0f0; }
.detail-table td { padding:8px; font-size:13px; border-bottom:1px solid #f5f5f5; }
.detail-table tfoot td { font-weight:700; font-size:14px; padding-top:10px; border-top:2px solid #f0f0f0; border-bottom:none; }
.text-right { text-align:right; }
</style>
