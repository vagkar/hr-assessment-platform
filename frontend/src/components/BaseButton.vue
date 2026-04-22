<script setup>
defineProps({
  variant: {
    type: String,
    default: 'primary',
    validator: (v) => ['primary', 'outline', 'danger', 'ghost', 'link', 'accent'].includes(v),
  },
  loading: { type: Boolean, default: false },
  disabled: { type: Boolean, default: false },
  block: { type: Boolean, default: false },
  sm: { type: Boolean, default: false },
})
</script>

<template>
  <button
    :class="[
      'btn',
      variant === 'primary' ? 'btn--primary' :
      variant === 'danger'  ? 'btn--danger'  :
      variant === 'ghost'   ? 'btn--ghost'   :
      variant === 'link'    ? 'btn--link'    :
      variant === 'accent'  ? 'btn--accent'  :
      'btn--ghost',
      { 'btn--block': block, 'btn--sm': sm },
    ]"
    :disabled="disabled || loading"
    v-bind="$attrs"
  >
    <span v-if="loading" class="spinner" />
    <slot />
  </button>
</template>
