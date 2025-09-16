import { useEffect, useState } from 'react'
import { api } from '../api/client'
import { Box, Button, Dialog, DialogActions, DialogContent, DialogTitle, Grid, MenuItem, Paper, Stack, Table, TableBody, TableCell, TableHead, TableRow, TextField, Typography } from '@mui/material'

export type Lab = { id?: number; name: string; location: string; capacity: number; type: string }

const LAB_TYPES = ['MAIN_COMPUTER_LAB','EXT_104','EXT_108','EXT_204','EXT_209','EXT_310','ENGLISH_LAB']

export default function LabsPage() {
  const [labs, setLabs] = useState<Lab[]>([])
  const [open, setOpen] = useState(false)
  const [form, setForm] = useState<Lab>({ name: '', location: '', capacity: 0, type: 'MAIN_COMPUTER_LAB' })

  const load = () => api.get('/api/labs').then(r => setLabs(r.data))
  useEffect(() => { load() }, [])

  const handleSave = async () => {
    if (form.id) await api.put(`/api/labs/${form.id}`, form)
    else await api.post('/api/labs', form)
    setOpen(false); setForm({ name: '', location: '', capacity: 0, type: 'MAIN_COMPUTER_LAB' }); load()
  }

  const handleEdit = (lab: Lab) => { setForm(lab); setOpen(true) }
  const handleDelete = async (id: number) => { await api.delete(`/api/labs/${id}`); load() }

  return (
    <Grid container spacing={2}>
      <Grid item xs={12}>
        <Stack direction="row" justifyContent="space-between" alignItems="center">
          <Typography variant="h6">Labs</Typography>
          <Button variant="contained" onClick={() => setOpen(true)}>Add Lab</Button>
        </Stack>
      </Grid>
      <Grid item xs={12}>
        <Paper>
          <Table size="small">
            <TableHead>
              <TableRow>
                <TableCell>Name</TableCell>
                <TableCell>Location</TableCell>
                <TableCell>Capacity</TableCell>
                <TableCell>Type</TableCell>
                <TableCell align="right">Actions</TableCell>
              </TableRow>
            </TableHead>
            <TableBody>
              {labs.map(l => (
                <TableRow key={l.id} hover>
                  <TableCell>{l.name}</TableCell>
                  <TableCell>{l.location}</TableCell>
                  <TableCell>{l.capacity}</TableCell>
                  <TableCell>{l.type}</TableCell>
                  <TableCell align="right">
                    <Stack direction="row" spacing={1} justifyContent="flex-end">
                      <Button size="small" onClick={() => handleEdit(l)}>Edit</Button>
                      <Button size="small" color="error" onClick={() => handleDelete(l.id!)}>Delete</Button>
                    </Stack>
                  </TableCell>
                </TableRow>
              ))}
            </TableBody>
          </Table>
        </Paper>
      </Grid>

      <Dialog open={open} onClose={() => setOpen(false)} fullWidth maxWidth="sm">
        <DialogTitle>{form.id ? 'Edit Lab' : 'Add Lab'}</DialogTitle>
        <DialogContent>
          <Box sx={{ display: 'grid', gap: 2, mt: 1 }}>
            <TextField label="Name" value={form.name} onChange={e => setForm({ ...form, name: e.target.value })} fullWidth />
            <TextField label="Location" value={form.location} onChange={e => setForm({ ...form, location: e.target.value })} fullWidth />
            <TextField label="Capacity" type="number" value={form.capacity} onChange={e => setForm({ ...form, capacity: parseInt(e.target.value, 10) || 0 })} />
            <TextField label="Type" select value={form.type} onChange={e => setForm({ ...form, type: e.target.value })}>
              {LAB_TYPES.map(t => <MenuItem key={t} value={t}>{t}</MenuItem>)}
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