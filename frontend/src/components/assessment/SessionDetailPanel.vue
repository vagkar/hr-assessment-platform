<script setup>
import { scoreTag } from '@/utils/score'
import BaseButton from '@/components/BaseButton.vue'
import BaseCard from '@/components/BaseCard.vue'

defineProps({
  session: { type: Object, required: true },
})

defineEmits(['close'])

function answerClass(a) {
  if (a.isCorrect === true) return 'is-correct'
  if (a.isCorrect === false && (a.selectedOptionId || a.openTextAnswer)) return 'is-incorrect'
  return 'is-open'
}
</script>

<template>
  <BaseCard class="fade-in session-detail">
    <div class="session-detail__header">
      <div>
        <div class="display session-detail__name">{{ session.candidateName }}</div>
        <div class="mono session-detail__email">{{ session.candidateEmail }}</div>
      </div>
      <div class="session-detail__meta">
        <span v-if="session.score !== null" :class="['tag', 'session-detail__score', scoreTag(session.score)]">
          {{ session.score }}%
        </span>
        <BaseButton variant="link" @click="$emit('close')">✕</BaseButton>
      </div>
    </div>

    <div class="session-detail__answers">
      <div
        v-for="(a, index) in session.answers"
        :key="a.questionId"
        :class="['answer-item', answerClass(a)]"
      >
        <div class="mono answer-item__meta">
          Q{{ String(index + 1).padStart(2, '0') }} · {{ a.questionType }}
          <span v-if="a.isCorrect !== null"> · {{ a.isCorrect ? 'CORRECT' : 'INCORRECT' }}</span>
          <span v-else> · OPEN · TO REVIEW</span>
        </div>
        <div class="answer-item__text">{{ a.questionText }}</div>
        <div v-if="a.selectedOptionText" class="answer-item__selected">
          → {{ a.selectedOptionText }}
        </div>
        <div v-if="a.openTextAnswer" class="answer-item__open">
          {{ a.openTextAnswer }}
        </div>
      </div>
    </div>
  </BaseCard>
</template>

<style scoped>
.session-detail { padding: 24px; margin-bottom: 24px; }
.session-detail__header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: 16px;
}
.session-detail__name { font-size: 22px; letter-spacing: -0.01em; }
.session-detail__email { font-size: 11px; color: var(--muted); margin-top: 2px; }
.session-detail__meta { display: flex; gap: 10px; align-items: center; }
.session-detail__score { font-size: 14px; padding: 4px 10px; }
.session-detail__answers { display: flex; flex-direction: column; gap: 8px; }

.answer-item {
  padding: 10px 14px;
  border-left: 3px solid var(--accent);
  background: var(--bg-2);
  border-radius: 0 4px 4px 0;
}
.answer-item.is-correct { border-left-color: var(--ok); }
.answer-item.is-incorrect { border-left-color: var(--bad); }
.answer-item__meta { font-size: 10px; color: var(--muted); letter-spacing: 0.1em; margin-bottom: 4px; }
.answer-item__text { font-size: 13px; color: var(--ink-2); }
.answer-item__selected { font-size: 12px; color: var(--muted); margin-top: 4px; }
.answer-item__open { font-size: 12px; color: var(--muted); margin-top: 4px; font-style: italic; }
</style>
