<script setup>
import { ref, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { getResults, getSessionDetail } from '@/api/results'
import ResultRow from '@/components/assessment/ResultRow.vue'
import SessionDetailPanel from '@/components/assessment/SessionDetailPanel.vue'

const route = useRoute()
const router = useRouter()

const assessmentId = Number(route.params.id)
const results = ref([])
const selectedSession = ref(null)
const error = ref(null)

onMounted(async () => {
  try {
    const res = await getResults(assessmentId)
    results.value = res.data
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
        <span class="eyebrow">Results</span>
        <h1>{{ results.filter(r => r.status === 'COMPLETED').length }} <em>completed</em></h1>
        <p class="page__subtitle">
          {{ results.length }} total invite{{ results.length !== 1 ? 's' : '' }}.
        </p>
      </div>
    </header>

    <p v-if="error" class="error-text results__error">{{ error }}</p>

    <!-- Session detail panel -->
    <SessionDetailPanel
      v-if="selectedSession"
      :session="selectedSession"
      @close="closeDetail"
    />

    <!-- Candidates list -->
    <div v-if="results.length === 0 && !error" class="empty-state">
      <p class="empty-title">No results yet</p>
      <p class="text-muted">Results will appear here once candidates complete the assessment.</p>
    </div>

    <div v-else class="card results-list">
      <div class="results-list__header">
        <span>Candidate</span>
        <span>Status</span>
        <span>Score</span>
        <span>Completed</span>
        <span></span>
      </div>
      <ResultRow
        v-for="r in results"
        :key="r.sessionId"
        :result="r"
        @view="openDetail"
      />
    </div>
  </div>
</template>

<style scoped>
.results__error { margin-bottom: 20px; }

.results-list { overflow: hidden; }
.results-list__header {
  display: grid;
  grid-template-columns: 1fr auto auto auto auto;
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

@media (max-width: 640px) {
  .results-list__header { display: none; }
}
</style>
