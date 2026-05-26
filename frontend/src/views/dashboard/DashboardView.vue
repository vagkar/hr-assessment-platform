<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useAssessmentStore } from '@/stores/assessment'
import AssessmentCard from '@/components/assessment/AssessmentCard.vue'
import BaseButton from '@/components/ui/BaseButton.vue'
import BaseCard from '@/components/ui/BaseCard.vue'
import BaseInput from '@/components/ui/BaseInput.vue'
import StatCard from '@/components/ui/StatCard.vue'
import { getDashboardStats } from '@/api/assessments'

const router = useRouter()
const assessmentStore = useAssessmentStore()

const showForm = ref(false)
const form = ref({ title: '', description: '', durationMinutes: 30, isActive: true })
const error = ref(null)
const loading = ref(false)
const stats = ref(null)

onMounted(async () => {
  await assessmentStore.fetchAll()
  const res = await getDashboardStats()
  stats.value = res.data
})

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

    <!-- Stats row -->
    <div v-if="stats" class="card stats-panel">
      <div class="stats-panel__item">
        <div class="stats-panel__label">Active assessments</div>
        <div class="stats-panel__value">{{ stats.activeAssessments }}</div>
      </div>
      <div class="stats-panel__item">
        <div class="stats-panel__label">Invites sent</div>
        <div class="stats-panel__value">{{ stats.invitesSent }}</div>
      </div>
      <div class="stats-panel__item">
        <div class="stats-panel__label">Completed</div>
        <div class="stats-panel__value">{{ stats.completed }}</div>
        <div class="stats-panel__sub">{{ stats.completionRate }}% completion</div>
      </div>
      <div class="stats-panel__item">
        <div class="stats-panel__label">Avg. score</div>
        <div class="stats-panel__value">{{ stats.avgScore != null ? `${Math.round(stats.avgScore)}%` : '—' }}</div>
      </div>
    </div>

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
.stats-panel {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  margin-bottom: 28px;
  padding: 0;
  overflow: hidden;
}
.stats-panel__item {
  padding: 20px 24px;
  border-right: 1px solid var(--rule);
}
.stats-panel__item:last-child { border-right: none; }
.stats-panel__label {
  font-size: 11px;
  font-weight: 600;
  letter-spacing: 0.08em;
  text-transform: uppercase;
  color: var(--muted);
  margin-bottom: 8px;
}
.stats-panel__value {
  font-size: 36px;
  font-weight: 700;
  line-height: 1;
  color: var(--ink);
}
.stats-panel__sub {
  font-size: 12px;
  color: var(--muted);
  margin-top: 6px;
}
@media (max-width: 640px) {
  .stats-panel { grid-template-columns: repeat(2, 1fr); }
  .stats-panel__item:nth-child(2) { border-right: none; }
  .stats-panel__item:nth-child(1),
  .stats-panel__item:nth-child(2) { border-bottom: 1px solid var(--rule); }
}
.create-card { padding: 28px; margin-bottom: 28px; }
.create-card__title { font-size: 26px; margin: 0 0 20px; }
.create-card__row { margin-bottom: 16px; }
.create-card__field { margin-bottom: 16px; }
.create-card__checkbox { margin-bottom: 20px; }
.create-card__error { margin-bottom: 12px; }
</style>
