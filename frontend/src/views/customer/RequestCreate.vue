<template>
  <div class="request-create-page">
    <h2 class="page-title">{{ isOrderMode ? '创建订单' : '发布服务需求' }}</h2>

    <div class="content-card">
      <!-- 匹配信息提示（从我的需求跳转来创建订单时） -->
      <div class="match-banner" v-if="isOrderMode && matchedRequest">
        <i class="el-icon-info"></i>
        <span>已匹配陪诊师，请确认需求信息并创建订单</span>
      </div>

      <el-form
        ref="requestForm"
        :model="form"
        :rules="rules"
        label-width="100px"
        class="request-form"
        size="medium"
      >
        <el-row :gutter="24">
          <el-col :span="12">
            <el-form-item label="服务类型" prop="serviceType">
              <el-select v-model="form.serviceType" placeholder="请选择服务类型" style="width:100%">
                <el-option label="门诊陪诊" value="门诊陪诊" />
                <el-option label="住院陪护" value="住院陪护" />
                <el-option label="检查陪同" value="检查陪同" />
                <el-option label="取药送药" value="取药送药" />
                <el-option label="夜间陪护" value="夜间陪护" />
                <el-option label="康复陪同" value="康复陪同" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="服务日期" prop="serviceDate">
              <el-date-picker
                v-model="form.serviceDate"
                type="datetime"
                placeholder="请选择服务日期时间"
                value-format="yyyy-MM-dd HH:mm:ss"
                style="width:100%"
              />
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="24">
          <el-col :span="12">
            <el-form-item label="医院名称" prop="hospitalName">
              <el-input v-model="form.hospitalName" placeholder="如：北京市第一人民医院" clearable />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="科室" prop="department">
              <el-input v-model="form.department" placeholder="如：心内科" clearable />
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="24">
          <el-col :span="12">
            <el-form-item label="联系人" prop="contactName">
              <el-input v-model="form.contactName" placeholder="请输入联系人姓名" clearable />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="联系电话" prop="contactPhone">
              <el-input v-model="form.contactPhone" placeholder="请输入联系电话" maxlength="11" clearable />
            </el-form-item>
          </el-col>
        </el-row>

        <el-form-item label="需要接送">
          <el-switch v-model="form.needPickup" active-text="需要陪诊师上门接送" inactive-text="自己前往医院" />
        </el-form-item>

        <el-form-item label="预算（元）" prop="budget">
          <el-input-number
            v-model="form.budget"
            :min="0"
            :max="99999"
            :precision="2"
            placeholder="请输入预算金额"
            style="width:280px"
          />
          <span class="form-hint">选填，设置预算有助于更快匹配</span>
        </el-form-item>

        <el-form-item label="需求内容" prop="requirement">
          <el-input
            v-model="form.requirement"
            type="textarea"
            :rows="5"
            placeholder="请详细描述您的陪诊需求，例如：需要陪老人做心脏彩超检查，帮忙排队挂号、取报告..."
            maxlength="2000"
            show-word-limit
          />
        </el-form-item>

        <el-form-item label="特殊说明">
          <el-input
            v-model="form.specialNotes"
            type="textarea"
            :rows="3"
            placeholder="选填：如有特殊要求请在此说明，如老人行动不便需轮椅、需要方言沟通等"
            maxlength="1000"
            show-word-limit
          />
        </el-form-item>

        <el-form-item label="AI需求摘要">
          <div class="ai-summary-tools">
            <el-button icon="el-icon-magic-stick" :loading="summaryGenerating" @click="handleGenerateSummary">生成摘要</el-button>
            <span class="form-hint">生成后可修改，提交时将展示给陪诊师。</span>
          </div>
          <el-input v-if="form.aiSummary" v-model="form.aiSummary" type="textarea" :rows="3" maxlength="800" show-word-limit class="ai-summary-input" />
        </el-form-item>

        <el-form-item>
          <el-button
            type="primary"
            :loading="submitting"
            size="medium"
            round
            @click="handleSubmit"
          >
            {{ isOrderMode ? '确认创建订单' : '发布需求' }}
          </el-button>
          <el-button size="medium" round @click="$router.back()">
            取消
          </el-button>
        </el-form-item>
      </el-form>
    </div>
  </div>
