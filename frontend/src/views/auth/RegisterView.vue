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
      <BrandLogo class="auth-brand" />
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
        <BaseButton type="submit" :loading="loading" block class="submit-btn">
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
h1 { margin-bottom: var(--space-xs); }
</style>
