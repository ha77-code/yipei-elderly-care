<template>
  <div class="page-wrap" v-loading="loading">
    <div class="page-head">
      <div>
        <h2 class="page-title">订单详情 #{{ order.id }}</h2>
        <p class="page-subtitle">
          <span :class="['status-tag', statusClass(order.status)]">{{ statusMap[order.status] || order.status }}</span>
          <span class="muted">创建于 {{ fmt(order.createdAt) }}</span>
        </p>
      </div>
      <el-button icon="el-icon-back" size="small" round @click="$router.back()">返回</el-button>
    </div>

    <!-- 操作按钮 -->
    <div class="action-bar" v-if="actions.length">
      <el-button
        v-for="act in actions" :key="act.key"
        :type="act.type" :class="act.class" size="small" round
        :loading="acting === act.key"
        @click="doAction(act)"
      >{{ act.label }}</el-button>
    </div>

    <div class="detail-grid">
      <!-- 左侧主信息 -->
      <div class="detail-main">
        <!-- 基本信息 -->
        <el-card shadow="never" class="info-card">
          <div slot="header"><span>基本信息</span></div>
          <el-descriptions :column="2" size="small" border>
            <el-descriptions-item label="客户">{{ order.customerName || '-' }}</el-descriptions-item>
            <el-descriptions-item label="陪诊师">{{ order.companionName || '-' }}</el-descriptions-item>
            <el-descriptions-item label="服务价格">{{ money(order.servicePrice) }}</el-descriptions-item>
            <el-descriptions-item label="平台抽成">{{ money(order.platformFee) }}</el-descriptions-item>
            <el-descriptions-item label="陪诊师收入">{{ money(order.companionIncome) }}</el-descriptions-item>
            <el-descriptions-item label="需求编号">#{{ order.requestId }}</el-descriptions-item>
            <el-descriptions-item label="接单时间">{{ fmt(order.acceptedAt) }}</el-descriptions-item>
            <el-descriptions-item label="开始时间">{{ fmt(order.startedAt) }}</el-descriptions-item>
            <el-descriptions-item label="完成时间">{{ fmt(order.completedAt) }}</el-descriptions-item>
            <el-descriptions-item label="取消原因" :span="2">{{ order.cancelReason || '-' }}</el-descriptions-item>
          </el-descriptions>
        </el-card>

        <!-- 需求信息 -->
        <el-card shadow="never" class="info-card" v-if="order.serviceType || order.hospitalName">
          <div slot="header"><span>需求信息</span></div>
          <el-descriptions :column="2" size="small" border>
            <el-descriptions-item label="服务类型">{{ order.serviceType || '-' }}</el-descriptions-item>
            <el-descriptions-item label="医院">{{ order.hospitalName || '-' }}</el-descriptions-item>
            <el-descriptions-item label="科室">{{ order.department || '-' }}</el-descriptions-item>
            <el-descriptions-item label="服务时间">{{ fmt(order.serviceDate) }}</el-descriptions-item>
          </el-descriptions>
        </el-card>

        <!-- 服务记录 -->
        <el-card shadow="never" class="info-card" v-if="serviceRecord">
          <div slot="header"><span>服务记录</span></div>
          <div class="record-content">{{ serviceRecord.content }}</div>
          <div class="record-notes" v-if="serviceRecord.importantNotes">
            <span class="notes-label">重要事项：</span>{{ serviceRecord.importantNotes }}
          </div>
        </el-card>

        <!-- 评价 -->
        <el-card shadow="never" class="info-card">
          <div slot="header"><span>评价 ({{ evaluations.length }})</span></div>
          <div v-if="evaluations.length === 0" class="empty-text">暂无评价</div>
          <div v-for="ev in evaluations" :key="ev.id" class="eval-item">
            <div class="eval-header">
              <span class="eval-score">{{ '★'.repeat(ev.score) }}{{ '☆'.repeat(5 - ev.score) }}</span>
              <span class="eval-user">评价人 ID: {{ ev.fromUserId }}</span>
              <span class="eval-time">{{ fmt(ev.createdAt) }}</span>
            </div>
            <div class="eval-content" v-if="ev.content">{{ ev.content }}</div>
          </div>
        </el-card>
      </div>

      <!-- 右侧状态时间线 -->
      <div class="detail-side">
        <el-card shadow="never" class="timeline-card">
          <div slot="header"><span>状态流转</span></div>
          <el-timeline v-if="statusLogs.length">
            <el-timeline-item
              v-for="log in statusLogs" :key="log.id"
              :timestamp="fmt(log.createdAt)"
              placement="top"
              :color="logColor(log)"
            >
              <div class="tl-content">
                <p class="tl-status">{{ log.fromStatus ? log.fromStatus + ' → ' : '' }}{{ log.toStatus }}</p>
                <p class="tl-remark">{{ log.remark || '-' }}</p>
                <p class="tl-operator">操作人 ID: {{ log.operatorId }}</p>
              </div>
            </el-timeline-item>
          </el-timeline>
          <div v-else class="empty-text">暂无状态记录</div>
        </el-card>
      </div>
    </div>

    <!-- 评价弹窗 -->
    <el-dialog title="评价" :visible.sync="evalVisible" width="420px" :close-on-click-modal="false">
      <el-form label-width="70px" size="small">
        <el-form-item label="评分">
          <el-rate v-model="evalForm.score" :max="5" show-score />
        </el-form-item>
        <el-form-item label="内容">
          <el-input v-model="evalForm.content" type="textarea" :rows="3" placeholder="请输入评价内容" />
        </el-form-item>
      </el-form>
      <span slot="footer">
        <el-button @click="evalVisible = false">取消</el-button>
        <el-button type="primary" :loading="acting === 'evaluate'" @click="submitEval">提交</el-button>
      </span>
    </el-dialog>

    <!-- 投诉弹窗 -->
    <el-dialog title="投诉" :visible.sync="reportVisible" width="420px" :close-on-click-modal="false">
      <el-form label-width="70px" size="small">
        <el-form-item label="原因">
          <el-select v-model="reportForm.reason" placeholder="请选择投诉原因" style="width:100%">
            <el-option label="服务态度差" value="服务态度差" />
            <el-option label="未按时到达" value="未按时到达" />
            <el-option label="服务未完成" value="服务未完成" />
            <el-option label="沟通不畅" value="沟通不畅" />
            <el-option label="其他" value="其他" />
          </el-select>
        </el-form-item>
        <el-form-item label="内容">
          <el-input v-model="reportForm.content" type="textarea" :rows="3" placeholder="请详细描述投诉内容" />
        </el-form-item>
      </el-form>
      <span slot="footer">
        <el-button @click="reportVisible = false">取消</el-button>
        <el-button type="danger" :loading="acting === 'report'" @click="submitReport">提交投诉</el-button>
      </span>
    </el-dialog>
  </div>
