import React, { useState, useEffect, useMemo } from 'react';
import { fetchBullpenStats } from '../services/api';
import './BullpenStats.css';

// Fechas clave de la temporada 2026 de MLB
const INICIO_TEMPORADA = '2026-03-26';
const FIN_TEMPORADA = '2026-09-30';   // Fecha aproximada de fin de temporada regular

const BullpenStats = ({ teamId }) => {
    const [stats, setStats] = useState(null);
    const [fromDate, setFromDate] = useState('');
    const [toDate, setToDate] = useState('');
    const [loading, setLoading] = useState(false);
    const [error, setError] = useState(null);

    // Calcula la fecha límite superior: el menor entre ayer y el fin de temporada
    const fechaMaxima = useMemo(() => {
        const ayer = new Date();
        ayer.setDate(ayer.getDate() - 1);
        const ayerStr = ayer.toISOString().split('T')[0];

        // Si ya pasó la temporada, devolvemos el fin de temporada
        return ayerStr > FIN_TEMPORADA ? FIN_TEMPORADA : ayerStr;
    }, []);

    // Inicializa las fechas con el rango completo de la temporada hasta la fecha máxima
    useEffect(() => {
        setFromDate(INICIO_TEMPORADA);
        setToDate(fechaMaxima);
    }, [fechaMaxima]);

    useEffect(() => {
        if (!teamId) return;
        const loadStats = async () => {
            setLoading(true);
            setError(null);
            try {
                const data = await fetchBullpenStats(teamId, fromDate, toDate);
                setStats(data);
            } catch (err) {
                setError("No se pudieron obtener los datos.");
            } finally {
                setLoading(false);
            }
        };
        loadStats();
    }, [teamId, fromDate, toDate]);

    const handleDateChange = (e) => {
        const { name, value } = e.target;
        if (name === 'from') {
            setFromDate(value);
            // Si la fecha "desde" es mayor que "hasta", ajustamos "hasta"
            if (value > toDate) {
                setToDate(value);
            }
        } else if (name === 'to') {
            setToDate(value);
            // Si la fecha "hasta" es menor que "desde", ajustamos "desde"
            if (value < fromDate) {
                setFromDate(value);
            }
        }
    };

    if (!teamId) return null;

    return (
        <div className={`bullpen-card ${loading ? 'is-loading' : ''}`}>
            <div className="card-header">
                <h3>Estadísticas del Bullpen</h3>
                <div className="date-filters">
                    <div className="input-group">
                        <span>Desde</span>
                        <input
                            type="date"
                            name="from"
                            value={fromDate}
                            min={INICIO_TEMPORADA}
                            max={fechaMaxima}
                            onChange={handleDateChange}
                        />
                    </div>
                    <div className="input-group">
                        <span>Hasta</span>
                        <input
                            type="date"
                            name="to"
                            value={toDate}
                            min={fromDate || INICIO_TEMPORADA}
                            max={fechaMaxima}
                            onChange={handleDateChange}
                        />
                    </div>
                </div>
            </div>

            {error ? (
                <div className="error-box">{error}</div>
            ) : !stats && !loading ? (
                <p className="empty-msg">No hay datos para este rango de fechas.</p>
            ) : (
                <div className="stats-container">
                    <div className="main-stats">
                        <div className="stat-card highlighted">
                            <span className="stat-label">ERA</span>
                            <span className="stat-value">{stats?.era?.toFixed(2) || '0.00'}</span>
                        </div>
                        <div className="stat-card highlighted">
                            <span className="stat-label">WHIP</span>
                            <span className="stat-value">{stats?.whip?.toFixed(2) || '0.00'}</span>
                        </div>
                    </div>
                    <div className="secondary-stats">
                        <div className="stat-item">
                            <span className="stat-label">Innings</span>
                            <span className="stat-value">{stats?.totalInnings?.toFixed(1) || '0.0'}</span>
                        </div>
                        <div className="stat-item">
                            <span className="stat-label">Carreras L.</span>
                            <span className="stat-value">{stats?.earnedRuns || '0'}</span>
                        </div>
                        <div className="stat-item">
                            <span className="stat-label">Apariciones</span>
                            <span className="stat-value">{stats?.appearancesCount || '0'}</span>
                        </div>
                    </div>
                </div>
            )}

            {loading && <div className="overlay-loader">Actualizando...</div>}
        </div>
    );
};

export default BullpenStats;