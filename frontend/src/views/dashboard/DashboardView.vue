<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useAssessmentStore } from '@/stores/assessment'
import AssessmentCard from '@/components/assessment/AssessmentCard.vue'
import BaseButton from '@/components/BaseButton.vue'
import BaseCard from '@/components/BaseCard.vue'
import BaseInput from '@/components/BaseInput.vue'

const router = useRouter()
const assessmentStore = useAssessmentStore()

const showForm = ref(false)
const form = ref({ title: '', description: '', durationMinutes: 30, isActive: true })
const error = ref(null)
const loading = ref(false)

onMounted(() => assessmentStore.fetchAll())

function cancelForm() {
  showForm.value = false
  error.value = null
}

async function handleSubmit() {
  error.value = null
  loading.value = true
  try {
    await assessmentStore.create(form.value)
    cancelForm()
  } catch (e) {
    error.value = e.response?.data?.message || 'Failed to save assessment'
  } finally {
    loading.value = false
  }
}

async function handleDelete(id) {
  if (!confirm('Delete this assessment?')) return
  await assessmentStore.remove(id)
}
</script>

<template>
  <div class="page__inner fade-in">
    <header class="page__header">
      <div class="page__title">
        <span class="eyebrow">Workspace</span>
        <h1>Your <em>assessments</em></h1>
        <p class="page__subtitle">Create timed screens and invite candidates to take them.</p>
      </div>
      <div class="page__actions">
        <BaseButton v-if="!showForm" variant="primary" @click="showForm = true">+ New assessment</BaseButton>
        <BaseButton v-else variant="ghost" @click="cancelForm">Cancel</BaseButton>
      </div>
    </header>

    <!-- New assessment form -->
    <BaseCard v-if="showForm" class="fade-in create-card">
      <h2 class="display create-card__title">New assessment</h2>
      <form @submit.prevent="handleSubmit">
        <div class="form-row create-card__row">
          <div class="field">
            <label class="field__label">Title</label>
            <BaseInput v-model="form.title" placeholder="e.g. Senior Frontend Engineer" required />
          </div>
          <div class="field">
            <label class="field__label">Duration (minutes)</label>
            <BaseInput type="number" v-model.number="form.durationMinutes" :min="1" required />
          </div>
        </div>
        <div class="field create-card__field">
          <label class="field__label">Description</label>
          <BaseInput v-model="form.description" placeholder="Short summary candidates will see before starting" />
        </div>
        <div class="checkbox-row create-card__checkbox">
          <input type="checkbox" id="isActive" v-model="form.isActive" />
          <label for="isActive">Active</label>
        </div>
        <p v-if="error" class="error-text create-card__error">{{ error }}</p>
        <div class="form-actions">
          <BaseButton type="button" variant="ghost" @click="cancelForm">Cancel</BaseButton>
          <BaseButton type="submit" variant="primary" :loading="loading">Create →</BaseButton>
        </div>
      </form>
    </BaseCard>

    <!-- Empty state -->
    <div v-if="assessmentStore.assessments.length === 0 && !showForm" class="empty-state">
      <p class="empty-title">No assessments yet</p>
      <p class="text-muted">Create your first assessment to start evaluating candidates.</p>
      <BaseButton variant="primary" @click="showForm = true">+ New assessment</BaseButton>
    </div>

    <!-- Assessment list -->
    <template v-else>
      <div class="section">
        <h2>All assessments</h2>
        <span class="section__eyebrow">
          <span class="num">{{ String(assessmentStore.assessments.length).padStart(2, '0') }}</span>
        </span>
      </div>

      <div class="alist">
        <AssessmentCard
          v-for="(assessment, i) in assessmentStore.assessments"
          :key="assessment.id"
          :assessment="assessment"
          :index="i"
          @edit="router.push(`/assessments/${assessment.id}`)"
          @delete="handleDelete"
        />
      </div>
    </template>
  </div>
</template>

<style scoped>
.create-card { padding: 28px; margin-bottom: 28px; }
.create-card__title { font-size: 26px; margin: 0 0 20px; }
.create-card__row { margin-bottom: 16px; }
.create-card__field { margin-bottom: 16px; }
.create-card__checkbox { margin-bottom: 20px; }
.create-card__error { margin-bottom: 12px; }
</style>
