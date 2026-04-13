import React, { useState, useEffect, useCallback, useMemo } from 'react';
import { fetchBatterStats, fetchBatterAppearances } from '../services/api';
import { Bar } from 'react-chartjs-2';
import {
    Chart as ChartJS,
    CategoryScale,
    LinearScale,
    BarElement,
    Title,
    Tooltip,
    Legend
} from 'chart.js';
import './BatterStats.css';

ChartJS.register(CategoryScale, LinearScale, BarElement, Title, Tooltip, Legend);

const BatterStats = ({ batterId }) => {
    const [stats, setStats] = useState(null);
    const [appearances, setAppearances] = useState([]);
    const [days, setDays] = useState(7);
    const [loading, setLoading] = useState(false);
    const [error, setError] = useState(null);

    const loadData = useCallback(async () => {
        if (!batterId) return;
        setLoading(true);
        setError(null);
        try {
            const [st, apps] = await Promise.all([
                fetchBatterStats(batterId, days),
                fetchBatterAppearances(batterId, days)
            ]);
            setStats(st);
            setAppearances(apps);
        } catch (err) {
            setError("Error al sincronizar las estadísticas del bateador.");
        } finally {
            setLoading(false);
        }
    }, [batterId, days]);

    useEffect(() => {
        loadData();
    }, [loadData]);

    const formatNumber = (value, decimals = 2) => {
        const num = Number(value);
        return isNaN(num) ? (decimals === 3 ? '.000' : '0') : num.toFixed(decimals);
    };

    // Optimizamos el gráfico con useMemo para evitar cálculos en cada render
    const chartData = useMemo(() => ({
        labels: appearances.map(a => {
            const date = new Date(a.gameDate);
            return date.toLocaleDateString('es-ES', { day: 'numeric', month: 'short' });
        }),
        datasets: [
            {
                label: 'Hits',
                data: appearances.map(a => a.hits || 0),
                backgroundColor: 'rgba(34, 197, 94, 0.6)',
                borderColor: '#16a34a',
                borderWidth: 1,
                borderRadius: 4,
            },
        ],
    }), [appearances]);

    const chartOptions = {
        responsive: true,
        maintainAspectRatio: false,
        plugins: { legend: { display: false } },
        scales: {
            y: { beginAtZero: true, ticks: { stepSize: 1, color: '#64748b' }, grid: { color: '#f1f5f9' } },
            x: { grid: { display: false }, ticks: { color: '#64748b' } }
        },
    };

    if (!batterId) return <div className="empty-state">Selecciona un bateador para ver el análisis</div>;

    return (
        <div className={`batter-stats-container ${loading ? 'is-loading' : ''}`}>
            <header className="stats-header">
                <div className="player-meta">
                    <h2>{stats?.fullName || 'Cargando...'}</h2>
                    <span className="pos-label">Bateador • Últimos {days} días</span>
                </div>
                <div className="days-nav">
                    {[3, 7, 14, 30].map(d => (
                        <button
                            key={d}
                            className={days === d ? 'active' : ''}
                            onClick={() => setDays(d)}
                        >
                            {d}D
                        </button>
                    ))}
                </div>
            </header>

            {error && <div className="error-message">{error}</div>}

            {/* MÉTRICAS PRINCIPALES */}
            <section className="primary-metrics">
                <div className="metric-item">
                    <span className="label">AVG</span>
                    <span className="value featured">{formatNumber(stats?.avg, 3)}</span>
                </div>
                <div className="metric-item">
                    <span className="label">HR</span>
                    <span className="value">{stats?.homeRuns || 0}</span>
                </div>
                <div className="metric-item">
                    <span className="label">RBI</span>
                    <span className="value">{stats?.rbi || 0}</span>
                </div>
                <div className="metric-item">
                    <span className="label">Juegos</span>
                    <span className="value">{stats?.gamesPlayed || 0}</span>
                </div>
            </section>

            {/* GRILLA AVANZADA */}
            <section className="details-grid">
                <div className="detail-box"><span>OBP</span><strong>{formatNumber(stats?.obp, 3)}</strong></div>
                <div className="detail-box"><span>SLG</span><strong>{formatNumber(stats?.slg, 3)}</strong></div>
                <div className="detail-box"><span>OPS</span><strong>{formatNumber(stats?.ops, 3)}</strong></div>
                <div className="detail-box"><span>H</span><strong>{stats?.hits || 0}</strong></div>
                <div className="detail-box"><span>2B</span><strong>{stats?.doubles || 0}</strong></div>
                <div className="detail-box"><span>3B</span><strong>{stats?.triples || 0}</strong></div>
                <div className="detail-box"><span>BB</span><strong>{stats?.baseOnBalls || 0}</strong></div>
                <div className="detail-box"><span>SO</span><strong>{stats?.strikeOuts || 0}</strong></div>
                <div className="detail-box"><span>SB</span><strong>{stats?.stolenBases || 0}</strong></div>
            </section>

            {/* GRÁFICO */}
            <section className="chart-section">
                <h3>Tendencia de Hits</h3>
                <div className="canvas-holder">
                    {appearances.length > 0 ? (
                        <Bar data={chartData} options={chartOptions} />
                    ) : (
                        <div className="no-data">No se registraron turnos en este periodo</div>
                    )}
                </div>
            </section>

            {loading && (
                <div className="loading-overlay">
                    <div className="pulse-loader"></div>
                    <span>Actualizando...</span>
                </div>
            )}
        </div>
    );
};

export default BatterStats;