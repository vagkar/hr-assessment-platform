<template>
  <div class="brand">
    <span class="brand-icon">HR</span>
    <span class="brand-name">Platform</span>
  </div>
</template>

<style scoped>
.brand {
  display: inline-flex;
  align-items: center;
  gap: 0.5rem;
}

.brand-icon {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  width: 28px;
  height: 28px;
  background: var(--color-primary);
  color: #fff;
  border-radius: 6px;
  font-size: 0.75rem;
  font-weight: 700;
}

.brand-name {
  font-size: 0.9375rem;
  font-weight: 700;
  color: var(--color-text);
}
</style>
