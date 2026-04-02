import React, { createContext, useState, useContext, useEffect, useCallback } from 'react';
import authService from '../services/authService';

const AuthContext = createContext();

export const useAuth = () => useContext(AuthContext);

export const AuthProvider = ({ children }) => {
    const [user, setUser] = useState(null);
    const [loading, setLoading] = useState(true);

    // Verificamos el usuario al montar el componente
    useEffect(() => {
        const checkAuth = () => {
            try {
                const storedUser = authService.getCurrentUser();
                if (storedUser) {
                    setUser(storedUser);
                }
            } catch (error) {
                console.error("Error cargando sesión:", error);
                localStorage.removeItem('user'); // Limpiar si el JSON está corrupto
            } finally {
                // Pequeño delay artificial para que el spinner de béisbol
                // se vea fluido y no desaparezca de golpe
                setTimeout(() => setLoading(false), 800);
            }
        };

        checkAuth();
    }, []);

    const login = async (credentials) => {
        try {
            const data = await authService.login(credentials);
            // El authService ya debería manejar el localStorage,
            // pero si no, es bueno centralizarlo aquí
            setUser(data);
            return { success: true, data };
        } catch (error) {
            return { success: false, error: error.message };
        }
    };

    const register = async (userData) => {
        try {
            const data = await authService.register(userData);
            setUser(data);
            return { success: true, data };
        } catch (error) {
            return { success: false, error: error.message };
        }
    };

    const logout = useCallback(() => {
        authService.logout();
        setUser(null);
        // Opcional: Redirigir manualmente o limpiar estados globales
    }, []);

    const value = {
        user,
        loading,
        login,
        register,
        logout,
        isAuthenticated: !!user // Helper útil para checks rápidos
    };

    return (
        <AuthContext.Provider value={value}>
            {children}
        </AuthContext.Provider>
    );
};