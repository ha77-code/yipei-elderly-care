<template>
  <div class="page-wrap">
    <h2 class="page-title">我的评价</h2>
    <div class="content-card">
      <div class="eval-list" v-loading="loading">
        <div v-for="item in list" :key="item.id" class="eval-item">
          <div class="eval-header">
            <div class="eval-score">
              <span class="stars">{{ '★'.repeat(item.score) }}{{ '☆'.repeat(5 - item.score) }}</span>
              <span class="score-num">{{ item.score }}.0</span>
            </div>
            <span class="eval-time">{{ fmt(item.createdAt || item.created_at) }}</span>
          </div>
          <p class="eval-content">{{ item.content || '用户未填写评价内容' }}</p>
          <div class="eval-meta">
            <span>订单 #{{ item.orderId }}</span>
          </div>
        </div>
        <div class="empty-state" v-if="!loading && list.length === 0">
          <i class="el-icon-chat-dot-round"></i>
          <p>暂无收到的评价</p>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import { getCompanionEvaluations } from '@/api/evaluation'
import { getMyProfile } from '@/api/companion'

export default {
  name: 'MyEvaluations',
  data() { return { list: [], loading: false } },
  async created() { await this.fetchList() },
  methods: {
    async fetchList() {
      this.loading = true
      try {
        const profileRes = await getMyProfile()
        const profile = profileRes.data || profileRes
        if (!profile || !profile.id) { this.list = []; return }
        const res = await getCompanionEvaluations(profile.id)
        this.list = res.data || res || []
      } catch { this.list = [] } finally { this.loading = false }
    },
    fmt(d) { if (!d) return '-'; return d.replace('T', ' ').substring(0, 16) }
  }
}
</script>

<style scoped>
.page-wrap { padding: 24px 32px; }
.page-title { font-size: 20px; font-weight: 700; color: var(--brand-cream-100); margin: 0 0 20px; }
.content-card { background: #fff; border: 1px solid var(--color-border-light); border-radius: var(--radius-md); padding: 20px; box-shadow: var(--shadow-sm); }
.eval-list { display: flex; flex-direction: column; gap: 16px; }
.eval-item { padding: 16px; border: 1px solid rgba(0,0,0,0.05); border-radius: 10px; background: #fafaf8; }
.eval-header { display: flex; align-items: center; justify-content: space-between; margin-bottom: 8px; }
.eval-score { display: flex; align-items: center; gap: 8px; }
.stars { color: #E6A23C; font-size: 15px; letter-spacing: 1px; }
.score-num { font-size: 14px; font-weight: 600; color: #8b7355; }
.eval-time { font-size: 12px; color: #999; }
.eval-content { font-size: 14px; color: #333; line-height: 1.7; margin: 0 0 8px; }
.eval-meta { font-size: 12px; color: #999; }
.empty-state { text-align: center; padding: 60px 0; }
.empty-state i { font-size: 40px; color: #ddd; display: block; margin-bottom: 12px; }
.empty-state p { font-size: 14px; color: #999; margin: 0; }
</style>
