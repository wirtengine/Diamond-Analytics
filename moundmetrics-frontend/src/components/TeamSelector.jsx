import React, { useState, useEffect, useCallback } from 'react';
import { fetchTeams } from '../services/api';
import './TeamSelector.css'; // Importamos nuestro CSS puro

const TeamSelector = ({ onSelectTeam }) => {
    const [teams, setTeams] = useState([]);
    const [selectedTeamId, setSelectedTeamId] = useState('');
    const [status, setStatus] = useState({ loading: true, error: null });

    const loadTeams = useCallback(async () => {
        try {
            setStatus({ loading: true, error: null });
            const data = await fetchTeams();
            setTeams(data);

            if (data?.length > 0) {
                setSelectedTeamId(data[0].id);
                onSelectTeam(data[0].id);
            }
        } catch (err) {
            setStatus({ loading: false, error: 'Error al cargar equipos' });
        } finally {
            setStatus(prev => ({ ...prev, loading: false }));
        }
    }, [onSelectTeam]);

    useEffect(() => {
        loadTeams();
    }, [loadTeams]);

    const handleChange = (e) => {
        const id = e.target.value;
        setSelectedTeamId(id);
        onSelectTeam(id);
    };

    if (status.loading) return <div className="loader-text">Cargando equipos...</div>;
    if (status.error) return <div className="error-message">{status.error}</div>;

    return (
        <div className="selector-wrapper">
            <label htmlFor="team-select" className="selector-label">
                Equipo Favorito
            </label>

            <div className="select-custom-container">
                <select
                    id="team-select"
                    value={selectedTeamId}
                    onChange={handleChange}
                    className="select-field"
                >
                    {teams.map(team => (
                        <option key={team.id} value={team.id}>
                            {team.name} {team.abbreviation && `(${team.abbreviation})`}
                        </option>
                    ))}
                </select>

                {/* Este es el icono de la flechita */}
                <span className="select-icon">
          <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2">
            <path d="M6 9l6 6 6-6" />
          </svg>
        </span>
            </div>
        </div>
    );
};

export default TeamSelector;