import React, { useState, useEffect, useCallback } from 'react';
import { fetchWorkload, fetchPitcherAppearances, fetchPitcherStats } from '../services/api';
import WorkloadChart from './WorkloadChart';
import './PitcherWorkload.css';

const PitcherWorkload = ({ pitcherId }) => {
    const [workload, setWorkload] = useState(null);
    const [appearances, setAppearances] = useState([]);
    const [stats, setStats] = useState(null);
    const [days, setDays] = useState(7);
    const [loading, setLoading] = useState(false);
    const [error, setError] = useState(null);

    const loadData = useCallback(async () => {
        if (!pitcherId) return;
        setLoading(true);
        setError(null);
        try {
            const [wl, apps, st] = await Promise.all([
                fetchWorkload(pitcherId, days),
                fetchPitcherAppearances(pitcherId, days),
                fetchPitcherStats(pitcherId, days)
            ]);

            // Logs de diagnóstico (puedes eliminar cuando todo funcione)
            console.log('===== DATOS RECIBIDOS DEL BACKEND =====');
            console.log('Workload:', wl);
            console.log('Apariciones:', apps);
            console.log('Stats (completo):', st);
            if (st) {
                console.log('Contenido JSON completo de stats:');
                console.log(JSON.stringify(st, null, 2));
            }
            console.log('========================================');

            setWorkload(wl);
            setAppearances(apps);
            setStats(st);
        } catch (err) {
            setError("Error al sincronizar datos del lanzador.");
            console.error(err);
        } finally {
            setLoading(false);
        }
    }, [pitcherId, days]);

    useEffect(() => {
        loadData();
    }, [loadData]);

    if (!pitcherId) {
        return (
            <div className="empty-state-container">
                <div className="empty-icon">⚾</div>
                <p>Selecciona un lanzador para visualizar el análisis de carga</p>
            </div>
        );
    }

    // Función segura para formatear números
    const formatNumber = (value, decimals = 2) => {
        if (value === null || value === undefined) return '0.00';
        const num = Number(value);
        return isNaN(num) ? '0.00' : num.toFixed(decimals);
    };

    return (
        <div className={`workload-dashboard ${loading ? 'is-loading' : ''}`}>
            {/* CABECERA */}
            <div className="dashboard-header">
                <div className="player-info">
                    <h2>{stats?.fullName || 'Cargando...'}</h2>
                    <span className="status-badge">Análisis en Tiempo Real</span>
                </div>

                <div className="days-selector">
                    {[3, 7, 14, 30].map((d) => (
                        <button
                            key={d}
                            className={days === d ? 'btn-day active' : 'btn-day'}
                            onClick={() => setDays(d)}
                        >
                            {d}D
                        </button>
                    ))}
                </div>
            </div>

            {error ? (
                <div className="error-banner">{error}</div>
            ) : (
                <div className="dashboard-content">

                    {/* NIVEL 1: RESUMEN FÍSICO (WORKLOAD) */}
                    <section className="summary-grid">
                        <div className="metric-card">
                            <span className="metric-label">Juegos</span>
                            <span className="metric-value">{workload?.gamesPlayed || 0}</span>
                        </div>
                        <div className="metric-card">
                            <span className="metric-label">Picheos Totales</span>
                            <span className="metric-value">{workload?.totalPitches || 0}</span>
                        </div>
                        <div className="metric-card highlight-blue">
                            <span className="metric-label">Última Salida</span>
                            <span className="metric-value">{workload?.pitchesLastGame || 0}</span>
                            <span className="metric-subtext">{workload?.lastGameDate || '--'}</span>
                        </div>
                    </section>

                    {/* NIVEL 2: RENDIMIENTO AVANZADO */}
                    <section className="advanced-stats-section">
                        <h4>Estadísticas de Rendimiento</h4>
                        <div className="advanced-grid">
                            <div className="stat-box">
                                <span className="stat-name">ERA</span>
                                <span className="stat-number">{formatNumber(stats?.era, 2)}</span>
                            </div>
                            <div className="stat-box">
                                <span className="stat-name">WHIP</span>
                                <span className="stat-number">{formatNumber(stats?.whip, 2)}</span>
                            </div>
                            <div className="stat-box">
                                <span className="stat-name">K/9</span>
                                {/* ✅ CORRECCIÓN: usar kper9 (todo minúsculas) */}
                                <span className="stat-number">{formatNumber(stats?.kper9, 2)}</span>
                            </div>
                            <div className="stat-box">
                                <span className="stat-name">BB/9</span>
                                {/* ✅ CORRECCIÓN: usar bbPer9 */}
                                <span className="stat-number">{formatNumber(stats?.bbPer9, 2)}</span>
                            </div>
                            <div className="stat-box">
                                <span className="stat-name">Innings</span>
                                <span className="stat-number">{formatNumber(stats?.inningsPitched, 1)}</span>
                            </div>
                            <div className="stat-box">
                                <span className="stat-name">Ponches</span>
                                <span className="stat-number">{stats?.strikeOuts || 0}</span>
                            </div>
                        </div>
                    </section>

                    {/* NIVEL 3: GRÁFICO TENDENCIA */}
                    <section className="chart-wrapper">
                        <WorkloadChart appearances={appearances} />
                    </section>
                </div>
            )}

            {/* CARGADOR SOBREPUESTO */}
            {loading && (
                <div className="dashboard-overlay">
                    <div className="spinner"></div>
                    <p>Sincronizando métricas...</p>
                </div>
            )}
        </div>
    );
};

export default PitcherWorkload;