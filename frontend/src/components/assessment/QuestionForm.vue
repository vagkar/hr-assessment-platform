<script setup>
import { ref, watch } from 'vue'
import { createQuestion, updateQuestion } from '@/api/questions'

const props = defineProps({
  assessmentId: { type: Number, required: true },
  editingQuestion: { type: Object, default: null },
  nextOrderIndex: { type: Number, default: 1 },
})
const emit = defineEmits(['saved', 'cancel'])

const form = ref({ text: '', type: 'MULTIPLE_CHOICE', orderIndex: props.nextOrderIndex, options: [] })
const newOption = ref({ text: '', isCorrect: false })
const loading = ref(false)
const error = ref(null)

watch(() => props.editingQuestion, (q) => {
  if (q) {
    form.value = {
      text: q.text,
      type: q.type,
      orderIndex: q.orderIndex,
      options: q.options.map((o) => ({ text: o.text, isCorrect: o.isCorrect })),
    }
  } else {
    form.value = { text: '', type: 'MULTIPLE_CHOICE', orderIndex: props.nextOrderIndex, options: [] }
  }
  newOption.value = { text: '', isCorrect: false }
  error.value = null
}, { immediate: true })

function addOption() {
  if (!newOption.value.text) return
  form.value.options.push({ ...newOption.value })
  newOption.value = { text: '', isCorrect: false }
}

function removeOption(index) {
  form.value.options.splice(index, 1)
}

function toggleCorrect(index) {
  form.value.options = form.value.options.map((x, j) =>
    j === index ? { ...x, isCorrect: !x.isCorrect } : x
  )
}

async function handleSubmit() {
  error.value = null
  loading.value = true
  try {
    if (props.editingQuestion) {
      await updateQuestion(props.editingQuestion.id, form.value)
    } else {
      await createQuestion(props.assessmentId, form.value)
    }
    emit('saved')
  } catch (e) {
    error.value = e.response?.data?.message || 'Failed to save question'
  } finally {
    loading.value = false
  }
}
</script>

<template>
  <div class="qform card fade-in">
    <div class="qform__header">
      <h2 class="display qform__title">
        {{ editingQuestion ? 'Edit question' : 'New question' }}
      </h2>
      <div class="qform__type-switcher">
        <button
          v-for="t in ['MULTIPLE_CHOICE', 'TRUE_FALSE', 'OPEN_TEXT']"
          :key="t"
          type="button"
          :class="['btn', 'btn--sm', form.type === t ? 'btn--primary' : 'btn--inactive']"
          @click="form.type = t"
        >{{ t.replace('_', ' ').toLowerCase() }}</button>
      </div>
    </div>

    <form @submit.prevent="handleSubmit">
      <div class="field qform__field">
        <label class="field__label">Question text</label>
        <textarea class="input" v-model="form.text" rows="3" placeholder="Write your question…" required />
      </div>

      <div v-if="form.type !== 'OPEN_TEXT'" class="field qform__field">
        <label class="field__label">Answers · check to mark correct</label>
        <div class="qform__options">
          <div v-for="(opt, i) in form.options" :key="i" class="qform__opt-row">
            <button
              type="button"
              :class="['icon-btn', { 'is-correct': opt.isCorrect }]"
              @click="toggleCorrect(i)"
            >✓</button>
            <input class="input" v-model="opt.text" :placeholder="`Option ${i + 1}`" />
            <button type="button" class="icon-btn danger" @click="removeOption(i)">✕</button>
          </div>
          <div class="qform__new-opt">
            <input class="input" v-model="newOption.text" placeholder="New option…" @keydown.enter.prevent="addOption" />
            <label class="qform__correct-label">
              <input type="checkbox" v-model="newOption.isCorrect" />
              Correct
            </label>
            <button type="button" class="btn btn--ghost btn--sm" @click="addOption">Add</button>
          </div>
        </div>
      </div>

      <p v-if="error" class="error-text qform__error">{{ error }}</p>
      <div class="form-actions">
        <button type="button" class="btn btn--ghost" @click="emit('cancel')">Cancel</button>
        <button type="submit" class="btn btn--primary" :disabled="loading">
          <span v-if="loading" class="spinner" />
          {{ editingQuestion ? 'Save changes' : 'Save question' }}
        </button>
      </div>
    </form>
  </div>
</template>

<style scoped>
.qform { padding: 24px; margin-bottom: 14px; }
.qform__header {
  display: flex;
  justify-content: space-between;
  align-items: baseline;
  margin-bottom: 16px;
}
.qform__title { font-size: 24px; margin: 0; }
.qform__type-switcher {
  display: flex;
  gap: 4px;
  padding: 3px;
  background: var(--bg-2);
  border-radius: 4px;
}
.btn--inactive { color: var(--muted); }
.qform__field { margin-bottom: 16px; }
.qform__options { display: flex; flex-direction: column; gap: 8px; }
.qform__opt-row { display: flex; gap: 10px; align-items: center; }
.icon-btn.is-correct { background: var(--ok); color: #fff; border: 1px solid var(--ok); }
.icon-btn:not(.is-correct) { border: 1px solid var(--rule); }
.qform__new-opt { display: flex; gap: 8px; align-items: center; margin-top: 4px; }
.qform__correct-label {
  display: flex;
  align-items: center;
  gap: 6px;
  white-space: nowrap;
  font-family: var(--f-mono);
  font-size: 11px;
  color: var(--muted);
}
.qform__error { margin-bottom: 12px; }
</style>
