<template>
  <div class="frosted-page">
    <h2 class="frosted-title">收入统计</h2>
    <div class="stat-grid" style="grid-template-columns:repeat(auto-fit,minmax(150px,1fr))">
      <div class="stat-item"><div class="sc-body"><span class="stat-value">¥{{ totalIncome.toFixed(2) }}</span><span class="stat-label">总收入（净）</span></div></div>
      <div class="stat-item"><div class="sc-body"><span class="stat-value">¥{{ totalPrice.toFixed(2) }}</span><span class="stat-label">服务总额</span></div></div>
      <div class="stat-item"><div class="sc-body"><span class="stat-value">¥{{ totalFee.toFixed(2) }}</span><span class="stat-label">平台抽成</span></div></div>
      <div class="stat-item"><div class="sc-body"><span class="stat-value">{{ completedCount }}</span><span class="stat-label">已完成订单</span></div></div>
    </div>

    <div class="frosted-card" style="padding:16px">
      <h3 style="font-size:16px;font-weight:700;color:rgba(78,106,56,0.9);margin:0 0 14px">已完成订单明细</h3>
      <el-table :data="completedOrders" v-loading="loading" stripe empty-text="暂无已完成订单">
        <el-table-column prop="id" label="订单号" min-width="80" align="center" />
        <el-table-column prop="servicePrice" label="服务金额" min-width="110" align="center">
          <template slot-scope="{ row }">¥{{ row.servicePrice }}</template>
        </el-table-column>
        <el-table-column prop="platformFee" label="平台抽成" min-width="110" align="center">
          <template slot-scope="{ row }">¥{{ row.platformFee }}</template>
        </el-table-column>
        <el-table-column prop="companionIncome" label="净收入" min-width="110" align="center">
          <template slot-scope="{ row }"><strong>¥{{ row.companionIncome }}</strong></template>
        </el-table-column>
        <el-table-column prop="completedAt" label="完成时间" min-width="160" align="center">
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
.sc-body { display: flex; flex-direction: column; align-items: center; width: 100%; }
</style>
