<template>
  <Teleport to="body">
    <transition name="modal-fade">
      <div v-if="visible && items.length > 0" class="modal-overlay" @click.self="close">
        <div class="modal-card">
          <button class="modal-close" @click="close">✕</button>
          <div class="modal-header">
            <span class="modal-badge">📢 公告</span>
            <span v-if="items.length > 1" class="modal-counter">{{ current + 1 }} / {{ items.length }}</span>
          </div>
          <div class="modal-body">
            <h3 class="modal-title">{{ items[current].title }}</h3>
            <p class="modal-content">{{ items[current].content }}</p>
          </div>
          <div v-if="items.length > 1" class="modal-dots">
            <span
              v-for="(_, i) in items"
              :key="i"
              :class="['dot', { active: i === current }]"
              @click="current = i"
            ></span>
          </div>
        </div>
      </div>
    </transition>
  </Teleport>
</template>

<script setup>
import { ref, onMounted, onUnmounted } from 'vue';
import { dataManager } from '../utils/dataManager';

const visible = ref(false);
const items = ref([]);
const current = ref(0);
let timer = null;

const close = () => {
  visible.value = false;
  if (timer) clearInterval(timer);
};

onMounted(async () => {
  items.value = await dataManager.getActiveAnnouncements();
  if (items.value.length > 0) {
    visible.value = true;
    if (items.value.length > 1) {
      timer = setInterval(() => {
        current.value = (current.value + 1) % items.value.length;
      }, 4000);
    }
  }
});

onUnmounted(() => { if (timer) clearInterval(timer); });
</script>

<style scoped>
.modal-overlay {
  position: fixed; inset: 0; background: rgba(0,0,0,0.45);
  display: flex; align-items: center; justify-content: center;
  z-index: 1000;
}
.modal-card {
  background: #fff; border-radius: 16px; padding: 0;
  width: 420px; max-width: 90vw; max-height: 80vh;
  box-shadow: 0 24px 64px rgba(0,0,0,0.2);
  overflow: hidden; position: relative;
}
.modal-close {
  position: absolute; top: 12px; right: 14px;
  width: 28px; height: 28px; border-radius: 50%;
  background: #f5f5f5; color: #999; font-size: 14px;
  display: flex; align-items: center; justify-content: center;
  transition: all 0.2s; z-index: 2;
}
.modal-close:hover { background: #ff4d4f; color: #fff; }
.modal-header {
  display: flex; align-items: center; justify-content: space-between;
  padding: 18px 22px 0;
}
.modal-badge { font-size: 14px; font-weight: 700; color: #ff5000; }
.modal-counter { font-size: 12px; color: #ccc; }
.modal-body { padding: 16px 22px 20px; }
.modal-title { font-size: 17px; font-weight: 700; color: #1a1a1a; margin-bottom: 12px; }
.modal-content { font-size: 14px; color: #666; line-height: 1.8; }
.modal-dots { display: flex; justify-content: center; gap: 8px; padding: 0 22px 18px; }
.dot { width: 8px; height: 8px; border-radius: 50%; background: #e0e0e0; cursor: pointer; transition: all 0.2s; }
.dot.active { background: #ff5000; width: 24px; border-radius: 4px; }

.modal-fade-enter-active { animation: fadeIn 0.3s ease; }
.modal-fade-leave-active { animation: fadeOut 0.2s ease; }
@keyframes fadeIn { from { opacity: 0; transform: scale(0.92) translateY(10px); } to { opacity: 1; transform: scale(1) translateY(0); } }
@keyframes fadeOut { from { opacity: 1; } to { opacity: 0; } }
</style>
