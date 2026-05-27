<script setup>
import { computed } from 'vue'
import ScoreDistributionChart from './ScoreDistributionChart.vue'

const props = defineProps({
  analytics: { type: Object, required: true },
})

const timeUsedPct = computed(() => {
  if (!props.analytics.avgCompletionMinutes || !props.analytics.allottedMinutes) return 0
  return Math.min(100, Math.round((props.analytics.avgCompletionMinutes / props.analytics.allottedMinutes) * 100))
})
</script>

<template>
  <div class="card stats-panel">
    <div class="stats-panel__section">
      <div class="stats-panel__label">Average score</div>
      <div class="stats-panel__big">
        {{ analytics.avgScore != null ? Math.round(analytics.avgScore) : '—'
        }}<span v-if="analytics.avgScore != null" class="stats-panel__unit">%</span>
      </div>
      <div v-if="analytics.medianScore != null" class="stats-panel__sub-row">
        <span>
          <span class="stats-panel__sub-label">MEDIAN</span>
          <span class="stats-panel__sub-val">{{ Math.round(analytics.medianScore) }}%</span>
        </span>
        <span>
          <span class="stats-panel__sub-label">HIGH</span>
          <span class="stats-panel__sub-val">{{ Math.round(analytics.highScore) }}%</span>
        </span>
        <span>
          <span class="stats-panel__sub-label">LOW</span>
          <span class="stats-panel__sub-val">{{ Math.round(analytics.lowScore) }}%</span>
        </span>
      </div>
    </div>

    <div class="stats-panel__section">
      <div class="stats-panel__label">Score distribution</div>
      <ScoreDistributionChart :distribution="analytics.scoreDistribution" />
    </div>

    <div class="stats-panel__section stats-panel__section--last">
      <div class="stats-panel__label">Avg. completion time</div>
      <div class="stats-panel__big">
        {{ analytics.avgCompletionMinutes ?? '—'
        }}<span v-if="analytics.avgCompletionMinutes" class="stats-panel__unit">min</span>
      </div>
      <div v-if="analytics.avgCompletionMinutes" class="stats-panel__time-sub">
        of {{ analytics.allottedMinutes }} min allotted · {{ timeUsedPct }}% used
      </div>
      <div v-if="analytics.avgCompletionMinutes" class="stats-panel__progress">
        <div class="stats-panel__progress-fill" :style="{ width: `${timeUsedPct}%` }" />
      </div>
    </div>
  </div>
</template>

<style scoped>
.stats-panel {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  margin-bottom: 28px;
  padding: 0;
  overflow: hidden;
}
.stats-panel__section {
  padding: 24px;
  border-right: 1px solid var(--rule);
}
.stats-panel__section--last { border-right: none; }
.stats-panel__label {
  font-size: 11px;
  font-weight: 600;
  letter-spacing: 0.08em;
  text-transform: uppercase;
  color: var(--muted);
  margin-bottom: 10px;
}
.stats-panel__big {
  font-size: 48px;
  font-weight: 700;
  line-height: 1;
  color: var(--ink);
  margin-bottom: 12px;
}
.stats-panel__unit {
  font-size: 20px;
  font-weight: 400;
  color: var(--muted);
  margin-left: 2px;
}
.stats-panel__sub-row { display: flex; gap: 16px; }
.stats-panel__sub-label {
  display: block;
  font-size: 9px;
  font-weight: 600;
  letter-spacing: 0.1em;
  color: var(--faint);
}
.stats-panel__sub-val {
  font-size: 14px;
  font-weight: 600;
  color: var(--ink-2);
}
.stats-panel__time-sub {
  font-size: 11.5px;
  color: var(--muted);
  margin-bottom: 10px;
}
.stats-panel__progress {
  height: 4px;
  background: var(--bg-2);
  border-radius: 2px;
  overflow: hidden;
}
.stats-panel__progress-fill {
  height: 100%;
  background: var(--accent);
  border-radius: 2px;
  transition: width 0.4s var(--ease);
}

@media (max-width: 640px) {
  .stats-panel { grid-template-columns: 1fr; }
  .stats-panel__section { border-right: none; border-bottom: 1px solid var(--rule); }
  .stats-panel__section--last { border-bottom: none; }
}
</style>
