<template>
  <div class="purchases-page">
    <div class="page-header">
      <h2 class="page-title">{{ isAdmin ? '订单管理' : '我的订单' }}</h2>
      <span v-if="purchases.length" class="order-count">共 {{ purchases.length }} 笔订单</span>
    </div>

    <!-- 管理员视图：表格 -->
    <template v-if="isAdmin">
      <div v-if="purchases.length === 0" class="empty-state">
        <svg viewBox="0 0 24 24" width="48" height="48" fill="none" stroke="#ddd" stroke-width="1.5">
          <rect x="2" y="7" width="20" height="14" rx="2"/><path d="M16 21V5a2 2 0 0 0-2-2h-4a2 2 0 0 0-2 2v16"/>
        </svg>
        <p>暂无订单记录</p>
      </div>
      <div v-else class="card">
        <table class="admin-table">
          <thead>
            <tr>
              <th>用户</th>
              <th>商品</th>
              <th>总价</th>
              <th>日期</th>
              <th>操作</th>
            </tr>
          </thead>
          <tbody>
            <tr v-for="purchase in purchases" :key="purchase.id">
              <td><span class="user-badge">{{ purchase.username }}</span></td>
              <td>
                <div v-for="item in purchase.items" :key="item.productId" class="order-item">
                  {{ item.name }} × {{ item.quantity }}
                  <span class="item-price">¥{{ (item.price * item.quantity).toFixed(1) }}</span>
                </div>
              </td>
              <td class="total-price">¥{{ purchase.total }}</td>
              <td class="date-cell">{{ purchase.date }}</td>
              <td>
                <button class="btn btn-sm btn-danger" @click="deletePurchase(purchase.id)">删除</button>
              </td>
            </tr>
          </tbody>
        </table>
      </div>
    </template>

    <!-- 用户视图：订单卡片 -->
    <template v-else>
      <div v-if="purchases.length === 0" class="empty-state">
        <svg viewBox="0 0 24 24" width="48" height="48" fill="none" stroke="#ddd" stroke-width="1.5">
          <path d="M6 2L3 6v14a2 2 0 0 0 2 2h14a2 2 0 0 0 2-2V6l-3-4z"/>
          <line x1="3" y1="6" x2="21" y2="6"/><path d="M16 10a4 4 0 0 1-8 0"/>
        </svg>
        <p>还没有购买记录</p>
        <router-link to="/user/products" class="go-shop-btn">去逛逛</router-link>
      </div>
      <div v-else class="order-list">
        <div v-for="purchase in purchases" :key="purchase.id" class="order-card">
          <div class="order-header">
            <div class="order-meta">
              <span class="order-id">订单 #{{ purchase.id }}</span>
              <span class="order-status">已完成</span>
            </div>
            <span class="order-date">{{ purchase.date }}</span>
          </div>
          <div class="order-body">
            <div v-for="item in purchase.items" :key="item.productId" class="order-item-row">
              <div class="item-img-placeholder" :style="{ background: itemColor(item.name) }">
                <span class="item-emoji">{{ itemEmoji(item.name) }}</span>
              </div>
              <div class="item-detail">
                <div class="item-name">{{ item.name }}</div>
                <div class="item-sub">¥{{ item.price }} × {{ item.quantity }}</div>
              </div>
              <div class="item-total">¥{{ (item.price * item.quantity).toFixed(1) }}</div>
            </div>
          </div>
          <div class="order-footer">
            <span class="order-total-label">合计</span>
            <span class="order-total-price">¥{{ purchase.total }}</span>
          </div>
        </div>
      </div>
    </template>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import { dataManager } from '../utils/dataManager';

const props = defineProps({ isAdmin: Boolean });
const purchases = ref([]);
const user = ref(null);

const loadPurchases = async () => {
  user.value = await dataManager.getCurrentUser();
  if (props.isAdmin) {
    purchases.value = await dataManager.getAllPurchases();
  } else {
    purchases.value = await dataManager.getUserPurchases(user.value.id);
  }
};

const deletePurchase = async (id) => {
  if (confirm('确定删除该订单？')) {
    await dataManager.deletePurchase(id);
    loadPurchases();
  }
};

const itemColor = (name) => {
  const colors = ['#667eea', '#f093fb', '#4facfe', '#43e97b', '#fa8231', '#a55eea', '#20bf6b', '#eb3b5a'];
  let hash = 0;
  for (let i = 0; i < name.length; i++) hash = name.charCodeAt(i) + ((hash << 5) - hash);
  return colors[Math.abs(hash) % colors.length];
};

const itemEmoji = (name) => {
  if (name.includes('可乐') || name.includes('饮料')) return '🥤';
  if (name.includes('水')) return '💧';
  if (name.includes('面')) return '🍜';
  if (name.includes('肠')) return '🌭';
  if (name.includes('薯片')) return '🥔';
  if (name.includes('巧克力')) return '🍫';
  if (name.includes('巾')) return '🧻';
  if (name.includes('刷')) return '🪥';
  return '📦';
};

