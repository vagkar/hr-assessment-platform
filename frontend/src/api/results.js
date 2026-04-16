import apiClient from './client'

export const getResults = (assessmentId) => apiClient.get(`/assessments/${assessmentId}/results`)
export const getSessionDetail = (sessionId) => apiClient.get(`/results/${sessionId}`)
