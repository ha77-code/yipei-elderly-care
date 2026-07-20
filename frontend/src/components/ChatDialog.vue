<template>
  <el-dialog
    :title="title"
    :visible.sync="visible"
    width="520px"
    custom-class="chat-dialog"
    @open="onOpen"
    @close="onClose"
  >
    <div class="chat-body" ref="body" v-loading="loading">
      <div v-if="!messages.length && !loading" class="chat-empty">
        还没有消息，发送第一条消息开始沟通吧
      </div>
      <div
        v-for="m in messages"
        :key="m.id"
        :class="['msg-row', m.fromUserId === myId ? 'msg-mine' : 'msg-other']"
      >
        <el-avatar :size="34" :src="m.fromAvatar || undefined" icon="el-icon-user-solid" />
        <div class="msg-content">
          <div class="msg-name">{{ m.fromName || '用户' }}</div>
          <div class="msg-bubble">{{ m.content }}</div>
          <div class="msg-time">{{ fmt(m.createdAt) }}</div>
        </div>
      </div>
    </div>

    <div class="chat-input" v-if="open">
      <el-input
        v-model="draft"
        type="textarea"
        :rows="2"
        resize="none"
        maxlength="1000"
        placeholder="输入消息，Enter 发送 / Shift+Enter 换行"
        @keydown.native.enter.exact.prevent="handleSend"
      />
      <el-button type="primary" :loading="sending" @click="handleSend">发送</el-button>
    </div>
    <div class="chat-closed" v-else>
      <i class="el-icon-lock"></i> 服务已结束，聊天通道已关闭，仅可查看历史记录
    </div>
  </el-dialog>
</template>

<script>
import { getChatMessages, sendChatMessage } from '@/api/chat'
import { getUser } from '@/utils/auth'

export default {
  name: 'ChatDialog',
  props: {
    orderId: { type: [Number, String], default: null },
    // 是否开启（可发消息）；由父组件依据订单状态传入
    chatOpen: { type: Boolean, default: false },
    title: { type: String, default: '私信沟通' }
  },
  data() {
    const u = getUser() || {}
    return { visible: false, loading: false, sending: false, messages: [], draft: '', myId: u.id, open: this.chatOpen, timer: null }
  },
  watch: {
    chatOpen(val) { this.open = val }
  },
  methods: {
    show() { this.visible = true },
    onOpen() {
      this.open = this.chatOpen
      this.fetchMessages()
      this.timer = setInterval(this.fetchMessages, 5000)
    },
    onClose() {
      if (this.timer) { clearInterval(this.timer); this.timer = null }
      this.$emit('closed')
    },
    async fetchMessages() {
      if (!this.orderId) return
      if (!this.messages.length) this.loading = true
      try {
        const res = await getChatMessages(this.orderId)
        this.messages = res.data || res || []
        this.$nextTick(this.scrollToBottom)
      } catch { /* 统一拦截 */ } finally { this.loading = false }
    },
    async handleSend() {
      const content = (this.draft || '').trim()
      if (!content) return
      this.sending = true
      try {
        await sendChatMessage(this.orderId, content)
        this.draft = ''
        await this.fetchMessages()
      } catch { /* 统一拦截 */ } finally { this.sending = false }
    },
    scrollToBottom() {
      const el = this.$refs.body
      if (el) el.scrollTop = el.scrollHeight
    },
    fmt(v) { if (!v) return ''; return String(v).replace('T', ' ').substring(0, 16) }
  },
  beforeDestroy() { if (this.timer) clearInterval(this.timer) }
}
</script>

<style scoped>
.chat-body { height: 380px; overflow-y: auto; padding: 8px 4px; background: var(--color-bg-page); border-radius: var(--radius-sm); }
.chat-empty { text-align: center; color: var(--color-text-placeholder); padding: 60px 0; font-size: 13px; }
.msg-row { display: flex; gap: 10px; margin-bottom: 16px; padding: 0 8px; }
.msg-row .msg-content { max-width: 70%; display: flex; flex-direction: column; }
.msg-name { font-size: 12px; color: var(--color-text-placeholder); margin-bottom: 4px; }
.msg-bubble { padding: 9px 13px; border-radius: 12px; font-size: 14px; line-height: 1.5; word-break: break-word; white-space: pre-wrap; }
.msg-time { font-size: 11px; color: var(--color-text-placeholder); margin-top: 4px; }
/* 对方消息：左对齐 */
.msg-other { flex-direction: row; }
.msg-other .msg-bubble { background: #fff; border: 1px solid rgba(0,0,0,0.05); color: var(--color-text-primary); border-top-left-radius: 3px; }
/* 我的消息：右对齐 */
.msg-mine { flex-direction: row-reverse; }
.msg-mine .msg-content { align-items: flex-end; }
.msg-mine .msg-bubble { background: var(--color-primary); color: #fff; border-top-right-radius: 3px; }
.chat-input { display: flex; gap: 10px; align-items: flex-end; margin-top: 14px; }
.chat-input .el-button { flex-shrink: 0; }
.chat-closed { margin-top: 14px; text-align: center; padding: 12px; font-size: 13px; color: var(--color-text-placeholder); background: var(--color-bg-page); border-radius: var(--radius-sm); }
</style>
