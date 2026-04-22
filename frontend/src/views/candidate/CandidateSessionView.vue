<script setup>
import { ref, computed, onMounted, onUnmounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { getSession, startSession, submitAnswers } from '@/api/candidate'
import BrandLogo from '@/components/BrandLogo.vue'

const route = useRoute()
const router = useRouter()

const token = route.params.token
const session = ref(null)
const answers = ref({})
const error = ref(null)
const loading = ref(false)
const currentIdx = ref(0)

const timeLeft = ref(0)
let timerInterval = null

const timeDisplay = computed(() => {
  const t = timeLeft.value
  const m = Math.floor(t / 60).toString().padStart(2, '0')
  const s = (t % 60).toString().padStart(2, '0')
  return `${m}:${s}`
})

const timerClass = computed(() => {
  if (!session.value) return ''
  const total = session.value.durationMinutes * 60
  const ratio = timeLeft.value / total
  if (ratio <= 0.10) return 'urgent'
  if (ratio <= 0.25) return 'warn'
  return ''
})

const answeredCount = computed(() => {
  if (!session.value) return 0
  return session.value.questions.filter((q) => {
    const a = answers.value[q.id]
    return a?.selectedOptionId || a?.openTextAnswer?.trim()
  }).length
})

const totalCount = computed(() => session.value?.questions.length ?? 0)
const currentQuestion = computed(() => session.value?.questions[currentIdx.value])

onMounted(async () => {
  try {
    const res = await getSession(token)
    session.value = res.data
    initAnswers()
    if (session.value.status === 'IN_PROGRESS') startTimer()
  } catch {
    error.value = 'Session not found or the link has expired.'
  }
})

onUnmounted(() => clearInterval(timerInterval))

function startTimer() {
  const endTime = new Date(session.value.startedAt).getTime() + session.value.durationMinutes * 60 * 1000
  const tick = () => {
    timeLeft.value = Math.max(0, Math.floor((endTime - Date.now()) / 1000))
    if (timeLeft.value === 0) {
      clearInterval(timerInterval)
      handleSubmit()
    }
  }
  tick()
  timerInterval = setInterval(tick, 1000)
}

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
    startTimer()
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

function isAnswered(q) {
  const a = answers.value[q.id]
  return !!(a?.selectedOptionId || a?.openTextAnswer?.trim())
}
</script>

<template>
  <div class="candidate">

    <!-- Error / Loading -->
    <div v-if="error && !session" style="display: grid; place-items: center; min-height: 100vh; padding: 40px;">
      <div style="text-align: center; max-width: 400px;">
        <div style="font-family: var(--f-display); font-size: 40px; color: var(--bad); margin-bottom: 16px;">⚠</div>
        <h2 class="display" style="font-size: 28px; margin-bottom: 12px;">Something went wrong</h2>
        <p style="color: var(--muted);">{{ error }}</p>
      </div>
    </div>

    <div v-else-if="!session" style="display: grid; place-items: center; min-height: 100vh;">
      <div style="text-align: center; color: var(--muted); font-family: var(--f-mono); font-size: 12px; letter-spacing: 0.1em;">
        LOADING…
      </div>
    </div>

    <template v-else>

      <!-- PENDING — Intro screen -->
      <template v-if="session.status === 'PENDING'">
        <div class="cand-top">
          <div class="cand-top__brand">
            <BrandLogo to="/" />
          </div>
          <div class="cand-top__meta">
            <span>ASSESSMENT INVITE</span>
          </div>
        </div>
        <div class="cand-main">
          <div style="max-width: 560px; text-align: center; display: flex; flex-direction: column; gap: 22px; align-items: center;">
            <span class="eyebrow">Ready when you are</span>
            <h1 class="display" style="font-size: 52px; line-height: 1.04; letter-spacing: -0.02em; margin: 0;">
              {{ session.assessmentTitle }}
            </h1>
            <p style="color: var(--muted); font-size: 15px; max-width: 44ch;">
              You have <strong style="color: var(--ink);">{{ session.durationMinutes }} minutes</strong> to answer
              <strong style="color: var(--ink);">{{ totalCount }} question{{ totalCount !== 1 ? 's' : '' }}</strong>.
              The timer starts when you press the button below.
            </p>
            <div style="display: flex; gap: 10px; flex-wrap: wrap; justify-content: center;">
              <span class="tag">⏱ {{ session.durationMinutes }} min</span>
              <span class="tag">{{ totalCount }} questions</span>
              <span class="tag">Auto-save</span>
            </div>
            <button
              class="btn btn--accent btn--lg"
              style="margin-top: 20px; padding: 14px 28px;"
              :disabled="loading"
              @click="handleStart"
            >
              <span v-if="loading" class="spinner" />
              ▶ Begin assessment
            </button>
            <p class="mono" style="font-size: 10.5px; color: var(--faint); letter-spacing: 0.1em;">
              YOUR ANSWERS ARE AUTO-SAVED
            </p>
          </div>
        </div>
      </template>

      <!-- IN_PROGRESS — Focus layout (one at a time) -->
      <template v-else-if="session.status === 'IN_PROGRESS'">
        <!-- Top bar -->
        <div class="cand-top">
          <div class="cand-top__brand">
            <BrandLogo to="/" />
            <span style="color: var(--faint); margin-left: 8px;">·</span>
            <span class="mono" style="font-size: 11px; color: var(--muted); letter-spacing: 0.08em; text-transform: uppercase;">
              {{ session.assessmentTitle }}
            </span>
          </div>
          <div class="cand-top__meta">
            <span>{{ answeredCount }} / {{ totalCount }} ANSWERED</span>
            <div :class="['cand-top__timer', timerClass]">
              <svg width="12" height="12" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5" stroke-linecap="round"><circle cx="12" cy="12" r="9"/><path d="M12 7v5l3 2"/></svg>
              {{ timeDisplay }}
            </div>
          </div>
        </div>

        <!-- Progress bar -->
        <div class="cand-progress">
          <div class="cand-progress__fill" :style="{ width: `${(answeredCount / totalCount) * 100}%` }" />
        </div>

        <!-- Question -->
        <div class="cand-main" v-if="currentQuestion">
          <div class="cand-question fade-in" :key="currentQuestion.id">
            <div class="cand-q__eyebrow">
              <span class="n">QUESTION {{ String(currentIdx + 1).padStart(2, '0') }} OF {{ String(totalCount).padStart(2, '0') }}</span>
              <span class="bar" />
              <span class="mono" style="font-size: 10px; color: var(--muted); letter-spacing: 0.1em;">
                {{ currentQuestion.type.replace('_', ' ') }}
              </span>
            </div>

            <p class="cand-q__text">{{ currentQuestion.text }}</p>

            <div v-if="currentQuestion.type !== 'OPEN_TEXT'" class="cand-opts">
              <button
                v-for="(opt, j) in currentQuestion.options"
                :key="opt.id"
                :class="['cand-opt', { 'is-selected': answers[currentQuestion.id].selectedOptionId === opt.id }]"
                @click="answers[currentQuestion.id].selectedOptionId = opt.id"
              >
                <span class="cand-opt__key">{{ String.fromCharCode(65 + j) }}</span>
                <span>{{ opt.text }}</span>
                <span class="cand-opt__check" />
              </button>
            </div>

            <textarea
              v-else
              class="input"
              rows="6"
              placeholder="Write your answer here…"
              v-model="answers[currentQuestion.id].openTextAnswer"
            />
          </div>
        </div>

        <!-- Bottom nav -->
        <div class="cand-foot">
          <button
            class="btn btn--ghost"
            :disabled="currentIdx === 0"
            @click="currentIdx--"
          >
            ← Previous
          </button>

          <div class="cand-foot__dots">
            <button
              v-for="(q, i) in session.questions"
              :key="q.id"
              :class="['cand-dot', { answered: isAnswered(q), current: i === currentIdx }]"
              @click="currentIdx = i"
            >{{ i + 1 }}</button>
          </div>

          <div style="display: flex; gap: 8px; align-items: center;">
            <p v-if="error" class="error-text">{{ error }}</p>
            <button
              v-if="currentIdx === totalCount - 1"
              class="btn btn--accent"
              :disabled="loading"
              @click="handleSubmit"
            >
              <span v-if="loading" class="spinner" />
              Submit →
            </button>
            <button
              v-else
              class="btn btn--primary"
              @click="currentIdx++"
            >
              Next →
            </button>
          </div>
        </div>
      </template>

      <!-- COMPLETED -->
      <div v-else-if="session.status === 'COMPLETED'" style="display: grid; place-items: center; min-height: 100vh; padding: 40px;">
        <div style="text-align: center; max-width: 400px;">
          <h2 class="display" style="font-size: 32px; margin-bottom: 12px;">Already submitted</h2>
          <p style="color: var(--muted);">This assessment has already been completed.</p>
        </div>
      </div>

    </template>
  </div>
</template>
