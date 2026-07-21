<template>
  <div class="page-wrap">
    <h2 class="page-title">陪诊师申请</h2>
    <p class="page-desc">查看陪诊师对您需求的申请，选择最合适的一位确认接单</p>

    <div class="content-card" v-loading="loading">
      <div v-if="!list.length && !loading" class="empty-tip">暂无陪诊师申请，请耐心等待</div>
      <div class="app-grid">
        <div v-for="row in list" :key="row.id" class="app-card">
          <div class="ac-header">
            <div class="ac-companion">
              <el-avatar :size="48" :src="row.companionAvatar || undefined" icon="el-icon-user-solid" />
              <div class="ac-info">
                <span class="ac-name">{{ row.companionName || '陪诊师' }}</span>
                <span class="ac-trait" v-if="row.traits">{{ row.traits }}</span>
              </div>
            </div>
            <el-tag v-if="row.status === 'PENDING'" size="small" type="warning" effect="plain">待选择</el-tag>
            <el-tag v-else-if="row.status === 'ACCEPTED'" size="small" type="success" effect="plain">已选择</el-tag>
            <el-tag v-else size="small" type="info" effect="plain">{{ row.status }}</el-tag>
          </div>
          <div class="ac-body">
            <div class="ac-row" v-if="row.rating">
              <i class="el-icon-star-on"></i> 评分 {{ row.rating }} · 完成 {{ row.completedCount || 0 }} 单
            </div>
            <div class="ac-row" v-if="row.experienceYears">
              <i class="el-icon-medal"></i> {{ row.experienceYears }}年经验
            </div>
            <div class="ac-row" v-if="row.serviceArea">
              <i class="el-icon-location-outline"></i> {{ row.serviceArea }}
            </div>
            <div class="ac-msg" v-if="row.message">
              <i class="el-icon-chat-line-round"></i>
              <span>"{{ row.message }}"</span>
            </div>
          </div>
          <div class="ac-footer" v-if="row.status === 'PENDING'">
            <el-button type="primary" size="small" round :loading="acceptingId === row.id" @click="doAccept(row)">
              <i class="el-icon-check"></i> 选择该陪诊师
            </el-button>
            <el-button type="danger" size="small" round plain @click="doReject(row)">
              <i class="el-icon-close"></i> 拒绝
            </el-button>
          </div>
        </div>
      </div>
    </div>

    <!-- 确认价格弹窗 -->
    <el-dialog title="确认选择陪诊师" :visible.sync="acceptVisible" width="420px">
      <el-form label-width="80px" size="medium">
        <el-form-item label="陪诊师">{{ acceptRow ? acceptRow.companionName : '' }}</el-form-item>
        <el-form-item label="服务价格">
          <el-input-number v-model="acceptPrice" :min="1" :max="99999" :precision="2" :step="50" />
          <span style="margin-left:8px;font-size:13px;color:rgba(130,140,116,0.7)">元</span>
        </el-form-item>
      </el-form>
      <span slot="footer">
        <el-button @click="acceptVisible = false">取消</el-button>
        <el-button type="primary" :loading="accepting" @click="confirmAccept">确认选择，生成订单</el-button>
      </span>
    </el-dialog>
  </div>
</template>

<script>
import { getApplicationsByRequest, acceptApplication, rejectApplication } from '@/api/application'

export default {
  name: 'RequestApplications',
  props: {
    requestId: { type: [Number, String], required: true }
  },
  data() {
    return {
      list: [], loading: false,
      acceptVisible: false, acceptRow: null, acceptPrice: 200.00, acceptingId: null, accepting: false
    }
  },
  created() { this.fetchList() },
  methods: {
    async fetchList() {
      this.loading = true
      try { const res = await getApplicationsByRequest(this.requestId); this.list = res.data || res || [] } catch { this.list = [] } finally { this.loading = false }
    },
    doAccept(row) {
      this.acceptRow = row; this.acceptPrice = row.suggestedPrice || 200.00; this.acceptVisible = true
    },
    async confirmAccept() {
      this.accepting = true; this.acceptingId = this.acceptRow.id
      try {
        await acceptApplication(this.acceptRow.id, { servicePrice: this.acceptPrice })
        this.$message.success('已选择该陪诊师，订单已生成！'); this.acceptVisible = false; this.fetchList()
      } catch {} finally { this.accepting = false; this.acceptingId = null }
    },
    doReject(row) {
      this.$confirm('确认拒绝该陪诊师的申请？', '确认拒绝', { type: 'warning' }).then(async () => {
        try { await rejectApplication(row.id); this.$message.success('已拒绝'); this.fetchList() } catch {}
      }).catch(() => {})
    }
  }
}
</script>

<style scoped>
.page-wrap { padding: 24px 32px; }
.page-title { font-size: 20px; font-weight: 700; color: rgba(78,106,56,0.92); margin: 0 0 4px; font-family: 'Noto Serif SC', serif; }
.page-desc { font-size: 13px; color: rgba(130,140,116,0.75); margin: 0 0 18px; }
.content-card { background: rgba(255,255,255,0.58); backdrop-filter: blur(14px); -webkit-backdrop-filter: blur(14px); border: 1px solid rgba(255,255,255,0.7); border-radius: 14px; padding: 24px; box-shadow: 0 12px 36px -16px rgba(50,60,30,0.2), inset 0 1px 0 rgba(255,255,255,0.6); min-height: 200px; }
.empty-tip { text-align: center; color: rgba(130,140,116,0.7); padding: 60px 0; font-size: 14px; }
.app-grid { display: grid; grid-template-columns: repeat(auto-fill, minmax(340px, 1fr)); gap: 18px; }
.app-card { border: 1px solid rgba(150,140,110,0.14); background: rgba(255,255,255,0.5); border-radius: 12px; padding: 20px; transition: all .25s ease; }
.app-card:hover { box-shadow: 0 8px 24px -8px rgba(50,60,30,0.18); border-color: rgba(180,215,115,0.25); }
.ac-header { display: flex; align-items: flex-start; justify-content: space-between; margin-bottom: 16px; }
.ac-companion { display: flex; gap: 12px; align-items: center; }
.ac-info { display: flex; flex-direction: column; gap: 2px; }
.ac-name { font-size: 16px; font-weight: 700; color: rgba(46,60,38,0.9); }
.ac-trait { font-size: 12px; color: rgba(78,106,56,0.78); }
.ac-body { margin-bottom: 16px; }
.ac-row { font-size: 13px; color: rgba(96,110,82,0.78); display: flex; align-items: center; gap: 8px; margin-bottom: 6px; }
.ac-row i { color: rgba(108,140,80,0.6); flex-shrink: 0; }
.ac-msg { display: flex; gap: 8px; align-items: flex-start; margin-top: 10px; padding: 10px 12px; background: rgba(248,250,240,0.6); border-radius: 8px; font-size: 13px; color: rgba(46,60,38,0.72); line-height: 1.5; }
.ac-msg i { color: rgba(108,140,80,0.5); flex-shrink: 0; margin-top: 2px; }
.ac-footer { display: flex; gap: 10px; justify-content: center; padding-top: 14px; border-top: 1px solid rgba(150,140,110,0.1); }
.ac-footer .el-button--primary { background: linear-gradient(135deg, rgba(108,140,80,0.9), rgba(78,106,56,0.9)) !important; border-color: rgba(78,106,56,0.9) !important; }
</style>
