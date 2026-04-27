<script setup>
defineProps({
  questions: { type: Array, required: true },
  answers: { type: Object, required: true },
  currentIdx: { type: Number, required: true },
  totalCount: { type: Number, required: true },
  loading: { type: Boolean, default: false },
  error: { type: String, default: null },
})

const emit = defineEmits(['update:current-idx', 'submit'])

function isAnswered(q, answers) {
  const a = answers[q.id]
  return !!(a?.selectedOptionId || a?.openTextAnswer?.trim())
}
</script>

<template>
  <div class="cand-foot">
    <button
      class="btn btn--ghost"
      :disabled="currentIdx === 0"
      @click="emit('update:current-idx', currentIdx - 1)"
    >
      ← Previous
    </button>

    <div class="cand-foot__dots">
      <button
        v-for="(q, i) in questions"
        :key="q.id"
        :class="['cand-dot', { answered: isAnswered(q, answers), current: i === currentIdx }]"
        @click="emit('update:current-idx', i)"
      >{{ i + 1 }}</button>
    </div>

    <div class="cand-foot__actions">
      <p v-if="error" class="error-text">{{ error }}</p>
      <button
        v-if="currentIdx === totalCount - 1"
        class="btn btn--accent"
        :disabled="loading"
        @click="emit('submit')"
      >
        <span v-if="loading" class="spinner" />
        Submit →
      </button>
      <button
        v-else
        class="btn btn--primary"
        @click="emit('update:current-idx', currentIdx + 1)"
      >
        Next →
      </button>
    </div>
  </div>
</template>

<style scoped>
.cand-foot__actions { display: flex; gap: 8px; align-items: center; }
</style>
