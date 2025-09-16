import { useEffect, useState } from 'react'
import { api } from '../api/client'
import { Box, Button, Dialog, DialogActions, DialogContent, DialogTitle, Grid, MenuItem, Paper, Stack, Table, TableBody, TableCell, TableHead, TableRow, TextField, Typography } from '@mui/material'
import dayjs from 'dayjs'
import { useAuth } from '../auth/store'

export type Booking = { id?: number; lab: { id: number, name?: string }; requestedBy?: any; startTime: string; endTime: string; status: string }

export default function BookingsPage() {
  const [rows, setRows] = useState<Booking[]>([])
  const [open, setOpen] = useState(false)
  const [labId, setLabId] = useState<number>(1)
  const [start, setStart] = useState<string>('')
  const [end, setEnd] = useState<string>('')
  const { user } = useAuth()

  const load = () => api.get('/api/bookings').then(r => setRows(r.data))
  useEffect(() => { load() }, [])

  const create = async () => {
    await api.post('/api/bookings', { labId, startTime: new Date(start).toISOString(), endTime: new Date(end).toISOString() })
    setOpen(false); setStart(''); setEnd(''); load()
  }

  const approve = async (id: number) => { await api.post(`/api/bookings/${id}/approve`); load() }
  const reject = async (id: number) => { await api.post(`/api/bookings/${id}/reject`); load() }

  return (
    <Grid container spacing={2}>
      <Grid item xs={12}>
        <Stack direction="row" justifyContent="space-between" alignItems="center">
          <Typography variant="h6">Bookings</Typography>
          {(user?.role === 'INSTRUCTOR' || user?.role === 'STUDENT') && (
            <Button variant="contained" onClick={() => setOpen(true)}>Request Booking</Button>
          )}
        </Stack>
      </Grid>
      <Grid item xs={12}>
        <Paper>
          <Table size="small">
            <TableHead>
              <TableRow>
                <TableCell>ID</TableCell>
                <TableCell>Lab</TableCell>
                <TableCell>Requested By</TableCell>
                <TableCell>Start</TableCell>
                <TableCell>End</TableCell>
                <TableCell>Status</TableCell>
                <TableCell align="right">Actions</TableCell>
              </TableRow>
            </TableHead>
            <TableBody>
              {rows.map(r => (
                <TableRow key={r.id} hover>
                  <TableCell>{r.id}</TableCell>
                  <TableCell>{(r as any).lab?.name || r.lab?.id}</TableCell>
                  <TableCell>{(r as any).requestedBy?.username || ''}</TableCell>
                  <TableCell>{dayjs(r.startTime).format('YYYY-MM-DD HH:mm')}</TableCell>
                  <TableCell>{dayjs(r.endTime).format('YYYY-MM-DD HH:mm')}</TableCell>
                  <TableCell>{r.status}</TableCell>
                  <TableCell align="right">
                    {(user?.role === 'LAB_MANAGER' || user?.role === 'ADMIN') && r.status === 'PENDING' && (
                      <Stack direction="row" spacing={1} justifyContent="flex-end">
                        <Button size="small" onClick={() => approve(r.id!)}>Approve</Button>
                        <Button size="small" color="error" onClick={() => reject(r.id!)}>Reject</Button>
                      </Stack>
                    )}
                  </TableCell>
                </TableRow>
              ))}
            </TableBody>
          </Table>
        </Paper>
      </Grid>

      <Dialog open={open} onClose={() => setOpen(false)} fullWidth maxWidth="sm">
        <DialogTitle>Request Booking</DialogTitle>
        <DialogContent>
          <Box sx={{ display: 'grid', gap: 2, mt: 1 }}>
            <TextField label="Lab ID" type="number" value={labId} onChange={e => setLabId(parseInt(e.target.value, 10) || 1)} />
            <TextField label="Start" type="datetime-local" value={start} onChange={e => setStart(e.target.value)} />
            <TextField label="End" type="datetime-local" value={end} onChange={e => setEnd(e.target.value)} />
          </Box>
        </DialogContent>
        <DialogActions>
          <Button onClick={() => setOpen(false)}>Cancel</Button>
          <Button onClick={create} variant="contained">Submit</Button>
        </DialogActions>
      </Dialog>
    </Grid>
  )
}