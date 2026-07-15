<template>
  <div class="service-record-page">
    <div class="back-link" @click="$router.back()">
      <i class="el-icon-arrow-left"></i> 返回
    </div>

    <h2 class="page-title">服务记录 — 订单 #{{ orderId }}</h2>

    <div class="content-card" v-loading="loading">
      <!-- 查看模式：已有服务记录 -->
      <template v-if="record">
        <div class="record-detail">
          <div class="info-item">
            <span class="info-label">记录编号</span>
            <span class="info-value">#{{ record.id }}</span>
          </div>
          <div class="info-item">
            <span class="info-label">填写时间</span>
            <span class="info-value">{{ fmt(record.createdAt || record.created_at) }}</span>
          </div>
          <div class="info-item info-full">
            <span class="info-label">服务内容</span>
            <div class="info-content">{{ record.content }}</div>
          </div>
          <div class="info-item info-full" v-if="record.importantNotes">
            <span class="info-label">重要事项</span>
            <div class="info-content">{{ record.importantNotes }}</div>
          </div>
        </div>
      </template>

      <!-- 无记录空状态 -->
      <div class="empty-state" v-if="!loading && !record && !writing">
        <i class="el-icon-tickets"></i>
        <p>该订单暂无服务记录</p>
        <el-button
          v-if="canWrite"
          type="primary"
          size="medium"
          round
          @click="writing = true"
        >填写服务记录</el-button>
      </div>

      <!-- 填写模式 -->
      <template v-if="writing">
        <h3 class="form-title">填写服务记录</h3>
        <el-form
          ref="recordForm"
          :model="form"
          :rules="rules"
          label-width="80px"
          size="medium"
        >
          <el-form-item label="服务内容" prop="content">
            <el-input
              v-model="form.content"
              type="textarea"
              :rows="6"
              placeholder="请详细记录本次服务的内容，如：陪诊过程、协助事项、医嘱记录等"
              maxlength="2000"
              show-word-limit
            />
          </el-form-item>

          <el-form-item label="重要事项">
            <el-input
              v-model="form.importantNotes"
              type="textarea"
              :rows="3"
              placeholder="选填：如有需要特别说明的事项请在此记录"
              maxlength="1000"
              show-word-limit
            />
          </el-form-item>

          <el-form-item>
            <el-button
              type="primary"
              :loading="submitting"
              size="medium"
              round
              @click="handleSubmit"
            >提交记录</el-button>
            <el-button size="medium" round @click="writing = false">取消</el-button>
          </el-form-item>
        </el-form>
      </template>
    </div>
  </div>
</template>

<script>
import { getServiceRecordByOrder, createServiceRecord } from '@/api/serviceRecord'
import { getUserRole } from '@/utils/auth'

export default {
  name: 'ServiceRecord',
  data() {
    return {
      orderId: null,
      record: null,
      loading: false,
      writing: false,
      form: {
        content: '',
        importantNotes: ''
      },
      rules: {
        content: [
          { required: true, message: '请填写服务内容', trigger: 'blur' }
        ]
      },
      submitting: false
    }
  },
  computed: {
    canWrite() {
      return getUserRole() === 'COMPANION'
    }
  },
  created() {
    this.orderId = this.$route.params.orderId
    if (this.orderId) this.fetchRecord()
  },
  methods: {
    async fetchRecord() {
      this.loading = true
      try {
        const res = await getServiceRecordByOrder(this.orderId)
        this.record = res.data || res
      } catch {
        this.record = null
      } finally {
        this.loading = false
      }
    },
    async handleSubmit() {
      try {
        await this.$refs.recordForm.validate()
      } catch {
        return
      }

      this.submitting = true
      try {
        await createServiceRecord({
          orderId: this.orderId,
          ...this.form
        })
        this.$message.success('服务记录提交成功')
        this.writing = false
        this.fetchRecord()
      } catch {
        /* 错误已统一处理 */
      } finally {
        this.submitting = false
      }
    },
    fmt(d) {
      if (!d) return '-'
      return d.replace('T', ' ').substring(0, 16)
    }
  }
}
</script>

<style scoped>
.service-record-page {
  padding: 24px 32px;
  max-width: 720px;
}

.back-link {
  display: inline-flex;
  align-items: center;
  gap: 4px;
  font-size: 14px;
  color: var(--color-primary);
  cursor: pointer;
  margin-bottom: 16px;
  font-weight: 500;
}

.back-link:hover {
  color: var(--color-primary-dark);
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
  border-radius: var(--radius-lg);
  padding: 32px;
  box-shadow: var(--shadow-sm);
  min-height: 200px;
}

/* 记录详情 */
.record-detail {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 20px 24px;
}

.info-item {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.info-full {
  grid-column: 1 / -1;
}

.info-label {
  font-size: 13px;
  color: var(--color-text-placeholder);
  font-weight: 500;
}

.info-value {
  font-size: 15px;
  color: var(--color-text-primary);
}

.info-content {
  font-size: 15px;
  color: var(--color-text-primary);
  line-height: 1.8;
  background: var(--color-bg-page);
  padding: 16px;
  border-radius: var(--radius-sm);
  white-space: pre-wrap;
}

/* 表单 */
.form-title {
  font-family: var(--font-family);
  font-size: 16px;
  font-weight: 600;
  color: var(--color-text-primary);
  margin: 0 0 20px;
}

/* 空状态 */
.empty-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 60px 0;
  color: var(--color-text-placeholder);
}

.empty-state i {
  font-size: 48px;
  color: var(--color-border);
  margin-bottom: 16px;
}

.empty-state p {
  font-size: 15px;
  margin: 0 0 20px;
}

/* 响应式 */
@media (max-width: 768px) {
  .service-record-page { padding: 16px; }
  .content-card { padding: 20px; }
  .record-detail { grid-template-columns: 1fr; }
}
</style>
