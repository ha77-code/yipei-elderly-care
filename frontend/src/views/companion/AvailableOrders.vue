<template>
  <div class="page-wrap">
    <h2 class="page-title">可接订单</h2>
    <div class="content-card">
      <div class="order-grid" v-loading="loading">
        <div v-for="item in list" :key="item.id" class="order-card">
          <div class="oc-header">
            <span class="oc-id">订单 #{{ item.id }}</span>
            <span class="oc-price">¥{{ item.servicePrice }}</span>
          </div>
          <div class="oc-body">
            <div class="oc-row"><i class="el-icon-office-building"></i> {{ item.hospitalName || '-' }} · {{ item.department || '-' }}</div>
            <div class="oc-row"><i class="el-icon-date"></i> {{ fmt(item.serviceDate) }}</div>
            <div class="oc-row"><i class="el-icon-document"></i> {{ item.requirement || '暂无需求描述' }}</div>
          </div>
          <div class="oc-footer">
            <el-tag size="small">{{ item.serviceType }}</el-tag>
            <el-button type="primary" size="small" round @click="handleAccept(item)">接单</el-button>
          </div>
        </div>
        <div class="empty-state" v-if="!loading && list.length === 0">
          <i class="el-icon-s-claim"></i><p>当前没有可接订单</p>
        </div>
      </div>
      <div class="table-footer" v-if="total > pageSize">
        <el-pagination background layout="prev, pager, next" :total="total" :page-size="pageSize" :current-page.sync="currentPage" @current-change="fetchList" />
      </div>
    </div>
  </div>
</template>

<script>
import { getAvailableOrders, acceptOrder } from '@/api/order'

export default {
  name: 'AvailableOrders',
  data() { return { list: [], loading: false, currentPage: 1, pageSize: 12, total: 0 } },
  created() { this.fetchList() },
  methods: {
    async fetchList() {
      this.loading = true
      try { const res = await getAvailableOrders({ page: this.currentPage, size: this.pageSize }); const d = res.data || res; this.list = d.records || d.list || d || []; this.total = d.total || this.list.length } catch { this.list = [] } finally { this.loading = false }
    },
    handleAccept(item) {
      this.$confirm(`确认接单 #${item.id}，服务金额 ¥${item.servicePrice}？`, '确认接单', { confirmButtonText: '确认接单', type: 'success' }).then(async () => { try { await acceptOrder(item.id); this.$message.success('接单成功'); this.fetchList() } catch {} }).catch(() => {})
    },
    fmt(d) { if (!d) return '-'; return d.replace('T', ' ').substring(0, 16) }
  }
}
</script>

<style scoped>
.page-wrap { padding: 24px 32px; }
.page-title { font-family: var(--font-family); font-size: 20px; font-weight: 700; color: var(--color-text-primary); margin: 0 0 20px; }
.content-card { background: #FFF; border: 1px solid var(--color-border-light); border-radius: var(--radius-md); padding: 20px; box-shadow: var(--shadow-sm); }
.order-grid { display: grid; grid-template-columns: repeat(auto-fill, minmax(320px, 1fr)); gap: 16px; min-height: 150px; }
.order-card { border: 1px solid var(--color-border-light); border-radius: var(--radius-md); padding: 20px; transition: all .2s; cursor: default; }
.order-card:hover { border-color: var(--color-primary-light); box-shadow: var(--shadow-md); }
.oc-header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 14px; }
.oc-id { font-size: 13px; color: var(--color-text-placeholder); font-weight: 500; }
.oc-price { font-family: var(--font-family); font-size: 22px; font-weight: 700; color: var(--color-primary-dark); }
.oc-body { display: flex; flex-direction: column; gap: 8px; margin-bottom: 16px; }
.oc-row { font-size: 14px; color: var(--color-text-secondary); display: flex; align-items: center; gap: 6px; overflow: hidden; text-overflow: ellipsis; white-space: nowrap; }
.oc-row i { color: var(--color-primary); font-size: 14px; flex-shrink: 0; }
.oc-footer { display: flex; justify-content: space-between; align-items: center; }
.empty-state { grid-column: 1 / -1; text-align: center; padding: 80px 0; color: var(--color-text-placeholder); }
.empty-state i { font-size: 48px; color: var(--color-border); margin-bottom: 16px; display: block; }
.table-footer { display: flex; justify-content: center; margin-top: 24px; }
</style>
