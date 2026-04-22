<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '@/stores/auth'
import BrandLogo from '@/components/BrandLogo.vue'

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
          <input class="input" v-model="form.companyName" placeholder="Acme Corp" required />
        </div>
        <div class="field">
          <label class="field__label">Company email</label>
          <input class="input" type="email" v-model="form.companyEmail" placeholder="hr@acme.com" required />
        </div>
        <div class="field">
          <label class="field__label">Your name</label>
          <input class="input" v-model="form.adminName" placeholder="Jane Doe" required />
        </div>
        <div class="field">
          <label class="field__label">Your email</label>
          <input class="input" type="email" v-model="form.adminEmail" placeholder="jane@acme.com" required />
        </div>
        <div class="field">
          <label class="field__label">Password</label>
          <input class="input" type="password" v-model="form.password" placeholder="Min. 8 characters" required />
        </div>
        <p v-if="error" class="error-text">{{ error }}</p>
        <button type="submit" class="btn btn--primary btn--lg btn--block" :disabled="loading">
          <span v-if="loading" class="spinner" />
          {{ loading ? 'Creating workspace…' : 'Create workspace →' }}
        </button>
      </form>

      <p class="login__footer">
        Already have an account? <RouterLink to="/login">Sign in →</RouterLink>
      </p>
    </div>
  </div>
</template>