</template>

<script>
import { getOrderDetail, acceptOrder, rejectOrder, startService, completeService, cancelOrder } from '@/api/order'
import { getServiceRecordByOrder } from '@/api/serviceRecord'
import { getEvaluationByOrder, submitEvaluation } from '@/api/evaluation'
import { submitReport } from '@/api/report'
import { getUserRole, getUser } from '@/utils/auth'

const STATUS_MAP = {
  PENDING_ACCEPT: '待接单', ACCEPTED: '已接单', IN_SERVICE: '服务中',
  PENDING_CONFIRM: '待确认', COMPLETED: '已完成', CANCELLED: '已取消',
  REJECTED: '已拒绝', COMPLAINT: '投诉中'
}

export default {
  name: 'OrderDetail',
  data() {
    return {
      loading: false, acting: null, order: {}, serviceRecord: null,
      evaluations: [], statusLogs: [], statusMap: STATUS_MAP,
      evalVisible: false, evalForm: { score: 5, content: '' },
      reportVisible: false, reportForm: { reason: '', content: '' }
    }
  },
  computed: {
    role() { return getUserRole() },
    userId() { const u = getUser(); return u ? u.id : null },
    actions() {
      const s = this.order.status; const r = this.role
      const acts = []
      if (r === 'COMPANION' && s === 'PENDING_ACCEPT') {
        acts.push({ key: 'accept', label: '接单', type: 'primary', action: 'accept' })
        acts.push({ key: 'reject', label: '拒单', type: 'danger', action: 'reject', class: 'plain' })
      }
      if (r === 'COMPANION' && s === 'ACCEPTED')
        acts.push({ key: 'start', label: '开始服务', type: 'primary', action: 'start' })
      if (r === 'COMPANION' && s === 'IN_SERVICE')
        acts.push({ key: 'complete', label: '完成服务', type: 'success', action: 'complete' })
      if (r === 'CUSTOMER' && (s === 'PENDING_ACCEPT' || s === 'ACCEPTED'))
        acts.push({ key: 'cancel', label: '取消订单', type: 'danger', action: 'cancel', class: 'plain' })
      if (s === 'COMPLETED' && (r === 'CUSTOMER' || r === 'COMPANION'))
        acts.push({ key: 'evaluate', label: '评价', type: 'warning', action: 'evaluate' })
      if (r === 'CUSTOMER' || r === 'COMPANION')
        acts.push({ key: 'report', label: '投诉', type: 'danger', action: 'report', class: 'plain' })
      return acts
    }
  },
  created() { this.fetchAll() },
  methods: {
    async fetchAll() {
      this.loading = true
      const id = this.$route.params.orderId
      try {
        const res = await getOrderDetail(id)
        this.order = res.data || res || {}
        this.statusLogs = this.order.statusLogs || []
      } catch { this.order = {} }
      try {
        const r = await getServiceRecordByOrder(id)
        this.serviceRecord = r.data || r
      } catch { this.serviceRecord = null }
      try {
        const e = await getEvaluationByOrder(id)
        this.evaluations = e.data || e || []
      } catch { this.evaluations = [] }
      this.loading = false
    },
    async doAction(act) {
      const orderId = this.order.id
      if (act.action === 'evaluate') { this.evalVisible = true; return }
      if (act.action === 'report') { this.reportVisible = true; return }
      if (act.action === 'reject' || act.action === 'cancel') {
        const label = act.action === 'reject' ? '拒单原因' : '取消原因'
        try {
          const { value } = await this.$prompt(`请输入${label}`, label, { inputType: 'textarea' })
          this.acting = act.key
          if (act.action === 'reject') {
            await rejectOrder(orderId, { reason: value })
          } else {
            await cancelOrder(orderId, { cancelReason: value })
          }
          this.$message.success('操作成功')
          this.fetchAll()
        } catch { /* cancelled */ } finally { this.acting = null }
        return
      }
      this.acting = act.key
      try {
        if (act.action === 'accept') await acceptOrder(orderId)
        else if (act.action === 'start') await startService(orderId)
        else if (act.action === 'complete') await completeService(orderId)
        this.$message.success('操作成功')
        this.fetchAll()
      } catch { /* error handled */ } finally { this.acting = null }
    },
    async submitEval() {
      if (!this.evalForm.score) { this.$message.warning('请选择评分'); return }
      this.acting = 'evaluate'
      const toUserId = this.role === 'CUSTOMER' ? this.order.companionUserId : this.order.customerId
      try {
        await submitEvaluation({ orderId: this.order.id, toUserId: toUserId, score: this.evalForm.score, content: this.evalForm.content })
        this.$message.success('评价成功')
        this.evalVisible = false
        this.fetchAll()
      } catch {} finally { this.acting = null }
    },
    async submitReport() {
      if (!this.reportForm.reason || !this.reportForm.content) { this.$message.warning('请填写完整投诉信息'); return }
      this.acting = 'report'
      try {
        await submitReport({ orderId: this.order.id, reason: this.reportForm.reason, content: this.reportForm.content })
        this.$message.success('投诉已提交')
        this.reportVisible = false
      } catch {} finally { this.acting = null }
    },
    statusClass(s) { const m = { PENDING_ACCEPT: 's--pending', ACCEPTED: 's--active', IN_SERVICE: 's--active', PENDING_CONFIRM: 's--warn', COMPLETED: 's--done', CANCELLED: 's--cancel', REJECTED: 's--cancel', COMPLAINT: 's--warn' }; return m[s] || '' },
    logColor(l) { const m = { PENDING_ACCEPT: '#E6A23C', ACCEPTED: '#7A9A7E', IN_SERVICE: '#409EFF', PENDING_CONFIRM: '#E6A23C', COMPLETED: '#67C23A', CANCELLED: '#909399', REJECTED: '#F56C6C' }; return m[l.toStatus] || '#909399' },
    money(v) { return v != null ? `￥${Number(v).toFixed(2)}` : '-' },
    fmt(v) { if (!v) return '-'; return String(v).replace('T', ' ').substring(0, 16) }
  }
}
</script>

