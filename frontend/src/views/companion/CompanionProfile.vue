<template>
  <div class="companion-profile-page">
    <h2 class="page-title">入驻资料</h2>

    <div class="content-card">
      <!-- 审核状态提示 -->
      <div v-if="profile.auditStatus !== undefined" class="audit-banner" :class="auditClass">
        <i :class="auditIcon"></i>
        <span>{{ auditText }}</span>
      </div>

      <el-form
        ref="profileForm"
        :model="form"
        :rules="rules"
        label-width="100px"
        class="profile-form"
        size="medium"
        :disabled="submitted && profile.auditStatus === 0"
      >
        <el-row :gutter="24">
          <el-col :span="12">
            <el-form-item label="真实姓名" prop="realName">
              <el-input v-model="form.realName" placeholder="请输入真实姓名" clearable />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="经验年限" prop="experienceYears">
              <el-input-number
                v-model="form.experienceYears"
                :min="0"
                :max="50"
                placeholder="0"
                style="width:100%"
              />
            </el-form-item>
          </el-col>
        </el-row>

        <el-form-item label="头像" prop="avatar">
          <el-input v-model="form.avatar" placeholder="请输入头像图片URL" clearable>
            <template slot="prepend">URL</template>
          </el-input>
        </el-form-item>

        <el-form-item label="服务区域" prop="serviceArea">
          <el-input v-model="form.serviceArea" placeholder="如：北京市朝阳区、海淀区" clearable />
        </el-form-item>

        <el-form-item label="服务类型" prop="serviceTypes">
          <el-checkbox-group v-model="form.serviceTypes">
            <el-checkbox label="门诊陪诊">门诊陪诊</el-checkbox>
            <el-checkbox label="住院陪护">住院陪护</el-checkbox>
            <el-checkbox label="检查陪同">检查陪同</el-checkbox>
            <el-checkbox label="取药送药">取药送药</el-checkbox>
            <el-checkbox label="夜间陪护">夜间陪护</el-checkbox>
          </el-checkbox-group>
        </el-form-item>

        <el-form-item label="个人介绍" prop="introduction">
          <el-input
            v-model="form.introduction"
            type="textarea"
            :rows="4"
            placeholder="请介绍您的陪诊经验和专业特长"
            maxlength="1000"
            show-word-limit
          />
        </el-form-item>

        <el-form-item>
          <el-button
            type="primary"
            :loading="saving"
            size="medium"
            round
            :disabled="submitted && profile.auditStatus === 0"
            @click="handleSubmit"
          >
            {{ profile.auditStatus === 1 ? '更新资料' : '提交审核' }}
          </el-button>
          <el-button v-if="profile.auditStatus === 2" type="warning" size="medium" round @click="handleSubmit">
            重新提交
          </el-button>
        </el-form-item>
      </el-form>
    </div>
  </div>
</template>

<script>
import { saveCompanionProfile, getMyProfile } from '@/api/companion'

const AUDIT_CONFIG = {
  0: { cls: 'audit--pending', icon: 'el-icon-time', text: '资料审核中，暂时无法修改' },
  1: { cls: 'audit--passed', icon: 'el-icon-success', text: '审核已通过' },
  2: { cls: 'audit--rejected', icon: 'el-icon-error', text: '审核未通过，请修改后重新提交' }
}

export default {
  name: 'CompanionProfile',
  data() {
    return {
      profile: {},
      submitted: false,
      form: {
        realName: '',
        avatar: '',
        introduction: '',
        serviceArea: '',
        serviceTypes: [],
        experienceYears: 0
      },
      rules: {
        realName: [
          { required: true, message: '请输入真实姓名', trigger: 'blur' }
        ],
        serviceArea: [
          { required: true, message: '请输入服务区域', trigger: 'blur' }
        ]
      },
      saving: false
    }
  },
  computed: {
    auditClass() {
      return AUDIT_CONFIG[this.profile.auditStatus]?.cls || ''
    },
    auditIcon() {
      return AUDIT_CONFIG[this.profile.auditStatus]?.icon || ''
    },
    auditText() {
      return AUDIT_CONFIG[this.profile.auditStatus]?.text || ''
    }
  },
  created() {
    this.fetchProfile()
  },
  methods: {
    async fetchProfile() {
      try {
        const res = await getMyProfile()
        const data = res.data || res
        if (data && data.realName) {
          this.profile = data
          this.submitted = true
          this.form = {
            realName: data.realName || '',
            avatar: data.avatar || '',
            introduction: data.introduction || '',
            serviceArea: data.serviceArea || '',
            serviceTypes: data.serviceTypes ? data.serviceTypes.split(',') : [],
            experienceYears: data.experienceYears || 0
          }
        }
      } catch {
        // 未入驻，使用空表单
      }
    },
    async handleSubmit() {
      try {
        await this.$refs.profileForm.validate()
      } catch {
        return
      }

      this.saving = true
      try {
        await saveCompanionProfile({
          ...this.form,
          serviceTypes: this.form.serviceTypes.join(',')
        })
        this.$message.success('提交成功')
        this.submitted = true
        this.fetchProfile()
      } catch {
        /* 错误已统一处理 */
      } finally {
        this.saving = false
      }
    }
  }
}
</script>

<style scoped>
.companion-profile-page {
  padding: 24px 32px;
  max-width: 780px;
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

/* 审核状态 */
.audit-banner {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 12px 16px;
  border-radius: var(--radius-sm);
  margin-bottom: 24px;
  font-size: 14px;
  font-weight: 500;
}

.audit--pending {
  background: rgba(230, 162, 60, 0.08);
  color: #B88230;
}

.audit--passed {
  background: rgba(122, 154, 126, 0.1);
  color: #5C7A60;
}

.audit--rejected {
  background: rgba(224, 96, 96, 0.08);
  color: #C05050;
}

/* 表单 */
.profile-form .el-form-item__label {
  color: var(--color-text-regular);
  font-weight: 500;
}

.profile-form .el-input__inner,
.profile-form .el-textarea__inner {
  border-radius: var(--radius-sm);
  border-color: var(--color-border);
}
</style>
