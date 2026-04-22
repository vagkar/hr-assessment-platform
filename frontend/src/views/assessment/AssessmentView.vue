<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useAssessmentStore } from '@/stores/assessment'
import { getQuestions, createQuestion, updateQuestion, deleteQuestion } from '@/api/questions'
import { createInvite, updateAssessment } from '@/api/assessments'
import QuestionCard from '@/components/QuestionCard.vue'

const route = useRoute()
const router = useRouter()
const assessmentStore = useAssessmentStore()

const assessmentId = Number(route.params.id)
const assessment = ref(null)
const questions = ref([])

const showQuestionForm = ref(false)
const editingQuestionId = ref(null)
const questionForm = ref({ text: '', type: 'MULTIPLE_CHOICE', orderIndex: 1, options: [] })
const newOption = ref({ text: '', isCorrect: false })

const showInviteForm = ref(false)
const inviteForm = ref({ candidateName: '', candidateEmail: '' })
const inviteResult = ref(null)
const copied = ref(false)

const error = ref(null)
const loading = ref(false)

const settingsForm = ref(null)
const settingsOriginal = ref(null)
const settingsSaved = ref(false)
const settingsError = ref(null)
const settingsLoading = ref(false)

const settingsChanged = computed(() => {
  if (!settingsForm.value || !settingsOriginal.value) return false
  return JSON.stringify(settingsForm.value) !== JSON.stringify(settingsOriginal.value)
})

const appUrl = import.meta.env.VITE_APP_URL

onMounted(async () => {
  assessment.value = assessmentStore.assessments.find((a) => a.id === assessmentId)
  if (assessment.value) {
    const initial = {
      title: assessment.value.title,
      description: assessment.value.description || '',
      durationMinutes: assessment.value.durationMinutes,
      isActive: assessment.value.isActive,
    }
    settingsForm.value = { ...initial }
    settingsOriginal.value = { ...initial }
  }
  await loadQuestions()
})

async function loadQuestions() {
  const res = await getQuestions(assessmentId)
  questions.value = res.data
}

function openEditQuestion(q) {
  editingQuestionId.value = q.id
  questionForm.value = {
    text: q.text,
    type: q.type,
    orderIndex: q.orderIndex,
    options: q.options.map((o) => ({ text: o.text, isCorrect: o.isCorrect })),
  }
  showQuestionForm.value = true
}

function resetQuestionForm() {
  questionForm.value = { text: '', type: 'MULTIPLE_CHOICE', orderIndex: questions.value.length + 1, options: [] }
  newOption.value = { text: '', isCorrect: false }
}

function cancelQuestionForm() {
  showQuestionForm.value = false
  editingQuestionId.value = null
  error.value = null
  resetQuestionForm()
}

function addOption() {
  if (!newOption.value.text) return
  questionForm.value.options.push({ ...newOption.value })
  newOption.value = { text: '', isCorrect: false }
}

function removeOption(index) {
  questionForm.value.options.splice(index, 1)
}

async function handleSubmitQuestion() {
  error.value = null
  loading.value = true
  try {
    if (editingQuestionId.value) {
      await updateQuestion(editingQuestionId.value, questionForm.value)
    } else {
      await createQuestion(assessmentId, questionForm.value)
    }
    cancelQuestionForm()
    await loadQuestions()
  } catch (e) {
    error.value = e.response?.data?.message || 'Failed to save question'
  } finally {
    loading.value = false
  }
}

async function handleDeleteQuestion(id) {
  if (!confirm('Delete this question?')) return
  await deleteQuestion(id)
  await loadQuestions()
}

async function handleInvite() {
  error.value = null
  loading.value = true
  try {
    const res = await createInvite(assessmentId, inviteForm.value)
    inviteResult.value = res.data
    inviteForm.value = { candidateName: '', candidateEmail: '' }
    showInviteForm.value = false
    await navigator.clipboard.writeText(`${appUrl}/candidate/${res.data.inviteToken}`)
    copied.value = true
    setTimeout(() => (copied.value = false), 2000)
  } catch (e) {
    error.value = e.response?.data?.message || 'Failed to create invite'
  } finally {
    loading.value = false
  }
}

async function handleSaveSettings() {
  settingsError.value = null
  settingsLoading.value = true
  try {
    const res = await updateAssessment(assessmentId, settingsForm.value)
    assessment.value = res.data
    assessmentStore.assessments = assessmentStore.assessments.map((a) =>
      a.id === assessmentId ? res.data : a
    )
    settingsOriginal.value = { ...settingsForm.value }
    settingsSaved.value = true
    setTimeout(() => (settingsSaved.value = false), 2000)
  } catch (e) {
    settingsError.value = e.response?.data?.message || 'Failed to save'
  } finally {
    settingsLoading.value = false
  }
}

async function copyInviteLink() {
  const link = `${appUrl}/candidate/${inviteResult.value.inviteToken}`
  await navigator.clipboard.writeText(link)
  copied.value = true
  setTimeout(() => (copied.value = false), 2000)
}
</script>

