<script setup>
import { ref } from 'vue'
import { createInvite } from '@/api/assessments'
import BaseButton from '@/components/BaseButton.vue'
import BaseCard from '@/components/BaseCard.vue'
import BaseInput from '@/components/BaseInput.vue'

const props = defineProps({
  assessmentId: { type: Number, required: true },
})

const emit = defineEmits(['sent', 'cancel'])

const form = ref({ candidateName: '', candidateEmail: '' })
const loading = ref(false)
const error = ref(null)

const appUrl = import.meta.env.VITE_APP_URL

async function handleSubmit() {
  error.value = null
  loading.value = true
  try {
    const res = await createInvite(props.assessmentId, form.value)
    await navigator.clipboard.writeText(`${appUrl}/candidate/${res.data.inviteToken}`)
    form.value = { candidateName: '', candidateEmail: '' }
    emit('sent', res.data)
  } catch (e) {
    error.value = e.response?.data?.message || 'Failed to create invite'
  } finally {
    loading.value = false
  }
}
</script>

<template>
  <BaseCard class="fade-in invite-form">
    <div class="invite-form__header">
      <h2 class="display invite-form__title">Send invite</h2>
      <span class="eyebrow">Candidate receives a unique link</span>
    </div>
    <form @submit.prevent="handleSubmit">
      <div class="form-row invite-form__row">
        <div class="field">
          <label class="field__label">Candidate name</label>
          <BaseInput v-model="form.candidateName" placeholder="Jane Doe" required />
        </div>
        <div class="field">
          <label class="field__label">Email</label>
          <BaseInput type="email" v-model="form.candidateEmail" placeholder="jane@example.com" required />
        </div>
      </div>
      <p v-if="error" class="error-text invite-form__error">{{ error }}</p>
      <div class="form-actions">
        <BaseButton type="button" variant="ghost" @click="emit('cancel')">Cancel</BaseButton>
        <BaseButton type="submit" variant="primary" :loading="loading">Send invite</BaseButton>
      </div>
    </form>
  </BaseCard>
</template>

<style scoped>
.invite-form { padding: 24px; margin-bottom: 22px; }
.invite-form__header { display: flex; align-items: baseline; gap: 12px; margin-bottom: 18px; }
.invite-form__title { font-size: 24px; margin: 0; }
.invite-form__row { margin-bottom: 18px; }
.invite-form__error { margin-bottom: 12px; }

@media (max-width: 640px) {
  .invite-form__header { flex-direction: column; align-items: flex-start; gap: 4px; }
}
</style>
