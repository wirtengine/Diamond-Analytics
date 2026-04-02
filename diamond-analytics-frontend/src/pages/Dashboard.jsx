import React, { useEffect, useState } from 'react';
import { useAuth } from '../context/AuthContext';

const Dashboard = () => {
    const { user } = useAuth();
    const [summary, setSummary] = useState(null);
    const [loading, setLoading] = useState(true);

    useEffect(() => {
        const fetchSummary = async () => {
            try {
                // Simulación de datos iniciales
                setSummary({
                    totalBets: 124,
                    wonBets: 78,
                    lostBets: 46,
                    roi: +12.5,
                    winRate: 62.9,
                    profit: "+$1,240"
                });
            } catch (error) {
                console.error(error);
            } finally {
                setTimeout(() => setLoading(false), 600);
            }
        };
        fetchSummary();
    }, [user]);

    if (loading) return (
        <div className="dashboard-loading">
            <div className="spinner-mini">⚾</div>
            <p>Sincronizando métricas de mercado...</p>
        </div>
    );

    return (
        <div className="dashboard-content">
            <header className="dashboard-header">
                <div className="welcome-text">
                    <h1>Resumen de Actividad</h1>
                    <p>Bienvenido de nuevo, <strong>{user?.username}</strong>. Aquí tienes el estado de tu bankroll.</p>
                </div>
                <div className="date-badge">
                    {new Date().toLocaleDateString('es-ES', { day: 'numeric', month: 'long' })}
                </div>
            </header>

            <div className="stats-grid">
                <div className="stat-card accent">
                    <div className="stat-icon">💰</div>
                    <div className="stat-data">
                        <h3>Beneficio Total</h3>
                        <p className="stat-value">{summary.profit}</p>
                        <span className="stat-trend positive">↑ 4.2% esta semana</span>
                    </div>
                </div>

                <div className="stat-card">
                    <div className="stat-icon">📊</div>
                    <div className="stat-data">
                        <h3>Total Apuestas</h3>
                        <p className="stat-value">{summary.totalBets}</p>
                        <span className="stat-label">Operaciones ejecutadas</span>
                    </div>
                </div>

                <div className="stat-card">
                    <div className="stat-icon">🎯</div>
                    <div className="stat-data">
                        <h3>Win Rate</h3>
                        <p className="stat-value">{summary.winRate}%</p>
                        <div className="progress-bar">
                            <div className="progress-fill" style={{width: `${summary.winRate}%`}}></div>
                        </div>
                    </div>
                </div>

                <div className="stat-card">
                    <div className="stat-icon">📈</div>
                    <div className="stat-data">
                        <h3>ROI Global</h3>
                        <p className={`stat-value ${summary.roi >= 0 ? 'text-positive' : 'text-negative'}`}>
                            {summary.roi}%
                        </p>
                        <span className="stat-label">Retorno de inversión</span>
                    </div>
                </div>
            </div>

            <div className="dashboard-lower-grid">
                <div className="main-chart-placeholder">
                    <div className="placeholder-content">
                        <h3>🤖 Predicciones Sugeridas por IA</h3>
                        <p>Los modelos de Gemini están analizando los juegos de hoy...</p>
                        <div className="skeleton-loader"></div>
                        <div className="skeleton-loader short"></div>
                    </div>
                </div>

                <div className="side-panel-placeholder">
                    <h3>Últimos Movimientos</h3>
                    <div className="empty-state">
                        <p>No hay apuestas recientes para mostrar.</p>
                    </div>
                </div>
            </div>
        </div>
    );
};

export default Dashboard;