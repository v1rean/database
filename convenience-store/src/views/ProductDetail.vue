<template>
  <div class="detail-page" v-if="product">
    <button class="back-btn" @click="goBack">
      <svg viewBox="0 0 24 24" width="16" height="16" fill="none" stroke="currentColor" stroke-width="2">
        <polyline points="15 18 9 12 15 6"/>
      </svg>
      返回
    </button>

    <!-- ===== 顶部：图片 + 信息 + 推荐 ===== -->
    <div class="detail-top">
      <div class="detail-left">
        <div class="main-image" :style="{ background: productColor(product.category) }">
          <span class="main-emoji">{{ productEmoji(product.category) }}</span>
          <img v-if="imageExists(product.id)" :src="`/images/products/${product.id}.jpg`" class="product-real-img" alt="" @error="onImgError" />
        </div>
      </div>

      <div class="detail-center">
        <h1 class="detail-name">{{ product.name }}</h1>
        <div class="detail-meta">
          <span class="detail-category">{{ product.category }}</span>
          <span class="detail-stock" :class="{ 'low-stock': product.stock <= 10 }">库存 {{ product.stock }}</span>
        </div>
        <div class="detail-price">¥{{ product.price }}</div>
        <div class="detail-desc">{{ product.description }}</div>

        <div class="detail-section">
          <h3 class="section-title">数量</h3>
          <div class="qty-control">
            <button class="qty-btn" @click="decreaseQty">−</button>
            <span class="qty-value">{{ qty }}</span>
            <button class="qty-btn" @click="increaseQty">+</button>
          </div>
        </div>

        <div class="detail-actions">
          <button class="btn-cart" @click="addToCart">
            <svg viewBox="0 0 24 24" width="18" height="18" fill="none" stroke="currentColor" stroke-width="2">
              <circle cx="9" cy="21" r="1"/><circle cx="20" cy="21" r="1"/>
              <path d="M1 1h4l2.68 13.39a2 2 0 0 0 2 1.61h9.72a2 2 0 0 0 2-1.61L23 6H6"/>
            </svg>
            加入购物车
          </button>
          <button class="btn-buy" @click="buyNow">立即购买</button>
        </div>
      </div>

      <div class="detail-right">
        <div class="recommend-header">
          <h3 class="recommend-title">推荐商品</h3>
          <span class="recommend-sub">{{ recommendEnabled ? '个性推荐' : '同分类推荐' }}</span>
        </div>
        <div class="recommend-list">
          <div v-for="item in recommended" :key="item.id" class="recommend-card" @click="goDetail(item.id)">
            <div class="rec-img" :style="{ background: productColor(item.category) }">
              <span class="rec-emoji">{{ productEmoji(item.category) }}</span>
            </div>
            <div class="rec-info">
              <div class="rec-name">{{ item.name }}</div>
              <div class="rec-price">¥{{ item.price }}</div>
            </div>
          </div>
          <div v-if="recommended.length === 0" class="rec-empty">暂无推荐</div>
        </div>
      </div>
    </div>

    <!-- ===== 底部：详细商品介绍 ===== -->
    <div class="detail-bottom">
      <div class="bottom-tabs">
        <button :class="['btab', { active: btab === 'detail' }]" @click="btab = 'detail'">商品详情</button>
        <button :class="['btab', { active: btab === 'desc' }]" @click="btab = 'desc'">图文介绍</button>
      </div>

      <div v-if="btab === 'detail'" class="btab-content">
        <div class="detail-specs">
          <div class="spec-row"><span class="spec-label">商品名称</span><span class="spec-value">{{ product.name }}</span></div>
          <div class="spec-row"><span class="spec-label">商品分类</span><span class="spec-value">{{ product.category }}</span></div>
          <div class="spec-row"><span class="spec-label">商品价格</span><span class="spec-value">¥{{ product.price }}</span></div>
          <div class="spec-row"><span class="spec-label">库存数量</span><span class="spec-value">{{ product.stock }}</span></div>
          <div class="spec-row"><span class="spec-label">商品描述</span><span class="spec-value">{{ product.description }}</span></div>
        </div>
        <div class="rich-text">
          <h4>详细说明</h4>
          <p>{{ fullDetail(product) }}</p>
        </div>
      </div>

      <div v-if="btab === 'desc'" class="btab-content">
        <div class="rich-text">
          <h4>产品展示</h4>
          <div class="gallery-grid">
            <div class="gallery-item" :style="{ background: productColor(product.category) }">
              <span class="gallery-emoji">{{ productEmoji(product.category) }}</span>
              <img v-if="imageExists(product.id, 1)" :src="`/images/products/${product.id}_1.jpg`" alt="" class="gallery-img" @error="onImgError" />
            </div>
            <div class="gallery-item" :style="{ background: productColor(product.category) }">
              <span class="gallery-emoji">{{ productEmoji(product.category) }}</span>
              <img v-if="imageExists(product.id, 2)" :src="`/images/products/${product.id}_2.jpg`" alt="" class="gallery-img" @error="onImgError" />
            </div>
          </div>
        </div>
        <div class="rich-text">
          <h4>产品特点</h4>
          <p>{{ productFeatures(product) }}</p>
        </div>
        <div class="rich-text">
          <h4>使用说明</h4>
          <p>{{ productUsage(product) }}</p>
        </div>
      </div>
    </div>
  </div>

  <div v-else class="loading-state"><p>加载中...</p></div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import { dataManager } from '../utils/dataManager';
