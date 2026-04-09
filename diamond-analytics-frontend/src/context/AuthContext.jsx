import React, { createContext, useState, useContext, useEffect, useCallback } from 'react';
import authService from '../services/authService';

const AuthContext = createContext();

export const useAuth = () => useContext(AuthContext);

export const AuthProvider = ({ children }) => {
    const [user, setUser] = useState(null);
    const [loading, setLoading] = useState(true);

    // Verificar usuario al montar
    useEffect(() => {
        const checkAuth = () => {
            try {
                const storedUser = authService.getCurrentUser();
                if (storedUser) {
                    setUser(storedUser);
                }
            } catch (error) {
                console.error("Error cargando sesión:", error);
                localStorage.removeItem('user');
            } finally {
                setTimeout(() => setLoading(false), 500);
            }
        };

        checkAuth();
    }, []);

    const login = async (credentials) => {
        try {
            const data = await authService.login(credentials);
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
    }, []);

    const value = {
        user,
        loading,
        login,
        register,
        logout,
        isAuthenticated: !!user
    };

    return (
        <AuthContext.Provider value={value}>
            {children}
        </AuthContext.Provider>
    );
};