<template>
  <div class="page-wrap">
    <h2 class="page-title">需求广场</h2>
    <p class="page-desc">浏览待匹配的服务需求，选择适合的申请接单</p>

    <div class="content-card" v-loading="loading">
      <div v-if="!list.length && !loading" class="empty-tip">暂无可申请的需求，请稍后再来</div>
      <div class="pool-grid">
        <div v-for="row in list" :key="row.requestId" class="pool-card">
          <div class="pc-header">
            <div>
              <el-tag size="small" effect="plain">{{ row.serviceType }}</el-tag>
              <el-tag v-if="row.applicationCount > 0" size="small" type="warning" effect="plain" class="pc-app-count">
                {{ row.applicationCount }}人申请
              </el-tag>
            </div>
            <span class="pc-budget" v-if="row.budget">¥{{ row.budget }}</span>
          </div>
          <div class="pc-row"><i class="el-icon-office-building"></i> {{ row.hospitalName || '-' }} · {{ row.department || '-' }}</div>
          <div class="pc-row"><i class="el-icon-date"></i> {{ fmt(row.serviceDate) }}</div>
          <div v-if="row.aiSummary" class="pc-ai">
            <span class="pc-ai-label">AI摘要</span>
            <p>{{ row.aiSummary }}</p>
          </div>
          <div class="pc-req">{{ row.requirement || '暂无需求描述' }}</div>
          <div class="pc-footer">
            <el-tag v-if="row.myStatus === 'PENDING'" size="mini" type="warning" effect="plain">已申请，待客户选择</el-tag>
            <el-tag v-else-if="row.myStatus === 'ACCEPTED'" size="mini" type="success" effect="plain">已被选中</el-tag>
            <template v-else>
              <el-button size="small" type="primary" round :loading="applyingId === row.requestId" @click="doApply(row)">
                <i class="el-icon-s-claim"></i> 申请接单
              </el-button>
            </template>
          </div>
        </div>
      </div>
    </div>

    <!-- 申请留言弹窗 -->
    <el-dialog title="申请接单" :visible.sync="applyVisible" width="440px">
      <el-form label-width="70px" size="medium">
        <el-form-item label="留言">
          <el-input v-model="applyMessage" type="textarea" :rows="3" maxlength="500" show-word-limit
            placeholder="向客户介绍一下自己（选填）" />
        </el-form-item>
      </el-form>
      <span slot="footer">
        <el-button @click="applyVisible = false">取消</el-button>
        <el-button type="primary" :loading="applying" @click="confirmApply">确认申请</el-button>
      </span>
    </el-dialog>
  </div>
</template>

<script>
import { getRequestPool, applyForRequest } from '@/api/application'

export default {
  name: 'RequestPool',
  data() {
    return { list: [], loading: false, applyVisible: false, applyMessage: '', applyReqId: null, applyingId: null, applying: false }
  },
  created() { this.fetchPool() },
  methods: {
    async fetchPool() {
      this.loading = true
      try { const res = await getRequestPool(); this.list = res.data || res || [] } catch { this.list = [] } finally { this.loading = false }
    },
    doApply(row) {
      this.applyReqId = row.requestId; this.applyMessage = ''; this.applyVisible = true
    },
    async confirmApply() {
      this.applying = true; this.applyingId = this.applyReqId
      try {
        await applyForRequest({ requestId: this.applyReqId, message: this.applyMessage })
        this.$message.success('已申请，等待客户选择'); this.applyVisible = false; this.fetchPool()
      } catch {} finally { this.applying = false; this.applyingId = null }
    },
    fmt(d) { if (!d) return '-'; return String(d).replace('T', ' ').substring(0, 16) }
  }
}
</script>

<style scoped>
.page-wrap { padding: 24px 32px; }
.page-title { font-size: 20px; font-weight: 700; color: rgba(78,106,56,0.92); margin: 0 0 4px; font-family: 'Noto Serif SC', serif; }
.page-desc { font-size: 13px; color: rgba(130,140,116,0.75); margin: 0 0 18px; }
.content-card { background: rgba(255,255,255,0.58); backdrop-filter: blur(14px); -webkit-backdrop-filter: blur(14px); border: 1px solid rgba(255,255,255,0.7); border-radius: 14px; padding: 24px; box-shadow: 0 12px 36px -16px rgba(50,60,30,0.2), inset 0 1px 0 rgba(255,255,255,0.6); min-height: 200px; }
.empty-tip { text-align: center; color: rgba(130,140,116,0.7); padding: 60px 0; font-size: 14px; }
.pool-grid { display: grid; grid-template-columns: repeat(auto-fill, minmax(320px, 1fr)); gap: 18px; }
.pool-card { border: 1px solid rgba(150,140,110,0.14); background: rgba(255,255,255,0.5); border-radius: 12px; padding: 20px; transition: all .25s ease; }
.pool-card:hover { box-shadow: 0 8px 24px -8px rgba(50,60,30,0.18); border-color: rgba(180,215,115,0.25); }
.pc-header { display: flex; align-items: center; justify-content: space-between; margin-bottom: 14px; }
.pc-app-count { margin-left: 8px; }
.pc-budget { font-size: 20px; font-weight: 700; color: rgba(170,130,60,0.9); }
.pc-row { font-size: 14px; color: rgba(96,110,82,0.8); display: flex; align-items: center; gap: 8px; margin-bottom: 8px; overflow: hidden; text-overflow: ellipsis; white-space: nowrap; }
.pc-row i { color: rgba(108,140,80,0.6); flex-shrink: 0; }
.pc-ai { padding: 10px 12px; border-left: 3px solid rgba(108,140,80,0.6); background: rgba(248,250,240,0.5); border-radius: 8px; margin: 10px 0; }
.pc-ai-label { font-size: 11px; font-weight: 700; color: rgba(78,106,56,0.85); }
.pc-ai p { margin: 4px 0 0; line-height: 1.55; font-size: 13px; color: rgba(46,60,38,0.72); }
.pc-req { font-size: 13px; color: rgba(96,110,82,0.8); line-height: 1.5; margin: 8px 0 14px; max-height: 60px; overflow: hidden; }
.pc-footer { display: flex; justify-content: center; padding-top: 14px; border-top: 1px solid rgba(150,140,110,0.1); }
.pc-footer .el-button--primary { background: linear-gradient(135deg, rgba(108,140,80,0.9), rgba(78,106,56,0.9)) !important; border-color: rgba(78,106,56,0.9) !important; }
</style>
