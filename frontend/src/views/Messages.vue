<template>
  <div class="page-wrap">
    <h2 class="page-title">我的消息</h2>
    <div class="notification-panel" v-loading="notificationLoading">
      <div class="notification-head">
        <div>
          <h3>系统通知</h3>
          <span>{{ notifications.length ? `共 ${notifications.length} 条` : '暂无通知' }}</span>
        </div>
        <el-button v-if="notificationUnread" type="text" @click="markAllRead">全部标记已读</el-button>
      </div>
      <div v-if="!notifications.length && !notificationLoading" class="notification-empty">暂无新通知</div>
      <button v-for="n in notifications" :key="n.id" :class="['notification-item', { unread: !n.isRead }]" @click="readNotification(n)">
        <span class="notification-dot"></span>
        <span class="notification-body"><strong>{{ n.title }}</strong><small>{{ n.content }}</small></span>
        <time>{{ fmt(n.createdAt) }}</time>
      </button>
    </div>

    <div class="chat-center">
      <!-- 左侧：会话列表 -->
      <div class="conv-list" v-loading="convLoading">
        <div v-if="!conversations.length && !convLoading" class="conv-empty">
          暂无聊天会话<br /><span>接单或下单后即可与对方沟通</span>
        </div>
        <div
          v-for="c in conversations"
          :key="c.orderId"
          :class="['conv-item', { active: current && current.orderId === c.orderId }]"
          @click="selectConversation(c)"
        >
          <el-badge :value="c.unreadCount" :hidden="!c.unreadCount" class="conv-badge">
            <el-avatar :size="42" :src="c.counterpartAvatar || undefined" icon="el-icon-user-solid" />
          </el-badge>
          <div class="conv-main">
            <div class="conv-top">
              <span class="conv-name">{{ c.counterpartName || '用户' }}</span>
              <span class="conv-time">{{ fmtShort(c.lastTime) }}</span>
            </div>
            <div class="conv-sub">
              <span class="conv-last">{{ c.lastMessage || '暂无消息，点击开始聊天' }}</span>
            </div>
            <div class="conv-meta">
              <el-tag size="mini" :type="c.chatOpen ? 'success' : 'info'" effect="plain">
                订单#{{ c.orderId }} · {{ c.chatOpen ? '进行中' : '已结束' }}
              </el-tag>
            </div>
          </div>
        </div>
      </div>

      <!-- 右侧：聊天记录 -->
      <div class="conv-thread">
        <template v-if="current">
          <div class="thread-head">
            <span class="thread-title">{{ current.counterpartName || '用户' }}</span>
            <span class="thread-sub">{{ current.hospitalName || '' }} {{ current.serviceType || '' }} · 订单#{{ current.orderId }}</span>
          </div>
          <div class="thread-body" ref="body" v-loading="msgLoading">
            <div v-if="!messages.length && !msgLoading" class="thread-empty">发送第一条消息开始沟通吧</div>
            <div
              v-for="m in messages"
              :key="m.id"
              :class="['msg-row', m.fromUserId === myId ? 'msg-mine' : 'msg-other']"
            >
              <el-avatar :size="34" :src="m.fromAvatar || undefined" icon="el-icon-user-solid" />
              <div class="msg-content">
                <div class="msg-bubble">{{ m.content }}</div>
                <div class="msg-time">{{ fmt(m.createdAt) }}</div>
              </div>
            </div>
          </div>
          <div class="thread-input" v-if="current.chatOpen">
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
          <div class="thread-closed" v-else>
            <i class="el-icon-lock"></i> 服务已结束，聊天通道已关闭，仅可查看历史记录
          </div>
        </template>
        <div v-else class="thread-placeholder">
          <i class="el-icon-chat-line-round"></i>
          <p>从左侧选择一个会话查看聊天记录</p>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import { getConversations, getChatMessages, sendChatMessage } from '@/api/chat'
import { getNotifications, markNotificationRead, markAllNotificationsRead } from '@/api/notification'
import { getUser } from '@/utils/auth'

