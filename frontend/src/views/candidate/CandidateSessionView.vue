<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { getSession, startSession, submitAnswers } from '@/api/candidate'
import BaseButton from '@/components/BaseButton.vue'
import BaseInput from '@/components/BaseInput.vue'
import BrandLogo from '@/components/BrandLogo.vue'

const route = useRoute()
const router = useRouter()

const token = route.params.token
const session = ref(null)
const answers = ref({})
const error = ref(null)
const loading = ref(false)

const answeredCount = computed(() => {
  if (!session.value) return 0
  return session.value.questions.filter((q) => {
    const a = answers.value[q.id]
    return a?.selectedOptionId || a?.openTextAnswer?.trim()
  }).length
})

const totalCount = computed(() => session.value?.questions.length ?? 0)

onMounted(async () => {
  try {
    const res = await getSession(token)
    session.value = res.data
    initAnswers()
  } catch {
    error.value = 'Session not found or the link has expired.'
  }
})

function initAnswers() {
  session.value.questions.forEach((q) => {
    answers.value[q.id] = { selectedOptionId: null, openTextAnswer: '' }
  })
}

async function handleStart() {
  loading.value = true
  try {
    const res = await startSession(token)
    session.value = res.data
  } catch (e) {
    error.value = e.response?.data?.message || 'Failed to start session'
  } finally {
    loading.value = false
  }
}

async function handleSubmit() {
  loading.value = true
  error.value = null
  try {
    const payload = session.value.questions.map((q) => ({
      questionId: q.id,
      selectedOptionId: answers.value[q.id]?.selectedOptionId || null,
      openTextAnswer: answers.value[q.id]?.openTextAnswer || null,
    }))
    const res = await submitAnswers(token, { answers: payload })
    router.push({ name: 'candidate-complete', params: { token }, state: { result: res.data } })
  } catch (e) {
    error.value = e.response?.data?.message || 'Failed to submit'
  } finally {
    loading.value = false
  }
}
</script>

<template>
  <div class="page candidate-page">
    <div class="candidate-container">

      <div v-if="error && !session" class="card state-card">
        <p class="state-icon">⚠️</p>
        <h2>Something went wrong</h2>
        <p class="text-muted">{{ error }}</p>
      </div>

      <div v-else-if="!session" class="card state-card">
        <div class="spinner spinner-centered" />
        <p class="text-muted">Loading assessment...</p>
      </div>

      <div v-else>
        <div class="session-header">
          <BrandLogo class="session-brand" />
          <h1>{{ session.assessmentTitle }}</h1>
          <div class="chips">
            <span class="chip">{{ session.durationMinutes }} min</span>
            <span class="chip">{{ totalCount }} questions</span>
          </div>
        </div>

        <!-- PENDING -->
        <div v-if="session.status === 'PENDING'" class="card state-card">
          <p class="state-icon">📋</p>
          <h2>Ready to begin?</h2>
          <p class="text-muted pending-desc">
            You have <strong>{{ session.durationMinutes }} minutes</strong> to complete
            <strong>{{ totalCount }} question{{ totalCount !== 1 ? 's' : '' }}</strong>.
            Once you start, the timer begins.
          </p>
          <BaseButton :loading="loading" class="start-btn" @click="handleStart">
            Start Assessment
          </BaseButton>
        </div>

        <!-- IN_PROGRESS -->
        <form v-else-if="session.status === 'IN_PROGRESS'" @submit.prevent="handleSubmit">
          <div class="progress-wrap">
            <div class="progress-bar">
              <div class="progress-fill" :style="{ width: `${(answeredCount / totalCount) * 100}%` }" />
            </div>
            <span class="progress-count">{{ answeredCount }} / {{ totalCount }} answered</span>
          </div>

          <div v-for="(q, index) in session.questions" :key="q.id" class="card question-card">
            <p class="question-number">Question {{ index + 1 }} of {{ totalCount }}</p>
            <p class="question-text">{{ q.text }}</p>

            <div v-if="q.type !== 'OPEN_TEXT'" class="options">
              <label
                v-for="opt in q.options"
                :key="opt.id"
                :class="['option-item', { selected: answers[q.id].selectedOptionId === opt.id }]"
              >
                <input type="radio" :name="`question-${q.id}`" :value="opt.id" v-model="answers[q.id].selectedOptionId" class="sr-only" />
                <span class="option-radio" />
                <span>{{ opt.text }}</span>
              </label>
            </div>

            <BaseInput
              v-else
              v-model="answers[q.id].openTextAnswer"
              :rows="4"
              placeholder="Write your answer here..."
              class="open-text-input"
            />
          </div>

          <div class="submit-area">
            <p v-if="error" class="error-text">{{ error }}</p>
            <p class="submit-count text-muted">{{ answeredCount }} of {{ totalCount }} questions answered</p>
            <BaseButton type="submit" :loading="loading">Submit Assessment</BaseButton>
          </div>
        </form>

        <div v-else-if="session.status === 'COMPLETED'" class="card state-card">
          <h2>Already submitted</h2>
          <p class="text-muted">This assessment has already been completed.</p>
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped>
.candidate-page {
  min-height: 100vh;
  display: flex;
  flex-direction: column;
}

