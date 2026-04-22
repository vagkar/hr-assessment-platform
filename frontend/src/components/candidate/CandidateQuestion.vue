<script setup>
defineProps({
  question: { type: Object, required: true },
  answers: { type: Object, required: true },
  currentIdx: { type: Number, required: true },
  totalCount: { type: Number, required: true },
})

const emit = defineEmits(['select-option', 'update-answer'])
</script>

<template>
  <div class="cand-question fade-in" :key="question.id">
    <div class="cand-q__eyebrow">
      <span class="n">QUESTION {{ String(currentIdx + 1).padStart(2, '0') }} OF {{ String(totalCount).padStart(2, '0') }}</span>
      <span class="bar" />
      <span class="cand-q__type">{{ question.type.replace('_', ' ') }}</span>
    </div>

    <p class="cand-q__text">{{ question.text }}</p>

    <div v-if="question.type !== 'OPEN_TEXT'" class="cand-opts">
      <button
        v-for="(opt, j) in question.options"
        :key="opt.id"
        :class="['cand-opt', { 'is-selected': answers[question.id].selectedOptionId === opt.id }]"
        @click="emit('select-option', opt.id)"
      >
        <span class="cand-opt__key">{{ String.fromCharCode(65 + j) }}</span>
        <span>{{ opt.text }}</span>
        <span class="cand-opt__check" />
      </button>
    </div>

    <textarea
      v-else
      class="input"
      rows="6"
      placeholder="Write your answer here…"
      :value="answers[question.id].openTextAnswer"
      @input="emit('update-answer', question.id, $event.target.value)"
    />
  </div>
</template>

<style scoped>
.cand-q__type {
  font-family: var(--f-mono);
  font-size: 10px;
  color: var(--muted);
  letter-spacing: 0.1em;
}
</style>
