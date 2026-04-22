<script setup>
import { ref, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useAssessmentStore } from '@/stores/assessment'
import { getQuestions, deleteQuestion } from '@/api/questions'
import { createInvite } from '@/api/assessments'
import QuestionCard from '@/components/assessment/QuestionCard.vue'
import QuestionForm from '@/components/assessment/QuestionForm.vue'
import AssessmentSettings from '@/components/assessment/AssessmentSettings.vue'

const route = useRoute()
const router = useRouter()
const assessmentStore = useAssessmentStore()

const assessmentId = Number(route.params.id)
const assessment = ref(null)
const questions = ref([])

const showQuestionForm = ref(false)
const editingQuestion = ref(null)

const showInviteForm = ref(false)
const inviteForm = ref({ candidateName: '', candidateEmail: '' })
const inviteResult = ref(null)
const copied = ref(false)

const error = ref(null)
const loading = ref(false)

const appUrl = import.meta.env.VITE_APP_URL

onMounted(async () => {
  if (assessmentStore.assessments.length === 0) await assessmentStore.fetchAll()
  assessment.value = assessmentStore.assessments.find((a) => a.id === assessmentId)
  await loadQuestions()
})

async function loadQuestions() {
  const res = await getQuestions(assessmentId)
  questions.value = res.data
}

function openEditQuestion(q) {
  editingQuestion.value = q
  showQuestionForm.value = true
}

function openNewQuestion() {
  editingQuestion.value = null
  showQuestionForm.value = true
}

function closeQuestionForm() {
  showQuestionForm.value = false
  editingQuestion.value = null
  error.value = null
}

async function handleQuestionSaved() {
  closeQuestionForm()
  await loadQuestions()
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

async function copyInviteLink() {
  const link = `${appUrl}/candidate/${inviteResult.value.inviteToken}`
  await navigator.clipboard.writeText(link)
  copied.value = true
  setTimeout(() => (copied.value = false), 2000)
}

function onSettingsSaved(updated) {
  assessment.value = updated
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
          <div class="asmview__tags">
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
            @click="showQuestionForm ? closeQuestionForm() : openNewQuestion()"
          >
            {{ showQuestionForm ? 'Cancel' : '+ Add question' }}
          </button>
        </div>
      </header>

      <!-- Invite form -->
      <div v-if="showInviteForm" class="card fade-in invite-form">
        <div class="invite-form__header">
          <h2 class="display invite-form__title">Send invite</h2>
          <span class="eyebrow">Candidate receives a unique link</span>
        </div>
        <form @submit.prevent="handleInvite">
          <div class="form-row invite-form__row">
            <div class="field">
              <label class="field__label">Candidate name</label>
              <input class="input" v-model="inviteForm.candidateName" placeholder="Jane Doe" required />
            </div>
            <div class="field">
              <label class="field__label">Email</label>
              <input class="input" type="email" v-model="inviteForm.candidateEmail" placeholder="jane@example.com" required />
            </div>
          </div>
          <div class="form-actions">
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
        <div class="invite-banner__body">
          <div class="invite-banner__check">✓</div>
          <div>
            <div class="invite-banner__label">Invite sent to {{ inviteResult.inviteToken ? 'candidate' : '' }}</div>
            <div class="mono invite-banner__url">
              {{ appUrl }}/candidate/{{ inviteResult.inviteToken }}
            </div>
          </div>
        </div>
        <div class="invite-banner__actions">
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
          <QuestionForm
            v-if="showQuestionForm"
            :assessmentId="assessmentId"
            :editingQuestion="editingQuestion"
            :nextOrderIndex="questions.length + 1"
            @saved="handleQuestionSaved"
            @cancel="closeQuestionForm"
          />

          <!-- Questions list -->
          <div class="section">
            <h2>Questions</h2>
            <span class="section__eyebrow">
              <span class="num">{{ String(questions.length).padStart(2, '0') }} total</span>
            </span>
          </div>

          <div v-if="questions.length === 0 && !showQuestionForm" class="empty-state asmview__empty">
            <p class="empty-title">No questions yet</p>
            <p class="text-muted">Add your first question to get started.</p>
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
          <AssessmentSettings
            :assessmentId="assessmentId"
            :assessment="assessment"
            @saved="onSettingsSaved"
          />
        </aside>
      </div>
    </div>
  </div>
</template>

<style scoped>
.asmview__tags { display: flex; gap: 8px; margin-top: 14px; flex-wrap: wrap; }
.asmview__empty { padding: 48px 20px; }

.invite-form { padding: 24px; margin-bottom: 22px; }
.invite-form__header { display: flex; align-items: baseline; gap: 12px; margin-bottom: 18px; }
.invite-form__title { font-size: 24px; margin: 0; }
.invite-form__row { margin-bottom: 18px; }

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
.invite-banner__body { display: flex; align-items: center; gap: 14px; }
.invite-banner__check {
  width: 32px;
  height: 32px;
  border-radius: 50%;
  background: var(--ok);
  color: #fff;
  display: grid;
  place-items: center;
  flex-shrink: 0;
}
.invite-banner__label { font-weight: 500; }
.invite-banner__url {
  font-size: 11px;
  color: var(--muted);
  margin-top: 2px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
  max-width: 400px;
}
.invite-banner__actions { display: flex; gap: 8px; flex-shrink: 0; }

@media (max-width: 640px) {
  .invite-banner { flex-direction: column; align-items: flex-start; }
}
</style>
