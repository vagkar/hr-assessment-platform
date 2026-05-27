<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useAssessmentStore } from '@/stores/assessment'
import { getResults, getSessionDetail } from '@/api/results'
import { getAssessmentAnalytics } from '@/api/assessments'
import ResultRow from '@/components/results/ResultRow.vue'
import SessionDetailPanel from '@/components/results/SessionDetailPanel.vue'
import BaseButton from '@/components/ui/BaseButton.vue'
import { Bar } from 'vue-chartjs'
import { Chart as ChartJS, BarElement, CategoryScale, LinearScale, Tooltip } from 'chart.js'

ChartJS.register(BarElement, CategoryScale, LinearScale, Tooltip)

const route = useRoute()
const router = useRouter()
const assessmentStore = useAssessmentStore()

const assessmentId = Number(route.params.id)
const results = ref([])
const analytics = ref(null)
const selectedSession = ref(null)
const error = ref(null)
const accentColor = ref('rgb(100,100,100)')

const assessment = computed(() =>
  assessmentStore.assessments.find((a) => a.id === assessmentId)
)

const completedCount = computed(() =>
  results.value.filter((r) => r.status === 'COMPLETED').length
)

const sortedResults = computed(() =>
  [...results.value].sort((a, b) => {
    if (b.score === null) return -1
    if (a.score === null) return 1
    return b.score - a.score
  })
)

const timeUsedPct = computed(() => {
  if (!analytics.value?.avgCompletionMinutes || !analytics.value?.allottedMinutes) return 0
  return Math.min(100, Math.round((analytics.value.avgCompletionMinutes / analytics.value.allottedMinutes) * 100))
})

const completionRate = computed(() => {
  if (!analytics.value?.totalInvites) return 0
  return Math.round((analytics.value.completedCount / analytics.value.totalInvites) * 100)
})

function resolveColor(cssVar) {
  const canvas = document.createElement('canvas')
  canvas.width = canvas.height = 1
  const ctx = canvas.getContext('2d')
  ctx.fillStyle = getComputedStyle(document.documentElement).getPropertyValue(cssVar).trim()
  ctx.fillRect(0, 0, 1, 1)
  const [r, g, b] = ctx.getImageData(0, 0, 1, 1).data
  return `rgb(${r},${g},${b})`
}

const chartData = computed(() => {
  if (!analytics.value) return null
  const dist = analytics.value.scoreDistribution
  const maxIdx = dist.indexOf(Math.max(...dist))
  return {
    labels: ['0–24', '25–49', '50–74', '75–100'],
    datasets: [{
      data: dist,
      backgroundColor: dist.map((_, i) =>
        i === maxIdx ? accentColor.value : 'rgba(128,128,128,0.2)'
      ),
      borderRadius: 3,
      borderWidth: 0,
    }],
  }
})

const chartOptions = {
  responsive: true,
  maintainAspectRatio: false,
  plugins: {
    legend: { display: false },
    tooltip: {
      callbacks: {
        label: (ctx) => ` ${ctx.raw} candidate${ctx.raw !== 1 ? 's' : ''}`,
      },
    },
  },
  scales: {
    x: {
      grid: { display: false },
      ticks: { font: { size: 10 }, color: 'rgba(128,128,128,0.7)' },
      border: { display: false },
    },
    y: { display: false },
  },
}

function difficultyClass(rate) {
  if (rate >= 70) return 'diff--ok'
  if (rate >= 40) return 'diff--warn'
  return 'diff--bad'
}

onMounted(async () => {
  if (assessmentStore.assessments.length === 0) await assessmentStore.fetchAll()
  try {
    const [resultsRes, analyticsRes] = await Promise.all([
      getResults(assessmentId),
      getAssessmentAnalytics(assessmentId),
    ])
    results.value = resultsRes.data
    analytics.value = analyticsRes.data
  } catch {
    error.value = 'Failed to load results.'
  }
  accentColor.value = resolveColor('--accent')
})

