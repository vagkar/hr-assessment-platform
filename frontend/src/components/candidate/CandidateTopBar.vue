<script setup>
import BrandLogo from '@/components/layout/BrandLogo.vue'
import ThemeToggle from '@/components/layout/ThemeToggle.vue'
import CandidateSessionTimer from '@/components/candidate/CandidateSessionTimer.vue'

defineProps({
  assessmentTitle: { type: String, required: true },
  answeredCount: { type: Number, required: true },
  totalCount: { type: Number, required: true },
  startedAt: { type: String, required: true },
  durationMinutes: { type: Number, required: true },
})

const emit = defineEmits(['timeout'])
</script>

<template>
  <div class="cand-top">
    <div class="cand-top__brand">
      <BrandLogo to="/" />
      <span class="cand-top__sep">·</span>
      <span class="cand-top__asmtitle">{{ assessmentTitle }}</span>
    </div>
    <div class="cand-top__meta">
      <span>{{ answeredCount }} / {{ totalCount }} ANSWERED</span>
      <CandidateSessionTimer
        :started-at="startedAt"
        :duration-minutes="durationMinutes"
        @timeout="emit('timeout')"
      />
      <ThemeToggle />
    </div>
  </div>
</template>

<style scoped>
.cand-top__sep { color: var(--faint); margin-left: 8px; }
.cand-top__asmtitle {
  font-family: var(--f-mono);
  font-size: 11px;
  color: var(--muted);
  letter-spacing: 0.08em;
  text-transform: uppercase;
}
</style>
