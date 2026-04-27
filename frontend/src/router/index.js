import { createRouter, createWebHistory } from 'vue-router'
import { useAuthStore } from '@/stores/auth'

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: '/',
      redirect: '/dashboard',
    },
    {
      path: '/login',
      component: () => import('@/views/auth/LoginView.vue'),
      meta: { public: true },
    },
    {
      path: '/register',
      component: () => import('@/views/auth/RegisterView.vue'),
      meta: { public: true },
    },
    {
      path: '/candidate/:token',
      name: 'candidate-session',
      component: () => import('@/views/candidate/CandidateSessionView.vue'),
      meta: { public: true },
    },
    {
      path: '/candidate/:token/complete',
      name: 'candidate-complete',
      component: () => import('@/views/candidate/CandidateCompleteView.vue'),
      meta: { public: true },
    },
    {
      path: '/',
      component: () => import('@/components/layout/AppLayout.vue'),
      children: [
        {
          path: 'dashboard',
          component: () => import('@/views/dashboard/DashboardView.vue'),
        },
        {
          path: 'assessments/:id',
          component: () => import('@/views/assessment/AssessmentView.vue'),
        },
        {
          path: 'assessments/:id/results',
          component: () => import('@/views/results/ResultsView.vue'),
        },
      ],
    },
  ],
})

router.beforeEach((to) => {
  const authStore = useAuthStore()
  if (!to.meta.public && !authStore.isAuthenticated) {
    return '/login'
  }
})

export default router
