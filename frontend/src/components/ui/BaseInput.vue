<script setup>
const model = defineModel()

defineProps({
  type: { type: String, default: 'text' },
  placeholder: { type: String, default: '' },
  required: { type: Boolean, default: false },
  disabled: { type: Boolean, default: false },
  min: { type: [String, Number], default: undefined },
  rows: { type: Number, default: undefined },
})
</script>

<template>
  <textarea
    v-if="rows"
    v-model="model"
    class="input"
    :placeholder="placeholder"
    :required="required"
    :disabled="disabled"
    :rows="rows"
  />
  <input
    v-else
    v-model="model"
    class="input"
    :type="type"
    :placeholder="placeholder"
    :required="required"
    :disabled="disabled"
    :min="min"
  />
</template>
