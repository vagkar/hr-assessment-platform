<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '@/stores/auth'
import { useAssessmentStore } from '@/stores/assessment'

const router = useRouter()
const authStore = useAuthStore()
const assessmentStore = useAssessmentStore()

const showForm = ref(false)
const form = ref({ title: '', description: '', durationMinutes: 30, isActive: true })
const error = ref(null)
const loading = ref(false)

onMounted(() => assessmentStore.fetchAll())

function logout() {
  authStore.logout()
  router.push('/login')
}

async function handleCreate() {
  error.value = null
  loading.value = true
  try {
    await assessmentStore.create(form.value)
    form.value = { title: '', description: '', durationMinutes: 30, isActive: true }
    showForm.value = false
  } catch (e) {
    error.value = e.response?.data?.message || 'Failed to create assessment'
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
  <div class="page">
    <header class="dashboard-header">
      <h1>Assessments</h1>
      <div class="header-actions">
        <button class="btn btn-primary" @click="showForm = !showForm">
          {{ showForm ? 'Cancel' : '+ New Assessment' }}
        </button>
        <button class="btn btn-outline" @click="logout">Logout</button>
      </div>
    </header>

    <div v-if="showForm" class="card" style="margin-bottom: 1.5rem">
      <h2>New Assessment</h2>
      <form @submit.prevent="handleCreate">
        <div class="form-group">
          <label>Title</label>
          <input v-model="form.title" type="text" required />
        </div>
        <div class="form-group">
          <label>Description</label>
          <textarea v-model="form.description" rows="3" />
        </div>
        <div class="form-group">
          <label>Duration (minutes)</label>
          <input v-model.number="form.durationMinutes" type="number" min="1" required />
        </div>
        <p v-if="error" class="error-text">{{ error }}</p>
        <div class="form-actions">
          <button type="submit" class="btn btn-primary" :disabled="loading">
            {{ loading ? 'Creating...' : 'Create' }}
          </button>
        </div>
      </form>
    </div>

    <div v-if="assessmentStore.assessments.length === 0" class="text-muted">
      No assessments yet.
    </div>

    <div class="assessment-list">
      <div
        v-for="assessment in assessmentStore.assessments"
        :key="assessment.id"
        class="card assessment-card"
      >
        <div class="assessment-info">
          <h2>{{ assessment.title }}</h2>
          <p class="text-muted">{{ assessment.durationMinutes }} min</p>
          <p v-if="assessment.description" class="text-muted">{{ assessment.description }}</p>
        </div>
        <div class="assessment-actions">
          <button class="btn btn-outline" @click="router.push(`/assessments/${assessment.id}`)">
            Open
          </button>
          <button class="btn btn-danger" @click="handleDelete(assessment.id)">Delete</button>
        </div>
      </div>
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

.header-actions {
  display: flex;
  gap: var(--space-sm);
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
