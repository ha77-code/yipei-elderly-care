<template>
  <div class="companions-page">
    <div class="page-header">
      <div>
        <h2 class="page-title">陪诊师列表</h2>
        <p class="page-subtitle">选择专业、可信赖的陪诊师，守护每一次就医</p>
      </div>
    </div>

    <div class="filter-bar">
      <el-input v-model="filterArea" placeholder="筛选服务区域" prefix-icon="el-icon-location-outline" clearable size="medium" style="width:180px" @change="fetchList" />
      <el-select v-model="filterType" placeholder="服务类型" clearable size="medium" style="width:160px" @change="fetchList">
        <el-option label="门诊陪诊" value="门诊陪诊" />
        <el-option label="住院陪护" value="住院陪护" />
        <el-option label="检查陪同" value="检查陪同" />
        <el-option label="取药送药" value="取药送药" />
        <el-option label="康复陪同" value="康复陪同" />
      </el-select>
      <span class="filter-count" v-if="list.length">共 {{ list.length }} 位陪诊师</span>
    </div>

    <div class="companion-grid" v-loading="loading">
      <div v-for="item in list" :key="item.id" class="companion-card" @click="goDetail(item)">
        <div class="card-top">
          <el-avatar :size="56" :src="item.avatar" icon="el-icon-user-solid" class="card-avatar" />
          <div class="card-badge" v-if="item.completedCount >= 10">
            <i class="el-icon-medal"></i>
          </div>
        </div>
        <div class="card-body">
          <h3 class="card-name">{{ item.realName }}</h3>
          <div class="card-rating">
            <span class="rating-stars">{{ '★'.repeat(Math.round(item.rating || 0)) }}{{ '☆'.repeat(5 - Math.round(item.rating || 0)) }}</span>
            <span class="rating-num">{{ item.rating || '0.0' }}</span>
          </div>
          <p class="card-intro">{{ item.introduction || '暂无介绍' }}</p>
          <div class="card-tags">
            <span v-for="t in parseServiceTypes(item.serviceTypes).slice(0, 3)" :key="t" class="service-tag">{{ t }}</span>
          </div>
          <div class="card-footer">
            <span class="meta-item"><i class="el-icon-location-outline"></i>{{ item.serviceArea || '未设置' }}</span>
            <span class="meta-item"><i class="el-icon-s-order"></i>{{ item.completedCount || 0 }}单</span>
            <span class="meta-item"><i class="el-icon-time"></i>{{ item.experienceYears || 0 }}年</span>
          </div>
        </div>
      </div>

      <div class="empty-state" v-if="!loading && list.length === 0">
        <i class="el-icon-s-custom"></i>
        <p>暂无匹配的陪诊师</p>
      </div>
    </div>

    <div class="table-footer" v-if="total > pageSize">
      <el-pagination background layout="prev, pager, next" :total="total" :page-size="pageSize" :current-page.sync="currentPage" @current-change="fetchList" />
    </div>
  </div>
</template>

<script>
import { getCompanionList } from '@/api/companion'

export default {
  name: 'Companions',
  data() {
    return {
      list: [],
      loading: false,
      currentPage: 1,
      pageSize: 12,
      total: 0,
      filterArea: '',
      filterType: ''
    }
  },
  created() {
    this.fetchList()
  },
  methods: {
    async fetchList() {
      this.loading = true
      try {
        const res = await getCompanionList({
          page: this.currentPage,
          size: this.pageSize,
          serviceArea: this.filterArea || undefined,
          serviceType: this.filterType || undefined
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
    goDetail(item) {
      this.$router.push(`/companion/${item.id}`)
    }
  }
}
</script>

<style scoped>
.companions-page { padding: 24px 32px; }
.page-header { margin-bottom: 20px; }
.page-title { font-size: 22px; font-weight: 700; color: var(--brand-cream-100); margin: 0 0 4px; }
.page-subtitle { font-size: 14px; color: rgba(220,200,170,0.65); margin: 0; }

.filter-bar { display: flex; align-items: center; gap: 12px; margin-bottom: 20px; padding: 14px 18px; background: rgba(255,248,238,0.06); border: 1px solid rgba(230,200,160,0.08); border-radius: 12px; }
.filter-count { margin-left: auto; font-size: 13px; color: rgba(220,200,170,0.6); font-weight: 500; }

.companion-grid { display: grid; grid-template-columns: repeat(auto-fill, minmax(280px, 1fr)); gap: 18px; min-height: 200px; }

.companion-card { position: relative; background: rgba(255,248,238,0.94); border: 1px solid rgba(230,200,160,0.1); border-radius: 14px; padding: 24px 20px 20px; cursor: pointer; transition: all 0.28s var(--ease-standard); box-shadow: 0 4px 20px rgba(7,16,7,0.1); display: flex; flex-direction: column; }
.companion-card:hover { transform: translateY(-4px); border-color: rgba(122,154,126,0.3); box-shadow: 0 12px 36px rgba(7,16,7,0.18); }

.card-top { display: flex; align-items: center; gap: 12px; margin-bottom: 14px; position: relative; }
.card-avatar { background: linear-gradient(135deg, #a3bfa6, #7a9a7e); flex-shrink: 0; }
.card-badge { position: absolute; top: -4px; right: -4px; width: 28px; height: 28px; display: flex; align-items: center; justify-content: center; background: linear-gradient(135deg, #E6A23C, #d4922a); border-radius: 50%; color: #fff; font-size: 14px; box-shadow: 0 2px 8px rgba(230,162,60,0.4); }

.card-body { flex: 1; display: flex; flex-direction: column; }
.card-name { font-size: 17px; font-weight: 700; color: #1a1a1a; margin: 0 0 6px; }
.card-rating { display: flex; align-items: center; gap: 6px; margin-bottom: 10px; }
.rating-stars { color: #E6A23C; font-size: 13px; letter-spacing: 1px; }
.rating-num { font-size: 13px; font-weight: 600; color: #8b7355; }
.card-intro { font-size: 13px; color: #555; margin: 0 0 12px; display: -webkit-box; -webkit-line-clamp: 2; -webkit-box-orient: vertical; overflow: hidden; line-height: 1.6; flex: 1; }
.card-tags { display: flex; flex-wrap: wrap; gap: 6px; margin-bottom: 14px; }
.service-tag { display: inline-block; padding: 3px 10px; background: rgba(122,154,126,0.08); border-radius: 20px; font-size: 12px; color: #3d6b42; font-weight: 500; }
.card-footer { display: flex; flex-wrap: wrap; gap: 10px; padding-top: 12px; border-top: 1px solid rgba(0,0,0,0.05); }
.meta-item { font-size: 12px; color: #888; display: flex; align-items: center; gap: 3px; }
.meta-item i { font-size: 13px; color: #aaa; }

.empty-state { grid-column: 1 / -1; text-align: center; padding: 80px 0; }
.empty-state i { font-size: 48px; color: rgba(220,200,170,0.3); margin-bottom: 16px; display: block; }
.empty-state p { font-size: 15px; color: rgba(220,200,170,0.5); margin: 0; }

.table-footer { display: flex; justify-content: center; margin-top: 28px; }
</style>
