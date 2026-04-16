<script setup>
import { ref, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useAssessmentStore } from '@/stores/assessment'
import { getQuestions, createQuestion, deleteQuestion } from '@/api/questions'
import { createInvite } from '@/api/assessments'
import BaseButton from '@/components/BaseButton.vue'
import BaseCard from '@/components/BaseCard.vue'
import FormGroup from '@/components/FormGroup.vue'

const route = useRoute()
const router = useRouter()
const assessmentStore = useAssessmentStore()

const assessmentId = Number(route.params.id)
const assessment = ref(null)
const questions = ref([])

const showQuestionForm = ref(false)
const questionForm = ref({ text: '', type: 'MULTIPLE_CHOICE', orderIndex: 1, options: [] })
const newOption = ref({ text: '', isCorrect: false })

const showInviteForm = ref(false)
const inviteForm = ref({ candidateName: '', candidateEmail: '' })
const inviteResult = ref(null)

const error = ref(null)
const loading = ref(false)

onMounted(async () => {
  assessment.value = assessmentStore.assessments.find((a) => a.id === assessmentId)
  await loadQuestions()
})

async function loadQuestions() {
  const res = await getQuestions(assessmentId)
  questions.value = res.data
}

function addOption() {
  if (!newOption.value.text) return
  questionForm.value.options.push({ ...newOption.value })
  newOption.value = { text: '', isCorrect: false }
}

function removeOption(index) {
  questionForm.value.options.splice(index, 1)
}

