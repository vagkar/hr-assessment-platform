<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '@/stores/auth'
import BaseInput from '@/components/BaseInput.vue'
import BaseButton from '@/components/BaseButton.vue'
import FormGroup from '@/components/FormGroup.vue'

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
      <div class="auth-brand">
        <span class="brand-icon">HR</span>
        <span class="brand-name">Platform</span>
      </div>
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
        <BaseButton type="submit" :loading="loading" style="width: 100%; margin-top: var(--space-sm)">
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
.auth-page {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 100vh;
  padding: var(--space-md);
}

.auth-card {
  width: 100%;
  max-width: 400px;
}

.auth-brand {
  display: flex;
  align-items: center;
  gap: var(--space-sm);
  margin-bottom: var(--space-xl);
}

.brand-icon {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  width: 32px;
  height: 32px;
  background: var(--color-primary);
  color: #fff;
  border-radius: 8px;
  font-size: 0.75rem;
  font-weight: 700;
}

.brand-name {
  font-weight: 700;
  font-size: 1rem;
  color: var(--color-text);
}

h1 { margin-bottom: var(--space-xs); }

.auth-subtitle {
  color: var(--color-text-muted);
  font-size: 0.9375rem;
  margin-bottom: var(--space-xl);
}

.auth-footer {
  text-align: center;
  margin-top: var(--space-lg);
  font-size: 0.875rem;
  color: var(--color-text-muted);
}
</style>
