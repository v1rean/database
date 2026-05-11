<template>
  <div class="products-page">
    <!-- ========== 管理员视图：商品管理 ========== -->
    <template v-if="isAdmin">
      <div class="page-header">
        <h2 class="page-title">商品管理</h2>
      </div>

      <div class="card add-card">
        <h3 class="card-title">
          <svg viewBox="0 0 24 24" width="18" height="18" fill="none" stroke="#ff5000" stroke-width="2">
            <line x1="12" y1="5" x2="12" y2="19"/><line x1="5" y1="12" x2="19" y2="12"/>
          </svg>
          添加商品
        </h3>
        <div class="add-form">
          <input v-model="newProduct.name" placeholder="商品名称" />
          <input v-model="newProduct.category" placeholder="分类" />
          <input v-model.number="newProduct.price" type="number" placeholder="价格" />
          <input v-model.number="newProduct.stock" type="number" placeholder="库存" />
          <input v-model="newProduct.description" placeholder="描述" />
          <button class="btn btn-primary" @click="addProduct">添加</button>
        </div>
      </div>

      <div class="card">
        <table class="admin-table">
          <thead>
            <tr>
              <th>名称</th>
              <th>分类</th>
              <th>价格</th>
              <th>库存</th>
              <th>描述</th>
              <th>操作</th>
            </tr>
          </thead>
          <tbody>
            <tr v-for="product in products" :key="product.id">
              <td class="cell-name">{{ product.name }}</td>
              <td><span class="category-tag">{{ product.category }}</span></td>
              <td class="cell-price">¥{{ product.price }}</td>
              <td>{{ product.stock }}</td>
              <td class="cell-desc">{{ product.description }}</td>
              <td class="cell-actions">
                <button class="btn btn-sm" @click="editProduct(product)">编辑</button>
                <button class="btn btn-sm btn-danger" @click="deleteProduct(product.id)">删除</button>
              </td>
            </tr>
          </tbody>
        </table>
      </div>

      <!-- 编辑弹窗 -->
      <div v-if="editingProduct" class="modal-overlay" @click.self="editingProduct = null">
        <div class="modal-card">
          <h3 class="modal-title">编辑商品</h3>
          <div class="modal-body">
            <input v-model="editingProduct.name" placeholder="商品名称" />
            <input v-model="editingProduct.category" placeholder="分类" />
            <input v-model.number="editingProduct.price" type="number" placeholder="价格" />
            <input v-model.number="editingProduct.stock" type="number" placeholder="库存" />
            <input v-model="editingProduct.description" placeholder="描述" />
          </div>
          <div class="modal-actions">
            <button class="btn" @click="editingProduct = null">取消</button>
            <button class="btn btn-primary" @click="updateProduct">保存</button>
          </div>
        </div>
      </div>
    </template>

    <!-- ========== 用户视图：商品浏览 ========== -->
    <template v-else>
      <div class="page-header">
        <h2 class="page-title">商品浏览</h2>
      </div>

      <CarouselBanner />
      <AnnouncementBar />
      <div class="search-bar">
        <svg class="search-icon" viewBox="0 0 24 24" width="18" height="18" fill="none" stroke="#bbb" stroke-width="2">
          <circle cx="11" cy="11" r="8"/><line x1="21" y1="21" x2="16.65" y2="16.65"/>
        </svg>
        <input v-model="searchQuery" type="text" placeholder="搜索商品名称..." @input="selectedCategory = ''" />
      </div>

      <div class="category-tabs">
        <button
          :class="['tab-btn', { active: selectedCategory === '' }]"
          @click="selectedCategory = ''"
        >全部</button>
        <button
          v-for="cat in categories"
          :key="cat"
          :class="['tab-btn', { active: selectedCategory === cat }]"
          @click="selectedCategory = cat"
        >{{ cat }}</button>
      </div>

      <div v-if="filteredProducts.length === 0" class="empty-state">
        <svg viewBox="0 0 24 24" width="48" height="48" fill="none" stroke="#ddd" stroke-width="1.5">
          <rect x="2" y="7" width="20" height="14" rx="2"/><path d="M16 21V5a2 2 0 0 0-2-2h-4a2 2 0 0 0-2 2v16"/>
        </svg>
        <p>暂无商品</p>
      </div>

      <div v-else class="product-grid">
        <div v-for="product in filteredProducts" :key="product.id" class="product-card">
          <div class="product-img" :style="{ background: productColor(product.category) }" @click="goDetail(product.id)">
            <span class="product-emoji">{{ productEmoji(product.category) }}</span>
          </div>
          <div class="product-info">
            <div class="product-name">{{ product.name }}</div>
            <div class="product-meta">
              <span class="product-category">{{ product.category }}</span>
              <span class="product-stock" :class="{ 'low-stock': product.stock <= 10 }">
                库存 {{ product.stock }}
              </span>
            </div>
            <div class="product-desc">{{ product.description }}</div>
            <div class="product-bottom">
              <span class="product-price">¥{{ product.price }}</span>
              <div class="buy-area">
                <div class="qty-control">
                  <button class="qty-btn" @click="decreaseQty(product)">−</button>
                  <span class="qty-value">{{ buyQuantities[product.id] || 1 }}</span>
                  <button class="qty-btn" @click="increaseQty(product)">+</button>
                </div>
                <button class="cart-add-btn" @click="addToCart(product)">加入购物车</button>
              </div>
            </div>
          </div>
        </div>
      </div>


    </template>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue';
