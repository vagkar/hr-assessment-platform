import { defineStore } from 'pinia'
import { ref } from 'vue'
import {
  getAssessments,
  createAssessment,
  updateAssessment,
  deleteAssessment,
  createInvite,
} from '@/api/assessments'

export const useAssessmentStore = defineStore('assessment', () => {
  const assessments = ref([])

  async function fetchAll() {
    const res = await getAssessments()
    assessments.value = res.data
  }

  async function create(data) {
    const res = await createAssessment(data)
    assessments.value.push(res.data)
    return res.data
  }

  async function update(id, data) {
    const res = await updateAssessment(id, data)
    const index = assessments.value.findIndex((a) => a.id === id)
    if (index !== -1) assessments.value[index] = res.data
    return res.data
  }

  async function remove(id) {
    await deleteAssessment(id)
    assessments.value = assessments.value.filter((a) => a.id !== id)
  }

  async function invite(assessmentId, data) {
    const res = await createInvite(assessmentId, data)
    return res.data
  }

  return { assessments, fetchAll, create, update, remove, invite }
})