</template>

<script>
import { createRequest } from '@/api/serviceRequest'
import { generateServiceSummary } from '@/api/ai'

export default {
  name: 'RequestCreate',
  data() {
    return {
      matchedRequest: null,
      form: {
        serviceType: '',
        serviceDate: '',
        hospitalName: '',
        department: '',
        contactName: '',
        contactPhone: '',
        budget: undefined,
        requirement: '',
        specialNotes: '',
        aiSummary: '',
        needPickup: false
      },
      rules: {
        serviceType: [
          { required: true, message: '请选择服务类型', trigger: 'change' }
        ],
        serviceDate: [
          { required: true, message: '请选择服务日期', trigger: 'change' }
        ],
        hospitalName: [
          { required: true, message: '请输入医院名称', trigger: 'blur' }
        ],
        department: [
          { required: true, message: '请输入科室', trigger: 'blur' }
        ],
        contactName: [
          { required: true, message: '请输入联系人', trigger: 'blur' }
        ],
        contactPhone: [
          { required: true, message: '请输入联系电话', trigger: 'blur' },
          { pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号', trigger: 'blur' }
        ],
        requirement: [
          { required: true, message: '请填写需求内容', trigger: 'blur' }
        ]
      },
      submitting: false,
      summaryGenerating: false
    }
  },
  computed: {
    isOrderMode() {
      return !!this.$route.query.requestId
    }
  },
  methods: {
    async handleGenerateSummary() {
      if (!this.form.requirement.trim()) {
        this.$message.warning('请先填写需求内容')
        return
      }
      this.summaryGenerating = true
      try {
        const res = await generateServiceSummary({
          serviceType: this.form.serviceType,
          serviceDate: this.form.serviceDate ? this.form.serviceDate.replace(' ', 'T') : '',
          hospitalName: this.form.hospitalName,
          department: this.form.department,
          text: this.form.requirement,
          specialNotes: this.form.specialNotes
        })
        const data = res.data || res
        this.form.aiSummary = data.summary || ''
        this.$message.success('摘要已生成')
      } catch {
        // Error feedback is handled by the shared request interceptor.
      } finally {
        this.summaryGenerating = false
      }
    },
    async handleSubmit() {
      try {
        await this.$refs.requestForm.validate()
      } catch {
        return
      }

      this.submitting = true
      try {
        const payload = { ...this.form }
        // 如果是从已匹配需求创建订单，带上 requestId
        if (this.isOrderMode) {
          payload.requestId = this.$route.query.requestId
        }
        await createRequest(payload)
        this.$message.success(this.isOrderMode ? '订单创建成功' : '需求发布成功')
        this.$router.push('/customer/requests')
      } catch {
        /* 错误已统一处理 */
      } finally {
        this.submitting = false
      }
    }
  }
}
</script>

<style scoped>
.request-create-page {
  padding: 24px 32px;
  max-width: 820px;
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
}

/* 匹配提示 */
.match-banner {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 12px 16px;
  background: rgba(122, 154, 126, 0.1);
  border-radius: var(--radius-sm);
  margin-bottom: 24px;
  font-size: 14px;
  color: var(--color-primary-dark);
}

/* 表单 */
.request-form .el-form-item {
  margin-bottom: 22px;
}

.request-form .el-form-item__label {
  color: var(--color-text-regular);
  font-weight: 500;
}

.request-form .el-input__inner,
.request-form .el-textarea__inner {
  border-radius: var(--radius-sm);
  border-color: var(--color-border);
  font-size: 14px;
}

.request-form .el-input__inner:focus,
.request-form .el-textarea__inner:focus {
  border-color: var(--color-primary);
}

.form-hint {
  margin-left: 12px;
  font-size: 13px;
  color: var(--color-text-placeholder);
}
.ai-summary-tools { display: flex; align-items: center; }
.ai-summary-input { margin-top: 10px; }
</style>
