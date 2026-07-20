<template>
  <div class="page-wrap">
    <h2 class="page-title">可接订单</h2>
    <el-tabs v-model="activeTab" class="order-tabs" @tab-click="onTabChange">
      <!-- 需求广场：主动申请 -->
      <el-tab-pane label="需求广场" name="pool">
        <div class="content-card">
          <div class="order-grid" v-loading="poolLoading">
            <div v-for="item in pool" :key="item.id" class="order-card">
              <div class="oc-header">
                <span class="oc-id">需求 #{{ item.id }}</span>
                <span class="oc-price">{{ item.budget ? '¥' + item.budget : '预算面议' }}</span>
              </div>
              <div class="oc-body">
                <div class="oc-row"><i class="el-icon-office-building"></i> {{ item.hospitalName || '-' }} · {{ item.department || '-' }}</div>
                <div class="oc-row"><i class="el-icon-date"></i> {{ fmt(item.serviceDate) }}</div>
                <div v-if="item.aiSummary" class="ai-summary">
                  <div class="ai-summary-title"><i class="el-icon-magic-stick"></i> AI需求摘要</div>
                  <p>{{ item.aiSummary }}</p>
                </div>
                <div class="oc-row"><i class="el-icon-document"></i> {{ item.requirement || '暂无需求描述' }}</div>
              </div>
              <div class="oc-footer">
                <el-tag size="small">{{ item.serviceType }}</el-tag>
                <div>
                  <span v-if="item.myApplicationStatus === 'PENDING'" class="applied-tag">已申请，等待客户选择</span>
                  <el-button v-else type="primary" size="small" round @click="openApply(item)">申请接单</el-button>
                </div>
              </div>
            </div>
            <div class="empty-state" v-if="!poolLoading && pool.length === 0">
              <i class="el-icon-s-claim"></i><p>需求广场暂无可申请的需求</p>
            </div>
          </div>
        </div>
      </el-tab-pane>

      <!-- 邀请我的：客户直接指定，待我确认 -->
      <el-tab-pane label="邀请我的" name="invited">
        <div class="content-card">
          <div class="order-grid" v-loading="invitedLoading">
            <div v-for="item in invited" :key="item.id" class="order-card">
              <div class="oc-header">
                <span class="oc-id">订单 #{{ item.id }}</span>
                <span class="oc-price">¥{{ item.servicePrice }}</span>
              </div>
              <div class="oc-body">
                <div class="oc-row"><i class="el-icon-office-building"></i> {{ item.hospitalName || '-' }} · {{ item.department || '-' }}</div>
                <div class="oc-row"><i class="el-icon-date"></i> {{ fmt(item.serviceDate) }}</div>
                <div v-if="item.aiSummary" class="ai-summary">
                  <div class="ai-summary-title"><i class="el-icon-magic-stick"></i> AI需求摘要</div>
                  <p>{{ item.aiSummary }}</p>
                </div>
                <div class="oc-row"><i class="el-icon-document"></i> {{ item.requirement || '暂无需求描述' }}</div>
              </div>
              <div class="oc-footer">
                <el-tag size="small">{{ item.serviceType }}</el-tag>
                <div>
                  <el-button type="danger" size="small" round plain @click="handleReject(item)">拒绝</el-button>
                  <el-button type="primary" size="small" round @click="handleAccept(item)">接单</el-button>
                </div>
              </div>
            </div>
            <div class="empty-state" v-if="!invitedLoading && invited.length === 0">
              <i class="el-icon-s-claim"></i><p>暂无客户直接邀请的订单</p>
            </div>
          </div>
        </div>
      </el-tab-pane>

      <!-- 我的申请 -->
      <el-tab-pane label="我的申请" name="mine">
        <div class="content-card">
          <el-table :data="mine" v-loading="mineLoading" stripe class="warm-table" empty-text="暂无申请记录">
            <el-table-column label="需求" min-width="180">
              <template slot-scope="{ row }">{{ row.hospitalName || '-' }} · {{ row.department || '-' }}</template>
            </el-table-column>
            <el-table-column prop="serviceType" label="类型" width="90" align="center" />
            <el-table-column label="服务时间" width="150"><template slot-scope="{ row }">{{ fmt(row.serviceDate) }}</template></el-table-column>
            <el-table-column label="预算" width="100" align="right"><template slot-scope="{ row }">{{ row.budget ? '¥' + row.budget : '面议' }}</template></el-table-column>
            <el-table-column label="申请状态" width="120" align="center">
              <template slot-scope="{ row }"><span :class="['app-tag', 'app--' + (row.status || '').toLowerCase()]">{{ appStatusMap[row.status] || row.status }}</span></template>
            </el-table-column>
            <el-table-column label="操作" width="100" align="center">
              <template slot-scope="{ row }">
                <el-button v-if="row.status === 'PENDING'" type="text" size="small" class="text-danger" @click="handleWithdraw(row)">撤回</el-button>
              </template>
            </el-table-column>
          </el-table>
        </div>
      </el-tab-pane>
    </el-tabs>

    <el-dialog title="申请接单" :visible.sync="applyVisible" width="440px">
      <el-form label-width="0">
        <p class="apply-tip">向客户简单介绍自己，提高被选中的机会（选填）</p>
        <el-input v-model="applyMessage" type="textarea" :rows="4" maxlength="500" show-word-limit
          placeholder="如：我有3年老年陪护经验，熟悉该医院流程，时间可配合……" />
      </el-form>
      <span slot="footer">
        <el-button @click="applyVisible = false">取消</el-button>
        <el-button type="primary" :loading="applying" @click="confirmApply">提交申请</el-button>
      </span>
    </el-dialog>
  </div>
