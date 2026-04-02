import React from 'react';
import { BrowserRouter, Routes, Route, Navigate } from 'react-router-dom';
import { AuthProvider } from './context/AuthContext'; // Corregido el path automático

// Componentes de Estructura
import PrivateRoute from './components/PrivateRoute';
import Layout from './components/Layout';

// Páginas
import Login from './pages/Login';
import Register from './pages/Register';
import Dashboard from './pages/Dashboard';

// Estilos Globales (Asegúrate de que el CSS bonito esté aquí)
import './App.css';

function App() {
    return (
        <BrowserRouter>
            <AuthProvider>
                <Routes>
                    {/* --- RUTAS PÚBLICAS --- */}
                    <Route path="/login" element={<Login />} />
                    <Route path="/register" element={<Register />} />

                    {/* --- RUTAS PROTEGIDAS (Requieren Login) --- */}
                    <Route element={<PrivateRoute />}>
                        <Route element={<Layout />}>
                            {/* Todas las rutas aquí dentro tendrán Sidebar y Navbar automáticamente */}
                            <Route path="/dashboard" element={<Dashboard />} />

                            {/* Aquí puedes agregar más adelante:
                  <Route path="/stats" element={<Stats />} />
                  <Route path="/predictions" element={<Predictions />} />
              */}
                        </Route>
                    </Route>

                    {/* --- REDIRECCIONES --- */}
                    <Route path="/" element={<Navigate to="/dashboard" replace />} />
                    <Route path="*" element={<Navigate to="/dashboard" replace />} />
                </Routes>
            </AuthProvider>
        </BrowserRouter>
    );
}

export default App;