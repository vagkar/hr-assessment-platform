<script setup>
defineProps({
  label: { type: String, required: true },
  error: { type: String, default: null },
})
</script>

<template>
  <div class="field">
    <label class="field__label">{{ label }}</label>
    <slot />
    <p v-if="error" class="error-text">{{ error }}</p>
  </div>

</template>
