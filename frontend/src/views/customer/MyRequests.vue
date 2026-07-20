<template>
  <div class="my-requests-page">
    <div class="page-header">
      <h2 class="page-title">我的服务需求</h2>
      <el-button type="primary" icon="el-icon-plus" round @click="$router.push('/customer/request/create')">
        发布新需求
      </el-button>
    </div>

    <!-- 列表 -->
    <div class="content-card">
      <el-table
        :data="list"
        v-loading="loading"
        stripe
        class="warm-table"
        empty-text="暂无服务需求"
      >
        <el-table-column prop="id" label="编号" width="80" align="center" />
        <el-table-column prop="serviceType" label="服务类型" min-width="100">
          <template slot-scope="{ row }">
            <el-tag size="small" type="info">{{ row.serviceType }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="hospitalName" label="医院" min-width="140" show-overflow-tooltip />
        <el-table-column prop="department" label="科室" min-width="100" show-overflow-tooltip />
        <el-table-column prop="serviceDate" label="服务日期" width="120" align="center">
          <template slot-scope="{ row }">
            {{ formatDate(row.serviceDate) }}
          </template>
        </el-table-column>
        <el-table-column prop="budget" label="预算" width="100" align="right">
          <template slot-scope="{ row }">
            <span v-if="row.budget">¥{{ row.budget }}</span>
            <span v-else class="text-muted">未设置</span>
          </template>
        </el-table-column>
        <el-table-column label="审核" width="100" align="center">
          <template slot-scope="{ row }">
            <span :class="['audit-tag', `audit--${row.auditStatus}`]">{{ auditMap[row.auditStatus] || '待审核' }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="100" align="center">
          <template slot-scope="{ row }">
            <span :class="['status-tag', `status--${row.status?.toLowerCase()}`]">
              {{ statusMap[row.status] || row.status }}
            </span>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="220" align="center" fixed="right">
          <template slot-scope="{ row }">
            <el-button type="text" size="small" @click="showDetail(row)">
              详情
            </el-button>
            <el-button
              v-if="row.status === 'PENDING' && row.auditStatus === 1"
              type="text"
              size="small"
              @click="showApplications(row)"
            >
              查看申请
            </el-button>
            <el-button
              v-if="row.status === 'PENDING'"
              type="text"
              size="small"
              class="text-danger"
              @click="handleCancel(row)"
            >
              取消
            </el-button>
          </template>
        </el-table-column>
      </el-table>

      <!-- 分页 -->
      <div class="table-footer" v-if="total > pageSize">
        <el-pagination
          background
          layout="prev, pager, next"
          :total="total"
          :page-size="pageSize"
          :current-page.sync="currentPage"
          @current-change="fetchList"
        />
      </div>
    </div>

    <!-- 详情弹窗 -->
    <el-dialog
      title="需求详情"
      :visible.sync="detailVisible"
      width="560px"
      class="warm-dialog"
    >
      <div class="detail-grid" v-if="current">
        <div class="detail-item">
          <span class="detail-label">服务类型</span>
          <span class="detail-value">{{ current.serviceType }}</span>
        </div>
        <div class="detail-item">
          <span class="detail-label">医院</span>
          <span class="detail-value">{{ current.hospitalName || '-' }}</span>
        </div>
        <div class="detail-item">
          <span class="detail-label">科室</span>
          <span class="detail-value">{{ current.department || '-' }}</span>
        </div>
        <div class="detail-item">
          <span class="detail-label">服务日期</span>
          <span class="detail-value">{{ formatDate(current.serviceDate) }}</span>
        </div>
        <div class="detail-item">
          <span class="detail-label">预算</span>
          <span class="detail-value">¥{{ current.budget || '未设置' }}</span>
        </div>
        <div class="detail-item">
          <span class="detail-label">联系人</span>
          <span class="detail-value">{{ current.contactName }}</span>
        </div>
        <div class="detail-item">
          <span class="detail-label">联系电话</span>
          <span class="detail-value">{{ current.contactPhone }}</span>
        </div>
        <div class="detail-item">
          <span class="detail-label">状态</span>
          <span :class="['status-tag', `status--${current.status?.toLowerCase()}`]">
            {{ statusMap[current.status] || current.status }}
          </span>
        </div>
        <div class="detail-item detail-full">
          <span class="detail-label">需求内容</span>
          <span class="detail-value">{{ current.requirement }}</span>
        </div>
        <div class="detail-item detail-full" v-if="current.specialNotes">
          <span class="detail-label">特殊说明</span>
          <span class="detail-value">{{ current.specialNotes }}</span>
        </div>
        <div class="detail-item detail-full" v-if="current.auditStatus === 2 && current.auditRemark">
          <span class="detail-label">审核未通过原因</span>
          <span class="detail-value text-danger">{{ current.auditRemark }}</span>
        </div>
      </div>
    </el-dialog>

    <!-- 申请列表弹窗 -->
    <el-dialog title="陪诊师申请" :visible.sync="appVisible" width="620px" class="warm-dialog">
      <div v-loading="appLoading" class="app-list">
        <div v-if="!applications.length && !appLoading" class="app-empty">暂时还没有陪诊师申请，可耐心等待</div>
        <div v-for="app in applications" :key="app.id" class="app-card">
          <el-avatar :size="52" :src="app.companionAvatar || undefined" icon="el-icon-user-solid" />
          <div class="app-main">
            <div class="app-top">
              <span class="app-name">{{ app.companionName || '陪诊师' }}</span>
              <span class="app-rating"><i class="el-icon-star-on"></i> {{ app.rating || '5.0' }} · 已完成{{ app.completedCount || 0 }}单</span>
            </div>
            <div class="app-meta">{{ app.serviceArea || '服务区域未填' }} · 从业{{ app.experienceYears || 0 }}年</div>
            <div class="app-intro" v-if="app.introduction">{{ app.introduction }}</div>
            <div class="app-msg" v-if="app.message"><i class="el-icon-chat-dot-round"></i> {{ app.message }}</div>
          </div>
          <div class="app-actions">
            <template v-if="app.status === 'PENDING'">
              <el-button type="primary" size="small" round @click="handleAcceptApp(app)">选TA</el-button>
              <el-button type="text" size="small" class="text-danger" @click="handleRejectApp(app)">拒绝</el-button>
            </template>
            <span v-else :class="['app-tag', 'app--' + (app.status || '').toLowerCase()]">{{ appStatusMap[app.status] || app.status }}</span>
          </div>
        </div>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { getMyRequests, cancelRequest } from '@/api/serviceRequest'
import { getApplicationsByRequest, acceptApplication, rejectApplication } from '@/api/application'

const STATUS_MAP = {
  PENDING: '待处理',
  MATCHED: '已匹配',
  CANCELLED: '已取消',
  CLOSED: '已关闭'
}

const AUDIT_MAP = { 0: '待审核', 1: '已通过', 2: '未通过' }
const APP_STATUS_MAP = { PENDING: '待选择', ACCEPTED: '已接受', REJECTED: '未选中', WITHDRAWN: '已撤回' }

export default {
  name: 'MyRequests',
  data() {
    return {
      list: [],
      loading: false,
      currentPage: 1,
      pageSize: 10,
      total: 0,
      statusMap: STATUS_MAP,
      auditMap: AUDIT_MAP,
      appStatusMap: APP_STATUS_MAP,
      detailVisible: false,
      current: null,
      appVisible: false,
      appLoading: false,
      applications: [],
      appRequest: null
    }
  },
  created() {
    this.fetchList()
  },
  methods: {
    async fetchList() {
      this.loading = true
      try {
        const res = await getMyRequests()
        const data = res.data || res
        this.list = data.records || data.list || data || []
        this.total = data.total || this.list.length
      } catch {
        this.list = []
      } finally {
        this.loading = false
      }
    },
    showDetail(row) {
      this.current = row
      this.detailVisible = true
    },
    handleCancel(row) {
      this.$confirm('确定要取消该服务需求吗？', '确认取消', {
        confirmButtonText: '确定',
        cancelButtonText: '返回',
        type: 'warning'
      }).then(async () => {
        try {
          await cancelRequest(row.id)
          this.$message.success('需求已取消')
          this.fetchList()
        } catch { /* 错误已统一处理 */ }
      }).catch(() => {})
    },
    async showApplications(row) {
      this.appRequest = row
      this.appVisible = true
      this.appLoading = true
      try {
        const res = await getApplicationsByRequest(row.id)
        this.applications = res.data || res || []
      } catch { this.applications = [] } finally { this.appLoading = false }
    },
    handleAcceptApp(app) {
      const doAccept = (servicePrice) => {
        this.$confirm(`确认选择 ${app.companionName || '该陪诊师'} 为您服务？确认后将生成订单。`, '确认选择', { type: 'success' })
          .then(async () => {
            try {
              await acceptApplication(app.id, servicePrice != null ? { servicePrice } : {})
              this.$message.success('已确认，订单已生成，可在“我的订单”中沟通')
              this.appVisible = false
              this.fetchList()
            } catch {}
          }).catch(() => {})
      }
      // 需求无预算时，让客户补填服务金额
      if (!this.appRequest || !this.appRequest.budget) {
        this.$prompt('请输入本单服务金额（元）', '设置金额', {
          inputPattern: /^\d+(\.\d{1,2})?$/, inputErrorMessage: '请输入有效金额'
        }).then(({ value }) => doAccept(Number(value))).catch(() => {})
      } else {
        doAccept(null)
      }
    },
    handleRejectApp(app) {
      this.$confirm('确认拒绝该申请？', '拒绝申请', { type: 'warning' }).then(async () => {
        try { await rejectApplication(app.id); this.$message.success('已拒绝'); this.showApplications(this.appRequest) } catch {}
      }).catch(() => {})
    },
    formatDate(dateStr) {
      if (!dateStr) return '-'
      return dateStr.replace('T', ' ').substring(0, 16)
    }
  }
}
</script>

<style scoped>
.my-requests-page {
  padding: 24px 32px;
}

.page-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 20px;
}

.page-title {
  font-family: var(--font-family);
  font-size: 20px;
  font-weight: 700;
  color: var(--color-text-primary);
  margin: 0;
}

.content-card {
  background: #FFFFFF;
  border: 1px solid var(--color-border-light);
  border-radius: var(--radius-md);
  padding: 8px;
  box-shadow: var(--shadow-sm);
}

/* 表格 */
.warm-table {
  width: 100%;
}

.warm-table::v-deep th {
  background: var(--color-bg-page);
  color: var(--color-text-regular);
  font-weight: 600;
  font-size: 13px;
}

.warm-table::v-deep td {
  font-size: 14px;
  color: var(--color-text-regular);
}

.text-muted {
  color: var(--color-text-placeholder);
}

.text-danger {
  color: #E06060 !important;
}

/* 状态标签 */
.status-tag {
  display: inline-block;
  padding: 2px 10px;
  border-radius: 20px;
  font-size: 12px;
  font-weight: 500;
}

.status--pending {
  background: rgba(230, 162, 60, 0.1);
  color: #B88230;
}

.status--matched {
  background: rgba(122, 154, 126, 0.12);
  color: #5C7A60;
}

.status--cancelled {
  background: rgba(153, 153, 153, 0.1);
  color: #888888;
}

.status--closed {
  background: rgba(100, 130, 180, 0.1);
  color: #5577AA;
}

/* 审核标签 */
.audit-tag { display: inline-block; padding: 2px 10px; border-radius: 20px; font-size: 12px; font-weight: 500; }
.audit--0 { background: rgba(230,162,60,.1); color: #B88230; }
.audit--1 { background: rgba(122,154,126,.12); color: #5C7A60; }
.audit--2 { background: rgba(224,96,96,.1); color: #C05050; }

/* 申请列表 */
.app-list { min-height: 120px; }
.app-empty { text-align: center; color: var(--color-text-placeholder); padding: 50px 0; font-size: 14px; }
.app-card { display: flex; gap: 14px; padding: 16px; border: 1px solid var(--color-border-light); border-radius: var(--radius-md); margin-bottom: 12px; }
.app-main { flex: 1; min-width: 0; }
.app-top { display: flex; align-items: center; gap: 12px; margin-bottom: 4px; }
.app-name { font-size: 15px; font-weight: 600; color: var(--color-text-primary); }
.app-rating { font-size: 12px; color: #B88230; }
.app-rating i { color: #E6A23C; }
.app-meta { font-size: 13px; color: var(--color-text-secondary); margin-bottom: 6px; }
.app-intro { font-size: 13px; color: var(--color-text-secondary); line-height: 1.5; }
.app-msg { font-size: 13px; color: var(--color-text-primary); background: var(--color-bg-page); padding: 8px 10px; border-radius: var(--radius-sm); margin-top: 8px; }
.app-msg i { color: var(--color-primary); }
.app-actions { display: flex; flex-direction: column; align-items: center; justify-content: center; gap: 8px; flex-shrink: 0; }
.app-tag { display: inline-block; padding: 2px 10px; border-radius: 20px; font-size: 12px; font-weight: 500; }
.app--pending { background: rgba(230,162,60,.1); color: #B88230; }
.app--accepted { background: rgba(122,154,126,.12); color: #5C7A60; }
.app--rejected, .app--withdrawn { background: rgba(153,153,153,.1); color: #888; }

/* 分页 */
.table-footer {
  display: flex;
  justify-content: flex-end;
  padding: 16px 0 8px;
}

/* 详情弹窗 */
.detail-grid {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 16px 24px;
}

.detail-item {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.detail-full {
  grid-column: 1 / -1;
}

.detail-label {
  font-size: 13px;
  color: var(--color-text-placeholder);
  font-weight: 500;
}

.detail-value {
  font-size: 15px;
  color: var(--color-text-primary);
  line-height: 1.6;
}
</style>
