<script setup>
import { computed } from 'vue'
import { useRouter } from 'vue-router'
import BaseButton from '@/components/ui/BaseButton.vue'

const props = defineProps({
  assessment: { type: Object, required: true },
  index: { type: Number, default: 0 },
})

defineEmits(['edit', 'delete'])

const router = useRouter()

const sparklinePath = computed(() => {
  const scores = [...(props.assessment.recentScores ?? [])].reverse()
  if (scores.length < 2) return null

  const W = 120, H = 44, pad = 2
  const min = Math.min(...scores)
  const max = Math.max(...scores)
  const range = max - min || 1

  const points = scores.map((s, i) => {
    const x = pad + (i / (scores.length - 1)) * (W - pad * 2)
    const y = H - pad - ((s - min) / range) * (H - pad * 2)
    return [x, y]
  })

  const line = points.map((p, i) => `${i === 0 ? 'M' : 'L'}${p[0].toFixed(1)},${p[1].toFixed(1)}`).join(' ')
  const fill = `${line} L${points[points.length - 1][0].toFixed(1)},${H} L${points[0][0].toFixed(1)},${H} Z`

  return { line, fill }
})
</script>

<template>
  <div class="arow" @click="router.push(`/assessments/${assessment.id}`)">
    <div class="arow__idx">{{ String(index + 1).padStart(2, '0') }}</div>

    <div class="arow__main">
      <div class="arow__title">{{ assessment.title }}</div>
      <div class="arow__meta">
        <span :class="['tag', 'tag--dot', assessment.isActive ? 'tag--ok' : 'tag--warn']">
          {{ assessment.isActive ? 'active' : 'inactive' }}
        </span>
        <span class="dot" />
        <span>{{ assessment.durationMinutes }} min</span>
      </div>
      <div v-if="assessment.description" class="arow__meta arow__meta--desc">
        {{ assessment.description }}
      </div>
    </div>

    <div class="arow__right">
      <svg v-if="sparklinePath" class="arow__sparkline" viewBox="0 0 120 44" preserveAspectRatio="none">
        <path :d="sparklinePath.fill" class="sparkline-fill" />
        <path :d="sparklinePath.line" class="sparkline-line" />
      </svg>
      <div class="arow__stat">
        <span class="arow__stat-val">{{ assessment.inviteCount }}</span>
        <span class="arow__stat-label">INVITES</span>
      </div>
      <div class="arow__stat">
        <span class="arow__stat-val arow__stat-val--score">{{ assessment.avgScore != null ? `${Math.round(assessment.avgScore)}%` : '—' }}</span>
        <span class="arow__stat-label">AVG SCORE</span>
      </div>
    </div>

    <div class="arow__actions" @click.stop>
      <BaseButton variant="ghost" sm @click="$emit('edit', assessment)">Edit</BaseButton>
      <BaseButton variant="danger" sm @click="$emit('delete', assessment.id)">Delete</BaseButton>
    </div>
  </div>
</template>

<style scoped>
.dot { width: 3px; height: 3px; border-radius: 50%; background: var(--faint); }
.arow__meta--desc { margin-top: 2px; }
.arow__right {
  display: flex;
  align-items: center;
  gap: 20px;
  flex-shrink: 0;
}
.arow__sparkline {
  width: 120px;
  height: 44px;
}
.sparkline-line {
  fill: none;
  stroke: var(--accent);
  stroke-width: 1.5;
}
.sparkline-fill {
  fill: var(--accent);
  opacity: 0.15;
}
.arow__stat {
  display: flex;
  flex-direction: column;
  align-items: flex-end;
  gap: 2px;
  min-width: 40px;
}
.arow__stat-val {
  font-size: 18px;
  font-weight: 700;
  color: var(--ink);
  line-height: 1;
}
.arow__stat-val--score { color: var(--accent); }
.arow__stat-label {
  font-size: 10px;
  font-weight: 600;
  letter-spacing: 0.06em;
  color: var(--muted);
}
@media (max-width: 640px) {
  .arow__right { display: none; }
}
</style>