</template>

<script>
import { getAvailableOrders, acceptOrder, rejectOrder } from '@/api/order'
import { getRequestPool, applyForRequest, getMyApplications, withdrawApplication } from '@/api/application'

const APP_STATUS_MAP = { PENDING: '待选择', ACCEPTED: '已接受', REJECTED: '未选中', WITHDRAWN: '已撤回' }

export default {
  name: 'AvailableOrders',
  data() {
    return {
      activeTab: 'pool',
      pool: [], poolLoading: false,
      invited: [], invitedLoading: false,
      mine: [], mineLoading: false,
      appStatusMap: APP_STATUS_MAP,
      applyVisible: false, applyMessage: '', applying: false, applyTarget: null
    }
  },
  created() { this.fetchPool() },
  methods: {
    onTabChange() {
      if (this.activeTab === 'pool') this.fetchPool()
      else if (this.activeTab === 'invited') this.fetchInvited()
      else this.fetchMine()
    },
    async fetchPool() {
      this.poolLoading = true
      try { const res = await getRequestPool(); this.pool = res.data || res || [] } catch { this.pool = [] } finally { this.poolLoading = false }
    },
    async fetchInvited() {
      this.invitedLoading = true
      try { const res = await getAvailableOrders(); const d = res.data || res; this.invited = d.records || d.list || d || [] } catch { this.invited = [] } finally { this.invitedLoading = false }
    },
    async fetchMine() {
      this.mineLoading = true
      try { const res = await getMyApplications(); this.mine = res.data || res || [] } catch { this.mine = [] } finally { this.mineLoading = false }
    },
    openApply(item) { this.applyTarget = item; this.applyMessage = ''; this.applyVisible = true },
    async confirmApply() {
      this.applying = true
      try {
        await applyForRequest({ requestId: this.applyTarget.id, message: this.applyMessage })
        this.$message.success('申请已提交，等待客户选择')
        this.applyVisible = false; this.fetchPool()
      } catch {} finally { this.applying = false }
    },
    handleWithdraw(row) {
      this.$confirm('确认撤回该申请？', '撤回申请', { type: 'warning' }).then(async () => {
        try { await withdrawApplication(row.id); this.$message.success('已撤回'); this.fetchMine() } catch {}
      }).catch(() => {})
    },
    handleAccept(item) {
      this.$confirm(`确认接单 #${item.id}，服务金额 ¥${item.servicePrice}？`, '确认接单', { confirmButtonText: '确认接单', type: 'success' }).then(async () => { try { await acceptOrder(item.id); this.$message.success('接单成功'); this.fetchInvited() } catch {} }).catch(() => {})
    },
    handleReject(item) {
      this.$prompt('请输入拒绝原因（可选）', '拒绝订单', { confirmButtonText: '确认拒绝', cancelButtonText: '取消', type: 'warning', inputPlaceholder: '如：时间冲突、不在服务区域等' }).then(async ({ value }) => { try { await rejectOrder(item.id, { reason: value || '陪诊师拒绝' }); this.$message.success('已拒绝'); this.fetchInvited() } catch {} }).catch(() => {})
    },
    fmt(d) { if (!d) return '-'; return String(d).replace('T', ' ').substring(0, 16) }
  }
}
</script>

