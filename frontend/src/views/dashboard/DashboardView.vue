<script setup>
import { ref, onMounted } from 'vue'
import { useAssessmentStore } from '@/stores/assessment'
import AssessmentCard from '@/components/AssessmentCard.vue'

const assessmentStore = useAssessmentStore()

const showForm = ref(false)
const editingId = ref(null)
const form = ref({ title: '', description: '', durationMinutes: 30, isActive: true })
const error = ref(null)
const loading = ref(false)

onMounted(() => assessmentStore.fetchAll())

function openCreate() {
  editingId.value = null
  form.value = { title: '', description: '', durationMinutes: 30, isActive: true }
  showForm.value = true
}

function openEdit(assessment) {
  editingId.value = assessment.id
  form.value = {
    title: assessment.title,
    description: assessment.description || '',
    durationMinutes: assessment.durationMinutes,
    isActive: assessment.isActive,
  }
  showForm.value = true
}

function cancelForm() {
  showForm.value = false
  editingId.value = null
  error.value = null
}

async function handleSubmit() {
  error.value = null
  loading.value = true
  try {
    if (editingId.value) {
      await assessmentStore.update(editingId.value, form.value)
    } else {
      await assessmentStore.create(form.value)
    }
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
        <button v-if="!showForm" class="btn btn--primary" @click="openCreate">
          + New assessment
        </button>
        <button v-else class="btn btn--ghost" @click="cancelForm">Cancel</button>
      </div>
    </header>

    <!-- New / Edit form -->
    <div v-if="showForm" class="card fade-in" style="padding: 28px; margin-bottom: 28px;">
      <div style="display: flex; align-items: baseline; gap: 12px; margin-bottom: 20px;">
        <h2 class="display" style="font-size: 26px; margin: 0;">
          {{ editingId ? 'Edit assessment' : 'New assessment' }}
        </h2>
      </div>
      <form @submit.prevent="handleSubmit">
        <div class="form-row" style="margin-bottom: 16px;">
          <div class="field">
            <label class="field__label">Title</label>
            <input class="input" v-model="form.title" placeholder="e.g. Senior Frontend Engineer" required />
          </div>
          <div class="field">
            <label class="field__label">Duration (minutes)</label>
            <input class="input" type="number" v-model="form.durationMinutes" min="1" required />
          </div>
        </div>
        <div class="field" style="margin-bottom: 20px;">
          <label class="field__label">Description</label>
          <input class="input" v-model="form.description" placeholder="Short summary candidates will see before starting" />
        </div>
        <p v-if="error" class="error-text" style="margin-bottom: 12px;">{{ error }}</p>
        <div style="display: flex; justify-content: flex-end; gap: 8px;">
          <button type="button" class="btn btn--ghost" @click="cancelForm">Cancel</button>
          <button type="submit" class="btn btn--primary" :disabled="loading">
            <span v-if="loading" class="spinner" />
            {{ editingId ? 'Save changes' : 'Create →' }}
          </button>
        </div>
      </form>
    </div>

    <!-- Empty state -->
    <div v-if="assessmentStore.assessments.length === 0 && !showForm" class="empty-state">
      <p class="empty-title">No assessments yet</p>
      <p style="color: var(--muted); margin-bottom: 20px;">Create your first assessment to start evaluating candidates.</p>
      <button class="btn btn--primary" @click="openCreate">+ New assessment</button>
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
          @edit="openEdit"
          @delete="handleDelete"
        />
      </div>
    </template>
  </div>
</template>
