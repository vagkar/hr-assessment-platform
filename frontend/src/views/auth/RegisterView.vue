<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '@/stores/auth'
import BrandLogo from '@/components/BrandLogo.vue'
import BaseButton from '@/components/BaseButton.vue'
import BaseInput from '@/components/BaseInput.vue'

const router = useRouter()
const authStore = useAuthStore()

const form = ref({
  companyName: '',
  companyEmail: '',
  adminName: '',
  adminEmail: '',
  password: '',
})
const error = ref(null)
const loading = ref(false)

async function handleSubmit() {
  error.value = null
  loading.value = true
  try {
    await authStore.register(form.value)
    router.push('/dashboard')
  } catch (e) {
    error.value = e.response?.data?.message || 'Registration failed'
  } finally {
    loading.value = false
  }
}
</script>

<template>
  <div class="login">
    <div class="login__art">
      <div class="login__artgrid" />
      <BrandLogo to="/login" class="login__brand" />
      <div class="login__quote">
        Build assessments that reveal real <em>talent</em>.
      </div>
      <div class="login__foot">
        <span>Assessment Platform</span>
        <span>Set up in under a minute</span>
      </div>
    </div>

    <div class="login__form fade-in">
      <h1>Create account</h1>
      <p class="sub">Set up your company workspace.</p>

      <form @submit.prevent="handleSubmit" class="login__fields">
        <div class="field">
          <label class="field__label">Company name</label>
          <BaseInput v-model="form.companyName" placeholder="Acme Corp" required />
        </div>
        <div class="field">
          <label class="field__label">Company email</label>
          <BaseInput type="email" v-model="form.companyEmail" placeholder="hr@acme.com" required />
        </div>
        <div class="field">
          <label class="field__label">Your name</label>
          <BaseInput v-model="form.adminName" placeholder="Jane Doe" required />
        </div>
        <div class="field">
          <label class="field__label">Your email</label>
          <BaseInput type="email" v-model="form.adminEmail" placeholder="jane@acme.com" required />
        </div>
        <div class="field">
          <label class="field__label">Password</label>
          <BaseInput type="password" v-model="form.password" placeholder="Min. 8 characters" required />
        </div>
        <p v-if="error" class="error-text">{{ error }}</p>
        <BaseButton type="submit" variant="primary" block :loading="loading" class="btn--lg">
          {{ loading ? 'Creating workspace…' : 'Create workspace →' }}
        </BaseButton>
      </form>

      <p class="login__footer">
        Already have an account? <RouterLink to="/login">Sign in →</RouterLink>
      </p>
    </div>
  </div>
</template>
