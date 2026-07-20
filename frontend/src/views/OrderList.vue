<template>
  <div class="page-wrap">
    <div class="page-head">
      <div>
        <h2 class="page-title">{{ pageTitle }}</h2>
        <p class="page-subtitle">查看订单状态、服务时间、陪诊师和价格</p>
      </div>
      <el-button icon="el-icon-refresh" size="medium" round @click="resetList">刷新</el-button>
    </div>

    <div v-if="isAdmin" class="filter-bar">
      <el-select v-model="filterStatus" placeholder="全部状态" clearable size="medium" @change="handleFilter">
        <el-option v-for="item in statusOptions" :key="item.value" :label="item.label" :value="item.value" />
      </el-select>
    </div>

    <div class="content-card">
      <el-table :data="list" v-loading="loading" stripe class="warm-table" empty-text="暂无订单">
        <el-table-column prop="id" label="订单号" width="90" align="center" />
        <el-table-column v-if="isAdmin || isCompanion" label="客户" min-width="100">
          <template slot-scope="{ row }">{{ row.customerName || row.customer_name || '-' }}</template>
        </el-table-column>
        <el-table-column label="服务时间" min-width="150">
          <template slot-scope="{ row }">{{ serviceTime(row) }}</template>
        </el-table-column>
        <el-table-column v-if="isCompanion" label="AI需求摘要" min-width="240" show-overflow-tooltip>
          <template slot-scope="{ row }">{{ row.aiSummary || '-' }}</template>
        </el-table-column>
        <el-table-column label="陪诊师" min-width="110">
          <template slot-scope="{ row }">{{ row.companionName || row.companion_name || '-' }}</template>
        </el-table-column>
        <el-table-column label="价格" width="110" align="right">
          <template slot-scope="{ row }">{{ money(row.servicePrice || row.service_price || row.price) }}</template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="110" align="center">
          <template slot-scope="{ row }">
            <span :class="['status-tag', statusClass(row.status)]">{{ statusMap[row.status] || row.status || '-' }}</span>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="260" align="center" fixed="right">
          <template slot-scope="{ row }">
            <el-button type="text" size="small" @click="goDetail(row)">详情</el-button>
            <el-button v-if="row.status === 'PENDING_CONFIRM' && isCustomer" type="text" size="small" @click="handleConfirm(row)">确认完成</el-button>
            <el-button v-if="canCancel(row.status) && isCustomer" type="text" size="small" class="text-danger" @click="handleCancel(row)">取消</el-button>
            <el-button v-if="row.status === 'ACCEPTED' && isCompanion" type="text" size="small" @click="handleServiceAction(row, 'start')">开始服务</el-button>
            <el-button v-if="row.status === 'IN_SERVICE' && isCompanion" type="text" size="small" @click="handleServiceAction(row, 'complete')">完成服务</el-button>
            <el-button type="text" size="small" @click="goServiceRecord(row)">服务记录</el-button>
          </template>
        </el-table-column>
      </el-table>

      <div class="table-footer" v-if="total > pageSize">
        <el-pagination
          background
          layout="prev, pager, next, total"
          :total="total"
          :page-size="pageSize"
          :current-page.sync="currentPage"
          @current-change="fetchList"
        />
      </div>
    </div>
  </div>
</template>

<script>
import {
  getMyOrders,
  getAllOrders,
  confirmOrder,
  cancelOrder,
  startService,
  completeService
} from '@/api/order'
import { getUser, ROLES } from '@/utils/auth'

const STATUS_MAP = {
  PENDING_ACCEPT: '待接单',
  ACCEPTED: '已接单',
  IN_SERVICE: '服务中',
  PENDING_CONFIRM: '待确认',
  COMPLETED: '已完成',
  CANCELLED: '已取消',
  REJECTED: '已拒绝',
  COMPLAINT: '投诉中'
}