import { useRouter } from 'vue-router';
import { dataManager } from '../utils/dataManager';
import { cartStore } from '../utils/cartStore';
import { cartUI } from '../utils/cartUI';
import { toast } from '../utils/toast';
import { selectedCategory } from '../utils/categoryState';
import CarouselBanner from '../components/CarouselBanner.vue';
import AnnouncementBar from '../components/AnnouncementBar.vue';

const router = useRouter();
const props = defineProps({ isAdmin: Boolean });
const products = ref([]);
const searchQuery = ref('');
const editingProduct = ref(null);
const newProduct = ref({ name: '', category: '', price: 0, stock: 0, description: '' });
const buyQuantities = ref({});

const categories = computed(() => [...new Set(products.value.map(p => p.category))]);

const filteredProducts = computed(() => {
  let list = products.value;
  if (selectedCategory.value) {
    list = list.filter(p => p.category === selectedCategory.value);
  }
  if (searchQuery.value) {
    const q = searchQuery.value.toLowerCase();
    list = list.filter(p => p.name.toLowerCase().includes(q));
  }
  return list;
});

const productColor = (category) => {
  const colors = {
    '饮料': 'linear-gradient(135deg, #667eea, #764ba2)',
    '速食': 'linear-gradient(135deg, #f093fb, #f5576c)',
    '零食': 'linear-gradient(135deg, #4facfe, #00f2fe)',
    '日用品': 'linear-gradient(135deg, #43e97b, #38f9d7)'
  };
  return colors[category] || 'linear-gradient(135deg, #a8edea, #fed6e3)';
};

const productEmoji = (category) => {
  const emojis = {
    '饮料': '🥤',
    '速食': '🍜',
    '零食': '🍪',
    '日用品': '🧴'
  };
  return emojis[category] || '📦';
};

const loadProducts = async () => {
  products.value = await dataManager.getProducts();
  products.value.forEach(p => {
    if (!(p.id in buyQuantities.value)) {
      buyQuantities.value[p.id] = 1;
    }
  });
};

const addProduct = async () => {
  await dataManager.addProduct(newProduct.value);
  newProduct.value = { name: '', category: '', price: 0, stock: 0, description: '' };
  loadProducts();
};

const editProduct = (product) => {
  editingProduct.value = { ...product };
};

const updateProduct = async () => {
  await dataManager.updateProduct(editingProduct.value);
  editingProduct.value = null;
  loadProducts();
};

const deleteProduct = async (id) => {
  if (confirm('确定删除该商品？')) {
    await dataManager.deleteProduct(id);
    loadProducts();
  }
};

const increaseQty = (product) => {
  if (!buyQuantities.value[product.id]) buyQuantities.value[product.id] = 1;
  if (buyQuantities.value[product.id] < product.stock) {
    buyQuantities.value[product.id]++;
  } else {
    showToastError('库存不足');
  }
};

