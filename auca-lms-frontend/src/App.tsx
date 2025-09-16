import { Navigate, Route, Routes } from 'react-router-dom'
import { Box } from '@mui/material'
import LoginPage from './pages/LoginPage'
import DashboardLayout from './layout/DashboardLayout'
import DashboardHome from './pages/DashboardHome'
import LabsPage from './pages/LabsPage'
import EquipmentPage from './pages/EquipmentPage'
import BookingsPage from './pages/BookingsPage'
import MaintenancePage from './pages/MaintenancePage'
import AccessLogsPage from './pages/AccessLogsPage'
import UsersPage from './pages/UsersPage'
import ReportsPage from './pages/ReportsPage'
import RequireAuth from './auth/RequireAuth'

function App() {
  return (
    <Box sx={{ minHeight: '100vh' }}>
      <Routes>
        <Route path="/login" element={<LoginPage />} />
        <Route element={<RequireAuth />}>
          <Route element={<DashboardLayout />}>
            <Route path="/" element={<Navigate to="/dashboard" replace />} />
            <Route path="/dashboard" element={<DashboardHome />} />
            <Route path="/labs" element={<LabsPage />} />
            <Route path="/equipment" element={<EquipmentPage />} />
            <Route path="/bookings" element={<BookingsPage />} />
            <Route path="/maintenance" element={<MaintenancePage />} />
            <Route path="/access" element={<AccessLogsPage />} />
            <Route path="/users" element={<UsersPage />} />
            <Route path="/reports" element={<ReportsPage />} />
          </Route>
        </Route>
        <Route path="*" element={<Navigate to="/login" replace />} />
      </Routes>
    </Box>
  )
}

export default App