export default {
  name: 'Messages',
  data() {
    const u = getUser() || {}
    return {
      myId: u.id,
      conversations: [], convLoading: false,
      current: null, messages: [], msgLoading: false,
      draft: '', sending: false, timer: null,
      notifications: [], notificationLoading: false, notificationUnread: 0
    }
  },
  created() {
    const pre = this.$route.query.orderId
    this.fetchConversations(pre ? Number(pre) : null)
    this.fetchNotifications()
  },
  methods: {
    async fetchNotifications() {
      this.notificationLoading = true
      try {
        const res = await getNotifications()
        this.notifications = res.data || res || []
        this.notificationUnread = this.notifications.filter(n => !n.isRead).length
      } catch { this.notifications = [] } finally { this.notificationLoading = false }
    },
    async readNotification(n) {
      if (!n.isRead) {
        try { await markNotificationRead(n.id); n.isRead = 1; this.notificationUnread = Math.max(0, this.notificationUnread - 1) } catch {}
      }
    },
    async markAllRead() {
      try { await markAllNotificationsRead(); this.notifications.forEach(n => { n.isRead = 1 }); this.notificationUnread = 0 } catch {}
    },
    async fetchConversations(autoSelectOrderId) {
      this.convLoading = true
      try {
        const res = await getConversations()
        this.conversations = res.data || res || []
        if (autoSelectOrderId) {
          const hit = this.conversations.find(c => c.orderId === autoSelectOrderId)
          if (hit) this.selectConversation(hit)
        }
      } catch { this.conversations = [] } finally { this.convLoading = false }
    },
    async selectConversation(c) {
      this.current = c
      this.draft = ''
      await this.fetchMessages()
      // 打开会话即视为已读，清掉列表红点
      c.unreadCount = 0
      this.restartPolling()
    },
    async fetchMessages() {
      if (!this.current) return
      if (!this.messages.length) this.msgLoading = true
      try {
        const res = await getChatMessages(this.current.orderId)
        this.messages = res.data || res || []
        this.$nextTick(this.scrollToBottom)
      } catch { /* 统一拦截 */ } finally { this.msgLoading = false }
    },
    async handleSend() {
      const content = (this.draft || '').trim()
      if (!content || !this.current) return
      this.sending = true
      try {
        await sendChatMessage(this.current.orderId, content)
        this.draft = ''
        await this.fetchMessages()
      } catch { /* 统一拦截 */ } finally { this.sending = false }
    },
    restartPolling() {
      if (this.timer) clearInterval(this.timer)
      this.timer = setInterval(this.fetchMessages, 5000)
    },
    scrollToBottom() {
      const el = this.$refs.body
      if (el) el.scrollTop = el.scrollHeight
    },
    fmt(v) { if (!v) return ''; return String(v).replace('T', ' ').substring(0, 16) },
    fmtShort(v) { if (!v) return ''; return String(v).replace('T', ' ').substring(5, 16) }
  },
  beforeDestroy() { if (this.timer) clearInterval(this.timer) }
}
</script>

<style scoped>
.page-wrap { padding: 24px 32px; height: calc(100vh - 66px); display: flex; flex-direction: column; }
.page-title { font-size: 20px; font-weight: 700; color: rgba(78,106,56,0.92); margin: 0 0 14px; font-family: 'Noto Serif SC', serif; }

.notification-panel { margin-bottom: 18px; padding: 18px 22px; background: rgba(255,255,255,0.58); backdrop-filter: blur(14px); -webkit-backdrop-filter: blur(14px); border: 1px solid rgba(255,255,255,0.7); border-radius: 12px; box-shadow: 0 8px 22px -12px rgba(50,60,30,0.15); }
.notification-head { display: flex; align-items: center; justify-content: space-between; margin-bottom: 8px; }
.notification-head h3 { margin: 0; color: rgba(46,60,38,0.88); font-size: 16px; font-weight: 700; }
.notification-head span { color: rgba(130,140,116,0.7); font-size: 13px; }
.notification-item { width: 100%; display: flex; align-items: flex-start; gap: 12px; padding: 13px 4px; border: 0; border-top: 1px solid rgba(150,140,110,0.1); background: transparent; color: rgba(46,60,38,0.85); text-align: left; cursor: pointer; font: inherit; }
.notification-item:hover { background: rgba(108,140,80,0.05); }
.notification-item time { margin-left: auto; color: rgba(130,140,116,0.65); font-size: 12px; white-space: nowrap; }
.notification-dot { width: 9px; height: 9px; margin-top: 7px; border-radius: 50%; background: transparent; flex: 0 0 9px; }
.notification-item.unread .notification-dot { background: rgba(200,150,70,0.85); }
.notification-body { display: flex; flex-direction: column; gap: 3px; min-width: 0; }
.notification-body strong { font-size: 14px; }
.notification-body small { color: rgba(96,110,82,0.75); font-size: 13px; line-height: 1.5; }
.notification-empty { padding: 12px 0 4px; color: rgba(130,140,116,0.6); font-size: 14px; }

