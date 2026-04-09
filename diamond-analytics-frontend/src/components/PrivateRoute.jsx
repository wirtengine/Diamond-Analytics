import React from 'react';
import { Navigate, Outlet } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';

const PrivateRoute = () => {
    const { user, loading } = useAuth();

    if (loading) {
        return (
            <div className="loading-screen">
                <div className="loader-container">
                    <div className="baseball-spinner">⚾</div>
                    <p>Preparando estadísticas...</p>
                </div>
            </div>
        );
    }

    return user ? <Outlet /> : <Navigate to="/login" />;
};

export default PrivateRoute;