import { defineStore } from 'pinia'
import { ref, watch } from 'vue'

const THEMES = ['paper', 'dark']

export const useThemeStore = defineStore('theme', () => {
  const stored = localStorage.getItem('theme')
  const theme = ref(stored === 'dark' ? 'dark' : 'paper')

  const isDark = ref(theme.value === 'dark')

  watch(theme, (t) => {
    isDark.value = t === 'dark'
    if (t === 'dark') {
      document.documentElement.setAttribute('data-theme', 'dark')
    } else {
      document.documentElement.removeAttribute('data-theme')
    }
  }, { immediate: true })

  function setTheme(value) {
    theme.value = value
    localStorage.setItem('theme', value)
  }

  function cycle() {
    const next = THEMES[(THEMES.indexOf(theme.value) + 1) % THEMES.length]
    setTheme(next)
  }

  return { theme, isDark, cycle, setTheme }
})