.chat-center { display: flex; flex: 1; min-height: 420px; background: rgba(255,255,255,0.58); backdrop-filter: blur(14px); -webkit-backdrop-filter: blur(14px); border: 1px solid rgba(255,255,255,0.7); border-radius: 14px; overflow: hidden; box-shadow: 0 12px 36px -16px rgba(50,60,30,0.2), inset 0 1px 0 rgba(255,255,255,0.6); }
/* 左侧列表 */
.conv-list { width: 300px; flex-shrink: 0; border-right: 1px solid rgba(150,140,110,0.14); overflow-y: auto; background: rgba(248,250,240,0.3); }
.conv-empty { text-align: center; color: rgba(130,140,116,0.8); padding: 60px 16px; font-size: 14px; line-height: 2; }
.conv-empty span { font-size: 12px; color: rgba(130,140,116,0.6); }
.conv-item { display: flex; gap: 12px; padding: 14px 16px; cursor: pointer; border-bottom: 1px solid rgba(150,140,110,0.08); transition: all .2s; }
.conv-item:hover { background: rgba(108,140,80,0.07); }
.conv-item.active { background: rgba(108,140,80,0.12); box-shadow: inset 3px 0 0 rgba(108,140,80,0.5); }
.conv-badge { flex-shrink: 0; }
.conv-main { flex: 1; min-width: 0; }
.conv-top { display: flex; justify-content: space-between; align-items: center; }
.conv-name { font-size: 14px; font-weight: 700; color: rgba(46,60,38,0.88); }
.conv-time { font-size: 11px; color: rgba(130,140,116,0.7); }
.conv-sub { margin: 2px 0 4px; }
.conv-last { font-size: 13px; color: rgba(96,110,82,0.75); display: block; overflow: hidden; text-overflow: ellipsis; white-space: nowrap; }
.conv-meta { font-size: 11px; }
/* 右侧会话 */
.conv-thread { flex: 1; display: flex; flex-direction: column; min-width: 0; background: rgba(255,255,255,0.3); }
.thread-head { padding: 14px 20px; border-bottom: 1px solid rgba(150,140,110,0.14); background: rgba(255,255,255,0.4); }
.thread-title { font-size: 16px; font-weight: 700; color: rgba(78,106,56,0.9); font-family: 'Noto Serif SC', serif; }
.thread-sub { font-size: 12px; color: rgba(130,140,116,0.7); margin-left: 10px; }
.thread-body { flex: 1; overflow-y: auto; padding: 16px 20px; background: rgba(248,250,240,0.25); }
.thread-empty { text-align: center; color: rgba(130,140,116,0.7); padding: 50px 0; font-size: 13px; }
.msg-row { display: flex; gap: 10px; margin-bottom: 16px; }
.msg-row .msg-content { max-width: 65%; display: flex; flex-direction: column; }
.msg-bubble { padding: 10px 14px; border-radius: 14px; font-size: 14px; line-height: 1.55; word-break: break-word; white-space: pre-wrap; }
.msg-time { font-size: 11px; color: rgba(130,140,116,0.65); margin-top: 4px; }
.msg-other { flex-direction: row; }
.msg-other .msg-bubble { background: rgba(255,255,255,0.8); border: 1px solid rgba(150,140,110,0.12); color: rgba(46,60,38,0.85); border-top-left-radius: 4px; box-shadow: 0 1px 3px rgba(0,0,0,0.03); }
.msg-mine { flex-direction: row-reverse; }
.msg-mine .msg-content { align-items: flex-end; }
.msg-mine .msg-bubble { background: linear-gradient(135deg, rgba(108,140,80,0.85), rgba(78,106,56,0.85)); color: #fff; border-top-right-radius: 4px; }
.thread-input { display: flex; gap: 12px; align-items: flex-end; padding: 14px 20px; border-top: 1px solid rgba(150,140,110,0.14); background: rgba(255,255,255,0.4); }
.thread-input .el-button { flex-shrink: 0; }
.thread-closed { padding: 16px 20px; text-align: center; font-size: 13px; color: rgba(130,140,116,0.7); border-top: 1px solid rgba(150,140,110,0.14); background: rgba(248,250,240,0.4); }
.thread-placeholder { flex: 1; display: flex; flex-direction: column; align-items: center; justify-content: center; color: rgba(130,140,116,0.55); }
.thread-placeholder i { font-size: 56px; opacity: .2; margin-bottom: 14px; color: rgba(108,140,80,0.5); }
</style>
