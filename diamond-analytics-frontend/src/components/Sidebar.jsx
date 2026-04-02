import React from 'react';
import { useAuth } from '../context/AuthContext';
// Si usas react-router-dom, te recomiendo cambiar <a> por <Link>
import { Link, useLocation } from 'react-router-dom';

const Sidebar = () => {
    const { user, logout } = useAuth();
    // const location = useLocation(); // Para marcar el link activo

    return (
        <aside className="sidebar">
            <div className="sidebar-header">
                <div className="logo-icon">⚾</div>
                <div className="logo-text">
                    <span className="brand-name">Diamond</span>
                    <span className="brand-sub">Analytics</span>
                </div>
            </div>

            <nav className="sidebar-nav">
                <p className="nav-label">Menú Principal</p>
                <ul>
                    <li className="nav-item">
                        <a href="/dashboard" className="nav-link active">
                            <span className="icon">📊</span> Dashboard
                        </a>
                    </li>
                    <li className="nav-item">
                        <a href="/games" className="nav-link">
                            <span className="icon">📅</span> Juegos del día
                        </a>
                    </li>
                    <li className="nav-item">
                        <a href="/predictions" className="nav-link">
                            <span className="icon">🤖</span> Predicciones AI
                        </a>
                    </li>
                    <li className="nav-item">
                        <a href="/bets" className="nav-link">
                            <span className="icon">💰</span> Mis apuestas
                        </a>
                    </li>
                    <li className="nav-item">
                        <a href="/stats" className="nav-link">
                            <span className="icon">📈</span> Estadísticas
                        </a>
                    </li>
                </ul>
            </nav>

            <div className="sidebar-footer">
                <div className="user-card">
                    <div className="user-avatar">
                        {user?.username?.charAt(0).toUpperCase() || 'U'}
                    </div>
                    <div className="user-details">
                        <p className="username">{user?.username || 'Usuario'}</p>
                        <button onClick={logout} className="logout-btn">
                            Cerrar sesión
                        </button>
                    </div>
                </div>
            </div>
        </aside>
    );
};

export default Sidebar;