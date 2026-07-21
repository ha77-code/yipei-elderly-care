<template>
  <el-dialog title="裁剪头像" :visible.sync="visible" width="380px" :close-on-click-modal="false" append-to-body @closed="onClosed">
    <div class="cropper-body">
      <div
        class="crop-stage"
        @mousedown="onDown" @mousemove="onMove" @mouseup="onUp" @mouseleave="onUp"
        @wheel.prevent="onWheel"
      >
        <img v-if="src" :src="src" class="crop-img" :style="imgStyle" draggable="false" @load="onImgLoad" />
        <div class="crop-mask"></div>
      </div>
      <div class="crop-controls">
        <i class="el-icon-picture-outline"></i>
        <el-slider v-model="scalePercent" :min="100" :max="300" :show-tooltip="false" class="crop-slider" />
        <i class="el-icon-picture"></i>
      </div>
      <p class="crop-hint">拖拽移动，滚轮或滑块缩放，圆圈内为最终展示区域</p>
    </div>
    <span slot="footer">
      <el-button @click="visible = false">取消</el-button>
      <el-button type="primary" :loading="processing" @click="confirm">确认上传</el-button>
    </span>
  </el-dialog>
</template>

<script>
// 轻量圆形头像裁剪：舞台为正方形，内切圆为裁剪区；拖拽平移 + 缩放，输出正方形 PNG Blob。
const STAGE = 300      // 舞台边长(px)
const OUTPUT = 320     // 输出图片边长(px)

export default {
  name: 'AvatarCropper',
  props: {
    value: { type: Boolean, default: false },
    src: { type: String, default: '' }
  },
  data() {
    return {
      visible: this.value,
      scalePercent: 100,
      offsetX: 0, offsetY: 0,
      baseW: 0, baseH: 0, natW: 0, natH: 0,
      dragging: false, startX: 0, startY: 0, startOX: 0, startOY: 0,
      processing: false
    }
  },
  computed: {
    scale() { return this.scalePercent / 100 },
    imgStyle() {
      const w = this.baseW * this.scale, h = this.baseH * this.scale
      return {
        width: w + 'px', height: h + 'px',
        left: (STAGE / 2 + this.offsetX - w / 2) + 'px',
        top: (STAGE / 2 + this.offsetY - h / 2) + 'px'
      }
    }
  },
  watch: {
    value(v) { this.visible = v; if (v) this.reset() },
    visible(v) { this.$emit('input', v) }
  },
  methods: {
    reset() { this.scalePercent = 100; this.offsetX = 0; this.offsetY = 0 },
    onImgLoad(e) {
      this.natW = e.target.naturalWidth; this.natH = e.target.naturalHeight
      // cover：较短边铺满舞台
      const ratio = Math.max(STAGE / this.natW, STAGE / this.natH)
      this.baseW = this.natW * ratio; this.baseH = this.natH * ratio
      this.reset()
    },
    onDown(e) { this.dragging = true; this.startX = e.clientX; this.startY = e.clientY; this.startOX = this.offsetX; this.startOY = this.offsetY },
    onMove(e) {
      if (!this.dragging) return
      this.offsetX = this.startOX + (e.clientX - this.startX)
      this.offsetY = this.startOY + (e.clientY - this.startY)
    },
    onUp() { this.dragging = false },
    onWheel(e) {
      const next = this.scalePercent + (e.deltaY < 0 ? 8 : -8)
      this.scalePercent = Math.min(300, Math.max(100, next))
    },
    confirm() {
      if (!this.src) { this.visible = false; return }
      this.processing = true
      const img = new Image()
      img.onload = () => {
        const canvas = document.createElement('canvas')
        canvas.width = OUTPUT; canvas.height = OUTPUT
        const ctx = canvas.getContext('2d')
        // 舞台像素 → 输出像素 的比例
        const k = OUTPUT / STAGE
        const dw = this.baseW * this.scale * k
        const dh = this.baseH * this.scale * k
        const dx = (OUTPUT / 2 + this.offsetX * k) - dw / 2
        const dy = (OUTPUT / 2 + this.offsetY * k) - dh / 2
        ctx.fillStyle = '#fff'; ctx.fillRect(0, 0, OUTPUT, OUTPUT)
        ctx.drawImage(img, dx, dy, dw, dh)
        canvas.toBlob(blob => {
          this.processing = false
          if (blob) { this.$emit('cropped', blob); this.visible = false }
        }, 'image/png')
      }
      img.onerror = () => { this.processing = false; this.$message && this.$message.error('图片处理失败') }
      img.src = this.src
    },
    onClosed() { this.reset() }
  }
}
</script>

<style scoped>
.cropper-body { display: flex; flex-direction: column; align-items: center; }
.crop-stage { position: relative; width: 300px; height: 300px; overflow: hidden; background: #f2f2f0; border-radius: 8px; cursor: move; user-select: none; }
.crop-img { position: absolute; pointer-events: none; }
.crop-mask { position: absolute; inset: 0; pointer-events: none; box-shadow: 0 0 0 9999px rgba(0,0,0,0.45); border-radius: 50%; }
.crop-controls { display: flex; align-items: center; gap: 12px; width: 300px; margin-top: 18px; color: var(--color-text-placeholder); }
.crop-controls .el-icon-picture-outline { font-size: 15px; }
.crop-controls .el-icon-picture { font-size: 20px; }
.crop-slider { flex: 1; }
.crop-hint { font-size: 12px; color: var(--color-text-placeholder); margin: 12px 0 0; text-align: center; }
</style>
