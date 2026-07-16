<template>
  <div class="audit">
    <div class="audit-card">
      <div class="audit-header">
        <div>
          <h2 class="audit-title">
            <i class="el-icon-s-check"></i> 陪诊师审核
          </h2>
          <p class="audit-subtitle">管理陪诊师入驻申请，审核资质的平台</p>
        </div>
        <div class="audit-stats">
          <span class="stat-badge stat-pending">{{ pendingCount }} 条待审核</span>
          <span class="stat-badge stat-total">共 {{ tableData.length }} 条记录</span>
        </div>
      </div>

      <el-table
        :data="tableData"
        border
        style="width:100%"
        :header-cell-style="{background:'#f8fafc',color:'#475569',fontWeight:'600',fontSize:'14px',borderColor:'#f1f5f9'}">

        <el-table-column
          prop="name"
          label="姓名"
          align="center">
          <template slot-scope="scope">
            <span class="name-cell">{{ scope.row.name }}</span>
          </template>
        </el-table-column>

        <el-table-column
          prop="area"
          label="服务区域"
          align="center">
          <template slot-scope="scope">
            <el-tag type="info" size="small" effect="plain">{{ scope.row.area }}</el-tag>
          </template>
        </el-table-column>

        <el-table-column
          prop="service"
          label="服务类型"
          align="center">
          <template slot-scope="scope">
            <el-tag size="small" effect="plain" class="service-tag">{{ scope.row.service }}</el-tag>
          </template>
        </el-table-column>

        <el-table-column
          prop="status"
          label="审核状态"
          align="center"
          width="120">
          <template slot-scope="scope">
            <span class="status-dot" :class="statusClass(scope.row.status)"></span>
            {{ scope.row.status }}
          </template>
        </el-table-column>

        <el-table-column
          label="操作"
          align="center"
          width="120">
          <template slot-scope="scope">
            <el-button
              v-if="scope.row.status === '待审核'"
              type="primary"
              size="mini"
              @click="openAudit(scope.row)">
              <i class="el-icon-edit-outline"></i> 审核
            </el-button>
            <span v-else class="done-label">已完成</span>
          </template>
        </el-table-column>
      </el-table>
    </div>

    <!-- 审核弹窗 -->
    <el-dialog
      title="陪诊师审核"
      :visible.sync="dialogVisible"
      width="480px">

      <div class="audit-dialog-body">
        <div class="dialog-info-row">
          <span class="dialog-label">姓名</span>
          <span class="dialog-value">{{ current.name }}</span>
        </div>
        <div class="dialog-info-row">
          <span class="dialog-label">区域</span>
          <span class="dialog-value">{{ current.area }}</span>
        </div>
        <div class="dialog-info-row">
          <span class="dialog-label">服务类型</span>
          <span class="dialog-value">{{ current.service }}</span>
        </div>
      </div>

      <span slot="footer">
        <el-button @click="reject">
          <i class="el-icon-close"></i> 拒绝
        </el-button>
        <el-button type="primary" @click="pass">
          <i class="el-icon-check"></i> 通过
        </el-button>
      </span>
    </el-dialog>
  </div>
</template>



<script>

export default {


  data(){


    return {


      tableData:[


        {
          name:"张三",
          area:"上海徐汇",
          service:"医院陪诊",
          status:"待审核"
        },


        {
          name:"李女士",
          area:"上海浦东",
          service:"老人陪伴",
          status:"待审核"
        }


      ],


      // 控制弹窗显示
      dialogVisible:false,


      // 当前审核对象
      current:{}


    }


  },


  computed: {
    pendingCount() {
      return this.tableData.filter(item => item.status === '待审核').length
    }
  },


  methods:{


    // 打开审核弹窗
    openAudit(row){


      this.current=row

      this.dialogVisible=true


    },


    // 通过审核
    pass(){


      this.current.status="已通过"

      this.dialogVisible=false


    },


    // 拒绝审核
    reject(){


      this.current.status="已拒绝"

      this.dialogVisible=false


    },


    // 状态样式类
    statusClass(status) {
      if (status === '待审核') return 'status-pending'
      if (status === '已通过') return 'status-passed'
      if (status === '已拒绝') return 'status-rejected'
      return ''
    }


  }


}

</script>



<style scoped>
.audit-card {
  background: #ffffff;
  border-radius: 16px;
  padding: 32px;
  box-shadow: 0 2px 16px rgba(0, 0, 0, 0.05);
  border: 1px solid #f1f5f9;
}

.audit-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: 24px;
  padding-bottom: 20px;
  border-bottom: 1px solid #f1f5f9;
}

.audit-title {
  font-size: 22px;
  font-weight: 700;
  color: #1e293b;
  margin: 0 0 6px 0;
  display: flex;
  align-items: center;
  gap: 10px;
}

.audit-title i {
  color: #3b82f6;
  font-size: 24px;
}

.audit-subtitle {
  font-size: 13px;
  color: #94a3b8;
  margin: 0;
}

.audit-stats {
  display: flex;
  gap: 10px;
}

.stat-badge {
  padding: 6px 16px;
  border-radius: 20px;
  font-size: 13px;
  font-weight: 600;
}

.stat-pending {
  background: #fef3c7;
  color: #d97706;
}

.stat-total {
  background: #e0e7ff;
  color: #4f46e5;
}

/* ===== 姓名单元格 ===== */
.name-cell {
  font-weight: 600;
  color: #1e293b;
}

/* ===== 服务类型标签 ===== */
.service-tag {
  background: #eff6ff !important;
  border-color: #93c5fd !important;
  color: #2563eb !important;
  font-weight: 500 !important;
}

/* ===== 状态指示点 ===== */
.status-dot {
  display: inline-block;
  width: 7px;
  height: 7px;
  border-radius: 50%;
  margin-right: 6px;
  vertical-align: middle;
}

.status-dot.status-pending {
  background: #f59e0b;
  box-shadow: 0 0 6px rgba(245, 158, 11, 0.4);
}

.status-dot.status-passed {
  background: #10b981;
  box-shadow: 0 0 6px rgba(16, 185, 129, 0.4);
}

.status-dot.status-rejected {
  background: #ef4444;
  box-shadow: 0 0 6px rgba(239, 68, 68, 0.4);
}

/* ===== 已完成标签 ===== */
.done-label {
  font-size: 13px;
  color: #94a3b8;
  font-weight: 500;
}

/* ===== 弹窗内容 ===== */
.audit-dialog-body {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.dialog-info-row {
  display: flex;
  align-items: center;
  padding: 14px 16px;
  background: #f8fafc;
  border-radius: 10px;
  border: 1px solid #f1f5f9;
}

.dialog-label {
  font-size: 14px;
  font-weight: 600;
  color: #64748b;
  width: 80px;
  flex-shrink: 0;
}

.dialog-value {
  font-size: 15px;
  font-weight: 500;
  color: #1e293b;
}
</style>
