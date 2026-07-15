<template>
  <div class="page-wrap">
    <div class="page-head">
      <div>
        <h2 class="page-title">订单详情</h2>
        <p class="page-subtitle">订单号：{{ order.id || '-' }}</p>
      </div>
      <el-button icon="el-icon-back" size="medium" round @click="$router.back()">返回</el-button>
    </div>

    <el-card v-loading="loading" class="detail-card" shadow="never">
      <div class="status-line">
        <span :class="['status-tag', statusClass(order.status)]">{{ statusMap[order.status] || order.status || '-' }}</span>
        <span class="muted">创建时间：{{ fmt(order.createdAt || order.created_at) }}</span>
      </div>

      <el-descriptions :column="2" border>
        <el-descriptions-item label="客户">{{ order.customerName || order.customer_name || '-' }}</el-descriptions-item>
        <el-descriptions-item label="陪诊师">{{ order.companionName || order.companion_name || '-' }}</el-descriptions-item>
        <el-descriptions-item label="服务时间">{{ serviceTime(order) }}</el-descriptions-item>
        <el-descriptions-item label="价格">{{ money(order.servicePrice || order.service_price || order.price) }}</el-descriptions-item>
        <el-descriptions-item label="需求编号">{{ order.requestId || order.request_id || '-' }}</el-descriptions-item>
        <el-descriptions-item label="接单时间">{{ fmt(order.acceptedAt || order.accepted_at) }}</el-descriptions-item>
        <el-descriptions-item label="开始时间">{{ fmt(order.startedAt || order.started_at) }}</el-descriptions-item>
        <el-descriptions-item label="完成时间">{{ fmt(order.completedAt || order.completed_at) }}</el-descriptions-item>
        <el-descriptions-item label="取消原因" :span="2">{{ order.cancelReason || order.cancel_reason || '-' }}</el-descriptions-item>
      </el-descriptions>

      <div class="actions">
        <el-button type="primary" plain round @click="$router.push(`/order/${order.id}/service-record`)">查看服务记录</el-button>
      </div>
    </el-card>
  </div>
</template>

<script>
import { getOrderDetail } from '@/api/order'

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
  name: 'OrderDetail',
  data() {
    return {
      loading: false,
      order: {},
      statusMap: STATUS_MAP
    }
  },
  created() {
    this.fetchDetail()
  },
  methods: {
    async fetchDetail() {
      this.loading = true
      try {
        const res = await getOrderDetail(this.$route.params.orderId)
        this.order = res.data || res || {}
      } catch (error) {
        this.order = {}
      } finally {
        this.loading = false
      }
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
    }
  }
}
</script>

<style scoped>
.page-wrap { padding: 24px 32px; }
.page-head { display: flex; align-items: center; justify-content: space-between; margin-bottom: 16px; }
.page-title { font-family: var(--font-family); font-size: 20px; font-weight: 700; color: var(--color-text-primary); margin: 0; }
.page-subtitle { margin: 6px 0 0; font-size: 13px; color: var(--color-text-secondary); }
.detail-card { border: 1px solid var(--color-border-light); border-radius: var(--radius-md); }
.status-line { display: flex; align-items: center; gap: 14px; margin-bottom: 18px; }
.muted { color: var(--color-text-secondary); font-size: 13px; }
.actions { display: flex; justify-content: flex-end; margin-top: 20px; }
.status-tag { display: inline-block; padding: 2px 10px; border-radius: 20px; font-size: 12px; font-weight: 500; }
.s--pending { background: rgba(230,162,60,.1); color: #B88230; }
.s--active { background: rgba(122,154,126,.12); color: #5C7A60; }
.s--warn { background: rgba(224,96,96,.08); color: #C05050; }
.s--done { background: rgba(100,130,180,.1); color: #5577AA; }
.s--cancel { background: rgba(153,153,153,.1); color: #888; }
</style>