<style scoped>
.page-wrap { padding: 24px 32px; max-width: 1100px; }
.page-head { display: flex; align-items: flex-start; justify-content: space-between; margin-bottom: 16px; }
.page-title { font-size: 20px; font-weight: 700; color: #2C2418; margin: 0; }
.page-subtitle { margin: 6px 0 0; font-size: 13px; color: #999; display: flex; align-items: center; gap: 10px; }
.muted { color: #999; font-size: 12px; }

/* 操作栏 */
.action-bar { display: flex; gap: 8px; margin-bottom: 16px; flex-wrap: wrap; }

/* 双栏 */
.detail-grid { display: grid; grid-template-columns: 1fr 280px; gap: 16px; align-items: start; }
@media (max-width: 900px) { .detail-grid { grid-template-columns: 1fr; } }

/* 信息卡片 */
.info-card { margin-bottom: 14px; }
.info-card .el-card__header { padding: 12px 16px; font-weight: 600; font-size: 14px; }
.info-card .el-card__body { padding: 0 16px 16px; }

/* 服务记录 */
.record-content { font-size: 14px; color: #333; line-height: 1.8; white-space: pre-wrap; }
.record-notes { margin-top: 12px; padding: 10px 12px; background: #FFF8E1; border-radius: 6px; font-size: 13px; color: #8B7355; }
.notes-label { font-weight: 600; }

/* 评价 */
.eval-item { padding: 10px 0; border-bottom: 1px solid #f0f0f0; }
.eval-item:last-child { border-bottom: none; }
.eval-header { display: flex; align-items: center; gap: 10px; }
.eval-score { color: #E6A23C; font-size: 14px; letter-spacing: 2px; }
.eval-user { font-size: 12px; color: #999; }
.eval-time { font-size: 12px; color: #bbb; margin-left: auto; }
.eval-content { margin-top: 6px; font-size: 14px; color: #555; }
.empty-text { text-align: center; color: #bbb; padding: 20px 0; font-size: 13px; }

/* 时间线 */
.timeline-card .el-card__body { padding: 8px 16px 16px; }
.tl-content p { margin: 0; }
.tl-status { font-size: 13px; font-weight: 600; color: #333; }
.tl-remark { font-size: 12px; color: #666; margin-top: 2px; }
.tl-operator { font-size: 11px; color: #aaa; margin-top: 2px; }

/* 状态标签 */
.status-tag { display: inline-block; padding: 2px 10px; border-radius: 20px; font-size: 12px; font-weight: 500; }
.s--pending { background: rgba(230,162,60,.1); color: #B88230; }
.s--active { background: rgba(122,154,126,.12); color: #5C7A60; }
.s--warn { background: rgba(224,96,96,.08); color: #C05050; }
.s--done { background: rgba(100,130,180,.1); color: #5577AA; }
.s--cancel { background: rgba(153,153,153,.1); color: #888; }
</style>
