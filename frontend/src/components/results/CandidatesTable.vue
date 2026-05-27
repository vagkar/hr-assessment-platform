<script setup>
import { computed } from 'vue'
import ResultRow from './ResultRow.vue'

const props = defineProps({
  results: { type: Array, required: true },
})

defineEmits(['view'])

const sortedResults = computed(() =>
  [...props.results].sort((a, b) => {
    if (b.score === null) return -1
    if (a.score === null) return 1
    return b.score - a.score
  })
)
</script>

<template>
  <div>
    <div class="section">
      <h2>Candidates</h2>
      <span class="section__eyebrow">
        <span class="num">{{ String(results.length).padStart(2, '0') }} total · sorted by score</span>
      </span>
    </div>

    <div class="card candidates-table">
      <div class="candidates-table__header">
        <span>Candidate</span>
        <span>Status</span>
        <span>Time</span>
        <span>Score</span>
      </div>
      <ResultRow
        v-for="r in sortedResults"
        :key="r.sessionId"
        :result="r"
        @view="(id) => $emit('view', id)"
      />
    </div>
  </div>
</template>

<style scoped>
.candidates-table { overflow: hidden; }
.candidates-table__header {
  display: grid;
  grid-template-columns: 1fr auto auto auto;
  gap: 16px;
  padding: 10px 20px;
  border-bottom: 1px solid var(--rule-soft);
  font-family: var(--f-mono);
  font-size: 10.5px;
  font-weight: 500;
  letter-spacing: 0.06em;
  text-transform: uppercase;
  color: var(--muted);
  background: var(--surface);
}

@media (max-width: 640px) {
  .candidates-table__header { display: none; }
}
</style>
