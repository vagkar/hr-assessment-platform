<script setup>
import { useThemeStore } from '@/stores/theme'

const themeStore = useThemeStore()
</script>

<template>
  <button
    class="btn btn--ghost btn--sm theme-toggle"
    :title="`Theme: ${themeStore.theme} → click to cycle`"
    @click="themeStore.cycle"
  >
    <!-- paper: circle outline -->
    <svg v-if="themeStore.theme === 'paper'" width="15" height="15" viewBox="0 0 15 15" fill="none" aria-hidden="true">
      <circle cx="7.5" cy="7.5" r="5.5" stroke="currentColor" stroke-width="1.5"/>
    </svg>
    <!-- light: sun -->
    <svg v-else-if="themeStore.theme === 'light'" width="15" height="15" viewBox="0 0 15 15" fill="none" aria-hidden="true">
      <circle cx="7.5" cy="7.5" r="2.5" fill="currentColor"/>
      <line x1="7.5" y1="1"    x2="7.5" y2="3"    stroke="currentColor" stroke-width="1.5" stroke-linecap="round"/>
      <line x1="7.5" y1="12"   x2="7.5" y2="14"   stroke="currentColor" stroke-width="1.5" stroke-linecap="round"/>
      <line x1="1"   y1="7.5"  x2="3"   y2="7.5"  stroke="currentColor" stroke-width="1.5" stroke-linecap="round"/>
      <line x1="12"  y1="7.5"  x2="14"  y2="7.5"  stroke="currentColor" stroke-width="1.5" stroke-linecap="round"/>
      <line x1="3.2" y1="3.2"  x2="4.6" y2="4.6"  stroke="currentColor" stroke-width="1.5" stroke-linecap="round"/>
      <line x1="10.4" y1="10.4" x2="11.8" y2="11.8" stroke="currentColor" stroke-width="1.5" stroke-linecap="round"/>
      <line x1="11.8" y1="3.2" x2="10.4" y2="4.6"  stroke="currentColor" stroke-width="1.5" stroke-linecap="round"/>
      <line x1="4.6" y1="10.4" x2="3.2"  y2="11.8" stroke="currentColor" stroke-width="1.5" stroke-linecap="round"/>
    </svg>
    <!-- dark: crescent moon -->
    <svg v-else width="15" height="15" viewBox="0 0 15 15" fill="none" aria-hidden="true">
      <path d="M12.5 9.5A5.5 5.5 0 0 1 5.5 2.5a5.5 5.5 0 1 0 7 7z" fill="currentColor"/>
    </svg>
  </button>
</template>

<style scoped>
.theme-toggle {
  display: grid;
  place-items: center;
  width: 30px;
  padding: 0;
}
</style>
