<script setup>
import { ref, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { getResults, getSessionDetail } from '@/api/results'
import BaseButton from '@/components/BaseButton.vue'
import BaseCard from '@/components/BaseCard.vue'

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

function scoreColor(score) {
  if (score === null) return ''
  if (score >= 70) return 'score-high'
  if (score >= 40) return 'score-mid'
  return 'score-low'
}
</script>

<template>
  <div>
    <button class="back-link" @click="router.push(`/assessments/${assessmentId}`)">← Back to Assessment</button>

    <h1 class="page-title">Results</h1>

    <p v-if="error" class="error-text">{{ error }}</p>
    <p v-if="results.length === 0 && !error" class="text-muted">No results yet.</p>

    <!-- Results table -->
    <BaseCard v-if="!selectedSession">
      <div class="table-scroll">
      <table class="results-table">
        <thead>
          <tr>
            <th>Candidate</th>
            <th>Email</th>
            <th>Status</th>
            <th>Score</th>
            <th>Completed</th>
            <th></th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="r in results" :key="r.sessionId">
            <td data-label="Candidate">{{ r.candidateName }}</td>
            <td data-label="Email">{{ r.candidateEmail }}</td>
            <td data-label="Status">{{ r.status }}</td>
            <td data-label="Score">
              <span v-if="r.score !== null" :class="['score-badge', scoreColor(r.score)]">
                {{ r.score }}%
              </span>
              <span v-else class="text-muted">—</span>
            </td>
            <td data-label="Completed" class="text-muted">
              {{ r.completedAt ? new Date(r.completedAt).toLocaleDateString() : '—' }}
            </td>
            <td data-label="">
              <BaseButton variant="outline" @click="openDetail(r.sessionId)">View</BaseButton>
            </td>
          </tr>
        </tbody>
      </table>
      </div>
    </BaseCard>

    <!-- Session detail -->
    <BaseCard v-if="selectedSession">
      <div class="detail-header">
        <div>
          <h2>{{ selectedSession.candidateName }}</h2>
          <p class="text-muted">{{ selectedSession.candidateEmail }}</p>
        </div>
        <div class="detail-score-area">
          <span v-if="selectedSession.score !== null" :class="['score-badge', 'score-large', scoreColor(selectedSession.score)]">
            {{ selectedSession.score }}%
          </span>
          <BaseButton variant="outline" @click="closeDetail">Close</BaseButton>
        </div>
      </div>

      <div class="answer-list">
        <div
          v-for="(a, index) in selectedSession.answers"
          :key="a.questionId"
          class="answer-card"
          :class="{ correct: a.isCorrect === true, incorrect: a.isCorrect === false, open: a.isCorrect === null }"
        >
          <p class="question-number">Question {{ index + 1 }} — {{ a.questionType }}</p>
          <p class="question-text">{{ a.questionText }}</p>
          <p v-if="a.selectedOptionText" class="answer-text">
            Answer: <strong>{{ a.selectedOptionText }}</strong>
          </p>
          <p v-if="a.openTextAnswer" class="answer-text">
            Answer: <em>{{ a.openTextAnswer }}</em>
          </p>
          <p v-if="a.isCorrect !== null" :class="a.isCorrect ? 'correct-label' : 'incorrect-label'">
            {{ a.isCorrect ? '✓ Correct' : '✗ Incorrect' }}
          </p>
        </div>
      </div>
    </BaseCard>
  </div>
</template>

<style scoped>
.page-title { margin-bottom: var(--space-lg); }

.results-table {
  width: 100%;
  border-collapse: collapse;
}

.results-table th {
  text-align: left;
  padding: var(--space-sm) var(--space-md);
  border-bottom: 2px solid var(--color-border);
  font-size: 0.875rem;
  color: var(--color-text-muted);
}

.results-table td {
  padding: var(--space-sm) var(--space-md);
  border-bottom: 1px solid var(--color-border);
}

.score-badge {
  padding: 0.2rem 0.6rem;
  border-radius: 999px;
  font-size: 0.875rem;
  font-weight: 600;
}

.score-high { background: color-mix(in srgb, var(--color-success) 15%, transparent); color: var(--color-success); }
.score-mid  { background: color-mix(in srgb, var(--color-warning) 15%, transparent); color: var(--color-warning); }
.score-low  { background: color-mix(in srgb, var(--color-danger)  15%, transparent); color: var(--color-danger); }

.score-large {
  font-size: 1.5rem;
  padding: 0.4rem 1rem;
}

.detail-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: var(--space-xl);
}

.detail-score-area {
  display: flex;
  align-items: center;
  gap: var(--space-md);
}

.answer-list {
  display: flex;
  flex-direction: column;
  gap: var(--space-md);
}

.answer-card {
  padding: var(--space-md);
  border-radius: var(--radius);
  border-left: 4px solid var(--color-border);
}

.answer-card.correct   { border-left-color: var(--color-success); background: color-mix(in srgb, var(--color-success) 8%, var(--color-bg-card)); }
.answer-card.incorrect { border-left-color: var(--color-danger);  background: color-mix(in srgb, var(--color-danger)  8%, var(--color-bg-card)); }
.answer-card.open      { border-left-color: var(--color-primary); background: color-mix(in srgb, var(--color-primary) 8%, var(--color-bg-card)); }

.question-number {
  font-size: 0.75rem;
  font-weight: 600;
  color: var(--color-text-muted);
  text-transform: uppercase;
  margin-bottom: var(--space-xs);
}

.question-text {
  margin-bottom: var(--space-sm);
}

.answer-text { font-size: 0.9rem; }

.correct-label   { color: var(--color-success); font-weight: 600; margin-top: var(--space-xs); }
.incorrect-label { color: var(--color-danger);  font-weight: 600; margin-top: var(--space-xs); }

.table-scroll { overflow-x: auto; }

@media (max-width: 640px) {
  .table-scroll { overflow-x: unset; }

  .results-table thead {
    display: none;
  }

  .results-table tr {
    display: block;
    padding: var(--space-md) 0;
    border-bottom: 1px solid var(--color-border);
  }

  .results-table tr:last-child {
    border-bottom: none;
  }

  .results-table td {
    display: flex;
    justify-content: space-between;
    align-items: center;
    padding: 0.25rem 0;
    border-bottom: none;
    font-size: 0.875rem;
  }

  .results-table td::before {
    content: attr(data-label);
    font-weight: 600;
    font-size: 0.75rem;
    color: var(--color-text-muted);
    text-transform: uppercase;
    letter-spacing: 0.04em;
    flex-shrink: 0;
    margin-right: var(--space-sm);
  }

  .results-table td[data-label=""] {
    justify-content: flex-end;
    margin-top: var(--space-xs);
  }
}

@media (max-width: 640px) {
  .detail-header {
    flex-direction: column;
    align-items: flex-start;
    gap: var(--space-md);
  }

  .detail-score-area {
    width: 100%;
    justify-content: space-between;
  }
}
</style>
