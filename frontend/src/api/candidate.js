import apiClient from './client'

export const getSession = (token) => apiClient.get(`/candidate/session/${token}`)
export const startSession = (token) => apiClient.post(`/candidate/session/${token}/start`)
export const submitAnswers = (token, data) => apiClient.post(`/candidate/session/${token}/submit`, data)
