<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '@/stores/auth'

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
  <div class="auth-page">
    <div class="card auth-card">
      <h1>Register</h1>
      <form @submit.prevent="handleSubmit">
        <div class="form-group">
          <label>Company Name</label>
          <input v-model="form.companyName" type="text" required />
        </div>
        <div class="form-group">
          <label>Company Email</label>
          <input v-model="form.companyEmail" type="email" required />
        </div>
        <div class="form-group">
          <label>Your Name</label>
          <input v-model="form.adminName" type="text" required />
        </div>
        <div class="form-group">
          <label>Your Email</label>
          <input v-model="form.adminEmail" type="email" required />
        </div>
        <div class="form-group">
          <label>Password</label>
          <input v-model="form.password" type="password" required minlength="8" />
        </div>
        <p v-if="error" class="error-text">{{ error }}</p>
        <div class="form-actions">
          <button type="submit" class="btn btn-primary" :disabled="loading" style="width: 100%">
            {{ loading ? 'Registering...' : 'Register' }}
          </button>
        </div>
      </form>
      <p class="text-muted" style="text-align: center; margin-top: 1rem">
        Already have an account? <RouterLink to="/login">Login</RouterLink>
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
}

.auth-card {
  width: 100%;
  max-width: 400px;
}

h1 {
  margin-bottom: var(--space-lg);
}
</style>
