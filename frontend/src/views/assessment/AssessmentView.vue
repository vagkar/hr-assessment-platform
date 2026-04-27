<script setup>
import { ref, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useAssessmentStore } from '@/stores/assessment'
import { getQuestions, deleteQuestion } from '@/api/questions'
import QuestionCard from '@/components/assessment/QuestionCard.vue'
import QuestionForm from '@/components/assessment/QuestionForm.vue'
import AssessmentSettings from '@/components/assessment/AssessmentSettings.vue'
import InviteForm from '@/components/assessment/InviteForm.vue'
import InviteBanner from '@/components/assessment/InviteBanner.vue'
import BaseButton from '@/components/ui/BaseButton.vue'

const route = useRoute()
const router = useRouter()
const assessmentStore = useAssessmentStore()

const assessmentId = Number(route.params.id)
const assessment = ref(null)
const questions = ref([])

const showQuestionForm = ref(false)
const editingQuestion = ref(null)

const showInviteForm = ref(false)
const inviteResult = ref(null)

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

function onInviteSent(result) {
  inviteResult.value = result
  showInviteForm.value = false
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
          <BaseButton variant="ghost" @click="router.push(`/assessments/${assessmentId}/results`)">
            View results →
          </BaseButton>
          <BaseButton variant="ghost" @click="showInviteForm = !showInviteForm">
            <svg width="13" height="13" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5"><rect x="3" y="5" width="18" height="14" rx="2"/><path d="M3 7l9 6 9-6"/></svg>
            {{ showInviteForm ? 'Cancel' : 'Send invite' }}
          </BaseButton>
          <BaseButton
            variant="primary"
            @click="showQuestionForm ? closeQuestionForm() : openNewQuestion()"
          >
            {{ showQuestionForm ? 'Cancel' : '+ Add question' }}
          </BaseButton>
        </div>
      </header>

      <InviteForm
        v-if="showInviteForm"
        :assessment-id="assessmentId"
        @sent="onInviteSent"
        @cancel="showInviteForm = false"
      />

      <InviteBanner
        v-if="inviteResult"
        :invite-token="inviteResult.inviteToken"
        @dismiss="inviteResult = null"
      />

      <!-- Two-column layout -->
      <div class="twocol">
        <div>
          <QuestionForm
            v-if="showQuestionForm"
            :assessment-id="assessmentId"
            :editing-question="editingQuestion"
            :next-order-index="questions.length + 1"
            @saved="handleQuestionSaved"
            @cancel="closeQuestionForm"
          />

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

        <aside class="twocol__side">
          <AssessmentSettings
            :assessment-id="assessmentId"
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
</style>
