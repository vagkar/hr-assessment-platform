<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '@/stores/auth'
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
  <div class="login">
    <div class="login__art">
      <div class="login__artgrid" />
      <BrandLogo to="/login" class="login__brand" />
      <div class="login__quote">
        Hire for what they <em>can do</em> — not what fits on a résumé.
      </div>
      <div class="login__foot">
        <span>Assessment Platform</span>
        <span>A calmer way to hire</span>
      </div>
    </div>

    <div class="login__form fade-in">
      <h1>Welcome back</h1>
      <p class="sub">Sign in to your workspace to manage assessments and review candidates.</p>

      <form @submit.prevent="handleSubmit" class="login__fields">
        <div class="field">
          <label class="field__label">Work email</label>
          <input class="input" type="email" v-model="email" placeholder="you@company.com" required />
        </div>
        <div class="field">
          <label class="field__label">Password</label>
          <input class="input" type="password" v-model="password" placeholder="••••••••" required />
        </div>
        <p v-if="error" class="error-text" style="margin-top: 4px;">{{ error }}</p>
        <button type="submit" class="btn btn--primary btn--lg btn--block" :disabled="loading" style="margin-top: 8px;">
          <span v-if="loading" class="spinner" />
          {{ loading ? 'Signing in…' : 'Sign in →' }}
        </button>
      </form>

      <p class="login__footer">
        New to Evalify? <RouterLink to="/register">Create a workspace →</RouterLink>
      </p>
    </div>
  </div>
</template>
