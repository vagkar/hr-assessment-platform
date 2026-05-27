<script setup>
import { computed } from 'vue'
import { scoreTag } from '@/utils/score'

const props = defineProps({
  result: { type: Object, required: true },
})

defineEmits(['view'])

function statusTag(status) {
  if (status === 'COMPLETED') return 'tag--ok'
  if (status === 'IN_PROGRESS') return 'tag--warn'
  return ''
}

const initials = computed(() => {
  if (!props.result.candidateName) return '?'
  return props.result.candidateName
    .split(' ')
    .map((w) => w[0])
    .slice(0, 2)
    .join('')
    .toUpperCase()
})

const avatarColor = computed(() => {
  const colors = ['#6366f1', '#f59e0b', '#10b981', '#3b82f6', '#ec4899', '#8b5cf6', '#14b8a6']
  const hash = (props.result.candidateName || '').split('').reduce((acc, c) => acc + c.charCodeAt(0), 0)
  return colors[hash % colors.length]
})

const timeTaken = computed(() => {
  if (!props.result.startedAt || !props.result.completedAt) return '—'
  const ms = new Date(props.result.completedAt) - new Date(props.result.startedAt)
  const totalMinutes = Math.floor(ms / 60000)
  const seconds = Math.floor((ms % 60000) / 1000)
  return `${String(totalMinutes).padStart(2, '0')}:${String(seconds).padStart(2, '0')}`
})
</script>

<template>
  <div class="result-row" @click="$emit('view', result.sessionId)">
    <div class="result-row__candidate">
      <div class="result-row__avatar" :style="{ background: avatarColor }">{{ initials }}</div>
      <div>
        <div class="result-row__name">{{ result.candidateName }}</div>
        <div class="mono result-row__email">{{ result.candidateEmail }}</div>
      </div>
    </div>
    <div class="result-row__status">
      <span :class="['tag', 'tag--dot', statusTag(result.status)]">
        {{ result.status.toLowerCase().replace('_', ' ') }}
      </span>
    </div>
    <div class="result-row__time mono">{{ timeTaken }}</div>
    <div class="result-row__score">
      <span v-if="result.score !== null" :class="['tag', scoreTag(result.score)]">{{ Math.round(result.score) }}%</span>
      <span v-else class="result-row__no-score">—</span>
    </div>
  </div>
</template>

<style scoped>
.result-row {
  display: grid;
  grid-template-columns: 1fr auto auto auto;
  align-items: center;
  gap: 16px;
  padding: 12px 20px;
  border-bottom: 1px solid var(--rule-soft);
  cursor: pointer;
  transition: background var(--dur-fast) var(--ease);
}
.result-row:last-child { border-bottom: none; }
.result-row:hover { background: var(--bg-2); }

.result-row__candidate {
  display: flex;
  align-items: center;
  gap: 12px;
  min-width: 0;
}
.result-row__avatar {
  width: 32px;
  height: 32px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 11px;
  font-weight: 700;
  color: #fff;
  flex-shrink: 0;
}
.result-row__name { font-weight: 500; font-size: 13.5px; }
.result-row__email { font-size: 10.5px; color: var(--muted); margin-top: 1px; }
.result-row__time { font-size: 12.5px; color: var(--muted); white-space: nowrap; }
.result-row__no-score { color: var(--faint); }

@media (max-width: 640px) {
  .result-row {
    grid-template-columns: 1fr auto;
    grid-template-rows: auto auto;
    gap: 6px 12px;
    padding: 14px 16px;
  }
  .result-row__candidate { grid-column: 1; grid-row: 1 / 3; }
  .result-row__score { grid-column: 2; grid-row: 1; justify-self: end; }
  .result-row__status { display: none; }
  .result-row__time { display: none; }
}
</style>
