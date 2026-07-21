<template>
  <div class="page-wrap">
    <h2 class="page-title">需求审核</h2>
    <div class="content-card" v-loading="loading">
      <div v-if="!list.length && !loading" class="empty-tip">暂无待审核的需求</div>
      <div class="review-grid">
        <div v-for="row in list" :key="row.id" class="review-card">
          <div class="rc-header">
            <div>
              <el-tag size="small" type="info">{{ row.serviceType }}</el-tag>
              <el-tag v-if="row.preferredCompanionId" size="small" type="warning" effect="dark" class="rc-directed">指定下单</el-tag>
            </div>
            <span class="rc-budget" v-if="row.budget">¥{{ row.budget }}</span>
          </div>
          <div class="rc-row rc-preferred" v-if="row.preferredCompanionId">
            <i class="el-icon-user-solid"></i> 指定陪诊师：{{ row.preferredCompanionName || ('#' + row.preferredCompanionId) }}
            <span class="rc-preferred-note">（通过后自动生成订单）</span>
          </div>
          <div class="rc-row"><i class="el-icon-office-building"></i> {{ row.hospitalName || '-' }} · {{ row.department || '-' }}</div>
          <div class="rc-row"><i class="el-icon-date"></i> {{ fmt(row.serviceDate) }}</div>
          <div class="rc-row"><i class="el-icon-user"></i> {{ row.contactName || '-' }} · {{ row.contactPhone || '-' }}</div>
          <div v-if="row.aiSummary" class="ai-summary">
            <div class="ai-title"><i class="el-icon-magic-stick"></i> AI摘要</div>
            <p>{{ row.aiSummary }}</p>
          </div>
          <div class="rc-req">{{ row.requirement || '暂无需求描述' }}</div>
          <div class="rc-actions">
            <el-button type="success" size="small" round @click="doAudit(row, 1)"><i class="el-icon-check"></i> 通过</el-button>
            <el-button type="danger" size="small" round @click="openReject(row)"><i class="el-icon-close"></i> 拒绝</el-button>
          </div>
        </div>
      </div>
    </div>

    <el-dialog title="拒绝需求" :visible.sync="rejectVisible" width="400px">
      <el-form label-width="70px" size="medium">
        <el-form-item label="原因">
          <el-input v-model="rejectRemark" type="textarea" :rows="3" placeholder="请填写拒绝原因（选填，客户可见）" />
        </el-form-item>
      </el-form>
      <span slot="footer">
        <el-button @click="rejectVisible = false">取消</el-button>
        <el-button type="danger" :loading="auditing" @click="confirmReject">确认拒绝</el-button>
      </span>
    </el-dialog>
  </div>
</template>

<script>
import { getPendingRequests, auditRequest } from '@/api/admin'

export default {
  name: 'RequestReview',
  data() {
    return { list: [], loading: false, rejectVisible: false, rejectRemark: '', currentRow: null, auditing: false }
  },
  created() { this.fetchList() },
  methods: {
    async fetchList() {
      this.loading = true
      try { const res = await getPendingRequests(); this.list = res.data || res || [] } catch { this.list = [] } finally { this.loading = false }
    },
    doAudit(row, status) {
      const tip = row.preferredCompanionId
        ? '确认通过该指定需求？通过后将自动为客户指定的陪诊师生成待接单订单。'
        : '确认通过该需求？通过后将进入陪诊师需求广场。'
      this.$confirm(tip, '确认通过', { type: 'success' }).then(async () => {
        try { await auditRequest(row.id, { auditStatus: status }); this.$message.success('已通过'); this.fetchList() } catch {}
      }).catch(() => {})
    },
    openReject(row) { this.currentRow = row; this.rejectRemark = ''; this.rejectVisible = true },
    async confirmReject() {
      this.auditing = true
      try {
        await auditRequest(this.currentRow.id, { auditStatus: 2, remark: this.rejectRemark })
        this.$message.success('已拒绝'); this.rejectVisible = false; this.fetchList()
      } catch {} finally { this.auditing = false }
    },
    fmt(d) { if (!d) return '-'; return d.replace('T', ' ').substring(0, 16) }
  }
}
</script>

<style scoped>
.page-wrap { padding: 24px 32px; }
.page-title { font-size: 20px; font-weight: 700; color: rgba(78,106,56,0.92); margin: 0 0 20px; font-family: 'Noto Serif SC', serif; }
.content-card { background: rgba(255,255,255,0.58); backdrop-filter: blur(14px); -webkit-backdrop-filter: blur(14px); border: 1px solid rgba(255,255,255,0.7); border-radius: 14px; padding: 24px; box-shadow: 0 12px 36px -16px rgba(50,60,30,0.2), inset 0 1px 0 rgba(255,255,255,0.6); min-height: 200px; }
.empty-tip { text-align: center; color: rgba(130,140,116,0.7); padding: 60px 0; font-size: 14px; }
.review-grid { display: grid; grid-template-columns: repeat(auto-fill, minmax(320px, 1fr)); gap: 18px; }
.review-card { border: 1px solid rgba(150,140,110,0.14); background: rgba(255,255,255,0.5); border-radius: 12px; padding: 20px; transition: all .25s ease; }
.review-card:hover { box-shadow: 0 8px 24px -8px rgba(50,60,30,0.18); border-color: rgba(180,215,115,0.25); }
.rc-header { display: flex; align-items: center; justify-content: space-between; margin-bottom: 14px; }
.rc-budget { font-size: 20px; font-weight: 700; color: rgba(170,130,60,0.9); }
.rc-row { font-size: 14px; color: rgba(96,110,82,0.8); display: flex; align-items: center; gap: 8px; margin-bottom: 8px; overflow: hidden; text-overflow: ellipsis; white-space: nowrap; }
.rc-row i { color: rgba(108,140,80,0.65); flex-shrink: 0; }
.ai-summary { padding: 12px 14px; border-left: 3px solid rgba(108,140,80,0.6); background: rgba(248,250,240,0.5); border-radius: 8px; margin: 10px 0; }
.ai-title { display: flex; align-items: center; gap: 6px; color: rgba(78,106,56,0.85); font-size: 12px; font-weight: 700; }
.ai-summary p { margin: 6px 0 0; line-height: 1.6; font-size: 13px; color: rgba(46,60,38,0.72); }
.rc-req { font-size: 13px; color: rgba(96,110,82,0.8); line-height: 1.5; margin: 8px 0 14px; max-height: 60px; overflow: hidden; }
.rc-actions { display: flex; gap: 10px; justify-content: center; padding-top: 14px; border-top: 1px solid rgba(150,140,110,0.1); }
.rc-directed { margin-left: 6px; }
.rc-preferred { color: rgba(78,106,56,0.88); font-weight: 600; white-space: normal; }
.rc-preferred-note { color: rgba(130,140,116,0.7); font-weight: 400; font-size: 12px; }
</style>
