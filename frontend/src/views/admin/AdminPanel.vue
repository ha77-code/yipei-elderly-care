<template>
  <div class="admin-panel">
    <div class="panel-layout">
      <aside class="side-bar">
        <arc-nav :items="arcItems" />
      </aside>

      <div class="content-zone">
        <a href="/landing.html" class="back-link">
          <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
            <path d="M19 12H5M12 19l-7-7 7-7" stroke-linecap="round"/>
          </svg>
          返回首页
        </a>
        <div class="panel-viewport">
          <!-- 用户管理 -->
          <div class="panel-page">
            <div class="panel-inner">
              <div class="section-head"><h2>用户管理</h2><span class="section-sub">共 1,286 人</span></div>
              <div class="filter-strip">
                <select><option>全部角色</option><option>客户</option><option>陪诊师</option><option>管理员</option></select>
                <select><option>全部状态</option><option>正常</option><option>已禁用</option></select>
                <input placeholder="搜索..." style="flex:1;max-width:180px">
                <button class="btn-sm">重置</button>
              </div>
              <div class="table-wrap">
                <table>
                  <thead><tr><th>ID</th><th>用户名</th><th>昵称</th><th>手机号</th><th>角色</th><th>状态</th><th>注册时间</th><th>操作</th></tr></thead>
                  <tbody>
                    <tr><td>1</td><td>customer1</td><td>Zhang San</td><td>138****0001</td><td><span class="tag-pill tag-green">客户</span></td><td><span class="tag-pill tag-green">正常</span></td><td>2026-07-16</td><td><button class="btn-action">禁用</button></td></tr>
                    <tr><td>2</td><td>companion1</td><td>Li Peizhen</td><td>138****0002</td><td><span class="tag-pill tag-amber">陪诊师</span></td><td><span class="tag-pill tag-green">正常</span></td><td>2026-07-16</td><td><button class="btn-action">禁用</button></td></tr>
                    <tr><td>3</td><td>admin1</td><td>Admin</td><td>138****0003</td><td><span class="tag-pill tag-gray">管理员</span></td><td><span class="tag-pill tag-green">正常</span></td><td>2026-07-16</td><td><button class="btn-action">-</button></td></tr>
                  </tbody>
                </table>
              </div>
            </div>
          </div>

          <!-- 陪诊师审核 -->
          <div class="panel-page">
            <div class="panel-inner">
              <div class="section-head"><h2>陪诊师审核</h2><span class="section-sub">待审核 5 人</span></div>
              <div class="table-wrap">
                <table>
                  <thead><tr><th>编号</th><th>姓名</th><th>服务区域</th><th>服务类型</th><th>经验</th><th>介绍</th><th>操作</th></tr></thead>
                  <tbody>
                    <tr><td>1</td><td>Wang Fang</td><td>北京市朝阳区</td><td><span class="tag-pill tag-green">门诊陪诊</span></td><td>5年</td><td>资深陪诊师，擅长老年护理...</td><td><button class="btn-action primary">通过</button><button class="btn-action danger">拒绝</button></td></tr>
                    <tr><td>2</td><td>Zhao Min</td><td>上海市浦东新区</td><td><span class="tag-pill tag-green">住院陪护</span></td><td>3年</td><td>护理专业毕业...</td><td><button class="btn-action primary">通过</button><button class="btn-action danger">拒绝</button></td></tr>
                  </tbody>
                </table>
              </div>
            </div>
          </div>

          <!-- 需求管理 -->
          <div class="panel-page">
            <div class="panel-inner">
              <div class="section-head"><h2>需求管理</h2><span class="section-sub">共 156 条</span></div>
              <div class="filter-strip">
                <select><option>全部状态</option><option>待匹配</option><option>已匹配</option><option>已完成</option></select>
                <button class="btn-sm">重置</button>
              </div>
              <div class="table-wrap">
                <table>
                  <thead><tr><th>需求号</th><th>发布人</th><th>服务类型</th><th>服务时间</th><th>地点</th><th>预算</th><th>操作</th></tr></thead>
                  <tbody>
                    <tr><td>#R001</td><td>Zhang San</td><td>门诊陪诊</td><td>2026-07-20 09:00</td><td>人民医院</td><td>￥200.00</td><td><button class="btn-action">详情</button></td></tr>
                  </tbody>
                </table>
              </div>
            </div>
          </div>

          <!-- 订单管理 -->
          <div class="panel-page">
            <div class="panel-inner">
              <div class="section-head"><h2>订单管理</h2><span class="section-sub">共 328 笔</span></div>
              <div class="filter-strip">
                <select><option>全部状态</option><option>待接单</option><option>服务中</option><option>已完成</option></select>
                <button class="btn-sm">重置</button>
              </div>
              <div class="table-wrap">
                <table>
                  <thead><tr><th>订单号</th><th>客户</th><th>服务时间</th><th>陪诊师</th><th>价格</th><th>状态</th><th>操作</th></tr></thead>
                  <tbody>
                    <tr><td>#1001</td><td>Zhang San</td><td>2026-07-20 09:00</td><td>Li Peizhen</td><td>￥200.00</td><td><span class="tag-pill tag-amber">服务中</span></td><td><button class="btn-action">详情</button></td></tr>
                    <tr><td>#1002</td><td>Wang Wu</td><td>2026-07-21 14:00</td><td>-</td><td>￥150.00</td><td><span class="tag-pill tag-gray">待接单</span></td><td><button class="btn-action">详情</button></td></tr>
                  </tbody>
                </table>
              </div>
            </div>
          </div>

          <!-- 投诉处理 -->
          <div class="panel-page">
            <div class="panel-inner">
              <div class="section-head"><h2>投诉处理</h2><span class="section-sub">待处理 3 件</span></div>
              <div class="table-wrap">
                <table>
                  <thead><tr><th>编号</th><th>订单号</th><th>投诉人</th><th>原因</th><th>状态</th><th>时间</th><th>操作</th></tr></thead>
                  <tbody>
                    <tr><td>1</td><td>#982</td><td>Zhang San</td><td>服务态度差</td><td><span class="tag-pill tag-red">待处理</span></td><td>2026-07-17</td><td><button class="btn-action primary">处理</button></td></tr>
                    <tr><td>2</td><td>#945</td><td>Li Si</td><td>未按时到达</td><td><span class="tag-pill tag-amber">处理中</span></td><td>2026-07-16</td><td><button class="btn-action primary">解决</button></td></tr>
                  </tbody>
                </table>
              </div>
            </div>
          </div>

          <!-- 平台统计 -->
          <div class="panel-page">
            <div class="panel-inner">
              <div class="section-head"><h2>平台统计</h2><span class="section-sub">实时数据</span></div>
              <div class="table-wrap">
                <table>
                  <thead><tr><th>指标</th><th>数值</th><th>指标</th><th>数值</th></tr></thead>
                  <tbody>
                    <tr><td>注册用户</td><td>1,286</td><td>陪诊师</td><td>86</td></tr>
                    <tr><td>总订单</td><td>328</td><td>已完成</td><td>290</td></tr>
                    <tr><td>待接单</td><td>12</td><td>服务中</td><td>18</td></tr>
                    <tr><td>投诉总数</td><td>8</td><td>平台收入</td><td>￥12,580.00</td></tr>
                  </tbody>
                </table>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import ArcNav from '@/components/ArcNav.vue'

