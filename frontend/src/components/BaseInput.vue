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
    class="base-input"
    :placeholder="placeholder"
    :required="required"
    :disabled="disabled"
    :rows="rows"
  />
  <input
    v-else
    v-model="model"
    class="base-input"
    :type="type"
    :placeholder="placeholder"
    :required="required"
    :disabled="disabled"
    :min="min"
  />
</template>

<style scoped>
.base-input {
  width: 100%;
  padding: 0.5625rem 0.75rem;
  border: 1.5px solid var(--color-border);
  border-radius: var(--radius-sm);
  font-size: 0.9375rem;
  font-family: inherit;
  color: var(--color-text);
  background: var(--color-bg-input);
  transition: border-color var(--transition), box-shadow var(--transition);
  resize: vertical;
}

.base-input:focus {
  outline: none;
  border-color: var(--color-primary);
  box-shadow: 0 0 0 3px color-mix(in srgb, var(--color-primary) 15%, transparent);
}

.base-input::placeholder {
  color: var(--color-text-muted);
}

.base-input:disabled {
  opacity: 0.5;
  cursor: not-allowed;
}
</style>
