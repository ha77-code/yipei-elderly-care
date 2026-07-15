<template>
  <div class="page-wrap">
    <h2 class="page-title">我的订单</h2>
    <div class="content-card">
      <el-table :data="list" v-loading="loading" stripe class="warm-table" empty-text="暂无订单">
        <el-table-column prop="id" label="订单号" width="90" align="center" />
        <el-table-column prop="customerName" label="客户" min-width="100" />
        <el-table-column prop="servicePrice" label="金额" width="100" align="right">
          <template slot-scope="{ row }">¥{{ row.servicePrice || '-' }}</template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="100" align="center">
          <template slot-scope="{ row }">
            <span :class="['status-tag', statusClass(row.status)]">{{ statusMap[row.status] || row.status }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="acceptedAt" label="接单时间" width="120" align="center">
          <template slot-scope="{ row }">{{ fmt(row.acceptedAt || row.accepted_at) }}</template>
        </el-table-column>
        <el-table-column label="操作" width="220" align="center" fixed="right">
          <template slot-scope="{ row }">
            <el-button v-if="row.status === 'ACCEPTED'" type="primary" size="small" round @click="handleAction(row, 'start')">开始服务</el-button>
            <el-button v-if="row.status === 'IN_SERVICE'" type="success" size="small" round @click="handleAction(row, 'complete')">完成服务</el-button>
            <el-button type="text" size="small" @click="goServiceRecord(row)">服务记录</el-button>
          </template>
        </el-table-column>
      </el-table>
      <div class="table-footer" v-if="total > pageSize">
        <el-pagination background layout="prev, pager, next" :total="total" :page-size="pageSize" :current-page.sync="currentPage" @current-change="fetchList" />
      </div>
    </div>
  </div>
</template>

<script>
import { getMyOrders, startService, completeService } from '@/api/order'

const STATUS_MAP = {
  PENDING_ACCEPT: '待接单', ACCEPTED: '已接单', IN_SERVICE: '服务中',
  PENDING_CONFIRM: '待确认', COMPLETED: '已完成', CANCELLED: '已取消', REJECTED: '已拒绝', COMPLAINT: '投诉中'
}

export default {
  name: 'CompanionOrders',
  data() { return { list: [], loading: false, currentPage: 1, pageSize: 10, total: 0, statusMap: STATUS_MAP } },
  created() { this.fetchList() },
  methods: {
    async fetchList() {
      this.loading = true
      try { const res = await getMyOrders({ page: this.currentPage, size: this.pageSize }); const d = res.data || res; this.list = d.records || d.list || d || []; this.total = d.total || this.list.length } catch { this.list = [] } finally { this.loading = false }
    },
    statusClass(s) { const map = { PENDING_ACCEPT: 's--pending', ACCEPTED: 's--active', IN_SERVICE: 's--active', PENDING_CONFIRM: 's--warn', COMPLETED: 's--done', CANCELLED: 's--cancel', REJECTED: 's--cancel', COMPLAINT: 's--warn' }; return map[s] || '' },
    handleAction(row, action) {
      const labels = { start: { title: '开始服务', msg: '确认开始该订单服务？', fn: startService, ok: '服务已开始' }, complete: { title: '完成服务', msg: '确认完成该订单服务？', fn: completeService, ok: '服务已完成，等待客户确认' } }
      const cfg = labels[action]
      this.$confirm(cfg.msg, cfg.title, { type: 'success' }).then(async () => { try { await cfg.fn(row.id); this.$message.success(cfg.ok); this.fetchList() } catch {} }).catch(() => {})
    },
    goServiceRecord(row) { this.$router.push(`/order/${row.id}/service-record`) },
    fmt(d) { if (!d) return '-'; return d.replace('T', ' ').substring(0, 16) }
  }
}
</script>

<style scoped>
.page-wrap { padding: 24px 32px; }
.page-title { font-family: var(--font-family); font-size: 20px; font-weight: 700; color: var(--color-text-primary); margin: 0 0 20px; }
.content-card { background: #FFF; border: 1px solid var(--color-border-light); border-radius: var(--radius-md); padding: 8px; box-shadow: var(--shadow-sm); }
.warm-table { width: 100%; }
.warm-table::v-deep th { background: var(--color-bg-page); color: var(--color-text-regular); font-weight: 600; font-size: 13px; }
.warm-table::v-deep td { font-size: 14px; }
.table-footer { display: flex; justify-content: flex-end; padding: 16px 0 8px; }
.status-tag { display: inline-block; padding: 2px 10px; border-radius: 20px; font-size: 12px; font-weight: 500; }
.s--pending { background: rgba(230,162,60,.1); color: #B88230; }
.s--active { background: rgba(122,154,126,.12); color: #5C7A60; }
.s--warn { background: rgba(224,96,96,.08); color: #C05050; }
.s--done { background: rgba(100,130,180,.1); color: #5577AA; }
.s--cancel { background: rgba(153,153,153,.1); color: #888; }
</style>
