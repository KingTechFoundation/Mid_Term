import { useEffect, useState } from 'react'
import { Grid, Paper, Typography } from '@mui/material'
import { api } from '../api/client'

export default function ReportsPage() {
  const [summary, setSummary] = useState<any>({})
  useEffect(() => { api.get('/api/reports/summary').then(r => setSummary(r.data)).catch(() => {}) }, [])

  const Metric = ({ label, value }: { label: string; value: any }) => (
    <Paper sx={{ p: 2 }}>
      <Typography variant="overline" color="text.secondary">{label}</Typography>
      <Typography variant="h4">{value ?? '-'}</Typography>
    </Paper>
  )

  return (
    <Grid container spacing={2}>
      <Grid item xs={12}><Typography variant="h6">Reports</Typography></Grid>
      <Grid item xs={12} sm={6} md={3}><Metric label="Bookings" value={summary.bookings} /></Grid>
      <Grid item xs={12} sm={6} md={3}><Metric label="Equipment" value={summary.equipment} /></Grid>
      <Grid item xs={12} sm={6} md={3}><Metric label="Maintenance" value={summary.maintenance} /></Grid>
      <Grid item xs={12} sm={6} md={3}><Metric label="Access Logs" value={summary.accessLogs} /></Grid>
    </Grid>
  )
}