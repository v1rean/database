<template>
  <div class="checkout-page">
    <div class="page-header">
      <h2 class="page-title">确认订单</h2>
    </div>

    <!-- 未提交：订单概览 -->
    <template v-if="!submitted">
      <div class="card">
        <h3 class="section-title">
          <svg viewBox="0 0 24 24" width="18" height="18" fill="none" stroke="#ff5000" stroke-width="2">
            <circle cx="9" cy="21" r="1"/><circle cx="20" cy="21" r="1"/>
            <path d="M1 1h4l2.68 13.39a2 2 0 0 0 2 1.61h9.72a2 2 0 0 0 2-1.61L23 6H6"/>
          </svg>
          商品清单
        </h3>
        <div v-for="item in cartStore.items" :key="item.productId" class="checkout-item">
          <div class="checkout-item-img" :style="{ background: itemColor(item.name) }">
            <span class="checkout-item-emoji">{{ itemEmoji(item.name) }}</span>
          </div>
          <div class="checkout-item-info">
            <div class="checkout-item-name">{{ item.name }}</div>
            <div class="checkout-item-price">¥{{ item.price }}</div>
          </div>
          <div class="checkout-item-qty">× {{ item.quantity }}</div>
          <div class="checkout-item-total">¥{{ (item.price * item.quantity).toFixed(1) }}</div>
        </div>
        <div v-if="cartStore.items.length === 0" class="empty-cart">
          <p>购物车是空的</p>
          <button class="back-btn" @click="goBack">去逛逛</button>
        </div>
      </div>

      <div class="card summary-card">
        <div class="summary-row">
          <span class="summary-label">商品数量</span>
          <span class="summary-value">{{ cartStore.count }} 件</span>
        </div>
        <div class="summary-row total-row">
          <span class="summary-label">合计</span>
          <span class="summary-price">¥{{ cartStore.total.toFixed(1) }}</span>
        </div>
      </div>

      <div class="action-bar">
        <button class="btn btn-outline" @click="goBack">返回购物车</button>
        <button class="btn btn-primary" :class="{ loading: submitting }" @click="submitCheckout" :disabled="cartStore.items.length === 0">
          <span v-if="!submitting">确认结算</span>
          <span v-else class="spinner"></span>
        </button>
      </div>
    </template>

    <!-- 已提交：结算结果 -->
    <template v-else>
      <div class="result-card" :class="result.success ? 'result-success' : 'result-fail'">
        <div class="result-icon">
          <svg v-if="result.success" viewBox="0 0 24 24" width="48" height="48" fill="none" stroke="currentColor" stroke-width="2">
            <path d="M22 11.08V12a10 10 0 1 1-5.93-9.14"/><polyline points="22 4 12 14.01 9 11.01"/>
          </svg>
          <svg v-else viewBox="0 0 24 24" width="48" height="48" fill="none" stroke="currentColor" stroke-width="2">
            <circle cx="12" cy="12" r="10"/><line x1="15" y1="9" x2="9" y2="15"/><line x1="9" y1="9" x2="15" y2="15"/>
          </svg>
        </div>
        <h3 class="result-title">{{ result.success ? '结算成功!' : '结算失败' }}</h3>
        <p class="result-msg">{{ result.message }}</p>
        <div class="result-actions">
          <button class="btn btn-primary" @click="goBack">{{ result.success ? '返回商品页' : '返回重新结算' }}</button>
        </div>
      </div>
    </template>
  </div>
</template>

<script setup>
import { ref } from 'vue';
import { useRouter } from 'vue-router';
import { cartStore } from '../utils/cartStore';
import { cartUI } from '../utils/cartUI';
import { dataManager } from '../utils/dataManager';

const router = useRouter();
const submitted = ref(false);
const submitting = ref(false);
const result = ref({ success: false, message: '' });

const goBack = () => {
  router.push('/user/products');
};

