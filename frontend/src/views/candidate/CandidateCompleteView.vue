<script setup>
import BrandLogo from '@/components/BrandLogo.vue'

const result = history.state?.result ?? null

function scoreColor(score) {
  if (score >= 70) return 'score-high'
  if (score >= 40) return 'score-mid'
  return 'score-low'
}
</script>

<template>
  <div class="complete-page">
    <div class="card complete-card">
      <BrandLogo class="complete-brand" />

      <div class="checkmark">✓</div>
      <h1>Assessment Complete</h1>
      <p class="complete-subtitle text-muted">
        Thank you. Your answers have been submitted.
      </p>

      <div v-if="result?.score !== null && result?.score !== undefined" class="score-section">
        <p class="score-label">Your Score</p>
        <p :class="['score-value', scoreColor(result.score)]">
          {{ result.score }}<span class="score-unit">%</span>
        </p>
        <p class="score-message text-muted">
          <span v-if="result.score >= 70">Great work! You passed the assessment.</span>
          <span v-else-if="result.score >= 40">Good effort. Results have been sent to the recruiter.</span>
          <span v-else>Results have been sent to the recruiter for review.</span>
        </p>
      </div>

      <div v-else-if="result" class="score-section">
        <p class="review-icon">📝</p>
        <p class="text-muted">Your open-ended answers will be reviewed by the recruiter.</p>
      </div>
    </div>
  </div>
</template>

<style scoped>
.complete-page {
  min-height: 100vh;
  background: var(--color-bg);
  display: flex;
  align-items: center;
  justify-content: center;
  padding: var(--space-md);
}

.complete-card {
  width: 100%;
  max-width: 440px;
  text-align: center;
}

.checkmark {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 56px;
  height: 56px;
  background: color-mix(in srgb, var(--color-success) 15%, transparent);
  color: var(--color-success);
  border-radius: 50%;
  font-size: 1.5rem;
  font-weight: 700;
  margin: 0 auto var(--space-lg);
}

.score-section {
  padding-top: var(--space-xl);
  border-top: 1px solid var(--color-border);
}

.score-label {
  font-size: 0.8125rem;
  font-weight: 700;
  text-transform: uppercase;
  letter-spacing: 0.06em;
  color: var(--color-text-muted);
  margin-bottom: var(--space-sm);
}

.score-value {
  font-size: 4rem;
  font-weight: 800;
  line-height: 1;
  letter-spacing: -0.03em;
  margin-bottom: var(--space-sm);
}

.score-unit { font-size: 2rem; font-weight: 600; }

.score-high { color: var(--color-success); }
.score-mid  { color: var(--color-warning); }
.score-low  { color: var(--color-danger); }

.complete-brand { margin-bottom: var(--space-xl); }
.complete-subtitle { margin-top: var(--space-xs); margin-bottom: var(--space-xl); }
.score-message { font-size: 0.9375rem; }
.review-icon { font-size: 2rem; margin-bottom: var(--space-sm); }
</style>
