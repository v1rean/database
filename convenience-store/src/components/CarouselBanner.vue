<template>
  <div v-if="items.length > 0" class="carousel-wrap">
    <div class="carousel-track" :style="{ transform: `translateX(-${current * 100}%)` }">
      <div v-for="(item, i) in items" :key="i" class="carousel-slide" @click="goLink(item)">
        <div class="slide-img-wrap">
          <img
            v-if="item.imageUrl"
            :src="item.imageUrl"
            :alt="item.title"
            class="slide-img"
            @error="onImgError($event, i)"
          />
          <div v-if="imgFailed[i]" class="slide-placeholder" :style="{ background: colors[i % colors.length] }">
            <span class="slide-emoji">{{ emojis[i % emojis.length] }}</span>
            <div class="slide-title">{{ item.title }}</div>
          </div>
        </div>
      </div>
    </div>
    <div class="carousel-dots">
      <span v-for="(_, i) in items" :key="i" :class="['dot', { active: i === current }]" @click="current = i"></span>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, onUnmounted } from 'vue';
import { useRouter } from 'vue-router';
import { dataManager } from '../utils/dataManager';

const router = useRouter();
const items = ref([]);
const current = ref(0);
const imgFailed = ref([]);
let timer = null;

const colors = ['#667eea', '#f093fb', '#4facfe', '#43e97b', '#fa8231', '#a55eea'];
const emojis = ['🎉', '🏪', '🛒', '🌟', '💫', '🎈'];

const goLink = (item) => {
  if (item.linkUrl) router.push(item.linkUrl);
};

const onImgError = (e, i) => {
  e.target.style.display = 'none';
  imgFailed.value[i] = true;
};

onMounted(async () => {
  items.value = await dataManager.getActiveCarousels();
  imgFailed.value = items.value.map(() => false);
  timer = setInterval(() => {
    if (items.value.length > 0) current.value = (current.value + 1) % items.value.length;
  }, 4000);
});

onUnmounted(() => clearInterval(timer));
</script>

<style scoped>
.carousel-wrap { position: relative; overflow: hidden; border-radius: 14px; margin-bottom: 20px; background: #fff; box-shadow: 0 2px 8px rgba(0,0,0,0.06); }
.carousel-track { display: flex; transition: transform 0.5s ease; }
.carousel-slide { min-width: 100%; cursor: pointer; }
.slide-img-wrap { position: relative; width: 100%; height: 200px; overflow: hidden; }
.slide-img { width: 100%; height: 100%; object-fit: cover; display: block; }
.slide-placeholder { width: 100%; height: 100%; display: flex; flex-direction: column; align-items: center; justify-content: center; gap: 10px; }
.slide-emoji { font-size: 56px; filter: drop-shadow(0 4px 8px rgba(0,0,0,0.1)); }
.slide-title { font-size: 20px; font-weight: 700; color: #fff; text-shadow: 0 2px 8px rgba(0,0,0,0.15); }
.carousel-dots { position: absolute; bottom: 12px; left: 50%; transform: translateX(-50%); display: flex; gap: 8px; z-index: 2; }
.dot { width: 8px; height: 8px; border-radius: 50%; background: rgba(255,255,255,0.4); cursor: pointer; transition: all 0.2s; }
.dot.active { width: 24px; border-radius: 4px; background: #fff; }
</style>
