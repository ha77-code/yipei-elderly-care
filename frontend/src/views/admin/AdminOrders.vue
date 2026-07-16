<template>
  <div class="page-wrap">
    <h2 class="page-title">订单管理</h2>
    <div class="filter-bar">
      <div class="filter-left">
        <el-select v-model="filterStatus" placeholder="全部状态" clearable size="medium" @change="handleFilter">
          <el-option v-for="(v,k) in statusMap" :key="k" :label="v" :value="k" />
        </el-select>
      </div>
      <el-button icon="el-icon-refresh" size="medium" round @click="resetFilter">重置</el-button>
    </div>
    <div class="content-card">
      <el-table :data="list" v-loading="loading" stripe class="warm-table" empty-text="暂无订单">
        <el-table-column prop="id" label="订单号" width="90" align="center" />
        <el-table-column prop="customerName" label="客户" width="100" />
        <el-table-column prop="companionName" label="陪诊师" width="100" />
        <el-table-column prop="servicePrice" label="金额" width="90" align="right">
          <template slot-scope="{ row }">¥{{ row.servicePrice || '-' }}</template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="100" align="center">
          <template slot-scope="{ row }">
            <span :class="['status-tag', statusClass(row.status)]">{{ statusMap[row.status] || row.status }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="createdAt" label="创建时间" width="120" align="center">
          <template slot-scope="{ row }">{{ fmt(row.createdAt || row.created_at) }}</template>
        </el-table-column>
        <el-table-column label="操作" width="100" align="center">
          <template slot-scope="{ row }">
            <el-button type="text" size="small" @click="goServiceRecord(row)">服务记录</el-button>
          </template>
        </el-table-column>
      </el-table>
      <div class="table-footer" v-if="total > pageSize">
        <el-pagination background layout="prev, pager, next, total" :total="total" :page-size="pageSize" :current-page.sync="currentPage" @current-change="fetchList" />
      </div>
    </div>
  </div>
</template>

<script>
import { getAllOrders } from '@/api/admin'

const STATUS_MAP = {
  PENDING_ACCEPT: '待接单', ACCEPTED: '已接单', IN_SERVICE: '服务中',
  PENDING_CONFIRM: '待确认', COMPLETED: '已完成', CANCELLED: '已取消', REJECTED: '已拒绝', COMPLAINT: '投诉中'
}

export default {
  name: 'AdminOrders',
  data() { return { list: [], loading: false, currentPage: 1, pageSize: 10, total: 0, filterStatus: '', statusMap: STATUS_MAP } },
  created() { this.fetchList() },
  methods: {
    async fetchList() {
      this.loading = true
      try { const params = { page: this.currentPage, size: this.pageSize }; if (this.filterStatus) params.status = this.filterStatus; const res = await getAllOrders(params); const d = res.data || res; this.list = d.records || d.list || d || []; this.total = d.total || this.list.length } catch { this.list = [] } finally { this.loading = false }
    },
    handleFilter() { this.currentPage = 1; this.fetchList() },
    resetFilter() { this.filterStatus = ''; this.currentPage = 1; this.fetchList() },
    statusClass(s) { const map = { PENDING_ACCEPT: 's--pending', ACCEPTED: 's--active', IN_SERVICE: 's--active', PENDING_CONFIRM: 's--warn', COMPLETED: 's--done', CANCELLED: 's--cancel', REJECTED: 's--cancel', COMPLAINT: 's--warn' }; return map[s] || '' },
    goServiceRecord(row) { this.$router.push(`/order/${row.id}/service-record`) },
    fmt(d) { if (!d) return '-'; return d.replace('T', ' ').substring(0, 16) }
  }
}
</script>

<style scoped>
.page-wrap { padding: 24px 32px; }
.page-title { font-family: var(--font-family); font-size: 20px; font-weight: 700; color: var(--color-text-primary); margin: 0 0 20px; }
.filter-bar { display: flex; align-items: center; justify-content: space-between; margin-bottom: 16px; padding: 16px 20px; background: #FFF; border: 1px solid var(--color-border-light); border-radius: var(--radius-md); box-shadow: var(--shadow-sm); }
.filter-left { display: flex; gap: 12px; }
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
