<template>
  <div class="companion-detail-page">
    <div class="back-link" @click="$router.back()">
      <i class="el-icon-arrow-left"></i> 返回列表
    </div>

    <div class="detail-card" v-loading="loading">
      <template v-if="profile">
        <!-- 头部信息 -->
        <div class="detail-header">
          <el-avatar :size="80" :src="profile.avatar" icon="el-icon-user-solid" />
          <div class="header-info">
            <h2 class="header-name">{{ profile.realName }}
              <TtsPlayer :text="ttsText" />
            </h2>
            <div class="header-rating">
              <i class="el-icon-star-on"></i>
              <span>{{ profile.rating || '0.0' }} 分</span>
              <span class="header-divider">|</span>
              <span>已完成 {{ profile.completedCount || 0 }} 单</span>
            </div>
            <el-tag
              :type="profile.auditStatus === 1 ? 'success' : 'warning'"
              size="small"
            >{{ profile.auditStatus === 1 ? '已认证' : '审核中' }}</el-tag>
          </div>
        </div>

        <!-- 详细信息 -->
        <div class="detail-section">
          <h3 class="section-title">基本信息</h3>
          <div class="info-grid">
            <div class="info-item">
              <span class="info-label">服务区域</span>
              <span class="info-value">{{ profile.serviceArea || '未设置' }}</span>
            </div>
            <div class="info-item">
              <span class="info-label">经验年限</span>
              <span class="info-value">{{ profile.experienceYears || 0 }} 年</span>
            </div>
            <div class="info-item info-full">
              <span class="info-label">服务类型</span>
              <span class="info-value">
                <el-tag
                  v-for="t in serviceTypeList"
                  :key="t"
                  size="small"
                  class="type-tag"
                >{{ t }}</el-tag>
                <span v-if="serviceTypeList.length === 0">未设置</span>
              </span>
            </div>
          </div>
        </div>

        <!-- 个人介绍 -->
        <div class="detail-section">
          <h3 class="section-title">个人介绍</h3>
          <p class="introduction">{{ profile.introduction || '暂无介绍' }}</p>
        </div>

        <!-- 操作按钮 -->
        <div class="detail-actions" v-if="profile.auditStatus === 1">
          <el-button v-if="canCreateOrder" type="primary" size="medium" round @click="goCreateOrder">
            选择该陪诊师
          </el-button>
          <el-button size="medium" round @click="$router.back()">返回</el-button>
        </div>
      </template>

      <!-- 未找到 -->
      <div class="empty-state" v-if="!loading && !profile">
        <i class="el-icon-user"></i>
        <p>陪诊师信息不存在</p>
        <el-button size="medium" round @click="$router.back()">返回列表</el-button>
      </div>
    </div>
  </div>
</template>

<script>
import { getCompanionDetail } from '@/api/companion'
import { getUserRole, ROLES } from '@/utils/auth'
import TtsPlayer from '@/components/TtsPlayer.vue'

export default {
  name: 'CompanionDetail',
  components: { TtsPlayer },
  data() {
    return {
      profile: null,
      loading: false
    }
  },
  computed: {
    serviceTypeList() {
      if (!this.profile || !this.profile.serviceTypes) return []
      return typeof this.profile.serviceTypes === 'string'
        ? this.profile.serviceTypes.split(',')
        : this.profile.serviceTypes
    },
    canCreateOrder() {
      return getUserRole() === ROLES.CUSTOMER
    },
    ttsText() {
      if (!this.profile) return ''
      const p = this.profile
      const types = this.serviceTypeList.join(' ')
      let text = p.realName + ' '
      text += '服务区域' + (p.serviceArea || '未设置') + ' '
      text += '经验' + (p.experienceYears || 0) + '年 '
      text += '擅长' + (types || '未设置') + ' '
      if (p.introduction) {
        text += '个人介绍 ' + p.introduction
      }
      return text
    }
  },
  created() {
    this.fetchDetail()
  },
  methods: {
    async fetchDetail() {
      const id = this.$route.params.id
      if (!id) return

      this.loading = true
      try {
        const res = await getCompanionDetail(id)
        this.profile = res.data || res
      } catch {
        this.profile = null
      } finally {
        this.loading = false
      }
    },
    goCreateOrder() {
      if (!this.canCreateOrder) return
      this.$router.push('/customer/request/create')
    }
  }
}
</script>

<style scoped>
.companion-detail-page {
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
  margin-bottom: 20px;
  font-weight: 500;
}

.back-link:hover {
  color: var(--color-primary-dark);
}

/* 卡片 */
.detail-card {
  background: #FFFFFF;
  border: 1px solid var(--color-border-light);
  border-radius: var(--radius-lg);
  padding: 36px;
  box-shadow: var(--shadow-sm);
  min-height: 300px;
}

/* 头部 */
.detail-header {
  display: flex;
  align-items: center;
  gap: 20px;
  padding-bottom: 24px;
  border-bottom: 1px solid var(--color-border-light);
}

.detail-header .el-avatar {
  background: var(--color-primary-light);
  flex-shrink: 0;
}

.header-name {
  font-family: var(--font-family);
  font-size: 22px;
  font-weight: 700;
  color: var(--color-text-primary);
  margin: 0 0 6px;
}

.header-rating {
  display: flex;
  align-items: center;
  gap: 6px;
  font-size: 14px;
  color: var(--color-text-secondary);
  margin-bottom: 8px;
}

.header-rating i {
  color: #E6A23C;
}

.header-divider {
  color: var(--color-border);
}

/* 区块 */
.detail-section {
  padding-top: 24px;
}

.section-title {
  font-family: var(--font-family);
  font-size: 16px;
  font-weight: 600;
  color: var(--color-text-primary);
  margin: 0 0 16px;
}

/* 信息网格 */
.info-grid {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 16px 24px;
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

.type-tag {
  margin-right: 6px;
  margin-bottom: 4px;
  background: rgba(122, 154, 126, 0.08);
  border-color: rgba(122, 154, 126, 0.15);
  color: var(--color-primary-dark);
}

/* 介绍 */
.introduction {
  font-size: 15px;
  color: var(--color-text-regular);
  line-height: 1.8;
  margin: 0;
  white-space: pre-wrap;
}

/* 操作 */
.detail-actions {
  margin-top: 32px;
  padding-top: 24px;
  border-top: 1px solid var(--color-border-light);
  display: flex;
  gap: 12px;
}

/* 空状态 */
.empty-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 80px 0;
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
  .companion-detail-page { padding: 16px; }
  .detail-card { padding: 20px; }
  .detail-header { flex-direction: column; text-align: center; }
  .info-grid { grid-template-columns: 1fr; }
}
</style>