async function openDetail(sessionId) {
  const res = await getSessionDetail(sessionId)
  selectedSession.value = res.data
}

function closeDetail() {
  selectedSession.value = null
}
</script>

<template>
  <div class="page__inner fade-in">
    <button class="back-link" @click="router.push(`/assessments/${assessmentId}`)">
      ← Back to assessment
    </button>

    <header class="page__header">
      <div class="page__title">
        <span class="eyebrow">{{ assessment?.title }} · Results</span>
        <h1>{{ completedCount }} <em>completed</em></h1>
        <p class="page__subtitle">
          {{ results.length }} total invite{{ results.length !== 1 ? 's' : '' }}
          <template v-if="results.length > 0"> · {{ completionRate }}% completion rate</template>
        </p>
      </div>
      <div class="page__actions">
        <BaseButton variant="ghost" disabled>Export CSV</BaseButton>
        <BaseButton variant="ghost" disabled>Send reminders</BaseButton>
      </div>
    </header>

    <p v-if="error" class="error-text results__error">{{ error }}</p>

    <!-- Analytics panel -->
    <div v-if="analytics" class="card analytics-panel">
      <div class="analytics-panel__section">
        <div class="analytics-panel__label">Average score</div>
        <div class="analytics-panel__big">
          {{ analytics.avgScore != null ? Math.round(analytics.avgScore) : '—'
          }}<span v-if="analytics.avgScore != null" class="analytics-panel__unit">%</span>
        </div>
        <div v-if="analytics.medianScore != null" class="analytics-panel__sub-row">
          <span>
            <span class="analytics-panel__sub-label">MEDIAN</span>
            <span class="analytics-panel__sub-val">{{ Math.round(analytics.medianScore) }}%</span>
          </span>
          <span>
            <span class="analytics-panel__sub-label">HIGH</span>
            <span class="analytics-panel__sub-val">{{ Math.round(analytics.highScore) }}%</span>
          </span>
          <span>
            <span class="analytics-panel__sub-label">LOW</span>
            <span class="analytics-panel__sub-val">{{ Math.round(analytics.lowScore) }}%</span>
          </span>
        </div>
      </div>

      <div class="analytics-panel__section">
        <div class="analytics-panel__label">Score distribution</div>
        <div class="analytics-panel__chart-wrap">
          <Bar v-if="chartData" :data="chartData" :options="chartOptions" />
        </div>
      </div>

      <div class="analytics-panel__section analytics-panel__section--last">
        <div class="analytics-panel__label">Avg. completion time</div>
        <div class="analytics-panel__big">
          {{ analytics.avgCompletionMinutes ?? '—'
          }}<span v-if="analytics.avgCompletionMinutes" class="analytics-panel__unit">min</span>
        </div>
        <div v-if="analytics.avgCompletionMinutes" class="analytics-panel__time-sub">
          of {{ analytics.allottedMinutes }} min allotted · {{ timeUsedPct }}% used
        </div>
        <div v-if="analytics.avgCompletionMinutes" class="analytics-panel__progress">
          <div class="analytics-panel__progress-fill" :style="{ width: `${timeUsedPct}%` }" />
        </div>
      </div>
    </div>

    <!-- Empty state -->
    <div v-if="results.length === 0 && !error" class="empty-state">
      <p class="empty-title">No results yet</p>
      <p class="text-muted">Results will appear here once candidates complete the assessment.</p>
    </div>

    <!-- Main 2-col layout -->
    <div v-else class="results-layout">
      <div class="results-layout__main">
        <div class="section">
          <h2>Candidates</h2>
          <span class="section__eyebrow">
            <span class="num">{{ String(results.length).padStart(2, '0') }} total · sorted by score</span>
          </span>
        </div>

        <div class="card results-list">
          <div class="results-list__header">
            <span>Candidate</span>
            <span>Status</span>
            <span>Time</span>
            <span>Score</span>
          </div>
          <ResultRow
            v-for="r in sortedResults"
            :key="r.sessionId"
            :result="r"
            @view="openDetail"
          />
        </div>
      </div>

      <aside class="results-layout__side">
        <div v-if="analytics?.perQuestionStats?.length" class="card diff-panel">
          <div class="diff-panel__title">Per-question difficulty</div>
          <div class="diff-panel__list">
            <div
              v-for="(q, i) in analytics.perQuestionStats"
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

        <SessionDetailPanel
          v-if="selectedSession"
          :session="selectedSession"
          @close="closeDetail"
        />
        <div v-else class="card detail-placeholder">
          <div class="detail-placeholder__title">Detail view</div>
          <p class="detail-placeholder__hint">Select a candidate to see their answer-by-answer breakdown.</p>
        </div>
      </aside>
    </div>
  </div>