const goDetail = (id) => {
  router.push(`/user/product/${id}`);
};

const decreaseQty = (product) => {
  if (!buyQuantities.value[product.id]) buyQuantities.value[product.id] = 1;
  if (buyQuantities.value[product.id] > 1) {
    buyQuantities.value[product.id]--;
  }
};

const addToCart = (product) => {
  const qty = buyQuantities.value[product.id] || 1;
  const ok = cartStore.addItem(product, qty);
  if (!ok) {
    toast.error('库存不足');
  } else {
    cartUI.expand();
    toast.success(`已加入购物车：${product.name}`);
  }
};

onMounted(loadProducts);
</script>

<style scoped>
.products-page {
  position: relative;
}

.page-header {
  margin-bottom: 20px;
}

.page-title {
  font-size: 20px;
  font-weight: 700;
  color: #1a1a1a;
}

/* ===== 通用卡片 ===== */
.card {
  background: #fff;
  border-radius: 10px;
  padding: 24px;
  margin-bottom: 20px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
}

.card-title {
  font-size: 15px;
  font-weight: 600;
  color: #333;
  margin-bottom: 16px;
  display: flex;
  align-items: center;
  gap: 6px;
}

/* ===== 添加表单 ===== */
.add-card {
  border: 1px solid #ffe0cc;
}

.add-form {
  display: flex;
  flex-wrap: wrap;
  gap: 10px;
}

.add-form input {
  flex: 1;
  min-width: 140px;
  height: 38px;
  padding: 0 12px;
  border: 1.5px solid #e0e0e0;
  border-radius: 8px;
  font-size: 13px;
  color: #333;
  background: #fafafa;
  transition: border 0.2s;
}

.add-form input:focus {
  border-color: #ff5000;
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
}

.admin-table tr:hover td {
  background: #fafafa;
}

.cell-name {
  font-weight: 600;
  color: #1a1a1a;
}

.cell-price {
  color: #ff5000;
  font-weight: 600;
}

