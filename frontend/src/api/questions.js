import apiClient from './client'

export const getQuestions = (assessmentId) => apiClient.get(`/assessments/${assessmentId}/questions`)
export const createQuestion = (assessmentId, data) => apiClient.post(`/assessments/${assessmentId}/questions`, data)
export const updateQuestion = (id, data) => apiClient.put(`/questions/${id}`, data)
export const deleteQuestion = (id) => apiClient.delete(`/questions/${id}`)
