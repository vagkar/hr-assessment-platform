<script setup>
import { scoreTag } from '@/utils/score'
import BaseButton from '@/components/ui/BaseButton.vue'

defineProps({
  result: { type: Object, required: true },
})

defineEmits(['view'])

function statusTag(status) {
  if (status === 'COMPLETED') return 'tag--ok'
  if (status === 'IN_PROGRESS') return 'tag--warn'
  return ''
}
</script>

<template>
  <div class="result-row" @click="$emit('view', result.sessionId)">
    <div class="result-row__candidate">
      <div class="result-row__name">{{ result.candidateName }}</div>
      <div class="mono result-row__email">{{ result.candidateEmail }}</div>
    </div>
    <div class="result-row__status">
      <span :class="['tag', 'tag--dot', statusTag(result.status)]">
        {{ result.status.toLowerCase().replace('_', ' ') }}
      </span>
    </div>
    <div class="result-row__score">
      <span v-if="result.score !== null" :class="['tag', scoreTag(result.score)]">{{ result.score }}%</span>
      <span v-else class="result-row__no-score">—</span>
    </div>
    <div class="result-row__date">
      {{ result.completedAt ? new Date(result.completedAt).toLocaleDateString() : '—' }}
    </div>
    <div class="result-row__action">
      <BaseButton variant="ghost" sm @click.stop="$emit('view', result.sessionId)">View</BaseButton>
    </div>
  </div>
</template>

<style scoped>
.result-row {
  display: grid;
  grid-template-columns: 1fr auto auto auto auto;
  align-items: center;
  gap: 16px;
  padding: 14px 20px;
  border-bottom: 1px solid var(--rule-soft);
  cursor: pointer;
  transition: background var(--dur-fast) var(--ease);
}
.result-row:last-child { border-bottom: none; }
.result-row:hover { background: var(--bg-2); }

.result-row__name { font-weight: 500; }
.result-row__email { font-size: 10.5px; color: var(--muted); margin-top: 2px; }
.result-row__date { color: var(--muted); font-size: 13px; white-space: nowrap; }
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
  .result-row__action { grid-column: 2; grid-row: 2; justify-self: end; }
  .result-row__status { display: none; }
  .result-row__date { display: none; }
}
</style>