export default {
  name: 'OrderList',
  data() {
    const user = getUser() || {}
    return {
      role: user.role || ROLES.CUSTOMER,
      list: [],
      loading: false,
      currentPage: 1,
      pageSize: 10,
      total: 0,
      filterStatus: '',
      statusMap: STATUS_MAP,
      statusOptions: Object.keys(STATUS_MAP).map(key => ({ value: key, label: STATUS_MAP[key] }))
    }
  },
  computed: {
    isAdmin() {
      return this.role === ROLES.ADMIN
    },
    isCustomer() {
      return this.role === ROLES.CUSTOMER
    },
    isCompanion() {
      return this.role === ROLES.COMPANION
    },
    pageTitle() {
      if (this.isAdmin) return '订单管理'
      return '我的订单'
    }
  },
  created() {
    this.fetchList()
  },
  methods: {
    async fetchList() {
      this.loading = true
      try {
        const params = { page: this.currentPage, size: this.pageSize }
        if (this.isAdmin && this.filterStatus) params.status = this.filterStatus
        const res = this.isAdmin ? await getAllOrders(params) : await getMyOrders(params)
        const data = res.data || res
        this.list = data.records || data.list || data.rows || data || []
        this.total = data.total || this.list.length
      } catch (error) {
        this.list = []
        this.total = 0
      } finally {
        this.loading = false
      }
    },
    handleFilter() {
      this.currentPage = 1
      this.fetchList()
    },
    resetList() {
      this.filterStatus = ''
      this.currentPage = 1
      this.fetchList()
    },
    statusClass(status) {
      const map = {
        PENDING_ACCEPT: 's--pending',
        ACCEPTED: 's--active',
        IN_SERVICE: 's--active',
        PENDING_CONFIRM: 's--warn',
        COMPLETED: 's--done',
        CANCELLED: 's--cancel',
        REJECTED: 's--cancel',
        COMPLAINT: 's--warn'
      }
      return map[status] || ''
    },
    serviceTime(row) {
      return this.fmt(
        row.serviceTime ||
        row.service_time ||
        row.serviceDate ||
        row.service_date ||
        row.requestServiceDate ||
        row.request_service_date ||
        (row.request && (row.request.serviceDate || row.request.service_date)) ||
        row.startedAt ||
        row.started_at ||
        row.createdAt ||
        row.created_at
      )
    },
    money(value) {
      if (value === null || value === undefined || value === '') return '-'
      return `￥${Number(value).toFixed(2)}`
    },
    fmt(value) {
      if (!value) return '-'
      return String(value).replace('T', ' ').substring(0, 16)
    },
    canCancel(status) {
      return ['PENDING_ACCEPT', 'ACCEPTED'].includes(status)
    },
    goDetail(row) {
      this.$router.push(`/order/${row.id}`)
    },
    goServiceRecord(row) {
      this.$router.push(`/order/${row.id}/service-record`)
    },
    handleConfirm(row) {
      this.$confirm('确认该订单已完成服务？', '确认完成', { type: 'success' })
        .then(async () => {
          await confirmOrder(row.id)
          this.$message.success('已确认完成')
          this.fetchList()
        })
        .catch(() => {})
    },
    handleCancel(row) {
      this.$prompt('请输入取消原因', '取消订单', {
        inputType: 'textarea',
        inputPlaceholder: '取消原因...'
      }).then(async ({ value }) => {
        await cancelOrder(row.id, { cancelReason: value })
        this.$message.success('已取消')
        this.fetchList()
      }).catch(() => {})
    },
    handleServiceAction(row, action) {
      const config = {
        start: { title: '开始服务', message: '确认开始该订单服务？', handler: startService, success: '服务已开始' },
        complete: { title: '完成服务', message: '确认完成该订单服务？', handler: completeService, success: '服务已完成，等待客户确认' }
      }[action]
      this.$confirm(config.message, config.title, { type: 'success' })
        .then(async () => {
          await config.handler(row.id)
          this.$message.success(config.success)
          this.fetchList()
        })
        .catch(() => {})
    }
  }
}
</script>

<style scoped>
.page-wrap { padding: 24px 32px; }
.page-head { display: flex; align-items: center; justify-content: space-between; margin-bottom: 16px; }
.page-title { font-family: var(--font-family); font-size: 20px; font-weight: 700; color: var(--brand-cream-100); margin: 0; }
.page-subtitle { margin: 6px 0 0; font-size: 13px; color: var(--color-text-secondary); }
.filter-bar { display: flex; align-items: center; margin-bottom: 16px; padding: 16px 20px; background: #FFF; border: 1px solid var(--color-border-light); border-radius: var(--radius-md); box-shadow: var(--shadow-sm); }
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
.text-danger { color: #E06060 !important; }
</style>
