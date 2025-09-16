import { useState } from 'react'
import { Box, Button, Card, CardContent, TextField, Typography, Alert } from '@mui/material'
import { api } from '../api/client'
import { useAuth } from '../auth/store'
import { useNavigate, useLocation } from 'react-router-dom'

export default function LoginPage() {
  const [username, setUsername] = useState('')
  const [password, setPassword] = useState('')
  const [error, setError] = useState<string | null>(null)
  const setAuth = useAuth(s => s.setAuth)
  const navigate = useNavigate()
  const location = useLocation() as any

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault()
    setError(null)
    try {
      const res = await api.post('/api/auth/login', { username, password })
      const { token, user } = res.data
      setAuth(token, user)
      const redirectTo = location.state?.from?.pathname || '/dashboard'
      navigate(redirectTo, { replace: true })
    } catch (err: any) {
      setError(err?.response?.data?.error || 'Login failed')
    }
  }

  return (
    <Box sx={{ minHeight: '100vh', display: 'flex', alignItems: 'center', justifyContent: 'center', bgcolor: 'background.default' }}>
      <Card sx={{ width: 380 }}>
        <CardContent>
          <Typography variant="h5" gutterBottom>Sign in</Typography>
          {error && <Alert severity="error" sx={{ mb: 2 }}>{error}</Alert>}
          <Box component="form" onSubmit={handleSubmit} sx={{ display: 'grid', gap: 2 }}>
            <TextField label="Username" value={username} onChange={e => setUsername(e.target.value)} required autoFocus />
            <TextField label="Password" type="password" value={password} onChange={e => setPassword(e.target.value)} required />
            <Button type="submit" variant="contained" size="large">Login</Button>
          </Box>
          <Typography variant="body2" sx={{ mt: 2, color: 'text.secondary' }}>
            Demo users: admin/admin123, manager/manager123, instructor/instructor123, student/student123
          </Typography>
        </CardContent>
      </Card>
    </Box>
  )
}