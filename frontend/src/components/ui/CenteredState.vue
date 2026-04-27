<script setup>
defineProps({
  icon: { type: String, default: null },
  title: { type: String, required: true },
  message: { type: String, default: null },
  variant: { type: String, default: 'default' }, // 'default' | 'error' | 'loading'
})
</script>

<template>
  <div class="centered-state">
    <div class="centered-state__inner">
      <div v-if="variant === 'loading'" class="centered-state__loading">
        {{ title }}
      </div>
      <template v-else>
        <div v-if="icon" :class="['centered-state__icon', variant === 'error' ? 'is-error' : '']">
          {{ icon }}
        </div>
        <h2 class="display centered-state__title">{{ title }}</h2>
        <p v-if="message" class="centered-state__msg">{{ message }}</p>
        <slot />
      </template>
    </div>
  </div>
</template>

<style scoped>
.centered-state {
  display: grid;
  place-items: center;
  min-height: 100vh;
  padding: 40px;
}
.centered-state__inner {
  text-align: center;
  max-width: 400px;
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 8px;
}
.centered-state__loading {
  font-family: var(--f-mono);
  font-size: 12px;
  color: var(--muted);
  letter-spacing: 0.1em;
}
.centered-state__icon {
  font-family: var(--f-display);
  font-size: 40px;
  color: var(--muted);
  margin-bottom: 4px;
}
.centered-state__icon.is-error { color: var(--bad); }
.centered-state__title { font-size: 28px; margin-bottom: 8px; }
.centered-state__msg { color: var(--muted); }
</style>
