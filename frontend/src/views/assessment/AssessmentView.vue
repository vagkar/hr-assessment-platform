<script setup>
import { ref, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useAssessmentStore } from '@/stores/assessment'
import { getQuestions, createQuestion, updateQuestion, deleteQuestion } from '@/api/questions'
import { createInvite } from '@/api/assessments'
import BaseButton from '@/components/BaseButton.vue'
import BaseCard from '@/components/BaseCard.vue'
import BaseInput from '@/components/BaseInput.vue'
import FormGroup from '@/components/FormGroup.vue'
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

const appUrl = import.meta.env.VITE_APP_URL

onMounted(async () => {
  assessment.value = assessmentStore.assessments.find((a) => a.id === assessmentId)
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

function cancelQuestionForm() {
  showQuestionForm.value = false
  editingQuestionId.value = null
  error.value = null
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
    questionForm.value = { text: '', type: 'MULTIPLE_CHOICE', orderIndex: questions.value.length + 1, options: [] }
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
</script>

<template>
  <div>
    <button class="back-link" @click="router.push('/dashboard')">← Back to Assessments</button>

    <div v-if="assessment">
      <header class="assessment-header">
        <div class="header-info">
          <h1>{{ assessment.title }}</h1>
          <div class="header-meta">
            <span class="chip">{{ assessment.durationMinutes }} min</span>
            <span class="chip">{{ questions.length }} question{{ questions.length !== 1 ? 's' : '' }}</span>
          </div>
        </div>
        <div class="header-actions">
          <BaseButton variant="outline" @click="router.push(`/assessments/${assessmentId}/results`)">
            Results
          </BaseButton>
          <BaseButton variant="outline" @click="showInviteForm = !showInviteForm">
            {{ showInviteForm ? 'Cancel' : 'Send Invite' }}
          </BaseButton>
          <BaseButton @click="showQuestionForm ? cancelQuestionForm() : (showQuestionForm = true)">
            {{ showQuestionForm ? 'Cancel' : '+ Add Question' }}
          </BaseButton>
        </div>
      </header>

      <!-- Invite form -->
      <BaseCard v-if="showInviteForm" class="form-card">
        <h2 class="form-title">Send Invite</h2>
        <p class="form-subtitle">The candidate will receive a unique link to take this assessment.</p>
        <form @submit.prevent="handleInvite">
          <div class="form-row">
            <FormGroup label="Candidate Name">
              <BaseInput v-model="inviteForm.candidateName" placeholder="Jane Doe" required />
            </FormGroup>
            <FormGroup label="Candidate Email">
              <BaseInput v-model="inviteForm.candidateEmail" type="email" placeholder="jane@example.com" required />
            </FormGroup>
          </div>
          <div class="form-actions">
            <BaseButton type="submit" :loading="loading">Send Invite</BaseButton>
          </div>
        </form>
      </BaseCard>

      <!-- Invite result -->
      <div v-if="inviteResult" class="invite-banner">
        <div class="invite-banner-content">
          <span class="invite-banner-icon">✓</span>
          <div class="invite-banner-text">
            <strong>Invite created!</strong>
            <span class="invite-link">{{ `${appUrl}/candidate/${inviteResult.inviteToken}` }}</span>
          </div>
        </div>
        <div class="invite-banner-actions">
          <BaseButton variant="outline" @click="copyInviteLink">
            {{ copied ? '✓ Copied' : 'Copy Link' }}
          </BaseButton>
          <BaseButton variant="outline" @click="inviteResult = null">Dismiss</BaseButton>
        </div>
      </div>

      <!-- Question form -->
      <BaseCard v-if="showQuestionForm" class="form-card">
        <h2 class="form-title">{{ editingQuestionId ? 'Edit Question' : 'New Question' }}</h2>
        <form @submit.prevent="handleSubmitQuestion">
          <FormGroup label="Question Text">
            <BaseInput v-model="questionForm.text" :rows="3" placeholder="Enter your question..." required />
          </FormGroup>
          <div class="form-row">
            <FormGroup label="Type">
              <select v-model="questionForm.type">
                <option value="MULTIPLE_CHOICE">Multiple Choice</option>
                <option value="TRUE_FALSE">True / False</option>
                <option value="OPEN_TEXT">Open Text</option>
              </select>
            </FormGroup>
            <FormGroup label="Order">
              <BaseInput v-model="questionForm.orderIndex" type="number" min="1" required />
            </FormGroup>
          </div>

          <div v-if="questionForm.type !== 'OPEN_TEXT'" class="options-section">
            <p class="options-label">Answer Options</p>
            <div v-if="questionForm.options.length" class="options-list">
              <div v-for="(opt, i) in questionForm.options" :key="i" class="option-row">
                <span class="option-text">{{ opt.text }}</span>
                <span :class="['option-badge', opt.isCorrect ? 'badge-correct' : 'badge-wrong']">
                  {{ opt.isCorrect ? 'Correct' : 'Wrong' }}
                </span>
                <button type="button" class="remove-btn" @click="removeOption(i)" title="Remove">✕</button>
              </div>
            </div>
            <div class="option-add">
              <BaseInput v-model="newOption.text" placeholder="Add an option..." />
              <label class="checkbox-label">
                <input v-model="newOption.isCorrect" type="checkbox" />
                Correct
              </label>
              <BaseButton type="button" variant="outline" @click="addOption">Add</BaseButton>
            </div>
          </div>

          <p v-if="error" class="error-text">{{ error }}</p>
          <div class="form-actions">
            <BaseButton variant="outline" type="button" @click="cancelQuestionForm">Cancel</BaseButton>
            <BaseButton type="submit" :loading="loading">
              {{ editingQuestionId ? 'Save Changes' : 'Save Question' }}
            </BaseButton>
          </div>
        </form>
      </BaseCard>

      <!-- Questions list -->
      <div v-if="questions.length === 0 && !showQuestionForm" class="empty-state">
        <div class="empty-icon">📝</div>
        <p class="empty-title">No questions yet</p>
        <p class="text-muted">Add your first question to get started.</p>
      </div>
      <div v-else class="question-list">
        <BaseCard v-for="q in questions" :key="q.id" class="question-item">
          <QuestionCard
            :question="q"
            @edit="openEditQuestion"
            @delete="handleDeleteQuestion"
          />
        </BaseCard>
      </div>
    </div>
  </div>
</template>

<style scoped>
.assessment-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: var(--space-xl);
  gap: var(--space-md);
}

.header-info h1 { margin-bottom: var(--space-xs); }

.header-meta {
  display: flex;
  gap: var(--space-sm);
  margin-top: var(--space-xs);
}

.header-actions {
  display: flex;
  gap: var(--space-sm);
  flex-shrink: 0;
}

/* Forms */
.form-title { margin-bottom: var(--space-xs); }

/* Invite banner */
.invite-banner {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: var(--space-md);
  padding: var(--space-md) var(--space-lg);
  background: color-mix(in srgb, var(--color-success) 10%, var(--color-bg-card));
  border: 1px solid color-mix(in srgb, var(--color-success) 30%, transparent);
  border-radius: var(--radius);
  margin-bottom: var(--space-lg);
}

.invite-banner-content {
  display: flex;
  align-items: center;
  gap: var(--space-md);
  min-width: 0;
}

.invite-banner-icon {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 28px;
  height: 28px;
  background: var(--color-success);
  color: #fff;
  border-radius: 50%;
  font-size: 0.875rem;
  flex-shrink: 0;
}

.invite-banner-text {
  display: flex;
  flex-direction: column;
  gap: 0.125rem;
  min-width: 0;
}

.invite-link {
  font-size: 0.8125rem;
  color: var(--color-text-muted);
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.invite-banner-actions {
  display: flex;
  gap: var(--space-sm);
  flex-shrink: 0;
}

/* Options */
.options-section { margin-top: var(--space-md); }

.options-label {
  font-size: 0.8125rem;
  font-weight: 600;
  letter-spacing: 0.01em;
  color: var(--color-text-muted);
  text-transform: uppercase;
  margin-bottom: var(--space-sm);
}

.options-list {
  display: flex;
  flex-direction: column;
  gap: var(--space-xs);
  margin-bottom: var(--space-sm);
}

.option-row {
  display: flex;
  align-items: center;
  gap: var(--space-sm);
  padding: var(--space-sm) var(--space-md);
  background: var(--color-bg);
  border: 1px solid var(--color-border);
  border-radius: var(--radius-sm);
}

.option-text { flex: 1; font-size: 0.9375rem; }

.option-badge {
  font-size: 0.75rem;
  font-weight: 600;
  padding: 0.125rem 0.5rem;
  border-radius: 999px;
}

.badge-correct { background: color-mix(in srgb, var(--color-success) 15%, transparent); color: var(--color-success); }
.badge-wrong   { background: var(--color-bg-hover); color: var(--color-text-muted); }

.remove-btn {
  background: none;
  border: none;
  color: var(--color-text-muted);
  cursor: pointer;
  font-size: 0.75rem;
  padding: 0.25rem;
  border-radius: var(--radius-sm);
  transition: color var(--transition), background var(--transition);
  line-height: 1;
}
.remove-btn:hover { color: var(--color-danger); background: color-mix(in srgb, var(--color-danger) 10%, transparent); }

.option-add {
  display: flex;
  align-items: center;
  gap: var(--space-sm);
}
.option-add input[type="text"] { flex: 1; }

.checkbox-label {
  display: flex;
  align-items: center;
  gap: var(--space-xs);
  white-space: nowrap;
  font-size: 0.875rem;
  font-weight: 500;
  color: var(--color-text);
  text-transform: none;
  letter-spacing: 0;
}

/* Questions list */
.question-list {
  display: flex;
  flex-direction: column;
  gap: var(--space-sm);
}

.question-item { padding: var(--space-lg); }

</style>
