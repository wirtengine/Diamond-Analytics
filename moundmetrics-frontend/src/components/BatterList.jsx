import React, { useState, useEffect, useCallback } from 'react';
import { fetchBattersByTeam } from '../services/api';
import './BatterList.css';

const BatterList = ({ teamId, onSelectBatter, selectedId }) => {
    const [batters, setBatters] = useState([]);
    const [loading, setLoading] = useState(false);
    const [error, setError] = useState(null);

    const loadBatters = useCallback(async () => {
        if (!teamId) return;
        setLoading(true);
        setError(null);
        try {
            const data = await fetchBattersByTeam(teamId);
            setBatters(data);

            // Selección automática del primer bateador si no hay uno seleccionado
            if (data.length > 0 && !selectedId) {
                onSelectBatter(data[0].id);
            }
        } catch (e) {
            setError("Error al cargar la lista de bateadores.");
        } finally {
            setLoading(false);
        }
    }, [teamId, onSelectBatter, selectedId]);

    useEffect(() => {
        loadBatters();
    }, [loadBatters]);

    return (
        <div className="batter-sidebar">
            <div className="sidebar-header">
                <h3>Bateadores</h3>
                <span className="player-count">{batters.length}</span>
            </div>

            <div className="batter-list-container">
                {loading ? (
                    <div className="status-msg">
                        <div className="spinner-small"></div>
                        Cargando nómina...
                    </div>
                ) : error ? (
                    <div className="status-msg error">{error}</div>
                ) : (
                    <ul className="batter-menu">
                        {batters.map((batter) => (
                            <li
                                key={batter.id}
                                className={`batter-item ${batter.id === selectedId ? 'active' : ''}`}
                                onClick={() => onSelectBatter(batter.id)}
                            >
                                <div className="active-indicator" />
                                <div className="batter-details">
                                    <span className="batter-name">{batter.fullName}</span>
                                    <span className={`pos-badge ${batter.primaryPosition?.toLowerCase() || 'dh'}`}>
                    {batter.primaryPosition || 'DH'}
                  </span>
                                </div>
                                <div className="selection-arrow">
                                    <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2.5">
                                        <path d="M9 5l7 7-7 7" />
                                    </svg>
                                </div>
                            </li>
                        ))}
                    </ul>
                )}
            </div>
        </div>
    );
};

export default BatterList;