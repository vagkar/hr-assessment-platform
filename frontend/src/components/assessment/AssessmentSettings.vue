<script setup>
import { ref, computed, watch } from 'vue'
import { updateAssessment } from '@/api/assessments'
import { useAssessmentStore } from '@/stores/assessment'

const props = defineProps({
  assessmentId: { type: Number, required: true },
  assessment: { type: Object, required: true },
})
const emit = defineEmits(['saved'])

const assessmentStore = useAssessmentStore()

const form = ref(null)
const original = ref(null)
const saved = ref(false)
const error = ref(null)
const loading = ref(false)

const changed = computed(() => {
  if (!form.value || !original.value) return false
  return JSON.stringify(form.value) !== JSON.stringify(original.value)
})

watch(() => props.assessment, (a) => {
  if (!a) return
  const snap = {
    title: a.title,
    description: a.description || '',
    durationMinutes: a.durationMinutes,
    isActive: a.isActive,
  }
  form.value = { ...snap }
  original.value = { ...snap }
}, { immediate: true })

async function handleSave() {
  error.value = null
  loading.value = true
  try {
    const res = await updateAssessment(props.assessmentId, form.value)
    assessmentStore.assessments = assessmentStore.assessments.map((a) =>
      a.id === props.assessmentId ? res.data : a
    )
    original.value = { ...form.value }
    saved.value = true
    setTimeout(() => (saved.value = false), 2000)
    emit('saved', res.data)
  } catch (e) {
    error.value = e.response?.data?.message || 'Failed to save'
  } finally {
    loading.value = false
  }
}
</script>

<template>
  <div class="settings-card card">
    <div class="eyebrow settings-card__label">Settings</div>
    <form v-if="form" @submit.prevent="handleSave">
      <div class="field settings-card__field">
        <label class="field__label">Title</label>
        <input class="input" v-model="form.title" required />
      </div>
      <div class="field settings-card__field">
        <label class="field__label">Description</label>
        <textarea class="input" v-model="form.description" rows="2" />
      </div>
      <div class="field settings-card__field">
        <label class="field__label">Duration (minutes)</label>
        <input class="input" type="number" min="1" v-model.number="form.durationMinutes" required />
      </div>
      <div class="checkbox-row settings-card__checkbox">
        <input type="checkbox" id="settings-isActive" v-model="form.isActive" />
        <label for="settings-isActive">Active</label>
      </div>
      <p v-if="error" class="error-text settings-card__error">{{ error }}</p>
      <button type="submit" class="btn btn--primary btn--sm btn--block" :disabled="loading || !changed">
        <span v-if="loading" class="spinner" />
        {{ saved ? '✓ Saved' : 'Save changes' }}
      </button>
    </form>
  </div>
</template>

<style scoped>
.settings-card { padding: 22px; }
.settings-card__label { margin-bottom: 16px; }
.settings-card__field { margin-bottom: 12px; }
.settings-card__checkbox { margin-bottom: 18px; }
.settings-card__error { margin-bottom: 10px; }
</style>
