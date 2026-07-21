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

    <!-- 推荐陪诊师弹窗 -->
    <el-dialog
      title="AI 已生成需求摘要 — 为您推荐以下陪诊师"
      :visible.sync="recommendVisible"
      width="720px"
      :close-on-click-modal="false"
      append-to-body
      class="recommend-dialog"
    >
      <div class="recommend-summary" v-if="form.aiSummary">
        <span class="recommend-summary-label">需求摘要</span>
        <p>{{ form.aiSummary }}</p>
      </div>

      <div class="recommend-budget-tip" v-if="!(form.budget > 0)">
        <i class="el-icon-warning-outline"></i>
        指定陪诊师需先设置预算：请关闭本弹窗，在表单「预算」处填写大于 0 的金额后再选择。
      </div>

      <div class="recommend-grid" v-loading="recommendLoading">
        <div v-if="!recommendLoading && !companions.length" class="recommend-empty">
          暂未匹配到合适的陪诊师，您可以直接发布需求，陪诊师稍后申请
        </div>
        <div
          v-for="c in companions"
          :key="c.id"
          :class="['recommend-card', { selected: selectedCompanion === c.id }]"
          @click="selectedCompanion = c.id"
        >
          <div class="rc-header">
            <el-avatar :size="44" :src="c.avatar || undefined" icon="el-icon-user-solid" />
            <div class="rc-info">
              <span class="rc-name">{{ c.realName || c.nickname || '陪诊师' }}</span>
              <span class="rc-rating">⭐ {{ c.rating || '4.5' }} · {{ c.completedCount || 0 }}单</span>
            </div>
            <el-tag size="mini" :type="selectedCompanion === c.id ? 'success' : 'info'" effect="plain">
              {{ selectedCompanion === c.id ? '已选择' : '可选' }}
            </el-tag>
          </div>
          <div class="rc-body">
            <div class="rc-row" v-if="c.serviceTypes"><i class="el-icon-s-marketing"></i> {{ c.serviceTypes }}</div>
            <div class="rc-row" v-if="c.serviceArea"><i class="el-icon-location-outline"></i> {{ c.serviceArea }}</div>
            <div class="rc-row" v-if="c.experienceYears"><i class="el-icon-medal"></i> {{ c.experienceYears }}年经验</div>
            <div class="rc-traits" v-if="c.traits">
              <span v-for="t in String(c.traits).split(',')" :key="t" class="trait-tag">{{ t.trim() }}</span>
            </div>
          </div>
        </div>
      </div>

      <span slot="footer" class="dialog-footer">
        <el-button @click="recommendVisible = false">暂不选择，直接发布</el-button>
        <el-button type="primary" :disabled="!selectedCompanion" @click="selectAndCreate">
          指定该陪诊师并提交需求
        </el-button>
      </span>
    </el-dialog>
  </div>
</template>

