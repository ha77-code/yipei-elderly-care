<template>
  <div class="page-wrap">
    <div class="page-header">
      <h2 class="page-title">我的订单</h2>
      <span class="page-count" v-if="list.length">共 {{ list.length }} 条</span>
    </div>
    <div class="content-card">
      <el-table :data="list" v-loading="loading" stripe class="warm-table" empty-text="暂无订单" @row-click="goDetail">
        <el-table-column prop="id" label="订单号" width="90" align="center" />
        <el-table-column prop="companionName" label="陪诊师" min-width="110">
          <template slot-scope="{ row }"><span class="name-cell">{{ row.companionName || '-' }}</span></template>
        </el-table-column>
        <el-table-column prop="servicePrice" label="金额" width="100" align="right">
          <template slot-scope="{ row }"><span class="price-cell">¥{{ row.servicePrice || '-' }}</span></template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="110" align="center">
          <template slot-scope="{ row }"><span :class="['status-tag', statusClass(row.status)]">{{ statusMap[row.status] || row.status }}</span></template>
        </el-table-column>
        <el-table-column prop="createdAt" label="创建时间" width="130" align="center">
          <template slot-scope="{ row }">{{ fmt(row.createdAt || row.created_at) }}</template>
        </el-table-column>
        <el-table-column label="操作" width="180" align="center" fixed="right">
          <template slot-scope="{ row }">
            <el-button v-if="row.status === 'PENDING_CONFIRM'" type="success" size="mini" round @click.stop="handleConfirm(row)">确认完成</el-button>
            <el-button v-if="canCancel(row.status)" type="danger" size="mini" round plain @click.stop="handleCancel(row)">取消</el-button>
            <el-button type="text" size="small" @click.stop="goDetail(row)">详情</el-button>
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
import { getMyOrders, confirmOrder, cancelOrder } from '@/api/order'
import { getUser } from '@/utils/auth'

const STATUS_MAP = { PENDING_ACCEPT: '待接单', ACCEPTED: '已接单', IN_SERVICE: '服务中', PENDING_CONFIRM: '待确认', COMPLETED: '已完成', CANCELLED: '已取消', REJECTED: '已拒绝', COMPLAINT: '投诉中' }

export default {
  name: 'CustomerOrders',
  data() { return { list: [], loading: false, currentPage: 1, pageSize: 10, total: 0, statusMap: STATUS_MAP } },
  created() { this.fetchList() },
  methods: {
    async fetchList() {
      this.loading = true
      try { const u = getUser(); const res = await getMyOrders({ userId: u.id, role: 'CUSTOMER' }); const d = res.data || res; this.list = d.records || d.list || d || []; this.total = d.total || this.list.length } catch { this.list = [] } finally { this.loading = false }
    },
    statusClass(s) { const m = { PENDING_ACCEPT: 's--pending', ACCEPTED: 's--active', IN_SERVICE: 's--active', PENDING_CONFIRM: 's--warn', COMPLETED: 's--done', CANCELLED: 's--cancel', REJECTED: 's--cancel', COMPLAINT: 's--warn' }; return m[s] || '' },
    canCancel(s) { return s === 'PENDING_ACCEPT' || s === 'ACCEPTED' },
    handleConfirm(row) {
      this.$confirm('确认该订单已完成服务？', '确认完成', { type: 'success' }).then(async () => { try { await confirmOrder(row.id); this.$message.success('已确认完成'); this.fetchList() } catch {} }).catch(() => {})
    },
    handleCancel(row) {
      this.$prompt('请输入取消原因', '取消订单', { inputType: 'textarea' }).then(async ({ value }) => { try { await cancelOrder(row.id, { cancelReason: value }); this.$message.success('已取消'); this.fetchList() } catch {} }).catch(() => {})
    },
    goDetail(row) { this.$router.push(`/order/${row.id}`) },
    fmt(d) { if (!d) return '-'; return String(d).replace('T', ' ').substring(0, 16) }
  }
}
</script>

<style scoped>
.page-wrap { padding: 28px 36px; }
.page-header { display: flex; align-items: baseline; gap: 12px; margin-bottom: 20px; }
.page-title { font-size: 20px; font-weight: 700; color: var(--color-text-primary); margin: 0; }
.page-count { font-size: 13px; color: var(--color-text-placeholder); }
.content-card { background: #fff; border: 1px solid rgba(0,0,0,0.04); border-radius: var(--radius-md); padding: 4px; box-shadow: var(--shadow-xs); }
.warm-table { width: 100%; cursor: pointer; }
.warm-table ::v-deep tr { transition: background 0.15s ease; }
.name-cell { font-weight: 550; }
.price-cell { font-weight: 650; color: var(--color-primary-dark); }
.table-footer { display: flex; justify-content: flex-end; padding: 14px 4px 6px; }
.status-tag { display: inline-block; padding: 2px 10px; border-radius: 20px; font-size: 12px; font-weight: 550; }
.s--pending { background: rgba(212,168,110,0.1); color: #B88230; }
.s--active { background: rgba(122,154,126,0.12); color: #5C7A60; }
.s--warn { background: rgba(212,120,110,0.08); color: #C05050; }
.s--done { background: rgba(100,130,180,0.1); color: #5577AA; }
.s--cancel { background: rgba(0,0,0,0.04); color: #999; }
</style>
