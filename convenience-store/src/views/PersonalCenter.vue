<template>
  <div class="profile-page">
    <button class="back-btn" @click="goBack">
      <svg viewBox="0 0 24 24" width="16" height="16" fill="none" stroke="currentColor" stroke-width="2">
        <polyline points="15 18 9 12 15 6"/>
      </svg>
      返回首页
    </button>

    <!-- 用户信息横幅 -->
    <div class="user-banner">
      <div class="user-avatar">{{ user.name ? user.name[0] : '?' }}</div>
      <div class="user-info">
        <h2 class="user-name">{{ user.name }}</h2>
        <p class="user-account">账号：{{ user.username }}</p>
      </div>
      <span class="user-badge">会员</span>
    </div>

    <div class="profile-body">
      <!-- ===== 左侧菜单栏 ===== -->
      <div class="menu-sidebar">
        <div
          v-for="item in menuItems"
          :key="item.key"
          :class="['menu-bar', { active: activeMenu === item.key }]"
          @click="onMenuClick(item)"
        >
          <div class="menu-bar-icon" :style="{ background: item.bg, color: item.color }" v-html="item.icon"></div>
          <span class="menu-bar-label">{{ item.label }}</span>
          <template v-if="item.toggle">
            <label class="toggle-switch" @click.stop>
              <input type="checkbox" v-model="recommendEnabled" @change="onToggleChange" />
              <span class="toggle-slider"></span>
            </label>
          </template>
          <template v-else>
            <svg class="menu-bar-arrow" viewBox="0 0 24 24" width="14" height="14" fill="none" stroke="#ccc" stroke-width="2">
              <polyline points="9 18 15 12 9 6"/>
            </svg>
          </template>
        </div>
      </div>

      <!-- ===== 右侧内容区 ===== -->
      <div class="content-area">
        <!-- 我的信息 -->
        <div v-if="activeMenu === 'info'" class="content-pane">
          <h3 class="pane-title">我的信息</h3>
          <div class="info-rows">
            <div class="info-row"><span class="info-label">用户名</span><span class="info-value">{{ user.username }}</span></div>
            <div class="info-row"><span class="info-label">显示名称</span><span class="info-value">{{ user.name }}</span></div>
            <div class="info-row"><span class="info-label">账号角色</span><span class="info-value">普通用户</span></div>
            <div class="info-row"><span class="info-label">会员等级</span><span class="info-value">标准会员</span></div>
            <div class="info-row"><span class="info-label">注册时间</span><span class="info-value">—</span></div>
          </div>
        </div>

        <!-- 我的订单 -->
        <div v-if="activeMenu === 'orders'" class="content-pane">
          <h3 class="pane-title">我的订单</h3>
          <div v-if="purchases.length === 0" class="empty-pane">
            <p>暂无订单记录</p>
            <button class="link-btn" @click="goBack">去逛逛</button>
          </div>
          <div v-else class="pane-orders">
            <div v-for="p in purchases" :key="p.id" class="order-card-small">
              <div class="order-card-header">
                <span class="order-id">订单 #{{ p.id }}</span>
                <span class="order-status-badge">已完成</span>
              </div>
              <div class="order-card-body">
                <div v-for="item in p.items" :key="item.productId" class="order-card-item">
                  <span>{{ item.name }} × {{ item.quantity }}</span>
                  <span>¥{{ (item.price * item.quantity).toFixed(1) }}</span>
                </div>
              </div>
              <div class="order-card-footer">
                <span>合计</span>
                <span class="order-total">¥{{ p.total }}</span>
              </div>
            </div>
          </div>
        </div>

        <!-- 修改密码 -->
        <div v-if="activeMenu === 'password'" class="content-pane">
          <h3 class="pane-title">修改密码</h3>
          <form @submit.prevent="handleChangePwd" class="pane-form">
            <div class="form-group">
              <label>当前密码</label>
              <input v-model="pwdOld" type="password" placeholder="请输入当前密码" required />
            </div>
            <div class="form-group">
              <label>新密码</label>
              <input v-model="pwdNew" type="password" placeholder="请输入新密码（至少6位）" required />
            </div>
            <div class="form-group">
              <label>确认新密码</label>
              <input v-model="pwdConfirm" type="password" placeholder="请再次输入新密码" required />
            </div>
            <p v-if="pwdMsg" :class="['form-msg', pwdMsgType]">{{ pwdMsg }}</p>
            <button type="submit" class="form-btn">确认修改</button>
          </form>
        </div>

        <!-- 个性推荐 -->
        <div v-if="activeMenu === 'recommend'" class="content-pane">
          <h3 class="pane-title">个性推荐</h3>
          <div class="recommend-info-card">
            <div class="recommend-status">
              <span class="status-label">当前状态</span>
              <span :class="['status-badge', recommendEnabled ? 'on' : 'off']">
                {{ recommendEnabled ? '已开启' : '已关闭' }}
              </span>
            </div>
            <p class="recommend-desc">
              {{ recommendEnabled
                ? '个性推荐已开启，在商品详情页的推荐栏目中，将根据您的购买记录智能推荐您可能喜欢的商品。'
                : '开启后，商品详情页的推荐将根据您的购买记录进行智能推荐。' }}
            </p>
            <div class="recommend-hint">
              <span>💡 在个人中心「个性推荐」菜单中可随时开关此功能</span>
            </div>
          </div>
        </div>

        <!-- 浏览历史 -->
        <div v-if="activeMenu === 'history'" class="content-pane">
          <h3 class="pane-title">浏览历史</h3>
          <div v-if="historyProducts.length === 0" class="empty-pane">
            <p>暂无浏览记录</p>
          </div>
          <div v-else class="rec-grid">
            <div v-for="item in historyProducts" :key="item.id" class="rec-card" @click="goProduct(item.id)">
              <div class="rec-card-img" :style="{ background: productColor(item.category) }">
                <span class="rec-card-emoji">{{ productEmoji(item.category) }}</span>
              </div>
              <div class="rec-card-info">
                <div class="rec-card-name">{{ item.name }}</div>
                <div class="rec-card-price">¥{{ item.price }}</div>
              </div>
            </div>
          </div>
        </div>

        <!-- 联系客服 -->
        <div v-if="activeMenu === 'service'" class="content-pane">
          <h3 class="pane-title">联系客服</h3>
          <div class="service-card">
            <div class="service-item">
              <span class="service-icon">📧</span>
              <div>
                <div class="service-label">电子邮箱</div>
                <div class="service-value">support@convstore.com</div>
              </div>
            </div>
            <div class="service-item">
              <span class="service-icon">📞</span>
              <div>
                <div class="service-label">客服电话</div>
                <div class="service-value">400-888-0000</div>
              </div>
            </div>
            <div class="service-item">
              <span class="service-icon">🕐</span>
              <div>
                <div class="service-label">服务时间</div>
                <div class="service-value">周一至周日 9:00 - 21:00</div>
              </div>
            </div>
          </div>
        </div>

        <!-- 关于我们 -->
        <div v-if="activeMenu === 'about'" class="content-pane">
          <h3 class="pane-title">关于我们</h3>
          <div class="about-content">
            <p>欢迎使用便利店管理系统！</p>
            <p>本系统致力于为您提供便捷、高效的购物体验。您可以在线浏览商品、加入购物车、一键结算，随时随地满足日常所需。</p>
            <div class="about-version">
              <span>版本号：v2.0.0</span>
              <span>更新日期：2026-05-11</span>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import { useRouter } from 'vue-router';
