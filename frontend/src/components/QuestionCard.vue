<script setup>
import BaseButton from '@/components/BaseButton.vue'

const TYPE_LABELS = {
  MULTIPLE_CHOICE: 'Multiple Choice',
  TRUE_FALSE: 'True / False',
  OPEN_TEXT: 'Open Text',
}

defineProps({
  question: { type: Object, required: true },
})

defineEmits(['edit', 'delete'])
</script>

<template>
  <div class="question-card">
    <div class="question-info">
      <span class="type-badge">{{ TYPE_LABELS[question.type] ?? question.type }}</span>
      <p class="question-text">{{ question.text }}</p>
      <ul v-if="question.options.length" class="options-list">
        <li v-for="opt in question.options" :key="opt.id" :class="{ correct: opt.isCorrect }">
          <span class="option-dot" />
          {{ opt.text }}
        </li>
      </ul>
    </div>
    <div class="question-actions">
      <BaseButton variant="outline" @click="$emit('edit', question)">Edit</BaseButton>
      <BaseButton variant="danger" @click="$emit('delete', question.id)">Delete</BaseButton>
    </div>
  </div>
</template>

<style scoped>
.question-card {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  gap: var(--space-md);
}

.question-info { min-width: 0; flex: 1; }

.type-badge {
  display: inline-block;
  font-size: 0.6875rem;
  font-weight: 700;
  letter-spacing: 0.04em;
  text-transform: uppercase;
  color: var(--color-primary);
  background: color-mix(in srgb, var(--color-primary) 10%, transparent);
  padding: 0.15rem 0.5rem;
  border-radius: 999px;
  margin-bottom: var(--space-sm);
}

.question-text {
  font-size: 0.9375rem;
  line-height: 1.5;
  margin-bottom: var(--space-sm);
}

.options-list {
  display: flex;
  flex-direction: column;
  gap: 0.25rem;
  list-style: none;
  padding: 0;
}

.options-list li {
  display: flex;
  align-items: center;
  gap: var(--space-xs);
  font-size: 0.875rem;
  color: var(--color-text-muted);
}

.options-list li.correct {
  color: var(--color-success);
  font-weight: 500;
}

.option-dot {
  width: 6px;
  height: 6px;
  border-radius: 50%;
  background: currentColor;
  flex-shrink: 0;
}

.question-actions {
  display: flex;
  gap: var(--space-sm);
  flex-shrink: 0;
}

@media (max-width: 640px) {
  .question-card {
    flex-direction: column;
  }

  .question-actions {
    width: 100%;
    justify-content: flex-end;
    padding-top: var(--space-sm);
    border-top: 1px solid var(--color-border);
  }
}
</style>
