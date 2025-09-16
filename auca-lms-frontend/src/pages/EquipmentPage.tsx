import { useEffect, useState } from 'react'
import { api } from '../api/client'
import { Box, Button, Dialog, DialogActions, DialogContent, DialogTitle, Grid, MenuItem, Paper, Stack, Table, TableBody, TableCell, TableHead, TableRow, TextField, Typography } from '@mui/material'

export type Equipment = { id?: number; lab: { id: number } | number; code: string; name: string; status: string }

const STATUSES = ['AVAILABLE','IN_USE','UNDER_MAINTENANCE','BROKEN']

export default function EquipmentPage() {
  const [rows, setRows] = useState<Equipment[]>([])
  const [open, setOpen] = useState(false)
  const [form, setForm] = useState<Equipment>({ lab: 1, code: '', name: '', status: 'AVAILABLE' })

  const load = () => api.get('/api/equipment').then(r => setRows(r.data))
  useEffect(() => { load() }, [])

  const handleSave = async () => {
    const payload = { ...form, lab: typeof form.lab === 'number' ? { id: form.lab } : form.lab }
    if (form.id) await api.put(`/api/equipment/${form.id}`, payload)
    else await api.post('/api/equipment', payload)
    setOpen(false); setForm({ lab: 1, code: '', name: '', status: 'AVAILABLE' }); load()
  }

  const handleEdit = (e: Equipment) => { setForm({ ...e, lab: (e as any).lab?.id || 1 }); setOpen(true) }
  const handleDelete = async (id: number) => { await api.delete(`/api/equipment/${id}`); load() }

  return (
    <Grid container spacing={2}>
      <Grid item xs={12}>
        <Stack direction="row" justifyContent="space-between" alignItems="center">
          <Typography variant="h6">Equipment</Typography>
          <Button variant="contained" onClick={() => setOpen(true)}>Add Equipment</Button>
        </Stack>
      </Grid>
      <Grid item xs={12}>
        <Paper>
          <Table size="small">
            <TableHead>
              <TableRow>
                <TableCell>Code</TableCell>
                <TableCell>Name</TableCell>
                <TableCell>Status</TableCell>
                <TableCell align="right">Actions</TableCell>
              </TableRow>
            </TableHead>
            <TableBody>
              {rows.map(r => (
                <TableRow key={r.id} hover>
                  <TableCell>{r.code}</TableCell>
                  <TableCell>{r.name}</TableCell>
                  <TableCell>{r.status}</TableCell>
                  <TableCell align="right">
                    <Stack direction="row" spacing={1} justifyContent="flex-end">
                      <Button size="small" onClick={() => handleEdit(r)}>Edit</Button>
                      <Button size="small" color="error" onClick={() => handleDelete(r.id!)}>Delete</Button>
                    </Stack>
                  </TableCell>
                </TableRow>
              ))}
            </TableBody>
          </Table>
        </Paper>
      </Grid>

      <Dialog open={open} onClose={() => setOpen(false)} fullWidth maxWidth="sm">
        <DialogTitle>{form.id ? 'Edit Equipment' : 'Add Equipment'}</DialogTitle>
        <DialogContent>
          <Box sx={{ display: 'grid', gap: 2, mt: 1 }}>
            <TextField label="Lab ID" type="number" value={form.lab as number} onChange={e => setForm({ ...form, lab: parseInt(e.target.value, 10) || 1 })} />
            <TextField label="Code" value={form.code} onChange={e => setForm({ ...form, code: e.target.value })} fullWidth />
            <TextField label="Name" value={form.name} onChange={e => setForm({ ...form, name: e.target.value })} fullWidth />
            <TextField label="Status" select value={form.status} onChange={e => setForm({ ...form, status: e.target.value })}>
              {STATUSES.map(t => <MenuItem key={t} value={t}>{t}</MenuItem>)}
            </TextField>
          </Box>
        </DialogContent>
        <DialogActions>
          <Button onClick={() => setOpen(false)}>Cancel</Button>
          <Button onClick={handleSave} variant="contained">Save</Button>
        </DialogActions>
      </Dialog>
    </Grid>
  )
}