<style scoped>
.page-wrap { padding: 28px 36px; }
.page-title { font-size: 20px; font-weight: 700; color: var(--brand-cream-100); margin: 0 0 16px; }
.order-tabs::v-deep .el-tabs__item { font-size: 15px; }
.content-card { background: transparent; }
.order-grid { display: grid; grid-template-columns: repeat(auto-fill, minmax(310px, 1fr)); gap: 16px; min-height: 150px; }
.order-card { background: #fff; border: 1px solid rgba(0,0,0,0.04); border-radius: var(--radius-md); padding: 24px; transition: all 0.3s cubic-bezier(0.4,0,0.2,1); position: relative; overflow: hidden; }
.order-card::before { content: ''; position: absolute; top: 0; left: 0; right: 0; height: 3px; background: linear-gradient(90deg, var(--color-primary), var(--color-primary-light)); transform: scaleX(0); transform-origin: left; transition: transform 0.35s cubic-bezier(0.4,0,0.2,1); }
.order-card:hover { border-color: rgba(122,154,126,0.18); box-shadow: var(--shadow-md); transform: translateY(-4px); }
.order-card:hover::before { transform: scaleX(1); }
.oc-header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 16px; }
.oc-id { font-size: 12px; color: var(--color-text-placeholder); font-weight: 550; letter-spacing: 0.03em; text-transform: uppercase; }
.oc-price { font-size: 22px; font-weight: 700; color: var(--color-primary-dark); letter-spacing: -0.02em; }
.oc-body { display: flex; flex-direction: column; gap: 10px; margin-bottom: 18px; }
.oc-row { font-size: 14px; color: var(--color-text-secondary); display: flex; align-items: center; gap: 8px; overflow: hidden; text-overflow: ellipsis; white-space: nowrap; }
.oc-row i { color: var(--color-primary); font-size: 15px; flex-shrink: 0; opacity: 0.7; }
.ai-summary { padding: 10px 12px; border-left: 3px solid var(--color-primary); background: var(--color-bg-page); color: var(--color-text-secondary); border-radius: var(--radius-sm); }
.ai-summary-title { display: flex; align-items: center; gap: 6px; color: var(--color-primary-dark); font-size: 12px; font-weight: 600; }
.ai-summary p { margin: 6px 0 0; line-height: 1.55; font-size: 13px; white-space: normal; }
.oc-footer { display: flex; justify-content: space-between; align-items: center; padding-top: 14px; border-top: 1px solid rgba(0,0,0,0.03); }
.applied-tag { font-size: 12px; color: #B88230; background: rgba(230,162,60,.1); padding: 4px 10px; border-radius: 20px; }
.empty-state { grid-column: 1 / -1; text-align: center; padding: 80px 0; color: var(--color-text-placeholder); }
.empty-state i { font-size: 48px; color: rgba(0,0,0,0.06); margin-bottom: 16px; display: block; }
.warm-table { width: 100%; background: #fff; border-radius: var(--radius-md); }
.warm-table::v-deep th { background: var(--color-bg-page); color: var(--color-text-regular); font-weight: 600; font-size: 13px; }
.app-tag { display: inline-block; padding: 2px 10px; border-radius: 20px; font-size: 12px; font-weight: 500; }
.app--pending { background: rgba(230,162,60,.1); color: #B88230; }
.app--accepted { background: rgba(122,154,126,.12); color: #5C7A60; }
.app--rejected { background: rgba(153,153,153,.1); color: #888; }
.app--withdrawn { background: rgba(153,153,153,.1); color: #888; }
.apply-tip { font-size: 13px; color: var(--color-text-secondary); margin: 0 0 10px; }
.text-danger { color: #E06060 !important; }
</style>
