import React, { useState, useEffect } from 'react';
import { fetchPitchersByTeam } from '../services/api';
import './PitcherList.css';

const PitcherList = ({ teamId, onSelectPitcher, selectedPitcherId }) => {
    const [pitchers, setPitchers] = useState([]);
    const [loading, setLoading] = useState(false);
    const [error, setError] = useState(null);

    useEffect(() => {
        if (!teamId) return;

        const loadPitchers = async () => {
            setLoading(true);
            setError(null);
            try {
                const data = await fetchPitchersByTeam(teamId);
                setPitchers(data);

                // Auto-seleccionar el primero si no hay uno seleccionado
                if (data.length > 0 && !selectedPitcherId) {
                    onSelectPitcher(data[0].id);
                }
            } catch (err) {
                setError("No se pudieron cargar los lanzadores.");
            } finally {
                setLoading(false);
            }
        };
        loadPitchers();
    }, [teamId, onSelectPitcher, selectedPitcherId]);

    return (
        <div className="pitcher-sidebar">
            <div className="sidebar-header">
                <h3>Lanzadores</h3>
                <span className="count-badge">{pitchers.length}</span>
            </div>

            <div className="list-container">
                {loading ? (
                    <div className="list-status">Cargando nómina...</div>
                ) : error ? (
                    <div className="list-status error">{error}</div>
                ) : (
                    <ul className="pitcher-menu">
                        {pitchers.map((pitcher) => (
                            <li
                                key={pitcher.id}
                                className={`pitcher-item ${pitcher.id === selectedPitcherId ? 'active' : ''}`}
                                onClick={() => onSelectPitcher(pitcher.id)}
                            >
                                <div className="pitcher-info">
                                    <span className="pitcher-name">{pitcher.fullName}</span>
                                    <span className={`role-tag ${pitcher.primaryPosition === 'SP' ? 'starter' : 'reliever'}`}>
                    {pitcher.primaryPosition === 'SP' ? 'Abridor' : 'Relevista'}
                  </span>
                                </div>
                                <div className="active-indicator"></div>
                            </li>
                        ))}
                    </ul>
                )}
            </div>
        </div>
    );
};

export default PitcherList;