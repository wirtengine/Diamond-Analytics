import React, { useEffect, useState } from 'react';
import { useAuth } from '../context/AuthContext';
import dashboardService from '../services/dashboardService';

const Dashboard = () => {
    const { user } = useAuth();
    const [summary, setSummary] = useState(null);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState(null);

    useEffect(() => {
        const fetchSummary = async () => {
            try {
                const data = await dashboardService.getSummary();
                setSummary(data);
            } catch (err) {
                console.error(err);
                setError(err.message);
            } finally {
                setLoading(false);
            }
        };

        if (user) {
            fetchSummary();
        }
    }, [user]);

    if (loading) {
        return (
            <div className="dashboard-loading">
                <div className="spinner-mini">⚾</div>
                <p>Sincronizando métricas...</p>
            </div>
        );
    }

    if (error) {
        return (
            <div className="dashboard-error">
                <p>⚠️ {error}</p>
            </div>
        );
    }

    if (!summary) {
        return <p>No hay datos disponibles</p>;
    }

    // Calcular winRate y profit a partir de los datos reales
    const { totalBets, wonBets, lostBets, roi } = summary;
    const winRate = totalBets > 0 ? ((wonBets / totalBets) * 100).toFixed(1) : 0;
    // El profit se puede calcular a partir del ROI y el monto total apostado, pero como no tenemos el monto total en este DTO,
    // lo dejamos como "no disponible" o puedes agregar un campo profit al backend.
    // Por ahora mostraremos el ROI como beneficio relativo.
    const profitDisplay = roi ? `${roi}%` : '0%';

    return (
        <div className="dashboard-content">
            <header className="dashboard-header">
                <h1>Resumen</h1>
                <p>Bienvenido, {user?.username}</p>
            </header>

            <div className="stats-grid">
                <div className="stat-card">
                    <h3>ROI (Beneficio)</h3>
                    <p>{roi}%</p>
                </div>

                <div className="stat-card">
                    <h3>Total Apuestas</h3>
                    <p>{totalBets}</p>
                </div>

                <div className="stat-card">
                    <h3>Win Rate</h3>
                    <p>{winRate}%</p>
                </div>

                <div className="stat-card">
                    <h3>Apuestas Ganadas</h3>
                    <p>{wonBets}</p>
                </div>

                <div className="stat-card">
                    <h3>Apuestas Perdidas</h3>
                    <p>{lostBets}</p>
                </div>
            </div>
        </div>
    );
};

export default Dashboard;