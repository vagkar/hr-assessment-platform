<script setup>
import { ref, onMounted } from 'vue'
import { useAssessmentStore } from '@/stores/assessment'
import BaseButton from '@/components/BaseButton.vue'
import BaseCard from '@/components/BaseCard.vue'
import BaseInput from '@/components/BaseInput.vue'
import FormGroup from '@/components/FormGroup.vue'
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
  <div>
    <header class="dashboard-header">
      <div>
        <h1>Assessments</h1>
        <p class="text-muted">Manage your assessments and track candidate results.</p>
      </div>
      <BaseButton @click="showForm ? cancelForm() : openCreate()">
        {{ showForm ? 'Cancel' : '+ New Assessment' }}
      </BaseButton>
    </header>

    <BaseCard v-if="showForm" class="form-card">
      <h2 class="form-title">{{ editingId ? 'Edit Assessment' : 'New Assessment' }}</h2>
      <form @submit.prevent="handleSubmit">
        <FormGroup label="Title">
          <BaseInput v-model="form.title" placeholder="e.g. Frontend Developer Assessment" required />
        </FormGroup>
        <FormGroup label="Description">
          <BaseInput v-model="form.description" :rows="2" placeholder="Optional description..." />
        </FormGroup>
        <FormGroup label="Duration (minutes)">
          <BaseInput v-model="form.durationMinutes" type="number" min="1" class="duration-input" required />
        </FormGroup>
        <p v-if="error" class="error-text">{{ error }}</p>
        <div class="form-actions">
          <BaseButton variant="outline" type="button" @click="cancelForm">Cancel</BaseButton>
          <BaseButton type="submit" :loading="loading">
            {{ editingId ? 'Save Changes' : 'Create' }}
          </BaseButton>
        </div>
      </form>
    </BaseCard>

    <div v-if="assessmentStore.assessments.length === 0 && !showForm" class="empty-state">
      <div class="empty-icon">🗂️</div>
      <p class="empty-title">No assessments yet</p>
      <p class="text-muted">Create your first assessment to start evaluating candidates.</p>
      <BaseButton class="empty-action" @click="openCreate">+ New Assessment</BaseButton>
    </div>

    <div v-else class="assessment-list">
      <BaseCard v-for="assessment in assessmentStore.assessments" :key="assessment.id">
        <AssessmentCard
          :assessment="assessment"
          @edit="openEdit"
          @delete="handleDelete"
        />
      </BaseCard>
    </div>
  </div>
</template>

<style scoped>
.dashboard-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: var(--space-xl);
  gap: var(--space-md);
}

.dashboard-header h1 { margin-bottom: var(--space-xs); }

.assessment-list {
  display: flex;
  flex-direction: column;
  gap: var(--space-sm);
}

.duration-input { max-width: 160px; }
</style>