.cell-desc {
  color: #999;
  font-size: 13px;
  max-width: 200px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.cell-actions {
  display: flex;
  gap: 6px;
}

.category-tag {
  display: inline-block;
  padding: 2px 8px;
  background: #fff5ef;
  color: #ff5000;
  border-radius: 4px;
  font-size: 12px;
}

/* ===== 按钮 ===== */
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

.btn:hover {
  border-color: #ff5000;
  color: #ff5000;
}

.btn-primary {
  background: linear-gradient(135deg, #ff5000, #ff6a00);
  color: #fff;
  border-color: transparent;
}

.btn-primary:hover {
  opacity: 0.9;
  color: #fff;
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

/* ===== 弹窗 ===== */
.modal-overlay {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background: rgba(0, 0, 0, 0.4);
  display: flex;
  justify-content: center;
  align-items: center;
  z-index: 200;
}

.modal-card {
  background: #fff;
  border-radius: 12px;
  padding: 28px;
  width: 420px;
  max-width: 90vw;
  box-shadow: 0 20px 60px rgba(0, 0, 0, 0.15);
}

.modal-title {
  font-size: 17px;
  font-weight: 700;
  margin-bottom: 20px;
  color: #1a1a1a;
}

.modal-body {
  display: flex;
  flex-direction: column;
  gap: 10px;
  margin-bottom: 20px;
}

.modal-body input {
  height: 40px;
  padding: 0 12px;
  border: 1.5px solid #e0e0e0;
  border-radius: 8px;
  font-size: 14px;
}

.modal-body input:focus {
  border-color: #ff5000;
}

.modal-actions {
  display: flex;
  gap: 10px;
  justify-content: flex-end;
}

/* ===== 用户视图 - 搜索栏 ===== */
.search-bar {
  position: relative;
  margin-bottom: 16px;
}

.search-icon {
  position: absolute;
  left: 14px;
  top: 50%;
  transform: translateY(-50%);
}

.search-bar input {
  width: 100%;
  height: 46px;
  padding: 0 16px 0 42px;
  border: 1.5px solid #e0e0e0;
  border-radius: 10px;
  font-size: 14px;
  background: #fff;
  transition: border 0.2s;
}

.search-bar input:focus {
  border-color: #ff5000;
  box-shadow: 0 0 0 3px rgba(255, 80, 0, 0.08);
}

/* ===== 分类标签 ===== */
.category-tabs {
  display: flex;
  gap: 8px;
  margin-bottom: 20px;
  flex-wrap: wrap;
}

.tab-btn {
  padding: 7px 18px;
  border-radius: 20px;
  font-size: 13px;
  font-weight: 500;
  background: #fff;
  color: #666;
  border: 1.5px solid #e0e0e0;
  transition: all 0.2s;
}

.tab-btn:hover {
  color: #ff5000;
  border-color: #ffd9c0;
  background: #fff5ef;
}

.tab-btn.active {
  background: #ff5000;
  color: #fff;
  border-color: #ff5000;
}

/* ===== 商品卡片网格 ===== */
.product-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(300px, 1fr));
  gap: 20px;
}

.product-card {
  background: #fff;
  border-radius: 14px;
  overflow: hidden;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
  transition: transform 0.25s, box-shadow 0.25s;
}

.product-card:hover {
  transform: translateY(-6px);
  box-shadow: 0 12px 32px rgba(0, 0, 0, 0.12);
}

.product-img {
  height: 180px;
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
}

.product-emoji {
  font-size: 60px;
  filter: drop-shadow(0 4px 8px rgba(0, 0, 0, 0.15));
}

.product-info {
  padding: 16px 18px 18px;
}

.product-name {
  font-size: 17px;
  font-weight: 700;
  color: #1a1a1a;
  margin-bottom: 8px;
}

.product-meta {
  display: flex;
  align-items: center;
  gap: 10px;
  margin-bottom: 8px;
}

.product-category {
  font-size: 12px;
  padding: 3px 10px;
  background: #fff5ef;
  color: #ff5000;
  border-radius: 4px;
}

.product-stock {
  font-size: 12px;
  color: #999;
}

.low-stock {
  color: #ff4d4f;
}

.product-desc {
  font-size: 13px;
  color: #bbb;
  margin-bottom: 14px;
}

.product-bottom {
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.product-price {
  font-size: 24px;
  font-weight: 800;
  color: #ff5000;
}

.buy-area {
  display: flex;
  align-items: center;
  gap: 10px;
}

.qty-control {
  display: flex;
  align-items: center;
  border: 1.5px solid #e0e0e0;
  border-radius: 8px;
  overflow: hidden;
}

.qty-btn {
  width: 32px;
  height: 32px;
  background: #fafafa;
  font-size: 18px;
  color: #666;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: background 0.2s;
}

.qty-btn:hover {
  background: #f0f0f0;
}

.qty-value {
  width: 36px;
  text-align: center;
  font-size: 14px;
  font-weight: 700;
  color: #333;
}

.cart-add-btn {
  height: 36px;
  padding: 0 18px;
  background: #fff;
  color: #ff5000;
  border: 2px solid #ff5000;
  border-radius: 8px;
  font-size: 13px;
  font-weight: 700;
  transition: all 0.2s;
  white-space: nowrap;
}

.cart-add-btn:hover {
  background: #ff5000;
  color: #fff;
}

/* ===== 空状态 ===== */
.empty-state {
  text-align: center;
  padding: 60px 20px;
  color: #ddd;
}

.empty-state p {
  margin-top: 12px;
  font-size: 14px;
  color: #ccc;
}

/* ===== Toast ===== */
.toast {
  position: fixed;
  top: 80px;
  left: 50%;
  transform: translateX(-50%);
  padding: 12px 24px;
  border-radius: 8px;
  font-size: 14px;
  font-weight: 500;
  display: flex;
  align-items: center;
  gap: 8px;
  z-index: 300;
  animation: toastIn 0.3s ease;
  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.15);
}

.toast-success {
  background: #f6ffed;
  color: #52c41a;
  border: 1px solid #b7eb8f;
}

.toast-error {
  background: #fff2f0;
  color: #ff4d4f;
  border: 1px solid #ffccc7;
}

@keyframes toastIn {
  from {
    opacity: 0;
    transform: translateX(-50%) translateY(-20px);
  }
  to {
    opacity: 1;
    transform: translateX(-50%) translateY(0);
  }
}
</style>
