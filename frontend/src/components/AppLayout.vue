<script setup>
import { useRouter } from 'vue-router'
import { useAuthStore } from '@/stores/auth'

const router = useRouter()
const authStore = useAuthStore()

function logout() {
  authStore.logout()
  router.push('/login')
}
</script>

<template>
  <div class="layout">
    <nav class="navbar">
      <RouterLink to="/dashboard" class="navbar-brand">HR Platform</RouterLink>
      <div class="navbar-actions">
        <span class="text-muted">{{ authStore.user?.email }}</span>
        <button class="btn btn-outline" @click="logout">Logout</button>
      </div>
    </nav>
    <main class="page">
      <RouterView />
    </main>
  </div>
</template>

<style scoped>
.navbar {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 0 var(--space-xl);
  height: 56px;
  background: var(--color-bg-card);
  border-bottom: 1px solid var(--color-border);
}

.navbar-brand {
  font-weight: 700;
  font-size: 1.1rem;
  color: var(--color-primary);
  text-decoration: none;
}

.navbar-actions {
  display: flex;
  align-items: center;
  gap: var(--space-md);
}
</style>
