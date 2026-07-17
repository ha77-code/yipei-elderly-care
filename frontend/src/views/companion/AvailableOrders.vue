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
            <div v-if="item.aiSummary" class="ai-summary">
              <div class="ai-summary-title"><i class="el-icon-magic-stick"></i> AI需求摘要</div>
              <p>{{ item.aiSummary }}</p>
            </div>
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
.page-wrap { padding: 28px 36px; }
.page-title { font-size: 20px; font-weight: 700; color: var(--color-text-primary); margin: 0 0 20px; }
.order-grid { display: grid; grid-template-columns: repeat(auto-fill, minmax(310px, 1fr)); gap: 16px; min-height: 150px; }

.order-card {
  background: #fff; border: 1px solid rgba(0,0,0,0.04); border-radius: var(--radius-md);
  padding: 24px; transition: all 0.3s cubic-bezier(0.4,0,0.2,1); cursor: default;
  position: relative; overflow: hidden;
}
.order-card::before {
  content: ''; position: absolute; top: 0; left: 0; right: 0; height: 3px;
  background: linear-gradient(90deg, var(--color-primary), var(--color-primary-light));
  transform: scaleX(0); transform-origin: left;
  transition: transform 0.35s cubic-bezier(0.4,0,0.2,1);
}
.order-card:hover { border-color: rgba(122,154,126,0.18); box-shadow: var(--shadow-md); transform: translateY(-4px); }
.order-card:hover::before { transform: scaleX(1); }

.oc-header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 16px; }
.oc-id { font-size: 12px; color: var(--color-text-placeholder); font-weight: 550; letter-spacing: 0.03em; text-transform: uppercase; }
.oc-price { font-size: 24px; font-weight: 700; color: var(--color-primary-dark); letter-spacing: -0.02em; }
.oc-body { display: flex; flex-direction: column; gap: 10px; margin-bottom: 18px; }
.oc-row { font-size: 14px; color: var(--color-text-secondary); display: flex; align-items: center; gap: 8px; overflow: hidden; text-overflow: ellipsis; white-space: nowrap; }
.oc-row i { color: var(--color-primary); font-size: 15px; flex-shrink: 0; opacity: 0.7; }
.ai-summary { padding: 10px 12px; border-left: 3px solid var(--color-primary); background: var(--color-bg-page); color: var(--color-text-secondary); border-radius: var(--radius-sm); }
.ai-summary-title { display: flex; align-items: center; gap: 6px; color: var(--color-primary-dark); font-size: 12px; font-weight: 600; }
.ai-summary-title i { color: var(--color-primary); }
.ai-summary p { margin: 6px 0 0; line-height: 1.55; font-size: 13px; white-space: normal; }
.oc-footer { display: flex; justify-content: space-between; align-items: center; padding-top: 14px; border-top: 1px solid rgba(0,0,0,0.03); }

.empty-state { grid-column: 1 / -1; text-align: center; padding: 80px 0; color: var(--color-text-placeholder); }
.empty-state i { font-size: 48px; color: rgba(0,0,0,0.06); margin-bottom: 16px; display: block; }
</style>
