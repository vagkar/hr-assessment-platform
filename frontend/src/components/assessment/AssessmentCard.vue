<script setup>
import { useRouter } from 'vue-router'

defineProps({
  assessment: { type: Object, required: true },
  index: { type: Number, default: 0 },
})

defineEmits(['edit', 'delete'])

const router = useRouter()
</script>

<template>
  <div class="arow" @click="router.push(`/assessments/${assessment.id}`)">
    <div class="arow__idx">{{ String(index + 1).padStart(2, '0') }}</div>

    <div class="arow__main">
      <div class="arow__title">{{ assessment.title }}</div>
      <div class="arow__meta">
        <span :class="['tag', 'tag--dot', assessment.isActive ? 'tag--ok' : 'tag--warn']">
          {{ assessment.isActive ? 'active' : 'inactive' }}
        </span>
        <span class="dot" />
        <span>{{ assessment.durationMinutes }} min</span>
      </div>
      <div v-if="assessment.description" class="arow__meta arow__meta--desc">
        {{ assessment.description }}
      </div>
    </div>

    <div class="arow__actions" @click.stop>
      <button class="btn btn--ghost btn--sm" @click="$emit('edit', assessment)">Edit</button>
      <button class="btn btn--danger btn--sm" @click="$emit('delete', assessment.id)">Delete</button>
    </div>
  </div>
</template>

<style scoped>
.dot { width: 3px; height: 3px; border-radius: 50%; background: var(--faint); }
.arow__meta--desc { margin-top: 2px; }
</style>
