<script setup>
import { ref, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { getResults, getSessionDetail } from '@/api/results'
import { scoreTag } from '@/utils/score'

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

function statusTag(status) {
  if (status === 'COMPLETED') return 'tag--ok'
  if (status === 'IN_PROGRESS') return 'tag--warn'
  return ''
}

function answerClass(isCorrect) {
  if (isCorrect === true) return 'is-correct'
  if (isCorrect === false) return 'is-incorrect'
  return 'is-open'
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
    <div v-if="selectedSession" class="card fade-in session-detail">
      <div class="session-detail__header">
        <div>
          <div class="display session-detail__name">{{ selectedSession.candidateName }}</div>
          <div class="mono session-detail__email">{{ selectedSession.candidateEmail }}</div>
        </div>
        <div class="session-detail__meta">
          <span v-if="selectedSession.score !== null" :class="['tag', 'session-detail__score', scoreTag(selectedSession.score)]">
            {{ selectedSession.score }}%
          </span>
          <button class="icon-btn" @click="closeDetail">✕</button>
        </div>
      </div>

      <div class="session-detail__answers">
        <div
          v-for="(a, index) in selectedSession.answers"
          :key="a.questionId"
          :class="['answer-item', answerClass(a.isCorrect)]"
        >
          <div class="mono answer-item__meta">
            Q{{ String(index + 1).padStart(2, '0') }} · {{ a.questionType }}
            <span v-if="a.isCorrect !== null"> · {{ a.isCorrect ? 'CORRECT' : 'INCORRECT' }}</span>
            <span v-else> · OPEN · TO REVIEW</span>
          </div>
          <div class="answer-item__text">{{ a.questionText }}</div>
          <div v-if="a.selectedOptionText" class="answer-item__selected">
            → {{ a.selectedOptionText }}
          </div>
          <div v-if="a.openTextAnswer" class="answer-item__open">
            {{ a.openTextAnswer }}
          </div>
        </div>
      </div>
    </div>

    <!-- Candidates table -->
    <div v-if="results.length === 0 && !error" class="empty-state">
      <p class="empty-title">No results yet</p>
      <p class="text-muted">Results will appear here once candidates complete the assessment.</p>
    </div>

    <div v-else class="card results-table-card">
      <table class="table">
        <thead>
          <tr>
            <th>Candidate</th>
            <th>Status</th>
            <th>Score</th>
            <th>Completed</th>
            <th></th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="r in results" :key="r.sessionId" @click="openDetail(r.sessionId)">
            <td>
              <div>
                <div class="results__cand-name">{{ r.candidateName }}</div>
                <div class="mono results__cand-email">{{ r.candidateEmail }}</div>
              </div>
            </td>
            <td>
              <span :class="['tag', 'tag--dot', statusTag(r.status)]">
                {{ r.status.toLowerCase().replace('_', ' ') }}
              </span>
            </td>
            <td>
              <span v-if="r.score !== null" :class="['tag', scoreTag(r.score)]">{{ r.score }}%</span>
              <span v-else class="results__no-score">—</span>
            </td>
            <td class="results__date">
              {{ r.completedAt ? new Date(r.completedAt).toLocaleDateString() : '—' }}
            </td>
            <td class="results__view-col">
              <button class="btn btn--ghost btn--sm" @click.stop="openDetail(r.sessionId)">View</button>
            </td>
          </tr>
        </tbody>
      </table>
    </div>
  </div>
</template>

<style scoped>
.results__error { margin-bottom: 20px; }

.session-detail { padding: 24px; margin-bottom: 24px; }
.session-detail__header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: 16px;
}
.session-detail__name { font-size: 22px; letter-spacing: -0.01em; }
.session-detail__email { font-size: 11px; color: var(--muted); margin-top: 2px; }
.session-detail__meta { display: flex; gap: 10px; align-items: center; }
.session-detail__score { font-size: 14px; padding: 4px 10px; }
.session-detail__answers { display: flex; flex-direction: column; gap: 8px; }

.answer-item {
  padding: 10px 14px;
  border-left: 3px solid var(--accent);
  background: var(--bg-2);
  border-radius: 0 4px 4px 0;
}
.answer-item.is-correct { border-left-color: var(--ok); }
.answer-item.is-incorrect { border-left-color: var(--bad); }
.answer-item__meta { font-size: 10px; color: var(--muted); letter-spacing: 0.1em; margin-bottom: 4px; }
.answer-item__text { font-size: 13px; color: var(--ink-2); }
.answer-item__selected { font-size: 12px; color: var(--muted); margin-top: 4px; }
.answer-item__open { font-size: 12px; color: var(--muted); margin-top: 4px; font-style: italic; }

.results-table-card { overflow: hidden; }
.results__cand-name { font-weight: 500; }
.results__cand-email { font-size: 10.5px; color: var(--muted); }
.results__no-score { color: var(--faint); }
.results__date { color: var(--muted); font-size: 13px; }
.results__view-col { text-align: right; }
</style>
