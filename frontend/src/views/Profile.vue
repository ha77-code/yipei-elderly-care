<template>
  <div class="profile-page">
    <h2 class="page-title">个人信息</h2>

    <div class="profile-card">
      <template v-if="!editing">
        <div class="profile-avatar-area">
          <div class="avatar-ring avatar-ring--clickable" @click="triggerAvatarUpload" title="点击更换头像">
            <el-avatar :size="88" :src="user.avatar || undefined" icon="el-icon-user-solid" class="profile-avatar" />
            <div v-if="uploadingAvatar" class="avatar-uploading-overlay"><i class="el-icon-loading" /></div>
          </div>
          <span v-if="avatarStatusText" :class="['avatar-status', `avatar-status--${user.avatarAuditStatus}`]">{{ avatarStatusText }}</span>
          <span class="avatar-upload-hint">点击头像更换</span>
          <span :class="['role-badge', `role-badge--${(user.role || '').toLowerCase()}`]">{{ roleLabel }}</span>
          <input ref="avatarInput" type="file" accept="image/jpeg,image/png,image/gif,image/webp" style="display:none" @change="handleAvatarSelect" />
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

    <!-- 头像裁剪 -->
    <avatar-cropper v-model="cropperVisible" :src="cropSrc" @cropped="handleCropped" />
  </div>
</template>

<script>
import { getUser, setUser, ROLE_LABELS } from '@/utils/auth'
import { getUserInfo, updateUserInfo, uploadAvatar } from '@/api/user'
import request from '@/api/request'
import AvatarCropper from '@/components/AvatarCropper.vue'

export default {
  name: 'Profile',
  components: { AvatarCropper },
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
      uploadingAvatar: false,
      cropperVisible: false,
      cropSrc: '',
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
    avatarStatusText() {
      const s = this.user.avatarAuditStatus
      if (s === 0) return '头像审核中'
      if (s === 2) return '头像审核未通过，请重新上传'
      return ''
    },
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
    triggerAvatarUpload() { if (!this.uploadingAvatar) this.$refs.avatarInput.click() },
    handleAvatarSelect(e) {
      const file = e.target.files && e.target.files[0]
      e.target.value = ''
      if (!file) return
      if (!/^image\/(jpeg|png|gif|webp)$/.test(file.type)) { this.$message.error('仅支持 JPG、PNG、GIF、WEBP 格式的图片'); return }
      if (file.size > 5 * 1024 * 1024) { this.$message.error('图片大小不能超过 5MB'); return }
      const reader = new FileReader()
      reader.onload = ev => { this.cropSrc = ev.target.result; this.cropperVisible = true }
      reader.readAsDataURL(file)
    },
    async handleCropped(blob) {
      this.uploadingAvatar = true
      try {
        const res = await uploadAvatar(blob)
        const d = res.data || res
        const u = { ...this.user, avatarAuditStatus: d.avatarAuditStatus, pendingAvatar: d.pendingAvatar }
        this.user = u; setUser(u)
        this.$message.success('头像已提交，等待管理员审核')
      } catch {} finally { this.uploadingAvatar = false }
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
.avatar-ring { position: relative; padding: 4px; border-radius: 50%; background: linear-gradient(135deg, var(--color-primary-light), var(--color-primary)); margin-bottom: 8px; }
.avatar-ring--clickable { cursor: pointer; transition: transform 0.2s ease, box-shadow 0.2s ease; }
.avatar-ring--clickable:hover { transform: scale(1.04); box-shadow: 0 6px 18px rgba(0,0,0,0.12); }
.avatar-uploading-overlay { position: absolute; inset: 4px; border-radius: 50%; background: rgba(0,0,0,0.45); display: flex; align-items: center; justify-content: center; color: #fff; font-size: 22px; }
.avatar-upload-hint { font-size: 12px; color: var(--color-text-placeholder); margin-bottom: 12px; }
.avatar-status { font-size: 12px; font-weight: 550; padding: 2px 10px; border-radius: 12px; margin-bottom: 6px; }
.avatar-status--0 { background: rgba(196,168,130,.16); color: #8B7355; }
.avatar-status--2 { background: rgba(224,96,96,.12); color: #C24444; }
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