<script>
import { createRequest } from '@/api/serviceRequest'
import { generateServiceSummary } from '@/api/ai'
import { getCompanionList } from '@/api/companion'

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
      recommendVisible: false,
      recommendLoading: false,
      companions: [],
      selectedCompanion: null
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
        this.$message.success('摘要已生成，正在匹配陪诊师...')
        await this.fetchCompanions()
      } catch {
        // Error feedback is handled by the shared request interceptor.
      } finally {
        this.summaryGenerating = false
      }
    },
    async fetchCompanions() {
      this.recommendLoading = true
      this.recommendVisible = true
      this.selectedCompanion = null
      try {
        const res = await getCompanionList({
          serviceType: this.form.serviceType || undefined,
          traits: this.form.specialNotes || undefined
        })
        this.companions = (res.data || res || []).filter(c => c.auditStatus === 1).slice(0, 6)
      } catch {
        this.companions = []
      } finally {
        this.recommendLoading = false
      }
    },
    // 从推荐弹窗选定某陪诊师 → 作为指定需求提交（审核通过后后端自动生成待接单订单）
    selectAndCreate() {
      const cid = this.selectedCompanion
      if (!cid) return
      if (!(this.form.budget > 0)) {
        this.$message.warning('指定陪诊师时请先填写大于 0 的预算')
        return
      }
      this.recommendVisible = false
      this.submitDirected(cid)
    },
    async handleSubmit() {
      try {
        await this.$refs.requestForm.validate()
      } catch {
        return
      }

      // 指定下单模式（从陪诊师详情/直接带 companionId 进入）
      if (this.isOrderMode) {
        if (!(this.form.budget > 0)) {
          this.$message.warning('指定陪诊师时请填写大于 0 的预算')
          return
        }
        this.submitDirected(Number(this.$route.query.companionId))
        return
      }

      // 普通公开发布
      this.submitting = true
      try {
        await createRequest({ ...this.form })
        this.$message.success('需求发布成功')
        this.$router.push('/customer/requests')
      } catch {
        /* 错误已统一处理 */
      } finally {
        this.submitting = false
      }
    },
    // 提交带指定陪诊师的需求：不直接建单，管理员审核通过后后端自动生成待接单订单
    async submitDirected(companionId) {
      try {
        await this.$refs.requestForm.validate()
      } catch {
        return
      }
      this.submitting = true
      try {
        await createRequest({ ...this.form, preferredCompanionId: companionId })
        this.$message.success('指定需求已提交，待管理员审核通过后将自动向该陪诊师发送订单')
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
.ai-summary-tools { display: flex; align-items: center; }
.ai-summary-input { margin-top: 10px; }

/* ═══ 推荐陪诊师弹窗 ═══ */
.recommend-summary {
  background: rgba(108,140,80,0.06); border-left: 3px solid rgba(108,140,80,0.5);
  border-radius: 8px; padding: 12px 16px; margin-bottom: 20px;
}
.recommend-summary-label { font-size: 12px; font-weight: 700; color: rgba(78,106,56,0.8); }
.recommend-summary p { margin: 6px 0 0; font-size: 13px; color: rgba(46,60,38,0.8); line-height: 1.6; }
.recommend-budget-tip { display: flex; align-items: center; gap: 6px; margin-bottom: 16px; padding: 10px 14px; background: rgba(230,162,60,0.1); border-radius: 8px; font-size: 13px; color: #B88230; }
.recommend-empty { text-align: center; color: rgba(130,140,116,0.7); padding: 40px 16px; font-size: 14px; }
.recommend-grid { display: grid; grid-template-columns: repeat(2, 1fr); gap: 12px; min-height: 200px; }
.recommend-card {
  border: 2px solid rgba(150,140,110,0.12); background: rgba(255,255,255,0.4);
  border-radius: 12px; padding: 16px; cursor: pointer;
  transition: all 0.25s ease;
}
.recommend-card:hover { border-color: rgba(180,215,115,0.35); box-shadow: 0 4px 16px -6px rgba(60,70,40,0.15); }
.recommend-card.selected { border-color: rgba(108,140,80,0.55); background: rgba(108,140,80,0.06); box-shadow: 0 0 0 3px rgba(108,140,80,0.1); }
.rc-header { display: flex; align-items: center; gap: 12px; margin-bottom: 12px; }
.rc-info { flex: 1; min-width: 0; }
.rc-name { display: block; font-size: 15px; font-weight: 700; color: rgba(46,60,38,0.9); }
.rc-rating { font-size: 12px; color: rgba(170,130,60,0.8); margin-top: 2px; display: block; }
.rc-body { display: flex; flex-direction: column; gap: 6px; }
.rc-row { font-size: 13px; color: rgba(96,110,82,0.75); display: flex; align-items: center; gap: 6px; }
.rc-row i { color: rgba(108,140,80,0.55); font-size: 14px; flex-shrink: 0; }
.rc-traits { display: flex; flex-wrap: wrap; gap: 6px; margin-top: 4px; }
.trait-tag {
  display: inline-block; padding: 2px 10px; border-radius: 20px;
  font-size: 11px; font-weight: 500;
  background: rgba(108,140,80,0.08); color: rgba(78,106,56,0.8);
  border: 1px solid rgba(108,140,80,0.15);
}
</style>
