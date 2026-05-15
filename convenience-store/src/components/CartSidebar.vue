<template>
  <div
    class="cart-wrapper"
    @mouseenter="onEnter"
    @mouseleave="onLeave"
  >
    <div class="cart-trigger" @click="onToggle">
      <svg viewBox="0 0 24 24" width="22" height="22" fill="none" stroke="currentColor" stroke-width="2">
        <circle cx="9" cy="21" r="1"/><circle cx="20" cy="21" r="1"/>
        <path d="M1 1h4l2.68 13.39a2 2 0 0 0 2 1.61h9.72a2 2 0 0 0 2-1.61L23 6H6"/>
      </svg>
      <span v-if="cartStore.count > 0" class="cart-badge">{{ cartStore.count > 99 ? '99+' : cartStore.count }}</span>
    </div>

    <transition name="slide">
      <div v-show="cartUI.expanded" class="cart-panel">
        <div class="cart-header">
          <h3 class="cart-title">
            <svg viewBox="0 0 24 24" width="18" height="18" fill="none" stroke="#ff5000" stroke-width="2">
              <circle cx="9" cy="21" r="1"/><circle cx="20" cy="21" r="1"/>
              <path d="M1 1h4l2.68 13.39a2 2 0 0 0 2 1.61h9.72a2 2 0 0 0 2-1.61L23 6H6"/>
            </svg>
            购物车
          </h3>
          <button class="cart-close" @click="onClose" title="收起">
            <svg viewBox="0 0 24 24" width="16" height="16" fill="none" stroke="#999" stroke-width="2">
              <line x1="18" y1="6" x2="6" y2="18"/><line x1="6" y1="6" x2="18" y2="18"/>
            </svg>
          </button>
        </div>

        <div v-if="cartStore.items.length === 0" class="cart-empty">
          <svg viewBox="0 0 24 24" width="40" height="40" fill="none" stroke="#e0e0e0" stroke-width="1.5">
            <circle cx="9" cy="21" r="1"/><circle cx="20" cy="21" r="1"/>
            <path d="M1 1h4l2.68 13.39a2 2 0 0 0 2 1.61h9.72a2 2 0 0 0 2-1.61L23 6H6"/>
          </svg>
          <p>购物车是空的</p>
          <span class="cart-empty-hint">去逛逛加些商品吧</span>
        </div>

        <div v-else class="cart-items">
          <div v-for="item in cartStore.items" :key="item.productId" class="cart-item">
            <div class="cart-item-info">
              <div class="cart-item-name">{{ item.name }}</div>
              <div class="cart-item-price">¥{{ item.price }}</div>
            </div>
            <div class="cart-item-controls">
              <button class="cqty-btn" @click="cartStore.updateQuantity(item.productId, item.quantity - 1)">−</button>
              <span class="cqty-value">{{ item.quantity }}</span>
              <button class="cqty-btn" @click="cartStore.updateQuantity(item.productId, item.quantity + 1)">+</button>
              <button class="cart-remove" @click="cartStore.removeItem(item.productId)" title="移除">
                <svg viewBox="0 0 24 24" width="14" height="14" fill="none" stroke="#ccc" stroke-width="2">
                  <polyline points="3 6 5 6 21 6"/><path d="M19 6v14a2 2 0 0 1-2 2H7a2 2 0 0 1-2-2V6m3 0V4a2 2 0 0 1 2-2h4a2 2 0 0 1 2 2v2"/>
                </svg>
              </button>
            </div>
          </div>
        </div>

        <div v-if="cartStore.items.length > 0" class="cart-footer">
          <div class="cart-total">
            <span class="cart-total-label">合计</span>
            <span class="cart-total-price">¥{{ cartStore.total.toFixed(1) }}</span>
          </div>
          <button class="checkout-btn" @click="goCheckout">去结算</button>
        </div>
      </div>
    </transition>
  </div>
</template>

<script setup>
import { useRouter } from 'vue-router';
import { cartStore } from '../utils/cartStore';
import { cartUI } from '../utils/cartUI';

const router = useRouter();

const onEnter = () => {
  if (!cartUI.locked) cartUI.expanded = true;
};

const onLeave = () => {
  if (!cartUI.locked) cartUI.expanded = false;
};

const onToggle = () => {
  cartUI.toggle();
};

const onClose = () => {
  cartUI.collapse();
};

const goCheckout = () => {
  if (cartStore.items.length === 0) return;
  router.push('/user/checkout');
};
</script>

