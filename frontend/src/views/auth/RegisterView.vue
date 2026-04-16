<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '@/stores/auth'
import BaseInput from '@/components/BaseInput.vue'
import BaseButton from '@/components/BaseButton.vue'
import FormGroup from '@/components/FormGroup.vue'

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
    <div class="auth-card card">
      <div class="auth-brand">
        <span class="brand-icon">HR</span>
        <span class="brand-name">Platform</span>
      </div>
      <h1>Create account</h1>
      <p class="auth-subtitle">Set up your company workspace</p>

      <form @submit.prevent="handleSubmit">
        <p class="section-label">Company</p>
        <FormGroup label="Company Name">
          <BaseInput v-model="form.companyName" placeholder="Acme Corp" required />
        </FormGroup>
        <FormGroup label="Company Email">
          <BaseInput v-model="form.companyEmail" type="email" placeholder="hr@acme.com" required />
        </FormGroup>

        <p class="section-label">Admin Account</p>
        <FormGroup label="Your Name">
          <BaseInput v-model="form.adminName" placeholder="Jane Doe" required />
        </FormGroup>
        <FormGroup label="Your Email">
          <BaseInput v-model="form.adminEmail" type="email" placeholder="jane@acme.com" required />
        </FormGroup>
        <FormGroup label="Password">
          <BaseInput v-model="form.password" type="password" placeholder="Min. 8 characters" required />
        </FormGroup>

        <p v-if="error" class="error-text">{{ error }}</p>
        <BaseButton type="submit" :loading="loading" style="width: 100%; margin-top: var(--space-sm)">
          Create account
        </BaseButton>
      </form>

      <p class="auth-footer">
        Already have an account? <RouterLink to="/login">Sign in</RouterLink>
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
  max-width: 420px;
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

.section-label {
  font-size: 0.75rem;
  font-weight: 700;
  text-transform: uppercase;
  letter-spacing: 0.06em;
  color: var(--color-text-muted);
  margin-bottom: var(--space-sm);
  margin-top: var(--space-lg);
}

.section-label:first-of-type { margin-top: 0; }

.auth-footer {
  text-align: center;
  margin-top: var(--space-lg);
  font-size: 0.875rem;
  color: var(--color-text-muted);
}
</style>
