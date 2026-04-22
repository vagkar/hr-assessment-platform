<script setup>
import { useRouter } from 'vue-router'
import { useAuthStore } from '@/stores/auth'
import BrandLogo from '@/components/BrandLogo.vue'
import ThemeToggle from '@/components/ThemeToggle.vue'

const router = useRouter()
const authStore = useAuthStore()
function logout() {
  authStore.logout()
  router.push('/login')
}


</script>

<template>
  <div class="app">
    <header class="topbar">
      <BrandLogo class="topbar__brand" />

      <div class="topbar__nav" />

      <div class="topbar__right">
        <span class="user-meta">{{ authStore.user?.email }}</span>
        <ThemeToggle />
        <button class="btn btn--ghost btn--sm" @click="logout">Sign out</button>
      </div>
    </header>

    <main class="page">
      <RouterView />
    </main>
  </div>
</template>

<style scoped>
.user-meta {
  font-family: var(--f-mono);
  font-size: 11px;
  color: var(--muted);
  letter-spacing: 0.04em;
}

@media (max-width: 640px) {
  .user-meta { display: none; }
}

</style>