onMounted(loadPurchases);
</script>

<style scoped>
.purchases-page {
  position: relative;
}

.page-header {
  display: flex;
  align-items: baseline;
  gap: 12px;
  margin-bottom: 20px;
}

.page-title {
  font-size: 20px;
  font-weight: 700;
  color: #1a1a1a;
}

.order-count {
  font-size: 13px;
  color: #bbb;
}

/* ===== 卡片 ===== */
.card {
  background: #fff;
  border-radius: 10px;
  padding: 24px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
}

/* ===== 管理员表格 ===== */
.admin-table {
  width: 100%;
  border-collapse: collapse;
}

.admin-table th {
  text-align: left;
  padding: 12px 10px;
  font-size: 13px;
  font-weight: 600;
  color: #999;
  border-bottom: 2px solid #f0f0f0;
}

.admin-table td {
  padding: 14px 10px;
  font-size: 14px;
  border-bottom: 1px solid #f5f5f5;
  vertical-align: top;
}

.admin-table tr:hover td {
  background: #fafafa;
}

.user-badge {
  display: inline-block;
  padding: 3px 10px;
  background: #e6f7ff;
  color: #1890ff;
  border-radius: 4px;
  font-size: 13px;
  font-weight: 500;
}

.order-item {
  font-size: 13px;
  color: #333;
  margin-bottom: 4px;
}

.order-item:last-child {
  margin-bottom: 0;
}

.item-price {
  color: #999;
  margin-left: 6px;
}

.total-price {
  font-weight: 700;
  color: #ff5000;
  font-size: 15px;
}

.date-cell {
  font-size: 13px;
  color: #999;
  white-space: nowrap;
}

.btn {
  padding: 8px 18px;
  border-radius: 6px;
  font-size: 13px;
  font-weight: 500;
  border: 1.5px solid #e0e0e0;
  background: #fff;
  color: #333;
  transition: all 0.2s;
}

.btn-sm {
  padding: 5px 12px;
  font-size: 12px;
}

.btn-danger {
  color: #ff4d4f;
  border-color: #ffd9d9;
}

.btn-danger:hover {
  background: #fff2f0;
  border-color: #ff4d4f;
}

/* ===== 用户视图 - 订单卡片列表 ===== */
.order-list {
  display: flex;
  flex-direction: column;
  gap: 14px;
}

.order-card {
  background: #fff;
  border-radius: 12px;
  overflow: hidden;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
  transition: box-shadow 0.2s;
}

.order-card:hover {
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.1);
}

.order-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 14px 20px;
  background: #fafafa;
  border-bottom: 1px solid #f0f0f0;
}

.order-meta {
  display: flex;
  align-items: center;
  gap: 10px;
}

.order-id {
  font-size: 13px;
  font-weight: 600;
  color: #333;
}

.order-status {
  font-size: 11px;
  padding: 2px 8px;
  background: #f6ffed;
  color: #52c41a;
  border-radius: 4px;
  border: 1px solid #b7eb8f;
}

.order-date {
  font-size: 12px;
  color: #bbb;
}

.order-body {
  padding: 14px 20px;
}

.order-item-row {
  display: flex;
  align-items: center;
  gap: 14px;
  padding: 8px 0;
  border-bottom: 1px solid #f5f5f5;
}

.order-item-row:last-child {
  border-bottom: none;
}

.item-img-placeholder {
  width: 48px;
  height: 48px;
  border-radius: 8px;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
}

.item-emoji {
  font-size: 22px;
}

.item-detail {
  flex: 1;
  min-width: 0;
}

.item-name {
  font-size: 14px;
  font-weight: 600;
  color: #1a1a1a;
  margin-bottom: 3px;
}

.item-sub {
  font-size: 12px;
  color: #bbb;
}

.item-total {
  font-size: 14px;
  font-weight: 600;
  color: #333;
  white-space: nowrap;
}

.order-footer {
  display: flex;
  justify-content: flex-end;
  align-items: center;
  gap: 8px;
  padding: 12px 20px;
  border-top: 1px solid #f0f0f0;
  background: #fafafa;
}

.order-total-label {
  font-size: 13px;
  color: #999;
}

.order-total-price {
  font-size: 18px;
  font-weight: 700;
  color: #ff5000;
}

/* ===== 空状态 ===== */
.empty-state {
  text-align: center;
  padding: 60px 20px;
  background: #fff;
  border-radius: 12px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
}

.empty-state p {
  margin-top: 12px;
  font-size: 14px;
  color: #ccc;
  margin-bottom: 20px;
}

.go-shop-btn {
  display: inline-block;
  padding: 10px 28px;
  background: linear-gradient(135deg, #ff5000, #ff6a00);
  color: #fff;
  border-radius: 8px;
  font-size: 14px;
  font-weight: 600;
  transition: opacity 0.2s;
}

.go-shop-btn:hover {
  opacity: 0.9;
}
</style>
