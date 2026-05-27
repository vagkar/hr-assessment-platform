<script setup>
defineProps({
  questions: { type: Array, required: true },
})

function difficultyClass(rate) {
  if (rate >= 70) return 'diff--ok'
  if (rate >= 40) return 'diff--warn'
  return 'diff--bad'
}
</script>

<template>
  <div class="card diff-panel">
    <div class="diff-panel__title">Per-question difficulty</div>
    <div class="diff-panel__list">
      <div
        v-for="(q, i) in questions"
        :key="q.questionId"
        class="diff-item"
      >
        <div class="diff-item__header">
          <span class="mono diff-item__num">Q{{ i + 1 }}</span>
          <span class="diff-item__text">{{ q.questionText }}</span>
          <span class="mono diff-item__pct">{{ Math.round(q.correctRate) }}%</span>
        </div>
        <div class="diff-item__bar-wrap">
          <div
            class="diff-item__bar"
            :class="difficultyClass(q.correctRate)"
            :style="{ width: `${q.correctRate}%` }"
          />
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped>
.diff-panel { padding: 20px; }
.diff-panel__title {
  font-size: 11px;
  font-weight: 600;
  letter-spacing: 0.08em;
  text-transform: uppercase;
  color: var(--muted);
  margin-bottom: 16px;
}
.diff-panel__list { display: flex; flex-direction: column; gap: 12px; }

.diff-item__header {
  display: flex;
  align-items: baseline;
  gap: 6px;
  margin-bottom: 5px;
}
.diff-item__num {
  font-size: 10px;
  color: var(--faint);
  flex-shrink: 0;
}
.diff-item__text {
  font-size: 12.5px;
  color: var(--ink-2);
  flex: 1;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}
.diff-item__pct {
  font-size: 12px;
  font-weight: 600;
  color: var(--ink);
  flex-shrink: 0;
}
.diff-item__bar-wrap {
  height: 3px;
  background: var(--bg-2);
  border-radius: 2px;
  overflow: hidden;
}
.diff-item__bar {
  height: 100%;
  border-radius: 2px;
  transition: width 0.3s var(--ease);
}
.diff--ok { background: var(--ok); }
.diff--warn { background: var(--warn); }
.diff--bad { background: var(--bad); }
</style>
