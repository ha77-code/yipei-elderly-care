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

        <!-- 用户评价 -->
        <div class="detail-section" v-if="evaluations.length > 0">
          <h3 class="section-title">用户评价 ({{ evaluations.length }})</h3>
          <div class="eval-list">
            <div v-for="ev in evaluations.slice(0, 5)" :key="ev.id" class="eval-item">
              <div class="eval-header">
                <span class="eval-stars">{{ '★'.repeat(ev.score) }}{{ '☆'.repeat(5 - ev.score) }}</span>
                <span class="eval-time">{{ fmtDate(ev.createdAt || ev.created_at) }}</span>
              </div>
              <p class="eval-text">{{ ev.content || '未填写评价内容' }}</p>
            </div>
          </div>
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
import { getCompanionEvaluations } from '@/api/evaluation'
import { getUserRole, ROLES } from '@/utils/auth'
import TtsPlayer from '@/components/TtsPlayer.vue'

export default {
  name: 'CompanionDetail',
  components: { TtsPlayer },
  data() {
    return {
      profile: null,
      evaluations: [],
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
        this.fetchEvaluations(id)
      } catch {
        this.profile = null
      } finally {
        this.loading = false
      }
    },
    async fetchEvaluations(companionId) {
      try {
        const res = await getCompanionEvaluations(companionId)
        this.evaluations = res.data || res || []
      } catch { this.evaluations = [] }
    },
    fmtDate(d) { if (!d) return ''; return d.replace('T', ' ').substring(0, 10) },
    goCreateOrder() {
      if (!this.canCreateOrder) return
      this.$router.push({
        path: '/customer/request/create',
        query: { companionId: this.profile.id }
      })
    }
  }
}
</script>

<style scoped>
.companion-detail-page { padding: 24px 32px; max-width: 780px; }
.back-link { display: inline-flex; align-items: center; gap: 4px; font-size: 14px; color: rgba(220,200,170,0.7); cursor: pointer; margin-bottom: 20px; font-weight: 500; transition: color 0.2s; }
.back-link:hover { color: var(--brand-gold-420); }

.detail-card { background: rgba(255,248,238,0.94); border: 1px solid rgba(230,200,160,0.1); border-radius: 16px; padding: 0; box-shadow: 0 16px 48px rgba(7,16,7,0.16); min-height: 300px; overflow: hidden; }

.detail-header { display: flex; align-items: center; gap: 24px; padding: 36px 36px 28px; background: linear-gradient(135deg, rgba(122,154,126,0.06) 0%, rgba(196,168,130,0.04) 100%); border-bottom: 1px solid rgba(0,0,0,0.04); }
.detail-header .el-avatar { background: linear-gradient(135deg, #a3bfa6, #7a9a7e); flex-shrink: 0; box-shadow: 0 4px 16px rgba(122,154,126,0.3); }
.header-info { flex: 1; }
.header-name { font-size: 24px; font-weight: 700; color: #1a1a1a; margin: 0 0 8px; display: flex; align-items: center; gap: 10px; }
.header-rating { display: flex; align-items: center; gap: 8px; font-size: 14px; color: #666; margin-bottom: 10px; }
.header-rating i { color: #E6A23C; font-size: 16px; }
.header-divider { color: #ddd; }

.detail-section { padding: 28px 36px; }
.detail-section + .detail-section { border-top: 1px solid rgba(0,0,0,0.04); }
.section-title { font-size: 15px; font-weight: 700; color: #1a1a1a; margin: 0 0 16px; display: flex; align-items: center; gap: 8px; }
.section-title::before { content: ''; width: 3px; height: 16px; background: linear-gradient(180deg, #7a9a7e, #5c7a60); border-radius: 2px; }

.info-grid { display: grid; grid-template-columns: 1fr 1fr; gap: 18px 28px; }
.info-item { display: flex; flex-direction: column; gap: 4px; }
.info-full { grid-column: 1 / -1; }
.info-label { font-size: 12px; color: #999; font-weight: 600; text-transform: uppercase; letter-spacing: 0.04em; }
.info-value { font-size: 15px; color: #1a1a1a; font-weight: 500; }
.type-tag { margin-right: 8px; margin-bottom: 4px; background: rgba(122,154,126,0.08); border-color: rgba(122,154,126,0.15); color: #3d6b42; border-radius: 20px; }

.introduction { font-size: 15px; color: #444; line-height: 1.9; margin: 0; white-space: pre-wrap; padding: 16px 20px; background: rgba(122,154,126,0.04); border-radius: 10px; border-left: 3px solid rgba(122,154,126,0.3); }

.eval-list { display: flex; flex-direction: column; gap: 12px; }
.eval-item { padding: 12px 16px; background: #fafaf8; border-radius: 8px; border: 1px solid rgba(0,0,0,0.04); }
.eval-header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 6px; }
.eval-stars { color: #E6A23C; font-size: 13px; letter-spacing: 1px; }
.eval-time { font-size: 12px; color: #999; }
.eval-text { font-size: 13px; color: #555; margin: 0; line-height: 1.6; }

.detail-actions { padding: 24px 36px 32px; border-top: 1px solid rgba(0,0,0,0.04); display: flex; gap: 12px; }

.empty-state { display: flex; flex-direction: column; align-items: center; justify-content: center; padding: 80px 0; color: #999; }
.empty-state i { font-size: 48px; color: #ddd; margin-bottom: 16px; }
.empty-state p { font-size: 15px; margin: 0 0 20px; }

@media (max-width: 768px) { .companion-detail-page { padding: 16px; } .detail-header { flex-direction: column; text-align: center; padding: 28px 20px; } .detail-section { padding: 20px; } .info-grid { grid-template-columns: 1fr; } }
</style>