const ARC_ICON_MAP = {
  '/admin/users': '☺',
  '/admin/companion-review': '✓',
  '/admin/requests': '✎',
  '/admin/orders': '☰',
  '/admin/reports': '⚠',
  '/admin/statistics': '◆'
}

export default {
  name: 'AdminPanel',
  components: { ArcNav },
  computed: {
    arcItems() {
      const items = [
        { label: '用户', path: '/admin/users' },
        { label: '审核', path: '/admin/companion-review' },
        { label: '需求', path: '/admin/requests' },
        { label: '订单', path: '/admin/orders' },
        { label: '投诉', path: '/admin/reports' },
        { label: '统计', path: '/admin/statistics' }
      ]
      return items.map(item => ({
        ...item,
        icon: ARC_ICON_MAP[item.path] || '•'
      }))
    }
  }
}
</script>

<style scoped>
.admin-panel {
  min-height: 100vh;
  background: #0E1C0A;
  display: flex;
}
.panel-layout {
  display: flex;
  width: 100%;
  height: 100vh;
  position: relative;
  z-index: 2;
}
.side-bar {
  width: 280px;
  flex: 0 0 280px;
  padding: 0;
  border-right: 1px solid rgba(180, 200, 150, 0.08);
  background: rgba(10, 20, 8, 0.12);
  backdrop-filter: blur(22px);
  -webkit-backdrop-filter: blur(22px);
  overflow: visible;
}
.content-zone {
  flex: 1;
  overflow: hidden;
  position: relative;
  display: flex;
  flex-direction: column;
}
.content-zone::before,
.content-zone::after {
  content: '';
  position: absolute;
  left: 0;
  right: 0;
  z-index: 5;
  pointer-events: none;
}
.content-zone::before {
  top: 0;
  height: 40px;
  background: linear-gradient(180deg, rgba(14, 28, 10, 0.8) 0%, transparent 100%);
}
.content-zone::after {
  bottom: 0;
  height: 40px;
  background: linear-gradient(0deg, rgba(14, 28, 10, 0.8) 0%, transparent 100%);
}
.back-link {
  position: fixed;
  top: 28px;
  left: calc(280px + 16px);
  min-width: 296px;
  z-index: 10;
  display: inline-flex;
  align-items: center;
  gap: 6px;
  font-size: 11px;
  color: rgba(180, 170, 150, 0.4);
  text-decoration: none;
  transition: color 0.3s;
  letter-spacing: 0.04em;
}
.back-link:hover {
  color: rgba(210, 195, 170, 0.7);
}
.back-link svg {
  width: 12px;
  height: 12px;
}
.panel-viewport {
  height: 100vh;
  overflow-y: auto;
  scroll-snap-type: y mandatory;
  flex: 1;
}
.panel-page {
  height: 100vh;
  scroll-snap-align: start;
  display: flex;
  flex-direction: column;
  padding: 40px 44px;
  position: relative;
}
.panel-inner {
  max-width: 880px;
  width: 100%;
  flex: 1;
  display: flex;
  flex-direction: column;
  margin: 0 auto;
}
.section-head {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 0 14px;
  margin-bottom: 32px;
  border-bottom: 1px solid rgba(170, 195, 145, 0.1);
}
.section-head h2 {
  font-family: 'Noto Serif SC', 'PingFang SC', sans-serif;
  font-size: 22px;
  font-weight: 300;
  color: rgba(200, 200, 175, 0.75);
  letter-spacing: 0.04em;
}
.section-sub {
  font-size: 12px;
  color: rgba(185, 180, 165, 0.45);
  letter-spacing: 0.05em;
}
.filter-strip {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 12px 18px;
  margin-bottom: 14px;
  background: rgba(28, 44, 22, 0.35);
  backdrop-filter: blur(8px);
  -webkit-backdrop-filter: blur(8px);
  border: 1px solid rgba(180, 200, 150, 0.08);
  border-radius: 10px;
}
.filter-strip select,
.filter-strip input {
  padding: 8px 14px;
  background: rgba(22, 36, 18, 0.5);
  border: 1px solid rgba(170, 190, 150, 0.12);
  border-radius: 8px;
  color: rgba(210, 205, 185, 0.7);
  font-size: 15px;
  font-family: inherit;
  outline: none;
  transition: all 0.3s ease;
}
.filter-strip select:focus,
.filter-strip input:focus {
  border-color: rgba(190, 210, 160, 0.3);
}
.btn-sm {
  padding: 8px 18px;
  background: rgba(180, 200, 150, 0.08);
  border: 1px solid rgba(180, 200, 150, 0.12);
  border-radius: 8px;
  color: rgba(200, 190, 160, 0.55);
  font-size: 12px;
  cursor: pointer;
  transition: all 0.3s ease;
  font-family: inherit;
}
.btn-sm:hover {
  background: rgba(180, 200, 150, 0.14);
  color: rgba(220, 210, 180, 0.75);
}
.table-wrap {
  background: rgba(18, 32, 14, 0.15);
  backdrop-filter: blur(22px);
  -webkit-backdrop-filter: blur(22px);
  border: 1px solid rgba(170, 195, 145, 0.06);
  border-radius: 16px;
  overflow: hidden;
  flex: 1;
}
table {
  width: 100%;
  border-collapse: collapse;
}
thead th {
  text-align: left;
  padding: 16px 18px;
  font-size: 15px;
  font-weight: 400;
  color: rgba(185, 185, 165, 0.7);
  letter-spacing: 0.04em;
  border-bottom: 1px solid rgba(170, 195, 145, 0.08);
}
tbody td {
  padding: 0 18px;
  height: 58px;
  font-size: 14px;
  color: rgba(235, 228, 215, 0.65);
  border-bottom: 1px solid rgba(170, 195, 145, 0.04);
}
tbody tr {
  transition: all 0.3s ease;
}
tbody tr:hover {
  background: rgba(160, 200, 130, 0.03);
}
.tag-pill {
  display: inline-block;
  padding: 4px 14px;
  border-radius: 20px;
  font-size: 12px;
  font-weight: 400;
  border: 1px solid;
  letter-spacing: 0.03em;
}
.tag-green {
  border-color: rgba(140, 190, 120, 0.2);
  color: rgba(155, 195, 140, 0.6);
  background: rgba(140, 190, 120, 0.04);
}
.tag-amber {
  border-color: rgba(195, 165, 120, 0.2);
  color: rgba(200, 175, 140, 0.6);
  background: rgba(195, 165, 120, 0.04);
}
.tag-red {
  border-color: rgba(185, 120, 115, 0.2);
  color: rgba(195, 140, 135, 0.55);
  background: rgba(185, 120, 115, 0.04);
}
.tag-gray {
  border-color: rgba(160, 160, 150, 0.15);
  color: rgba(175, 170, 160, 0.5);
  background: rgba(160, 160, 150, 0.04);
}
.btn-action {
  padding: 6px 14px;
  background: transparent;
  border: 1px solid rgba(170, 195, 145, 0.12);
  border-radius: 8px;
  color: rgba(195, 185, 165, 0.5);
  font-size: 14px;
  cursor: pointer;
  transition: all 0.3s ease;
  font-family: inherit;
  margin-right: 6px;
}
.btn-action:hover {
  background: rgba(170, 195, 145, 0.08);
  color: rgba(215, 205, 180, 0.65);
  border-color: rgba(170, 195, 145, 0.2);
}
.btn-action.primary {
  background: rgba(140, 180, 120, 0.1);
  border-color: rgba(140, 180, 120, 0.18);
  color: rgba(160, 195, 145, 0.6);
}
.btn-action.primary:hover {
  background: rgba(140, 180, 120, 0.15);
}
.btn-action.danger {
  color: rgba(185, 130, 125, 0.5);
  border-color: rgba(185, 130, 125, 0.12);
}
.btn-action.danger:hover {
  background: rgba(185, 130, 125, 0.06);
  color: rgba(200, 155, 150, 0.6);
}
</style>
