<template>
  <div class="page">
    <h2 class="page-title">销售报表</h2>

    <!-- 时间筛选 -->
    <div class="card filter-card">
      <div class="filter-row">
        <div class="filter-group">
          <label>开始日期</label>
          <input v-model="fStart" type="date" class="fi" />
        </div>
        <div class="filter-group">
          <label>结束日期</label>
          <input v-model="fEnd" type="date" class="fi" />
        </div>
        <button class="btn btn-primary" @click="loadReport">查询</button>
        <button class="btn" @click="setThisMonth">本月</button>
        <button class="btn" @click="setThisWeek">本周</button>
        <button class="btn" @click="setToday">今天</button>
      </div>
    </div>

    <!-- 概览卡片 -->
    <div class="stats-row">
      <div class="stat-card">
        <span class="stat-label">订单总数</span>
        <span class="stat-value">{{ report.orderCount || 0 }}</span>
      </div>
      <div class="stat-card">
        <span class="stat-label">销售总额</span>
        <span class="stat-value orange">¥{{ (report.totalSales || 0).toFixed(1) }}</span>
      </div>
      <div class="stat-card">
        <span class="stat-label">平均单价</span>
        <span class="stat-value">¥{{ report.orderCount ? (report.totalSales / report.orderCount).toFixed(1) : '0.0' }}</span>
      </div>
    </div>

    <!-- 柱状图：商品销量排行 -->
    <div class="card">
      <h3 class="card-title">商品销量排行</h3>
      <div v-if="report.ranking && report.ranking.length > 0" class="bar-chart">
        <div v-for="(item, i) in report.ranking.slice(0, 10)" :key="i" class="bar-row">
          <span class="bar-rank">{{ i + 1 }}</span>
          <span class="bar-name">{{ item.name }}</span>
          <div class="bar-track">
            <div class="bar-fill" :style="{ width: barWidth(item.quantity) }"></div>
          </div>
          <span class="bar-qty">{{ item.quantity }}</span>
        </div>
      </div>
      <div v-else class="empty-chart">暂无销售数据</div>
    </div>

    <!-- 表格 -->
    <div class="card">
      <h3 class="card-title">销量明细</h3>
      <table class="table" v-if="report.ranking && report.ranking.length > 0">
        <thead><tr><th>排名</th><th>商品名称</th><th>销售数量</th><th>占比</th></tr></thead>
        <tbody>
          <tr v-for="(item, i) in report.ranking" :key="i">
            <td>{{ i + 1 }}</td><td>{{ item.name }}</td>
            <td class="price">{{ item.quantity }}</td>
            <td>{{ percent(item.quantity) }}%</td>
          </tr>
        </tbody>
      </table>
      <div v-else class="empty-chart">暂无销售数据</div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue';
import { dataManager } from '../utils/dataManager';

const fStart = ref('');
const fEnd = ref('');
const report = reactive({ totalSales: 0, orderCount: 0, ranking: [] });
const maxQty = ref(1);

const loadReport = async () => {
  const res = await dataManager.getSalesReport(fStart.value, fEnd.value);
  report.totalSales = res.totalSales || 0;
  report.orderCount = res.orderCount || 0;
  report.ranking = res.ranking || [];
  if (report.ranking.length > 0) maxQty.value = report.ranking[0].quantity;
};

const barWidth = (qty) => maxQty.value > 0 ? (qty / maxQty.value * 100) + '%' : '0%';
const percent = (qty) => {
  const total = report.ranking.reduce((s, r) => s + r.quantity, 0);
  return total > 0 ? (qty / total * 100).toFixed(1) : '0.0';
};

const pad = (n) => String(n).padStart(2, '0');
const setToday = () => {
  const d = new Date();
  fStart.value = fEnd.value = `${d.getFullYear()}-${pad(d.getMonth()+1)}-${pad(d.getDate())}`;
  loadReport();
};
const setThisWeek = () => {
  const d = new Date(); const day = d.getDay();
  const mon = new Date(d); mon.setDate(d.getDate() - (day === 0 ? 6 : day - 1));
  fStart.value = `${mon.getFullYear()}-${pad(mon.getMonth()+1)}-${pad(mon.getDate())}`;
  fEnd.value = `${d.getFullYear()}-${pad(d.getMonth()+1)}-${pad(d.getDate())}`;
  loadReport();
};
const setThisMonth = () => {
  const d = new Date();
  fStart.value = `${d.getFullYear()}-${pad(d.getMonth()+1)}-01`;
  fEnd.value = `${d.getFullYear()}-${pad(d.getMonth()+1)}-${pad(d.getDate())}`;
  loadReport();
};

// Default: this month
setThisMonth();
</script>

<style scoped>
.page-title { font-size:20px; font-weight:700; color:#fff; margin-bottom:20px; }
.card { background:#fff; border-radius:10px; padding:20px; margin-bottom:16px; }
.card-title { font-size:15px; font-weight:700; color:#1a1a1a; margin-bottom:16px; padding-bottom:10px; border-bottom:1px solid #f0f0f0; }

.filter-card { padding:16px 20px; }
.filter-row { display:flex; gap:10px; align-items:flex-end; flex-wrap:wrap; }
.filter-group { display:flex; flex-direction:column; gap:4px; }
.filter-group label { font-size:12px; color:#999; }
.fi { height:36px; padding:0 10px; border:1.5px solid #e0e0e0; border-radius:6px; font-size:13px; }
.fi:focus { border-color:#ff5000; }

.btn { padding:8px 18px; border-radius:6px; font-size:13px; border:1.5px solid #e0e0e0; background:#fff; color:#333; cursor:pointer; }
.btn-primary { background:linear-gradient(135deg,#ff5000,#ff6a00); color:#fff; border-color:transparent; }
.btn-primary:hover { opacity:0.9; }

/* 概览 */
.stats-row { display:flex; gap:16px; margin-bottom:16px; }
.stat-card { flex:1; background:#fff; border-radius:12px; padding:20px; text-align:center; box-shadow:0 2px 8px rgba(0,0,0,0.06); }
.stat-label { display:block; font-size:13px; color:#999; margin-bottom:8px; }
.stat-value { font-size:28px; font-weight:800; color:#1a1a1a; }
.stat-value.orange { color:#ff5000; }

/* 柱状图 */
.bar-chart { display:flex; flex-direction:column; gap:8px; }
.bar-row { display:flex; align-items:center; gap:10px; }
.bar-rank { width:20px; font-size:12px; font-weight:700; color:#ccc; text-align:center; }
.bar-name { width:120px; font-size:13px; color:#333; overflow:hidden; text-overflow:ellipsis; white-space:nowrap; flex-shrink:0; }
.bar-track { flex:1; height:20px; background:#f5f5f5; border-radius:10px; overflow:hidden; }
.bar-fill { height:100%; background:linear-gradient(90deg,#ff5000,#ff6a00); border-radius:10px; transition:width 0.5s ease; min-width:4px; }
.bar-qty { width:40px; font-size:13px; font-weight:700; color:#ff5000; text-align:right; }

.empty-chart { text-align:center; padding:30px; color:#ccc; font-size:14px; }

/* 表格 */
.table { width:100%; border-collapse:collapse; }
.table th { text-align:left; padding:10px; font-size:13px; font-weight:600; color:#999; border-bottom:2px solid #f0f0f0; }
.table td { padding:10px; font-size:14px; border-bottom:1px solid #f5f5f5; }
.price { color:#ff5000; font-weight:600; }
</style>
