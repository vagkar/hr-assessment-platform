<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useAssessmentStore } from '@/stores/assessment'
import BaseButton from '@/components/BaseButton.vue'
import BaseCard from '@/components/BaseCard.vue'
import FormGroup from '@/components/FormGroup.vue'

const router = useRouter()
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
  <div>
    <header class="dashboard-header">
      <h1>Assessments</h1>
      <BaseButton @click="showForm ? cancelForm() : openCreate()">
        {{ showForm ? 'Cancel' : '+ New Assessment' }}
      </BaseButton>
    </header>

    <BaseCard v-if="showForm" style="margin-bottom: 1.5rem">
      <h2>{{ editingId ? 'Edit Assessment' : 'New Assessment' }}</h2>
      <form @submit.prevent="handleSubmit">
        <FormGroup label="Title">
          <input v-model="form.title" type="text" required />
        </FormGroup>
        <FormGroup label="Description">
          <textarea v-model="form.description" rows="3" />
        </FormGroup>
        <FormGroup label="Duration (minutes)">
          <input v-model.number="form.durationMinutes" type="number" min="1" required />
        </FormGroup>
        <p v-if="error" class="error-text">{{ error }}</p>
        <div class="form-actions">
          <BaseButton type="submit" :loading="loading">
            {{ loading ? 'Saving...' : editingId ? 'Save Changes' : 'Create' }}
          </BaseButton>
        </div>
      </form>
    </BaseCard>

    <p v-if="assessmentStore.assessments.length === 0" class="text-muted">No assessments yet.</p>

    <div class="assessment-list">
      <BaseCard
        v-for="assessment in assessmentStore.assessments"
        :key="assessment.id"
        class="assessment-card"
      >
        <div class="assessment-info">
          <h2>{{ assessment.title }}</h2>
          <p class="text-muted">{{ assessment.durationMinutes }} min</p>
          <p v-if="assessment.description" class="text-muted">{{ assessment.description }}</p>
        </div>
        <div class="assessment-actions">
          <BaseButton variant="outline" @click="router.push(`/assessments/${assessment.id}`)">
            Open
          </BaseButton>
          <BaseButton variant="outline" @click="openEdit(assessment)">Edit</BaseButton>
          <BaseButton variant="danger" @click="handleDelete(assessment.id)">Delete</BaseButton>
        </div>
      </BaseCard>
    </div>
  </div>
</template>

<style scoped>
.dashboard-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: var(--space-xl);
}

.assessment-list {
  display: flex;
  flex-direction: column;
  gap: var(--space-md);
}

.assessment-card {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.assessment-actions {
  display: flex;
  gap: var(--space-sm);
  flex-shrink: 0;
}
</style>