<style scoped>
.cart-wrapper {
  position: fixed;
  right: 0;
  top: 50%;
  transform: translateY(-50%);
  z-index: 150;
  display: flex;
  align-items: flex-start;
}

.cart-trigger {
  width: 44px;
  height: 44px;
  background: linear-gradient(135deg, #ff5000, #ff6a00);
  color: #fff;
  border-radius: 10px 0 0 10px;
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  position: relative;
  box-shadow: -2px 2px 8px rgba(255, 80, 0, 0.25);
  transition: box-shadow 0.2s;
  flex-shrink: 0;
}

.cart-trigger:hover {
  box-shadow: -2px 4px 12px rgba(255, 80, 0, 0.35);
}

.cart-badge {
  position: absolute;
  top: -6px;
  right: -6px;
  min-width: 18px;
  height: 18px;
  background: #ff4d4f;
  color: #fff;
  font-size: 11px;
  font-weight: 700;
  border-radius: 9px;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 0 4px;
  box-shadow: 0 2px 4px rgba(255, 77, 79, 0.3);
}

.cart-panel {
  width: 320px;
  background: #fff;
  border-radius: 12px 0 0 12px;
  box-shadow: -4px 4px 20px rgba(0, 0, 0, 0.12);
  display: flex;
  flex-direction: column;
  max-height: 480px;
  overflow: hidden;
  margin-left: -1px;
}

.cart-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 14px 16px;
  border-bottom: 1px solid #f0f0f0;
}

.cart-title {
  font-size: 15px;
  font-weight: 700;
  color: #1a1a1a;
  display: flex;
  align-items: center;
  gap: 6px;
}

.cart-close {
  width: 28px;
  height: 28px;
  border-radius: 6px;
  display: flex;
  align-items: center;
  justify-content: center;
  background: transparent;
  transition: background 0.2s;
}

.cart-close:hover {
  background: #f5f5f5;
}

.cart-empty {
  flex: 1;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 40px 20px;
  color: #ccc;
}

.cart-empty p {
  margin-top: 12px;
  font-size: 14px;
  color: #ccc;
}

.cart-empty-hint {
  font-size: 12px;
  color: #e0e0e0;
  margin-top: 4px;
}

.cart-items {
  flex: 1;
  overflow-y: auto;
  padding: 8px 0;
}

.cart-item {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 10px 16px;
  border-bottom: 1px solid #f8f8f8;
}

.cart-item:last-child {
  border-bottom: none;
}

.cart-item-info {
  min-width: 0;
  flex: 1;
}

.cart-item-name {
  font-size: 13px;
  font-weight: 600;
  color: #333;
  margin-bottom: 3px;
}

.cart-item-price {
  font-size: 13px;
  color: #ff5000;
  font-weight: 600;
}

.cart-item-controls {
  display: flex;
  align-items: center;
  gap: 4px;
  flex-shrink: 0;
}

.cqty-btn {
  width: 24px;
  height: 24px;
  border-radius: 4px;
  background: #f5f5f5;
  color: #666;
  font-size: 14px;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: background 0.2s;
}

.cqty-btn:hover {
  background: #e8e8e8;
}

.cqty-value {
  width: 28px;
  text-align: center;
  font-size: 13px;
  font-weight: 600;
  color: #333;
}

.cart-remove {
  width: 24px;
  height: 24px;
  border-radius: 4px;
  display: flex;
  align-items: center;
  justify-content: center;
  background: transparent;
  margin-left: 4px;
  transition: background 0.2s;
}

.cart-remove:hover {
  background: #fff2f0;
}

.cart-remove:hover svg {
  stroke: #ff4d4f;
}

.cart-footer {
  padding: 12px 16px;
  border-top: 1px solid #f0f0f0;
  background: #fafafa;
}

.cart-total {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 10px;
}

.cart-total-label {
  font-size: 13px;
  color: #999;
}

.cart-total-price {
  font-size: 18px;
  font-weight: 700;
  color: #ff5000;
}

.checkout-btn {
  width: 100%;
  height: 40px;
  background: linear-gradient(135deg, #ff5000, #ff6a00);
  color: #fff;
  border-radius: 8px;
  font-size: 15px;
  font-weight: 600;
  letter-spacing: 1px;
  transition: opacity 0.2s;
}

.checkout-btn:hover {
  opacity: 0.9;
}

/* Transition */
.slide-enter-active,
.slide-leave-active {
  transition: all 0.25s ease;
}

.slide-enter-from,
.slide-leave-to {
  opacity: 0;
  transform: translateX(20px);
}
</style>
