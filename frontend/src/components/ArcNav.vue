<template>
  <nav class="arc-nav">
    <div class="arc-nav-brand">益陪</div>
    <svg class="arc-svg" viewBox="-60 200 170 320" preserveAspectRatio="xMidYMid meet" @wheel="onWheel">
      <path class="arc-track" :d="trackPath" />
      <g
        v-for="(item, i) in items"
        :key="item.path"
        class="arc-node-group"
        :style="{ opacity: nodes[i].opacity }"
        @click="goTo(i)"
        @mouseenter="hoverIndex = i"
        @mouseleave="hoverIndex = null"
      >
        <circle
          class="arc-node"
          :class="{ 'is-active': i === previewIndex }"
          :cx="nodes[i].x"
          :cy="nodes[i].y"
          :r="hoverIndex === i ? 18 : 16"
        />
        <text class="arc-icon" :x="nodes[i].x" :y="nodes[i].y + 1">{{ item.icon }}</text>
        <text class="arc-label" :x="nodes[i].x" :y="nodes[i].y + 22">{{ item.label }}</text>
      </g>
    </svg>
  </nav>
</template>

<script>
import gsap from 'gsap'

const CX = -80
const CY = 350
const R = 140
const ANGLE_RANGE = Math.PI // 180°
const WHEEL_SETTLE_DELAY = 220

export default {
  name: 'ArcNav',
  props: {
    // [{ label, path, icon }]
    items: { type: Array, required: true }
  },
  data() {
    return {
      offset: 0,
      hoverIndex: null,
      previewIndex: 0,
      wheelBusy: false,
      wheelTimer: null
    }
  },
  computed: {
    trackPath() {
      return `M${CX - R},${CY} A${R},${R} 0 0,1 ${CX - R},${CY}`
    },
    baseAngles() {
      const n = this.items.length
      if (n <= 1) return [0]
      // 槽位分布：节点落在弧的内侧（±75°），首尾不足 180°，取模循环时永不重叠
      return this.items.map((_, i) => -ANGLE_RANGE / 2 + ANGLE_RANGE * ((i + 0.5) / n))
    },
    routeIndex() {
      const path = this.$route.path
      const idx = this.items.findIndex((item) => path === item.path || path.startsWith(`${item.path}/`))
      return idx === -1 ? 0 : idx
    },
    nodes() {
      return this.items.map((_, i) => {
        const angle = this.normalize(this.baseAngles[i] + this.offset)
        const edgeDist = Math.abs(angle) / (ANGLE_RANGE / 2)
        const opacity = 1 - Math.max(0, (edgeDist - 0.7) / 0.3) * 0.6
        return { x: CX + R * Math.cos(angle), y: CY + R * Math.sin(angle), opacity }
      })
    }
  },
  watch: {
    routeIndex: {
      immediate: true,
      handler(idx) {
        this.previewIndex = idx
        this.animateTo(idx)
      }
    }
  },
  beforeDestroy() {
    gsap.killTweensOf(this)
    clearTimeout(this.wheelTimer)
  },
  methods: {
    normalize(angle) {
      let a = angle
      while (a > ANGLE_RANGE / 2) a -= ANGLE_RANGE
      while (a < -ANGLE_RANGE / 2) a += ANGLE_RANGE
      return a
    },
    shortestOffset(target) {
      let diff = target - this.offset
      while (diff > ANGLE_RANGE / 2) diff -= ANGLE_RANGE
      while (diff < -ANGLE_RANGE / 2) diff += ANGLE_RANGE
      return this.offset + diff
    },
    animateTo(idx) {
      const target = this.shortestOffset(-this.baseAngles[idx])
      gsap.killTweensOf(this)
      gsap.to(this, { offset: target, duration: 0.35, ease: 'power2.out', overwrite: true })
    },
    goTo(i) {
      this.previewIndex = i
      this.animateTo(i)
      if (this.$route.path !== this.items[i].path) {
        this.$router.push(this.items[i].path)
      }
    },
    onWheel(e) {
      e.preventDefault()
      if (this.wheelBusy) return
      this.wheelBusy = true
      setTimeout(() => { this.wheelBusy = false }, 20)

      this.offset += e.deltaY * 0.0025
      let best = 0
      let bestDist = Infinity
      this.baseAngles.forEach((base, i) => {
        const dist = Math.abs(this.normalize(base + this.offset))
        if (dist < bestDist) { bestDist = dist; best = i }
      })
      this.previewIndex = best

      clearTimeout(this.wheelTimer)
      this.wheelTimer = setTimeout(() => {
        if (this.items[best].path !== this.$route.path) {
          this.$router.push(this.items[best].path)
        } else {
          this.animateTo(best)
        }
      }, WHEEL_SETTLE_DELAY)
    }
  }
}
</script>

<style scoped>
.arc-nav {
  position: relative;
  width: 100%;
  height: 100%;
  display: flex;
  align-items: center;
  overflow: visible;
}
.arc-nav-brand {
  position: absolute;
  top: 32px;
  left: 24px;
  font-family: 'Noto Serif SC', serif;
  font-size: 16px;
  font-weight: 700;
  color: rgba(78, 106, 56, 0.85);
  letter-spacing: 0.08em;
  z-index: 1;
}
.arc-svg {
  width: 100%;
  height: 100%;
  cursor: grab;
  user-select: none;
  overflow: visible;
}
.arc-svg:active { cursor: grabbing; }
.arc-track { fill: none; stroke: rgba(160, 205, 105, 0.6); stroke-width: 1; }
.arc-node-group { cursor: pointer; }
.arc-node {
  fill: rgba(255, 255, 255, 0.6);
  stroke: rgba(160, 205, 105, 0.45);
  stroke-width: 1;
  transition: r 0.2s ease, fill 0.3s ease, stroke 0.3s ease, filter 0.3s ease;
}
.arc-node.is-active {
  fill: rgba(160, 210, 110, 0.88);
  stroke: rgba(140, 195, 90, 0.92);
  filter: drop-shadow(0 0 14px rgba(180, 215, 120, 0.6)) drop-shadow(0 0 28px rgba(240, 210, 120, 0.3));
}
.arc-icon {
  fill: rgba(140, 195, 90, 0.78);
  font-size: 13px;
  text-anchor: middle;
  dominant-baseline: central;
  pointer-events: none;
  transition: fill 0.3s ease;
}
.arc-node-group:hover .arc-icon { fill: rgba(160, 210, 110, 0.95); }
.arc-label {
  fill: rgba(96, 110, 82, 0.7);
  font-size: 10px;
  text-anchor: middle;
  letter-spacing: 0.05em;
  pointer-events: none;
  transition: fill 0.3s ease;
}
.arc-node-group.active .arc-icon { fill: #fff; }
.arc-node-group.active .arc-label { fill: rgba(140, 195, 90, 0.95); font-weight: 600; }
.arc-node-group:hover .arc-label { fill: rgba(78, 106, 56, 0.85); }
.arc-node-group:hover .arc-node {
  fill: rgba(255, 245, 210, 0.85);
  stroke: rgba(240, 210, 130, 0.6);
  filter: drop-shadow(0 0 8px rgba(255, 225, 140, 0.45));
}
</style>
