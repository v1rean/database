<template>
  <div v-if="items.length > 0" class="announce-bar">
    <div class="announce-label">📢 公告</div>
    <div class="announce-scroll">
      <div class="announce-track" :style="{ transform: `translateY(-${current * 28}px)` }">
        <div v-for="(item, i) in items" :key="i" class="announce-item" @click="showDetail(item)">
          <span class="announce-text">{{ item.title }}</span>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, onUnmounted } from 'vue';
import { dataManager } from '../utils/dataManager';
import { toast } from '../utils/toast';

const items = ref([]);
const current = ref(0);
let timer = null;

const showDetail = (item) => {
  toast.success(`${item.title}: ${item.content}`);
};

onMounted(async () => {
  items.value = await dataManager.getActiveAnnouncements();
  timer = setInterval(() => {
    if (items.value.length > 0) current.value = (current.value + 1) % items.value.length;
  }, 3500);
});

onUnmounted(() => clearInterval(timer));
</script>

<style scoped>
.announce-bar { display: flex; align-items: center; gap: 10px; background: #fff; border-radius: 10px; padding: 8px 16px; margin-bottom: 16px; box-shadow: 0 2px 8px rgba(0,0,0,0.04); }
.announce-label { font-size: 13px; font-weight: 600; color: #ff5000; white-space: nowrap; }
.announce-scroll { flex: 1; overflow: hidden; height: 28px; }
.announce-track { transition: transform 0.4s ease; }
.announce-item { height: 28px; display: flex; align-items: center; cursor: pointer; }
.announce-text { font-size: 13px; color: #666; overflow: hidden; text-overflow: ellipsis; white-space: nowrap; }
.announce-item:hover .announce-text { color: #ff5000; }
</style>
