<script setup>
defineProps({
  label: { type: String, required: true },
  value: { type: [String, Number], required: true },
  sub: { type: String, default: null },
})
</script>

<template>
  <div class="stat-card">
    <div class="stat-card__label">{{ label }}</div>
    <div class="stat-card__value">{{ value }}</div>
    <div v-if="sub" class="stat-card__sub">{{ sub }}</div>
  </div>
</template>

<style scoped>
.stat-card {
  padding: 20px 24px;
}
.stat-card__label {
  font-size: 11px;
  font-weight: 600;
  letter-spacing: 0.08em;
  text-transform: uppercase;
  color: var(--muted);
  margin-bottom: 8px;
}
.stat-card__value {
  font-size: 36px;
  font-weight: 700;
  line-height: 1;
  color: var(--ink);
}
.stat-card__sub {
  font-size: 12px;
  color: var(--muted);
  margin-top: 6px;
}
</style>
