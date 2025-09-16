import { useEffect, useState } from 'react'
import { api } from '../api/client'
import { Box, Button, Dialog, DialogActions, DialogContent, DialogTitle, Grid, Paper, Stack, Table, TableBody, TableCell, TableHead, TableRow, TextField, Typography } from '@mui/material'
import { useAuth } from '../auth/store'

export type Maintenance = { id?: number; equipment: { id: number }; requestedBy?: any; issueDescription: string; status: string }

export default function MaintenancePage() {
  const [rows, setRows] = useState<Maintenance[]>([])
  const [open, setOpen] = useState(false)
  const [equipmentId, setEquipmentId] = useState<number>(1)
  const [issueDescription, setIssueDescription] = useState('')
  const { user } = useAuth()

  const load = () => api.get('/api/maintenance').then(r => setRows(r.data))
  useEffect(() => { load() }, [])

  const create = async () => {
    await api.post('/api/maintenance', { equipmentId, issueDescription })
    setOpen(false); setIssueDescription(''); load()
  }

  const assign = async (id: number, technicianId: number) => { await api.post(`/api/maintenance/${id}/assign/${technicianId}`); load() }
  const resolve = async (id: number) => { await api.post(`/api/maintenance/${id}/resolve`); load() }

  return (
    <Grid container spacing={2}>
      <Grid item xs={12}>
        <Stack direction="row" justifyContent="space-between" alignItems="center">
          <Typography variant="h6">Maintenance</Typography>
          <Button variant="contained" onClick={() => setOpen(true)}>New Request</Button>
        </Stack>
      </Grid>
      <Grid item xs={12}>
        <Paper>
          <Table size="small">
            <TableHead>
              <TableRow>
                <TableCell>ID</TableCell>
                <TableCell>Equipment</TableCell>
                <TableCell>Issue</TableCell>
                <TableCell>Status</TableCell>
                <TableCell align="right">Actions</TableCell>
              </TableRow>
            </TableHead>
            <TableBody>
              {rows.map(r => (
                <TableRow key={r.id} hover>
                  <TableCell>{r.id}</TableCell>
                  <TableCell>{(r as any).equipment?.id}</TableCell>
                  <TableCell>{r.issueDescription}</TableCell>
                  <TableCell>{r.status}</TableCell>
                  <TableCell align="right">
                    <Stack direction="row" spacing={1} justifyContent="flex-end">
                      {(user?.role === 'LAB_MANAGER' || user?.role === 'ADMIN') && r.status === 'OPEN' && (
                        <Button size="small" onClick={() => assign(r.id!, 1)}>Assign #1</Button>
                      )}
                      {(user?.role === 'LAB_MANAGER' || user?.role === 'ADMIN' || user?.role === 'TECHNICIAN') && r.status !== 'RESOLVED' && (
                        <Button size="small" onClick={() => resolve(r.id!)}>Resolve</Button>
                      )}
                    </Stack>
                  </TableCell>
                </TableRow>
              ))}
            </TableBody>
          </Table>
        </Paper>
      </Grid>

      <Dialog open={open} onClose={() => setOpen(false)} fullWidth maxWidth="sm">
        <DialogTitle>New Maintenance Request</DialogTitle>
        <DialogContent>
          <Box sx={{ display: 'grid', gap: 2, mt: 1 }}>
            <TextField label="Equipment ID" type="number" value={equipmentId} onChange={e => setEquipmentId(parseInt(e.target.value, 10) || 1)} />
            <TextField label="Issue" value={issueDescription} onChange={e => setIssueDescription(e.target.value)} fullWidth />
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