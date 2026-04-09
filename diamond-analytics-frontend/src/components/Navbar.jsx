import React from 'react';
import { useAuth } from '../context/AuthContext';

const Navbar = () => {
    const { user, logout } = useAuth();

    // Obtener la hora para un saludo amable
    const hour = new Date().getHours();
    const greeting = hour < 12 ? 'Buenos días' : hour < 18 ? 'Buenas tardes' : 'Buenas noches';

    return (
        <header className="navbar">
            <div className="navbar-left">
                <span className="page-title">Panel de Control</span>
                <span className="breadcrumb-separator">/</span>
                <span className="current-path">Resumen</span>
            </div>

            <div className="navbar-right">
                <div className="search-bar-minimal">
                    <span className="search-icon">🔍</span>
                    <input type="text" placeholder="Buscar estadísticas..." />
                </div>

                <div className="navbar-user-info">
                    <div className="text-container">
                        <span className="greeting">{greeting},</span>
                        <span className="user-name">{user?.username || 'Guest'}</span>
                    </div>
                    <button onClick={logout} className="logout-pill">
                        Salir
                    </button>
                </div>
            </div>
        </header>
    );
};

export default Navbar;