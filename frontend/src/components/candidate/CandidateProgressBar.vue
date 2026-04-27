<script setup>
defineProps({
  answered: { type: Number, required: true },
  total: { type: Number, required: true },
})
</script>

<template>
  <div class="cand-progress">
    <div class="cand-progress__fill" :style="{ width: `${(answered / total) * 100}%` }" />
  </div>
</template>