<template>
  <div class="page__inner fade-in">
    <button class="back-link" @click="router.push('/dashboard')">
      ← Back to assessments
    </button>

    <div v-if="assessment">
      <header class="page__header">
        <div class="page__title">
          <span class="eyebrow">Assessment № {{ String(assessmentId).padStart(3, '0') }}</span>
          <h1>{{ assessment.title }}</h1>
          <p class="page__subtitle">{{ assessment.description }}</p>
          <div style="display: flex; gap: 8px; margin-top: 14px; flex-wrap: wrap;">
            <span :class="['tag', 'tag--dot', assessment.isActive ? 'tag--ok' : 'tag--warn']">
              {{ assessment.isActive ? 'active' : 'inactive' }}
            </span>
            <span class="tag">{{ assessment.durationMinutes }} min</span>
            <span class="tag">{{ questions.length }} questions</span>
          </div>
        </div>
        <div class="page__actions">
          <button class="btn btn--ghost" @click="router.push(`/assessments/${assessmentId}/results`)">
            View results →
          </button>
          <button class="btn btn--ghost" @click="showInviteForm = !showInviteForm">
            <svg width="13" height="13" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5"><rect x="3" y="5" width="18" height="14" rx="2"/><path d="M3 7l9 6 9-6"/></svg>
            {{ showInviteForm ? 'Cancel' : 'Send invite' }}
          </button>
          <button
            class="btn btn--primary"
            @click="showQuestionForm ? cancelQuestionForm() : (resetQuestionForm(), showQuestionForm = true)"
          >
            {{ showQuestionForm ? 'Cancel' : '+ Add question' }}
          </button>
        </div>
      </header>

      <!-- Invite form -->
      <div v-if="showInviteForm" class="card fade-in" style="padding: 24px; margin-bottom: 22px;">
        <div style="display: flex; align-items: baseline; gap: 12px; margin-bottom: 18px;">
          <h2 class="display" style="font-size: 24px; margin: 0;">Send invite</h2>
          <span class="eyebrow">Candidate receives a unique link</span>
        </div>
        <form @submit.prevent="handleInvite">
          <div class="form-row" style="margin-bottom: 18px;">
            <div class="field">
              <label class="field__label">Candidate name</label>
              <input class="input" v-model="inviteForm.candidateName" placeholder="Jane Doe" required />
            </div>
            <div class="field">
              <label class="field__label">Email</label>
              <input class="input" type="email" v-model="inviteForm.candidateEmail" placeholder="jane@example.com" required />
            </div>
          </div>
          <div style="display: flex; justify-content: flex-end; gap: 8px;">
            <button type="button" class="btn btn--ghost" @click="showInviteForm = false">Cancel</button>
            <button type="submit" class="btn btn--primary" :disabled="loading">
              <span v-if="loading" class="spinner" />
              Send invite
            </button>
          </div>
        </form>
      </div>

      <!-- Invite result banner -->
      <div v-if="inviteResult" class="card fade-in invite-banner">
        <div style="display: flex; align-items: center; gap: 14px;">
          <div style="width: 32px; height: 32px; border-radius: 50%; background: var(--ok); color: #fff; display: grid; place-items: center; flex-shrink: 0;">✓</div>
          <div>
            <div style="font-weight: 500;">Invite sent to {{ inviteResult.inviteToken ? 'candidate' : '' }}</div>
            <div class="mono" style="font-size: 11px; color: var(--muted); margin-top: 2px; overflow: hidden; text-overflow: ellipsis; white-space: nowrap; max-width: 400px;">
              {{ appUrl }}/candidate/{{ inviteResult.inviteToken }}
            </div>
          </div>
        </div>
        <div style="display: flex; gap: 8px; flex-shrink: 0;">
          <button class="btn btn--ghost btn--sm" @click="copyInviteLink">
            {{ copied ? '✓ Copied' : 'Copy link' }}
          </button>
          <button class="btn btn--link btn--sm" @click="inviteResult = null">✕</button>
        </div>
      </div>

      <!-- Two-column layout -->
      <div class="twocol">
        <div>
          <!-- Question form -->
          <div v-if="showQuestionForm" class="card fade-in" style="padding: 24px; margin-bottom: 14px;">
            <div style="display: flex; justify-content: space-between; align-items: baseline; margin-bottom: 16px;">
              <h2 class="display" style="font-size: 24px; margin: 0;">
                {{ editingQuestionId ? 'Edit question' : 'New question' }}
              </h2>
              <div style="display: flex; gap: 4px; padding: 3px; background: var(--bg-2); border-radius: 4px;">
                <button
                  v-for="t in ['MULTIPLE_CHOICE', 'TRUE_FALSE', 'OPEN_TEXT']"
                  :key="t"
                  type="button"
                  :class="['btn', 'btn--sm', questionForm.type === t ? 'btn--primary' : '']"
                  :style="questionForm.type !== t ? 'color: var(--muted)' : ''"
                  @click="questionForm.type = t"
                >{{ t.replace('_', ' ').toLowerCase() }}</button>
              </div>
            </div>

            <form @submit.prevent="handleSubmitQuestion">
              <div class="field" style="margin-bottom: 16px;">
                <label class="field__label">Question text</label>
                <textarea class="input" v-model="questionForm.text" rows="3" placeholder="Write your question…" required />
              </div>

              <div v-if="questionForm.type !== 'OPEN_TEXT'" class="field" style="margin-bottom: 16px;">
                <label class="field__label">Answers · check to mark correct</label>
                <div style="display: flex; flex-direction: column; gap: 8px;">
                  <div v-for="(opt, i) in questionForm.options" :key="i" style="display: flex; gap: 10px; align-items: center;">
                    <button
                      type="button"
                      class="icon-btn"
                      :style="opt.isCorrect ? 'background: var(--ok); color: #fff; border: 1px solid var(--ok);' : 'border: 1px solid var(--rule);'"
                      @click="questionForm.options = questionForm.options.map((x, j) => j === i ? { ...x, isCorrect: !x.isCorrect } : x)"
                    >✓</button>
                    <input class="input" v-model="opt.text" :placeholder="`Option ${i + 1}`" />
                    <button type="button" class="icon-btn danger" @click="removeOption(i)">✕</button>
                  </div>
                  <div style="display: flex; gap: 8px; align-items: center; margin-top: 4px;">
                    <input class="input" v-model="newOption.text" placeholder="New option…" @keydown.enter.prevent="addOption" />
                    <label style="display: flex; align-items: center; gap: 6px; white-space: nowrap; font-family: var(--f-mono); font-size: 11px; color: var(--muted);">
                      <input type="checkbox" v-model="newOption.isCorrect" />
                      Correct
                    </label>
                    <button type="button" class="btn btn--ghost btn--sm" @click="addOption">Add</button>
                  </div>
                </div>
              </div>

              <p v-if="error" class="error-text" style="margin-bottom: 12px;">{{ error }}</p>
              <div style="display: flex; justify-content: flex-end; gap: 8px;">
                <button type="button" class="btn btn--ghost" @click="cancelQuestionForm">Cancel</button>
                <button type="submit" class="btn btn--primary" :disabled="loading">
                  <span v-if="loading" class="spinner" />
                  {{ editingQuestionId ? 'Save changes' : 'Save question' }}
                </button>
              </div>
            </form>
          </div>

          <!-- Questions list -->
          <div class="section">
            <h2>Questions</h2>
            <span class="section__eyebrow">
              <span class="num">{{ String(questions.length).padStart(2, '0') }} total</span>
            </span>
          </div>

          <div v-if="questions.length === 0 && !showQuestionForm" class="empty-state" style="padding: 48px 20px;">
            <p class="empty-title">No questions yet</p>
            <p style="color: var(--muted); margin-bottom: 16px;">Add your first question to get started.</p>
          </div>
          <div v-else class="qlist">
            <QuestionCard
              v-for="q in questions"
              :key="q.id"
              :question="q"
              @edit="openEditQuestion"
              @delete="handleDeleteQuestion"
            />
          </div>
        </div>

        <!-- Sidebar -->
        <aside class="twocol__side">
          <div class="card" style="padding: 22px;">
            <div class="eyebrow" style="margin-bottom: 16px;">Settings</div>
            <form v-if="settingsForm" @submit.prevent="handleSaveSettings">
              <div class="field" style="margin-bottom: 12px;">
                <label class="field__label">Title</label>
                <input class="input" v-model="settingsForm.title" required />
              </div>
              <div class="field" style="margin-bottom: 12px;">
                <label class="field__label">Description</label>
                <textarea class="input" v-model="settingsForm.description" rows="2" />
              </div>
              <div class="field" style="margin-bottom: 12px;">
                <label class="field__label">Duration (minutes)</label>
                <input class="input" type="number" min="1" v-model.number="settingsForm.durationMinutes" required />
              </div>
              <div style="display: flex; align-items: center; gap: 10px; margin-bottom: 18px;">
                <input type="checkbox" id="isActive" v-model="settingsForm.isActive" />
                <label for="isActive" style="font-size: 13px; color: var(--ink);">Active</label>
              </div>
              <p v-if="settingsError" class="error-text" style="margin-bottom: 10px;">{{ settingsError }}</p>
              <button type="submit" class="btn btn--primary btn--sm btn--block" :disabled="settingsLoading || !settingsChanged">
                <span v-if="settingsLoading" class="spinner" />
                {{ settingsSaved ? '✓ Saved' : 'Save changes' }}
              </button>
            </form>
          </div>
        </aside>
      </div>
    </div>
  </div>
</template>

<style scoped>
.invite-banner {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 16px;
  padding: 18px 22px;
  margin-bottom: 22px;
  background: color-mix(in srgb, var(--ok) 8%, var(--surface));
  border-color: color-mix(in srgb, var(--ok) 25%, var(--rule));
}

@media (max-width: 640px) {
  .invite-banner { flex-direction: column; align-items: flex-start; }
}
</style>
