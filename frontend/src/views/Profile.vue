<template>
  <div class="profile-page">
    <h2 class="page-title">个人信息</h2>

    <div class="profile-card">
      <!-- 查看模式 -->
      <template v-if="!editing">
        <div class="profile-avatar-area">
          <el-avatar :size="80" icon="el-icon-user-solid" class="profile-avatar"></el-avatar>
          <span :class="['role-badge', `role-badge--${user.role?.toLowerCase()}`]">
            {{ roleLabel }}
          </span>
        </div>

        <div class="profile-info-grid">
          <div class="info-item">
            <span class="info-label">用户名</span>
            <span class="info-value">{{ user.username }}</span>
          </div>
          <div class="info-item">
            <span class="info-label">昵称</span>
            <span class="info-value">{{ user.nickname || '-' }}</span>
          </div>
          <div class="info-item">
            <span class="info-label">手机号</span>
            <span class="info-value">{{ user.phone || '-' }}</span>
          </div>
          <div class="info-item">
            <span class="info-label">角色</span>
            <span class="info-value">{{ roleLabel }}</span>
          </div>
          <div class="info-item">
            <span class="info-label">状态</span>
            <span class="info-value">
              <el-tag :type="user.status === 1 ? 'success' : 'danger'" size="small">
                {{ user.status === 1 ? '正常' : '已禁用' }}
              </el-tag>
            </span>
          </div>
          <div class="info-item">
            <span class="info-label">注册时间</span>
            <span class="info-value">{{ user.createdAt || user.created_at || '-' }}</span>
          </div>
        </div>

        <div class="profile-actions">
          <el-button type="primary" icon="el-icon-edit" size="medium" round @click="startEdit">
            修改资料
          </el-button>
        </div>
      </template>

      <!-- 编辑模式 -->
      <template v-else>
        <h3 class="edit-title">修改个人资料</h3>

        <el-form
          ref="profileForm"
          :model="editForm"
          :rules="rules"
          label-width="80px"
          class="profile-form"
          size="medium"
        >
          <el-form-item label="用户名">
            <el-input v-model="user.username" disabled />
          </el-form-item>

          <el-form-item label="昵称" prop="nickname">
            <el-input
              v-model="editForm.nickname"
              placeholder="请输入昵称"
              maxlength="100"
              show-word-limit
              clearable
            />
          </el-form-item>

          <el-form-item label="手机号" prop="phone">
            <el-input
              v-model="editForm.phone"
              placeholder="请输入手机号"
              maxlength="11"
              clearable
            />
          </el-form-item>

          <el-form-item>
            <el-button type="primary" :loading="saving" @click="handleSave">
              保存修改
            </el-button>
            <el-button @click="cancelEdit">取消</el-button>
          </el-form-item>
        </el-form>
      </template>
    </div>
  </div>
</template>

<script>
import { getUser, setUser, ROLE_LABELS } from '@/utils/auth'
import { getUserInfo, updateUserInfo } from '@/api/user'