import { cartStore } from '../utils/cartStore';
import { cartUI } from '../utils/cartUI';
import { toast } from '../utils/toast';
import { browseHistory } from '../utils/browseHistory';

const route = useRoute();
const router = useRouter();
const product = ref(null);
const allProducts = ref([]);
const purchases = ref([]);
const qty = ref(1);
const btab = ref('detail');
const recommendEnabled = ref(true);

const recommended = computed(() => {
  if (!recommendEnabled.value) {
    return allProducts.value.filter(p => p.category === product.value?.category && p.id !== product.value?.id).slice(0, 4);
  }
  const boughtCats = [...new Set(purchases.value.flatMap(p => (p.items || []).map(i => {
    const prod = allProducts.value.find(x => x.id === i.productId);
    return prod ? prod.category : '';
  })).filter(Boolean))];
  let list;
  if (boughtCats.length > 0) {
    const boughtIds = new Set(purchases.value.flatMap(p => (p.items || []).map(i => i.productId)));
    list = allProducts.value.filter(p => boughtCats.includes(p.category) && p.id !== product.value?.id);
    if (list.length < 4 && product.value) {
      const more = allProducts.value.filter(p => p.category === product.value.category && p.id !== product.value.id && !list.find(x => x.id === p.id));
      list = [...list, ...more].slice(0, 4);
    }
  } else if (product.value) {
    list = allProducts.value.filter(p => p.category === product.value.category && p.id !== product.value.id);
  } else {
    list = [];
  }
  return list.slice(0, 4);
});

const productColor = (c) => ({ '饮料': 'linear-gradient(135deg, #667eea, #764ba2)', '速食': 'linear-gradient(135deg, #f093fb, #f5576c)', '零食': 'linear-gradient(135deg, #4facfe, #00f2fe)', '日用品': 'linear-gradient(135deg, #43e97b, #38f9d7)' }[c] || 'linear-gradient(135deg, #a8edea, #fed6e3)');
const productEmoji = (c) => ({ '饮料': '🥤', '速食': '🍜', '零食': '🍪', '日用品': '🧴' }[c] || '📦');

const imageExists = (id, sub) => false;

