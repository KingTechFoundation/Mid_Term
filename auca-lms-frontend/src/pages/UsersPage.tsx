import { useEffect, useState } from 'react'
import { api } from '../api/client'
import { Box, Button, Dialog, DialogActions, DialogContent, DialogTitle, Grid, MenuItem, Paper, Stack, Table, TableBody, TableCell, TableHead, TableRow, TextField, Typography } from '@mui/material'

export type User = { id?: number; username: string; fullName: string; email: string; role: string; enabled: boolean }

const ROLES = ['ADMIN','LAB_MANAGER','INSTRUCTOR','STUDENT','TECHNICIAN']

export default function UsersPage() {
  const [rows, setRows] = useState<User[]>([])
  const [open, setOpen] = useState(false)
  const [form, setForm] = useState<{username:string;password:string;fullName:string;email:string;role:string}>({ username: '', password: '', fullName: '', email: '', role: 'STUDENT' })

  const load = () => api.get('/api/users').then(r => setRows(r.data))
  useEffect(() => { load() }, [])

  const create = async () => {
    await api.post('/api/users', form)
    setOpen(false); setForm({ username: '', password: '', fullName: '', email: '', role: 'STUDENT' }); load()
  }
  const remove = async (id: number) => { await api.delete(`/api/users/${id}`); load() }

  return (
    <Grid container spacing={2}>
      <Grid item xs={12}>
        <Stack direction="row" justifyContent="space-between" alignItems="center">
          <Typography variant="h6">Users</Typography>
          <Button variant="contained" onClick={() => setOpen(true)}>Create User</Button>
        </Stack>
      </Grid>
      <Grid item xs={12}>
        <Paper>
          <Table size="small">
            <TableHead>
              <TableRow>
                <TableCell>Username</TableCell>
                <TableCell>Full Name</TableCell>
                <TableCell>Email</TableCell>
                <TableCell>Role</TableCell>
                <TableCell>Enabled</TableCell>
                <TableCell align="right">Actions</TableCell>
              </TableRow>
            </TableHead>
            <TableBody>
              {rows.map(r => (
                <TableRow key={r.id} hover>
                  <TableCell>{r.username}</TableCell>
                  <TableCell>{r.fullName}</TableCell>
                  <TableCell>{r.email}</TableCell>
                  <TableCell>{r.role}</TableCell>
                  <TableCell>{r.enabled ? 'Yes' : 'No'}</TableCell>
                  <TableCell align="right">
                    <Button size="small" color="error" onClick={() => remove(r.id!)}>Delete</Button>
                  </TableCell>
                </TableRow>
              ))}
            </TableBody>
          </Table>
        </Paper>
      </Grid>

      <Dialog open={open} onClose={() => setOpen(false)} fullWidth maxWidth="sm">
        <DialogTitle>Create User</DialogTitle>
        <DialogContent>
          <Box sx={{ display: 'grid', gap: 2, mt: 1 }}>
            <TextField label="Username" value={form.username} onChange={e => setForm({ ...form, username: e.target.value })} fullWidth />
            <TextField label="Password" type="password" value={form.password} onChange={e => setForm({ ...form, password: e.target.value })} fullWidth />
            <TextField label="Full Name" value={form.fullName} onChange={e => setForm({ ...form, fullName: e.target.value })} fullWidth />
            <TextField label="Email" value={form.email} onChange={e => setForm({ ...form, email: e.target.value })} fullWidth />
            <TextField label="Role" select value={form.role} onChange={e => setForm({ ...form, role: e.target.value })}>
              {ROLES.map(t => <MenuItem key={t} value={t}>{t}</MenuItem>)}
            </TextField>
          </Box>
        </DialogContent>
        <DialogActions>
          <Button onClick={() => setOpen(false)}>Cancel</Button>
          <Button onClick={create} variant="contained">Create</Button>
        </DialogActions>
      </Dialog>
    </Grid>
  )
}