import axios from 'axios'
import { useAuth } from '../auth/store'

export const api = axios.create({
  baseURL: import.meta.env.VITE_API_URL || 'http://localhost:8080'
})

api.interceptors.request.use((config) => {
  const token = useAuth.getState().token
  if (token) {
    config.headers = config.headers || {}
    config.headers['Authorization'] = `Bearer ${token}`
  }
  return config
})