</template>

<style scoped>
.results__error { margin-bottom: 20px; }

.analytics-panel {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  margin-bottom: 28px;
  padding: 0;
  overflow: hidden;
}
.analytics-panel__section {
  padding: 24px;
  border-right: 1px solid var(--rule);
}
.analytics-panel__section--last { border-right: none; }
.analytics-panel__label {
  font-size: 11px;
  font-weight: 600;
  letter-spacing: 0.08em;
  text-transform: uppercase;
  color: var(--muted);
  margin-bottom: 10px;
}
.analytics-panel__big {
  font-size: 48px;
  font-weight: 700;
  line-height: 1;
  color: var(--ink);
  margin-bottom: 12px;
}
.analytics-panel__unit {
  font-size: 20px;
  font-weight: 400;
  color: var(--muted);
  margin-left: 2px;
}
.analytics-panel__sub-row {
  display: flex;
  gap: 16px;
}
.analytics-panel__sub-label {
  display: block;
  font-size: 9px;
  font-weight: 600;
  letter-spacing: 0.1em;
  color: var(--faint);
}
.analytics-panel__sub-val {
  font-size: 14px;
  font-weight: 600;
  color: var(--ink-2);
}
.analytics-panel__chart-wrap {
  height: 90px;
  margin-top: 4px;
}
.analytics-panel__time-sub {
  font-size: 11.5px;
  color: var(--muted);
  margin-bottom: 10px;
}
.analytics-panel__progress {
  height: 4px;
  background: var(--bg-2);
  border-radius: 2px;
  overflow: hidden;
}
.analytics-panel__progress-fill {
  height: 100%;
  background: var(--accent);
  border-radius: 2px;
  transition: width 0.4s var(--ease);
}

.results-layout {
  display: grid;
  grid-template-columns: 1fr 320px;
  gap: 24px;
  align-items: start;
}
.results-layout__side {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.results-list { overflow: hidden; }
.results-list__header {
  display: grid;
  grid-template-columns: 1fr auto auto auto;
  gap: 16px;
  padding: 10px 20px;
  border-bottom: 1px solid var(--rule-soft);
  font-family: var(--f-mono);
  font-size: 10.5px;
  font-weight: 500;
  letter-spacing: 0.06em;
  text-transform: uppercase;
  color: var(--muted);
  background: var(--surface);
}

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

.diff-item {}
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

.detail-placeholder {
  padding: 20px;
}
.detail-placeholder__title {
  font-size: 11px;
  font-weight: 600;
  letter-spacing: 0.08em;
  text-transform: uppercase;
  color: var(--muted);
  margin-bottom: 8px;
}
.detail-placeholder__hint {
  font-size: 12.5px;
  color: var(--faint);
  line-height: 1.5;
}

@media (max-width: 900px) {
  .results-layout { grid-template-columns: 1fr; }
  .results-layout__side { order: -1; }
}
@media (max-width: 640px) {
  .analytics-panel { grid-template-columns: 1fr; }
  .analytics-panel__section { border-right: none; border-bottom: 1px solid var(--rule); }
  .analytics-panel__section--last { border-bottom: none; }
  .results-list__header { display: none; }
}
</style>