const submitCheckout = async () => {
  if (cartStore.items.length === 0) return;
  const user = await dataManager.getCurrentUser();
  if (!user) {
    router.push('/');
    return;
  }
  submitting.value = true;
  const purchase = {
    userId: user.id,
    username: user.name,
    items: cartStore.items.map(i => ({
      productId: i.productId,
      name: i.name,
      price: i.price,
      quantity: i.quantity
    })),
    total: cartStore.total,
    date: new Date().toLocaleString('zh-CN', { hour12: false })
  };
  try {
    const res = await dataManager.checkout(purchase);
    result.value = res;
    if (res.success) {
      cartStore.clear();
    }
  } catch {
    result.value = { success: false, message: '网络错误，请重试' };
  } finally {
    submitting.value = false;
    submitted.value = true;
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
</script>

<style scoped>
.checkout-page {
  max-width: 640px;
  margin: 0 auto;
  position: relative;
  z-index: 1;
}

.page-header {
  margin-bottom: 20px;
}

.page-title {
  font-size: 20px;
  font-weight: 700;
  color: #1a1a1a;
}

.card {
  background: #fff;
  border-radius: 12px;
  padding: 20px;
  margin-bottom: 16px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
}

.section-title {
  font-size: 15px;
  font-weight: 600;
  color: #333;
  display: flex;
  align-items: center;
  gap: 6px;
  margin-bottom: 16px;
  padding-bottom: 12px;
  border-bottom: 1px solid #f0f0f0;
}

.checkout-item {
  display: flex;
  align-items: center;
  gap: 14px;
  padding: 10px 0;
  border-bottom: 1px solid #f8f8f8;
}

.checkout-item:last-child {
  border-bottom: none;
}

.checkout-item-img {
  width: 44px;
  height: 44px;
  border-radius: 8px;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
}

.checkout-item-emoji {
  font-size: 20px;
}

.checkout-item-info {
  flex: 1;
  min-width: 0;
}

.checkout-item-name {
  font-size: 14px;
  font-weight: 600;
  color: #1a1a1a;
  margin-bottom: 2px;
}

.checkout-item-price {
  font-size: 12px;
  color: #bbb;
}

.checkout-item-qty {
  font-size: 14px;
  color: #999;
  white-space: nowrap;
}

.checkout-item-total {
  font-size: 14px;
  font-weight: 600;
  color: #333;
  min-width: 60px;
  text-align: right;
}

.empty-cart {
  text-align: center;
  padding: 30px;
  color: #ccc;
}

.empty-cart p {
  margin-bottom: 12px;
}

.summary-card {
  padding: 16px 20px;
}

.summary-row {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 6px 0;
}

.summary-label {
  font-size: 14px;
  color: #999;
}

.summary-value {
  font-size: 14px;
  color: #333;
}

.total-row {
  margin-top: 6px;
  padding-top: 10px;
  border-top: 1px solid #f0f0f0;
}

.summary-price {
  font-size: 22px;
  font-weight: 700;
  color: #ff5000;
}

.action-bar {
  display: flex;
  gap: 12px;
  margin-top: 8px;
}

.btn {
  flex: 1;
  height: 46px;
  border-radius: 10px;
  font-size: 15px;
  font-weight: 600;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: all 0.2s;
}

.btn-outline {
  background: #fff;
  color: #666;
  border: 1.5px solid #e0e0e0;
}

.btn-outline:hover {
  border-color: #ff5000;
  color: #ff5000;
}

.btn-primary {
  background: linear-gradient(135deg, #ff5000, #ff6a00);
  color: #fff;
  border: none;
}

.btn-primary:hover {
  box-shadow: 0 4px 16px rgba(255, 80, 0, 0.35);
  transform: translateY(-1px);
}

.btn-primary:disabled {
  opacity: 0.5;
  cursor: not-allowed;
  transform: none;
  box-shadow: none;
}

.btn-primary.loading {
  opacity: 0.8;
  pointer-events: none;
}

.spinner {
  width: 22px;
  height: 22px;
  border: 3px solid rgba(255, 255, 255, 0.3);
  border-top-color: #fff;
  border-radius: 50%;
  animation: spin 0.6s linear infinite;
}

@keyframes spin {
  to { transform: rotate(360deg); }
}

/* ===== 结算结果 ===== */
.result-card {
  text-align: center;
  padding: 48px 32px;
  background: #fff;
  border-radius: 16px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
}

.result-success .result-icon {
  color: #52c41a;
}

.result-fail .result-icon {
  color: #ff4d4f;
}

.result-icon {
  margin-bottom: 16px;
}

.result-title {
  font-size: 22px;
  font-weight: 700;
  margin-bottom: 8px;
  color: #1a1a1a;
}

.result-msg {
  font-size: 14px;
  color: #999;
  margin-bottom: 28px;
}

.result-actions .btn {
  max-width: 200px;
  margin: 0 auto;
}
</style>