import { dataManager } from '../utils/dataManager';
import { browseHistory } from '../utils/browseHistory';

const router = useRouter();
const user = ref({});
const purchases = ref([]);
const allProducts = ref([]);
const recommended = ref([]);
const historyProducts = ref([]);
const activeMenu = ref('orders');
const recommendEnabled = ref(true);

// Password form
const pwdOld = ref(''); const pwdNew = ref(''); const pwdConfirm = ref('');
const pwdMsg = ref(''); const pwdMsgType = ref('');

const menuItems = [
  { key: 'info', label: '我的信息', icon: '<svg viewBox="0 0 24 24" width="18" height="18" fill="none" stroke="currentColor" stroke-width="2"><path d="M20 21v-2a4 4 0 0 0-4-4H8a4 4 0 0 0-4 4v2"/><circle cx="12" cy="7" r="4"/></svg>', bg: '#fff5ef', color: '#ff5000' },
  { key: 'orders', label: '我的订单', icon: '<svg viewBox="0 0 24 24" width="18" height="18" fill="none" stroke="currentColor" stroke-width="2"><rect x="2" y="7" width="20" height="14" rx="2"/><path d="M16 21V5a2 2 0 0 0-2-2h-4a2 2 0 0 0-2 2v16"/></svg>', bg: '#e6f7ff', color: '#1890ff' },
  { key: 'password', label: '修改密码', icon: '<svg viewBox="0 0 24 24" width="18" height="18" fill="none" stroke="currentColor" stroke-width="2"><rect x="3" y="11" width="18" height="11" rx="2" ry="2"/><path d="M7 11V7a5 5 0 0 1 10 0v4"/></svg>', bg: '#f6ffed', color: '#52c41a' },
  { key: 'recommend', label: '个性推荐', icon: '<svg viewBox="0 0 24 24" width="18" height="18" fill="none" stroke="currentColor" stroke-width="2"><polygon points="12 2 15.09 8.26 22 9.27 17 14.14 18.18 21.02 12 17.77 5.82 21.02 7 14.14 2 9.27 8.91 8.26 12 2"/></svg>', bg: '#fff7e6', color: '#fa8c16', toggle: true },
  { key: 'history', label: '浏览历史', icon: '<svg viewBox="0 0 24 24" width="18" height="18" fill="none" stroke="currentColor" stroke-width="2"><circle cx="12" cy="12" r="10"/><polyline points="12 6 12 12 16 14"/></svg>', bg: '#f0f5ff', color: '#2f54eb' },
  { key: 'service', label: '联系客服', icon: '<svg viewBox="0 0 24 24" width="18" height="18" fill="none" stroke="currentColor" stroke-width="2"><path d="M21 15a2 2 0 0 1-2 2H7l-4 4V5a2 2 0 0 1 2-2h14a2 2 0 0 1 2 2z"/></svg>', bg: '#fff0f6', color: '#eb2f96' },
  { key: 'about', label: '关于我们', icon: '<svg viewBox="0 0 24 24" width="18" height="18" fill="none" stroke="currentColor" stroke-width="2"><circle cx="12" cy="12" r="10"/><line x1="12" y1="16" x2="12" y2="12"/><line x1="12" y1="8" x2="12.01" y2="8"/></svg>', bg: '#f0f0f0', color: '#666' }
];

