<script setup>
import { computed } from 'vue'
import CandidateTopBar from '@/components/candidate/CandidateTopBar.vue'
import CandidateProgressBar from '@/components/candidate/CandidateProgressBar.vue'
import CandidateQuestion from '@/components/candidate/CandidateQuestion.vue'
import CandidateFootNav from '@/components/candidate/CandidateFootNav.vue'

const props = defineProps({
  session: { type: Object, required: true },
  answers: { type: Object, required: true },
  currentIdx: { type: Number, required: true },
  loading: { type: Boolean, default: false },
  error: { type: String, default: null },
})

const emit = defineEmits(['update:currentIdx', 'submit', 'selectOption', 'updateAnswer'])

const totalCount = computed(() => props.session.questions.length)
const currentQuestion = computed(() => props.session.questions[props.currentIdx])
const answeredCount = computed(() =>
  props.session.questions.filter((q) => {
    const a = props.answers[q.id]
    return a?.selectedOptionId || a?.openTextAnswer?.trim()
  }).length
)

function handleOptionClick(optId) {
  emit('selectOption', currentQuestion.value.id, optId)
  if (props.currentIdx < totalCount.value - 1) {
    setTimeout(() => emit('update:currentIdx', props.currentIdx + 1), 320)
  }
}
</script>

<template>
  <CandidateTopBar
    :assessment-title="session.assessmentTitle"
    :answered-count="answeredCount"
    :total-count="totalCount"
    :started-at="session.startedAt"
    :duration-minutes="session.durationMinutes"
    @timeout="emit('submit')"
  />

  <CandidateProgressBar :answered="answeredCount" :total="totalCount" />

  <div class="cand-main">
    <CandidateQuestion
      :question="currentQuestion"
      :answers="answers"
      :current-idx="currentIdx"
      :total-count="totalCount"
      @select-option="handleOptionClick"
      @update-answer="(questionId, text) => emit('updateAnswer', questionId, text)"
    />
  </div>

  <CandidateFootNav
    :questions="session.questions"
    :answers="answers"
    :current-idx="currentIdx"
    :total-count="totalCount"
    :loading="loading"
    :error="error"
    @update:current-idx="emit('update:currentIdx', $event)"
    @submit="emit('submit')"
  />
</template>
