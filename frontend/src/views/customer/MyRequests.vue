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
        <el-table-column prop="status" label="状态" width="100" align="center">
          <template slot-scope="{ row }">
            <span :class="['status-tag', `status--${row.status?.toLowerCase()}`]">
              {{ statusMap[row.status] || row.status }}
            </span>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="200" align="center" fixed="right">
          <template slot-scope="{ row }">
            <el-button type="text" size="small" @click="showDetail(row)">
              详情
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
            <el-button
              v-if="row.status === 'MATCHED'"
              type="text"
              size="small"
              @click="goCreateOrder(row)"
            >
              创建订单
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
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { getMyRequests, cancelRequest } from '@/api/serviceRequest'
import { getUser } from '@/utils/auth'

const STATUS_MAP = {
  PENDING: '待处理',
  MATCHED: '已匹配',
  CANCELLED: '已取消',
  CLOSED: '已关闭'
}

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
      detailVisible: false,
      current: null
    }
  },
  created() {
    this.fetchList()
  },
  methods: {
    async fetchList() {
      this.loading = true
      try {
        const user = getUser()
        const res = await getMyRequests({
          customerId: user?.id
        })
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
    goCreateOrder(row) {
      this.$router.push({
        path: '/customer/request/create',
        query: { requestId: row.id }
      })
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
