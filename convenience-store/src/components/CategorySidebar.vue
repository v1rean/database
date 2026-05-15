<template>
  <div
    class="cat-wrapper"
    @mouseenter="onEnter"
    @mouseleave="onLeave"
  >
    <div class="cat-trigger" @click="onToggle" :class="{ 'has-active': activeCat }">
      <svg viewBox="0 0 24 24" width="20" height="20" fill="none" stroke="currentColor" stroke-width="2">
        <line x1="3" y1="6" x2="21" y2="6"/><line x1="3" y1="12" x2="21" y2="12"/><line x1="3" y1="18" x2="21" y2="18"/>
      </svg>
    </div>

    <transition name="slide-left">
      <div v-show="expanded" class="cat-panel">
        <div class="cat-header">
          <h3 class="cat-title">
            <svg viewBox="0 0 24 24" width="16" height="16" fill="none" stroke="#ff5000" stroke-width="2">
              <line x1="3" y1="6" x2="21" y2="6"/><line x1="3" y1="12" x2="21" y2="12"/><line x1="3" y1="18" x2="21" y2="18"/>
            </svg>
            商品分类
          </h3>
          <button class="cat-close" @click="onClose" title="收起">
            <svg viewBox="0 0 24 24" width="14" height="14" fill="none" stroke="#999" stroke-width="2">
              <line x1="18" y1="6" x2="6" y2="18"/><line x1="6" y1="6" x2="18" y2="18"/>
            </svg>
          </button>
        </div>
        <div class="cat-list">
          <button
            :class="['cat-item', { active: selectedCategory === '' }]"
            @click="selectCat('')"
          >
            <span class="cat-icon">📋</span>
            <span class="cat-name">全部分类</span>
          </button>
          <button
            v-for="cat in categories"
            :key="cat"
            :class="['cat-item', { active: selectedCategory === cat }]"
            @click="selectCat(cat)"
          >
            <span class="cat-icon">{{ catIcon(cat) }}</span>
            <span class="cat-name">{{ cat }}</span>
          </button>
        </div>
        <div v-if="categories.length === 0" class="cat-empty">
          <p>暂无分类</p>
        </div>
      </div>
    </transition>
  </div>
</template>

<script setup>
import { ref, computed } from 'vue';
import { useRouter } from 'vue-router';
import { selectedCategory } from '../utils/categoryState';
import { dataManager } from '../utils/dataManager';

const router = useRouter();
const expanded = ref(false);
const locked = ref(false);
const products = ref([]);

const categories = computed(() => [...new Set(products.value.map(p => p.category))]);
const activeCat = computed(() => selectedCategory.value);

const catIcon = (cat) => {
  const icons = { '饮料': '🥤', '速食': '🍜', '零食': '🍪', '日用品': '🧴' };
  return icons[cat] || '📦';
};

const loadCategories = async () => {
  products.value = await dataManager.getProducts();
};

const selectCat = (cat) => {
  selectedCategory.value = cat;
  router.push('/user/products');
};

const onEnter = () => {
  if (!locked.value) expanded.value = true;
};

const onLeave = () => {
  if (!locked.value) expanded.value = false;
};

const onToggle = () => {
  locked.value = !locked.value;
  expanded.value = locked.value;
};

const onClose = () => {
  locked.value = false;
  expanded.value = false;
};

loadCategories();
</script>

<style scoped>
.cat-wrapper {
  position: fixed;
  left: 0;
  top: 50%;
  transform: translateY(-50%);
  z-index: 140;
  display: flex;
  align-items: flex-start;
}

.cat-trigger {
  width: 36px;
  height: 44px;
  background: linear-gradient(135deg, #ff6a00, #ff8c00);
  color: #fff;
  border-radius: 0 10px 10px 0;
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  box-shadow: 2px 2px 8px rgba(255, 106, 0, 0.25);
  transition: box-shadow 0.2s;
  flex-shrink: 0;
}

.cat-trigger:hover {
  box-shadow: 2px 4px 12px rgba(255, 106, 0, 0.35);
}

.cat-trigger.has-active {
  background: linear-gradient(135deg, #ff5000, #ff6a00);
}

.cat-panel {
  width: 200px;
  background: #fff;
  border-radius: 0 12px 12px 0;
  box-shadow: 4px 4px 20px rgba(0, 0, 0, 0.12);
  display: flex;
  flex-direction: column;
  max-height: 420px;
  overflow: hidden;
  margin-right: -1px;
}

.cat-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 14px 16px;
  border-bottom: 1px solid #f0f0f0;
}

.cat-title {
  font-size: 14px;
  font-weight: 700;
  color: #1a1a1a;
  display: flex;
  align-items: center;
  gap: 6px;
}

.cat-close {
  width: 26px;
  height: 26px;
  border-radius: 6px;
  display: flex;
  align-items: center;
  justify-content: center;
  background: transparent;
  transition: background 0.2s;
}

.cat-close:hover {
  background: #f5f5f5;
}

.cat-list {
  flex: 1;
  overflow-y: auto;
  padding: 8px;
}

.cat-item {
  width: 100%;
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 10px 14px;
  border-radius: 8px;
  font-size: 14px;
  color: #333;
  background: transparent;
  transition: all 0.2s;
  text-align: left;
  margin-bottom: 2px;
}

.cat-item:hover {
  background: #fff5ef;
  color: #ff5000;
}

.cat-item.active {
  background: #fff5ef;
  color: #ff5000;
  font-weight: 600;
}

.cat-icon {
  font-size: 18px;
  width: 24px;
  text-align: center;
  flex-shrink: 0;
}

.cat-name {
  font-size: 13px;
}

.cat-empty {
  flex: 1;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 30px;
  color: #ccc;
  font-size: 13px;
}

.slide-left-enter-active,
.slide-left-leave-active {
  transition: all 0.25s ease;
}

.slide-left-enter-from,
.slide-left-leave-to {
  opacity: 0;
  transform: translateX(-20px);
}
</style>
