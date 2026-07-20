<template>
  <div class="profile-page">
    <h2 class="page-title">个人信息</h2>

    <div class="profile-card">
      <template v-if="!editing">
        <div class="profile-avatar-area">
          <div class="avatar-ring">
            <el-avatar :size="88" icon="el-icon-user-solid" class="profile-avatar" />
          </div>
          <span :class="['role-badge', `role-badge--${(user.role || '').toLowerCase()}`]">{{ roleLabel }}</span>
        </div>

        <div class="info-grid">
          <div class="info-cell" v-for="item in infoItems" :key="item.key">
            <span class="info-label">{{ item.label }}</span>
            <span class="info-value">{{ item.value }}</span>
          </div>
        </div>

        <div class="profile-actions">
          <el-button type="primary" icon="el-icon-edit" size="medium" round @click="startEdit">修改资料</el-button>
          <el-button icon="el-icon-lock" size="medium" round plain @click="showPwdDialog = true">修改密码</el-button>
        </div>
      </template>

      <template v-else>
        <div class="edit-header">
          <h3 class="edit-title">修改个人资料</h3>
          <span class="edit-hint">完善信息，让服务更贴心</span>
        </div>
        <el-form ref="profileForm" :model="editForm" :rules="rules" label-width="80px" size="medium" class="profile-form">
          <el-form-item label="用户名"><el-input v-model="user.username" disabled /></el-form-item>
          <el-form-item label="昵称" prop="nickname"><el-input v-model="editForm.nickname" placeholder="请输入昵称" maxlength="100" show-word-limit clearable /></el-form-item>
          <el-form-item label="手机号" prop="phone"><el-input v-model="editForm.phone" placeholder="请输入手机号" maxlength="11" clearable /></el-form-item>
          <el-form-item>
            <el-button type="primary" :loading="saving" @click="handleSave">保存修改</el-button>
            <el-button @click="cancelEdit">取消</el-button>
          </el-form-item>
        </el-form>
      </template>
    </div>

    <!-- Modify Password Dialog -->
    <el-dialog title="修改密码" :visible.sync="showPwdDialog" width="400px" :close-on-click-modal="false">
      <el-form ref="pwdForm" :model="pwdForm" :rules="pwdRules" label-width="80px" size="medium">
        <el-form-item label="旧密码" prop="oldPassword"><el-input v-model="pwdForm.oldPassword" type="password" placeholder="请输入旧密码" show-password /></el-form-item>
        <el-form-item label="新密码" prop="newPassword"><el-input v-model="pwdForm.newPassword" type="password" placeholder="请输入新密码" show-password /></el-form-item>
      </el-form>
      <span slot="footer">
        <el-button @click="showPwdDialog = false">取消</el-button>
        <el-button type="primary" :loading="changingPwd" @click="handleChangePwd">确认修改</el-button>
      </span>
    </el-dialog>
  </div>
</template>

<script>
import { getUser, setUser, ROLE_LABELS } from '@/utils/auth'
import { getUserInfo, updateUserInfo } from '@/api/user'
import request from '@/api/request'

