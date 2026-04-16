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
    <BaseButton variant="outline" style="margin-bottom: 1rem" @click="router.push(`/assessments/${assessmentId}`)">
      ← Back
    </BaseButton>

    <h1 style="margin-bottom: 1.5rem">Results</h1>

    <p v-if="error" class="error-text">{{ error }}</p>
    <p v-if="results.length === 0 && !error" class="text-muted">No results yet.</p>

    <!-- Results table -->
    <BaseCard v-if="!selectedSession">
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
            <td>{{ r.candidateName }}</td>
            <td>{{ r.candidateEmail }}</td>
            <td>{{ r.status }}</td>
            <td>
              <span v-if="r.score !== null" :class="['score-badge', scoreColor(r.score)]">
                {{ r.score }}%
              </span>
              <span v-else class="text-muted">—</span>
            </td>
            <td class="text-muted">
              {{ r.completedAt ? new Date(r.completedAt).toLocaleDateString() : '—' }}
            </td>
            <td>
              <BaseButton variant="outline" @click="openDetail(r.sessionId)">View</BaseButton>
            </td>
          </tr>
        </tbody>
      </table>
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

.score-high { background: #dcfce7; color: #15803d; }
.score-mid  { background: #fef9c3; color: #a16207; }
.score-low  { background: #fee2e2; color: #b91c1c; }

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

.answer-card.correct   { border-left-color: var(--color-success); background: #f0fdf4; }
.answer-card.incorrect { border-left-color: var(--color-danger);  background: #fef2f2; }
.answer-card.open      { border-left-color: var(--color-primary); background: #eff6ff; }

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
</style>
