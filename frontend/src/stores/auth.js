import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import { login as apiLogin, register as apiRegister } from '@/api/auth'

export const useAuthStore = defineStore('auth', () => {
  const token = ref(localStorage.getItem('token') || null)
  const user = ref(JSON.parse(localStorage.getItem('user') || 'null'))

  const isAuthenticated = computed(() => !!token.value)

  function setAuth(data) {
    token.value = data.token
    user.value = { userId: data.userId, companyId: data.companyId, email: data.email, role: data.role }
    localStorage.setItem('token', data.token)
    localStorage.setItem('user', JSON.stringify(user.value))
  }

  function logout() {
    token.value = null
    user.value = null
    localStorage.removeItem('token')
    localStorage.removeItem('user')
  }

  async function login(email, password) {
    const res = await apiLogin({ email, password })
    setAuth(res.data)
  }

  async function register(payload) {
    const res = await apiRegister(payload)
    setAuth(res.data)
  }

  return { token, user, isAuthenticated, login, register, logout }
})
