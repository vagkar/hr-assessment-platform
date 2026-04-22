<script setup>
import { scoreTag } from '@/utils/score'

const result = history.state?.result ?? null
</script>

<template>
  <div class="done fade-in">
    <div class="done__inner">
      <div class="done__mark">✓</div>

      <span class="eyebrow">Submitted</span>

      <h1>Thank you<em v-if="false">, name</em>.</h1>

      <p class="done__desc">
        Your answers have been recorded. The hiring team will review your submission and follow up with you.
      </p>

      <div v-if="result?.score !== null && result?.score !== undefined">
        <span :class="['tag', 'done__score', scoreTag(result.score)]">
          Score: {{ result.score }}%
        </span>
      </div>

      <div v-else-if="result">
        <p class="done__review">
          Open-text answers will be reviewed manually.
        </p>
      </div>

      <p class="mono done__close">
        You may close this tab
      </p>
    </div>
  </div>
</template>

<style scoped>
.done__desc { color: var(--muted); font-size: 15px; max-width: 40ch; }
.done__score { font-size: 15px; padding: 6px 14px; }
.done__review { color: var(--muted); font-size: 14px; font-family: var(--f-mono); }
.done__close { font-size: 11px; color: var(--faint); letter-spacing: 0.08em; text-transform: uppercase; }
</style>
