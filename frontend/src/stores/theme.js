import { defineStore } from 'pinia'
import { ref, computed, watch } from 'vue'

export const useThemeStore = defineStore('theme', () => {
  const theme = ref(localStorage.getItem('theme') ?? 'system')
  const prefersDark = ref(window.matchMedia('(prefers-color-scheme: dark)').matches)

  window.matchMedia('(prefers-color-scheme: dark)').addEventListener('change', (e) => {
    prefersDark.value = e.matches
  })

  const isDark = computed(() =>
    theme.value === 'system' ? prefersDark.value : theme.value === 'dark'
  )

  watch(isDark, (dark) => {
    document.documentElement.setAttribute('data-theme', dark ? 'dark' : 'light')
  }, { immediate: true })

  function setTheme(value) {
    theme.value = value
    localStorage.setItem('theme', value)
  }

  function toggle() {
    setTheme(isDark.value ? 'light' : 'dark')
  }

  return { theme, isDark, toggle, setTheme }
})
