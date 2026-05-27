<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useAssessmentStore } from '@/stores/assessment'
import { getResults, getSessionDetail } from '@/api/results'
import { getAssessmentAnalytics } from '@/api/assessments'
import AssessmentStatsPanel from '@/components/analytics/AssessmentStatsPanel.vue'
import QuestionDifficultyPanel from '@/components/analytics/QuestionDifficultyPanel.vue'
import CandidatesTable from '@/components/results/CandidatesTable.vue'
import SessionDetailPanel from '@/components/results/SessionDetailPanel.vue'
import BaseButton from '@/components/ui/BaseButton.vue'

const route = useRoute()
const router = useRouter()
const assessmentStore = useAssessmentStore()

const assessmentId = Number(route.params.id)
const results = ref([])
const analytics = ref(null)
const selectedSession = ref(null)
const error = ref(null)

const assessment = computed(() =>
  assessmentStore.assessments.find((a) => a.id === assessmentId)
)

const completedCount = computed(() =>
  results.value.filter((r) => r.status === 'COMPLETED').length
)

const completionRate = computed(() => {
  if (!analytics.value?.totalInvites) return 0
  return Math.round((analytics.value.completedCount / analytics.value.totalInvites) * 100)
})

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

    <p v-if="error" class="error-text results-view__error">{{ error }}</p>

    <AssessmentStatsPanel v-if="analytics" :analytics="analytics" />

    <div v-if="results.length === 0 && !error" class="empty-state">
      <p class="empty-title">No results yet</p>
      <p class="text-muted">Results will appear here once candidates complete the assessment.</p>
    </div>

    <div v-else class="results-view__layout">
      <CandidatesTable :results="results" @view="openDetail" />

      <aside class="results-view__side">
        <QuestionDifficultyPanel
          v-if="analytics?.perQuestionStats?.length"
          :questions="analytics.perQuestionStats"
        />

        <SessionDetailPanel
          v-if="selectedSession"
          :session="selectedSession"
          @close="closeDetail"
        />
        <div v-else class="card results-view__placeholder">
          <div class="results-view__placeholder-title">Detail view</div>
          <p class="results-view__placeholder-hint">Select a candidate to see their answer-by-answer breakdown.</p>
        </div>
      </aside>
    </div>
  </div>
</template>

<style scoped>
.results-view__error { margin-bottom: 20px; }

.results-view__layout {
  display: grid;
  grid-template-columns: 1fr 320px;
  gap: 24px;
  align-items: start;
}
.results-view__side {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.results-view__placeholder { padding: 20px; }
.results-view__placeholder-title {
  font-size: 11px;
  font-weight: 600;
  letter-spacing: 0.08em;
  text-transform: uppercase;
  color: var(--muted);
  margin-bottom: 8px;
}
.results-view__placeholder-hint {
  font-size: 12.5px;
  color: var(--faint);
  line-height: 1.5;
}

@media (max-width: 900px) {
  .results-view__layout { grid-template-columns: 1fr; }
  .results-view__side { order: -1; }
}
</style>