async function handleCreateQuestion() {
  error.value = null
  loading.value = true
  try {
    await createQuestion(assessmentId, questionForm.value)
    questionForm.value = { text: '', type: 'MULTIPLE_CHOICE', orderIndex: questions.value.length + 1, options: [] }
    showQuestionForm.value = false
    await loadQuestions()
  } catch (e) {
    error.value = e.response?.data?.message || 'Failed to create question'
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
</script>

<template>
  <div>
    <BaseButton variant="outline" style="margin-bottom: 1rem" @click="router.push('/dashboard')">
      ← Back
    </BaseButton>

    <div v-if="assessment">
      <header class="assessment-header">
        <div>
          <h1>{{ assessment.title }}</h1>
          <p class="text-muted">{{ assessment.durationMinutes }} min</p>
        </div>
        <div class="header-actions">
          <BaseButton variant="outline" @click="router.push(`/assessments/${assessmentId}/results`)">
            Results
          </BaseButton>
          <BaseButton variant="outline" @click="showInviteForm = !showInviteForm">
            {{ showInviteForm ? 'Cancel' : 'Send Invite' }}
          </BaseButton>
          <BaseButton @click="showQuestionForm = !showQuestionForm">
            {{ showQuestionForm ? 'Cancel' : '+ Add Question' }}
          </BaseButton>
        </div>
      </header>

      <!-- Invite form -->
      <BaseCard v-if="showInviteForm" style="margin-bottom: 1.5rem">
        <h2>Send Invite</h2>
        <form @submit.prevent="handleInvite">
          <FormGroup label="Candidate Name">
            <input v-model="inviteForm.candidateName" type="text" required />
          </FormGroup>
          <FormGroup label="Candidate Email">
            <input v-model="inviteForm.candidateEmail" type="email" required />
          </FormGroup>
          <div class="form-actions">
            <BaseButton type="submit" :loading="loading">
              {{ loading ? 'Sending...' : 'Send' }}
            </BaseButton>
          </div>
        </form>
      </BaseCard>

      <!-- Invite result -->
      <BaseCard v-if="inviteResult" class="invite-result" style="margin-bottom: 1.5rem">
        <p><strong>Invite created!</strong> Share this link with the candidate:</p>
        <code>{{ `http://localhost:5173/candidate/${inviteResult.inviteToken}` }}</code>
        <BaseButton variant="outline" style="margin-top: 0.5rem" @click="inviteResult = null">Dismiss</BaseButton>
      </BaseCard>

      <!-- Question form -->
      <BaseCard v-if="showQuestionForm" style="margin-bottom: 1.5rem">
        <h2>New Question</h2>
        <form @submit.prevent="handleCreateQuestion">
          <FormGroup label="Question Text">
            <textarea v-model="questionForm.text" rows="2" required />
          </FormGroup>
          <FormGroup label="Type">
            <select v-model="questionForm.type">
              <option value="MULTIPLE_CHOICE">Multiple Choice</option>
              <option value="TRUE_FALSE">True / False</option>
              <option value="OPEN_TEXT">Open Text</option>
            </select>
          </FormGroup>
          <FormGroup label="Order">
            <input v-model.number="questionForm.orderIndex" type="number" min="1" required />
          </FormGroup>

          <div v-if="questionForm.type !== 'OPEN_TEXT'">
            <label>Options</label>
            <div v-for="(opt, i) in questionForm.options" :key="i" class="option-row">
              <span>{{ opt.text }}</span>
              <span :class="opt.isCorrect ? 'correct' : 'wrong'">
                {{ opt.isCorrect ? 'Correct' : 'Wrong' }}
              </span>
              <BaseButton type="button" variant="outline" @click="removeOption(i)">Remove</BaseButton>
            </div>
            <div class="option-add">
              <input v-model="newOption.text" type="text" placeholder="Option text" />
              <label class="checkbox-label">
                <input v-model="newOption.isCorrect" type="checkbox" />
                Correct
              </label>
              <BaseButton type="button" variant="outline" @click="addOption">Add</BaseButton>
            </div>
          </div>

          <p v-if="error" class="error-text">{{ error }}</p>
          <div class="form-actions">
            <BaseButton type="submit" :loading="loading">
              {{ loading ? 'Saving...' : 'Save Question' }}
            </BaseButton>
          </div>
        </form>
      </BaseCard>

      <!-- Questions list -->
      <p v-if="questions.length === 0" class="text-muted">No questions yet.</p>
      <div class="question-list">
        <BaseCard v-for="q in questions" :key="q.id" class="question-card">
          <div class="question-info">
            <p class="question-type">{{ q.type }}</p>
            <p>{{ q.text }}</p>
            <ul v-if="q.options.length" class="options-list">
              <li v-for="opt in q.options" :key="opt.id" :class="{ correct: opt.isCorrect }">
                {{ opt.text }}
              </li>
            </ul>
          </div>
          <BaseButton variant="danger" @click="handleDeleteQuestion(q.id)">Delete</BaseButton>
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
}

.header-actions {
  display: flex;
  gap: var(--space-sm);
}

.question-list {
  display: flex;
  flex-direction: column;
  gap: var(--space-md);
}

.question-card {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  gap: var(--space-md);
}

.question-type {
  font-size: 0.75rem;
  font-weight: 600;
  color: var(--color-primary);
  text-transform: uppercase;
  margin-bottom: var(--space-xs);
}

.options-list {
  margin-top: var(--space-sm);
  padding-left: var(--space-md);
  list-style: disc;
}

.options-list li.correct {
  color: var(--color-success);
  font-weight: 500;
}

.option-row {
  display: flex;
  align-items: center;
  gap: var(--space-sm);
  margin-bottom: var(--space-xs);
}

.option-add {
  display: flex;
  align-items: center;
  gap: var(--space-sm);
  margin-top: var(--space-sm);
}

.option-add input[type="text"] {
  flex: 1;
}

.checkbox-label {
  display: flex;
  align-items: center;
  gap: var(--space-xs);
  white-space: nowrap;
}

.correct { color: var(--color-success); }
.wrong { color: var(--color-text-muted); }

.invite-result {
  margin-bottom: var(--space-lg);
  background: #f0fdf4;
  border: 1px solid #bbf7d0;
}

.invite-result code {
  display: block;
  margin-top: var(--space-sm);
  padding: var(--space-sm);
  background: white;
  border-radius: var(--radius);
  font-size: 0.875rem;
  word-break: break-all;
}
</style>
