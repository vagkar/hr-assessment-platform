<script setup>
import { ref, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { getSession, startSession, submitAnswers } from '@/api/candidate'

const route = useRoute()
const router = useRouter()

const token = route.params.token
const session = ref(null)
const answers = ref({})
const error = ref(null)
const loading = ref(false)

onMounted(async () => {
  try {
    const res = await getSession(token)
    session.value = res.data
    initAnswers()
  } catch {
    error.value = 'Session not found.'
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
  <div class="page">
    <div v-if="error" class="card">
      <p class="error-text">{{ error }}</p>
    </div>

    <div v-else-if="!session">
      <p class="text-muted">Loading...</p>
    </div>

    <div v-else>
      <div class="card session-header">
        <h1>{{ session.assessmentTitle }}</h1>
        <p class="text-muted">Duration: {{ session.durationMinutes }} min</p>
        <p class="status">Status: {{ session.status }}</p>
      </div>

      <!-- PENDING -->
      <div v-if="session.status === 'PENDING'" class="card" style="margin-top: 1rem">
        <p>You are about to start the assessment. Once started, the timer begins.</p>
        <div class="form-actions" style="margin-top: 1rem">
          <button class="btn btn-primary" :disabled="loading" @click="handleStart">
            {{ loading ? 'Starting...' : 'Start Assessment' }}
          </button>
        </div>
      </div>

      <!-- IN_PROGRESS -->
      <form v-else-if="session.status === 'IN_PROGRESS'" @submit.prevent="handleSubmit">
        <div
          v-for="(q, index) in session.questions"
          :key="q.id"
          class="card question-card"
          style="margin-top: 1rem"
        >
          <p class="question-number">Question {{ index + 1 }}</p>
          <p class="question-text">{{ q.text }}</p>

          <!-- Multiple choice / True-False -->
          <div v-if="q.type !== 'OPEN_TEXT'" class="options">
            <label
              v-for="opt in q.options"
              :key="opt.id"
              class="option-label"
            >
              <input
                type="radio"
                :name="`question-${q.id}`"
                :value="opt.id"
                v-model="answers[q.id].selectedOptionId"
              />
              {{ opt.text }}
            </label>
          </div>

          <!-- Open text -->
          <div v-else class="form-group" style="margin-top: 0.5rem">
            <textarea
              v-model="answers[q.id].openTextAnswer"
              rows="4"
              placeholder="Your answer..."
            />
          </div>
        </div>

        <p v-if="error" class="error-text" style="margin-top: 1rem">{{ error }}</p>
        <div class="form-actions" style="margin-top: 1.5rem">
          <button type="submit" class="btn btn-primary" :disabled="loading">
            {{ loading ? 'Submitting...' : 'Submit Assessment' }}
          </button>
        </div>
      </form>

      <!-- COMPLETED -->
      <div v-else-if="session.status === 'COMPLETED'" class="card" style="margin-top: 1rem">
        <p>This assessment has already been completed.</p>
      </div>
    </div>
  </div>
</template>

<style scoped>
.session-header {
  margin-bottom: var(--space-md);
}

.status {
  margin-top: var(--space-xs);
  font-weight: 500;
}

.question-number {
  font-size: 0.75rem;
  font-weight: 600;
  color: var(--color-primary);
  text-transform: uppercase;
  margin-bottom: var(--space-xs);
}

.question-text {
  font-size: 1rem;
  margin-bottom: var(--space-md);
}

.options {
  display: flex;
  flex-direction: column;
  gap: var(--space-sm);
}

.option-label {
  display: flex;
  align-items: center;
  gap: var(--space-sm);
  cursor: pointer;
}
</style>
