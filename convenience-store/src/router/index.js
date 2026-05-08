import { createRouter, createWebHistory } from 'vue-router';
import LoginView from '../views/LoginView.vue';
import UserDashboard from '../views/UserDashboard.vue';
import AdminDashboard from '../views/AdminDashboard.vue';
import ProductsView from '../views/ProductsView.vue';
import PurchaseHistory from '../views/PurchaseHistory.vue';

const routes = [
  { path: '/', component: LoginView },
  {
    path: '/user',
    component: UserDashboard,
    children: [
      { path: 'products', component: ProductsView, props: { isAdmin: false } },
      { path: 'purchases', component: PurchaseHistory, props: { isAdmin: false } },
      { path: '', redirect: '/user/products' }
    ]
  },
  {
    path: '/admin',
    component: AdminDashboard,
    children: [
      { path: 'products', component: ProductsView, props: { isAdmin: true } },
      { path: 'purchases', component: PurchaseHistory, props: { isAdmin: true } },
      { path: '', redirect: '/admin/products' }
    ]
  }
];

const router = createRouter({
  history: createWebHistory(),
  routes
});

export default router;