const categoryDetailTpl = {
  '饮料': (p) => `${p.name}，精选优质原料，口感纯正。${p.description}。适合日常饮用、聚会分享等多种场景。开盖即饮，冰镇后口感更佳。保质期12个月，请存放于阴凉干燥处。`,
  '速食': (p) => `${p.name}，方便快捷的美味选择。${p.description}。采用优质原料精心制作，只需简单加工即可享用。适合居家、办公、旅行等场景。请按照包装说明操作，保质期6个月。`,
  '零食': (p) => `${p.name}，休闲时刻的美味伴侣。${p.description}。精选上等原料，口感丰富，老少皆宜。开袋即食，开封后请尽快食用完毕，避免受潮影响口感。`,
  '日用品': (p) => `${p.name}，品质生活的好帮手。${p.description}。精选优质材料，安全环保，经久耐用。请按照产品说明正常使用，存放于干燥处。`
};

const categoryFeatureTpl = {
  '饮料': () => '• 精选原料，口感纯正\n• 品质保障，安全放心\n• 便携包装，随时享用\n• 冰镇更佳，畅爽一夏',
  '速食': () => '• 优质原料，精心制作\n• 快捷方便，几分钟即食\n• 分量充足，饱腹满足\n• 多场景适用，居家旅行必备',
  '零食': () => '• 精选食材，口感丰富\n• 独立包装，干净卫生\n• 休闲必备，分享快乐\n• 老少皆宜，美味健康',
  '日用品': () => '• 优质材料，安全环保\n• 实用设计，经久耐用\n• 品牌保障，值得信赖\n• 居家必备，品质生活'
};

const categoryUsageTpl = {
  '饮料': () => '开盖即饮，建议冰镇后饮用口感更佳。开启后请尽快饮用完毕。避免阳光直射，存放于阴凉干燥处。',
  '速食': (p) => {
    if (p.name.includes('面') || p.name.includes('粉')) return '1. 打开包装，取出调料包\n2. 将调料加入碗中\n3. 加入沸水至注水线\n4. 盖紧等待3-5分钟\n5. 搅拌均匀后即可食用';
    if (p.name.includes('冻')) return '需加热后食用。按照包装说明进行烹饪。未食用完请密封冷冻保存。';
    return '按照包装说明操作即可。开袋即食或加热后食用。未食用完请密封保存。';
  },
  '零食': () => '开袋即食。开封后请密封保存，避免受潮影响口感。建议一次性食用完毕。',
  '日用品': (p) => {
    if (p.name.includes('巾') || p.name.includes('纸')) return '抽取式使用。请存放于干燥处，避免受潮。使用后请勿冲入马桶。';
    if (p.name.includes('膏') || p.name.includes('刷')) return '1. 取适量于刷毛上\n2. 轻柔刷洗2-3分钟\n3. 用后冲洗干净\n4. 建议定期更换';
    return '请按照产品说明正常使用。存放于干燥处，避免儿童接触。';
  }
};

const fullDetail = (p) => {
  const fn = categoryDetailTpl[p.category];
  return fn ? fn(p) : `${p.name}，品质保证，欢迎选购。${p.description}。`;
};

const productFeatures = (p) => {
  const fn = categoryFeatureTpl[p.category];
  return fn ? fn(p) : '优质商品，值得信赖。';
};

const productUsage = (p) => {
  const fn = categoryUsageTpl[p.category];
  return fn ? fn(p) : '请按照商品说明正常使用。';
};

const increaseQty = () => { if (qty.value < product.value.stock) qty.value++; else toast.error('库存不足'); };
const decreaseQty = () => { if (qty.value > 1) qty.value--; };

const addToCart = () => {
  const ok = cartStore.addItem(product.value, qty.value);
  if (!ok) toast.error('库存不足');
  else { cartUI.expand(); toast.success(`已加入购物车：${product.value.name}`); }
};

const buyNow = async () => {
  const ok = cartStore.addItem(product.value, qty.value);
  if (!ok) { toast.error('库存不足'); return; }
  cartUI.expand();
  router.push('/user/checkout');
};

