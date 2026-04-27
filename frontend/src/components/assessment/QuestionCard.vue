<script setup>
defineProps({
  question: { type: Object, required: true },
})

defineEmits(['edit', 'delete'])

const TYPE_LABELS = {
  MULTIPLE_CHOICE: 'multiple choice',
  TRUE_FALSE: 'true / false',
  OPEN_TEXT: 'open text',
}
</script>

<template>
  <div class="qcard">
    <div class="qcard__n">{{ String(question.orderIndex).padStart(2, '0') }}</div>

    <div class="qcard__body">
      <span class="eyebrow">{{ TYPE_LABELS[question.type] ?? question.type }}</span>
      <div class="qcard__text">{{ question.text }}</div>

      <div v-if="question.options.length" class="qcard__opts">
        <span
          v-for="opt in question.options"
          :key="opt.id"
          :class="['opt', { 'is-correct': opt.isCorrect }]"
        >
          <span class="opt__dot" />
          {{ opt.text }}
        </span>
      </div>

      <div v-if="question.type === 'OPEN_TEXT'" class="qcard__open">
        Open text response · reviewed manually
      </div>
    </div>

    <div class="qcard__tools">
      <button class="icon-btn" title="Edit" @click="$emit('edit', question)">
        <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5" stroke-linecap="round" stroke-linejoin="round">
          <path d="M4 20h4L20 8l-4-4L4 16v4z M14 6l4 4"/>
        </svg>
      </button>
      <button class="icon-btn danger" title="Delete" @click="$emit('delete', question.id)">
        <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5" stroke-linecap="round">
          <path d="M4 7h16M9 7V5a2 2 0 012-2h2a2 2 0 012 2v2m-8 0v13a2 2 0 002 2h6a2 2 0 002-2V7"/>
        </svg>
      </button>
    </div>
  </div>
</template>

<style scoped>
.qcard__open {
  padding: 12px 14px;
  background: var(--bg-2);
  border-radius: 4px;
  font-style: italic;
  color: var(--muted);
  font-size: 13px;
}
</style>
