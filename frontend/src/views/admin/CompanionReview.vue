<template>
  <div class="companion-review-page">
    <h2 class="page-title">陪诊师审核</h2>

    <div class="content-card">
      <el-table
        :data="list"
        v-loading="loading"
        stripe
        class="warm-table"
        empty-text="暂无待审核的陪诊师"
      >
        <el-table-column prop="id" label="编号" width="80" align="center" />
        <el-table-column prop="realName" label="真实姓名" min-width="100" />
        <el-table-column label="头像" width="70" align="center">
          <template slot-scope="{ row }">
            <el-avatar :size="36" :src="row.avatar" icon="el-icon-user-solid" />
          </template>
        </el-table-column>
        <el-table-column prop="serviceArea" label="服务区域" min-width="130" show-overflow-tooltip />
        <el-table-column prop="serviceTypes" label="服务类型" min-width="160">
          <template slot-scope="{ row }">
            <el-tag
              v-for="t in parseServiceTypes(row.serviceTypes)"
              :key="t"
              size="mini"
              class="mini-tag"
            >{{ t }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="experienceYears" label="经验" width="70" align="center">
          <template slot-scope="{ row }">
            {{ row.experienceYears || 0 }} 年
          </template>
        </el-table-column>
        <el-table-column prop="introduction" label="个人介绍" min-width="160" show-overflow-tooltip />
        <el-table-column label="操作" width="220" align="center" fixed="right">
          <template slot-scope="{ row }">
            <el-button type="success" size="small" round @click="handleAudit(row, 1)">
              <i class="el-icon-check"></i> 通过
            </el-button>
            <el-button type="danger" size="small" round @click="handleAudit(row, 2)">
              <i class="el-icon-close"></i> 拒绝
            </el-button>
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

    <!-- 审核弹窗 -->
    <el-dialog
      :title="auditAction === 1 ? '确认通过' : '确认拒绝'"
      :visible.sync="auditVisible"
      width="440px"
      class="warm-dialog"
    >
      <div class="audit-info" v-if="currentRow">
        <p><strong>陪诊师：</strong>{{ currentRow.realName }}</p>
        <p><strong>服务区域：</strong>{{ currentRow.serviceArea }}</p>
      </div>
      <el-form label-width="70px" size="medium">
        <el-form-item label="备注">
          <el-input
            v-model="auditRemark"
            type="textarea"
            :rows="3"
            :placeholder="auditAction === 1 ? '（选填）审核通过备注' : '请填写拒绝原因'"
          />
        </el-form-item>
      </el-form>
      <span slot="footer">
        <el-button @click="auditVisible = false">取消</el-button>
        <el-button
          :type="auditAction === 1 ? 'success' : 'danger'"
          :loading="auditing"
          @click="confirmAudit"
        >
          {{ auditAction === 1 ? '确认通过' : '确认拒绝' }}
        </el-button>
      </span>
    </el-dialog>
  </div>
</template>

<script>
import { getPendingCompanions } from '@/api/admin'
import { auditCompanion } from '@/api/companion'

export default {
  name: 'CompanionReview',
  data() {
    return {
      list: [],
      loading: false,
      currentPage: 1,
      pageSize: 10,
      total: 0,
      auditVisible: false,
      auditAction: 1,
      auditRemark: '',
      currentRow: null,
      auditing: false
    }
  },
  created() {
    this.fetchList()
  },
  methods: {
    async fetchList() {
      this.loading = true
      try {
        const res = await getPendingCompanions({
          page: this.currentPage,
          size: this.pageSize
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
    parseServiceTypes(types) {
      if (!types) return []
      return typeof types === 'string' ? types.split(',') : types
    },
    handleAudit(row, status) {
      this.currentRow = row
      this.auditAction = status
      this.auditRemark = ''
      this.auditVisible = true
    },
    async confirmAudit() {
      this.auditing = true
      try {
        await auditCompanion({
          businessId: this.currentRow.id,
          auditStatus: this.auditAction,
          remark: this.auditRemark
        })
        this.$message.success(this.auditAction === 1 ? '已通过审核' : '已拒绝')
        this.auditVisible = false
        this.fetchList()
      } catch {
        /* 错误已统一处理 */
      } finally {
        this.auditing = false
      }
    }
  }
}
</script>

<style scoped>
.companion-review-page {
  padding: 24px 32px;
}

.page-title {
  font-family: var(--font-family);
  font-size: 20px;
  font-weight: 700;
  color: var(--color-text-primary);
  margin: 0 0 20px;
}

.content-card {
  background: #FFFFFF;
  border: 1px solid var(--color-border-light);
  border-radius: var(--radius-md);
  padding: 8px;
  box-shadow: var(--shadow-sm);
}

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

.mini-tag {
  margin-right: 4px;
  margin-bottom: 2px;
  background: rgba(122, 154, 126, 0.08);
  border-color: rgba(122, 154, 126, 0.15);
  color: var(--color-primary-dark);
}

.table-footer {
  display: flex;
  justify-content: flex-end;
  padding: 16px 0 8px;
}

/* 审核弹窗 */
.audit-info {
  margin-bottom: 16px;
}

.audit-info p {
  margin: 0 0 6px;
  font-size: 14px;
  color: var(--color-text-regular);
}
</style>
