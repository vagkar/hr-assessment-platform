<script setup>
defineProps({
  to: { type: String, default: '/dashboard' },
})
</script>

<template>
  <RouterLink :to="to" class="brand">
    <div class="mark">E</div>
    <span class="name">Evalify</span>
  </RouterLink>
</template>

<style scoped>
.brand {
  display: inline-flex;
  align-items: center;
  gap: 10px;
  text-decoration: none;
  color: inherit;
}
.mark {
  width: 22px; height: 22px;
  background: var(--ink); color: var(--bg);
  display: grid; place-items: center;
  font-family: var(--f-display);
  font-size: 16px; line-height: 1;
  border-radius: 3px;
  flex-shrink: 0;
}
.name {
  font-family: var(--f-display);
  font-size: 22px;
  letter-spacing: -0.01em;
}
</style>