const goBack = () => router.push('/user/products');
const goDetail = (id) => router.push(`/user/product/${id}`);
const onImgError = (e) => { e.target.style.display = 'none'; };

onMounted(async () => {
  const id = parseInt(route.params.id);
  allProducts.value = await dataManager.getProducts();
  product.value = allProducts.value.find(p => p.id === id) || null;
  if (product.value) browseHistory.add(id);
  const user = await dataManager.getCurrentUser();
  if (user) {
    purchases.value = await dataManager.getUserPurchases(user.id);
    const pref = await dataManager.loadPreference(user.id);
    recommendEnabled.value = pref.recommendEnabled;
  }
});
</script>

<style scoped>
.detail-page { max-width: 1100px; margin: 0 auto; }

.back-btn {
  display: inline-flex; align-items: center; gap: 4px;
  padding: 8px 18px; font-size: 14px; color: #666;
  background: #fff; border-radius: 8px; border: 1.5px solid #e0e0e0;
  margin-bottom: 20px; transition: all 0.2s;
}
.back-btn:hover { border-color: #ff5000; color: #ff5000; }

/* ===== 顶部布局 ===== */
.detail-top { display: flex; gap: 28px; align-items: flex-start; }

/* ===== 左：图片 ===== */
.detail-left { width: 420px; flex-shrink: 0; }
.main-image {
  width: 100%; height: 420px; border-radius: 16px;
  display: flex; align-items: center; justify-content: center;
  box-shadow: 0 6px 24px rgba(0,0,0,0.10);
  position: relative; overflow: hidden;
}
.main-emoji { font-size: 120px; filter: drop-shadow(0 4px 12px rgba(0,0,0,0.12)); }
.product-real-img {
  position: absolute; inset: 0; width: 100%; height: 100%;
  object-fit: cover;
}

/* ===== 中：信息 ===== */
.detail-center {
  flex: 1; min-width: 0;
  background: #fff; border-radius: 14px; padding: 28px;
  box-shadow: 0 2px 8px rgba(0,0,0,0.06);
}
.detail-name { font-size: 26px; font-weight: 800; color: #1a1a1a; margin-bottom: 10px; }
.detail-meta { display: flex; gap: 10px; align-items: center; margin-bottom: 14px; }
.detail-category { font-size: 13px; padding: 4px 12px; background: #fff5ef; color: #ff5000; border-radius: 4px; }
.detail-stock { font-size: 13px; color: #999; }
.low-stock { color: #ff4d4f; }
.detail-price { font-size: 32px; font-weight: 800; color: #ff5000; margin-bottom: 14px; }
.detail-desc {
  font-size: 15px; color: #999;
  padding-bottom: 18px; border-bottom: 1px solid #f0f0f0; margin-bottom: 18px;
}

.detail-section { margin-bottom: 18px; }
.section-title { font-size: 15px; font-weight: 600; color: #333; margin-bottom: 10px; }

.qty-control { display: inline-flex; align-items: center; border: 1.5px solid #e0e0e0; border-radius: 10px; overflow: hidden; }
.qty-btn { width: 40px; height: 40px; background: #fafafa; font-size: 20px; color: #666; display: flex; align-items: center; justify-content: center; transition: background 0.2s; }
.qty-btn:hover { background: #f0f0f0; }
.qty-value { width: 48px; text-align: center; font-size: 17px; font-weight: 700; color: #333; }

.detail-actions { display: flex; gap: 14px; margin-top: 24px; padding-top: 18px; border-top: 1px solid #f0f0f0; }
.btn-cart, .btn-buy { flex: 1; height: 52px; border-radius: 12px; font-size: 16px; font-weight: 700; display: flex; align-items: center; justify-content: center; gap: 8px; transition: all 0.2s; }
.btn-cart { background: #fff; color: #ff5000; border: 2px solid #ff5000; }
.btn-cart:hover { background: #fff5ef; }
.btn-buy { background: linear-gradient(135deg, #ff5000, #ff6a00); color: #fff; }
.btn-buy:hover { box-shadow: 0 6px 20px rgba(255,80,0,0.35); transform: translateY(-2px); }

/* ===== 右：推荐 ===== */
.detail-right { width: 230px; flex-shrink: 0; background: #fff; border-radius: 14px; padding: 18px; box-shadow: 0 2px 8px rgba(0,0,0,0.06); position: sticky; top: 80px; }
.recommend-header { margin-bottom: 14px; padding-bottom: 12px; border-bottom: 1px solid #f0f0f0; }
.recommend-title { font-size: 15px; font-weight: 700; color: #1a1a1a; margin-bottom: 2px; }
.recommend-sub { font-size: 12px; color: #ccc; }
.recommend-list { display: flex; flex-direction: column; gap: 12px; }
.recommend-card { display: flex; gap: 10px; padding: 8px; border-radius: 8px; cursor: pointer; transition: background 0.2s; }
.recommend-card:hover { background: #fafafa; }
.rec-img { width: 52px; height: 52px; border-radius: 8px; display: flex; align-items: center; justify-content: center; flex-shrink: 0; }
.rec-emoji { font-size: 24px; }
.rec-info { min-width: 0; flex: 1; display: flex; flex-direction: column; justify-content: center; }
.rec-name { font-size: 13px; font-weight: 600; color: #333; overflow: hidden; text-overflow: ellipsis; white-space: nowrap; margin-bottom: 3px; }
.rec-price { font-size: 15px; font-weight: 700; color: #ff5000; }
.rec-empty { text-align: center; color: #ccc; font-size: 13px; padding: 20px; }

/* ===== 底部：详细介绍 ===== */
.detail-bottom { margin-top: 28px; background: #fff; border-radius: 14px; box-shadow: 0 2px 8px rgba(0,0,0,0.06); overflow: hidden; }

.bottom-tabs { display: flex; border-bottom: 2px solid #f0f0f0; }
.btab {
  flex: 1; height: 48px; font-size: 15px; font-weight: 600; color: #999;
  background: transparent; transition: all 0.2s; position: relative;
}
.btab:hover { color: #ff5000; }
.btab.active { color: #ff5000; }
.btab.active::after {
  content: ''; position: absolute; bottom: -2px; left: 20%; right: 20%;
  height: 3px; background: #ff5000; border-radius: 2px;
}

.btab-content { padding: 24px 28px; }

.detail-specs { margin-bottom: 20px; }
.spec-row { display: flex; padding: 10px 0; border-bottom: 1px solid #f8f8f8; font-size: 14px; }
.spec-label { width: 100px; color: #999; flex-shrink: 0; }
.spec-value { color: #333; font-weight: 500; }

.rich-text { margin-bottom: 24px; }
.rich-text:last-child { margin-bottom: 0; }
.rich-text h4 { font-size: 16px; font-weight: 700; color: #1a1a1a; margin-bottom: 12px; padding-left: 10px; border-left: 3px solid #ff5000; }
.rich-text p { font-size: 14px; color: #666; line-height: 1.9; white-space: pre-line; }

.gallery-grid { display: grid; grid-template-columns: 1fr 1fr; gap: 16px; margin-bottom: 16px; }
.gallery-item {
  height: 260px; border-radius: 12px;
  display: flex; align-items: center; justify-content: center;
  position: relative; overflow: hidden;
}
.gallery-emoji { font-size: 64px; filter: drop-shadow(0 4px 8px rgba(0,0,0,0.1)); }
.gallery-img { position: absolute; inset: 0; width: 100%; height: 100%; object-fit: cover; }

.loading-state { text-align: center; padding: 80px; color: #bbb; font-size: 15px; }
</style>
