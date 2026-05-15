import { createRouter, createWebHistory } from 'vue-router';
import LoginView from '../views/LoginView.vue';
import UserDashboard from '../views/UserDashboard.vue';
import AdminDashboard from '../views/AdminDashboard.vue';
import ProductsView from '../views/ProductsView.vue';
import PurchaseHistory from '../views/PurchaseHistory.vue';
import CheckoutView from '../views/CheckoutView.vue';
import ProductDetail from '../views/ProductDetail.vue';
import PersonalCenter from '../views/PersonalCenter.vue';

const routes = [
  { path: '/', component: LoginView },
  {
    path: '/user',
    component: UserDashboard,
    children: [
      { path: 'products', component: ProductsView, props: { isAdmin: false } },
      { path: 'purchases', component: PurchaseHistory, props: { isAdmin: false } },
      { path: 'checkout', component: CheckoutView },
      { path: 'product/:id', component: ProductDetail },
      { path: 'profile', component: PersonalCenter },
      { path: '', redirect: '/user/products' }
    ]
  },
  {
    path: '/admin',
    component: AdminDashboard,
    children: [
      { path: 'users', component: () => import('../views/AdminUsers.vue') },
      { path: 'products', component: ProductsView, props: { isAdmin: true } },
      { path: 'categories', component: () => import('../views/AdminCategories.vue') },
      { path: 'inventory', component: () => import('../views/AdminInventory.vue') },
      { path: 'purchases', component: () => import('../views/AdminPurchases.vue') },
      { path: 'carousels', component: () => import('../views/AdminCarousels.vue') },
      { path: 'announcements', component: () => import('../views/AdminAnnouncements.vue') },
      { path: 'sales', component: () => import('../views/AdminSalesReport.vue') },
      { path: '', redirect: '/admin/users' }
    ]
  }
];

const router = createRouter({ history: createWebHistory(), routes });
export default router;
