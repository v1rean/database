<template>
  <div class="products">
    <h2>{{ isAdmin ? '商品管理' : '商品浏览' }}</h2>
    <div v-if="isAdmin" class="add-form">
      <h3>添加商品</h3>
      <input v-model="newProduct.name" placeholder="商品名称" />
      <input v-model="newProduct.category" placeholder="分类" />
      <input v-model.number="newProduct.price" type="number" placeholder="价格" />
      <input v-model.number="newProduct.stock" type="number" placeholder="库存" />
      <input v-model="newProduct.description" placeholder="描述" />
      <button @click="addProduct">添加</button>
    </div>
    <div class="category-filter" v-if="!isAdmin">
      <button @click="selectedCategory = ''">全部</button>
      <button v-for="cat in categories" :key="cat" @click="selectedCategory = cat">
        {{ cat }}
      </button>
    </div>
    <table>
      <thead>
        <tr>
          <th>名称</th>
          <th>分类</th>
          <th>价格</th>
          <th>库存</th>
          <th>描述</th>
          <th v-if="isAdmin">操作</th>
        </tr>
      </thead>
      <tbody>
        <tr v-for="product in filteredProducts" :key="product.id">
          <td>{{ product.name }}</td>
          <td>{{ product.category }}</td>
          <td>¥{{ product.price }}</td>
          <td>{{ product.stock }}</td>
          <td>{{ product.description }}</td>
          <td v-if="isAdmin">
            <button @click="editProduct(product)">编辑</button>
            <button @click="deleteProduct(product.id)" class="delete">删除</button>
          </td>
        </tr>
      </tbody>
    </table>

    <div v-if="editingProduct" class="modal">
      <div class="modal-content">
        <h3>编辑商品</h3>
        <input v-model="editingProduct.name" placeholder="商品名称" />
        <input v-model="editingProduct.category" placeholder="分类" />
        <input v-model.number="editingProduct.price" type="number" placeholder="价格" />
        <input v-model.number="editingProduct.stock" type="number" placeholder="库存" />
        <input v-model="editingProduct.description" placeholder="描述" />
        <button @click="updateProduct">保存</button>
        <button @click="editingProduct = null">取消</button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue';
import { dataManager } from '../utils/dataManager';

const props = defineProps({ isAdmin: Boolean });
const products = ref([]);
const selectedCategory = ref('');
const editingProduct = ref(null);
const newProduct = ref({ name: '', category: '', price: 0, stock: 0, description: '' });

const categories = computed(() => [...new Set(products.value.map(p => p.category))]);
const filteredProducts = computed(() =>
  selectedCategory.value ? products.value.filter(p => p.category === selectedCategory.value) : products.value
);

const loadProducts = async () => { products.value = await dataManager.getProducts(); };
const addProduct = async () => {
  await dataManager.addProduct(newProduct.value);
  newProduct.value = { name: '', category: '', price: 0, stock: 0, description: '' };
  loadProducts();
};
const editProduct = (product) => { editingProduct.value = { ...product }; };
const updateProduct = async () => {
  await dataManager.updateProduct(editingProduct.value);
  editingProduct.value = null;
  loadProducts();
};
const deleteProduct = async (id) => {
  if (confirm('确定删除？')) {
    await dataManager.deleteProduct(id);
    loadProducts();
  }
};

onMounted(loadProducts);
</script>

<style scoped>
.products { padding: 20px; }
.add-form { margin-bottom: 20px; }
.add-form input { margin-right: 10px; padding: 5px; }
.category-filter button {
  margin-right: 10px;
  padding: 5px 15px;
  background: #667eea;
  color: white;
  border: none;
  border-radius: 3px;
  cursor: pointer;
}
table { width: 100%; border-collapse: collapse; margin-top: 20px; }
th, td { padding: 10px; text-align: left; border-bottom: 1px solid #ddd; }
button { padding: 5px 10px; margin-right: 5px; cursor: pointer; }
.delete { background: #f44336; color: white; border: none; }
.modal {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background: rgba(0,0,0,0.5);
  display: flex;
  justify-content: center;
  align-items: center;
}
.modal-content {
  background: white;
  padding: 30px;
  border-radius: 10px;
}
.modal-content input { display: block; margin: 10px 0; padding: 5px; width: 100%; }
</style>
