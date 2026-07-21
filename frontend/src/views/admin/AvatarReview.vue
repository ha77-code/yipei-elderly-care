<template>
  <div class="page-wrap">
    <h2 class="page-title">头像审核</h2>
    <div class="content-card" v-loading="loading">
      <div v-if="!list.length && !loading" class="empty-tip">暂无待审核的头像</div>
      <div class="avatar-review-grid">
        <div v-for="row in list" :key="row.id" class="review-card">
          <div class="review-user">
            <span class="review-name">{{ row.nickname || row.username }}</span>
            <span :class="['role-tag', roleClass(row.role)]">{{ roleMap[row.role] || row.role }}</span>
          </div>
          <div class="avatar-compare">
            <div class="compare-col">
              <el-avatar :size="72" :src="row.avatar || undefined" icon="el-icon-user-solid" />
              <span class="compare-label">当前</span>
            </div>
            <i class="el-icon-right compare-arrow"></i>
            <div class="compare-col">
              <div class="avatar-ring-new">
                <el-avatar :size="72" :src="row.pendingAvatar || undefined" icon="el-icon-user-solid" />
              </div>
              <span class="compare-label compare-label--new">待审核</span>
            </div>
          </div>
          <div class="review-actions">
            <el-button type="success" size="small" round @click="doAudit(row, 1)">
              <i class="el-icon-check"></i> 通过
            </el-button>
            <el-button type="danger" size="small" round @click="openReject(row)">
              <i class="el-icon-close"></i> 拒绝
            </el-button>
          </div>
        </div>
      </div>
    </div>

    <el-dialog title="拒绝头像" :visible.sync="rejectVisible" width="400px">
      <el-form label-width="70px" size="medium">
        <el-form-item label="原因">
          <el-input v-model="rejectRemark" type="textarea" :rows="3" placeholder="请填写拒绝原因（选填）" />
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
import { getPendingAvatars, auditAvatar } from '@/api/admin'

const ROLE_MAP = { CUSTOMER: '客户', COMPANION: '陪诊师', ADMIN: '管理员' }

export default {
  name: 'AvatarReview',
  data() {
    return { list: [], loading: false, roleMap: ROLE_MAP, rejectVisible: false, rejectRemark: '', currentRow: null, auditing: false }
  },
  created() { this.fetchList() },
  methods: {
    async fetchList() {
      this.loading = true
      try { const res = await getPendingAvatars(); this.list = res.data || res || [] } catch { this.list = [] } finally { this.loading = false }
    },
    roleClass(r) { return { CUSTOMER: 'r--customer', COMPANION: 'r--companion', ADMIN: 'r--admin' }[r] || '' },
    doAudit(row, status) {
      this.$confirm('确认通过该头像？', '确认通过', { type: 'success' }).then(async () => {
        try { await auditAvatar(row.id, { auditStatus: status }); this.$message.success('已通过'); this.fetchList() } catch {}
      }).catch(() => {})
    },
    openReject(row) { this.currentRow = row; this.rejectRemark = ''; this.rejectVisible = true },
    async confirmReject() {
      this.auditing = true
      try {
        await auditAvatar(this.currentRow.id, { auditStatus: 2, remark: this.rejectRemark })
        this.$message.success('已拒绝'); this.rejectVisible = false; this.fetchList()
      } catch {} finally { this.auditing = false }
    }
  }
}
</script>

<style scoped>
.page-wrap { padding: 24px 32px; }
.page-title { font-size: 20px; font-weight: 700; color: rgba(78,106,56,0.92); margin: 0 0 20px; font-family: 'Noto Serif SC', serif; }
.content-card { background: rgba(255,255,255,0.58); backdrop-filter: blur(14px); -webkit-backdrop-filter: blur(14px); border: 1px solid rgba(255,255,255,0.7); border-radius: 14px; padding: 24px; box-shadow: 0 12px 36px -16px rgba(50,60,30,0.2), inset 0 1px 0 rgba(255,255,255,0.6); min-height: 200px; }
.empty-tip { text-align: center; color: rgba(130,140,116,0.7); padding: 60px 0; font-size: 14px; }
.avatar-review-grid { display: grid; grid-template-columns: repeat(auto-fill, minmax(290px, 1fr)); gap: 18px; }
.review-card { border: 1px solid rgba(150,140,110,0.14); background: rgba(255,255,255,0.5); border-radius: 12px; padding: 20px; transition: all .25s ease; }
.review-card:hover { box-shadow: 0 8px 24px -8px rgba(50,60,30,0.18); border-color: rgba(180,215,115,0.25); }
.review-user { display: flex; align-items: center; justify-content: space-between; margin-bottom: 16px; }
.review-name { font-size: 15px; font-weight: 700; color: rgba(46,60,38,0.9); }
.avatar-compare { display: flex; align-items: center; justify-content: center; gap: 24px; margin-bottom: 18px; padding: 16px 0; background: rgba(248,250,240,0.4); border-radius: 10px; }
.compare-col { display: flex; flex-direction: column; align-items: center; gap: 8px; }
.compare-label { font-size: 12px; color: rgba(130,140,116,0.7); }
.compare-label--new { color: rgba(78,106,56,0.88); font-weight: 600; }
.avatar-ring-new { padding: 3px; border-radius: 50%; background: linear-gradient(135deg, rgba(108,140,80,0.7), rgba(78,106,56,0.7)); }
.compare-arrow { font-size: 22px; color: rgba(130,140,116,0.4); }
.review-actions { display: flex; gap: 10px; justify-content: center; padding-top: 14px; border-top: 1px solid rgba(150,140,110,0.1); }
.role-tag { display: inline-block; padding: 2px 10px; border-radius: 20px; font-size: 12px; font-weight: 500; }
.r--customer { background: rgba(108,140,80,.12); color: rgba(78,106,56,0.85); }
.r--companion { background: rgba(245,215,140,.18); color: rgba(170,130,60,0.9); }
.r--admin { background: rgba(150,145,130,.12); color: rgba(110,110,95,0.85); }
</style>
