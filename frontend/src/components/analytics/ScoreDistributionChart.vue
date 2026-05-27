<script setup>
import { computed, ref, onMounted } from 'vue'
import { Bar } from 'vue-chartjs'
import { Chart as ChartJS, BarElement, CategoryScale, LinearScale, Tooltip } from 'chart.js'

ChartJS.register(BarElement, CategoryScale, LinearScale, Tooltip)

const props = defineProps({
  distribution: { type: Array, required: true },
})

const accentColor = ref('rgb(100,100,100)')

onMounted(() => {
  const canvas = document.createElement('canvas')
  canvas.width = canvas.height = 1
  const ctx = canvas.getContext('2d')
  ctx.fillStyle = getComputedStyle(document.documentElement).getPropertyValue('--accent').trim()
  ctx.fillRect(0, 0, 1, 1)
  const [r, g, b] = ctx.getImageData(0, 0, 1, 1).data
  accentColor.value = `rgb(${r},${g},${b})`
})

const chartData = computed(() => {
  const maxIdx = props.distribution.indexOf(Math.max(...props.distribution))
  return {
    labels: ['0–24', '25–49', '50–74', '75–100'],
    datasets: [{
      data: props.distribution,
      backgroundColor: props.distribution.map((_, i) =>
        i === maxIdx ? accentColor.value : 'rgba(128,128,128,0.2)'
      ),
      borderRadius: 3,
      borderWidth: 0,
    }],
  }
})

const chartOptions = {
  responsive: true,
  maintainAspectRatio: false,
  plugins: {
    legend: { display: false },
    tooltip: {
      callbacks: {
        label: (ctx) => ` ${ctx.raw} candidate${ctx.raw !== 1 ? 's' : ''}`,
      },
    },
  },
  scales: {
    x: {
      grid: { display: false },
      ticks: { font: { size: 10 }, color: 'rgba(128,128,128,0.7)' },
      border: { display: false },
    },
    y: { display: false },
  },
}
</script>

<template>
  <div class="score-distribution-chart">
    <Bar :data="chartData" :options="chartOptions" />
  </div>
</template>

<style scoped>
.score-distribution-chart {
  height: 90px;
  margin-top: 4px;
}
</style>
