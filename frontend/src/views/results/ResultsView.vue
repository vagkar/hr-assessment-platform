<script setup>
import { ref, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { getResults, getSessionDetail } from '@/api/results'

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

function scoreTag(score) {
  if (score === null) return ''
  if (score >= 75) return 'tag--ok'
  if (score >= 50) return 'tag--warn'
  return 'tag--bad'
}

function statusTag(status) {
  if (status === 'COMPLETED') return 'tag--ok'
  if (status === 'IN_PROGRESS') return 'tag--warn'
  return ''
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

    <p v-if="error" class="error-text" style="margin-bottom: 20px;">{{ error }}</p>

    <!-- Session detail panel -->
    <div v-if="selectedSession" class="card fade-in" style="padding: 24px; margin-bottom: 24px;">
      <div style="display: flex; justify-content: space-between; align-items: flex-start; margin-bottom: 16px;">
        <div>
          <div class="display" style="font-size: 22px; letter-spacing: -0.01em;">{{ selectedSession.candidateName }}</div>
          <div class="mono" style="font-size: 11px; color: var(--muted); margin-top: 2px;">{{ selectedSession.candidateEmail }}</div>
        </div>
        <div style="display: flex; gap: 10px; align-items: center;">
          <span v-if="selectedSession.score !== null" :class="['tag', scoreTag(selectedSession.score)]" style="font-size: 14px; padding: 4px 10px;">
            {{ selectedSession.score }}%
          </span>
          <button class="icon-btn" @click="closeDetail">✕</button>
        </div>
      </div>

      <div style="display: flex; flex-direction: column; gap: 8px;">
        <div
          v-for="(a, index) in selectedSession.answers"
          :key="a.questionId"
          :style="{
            padding: '10px 14px',
            borderLeft: '3px solid ' + (a.isCorrect === true ? 'var(--ok)' : a.isCorrect === false ? 'var(--bad)' : 'var(--accent)'),
            background: 'var(--bg-2)',
            borderRadius: '0 4px 4px 0',
          }"
        >
          <div class="mono" style="font-size: 10px; color: var(--muted); letter-spacing: 0.1em; margin-bottom: 4px;">
            Q{{ String(index + 1).padStart(2, '0') }} · {{ a.questionType }}
            <span v-if="a.isCorrect !== null"> · {{ a.isCorrect ? 'CORRECT' : 'INCORRECT' }}</span>
            <span v-else> · OPEN · TO REVIEW</span>
          </div>
          <div style="font-size: 13px; color: var(--ink-2);">{{ a.questionText }}</div>
          <div v-if="a.selectedOptionText" style="font-size: 12px; color: var(--muted); margin-top: 4px;">
            → {{ a.selectedOptionText }}
          </div>
          <div v-if="a.openTextAnswer" style="font-size: 12px; color: var(--muted); margin-top: 4px; font-style: italic;">
            {{ a.openTextAnswer }}
          </div>
        </div>
      </div>
    </div>

    <!-- Candidates table -->
    <div v-if="results.length === 0 && !error" class="empty-state">
      <p class="empty-title">No results yet</p>
      <p style="color: var(--muted);">Results will appear here once candidates complete the assessment.</p>
    </div>

    <div v-else class="card" style="overflow: hidden;">
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
                <div style="font-weight: 500;">{{ r.candidateName }}</div>
                <div class="mono" style="font-size: 10.5px; color: var(--muted);">{{ r.candidateEmail }}</div>
              </div>
            </td>
            <td>
              <span :class="['tag', 'tag--dot', statusTag(r.status)]">
                {{ r.status.toLowerCase().replace('_', ' ') }}
              </span>
            </td>
            <td>
              <span v-if="r.score !== null" :class="['tag', scoreTag(r.score)]">{{ r.score }}%</span>
              <span v-else style="color: var(--faint);">—</span>
            </td>
            <td style="color: var(--muted); font-size: 13px;">
              {{ r.completedAt ? new Date(r.completedAt).toLocaleDateString() : '—' }}
            </td>
            <td style="text-align: right;">
              <button class="btn btn--ghost btn--sm" @click.stop="openDetail(r.sessionId)">View</button>
            </td>
          </tr>
        </tbody>
      </table>
    </div>
  </div>
</template>
