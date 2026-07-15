<template>
  <div class="page-wrap">
    <h2 class="page-title">服务记录</h2>
    <div class="content-card">
      <el-table :data="list" v-loading="loading" stripe class="warm-table" empty-text="暂无服务记录">
        <el-table-column prop="id" label="编号" width="80" align="center" />
        <el-table-column prop="orderId" label="订单号" width="90" align="center" />
        <el-table-column prop="content" label="服务内容" min-width="220" show-overflow-tooltip />
        <el-table-column prop="importantNotes" label="重要事项" min-width="160" show-overflow-tooltip>
          <template slot-scope="{ row }">{{ row.importantNotes || '-' }}</template>
        </el-table-column>
        <el-table-column prop="createdAt" label="填写时间" width="120" align="center">
          <template slot-scope="{ row }">{{ fmt(row.createdAt || row.created_at) }}</template>
        </el-table-column>
        <el-table-column label="操作" width="100" align="center">
          <template slot-scope="{ row }">
            <el-button type="text" size="small" @click="goDetail(row)">查看详情</el-button>
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
import { getMyServiceRecords } from '@/api/serviceRecord'

export default {
  name: 'ServiceRecords',
  data() { return { list: [], loading: false, currentPage: 1, pageSize: 10, total: 0 } },
  created() { this.fetchList() },
  methods: {
    async fetchList() {
      this.loading = true
      try { const res = await getMyServiceRecords({ page: this.currentPage, size: this.pageSize }); const d = res.data || res; this.list = d.records || d.list || d || []; this.total = d.total || this.list.length } catch { this.list = [] } finally { this.loading = false }
    },
    goDetail(row) { this.$router.push(`/order/${row.orderId}/service-record`) },
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
</style>
