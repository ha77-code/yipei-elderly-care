<template>
  <div class="page-wrap">
    <h2 class="page-title">投诉处理</h2>
    <div class="filter-bar">
      <div class="filter-left">
        <el-select v-model="filterStatus" placeholder="全部状态" clearable size="medium" @change="handleFilter">
          <el-option label="待处理" value="PENDING" />
          <el-option label="处理中" value="PROCESSING" />
          <el-option label="已处理" value="RESOLVED" />
          <el-option label="已驳回" value="REJECTED" />
        </el-select>
      </div>
      <el-button icon="el-icon-refresh" size="medium" round @click="resetFilter">重置</el-button>
    </div>
    <div class="content-card">
      <el-table :data="list" v-loading="loading" stripe class="warm-table" empty-text="暂无投诉">
        <el-table-column prop="id" label="编号" width="80" align="center" />
        <el-table-column prop="orderId" label="订单号" width="90" align="center" />
        <el-table-column prop="reporterName" label="投诉人" min-width="100" />
        <el-table-column prop="reason" label="投诉原因" min-width="120" show-overflow-tooltip />
        <el-table-column prop="status" label="状态" width="90" align="center">
          <template slot-scope="{ row }">
            <span :class="['status-tag', statusClass(row.status)]">{{ statusMap[row.status] || row.status }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="createdAt" label="投诉时间" width="120" align="center">
          <template slot-scope="{ row }">{{ fmt(row.createdAt || row.created_at) }}</template>
        </el-table-column>
        <el-table-column label="操作" width="180" align="center" fixed="right">
          <template slot-scope="{ row }">
            <el-button type="text" size="small" @click="showDetail(row)">详情</el-button>
            <el-button v-if="row.status === 'PENDING'" type="primary" size="small" round @click="handleAction(row, 'PROCESSING')">处理</el-button>
            <el-button v-if="row.status === 'PROCESSING'" type="success" size="small" round @click="handleAction(row, 'RESOLVED')">解决</el-button>
          </template>
        </el-table-column>
      </el-table>
      <div class="table-footer" v-if="total > pageSize">
        <el-pagination background layout="prev, pager, next, total" :total="total" :page-size="pageSize" :current-page.sync="currentPage" @current-change="fetchList" />
      </div>
    </div>

    <!-- 详情弹窗 -->
    <el-dialog title="投诉详情" :visible.sync="detailVisible" width="520px" class="warm-dialog">
      <div class="detail-grid" v-if="current">
        <div class="d-item"><span class="d-label">投诉人</span><span class="d-val">{{ current.reporterName || current.reporterId }}</span></div>
        <div class="d-item"><span class="d-label">订单号</span><span class="d-val">#{{ current.orderId }}</span></div>
        <div class="d-item"><span class="d-label">原因</span><span class="d-val">{{ current.reason }}</span></div>
        <div class="d-item"><span class="d-label">状态</span><span class="d-val"><span :class="['status-tag', statusClass(current.status)]">{{ statusMap[current.status] }}</span></span></div>
        <div class="d-item d-full"><span class="d-label">投诉内容</span><span class="d-val d-content">{{ current.content }}</span></div>
        <div class="d-item d-full" v-if="current.handleRemark"><span class="d-label">处理备注</span><span class="d-val d-content">{{ current.handleRemark }}</span></div>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { getAllReports, handleReport } from '@/api/report'

const STATUS_MAP = { PENDING: '待处理', PROCESSING: '处理中', RESOLVED: '已处理', REJECTED: '已驳回' }

export default {
  name: 'AdminReports',
  data() { return { list: [], loading: false, currentPage: 1, pageSize: 10, total: 0, filterStatus: '', statusMap: STATUS_MAP, detailVisible: false, current: null } },
  created() { this.fetchList() },
  methods: {
    async fetchList() {
      this.loading = true
      try { const params = { page: this.currentPage, size: this.pageSize }; if (this.filterStatus) params.status = this.filterStatus; const res = await getAllReports(params); const d = res.data || res; this.list = d.records || d.list || d || []; this.total = d.total || this.list.length } catch { this.list = [] } finally { this.loading = false }
    },
    handleFilter() { this.currentPage = 1; this.fetchList() },
    resetFilter() { this.filterStatus = ''; this.currentPage = 1; this.fetchList() },
    statusClass(s) { const map = { PENDING: 's--pending', PROCESSING: 's--active', RESOLVED: 's--done', REJECTED: 's--cancel' }; return map[s] || '' },
    showDetail(row) { this.current = row; this.detailVisible = true },
    handleAction(row, status) {
      const label = status === 'PROCESSING' ? '开始处理' : '标记解决'
      this.$prompt(`处理备注（选填）`, label, { inputType: 'textarea' }).then(async ({ value }) => { try { await handleReport(row.id, { status, handleRemark: value }); this.$message.success('操作成功'); this.fetchList() } catch {} }).catch(() => {})
    },
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
.s--done { background: rgba(100,130,180,.1); color: #5577AA; }
.s--cancel { background: rgba(153,153,153,.1); color: #888; }
.detail-grid { display: grid; grid-template-columns: 1fr 1fr; gap: 14px 20px; }
.d-item { display: flex; flex-direction: column; gap: 4px; }
.d-full { grid-column: 1 / -1; }
.d-label { font-size: 13px; color: var(--color-text-placeholder); font-weight: 500; }
.d-val { font-size: 15px; color: var(--color-text-primary); }
.d-content { background: var(--color-bg-page); padding: 12px 16px; border-radius: var(--radius-sm); line-height: 1.6; }
</style>