.candidate-container {
  max-width: 680px;
  width: 100%;
  margin: 0 auto;
}

.state-card { text-align: center; }
.state-icon { font-size: 2rem; margin-bottom: var(--space-md); }
.spinner-centered { margin: 0 auto var(--space-md); }

.session-header {
  text-align: center;
  margin-bottom: var(--space-xl);
}

.session-brand { margin-bottom: var(--space-lg); }
.session-header h1 { margin-bottom: var(--space-sm); }

.pending-desc { margin-top: var(--space-xs); }
.start-btn { margin-top: var(--space-lg); }

.chips {
  display: flex;
  justify-content: center;
  gap: var(--space-sm);
}

.progress-wrap {
  display: flex;
  align-items: center;
  gap: var(--space-md);
  margin-bottom: var(--space-lg);
}

.progress-bar {
  flex: 1;
  height: 6px;
  background: var(--color-border);
  border-radius: 999px;
  overflow: hidden;
}

.progress-fill {
  height: 100%;
  background: var(--color-primary);
  border-radius: 999px;
  transition: width 0.3s ease;
}

.question-card { margin-bottom: var(--space-md); }

.question-number {
  font-size: 0.75rem;
  font-weight: 700;
  text-transform: uppercase;
  letter-spacing: 0.05em;
  color: var(--color-primary);
  margin-bottom: var(--space-sm);
}

.question-text {
  font-size: 1.0625rem;
  font-weight: 500;
  margin-bottom: var(--space-lg);
}

.options {
  display: flex;
  flex-direction: column;
  gap: var(--space-sm);
}

.option-item {
  display: flex;
  align-items: center;
  gap: var(--space-md);
  padding: var(--space-md);
  border: 1.5px solid var(--color-border);
  border-radius: var(--radius);
  cursor: pointer;
  transition: border-color var(--transition), background var(--transition);
}

.option-item:hover { border-color: var(--color-primary); background: color-mix(in srgb, var(--color-primary) 5%, transparent); }
.option-item.selected { border-color: var(--color-primary); background: color-mix(in srgb, var(--color-primary) 8%, transparent); }

.option-radio {
  width: 18px;
  height: 18px;
  border: 2px solid var(--color-border);
  border-radius: 50%;
  flex-shrink: 0;
  transition: border-color var(--transition);
}

.option-item.selected .option-radio {
  border-color: var(--color-primary);
  background: var(--color-primary);
  box-shadow: inset 0 0 0 3px var(--color-bg-card);
}

.progress-count {
  white-space: nowrap;
  font-size: 0.8125rem;
  color: var(--color-text-muted);
}

.open-text-input { margin-top: var(--space-sm); }
.submit-count { font-size: 0.875rem; }

.submit-area {
  display: flex;
  flex-direction: column;
  align-items: flex-end;
  gap: var(--space-sm);
  padding-top: var(--space-lg);
  border-top: 1px solid var(--color-border);
}
</style>
