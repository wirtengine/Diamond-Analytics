import React from 'react';
import { NavLink } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';

const Sidebar = () => {
    const { user, logout } = useAuth();

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
                        <NavLink to="/dashboard" className={({ isActive }) => isActive ? "nav-link active" : "nav-link"}>
                            <span className="icon">📊</span> Dashboard
                        </NavLink>
                    </li>
                    <li className="nav-item">
                        <NavLink to="/games" className={({ isActive }) => isActive ? "nav-link active" : "nav-link"}>
                            <span className="icon">📅</span> Juegos del día
                        </NavLink>
                    </li>
                    <li className="nav-item">
                        <NavLink to="/predictions" className={({ isActive }) => isActive ? "nav-link active" : "nav-link"}>
                            <span className="icon">🤖</span> Predicciones AI
                        </NavLink>
                    </li>
                    <li className="nav-item">
                        <NavLink to="/bets" className={({ isActive }) => isActive ? "nav-link active" : "nav-link"}>
                            <span className="icon">💰</span> Mis apuestas
                        </NavLink>
                    </li>
                    <li className="nav-item">
                        <NavLink to="/stats" className={({ isActive }) => isActive ? "nav-link active" : "nav-link"}>
                            <span className="icon">📈</span> Estadísticas
                        </NavLink>
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