<script setup>
import { useRouter } from 'vue-router'
import { useAuthStore } from '@/stores/auth'
import { useThemeStore } from '@/stores/theme'

const router = useRouter()
const authStore = useAuthStore()
const themeStore = useThemeStore()

function logout() {
  authStore.logout()
  router.push('/login')
}
</script>

<template>
  <div class="layout">
    <nav class="navbar">
      <RouterLink to="/dashboard" class="navbar-brand">
        <span class="brand-icon">HR</span>
        Platform
      </RouterLink>
      <div class="navbar-actions">
        <span class="user-email">{{ authStore.user?.email }}</span>
        <button
          class="btn btn-outline icon-btn"
          :title="themeStore.isDark ? 'Switch to light' : 'Switch to dark'"
          @click="themeStore.toggle"
        >
          {{ themeStore.isDark ? '☀️' : '🌙' }}
        </button>
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
  height: 60px;
  background: var(--color-bg-card);
  border-bottom: 1px solid var(--color-border);
  position: sticky;
  top: 0;
  z-index: 100;
  backdrop-filter: blur(8px);
}

.navbar-brand {
  display: flex;
  align-items: center;
  gap: var(--space-sm);
  font-weight: 700;
  font-size: 1rem;
  color: var(--color-text);
  text-decoration: none;
  letter-spacing: -0.01em;
}

.brand-icon {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  width: 28px;
  height: 28px;
  background: var(--color-primary);
  color: #fff;
  border-radius: 6px;
  font-size: 0.75rem;
  font-weight: 700;
  letter-spacing: 0;
}

.navbar-actions {
  display: flex;
  align-items: center;
  gap: var(--space-sm);
}

.user-email {
  font-size: 0.8125rem;
  color: var(--color-text-muted);
  margin-right: var(--space-xs);
}

.icon-btn {
  padding: 0.4375rem 0.5625rem;
  font-size: 0.9375rem;
  line-height: 1;
}
</style>
