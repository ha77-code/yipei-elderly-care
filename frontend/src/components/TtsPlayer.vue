<template>
  <span class="tts-player">
    <el-dropdown
      v-if="voices.length > 1"
      size="small"
      trigger="click"
      @command="selectVoice"
    >
      <span class="voice-trigger">
        {{ selectedName }} <i class="el-icon-arrow-down el-icon--right"></i>
      </span>
      <el-dropdown-menu slot="dropdown">
        <el-dropdown-item
          v-for="v in voices"
          :key="v.voiceType"
          :command="v.voiceType"
        >{{ v.name }}</el-dropdown-item>
      </el-dropdown-menu>
    </el-dropdown>

    <el-button
      v-if="state === 'idle'"
      icon="el-icon-microphone"
      circle
      size="small"
      class="tts-btn"
      title="朗读"
      @click="play"
    />

    <el-button
      v-else-if="state === 'loading'"
      icon="el-icon-loading"
      circle
      size="small"
      class="tts-btn tts-loading"
      title="生成中..."
      disabled
    />

    <el-button
      v-else-if="state === 'playing'"
      icon="el-icon-switch-button"
      circle
      size="small"
      class="tts-btn tts-playing"
      title="停止"
      @click="stop"
    />
  </span>
</template>

<script>
import { getVoices, speakText } from '@/api/tts'

export default {
  name: 'TtsPlayer',
  props: {
    text: { type: String, required: true }
  },
  data() {
    return {
      state: 'idle',       // idle | loading | playing
      voices: [],
      selectedVoiceType: '',
      audio: null
    }
  },
  computed: {
    selectedName() {
      const v = this.voices.find(v => v.voiceType === this.selectedVoiceType)
      return v ? v.name : '音色'
    }
  },
  async created() {
    try {
      const res = await getVoices()
      this.voices = res.data || res || []
      if (this.voices.length) {
        this.selectedVoiceType = this.voices[0].voiceType
      }
    } catch {
      // voices list failed silently — use default voice
    }
  },
  beforeDestroy() {
    this.stopAudio()
  },
  methods: {
    selectVoice(voiceType) {
      this.selectedVoiceType = voiceType
    },
    async play() {
      if (!this.text || !this.text.trim()) return
      this.state = 'loading'
      try {
        const res = await speakText(this.text, this.selectedVoiceType || undefined)
        const blob = res.data || res
        const url = URL.createObjectURL(blob)
        this.stopAudio()
        const audio = new Audio(url)
        audio.onended = () => {
          this.state = 'idle'
          URL.revokeObjectURL(url)
        }
        audio.onerror = () => {
          this.state = 'idle'
          URL.revokeObjectURL(url)
          this.$message.error('播放失败')
        }
        this.audio = audio
        this.state = 'playing'
        audio.play()
      } catch {
        this.state = 'idle'
        // 错误由 axios 拦截器统一提示
      }
    },
    stop() {
      this.stopAudio()
      this.state = 'idle'
    },
    stopAudio() {
      if (this.audio) {
        this.audio.pause()
        this.audio = null
      }
    }
  }
}
</script>

<style scoped>
.tts-player {
  display: inline-flex;
  align-items: center;
  gap: 6px;
  vertical-align: middle;
}

.voice-trigger {
  font-size: 12px;
  color: var(--color-text-secondary);
  cursor: pointer;
  padding: 2px 8px;
  border-radius: 4px;
  border: 1px solid var(--color-border-light);
  background: var(--color-bg-card);
  white-space: nowrap;
  transition: border-color 0.2s;
}
.voice-trigger:hover {
  border-color: var(--color-primary-light);
}

.tts-btn {
  width: 32px;
  height: 32px;
  padding: 0;
  border: 1px solid var(--color-border-light);
  background: var(--color-bg-card);
  color: var(--color-primary-dark);
  transition: all 0.2s ease;
}
.tts-btn:hover {
  border-color: var(--color-primary-light);
  color: var(--color-primary);
}

.tts-loading {
  color: var(--color-warm) !important;
  cursor: default;
}

.tts-playing {
  background: var(--color-primary-dim) !important;
  border-color: var(--color-primary-light) !important;
  color: var(--color-primary-dark) !important;
  animation: pulse 1.5s ease-in-out infinite;
}

@keyframes pulse {
  0%, 100% { box-shadow: 0 0 0 0 rgba(122,154,126,0.3); }
  50% { box-shadow: 0 0 0 6px rgba(122,154,126,0); }
}
</style>
