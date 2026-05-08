<template>
  <div class="purchase-history">
    <h2>{{ isAdmin ? '所有购买记录' : '我的购买记录' }}</h2>
    <table>
      <thead>
        <tr>
          <th v-if="isAdmin">用户</th>
          <th>商品</th>
          <th>总价</th>
          <th>日期</th>
          <th v-if="isAdmin">操作</th>
        </tr>
      </thead>
      <tbody>
        <tr v-for="purchase in purchases" :key="purchase.id">
          <td v-if="isAdmin">{{ purchase.username }}</td>
          <td>
            <div v-for="item in purchase.items" :key="item.productId">
              {{ item.name }} x {{ item.quantity }} (¥{{ item.price }})
            </div>
          </td>
          <td>¥{{ purchase.total }}</td>
          <td>{{ purchase.date }}</td>
          <td v-if="isAdmin">
            <button @click="deletePurchase(purchase.id)" class="delete">删除</button>
          </td>
        </tr>
      </tbody>
    </table>
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
  if (confirm('确定删除？')) {
    await dataManager.deletePurchase(id);
    loadPurchases();
  }
};

onMounted(loadPurchases);
</script>

<style scoped>
.purchase-history { padding: 20px; }
table { width: 100%; border-collapse: collapse; margin-top: 20px; }
th, td { padding: 10px; text-align: left; border-bottom: 1px solid #ddd; }
.delete { background: #f44336; color: white; border: none; padding: 5px 10px; cursor: pointer; }
</style>
