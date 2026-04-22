<script setup>
const result = history.state?.result ?? null

function scoreTag(score) {
  if (score >= 75) return 'tag--ok'
  if (score >= 50) return 'tag--warn'
  return 'tag--bad'
}
</script>

<template>
  <div class="done fade-in">
    <div class="done__inner">
      <div class="done__mark">✓</div>

      <span class="eyebrow">Submitted</span>

      <h1>Thank you<em v-if="false">, name</em>.</h1>

      <p style="color: var(--muted); font-size: 15px; max-width: 40ch;">
        Your answers have been recorded. The hiring team will review your submission and follow up with you.
      </p>

      <div v-if="result?.score !== null && result?.score !== undefined">
        <span :class="['tag', scoreTag(result.score)]" style="font-size: 15px; padding: 6px 14px;">
          Score: {{ result.score }}%
        </span>
      </div>

      <div v-else-if="result">
        <p style="color: var(--muted); font-size: 14px; font-family: var(--f-mono);">
          Open-text answers will be reviewed manually.
        </p>
      </div>

      <p class="mono" style="font-size: 11px; color: var(--faint); letter-spacing: 0.08em; text-transform: uppercase;">
        You may close this tab
      </p>
    </div>
  </div>
</template>
