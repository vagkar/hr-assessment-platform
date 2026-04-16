import apiClient from './client'

export const getAssessments = () => apiClient.get('/assessments')
export const createAssessment = (data) => apiClient.post('/assessments', data)
export const updateAssessment = (id, data) => apiClient.put(`/assessments/${id}`, data)
export const deleteAssessment = (id) => apiClient.delete(`/assessments/${id}`)
export const createInvite = (assessmentId, data) => apiClient.post(`/assessments/${assessmentId}/invite`, data)
