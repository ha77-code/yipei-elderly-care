<template>
  <div class="request-create-page">
    <h2 class="page-title">{{ isOrderMode ? '创建订单' : '发布服务需求' }}</h2>

    <div class="content-card">
      <!-- 指定下单提示（从陪诊师详情/智能推荐指定 TA 跳转而来） -->
      <div class="match-banner" v-if="isOrderMode">
        <i class="el-icon-info"></i>
        <span>指定下单模式：填写需求并设置预算，提交后经管理员审核，通过即自动向该陪诊师发送订单。</span>
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

        <el-form-item :label="isOrderMode ? '预算（元）*' : '预算（元）'" prop="budget">
          <el-input-number
            v-model="form.budget"
            :min="0"
            :max="99999"
            :precision="2"
            placeholder="请输入预算金额"
            style="width:280px"
          />
          <span class="form-hint">{{ isOrderMode ? '指定下单必填，将作为订单服务金额' : '选填，设置预算有助于更快匹配' }}</span>
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
            <el-button v-if="!isOrderMode" icon="el-icon-star-off" :loading="recommending" @click="handleRecommend">智能推荐陪诊师</el-button>
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

    <!-- 智能推荐陪诊师 -->
    <el-dialog title="智能推荐陪诊师" :visible.sync="recommendVisible" width="640px">
      <p class="rec-tip">根据你填写的需求内容，为你推荐以下比较适配的陪诊师。可点「指定TA」直接前往创建指定订单。</p>
      <div v-if="!recommendList.length" class="rec-empty">暂时没有与当前需求高度匹配的陪诊师，可直接发布需求进入需求广场，由陪诊师主动申请。</div>
      <div v-for="c in recommendList" :key="c.id" class="rec-card">
        <div class="rec-head">
          <span class="rec-name">{{ c.nickname || c.realName || '陪诊师' }}</span>
          <el-tag size="small" type="warning" effect="plain">匹配 {{ c.matchScore || 0 }}</el-tag>
        </div>
        <div class="rec-meta">{{ c.serviceTypes || '陪诊服务' }} · {{ c.serviceArea || '服务区域待补充' }} · {{ c.experienceYears || 0 }}年 · ★{{ Number(c.rating || 0).toFixed(1) }}</div>
        <div class="rec-reason">推荐理由：{{ c.matchReason || '综合资历匹配' }}</div>
        <div class="rec-actions">
          <el-button size="mini" type="primary" round @click="pickCompanion(c)">指定TA</el-button>
        </div>
      </div>
      <span slot="footer"><el-button @click="recommendVisible = false">关闭</el-button></span>
    </el-dialog>
  </div>
</template>

<script>
import { createRequest } from '@/api/serviceRequest'
import { generateServiceSummary } from '@/api/ai'
import { recommendCompanions } from '@/api/serviceRequest'

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
      summaryGenerating: false,
      recommending: false,
      recommendVisible: false,
      recommendList: []
    }
  },
  computed: {
    isOrderMode() {
      return !!this.$route.query.companionId
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
    async handleRecommend() {
      if (!this.form.requirement.trim()) {
        this.$message.warning('请先填写需求内容，再看智能推荐')
        return
      }
      this.recommending = true
      try {
        const res = await recommendCompanions({
          serviceType: this.form.serviceType,
          hospitalName: this.form.hospitalName,
          department: this.form.department,
          requirement: this.form.requirement,
          specialNotes: this.form.specialNotes,
          preferredTraits: this.form.preferredTraits
        })
        this.recommendList = res.data || res || []
        this.recommendVisible = true
      } catch {
        // 错误由统一拦截器提示
      } finally {
        this.recommending = false
      }
    },
    pickCompanion(c) {
      this.recommendVisible = false
      this.$router.push({ path: '/customer/request/create', query: { companionId: c.id } })
    },
    async handleSubmit() {
      try {
        await this.$refs.requestForm.validate()
      } catch {
        return
      }

      if (this.isOrderMode && !(this.form.budget > 0)) {
        this.$message.warning('指定陪诊师时请填写大于 0 的预算')
        return
      }

      this.submitting = true
      try {
        const payload = { ...this.form }
        if (this.isOrderMode) {
          // 指定下单：提交带指定陪诊师的需求，管理员审核通过后后端自动生成待接单订单。
          payload.preferredCompanionId = Number(this.$route.query.companionId)
          await createRequest(payload)
          this.$message.success('指定需求已提交，待管理员审核通过后将自动向该陪诊师发送订单')
        } else {
          await createRequest(payload)
          this.$message.success('需求发布成功')
        }
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
  color: var(--brand-cream-100);
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
  color: #1a1a1a;
  font-weight: 600;
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
.ai-summary-tools { display: flex; align-items: center; flex-wrap: wrap; gap: 8px; }
.ai-summary-input { margin-top: 10px; }
.rec-tip { font-size: 13px; color: rgba(96,110,82,0.85); line-height: 1.7; margin: 0 0 12px; }
.rec-empty { font-size: 13px; color: rgba(130,140,116,0.8); padding: 20px; text-align: center; background: rgba(248,250,240,0.5); border-radius: 8px; }
.rec-card { border: 1px solid rgba(150,140,110,0.16); border-radius: 10px; padding: 14px 16px; margin-bottom: 12px; background: rgba(255,255,255,0.6); }
.rec-head { display: flex; align-items: center; justify-content: space-between; margin-bottom: 6px; }
.rec-name { font-size: 15px; font-weight: 700; color: rgba(78,106,56,0.92); }
.rec-meta { font-size: 12.5px; color: rgba(96,110,82,0.8); margin-bottom: 4px; }
.rec-reason { font-size: 12.5px; color: rgba(170,130,60,0.9); line-height: 1.6; margin-bottom: 10px; }
.rec-actions { text-align: right; }
</style>
