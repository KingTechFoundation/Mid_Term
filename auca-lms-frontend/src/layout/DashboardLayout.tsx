import { useMemo, useState } from 'react'
import { AppBar, Avatar, Box, Divider, Drawer, IconButton, List, ListItemButton, ListItemIcon, ListItemText, Toolbar, Typography } from '@mui/material'
import { Menu as MenuIcon, Dashboard as DashboardIcon, Memory as MemoryIcon, Devices as DevicesIcon, EventNote as EventIcon, Build as BuildIcon, DoorFront as DoorIcon, Group as GroupIcon, Assessment as AssessmentIcon } from '@mui/icons-material'
import { Link, Outlet, useLocation } from 'react-router-dom'
import { useAuth } from '../auth/store'

const drawerWidth = 240

const navItems = [
  { to: '/dashboard', icon: <DashboardIcon />, text: 'Dashboard' },
  { to: '/labs', icon: <MemoryIcon />, text: 'Labs' },
  { to: '/equipment', icon: <DevicesIcon />, text: 'Equipment' },
  { to: '/bookings', icon: <EventIcon />, text: 'Bookings' },
  { to: '/maintenance', icon: <BuildIcon />, text: 'Maintenance' },
  { to: '/access', icon: <DoorIcon />, text: 'Access Logs' },
  { to: '/users', icon: <GroupIcon />, text: 'Users', roles: ['ADMIN'] as const },
  { to: '/reports', icon: <AssessmentIcon />, text: 'Reports', roles: ['ADMIN','LAB_MANAGER'] as const }
]

export default function DashboardLayout() {
  const [open, setOpen] = useState(true)
  const { user, clear } = useAuth()
  const location = useLocation()

  const filteredNav = useMemo(() => navItems.filter(n => !n.roles || (user && (n.roles as readonly string[]).includes(user.role))), [user])

  return (
    <Box sx={{ display: 'flex' }}>
      <AppBar position="fixed" sx={{ zIndex: (theme) => theme.zIndex.drawer + 1 }}>
        <Toolbar>
          <IconButton color="inherit" edge="start" onClick={() => setOpen(!open)} sx={{ mr: 2 }}>
            <MenuIcon />
          </IconButton>
          <Typography variant="h6" sx={{ flexGrow: 1 }}>AUCA Lab Management</Typography>
          <Box sx={{ display: 'flex', alignItems: 'center', gap: 1 }}>
            <Typography variant="body2">{user?.fullName}</Typography>
            <Avatar sx={{ bgcolor: 'secondary.main' }}>{user?.fullName?.[0]}</Avatar>
            <IconButton color="inherit" onClick={clear} title="Logout">
              <Typography variant="body2">Logout</Typography>
            </IconButton>
          </Box>
        </Toolbar>
      </AppBar>

      <Drawer variant="persistent" open={open} sx={{ width: drawerWidth, flexShrink: 0, [`& .MuiDrawer-paper`]: { width: drawerWidth, boxSizing: 'border-box' } }}>
        <Toolbar />
        <Divider />
        <List>
          {filteredNav.map(item => (
            <ListItemButton key={item.to} component={Link} to={item.to} selected={location.pathname === item.to}>
              <ListItemIcon>{item.icon}</ListItemIcon>
              <ListItemText primary={item.text} />
            </ListItemButton>
          ))}
        </List>
      </Drawer>

      <Box component="main" sx={{ flexGrow: 1, p: 3 }}>
        <Toolbar />
        <Outlet />
      </Box>
    </Box>
  )
}