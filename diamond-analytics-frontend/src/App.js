import React from 'react';
import { BrowserRouter, Routes, Route, Navigate } from 'react-router-dom';
import { AuthProvider } from './context/AuthContext';

import PrivateRoute from './components/PrivateRoute';
import Layout from './components/Layout';

import Login from './pages/Login';
import Register from './pages/Register';
import Dashboard from './pages/Dashboard';
import GamesToday from './pages/GamesToday';   // 👈 Importa el componente

import './App.css';

function App() {
    return (
        <BrowserRouter>
            <AuthProvider>
                <Routes>
                    {/* PUBLIC */}
                    <Route path="/login" element={<Login />} />
                    <Route path="/register" element={<Register />} />

                    {/* PRIVATE */}
                    <Route element={<PrivateRoute />}>
                        <Route element={<Layout />}>
                            <Route path="/dashboard" element={<Dashboard />} />
                            <Route path="/games" element={<GamesToday />} />   {/* 👈 Nueva ruta */}
                            {/* Aquí agregarás más rutas en el futuro */}
                        </Route>
                    </Route>

                    {/* REDIRECT */}
                    <Route path="/" element={<Navigate to="/dashboard" />} />
                    <Route path="*" element={<Navigate to="/dashboard" />} />
                </Routes>
            </AuthProvider>
        </BrowserRouter>
    );
}

export default App;