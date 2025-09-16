import { create } from 'zustand'

export type UserInfo = { username: string; fullName: string; email: string; role: 'ADMIN'|'LAB_MANAGER'|'INSTRUCTOR'|'STUDENT'|'TECHNICIAN' }

type AuthState = {
  token: string | null
  user: UserInfo | null
  setAuth: (token: string, user: UserInfo) => void
  clear: () => void
}

export const useAuth = create<AuthState>((set) => ({
  token: localStorage.getItem('token'),
  user: localStorage.getItem('user') ? JSON.parse(localStorage.getItem('user') as string) : null,
  setAuth: (token, user) => {
    localStorage.setItem('token', token)
    localStorage.setItem('user', JSON.stringify(user))
    set({ token, user })
  },
  clear: () => {
    localStorage.removeItem('token')
    localStorage.removeItem('user')
    set({ token: null, user: null })
  }
}))