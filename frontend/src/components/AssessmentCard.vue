<script setup>
import { useRouter } from 'vue-router'
import BaseButton from '@/components/BaseButton.vue'

defineProps({
  assessment: { type: Object, required: true },
})

defineEmits(['edit', 'delete'])

const router = useRouter()
</script>

<template>
  <div class="assessment-card">
    <div class="assessment-info">
      <div class="assessment-title-row">
        <h2>{{ assessment.title }}</h2>
        <span :class="['status-badge', assessment.isActive ? 'status-active' : 'status-inactive']">
          {{ assessment.isActive ? 'Active' : 'Inactive' }}
        </span>
      </div>
      <p v-if="assessment.description" class="description text-muted">{{ assessment.description }}</p>
      <p class="meta text-muted">{{ assessment.durationMinutes }} min</p>
    </div>
    <div class="assessment-actions">
      <BaseButton variant="outline" @click="router.push(`/assessments/${assessment.id}`)">Open</BaseButton>
      <BaseButton variant="outline" @click="$emit('edit', assessment)">Edit</BaseButton>
      <BaseButton variant="danger" @click="$emit('delete', assessment.id)">Delete</BaseButton>
    </div>
  </div>
</template>

<style scoped>
.assessment-card {
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: var(--space-md);
}

.assessment-info { min-width: 0; flex: 1; }

.assessment-title-row {
  display: flex;
  align-items: center;
  gap: var(--space-sm);
  margin-bottom: var(--space-xs);
}

.status-badge {
  font-size: 0.6875rem;
  font-weight: 600;
  padding: 0.15rem 0.5rem;
  border-radius: 999px;
  text-transform: uppercase;
  letter-spacing: 0.04em;
  flex-shrink: 0;
}

.status-active   { background: color-mix(in srgb, var(--color-success) 15%, transparent); color: var(--color-success); }
.status-inactive { background: var(--color-bg-hover); color: var(--color-text-muted); }

.description {
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
  margin-bottom: var(--space-xs);
}

.meta { font-size: 0.8125rem; }

.assessment-actions {
  display: flex;
  gap: var(--space-sm);
  flex-shrink: 0;
}

@media (max-width: 640px) {
  .assessment-card {
    flex-direction: column;
    align-items: flex-start;
  }

  .assessment-actions {
    width: 100%;
    justify-content: flex-end;
    padding-top: var(--space-sm);
    border-top: 1px solid var(--color-border);
  }
}
</style>