export default {
  name: 'Profile',
  data() {
    const user = getUser() || {}
    return {
      user: { ...user },
      editing: false,
      editForm: {
        nickname: user.nickname || '',
        phone: user.phone || ''
      },
      rules: {
        nickname: [
          { required: true, message: '请输入昵称', trigger: 'blur' },
          { max: 100, message: '昵称不超过100个字符', trigger: 'blur' }
        ],
        phone: [
          { required: true, message: '请输入手机号', trigger: 'blur' },
          { pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号', trigger: 'blur' }
        ]
      },
      saving: false
    }
  },
  computed: {
    roleLabel() {
      return ROLE_LABELS[this.user.role] || '客户'
    }
  },
  created() {
    this.fetchUserInfo()
  },
  methods: {
    async fetchUserInfo() {
      try {
        const res = await getUserInfo()
        const userData = res.data || res
        this.user = { ...userData }
        setUser(userData)
      } catch {
        // 接口暂未实现时使用本地数据
      }
    },
    startEdit() {
      this.editForm.nickname = this.user.nickname || ''
      this.editForm.phone = this.user.phone || ''
      this.editing = true
    },
    cancelEdit() {
      this.editing = false
    },
    async handleSave() {
      try {
        await this.$refs.profileForm.validate()
      } catch {
        return
      }

      this.saving = true
      try {
        await updateUserInfo({
          nickname: this.editForm.nickname,
          phone: this.editForm.phone
        })
        // 更新本地存储
        const updatedUser = {
          ...this.user,
          nickname: this.editForm.nickname,
          phone: this.editForm.phone
        }
        this.user = updatedUser
        setUser(updatedUser)
        this.editing = false
        this.$message.success('个人信息修改成功')
      } catch {
        // 错误已在拦截器中统一处理
      } finally {
        this.saving = false
      }
    }
  }
}
</script>

<style scoped>
.profile-page {
  padding: 32px;
  max-width: 720px;
}

.page-title {
  font-family: var(--font-family);
  font-size: 22px;
  font-weight: 700;
  color: var(--color-text-primary);
  margin: 0 0 24px;
}

/* ===== 卡片 ===== */
.profile-card {
  background: #FFFFFF;
  border: 1px solid var(--color-border-light);
  border-radius: var(--radius-lg);
  padding: 40px;
  box-shadow: var(--shadow-sm);
}

/* ===== 头像区 ===== */
.profile-avatar-area {
  display: flex;
  flex-direction: column;
  align-items: center;
  margin-bottom: 36px;
}

.profile-avatar {
  background: var(--color-primary-light);
  margin-bottom: 12px;
}

.role-badge {
  padding: 4px 16px;
  border-radius: 20px;
  font-size: 13px;
  font-weight: 500;
}

.role-badge--customer {
  background: rgba(122, 154, 126, 0.1);
  color: #5C7A60;
}

.role-badge--companion {
  background: rgba(196, 168, 130, 0.1);
  color: #8B7355;
}

.role-badge--admin {
  background: rgba(26, 26, 26, 0.06);
  color: #333333;
}

/* ===== 信息网格 ===== */
.profile-info-grid {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 0;
  border: 1px solid var(--color-border-light);
  border-radius: var(--radius-sm);
  overflow: hidden;
  margin-bottom: 32px;
}

.info-item {
  display: flex;
  flex-direction: column;
  gap: 4px;
  padding: 16px 20px;
  border-bottom: 1px solid var(--color-border-light);
  border-right: 1px solid var(--color-border-light);
}
.info-item:nth-child(even) {
  border-right: none;
}
.info-item:nth-last-child(-n+2) {
  border-bottom: none;
}

.info-label {
  font-size: 13px;
  color: var(--color-text-placeholder);
  font-weight: 500;
}

.info-value {
  font-size: 15px;
  color: var(--color-text-primary);
  font-weight: 500;
}

/* ===== 操作按钮 ===== */
.profile-actions {
  text-align: center;
}

/* ===== 编辑模式 ===== */
.edit-title {
  font-family: var(--font-family);
  font-size: 18px;
  font-weight: 600;
  color: var(--color-text-primary);
  margin: 0 0 28px;
}

.profile-form {
  max-width: 480px;
}

.profile-form .el-form-item__label {
  color: var(--color-text-regular);
  font-weight: 500;
}

.profile-form .el-input__inner {
  border-radius: var(--radius-sm);
  border-color: var(--color-border);
}

.profile-form .el-input__inner:focus {
  border-color: var(--color-primary);
}

/* ===== 响应式 ===== */
@media (max-width: 768px) {
  .profile-page {
    padding: 16px;
  }
  .profile-card {
    padding: 24px;
  }
  .profile-info-grid {
    grid-template-columns: 1fr;
  }
  .info-item {
    border-right: none;
  }
  .info-item:nth-last-child(2) {
    border-bottom: 1px solid var(--color-border-light);
  }
}
</style>
