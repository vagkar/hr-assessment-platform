<script setup>
import { ref } from 'vue'
import { useRoute } from 'vue-router'

const route = useRoute()
const result = ref(history.state?.result || null)
</script>

<template>
  <div class="page">
    <div class="card complete-card">
      <h1>Assessment Completed</h1>
      <div v-if="result">
        <p class="text-muted">Thank you for completing the assessment.</p>
        <div v-if="result.score !== null" class="score">
          <span class="score-label">Your Score</span>
          <span class="score-value">{{ result.score }}%</span>
        </div>
        <p v-else class="text-muted" style="margin-top: 1rem">
          Your answers have been submitted for review.
        </p>
      </div>
      <p v-else class="text-muted">Your answers have been submitted.</p>
    </div>
  </div>
</template>

<style scoped>
.complete-card {
  max-width: 480px;
  margin: 4rem auto;
  text-align: center;
}

h1 {
  margin-bottom: var(--space-md);
}

.score {
  display: flex;
  flex-direction: column;
  align-items: center;
  margin-top: var(--space-xl);
}

.score-label {
  font-size: 0.875rem;
  color: var(--color-text-muted);
  text-transform: uppercase;
  letter-spacing: 0.05em;
}

.score-value {
  font-size: 3rem;
  font-weight: 700;
  color: var(--color-primary);
}
</style>