export default {
  name: 'Profile',
  data() {
    const user = getUser() || {}
    return {
      user: { ...user },
      editing: false,
      editForm: { nickname: user.nickname || '', phone: user.phone || '' },
      rules: {
        nickname: [{ required: true, message: '请输入昵称', trigger: 'blur' }, { max: 100, message: '昵称不超过100个字符', trigger: 'blur' }],
        phone: [{ required: true, message: '请输入手机号', trigger: 'blur' }, { pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号', trigger: 'blur' }]
      },
      saving: false,
      showPwdDialog: false,
      pwdForm: { oldPassword: '', newPassword: '' },
      pwdRules: {
        oldPassword: [{ required: true, message: '请输入旧密码', trigger: 'blur' }],
        newPassword: [{ required: true, message: '请输入新密码', trigger: 'blur' }, { min: 6, message: '密码长度不少于6位', trigger: 'blur' }]
      },
      changingPwd: false
    }
  },
  computed: {
    roleLabel() { return ROLE_LABELS[this.user.role] || '客户' },
    infoItems() {
      return [
        { key: 'username', label: '用户名', value: this.user.username || '-' },
        { key: 'nickname', label: '昵称', value: this.user.nickname || '-' },
        { key: 'phone', label: '手机号', value: this.user.phone || '-' },
        { key: 'role', label: '角色', value: this.roleLabel },
        { key: 'status', label: '状态', value: this.user.status === 1 ? '正常' : '已禁用' },
        { key: 'createdAt', label: '注册时间', value: this.fmt(this.user.createdAt || this.user.createAt || this.user.create_at) }
      ]
    }
  },
  created() { this.fetchUserInfo() },
  methods: {
    async fetchUserInfo() {
      try { const res = await getUserInfo(); const d = res.data || res; this.user = { ...d }; setUser(d) } catch {}
    },
    startEdit() { this.editForm.nickname = this.user.nickname || ''; this.editForm.phone = this.user.phone || ''; this.editing = true },
    cancelEdit() { this.editing = false },
    async handleSave() {
      try { await this.$refs.profileForm.validate() } catch { return }
      this.saving = true
      try {
        await updateUserInfo({ nickname: this.editForm.nickname, phone: this.editForm.phone })
        const u = { ...this.user, nickname: this.editForm.nickname, phone: this.editForm.phone }
        this.user = u; setUser(u); this.editing = false
        this.$message.success('个人信息修改成功')
      } catch {} finally { this.saving = false }
    },
    async handleChangePwd() {
      try { await this.$refs.pwdForm.validate() } catch { return }
      this.changingPwd = true
      try {
        await request({ url: '/api/user/password', method: 'put', data: this.pwdForm, headers: { 'X-User-Id': this.user.id } })
        this.$message.success('密码修改成功'); this.showPwdDialog = false
        this.pwdForm = { oldPassword: '', newPassword: '' }
      } catch {} finally { this.changingPwd = false }
    },
    fmt(v) { if (!v) return '-'; return String(v).replace('T', ' ').substring(0, 16) }
  }
}
</script>

<style scoped>
.profile-page { padding: 36px 40px; max-width: 680px; }
.page-title { font-size: 22px; font-weight: 700; color: var(--brand-cream-100); margin: 0 0 28px; }

.profile-card { background: #fff; border: 1px solid rgba(0,0,0,0.04); border-radius: var(--radius-lg); padding: 48px; box-shadow: var(--shadow-sm); }

/* Avatar */
.profile-avatar-area { display: flex; flex-direction: column; align-items: center; margin-bottom: 40px; }
.avatar-ring { padding: 4px; border-radius: 50%; background: linear-gradient(135deg, var(--color-primary-light), var(--color-primary)); margin-bottom: 16px; }
.profile-avatar { background: #fff; color: var(--color-primary-dark); }
.role-badge { padding: 5px 18px; border-radius: 20px; font-size: 13px; font-weight: 550; }
.role-badge--customer { background: var(--color-primary-dim); color: var(--color-primary-dark); }
.role-badge--companion { background: var(--color-warm-dim); color: var(--color-warm-dark); }
.role-badge--admin { background: rgba(0,0,0,0.05); color: var(--color-text-secondary); }

/* Info Grid */
.info-grid { display: grid; grid-template-columns: 1fr 1fr; border: 1px solid rgba(0,0,0,0.04); border-radius: var(--radius-md); overflow: hidden; margin-bottom: 36px; }
.info-cell { padding: 18px 22px; border-bottom: 1px solid rgba(0,0,0,0.03); border-right: 1px solid rgba(0,0,0,0.03); transition: background 0.2s ease; cursor: default; }
.info-cell:hover { background: #FAFAF8; }
.info-cell:nth-child(even) { border-right: none; }
.info-cell:nth-last-child(-n+2) { border-bottom: none; }
.info-label { font-size: 12px; color: var(--color-text-placeholder); font-weight: 550; text-transform: uppercase; letter-spacing: 0.04em; display: block; margin-bottom: 4px; }
.info-value { font-size: 15px; color: var(--brand-cream-100); font-weight: 550; }

.profile-actions { text-align: center; display: flex; gap: 12px; justify-content: center; }

/* Edit Mode */
.edit-header { margin-bottom: 28px; }
.edit-title { font-size: 18px; font-weight: 650; color: var(--brand-cream-100); margin: 0 0 4px; }
.edit-hint { font-size: 13px; color: var(--color-text-placeholder); }
.profile-form { max-width: 440px; }

@media (max-width: 768px) {
  .profile-page { padding: 16px; }
  .profile-card { padding: 28px 20px; }
  .info-grid { grid-template-columns: 1fr; }
  .info-cell { border-right: none; }
  .info-cell:nth-last-child(2) { border-bottom: 1px solid rgba(0,0,0,0.03); }
}
</style>
