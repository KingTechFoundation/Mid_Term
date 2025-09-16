import { useEffect, useState } from 'react'
import { api } from '../api/client'
import { Box, Button, Dialog, DialogActions, DialogContent, DialogTitle, Grid, Paper, Stack, Table, TableBody, TableCell, TableHead, TableRow, TextField, Typography } from '@mui/material'
import dayjs from 'dayjs'

export type AccessLog = { id?: number; lab: { id: number }; user?: any; enteredAt: string; exitedAt?: string }

export default function AccessLogsPage() {
  const [rows, setRows] = useState<AccessLog[]>([])
  const [open, setOpen] = useState(false)
  const [labId, setLabId] = useState<number>(1)

  const load = () => api.get('/api/access').then(r => setRows(r.data))
  useEffect(() => { load() }, [])

  const enter = async () => { await api.post('/api/access/enter', { labId }); setOpen(false); load() }
  const exit = async (id: number) => { await api.post(`/api/access/${id}/exit`); load() }

  return (
    <Grid container spacing={2}>
      <Grid item xs={12}>
        <Stack direction="row" justifyContent="space-between" alignItems="center">
          <Typography variant="h6">Access Logs</Typography>
          <Button variant="contained" onClick={() => setOpen(true)}>Enter Lab</Button>
        </Stack>
      </Grid>
      <Grid item xs={12}>
        <Paper>
          <Table size="small">
            <TableHead>
              <TableRow>
                <TableCell>ID</TableCell>
                <TableCell>Lab</TableCell>
                <TableCell>User</TableCell>
                <TableCell>Entered</TableCell>
                <TableCell>Exited</TableCell>
                <TableCell align="right">Actions</TableCell>
              </TableRow>
            </TableHead>
            <TableBody>
              {rows.map(r => (
                <TableRow key={r.id} hover>
                  <TableCell>{r.id}</TableCell>
                  <TableCell>{(r as any).lab?.id}</TableCell>
                  <TableCell>{(r as any).user?.username || ''}</TableCell>
                  <TableCell>{dayjs(r.enteredAt).format('YYYY-MM-DD HH:mm')}</TableCell>
                  <TableCell>{r.exitedAt ? dayjs(r.exitedAt).format('YYYY-MM-DD HH:mm') : '-'}</TableCell>
                  <TableCell align="right">
                    {!r.exitedAt && (
                      <Button size="small" onClick={() => exit(r.id!)}>Exit</Button>
                    )}
                  </TableCell>
                </TableRow>
              ))}
            </TableBody>
          </Table>
        </Paper>
      </Grid>

      <Dialog open={open} onClose={() => setOpen(false)} fullWidth maxWidth="sm">
        <DialogTitle>Enter Lab</DialogTitle>
        <DialogContent>
          <Box sx={{ display: 'grid', gap: 2, mt: 1 }}>
            <TextField label="Lab ID" type="number" value={labId} onChange={e => setLabId(parseInt(e.target.value, 10) || 1)} />
          </Box>
        </DialogContent>
        <DialogActions>
          <Button onClick={() => setOpen(false)}>Cancel</Button>
          <Button onClick={enter} variant="contained">Enter</Button>
        </DialogActions>
      </Dialog>
    </Grid>
  )
}