const goBack = () => router.push('/user/products');
const goProduct = (id) => router.push(`/user/product/${id}`);

const productColor = (c) => ({ '饮料': 'linear-gradient(135deg, #667eea, #764ba2)', '速食': 'linear-gradient(135deg, #f093fb, #f5576c)', '零食': 'linear-gradient(135deg, #4facfe, #00f2fe)', '日用品': 'linear-gradient(135deg, #43e97b, #38f9d7)' }[c] || 'linear-gradient(135deg, #a8edea, #fed6e3)');
const productEmoji = (c) => ({ '饮料': '🥤', '速食': '🍜', '零食': '🍪', '日用品': '🧴' }[c] || '📦');

const onMenuClick = (item) => {
  if (!item.toggle) activeMenu.value = item.key;
};

const onToggleChange = () => {
  dataManager.savePreference(user.value.id, recommendEnabled.value);
  if (recommendEnabled.value) activeMenu.value = 'recommend';
};

const handleChangePwd = async () => {
  pwdMsg.value = '';
  if (pwdNew.value.length < 6) { pwdMsg.value = '密码长度不能少于6位'; pwdMsgType.value = 'error'; return; }
  if (pwdNew.value !== pwdConfirm.value) { pwdMsg.value = '两次密码输入不一致'; pwdMsgType.value = 'error'; return; }
  const ok = await dataManager.changePassword(user.value.id, pwdOld.value, pwdNew.value);
  if (ok) {
    pwdMsg.value = '密码修改成功！'; pwdMsgType.value = 'success';
    pwdOld.value = ''; pwdNew.value = ''; pwdConfirm.value = '';
  } else {
    pwdMsg.value = '当前密码错误'; pwdMsgType.value = 'error';
  }
};

onMounted(async () => {
  const cu = await dataManager.getCurrentUser();
  if (!cu) { router.push('/'); return; }
  user.value = cu;
  purchases.value = await dataManager.getUserPurchases(user.value.id);
  allProducts.value = await dataManager.getProducts();
  const pref = await dataManager.loadPreference(user.value.id);
  recommendEnabled.value = pref.recommendEnabled;

  // Browse history
  const ids = browseHistory.get();
  historyProducts.value = ids.map(id => allProducts.value.find(p => p.id === id)).filter(Boolean);

  // Recommendations based on purchase history
  const boughtCats = [...new Set(purchases.value.flatMap(p => (p.items || []).map(i => {
    const prod = allProducts.value.find(x => x.id === i.productId);
    return prod ? prod.category : '';
  })).filter(Boolean))];
  if (boughtCats.length > 0) {
    const boughtIds = new Set(purchases.value.flatMap(p => (p.items || []).map(i => i.productId)));
    recommended.value = allProducts.value.filter(p => boughtCats.includes(p.category) && !boughtIds.has(p.id)).slice(0, 8);
  }
  if (recommended.value.length === 0) {
    recommended.value = allProducts.value.slice(0, 8);
  }
});
</script>

