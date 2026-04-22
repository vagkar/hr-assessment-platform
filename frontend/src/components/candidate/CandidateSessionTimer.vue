<script setup>
import { ref, computed, onMounted, onUnmounted } from 'vue'

const props = defineProps({
  startedAt: { type: String, required: true },
  durationMinutes: { type: Number, required: true },
})

const emit = defineEmits(['timeout'])

const timeLeft = ref(0)
let interval = null

const display = computed(() => {
  const m = Math.floor(timeLeft.value / 60).toString().padStart(2, '0')
  const s = (timeLeft.value % 60).toString().padStart(2, '0')
  return `${m}:${s}`
})

const urgency = computed(() => {
  const total = props.durationMinutes * 60
  const ratio = timeLeft.value / total
  if (ratio <= 0.10) return 'urgent'
  if (ratio <= 0.25) return 'warn'
  return ''
})

onMounted(() => {
  const endTime = new Date(props.startedAt).getTime() + props.durationMinutes * 60 * 1000
  const tick = () => {
    timeLeft.value = Math.max(0, Math.floor((endTime - Date.now()) / 1000))
    if (timeLeft.value === 0) {
      clearInterval(interval)
      emit('timeout')
    }
  }
  tick()
  interval = setInterval(tick, 1000)
})

onUnmounted(() => clearInterval(interval))
</script>

<template>
  <div :class="['cand-top__timer', urgency]">
    <svg width="12" height="12" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5" stroke-linecap="round">
      <circle cx="12" cy="12" r="9"/><path d="M12 7v5l3 2"/>
    </svg>
    {{ display }}
  </div>
</template>
