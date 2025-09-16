import { useEffect, useState } from 'react'
import { Grid, Paper, Typography } from '@mui/material'
import { api } from '../api/client'
import { PieChart, Pie, Cell, ResponsiveContainer, Tooltip, Legend } from 'recharts'

export default function DashboardHome() {
  const [summary, setSummary] = useState<{bookings:number; equipment:number; maintenance:number; accessLogs:number} | null>(null)
  useEffect(() => {
    api.get('/api/reports/summary').then(r => setSummary(r.data)).catch(() => {})
  }, [])

  const data = summary ? [
    { name: 'Bookings', value: summary.bookings },
    { name: 'Equipment', value: summary.equipment },
    { name: 'Maintenance', value: summary.maintenance },
    { name: 'Access Logs', value: summary.accessLogs }
  ] : []
  const colors = ['#0057b7', '#ffd200', '#00b894', '#d63031']

  return (
    <Grid container spacing={2}>
      <Grid item xs={12} md={6}>
        <Paper sx={{ p: 2, height: 360 }}>
          <Typography variant="h6" gutterBottom>System Overview</Typography>
          <ResponsiveContainer width="100%" height={280}>
            <PieChart>
              <Pie data={data} dataKey="value" nameKey="name" cx="50%" cy="50%" outerRadius={100} label>
                {data.map((entry, index) => (
                  <Cell key={`cell-${index}`} fill={colors[index % colors.length]} />
                ))}
              </Pie>
              <Tooltip />
              <Legend />
            </PieChart>
          </ResponsiveContainer>
        </Paper>
      </Grid>
      <Grid item xs={12} md={6}>
        <Paper sx={{ p: 2, height: 360 }}>
          <Typography variant="h6" gutterBottom>Welcome</Typography>
          <Typography color="text.secondary">Use the navigation to manage labs, equipment, bookings, maintenance, users, and view reports.</Typography>
        </Paper>
      </Grid>
    </Grid>
  )
}