<style scoped>
.profile-page {
  max-width: 820px;
  margin: 0 auto;
}

.back-btn {
  display: inline-flex; align-items: center; gap: 4px;
  padding: 8px 18px; font-size: 14px; color: #666;
  background: #fff; border-radius: 8px; border: 1.5px solid #e0e0e0;
  margin-bottom: 18px; transition: all 0.2s;
}
.back-btn:hover { border-color: #ff5000; color: #ff5000; }

/* ===== 用户横幅 ===== */
.user-banner {
  display: flex; align-items: center; gap: 16px;
  background: #fff; border-radius: 14px;
  padding: 22px 24px; margin-bottom: 20px;
  box-shadow: 0 2px 8px rgba(0,0,0,0.06);
}
.user-avatar {
  width: 56px; height: 56px;
  background: linear-gradient(135deg, #ff5000, #ff6a00);
  color: #fff; border-radius: 50%;
  display: flex; align-items: center; justify-content: center;
  font-size: 22px; font-weight: 700; flex-shrink: 0;
}
.user-name { font-size: 18px; font-weight: 700; color: #1a1a1a; }
.user-account { font-size: 13px; color: #999; margin-top: 2px; }
.user-badge {
  margin-left: auto; padding: 4px 14px;
  background: #fff5ef; color: #ff5000;
  font-size: 12px; font-weight: 600; border-radius: 12px;
  border: 1px solid #ffe0cc;
}

/* ===== 主体布局 ===== */
.profile-body {
  display: flex; gap: 20px; align-items: flex-start;
}

/* ===== 左侧菜单 ===== */
.menu-sidebar {
  width: 200px; flex-shrink: 0;
  background: #fff; border-radius: 14px;
  padding: 8px; box-shadow: 0 2px 8px rgba(0,0,0,0.06);
}

.menu-bar {
  display: flex; align-items: center; gap: 10px;
  padding: 12px 12px; border-radius: 10px;
  cursor: pointer; transition: all 0.2s;
  margin-bottom: 2px;
}
.menu-bar:last-child { margin-bottom: 0; }
.menu-bar:hover { background: #fafafa; }
.menu-bar.active { background: #fff5ef; }

.menu-bar-icon {
  width: 34px; height: 34px; border-radius: 8px;
  display: flex; align-items: center; justify-content: center;
  flex-shrink: 0;
}
.menu-bar-label { font-size: 14px; font-weight: 500; color: #333; flex: 1; }
.menu-bar-arrow { flex-shrink: 0; }

/* ===== Toggle 开关 ===== */
.toggle-switch {
  position: relative; display: inline-block;
  width: 38px; height: 22px; flex-shrink: 0;
}
.toggle-switch input { opacity: 0; width: 0; height: 0; }
.toggle-slider {
  position: absolute; inset: 0; cursor: pointer;
  background: #ddd; border-radius: 22px;
  transition: 0.3s;
}
.toggle-slider::before {
  content: ''; position: absolute; left: 3px; bottom: 3px;
  width: 16px; height: 16px; background: #fff;
  border-radius: 50%; transition: 0.3s;
}
.toggle-switch input:checked + .toggle-slider { background: #ff5000; }
.toggle-switch input:checked + .toggle-slider::before { transform: translateX(16px); }

/* ===== 右侧内容 ===== */
.content-area {
  flex: 1; min-width: 0;
  background: #fff; border-radius: 14px;
  padding: 24px; box-shadow: 0 2px 8px rgba(0,0,0,0.06);
  min-height: 300px;
}

.pane-title { font-size: 17px; font-weight: 700; color: #1a1a1a; margin-bottom: 18px; padding-bottom: 12px; border-bottom: 2px solid #f0f0f0; }
.pane-desc { font-size: 13px; color: #bbb; margin-top: -14px; margin-bottom: 16px; }

/* 信息行 */
.info-rows { display: flex; flex-direction: column; }
.info-row { display: flex; padding: 12px 0; border-bottom: 1px solid #f8f8f8; font-size: 14px; }
.info-row:last-child { border-bottom: none; }
.info-label { width: 80px; color: #999; }
.info-value { color: #333; font-weight: 500; }

/* 空状态 */
.empty-pane { text-align: center; padding: 40px 20px; color: #ccc; }
.empty-pane p { margin-bottom: 6px; }
.empty-hint { font-size: 12px; color: #e0e0e0; }
.link-btn { display: inline-block; margin-top: 12px; padding: 8px 24px; background: linear-gradient(135deg, #ff5000, #ff6a00); color: #fff; border-radius: 8px; font-size: 13px; font-weight: 600; }

/* 订单列表 */
.pane-orders { display: flex; flex-direction: column; gap: 12px; }
.order-card-small { border: 1px solid #f0f0f0; border-radius: 10px; padding: 14px; }
.order-card-header { display: flex; justify-content: space-between; margin-bottom: 8px; padding-bottom: 8px; border-bottom: 1px solid #f5f5f5; }
.order-id { font-size: 13px; font-weight: 600; color: #333; }
.order-status-badge { font-size: 11px; padding: 2px 8px; background: #f6ffed; color: #52c41a; border-radius: 4px; }
.order-card-item { display: flex; justify-content: space-between; font-size: 13px; color: #666; padding: 4px 0; }
.order-card-footer { display: flex; justify-content: space-between; margin-top: 8px; padding-top: 8px; border-top: 1px solid #f0f0f0; font-size: 14px; font-weight: 600; }
.order-total { color: #ff5000; }

/* 密码表单 */
.pane-form { display: flex; flex-direction: column; gap: 14px; max-width: 360px; }
.form-group { display: flex; flex-direction: column; gap: 5px; }
.form-group label { font-size: 13px; font-weight: 500; color: #333; }
.form-group input { height: 42px; padding: 0 12px; border: 1.5px solid #e0e0e0; border-radius: 8px; font-size: 14px; transition: border 0.2s; }
.form-group input:focus { border-color: #ff5000; }
.form-msg { font-size: 13px; text-align: center; }
.form-msg.success { color: #52c41a; }
.form-msg.error { color: #ff4d4f; }
.form-btn { height: 44px; background: linear-gradient(135deg, #ff5000, #ff6a00); color: #fff; border-radius: 10px; font-size: 15px; font-weight: 600; letter-spacing: 1px; transition: opacity 0.2s; }
.form-btn:hover { opacity: 0.9; }

/* 个性推荐信息卡 */
.recommend-info-card { background: #fafafa; border-radius: 12px; padding: 24px; }
.recommend-status { display: flex; align-items: center; gap: 12px; margin-bottom: 14px; }
.status-label { font-size: 14px; color: #333; font-weight: 500; }
.status-badge { font-size: 12px; padding: 4px 14px; border-radius: 12px; font-weight: 600; }
.status-badge.on { background: #f6ffed; color: #52c41a; border: 1px solid #b7eb8f; }
.status-badge.off { background: #fff2f0; color: #ff4d4f; border: 1px solid #ffccc7; }
.recommend-desc { font-size: 13px; color: #666; line-height: 1.7; margin-bottom: 14px; }
.recommend-hint { font-size: 12px; color: #ccc; }

/* 推荐网格 */
.rec-grid { display: grid; grid-template-columns: repeat(auto-fill, minmax(160px, 1fr)); gap: 12px; }
.rec-card { background: #fff; border-radius: 10px; overflow: hidden; box-shadow: 0 2px 8px rgba(0,0,0,0.06); cursor: pointer; transition: transform 0.2s; border: 1px solid #f5f5f5; }
.rec-card:hover { transform: translateY(-3px); box-shadow: 0 6px 16px rgba(0,0,0,0.1); }
.rec-card-img { height: 120px; display: flex; align-items: center; justify-content: center; }
.rec-card-emoji { font-size: 42px; }
.rec-card-info { padding: 10px 12px 12px; }
.rec-card-name { font-size: 13px; font-weight: 600; color: #333; margin-bottom: 4px; }
.rec-card-price { font-size: 16px; font-weight: 700; color: #ff5000; }

/* 客服卡片 */
.service-card { display: flex; flex-direction: column; gap: 16px; }
.service-item { display: flex; align-items: center; gap: 14px; padding: 12px; background: #fafafa; border-radius: 10px; }
.service-icon { font-size: 28px; }
.service-label { font-size: 12px; color: #999; margin-bottom: 2px; }
.service-value { font-size: 14px; color: #333; font-weight: 500; }

/* 关于 */
.about-content { font-size: 14px; color: #666; line-height: 1.8; }
.about-content p { margin-bottom: 12px; }
.about-version { margin-top: 20px; padding-top: 14px; border-top: 1px solid #f0f0f0; display: flex; flex-direction: column; gap: 4px; font-size: 12px; color: #ccc; }
</style>
