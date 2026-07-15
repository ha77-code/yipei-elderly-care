<template>
  <div class="companions-page">
    <h2 class="page-title">陪诊师列表</h2>

    <!-- 卡片网格 -->
    <div class="companion-grid" v-loading="loading">
      <div
        v-for="item in list"
        :key="item.id"
        class="companion-card"
        @click="goDetail(item)"
      >
        <div class="card-avatar">
          <el-avatar :size="64" :src="item.avatar" icon="el-icon-user-solid" />
        </div>
        <div class="card-body">
          <div class="card-header">
            <h3 class="card-name">{{ item.realName }}</h3>
            <div class="card-rating">
              <i class="el-icon-star-on"></i>
              <span>{{ item.rating || '0.0' }}</span>
            </div>
          </div>
          <p class="card-intro">{{ item.introduction || '暂无介绍' }}</p>
          <div class="card-tags">
            <el-tag
              v-for="t in parseServiceTypes(item.serviceTypes)"
              :key="t"
              size="mini"
              class="service-tag"
            >{{ t }}</el-tag>
          </div>
          <div class="card-meta">
            <span><i class="el-icon-location-outline"></i> {{ item.serviceArea || '未设置' }}</span>
            <span><i class="el-icon-s-order"></i> 已完成 {{ item.completedCount || 0 }} 单</span>
            <span><i class="el-icon-data-line"></i> {{ item.experienceYears || 0 }} 年经验</span>
          </div>
        </div>
      </div>

      <div class="empty-state" v-if="!loading && list.length === 0">
        <i class="el-icon-s-custom"></i>
        <p>暂无陪诊师</p>
      </div>
    </div>

    <!-- 分页 -->
    <div class="table-footer" v-if="total > pageSize">
      <el-pagination
        background
        layout="prev, pager, next"
        :total="total"
        :page-size="pageSize"
        :current-page.sync="currentPage"
        @current-change="fetchList"
      />
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
      total: 0
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
    goDetail(item) {
      this.$router.push(`/companion/${item.id}`)
    }
  }
}
</script>

<style scoped>
.companions-page {
  padding: 24px 32px;
}

.page-title {
  font-family: var(--font-family);
  font-size: 20px;
  font-weight: 700;
  color: var(--color-text-primary);
  margin: 0 0 20px;
}

/* 卡片网格 */
.companion-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(340px, 1fr));
  gap: 16px;
  min-height: 200px;
}

.companion-card {
  display: flex;
  gap: 16px;
  background: #FFFFFF;
  border: 1px solid var(--color-border-light);
  border-radius: var(--radius-md);
  padding: 20px;
  cursor: pointer;
  transition: all 0.2s ease;
  box-shadow: var(--shadow-sm);
}

.companion-card:hover {
  border-color: var(--color-primary-light);
  transform: translateY(-2px);
  box-shadow: var(--shadow-md);
}

.card-avatar {
  flex-shrink: 0;
}

.card-avatar .el-avatar {
  background: var(--color-primary-light);
}

.card-body {
  flex: 1;
  min-width: 0;
}

.card-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 6px;
}

.card-name {
  font-family: var(--font-family);
  font-size: 17px;
  font-weight: 600;
  color: var(--color-text-primary);
  margin: 0;
}

.card-rating {
  display: flex;
  align-items: center;
  gap: 4px;
  font-size: 14px;
  font-weight: 600;
  color: #E6A23C;
}

.card-rating i {
  font-size: 14px;
}

.card-intro {
  font-size: 13px;
  color: var(--color-text-secondary);
  margin: 0 0 10px;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.card-tags {
  display: flex;
  flex-wrap: wrap;
  gap: 6px;
  margin-bottom: 10px;
}

.service-tag {
  background: rgba(122, 154, 126, 0.08);
  border-color: rgba(122, 154, 126, 0.15);
  color: var(--color-primary-dark);
}

.card-meta {
  display: flex;
  flex-wrap: wrap;
  gap: 12px;
  font-size: 12px;
  color: var(--color-text-placeholder);
}

.card-meta i {
  margin-right: 2px;
}

/* 空状态 */
.empty-state {
  grid-column: 1 / -1;
  text-align: center;
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
  margin: 0;
}

/* 分页 */
.table-footer {
  display: flex;
  justify-content: center;
  margin-top: 24px;
}
</style>
