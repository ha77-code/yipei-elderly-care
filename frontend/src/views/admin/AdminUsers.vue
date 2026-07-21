<template>
  <div class="frosted-page">
    <h2 class="frosted-title">用户管理</h2>
    <div class="frosted-filter" style="display:flex;align-items:center;justify-content:space-between;margin-bottom:14px">
      <div style="display:flex;gap:12px">
        <el-select v-model="filterRole" placeholder="全部角色" clearable size="medium" @change="handleFilter">
          <el-option label="客户" value="CUSTOMER" />
          <el-option label="陪诊师" value="COMPANION" />
          <el-option label="管理员" value="ADMIN" />
        </el-select>
        <el-select v-model="filterStatus" placeholder="全部状态" clearable size="medium" @change="handleFilter">
          <el-option label="正常" :value="1" />
          <el-option label="已禁用" :value="0" />
        </el-select>
      </div>
      <el-button icon="el-icon-refresh" size="medium" round @click="resetFilter">重置</el-button>
    </div>
    <div class="frosted-card" style="padding:4px">
      <el-table :data="list" v-loading="loading" stripe class="warm-table" empty-text="暂无用户">
        <el-table-column prop="id" label="ID" width="80" align="center" />
        <el-table-column label="头像" width="70" align="center">
          <template slot-scope="{ row }">
            <el-avatar :size="36" :src="row.avatar || undefined" icon="el-icon-user-solid" />
          </template>
        </el-table-column>
        <el-table-column prop="username" label="用户名" min-width="110" />
        <el-table-column prop="nickname" label="昵称" min-width="100">
          <template slot-scope="{ row }">{{ row.nickname || '-' }}</template>
        </el-table-column>
        <el-table-column prop="phone" label="手机号" min-width="120">
          <template slot-scope="{ row }">{{ row.phone || '-' }}</template>
        </el-table-column>
        <el-table-column prop="role" label="角色" width="90" align="center">
          <template slot-scope="{ row }">
            <span :class="['role-tag', roleClass(row.role)]">{{ roleMap[row.role] || row.role }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="80" align="center">
          <template slot-scope="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'danger'" size="small">{{ row.status === 1 ? '正常' : '禁用' }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createdAt" label="注册时间" width="120" align="center">
          <template slot-scope="{ row }">{{ fmt(row.createdAt || row.created_at) }}</template>
        </el-table-column>
        <el-table-column label="操作" width="100" align="center" fixed="right">
          <template slot-scope="{ row }">
            <el-button v-if="row.status === 1" type="text" size="small" class="text-danger" @click="handleToggle(row)">禁用</el-button>
            <el-button v-else type="text" size="small" @click="handleToggle(row)">启用</el-button>
          </template>
        </el-table-column>
      </el-table>
      <div class="table-footer" v-if="total > pageSize">
        <el-pagination background layout="prev, pager, next, total" :total="total" :page-size="pageSize" :current-page.sync="currentPage" @current-change="fetchList" />
      </div>
    </div>
  </div>
</template>

<script>
import { getUserList, updateUserStatus } from '@/api/admin'

const ROLE_MAP = { CUSTOMER: '客户', COMPANION: '陪诊师', ADMIN: '管理员' }

export default {
  name: 'AdminUsers',
  data() { return { list: [], loading: false, currentPage: 1, pageSize: 10, total: 0, filterRole: '', filterStatus: '', roleMap: ROLE_MAP } },
  created() { this.fetchList() },
  methods: {
    async fetchList() {
      this.loading = true
      try { const params = { page: this.currentPage, size: this.pageSize }; if (this.filterRole) params.role = this.filterRole; if (this.filterStatus !== '') params.status = this.filterStatus; const res = await getUserList(params); const d = res.data || res; this.list = d.records || d.list || d || []; this.total = d.total || this.list.length } catch { this.list = [] } finally { this.loading = false }
    },
    handleFilter() { this.currentPage = 1; this.fetchList() },
    resetFilter() { this.filterRole = ''; this.filterStatus = ''; this.currentPage = 1; this.fetchList() },
    roleClass(r) { const map = { CUSTOMER: 'r--customer', COMPANION: 'r--companion', ADMIN: 'r--admin' }; return map[r] || '' },
    handleToggle(row) {
      const newStatus = row.status === 1 ? 0 : 1
      const label = newStatus === 0 ? '禁用' : '启用'
      this.$confirm(`确认${label}用户「${row.nickname || row.username}」？`, `确认${label}`, { type: 'warning' }).then(async () => { try { await updateUserStatus(row.id, { status: newStatus }); this.$message.success(`已${label}`); this.fetchList() } catch {} }).catch(() => {})
    },
    fmt(d) { if (!d) return '-'; return d.replace('T', ' ').substring(0, 16) }
  }
}
</script>

<style scoped>
.warm-table { width: 100%; }
.table-footer { display: flex; justify-content: flex-end; padding: 16px 0 8px; }
.role-tag { display: inline-block; padding: 2px 10px; border-radius: 20px; font-size: 12px; font-weight: 500; }
.r--customer { background: rgba(122,154,126,.1); color: #5C7A60; }
.r--companion { background: rgba(196,168,130,.1); color: #8B7355; }
.r--admin { background: rgba(26,26,26,.06); color: #333; }
.text-danger { color: #E06060 !important; }
</style>
