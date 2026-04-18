<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '@/stores/auth'
import BaseInput from '@/components/BaseInput.vue'
import BaseButton from '@/components/BaseButton.vue'
import FormGroup from '@/components/FormGroup.vue'
import BrandLogo from '@/components/BrandLogo.vue'

const router = useRouter()
const authStore = useAuthStore()

const email = ref('')
const password = ref('')
const error = ref(null)
const loading = ref(false)

async function handleSubmit() {
  error.value = null
  loading.value = true
  try {
    await authStore.login(email.value, password.value)
    router.push('/dashboard')
  } catch (e) {
    error.value = e.response?.data?.message || 'Login failed'
  } finally {
    loading.value = false
  }
}
</script>

<template>
  <div class="auth-page">
    <div class="auth-card card">
      <BrandLogo class="auth-brand" />
      <h1>Welcome back</h1>
      <p class="auth-subtitle">Sign in to your account</p>

      <form @submit.prevent="handleSubmit">
        <FormGroup label="Email">
          <BaseInput v-model="email" type="email" placeholder="you@company.com" required />
        </FormGroup>
        <FormGroup label="Password">
          <BaseInput v-model="password" type="password" placeholder="••••••••" required />
        </FormGroup>
        <p v-if="error" class="error-text">{{ error }}</p>
        <BaseButton type="submit" :loading="loading" block class="submit-btn">
          Sign in
        </BaseButton>
      </form>

      <p class="auth-footer">
        Don't have an account? <RouterLink to="/register">Sign up</RouterLink>
      </p>
    </div>
  </div>
</template>

<style scoped>
h1 { margin-bottom: var(--space-xs); }
</style>
