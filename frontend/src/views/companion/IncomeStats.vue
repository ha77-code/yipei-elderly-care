<template>
  <div class="page-wrap">
    <h2 class="page-title">收入统计</h2>
    <div class="stat-row">
      <div class="stat-card">
        <span class="stat-value">¥{{ totalIncome.toFixed(2) }}</span>
        <span class="stat-label">总收入（净）</span>
      </div>
      <div class="stat-card">
        <span class="stat-value">¥{{ totalPrice.toFixed(2) }}</span>
        <span class="stat-label">服务总额</span>
      </div>
      <div class="stat-card">
        <span class="stat-value">¥{{ totalFee.toFixed(2) }}</span>
        <span class="stat-label">平台抽成</span>
      </div>
      <div class="stat-card">
        <span class="stat-value">{{ completedCount }}</span>
        <span class="stat-label">已完成订单</span>
      </div>
    </div>

    <div class="content-card">
      <h3 class="section-title">已完成订单明细</h3>
      <el-table :data="completedOrders" v-loading="loading" stripe empty-text="暂无已完成订单">
        <el-table-column prop="id" label="订单号" width="80" align="center" />
        <el-table-column prop="servicePrice" label="服务金额" width="110" align="center">
          <template slot-scope="{ row }">¥{{ row.servicePrice }}</template>
        </el-table-column>
        <el-table-column prop="platformFee" label="平台抽成" width="110" align="center">
          <template slot-scope="{ row }">¥{{ row.platformFee }}</template>
        </el-table-column>
        <el-table-column prop="companionIncome" label="净收入" width="110" align="center">
          <template slot-scope="{ row }"><strong>¥{{ row.companionIncome }}</strong></template>
        </el-table-column>
        <el-table-column prop="completedAt" label="完成时间" width="160" align="center">
          <template slot-scope="{ row }">{{ fmt(row.completedAt || row.completed_at) }}</template>
        </el-table-column>
      </el-table>
    </div>
  </div>
</template>

<script>
import { getMyOrders } from '@/api/order'

export default {
  name: 'IncomeStats',
  data() { return { orders: [], loading: false } },
  computed: {
    completedOrders() { return this.orders.filter(o => o.status === 'COMPLETED') },
    completedCount() { return this.completedOrders.length },
    totalPrice() { return this.completedOrders.reduce((s, o) => s + Number(o.servicePrice || 0), 0) },
    totalFee() { return this.completedOrders.reduce((s, o) => s + Number(o.platformFee || 0), 0) },
    totalIncome() { return this.completedOrders.reduce((s, o) => s + Number(o.companionIncome || 0), 0) }
  },
  created() { this.fetchOrders() },
  methods: {
    async fetchOrders() {
      this.loading = true
      try { const res = await getMyOrders(); const d = res.data || res; this.orders = d.records || d.list || d || [] }
      catch { this.orders = [] } finally { this.loading = false }
    },
    fmt(d) { if (!d) return '-'; return d.replace('T', ' ').substring(0, 16) }
  }
}
</script>

<style scoped>
.page-wrap { padding: 24px 32px; }
.page-title { font-size: 20px; font-weight: 700; color: var(--brand-cream-100); margin: 0 0 20px; }
.stat-row { display: grid; grid-template-columns: repeat(auto-fit, minmax(180px, 1fr)); gap: 16px; margin-bottom: 24px; }
.stat-card { background: #fff; border: 1px solid var(--color-border-light); border-radius: 12px; padding: 20px; text-align: center; box-shadow: var(--shadow-sm); }
.stat-value { display: block; font-size: 24px; font-weight: 700; color: #1a1a1a; margin-bottom: 4px; }
.stat-label { font-size: 13px; color: #888; }
.content-card { background: #fff; border: 1px solid var(--color-border-light); border-radius: var(--radius-md); padding: 20px; box-shadow: var(--shadow-sm); }
.section-title { font-size: 16px; font-weight: 600; color: #1a1a1a; margin: 0 0 16px; }
</style>
