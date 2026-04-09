import React, { useState, useEffect, useCallback } from 'react';
import { useAuth } from '../context/AuthContext';
import gamesService from '../services/gamesService';
import adminService from '../services/adminService';

const GamesToday = () => {
    const { user } = useAuth();
    const [games, setGames] = useState([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState('');
    const [ingesting, setIngesting] = useState(false);
    const [ingestMessage, setIngestMessage] = useState('');

    const fetchGames = useCallback(async () => {
        setLoading(true);
        try {
            const data = await gamesService.getTodaysGames();
            setGames(data);
            setError('');
        } catch (err) {
            setError('No se pudo sincronizar el calendario de hoy.');
        } finally {
            setLoading(false);
        }
    }, []);

    const handleIngest = async () => {
        setIngesting(true);
        setIngestMessage('');
        try {
            await adminService.ingestGames();
            setIngestMessage('✅ Ingesta completada con éxito. Actualizando juegos...');

            setTimeout(() => {
                fetchGames();
                setIngestMessage('');
            }, 2000);

        } catch (err) {
            setIngestMessage(`❌ Error: ${err.message}`);
        } finally {
            setIngesting(false);
        }
    };

    useEffect(() => {
        if (user) fetchGames();
    }, [user, fetchGames]);

    if (loading) return (
        <div className="internal-loader">
            <div className="spinner-mini">⚾</div>
            <p>Sincronizando con el diamante...</p>
        </div>
    );

    return (
        <div className="games-container">
            <header className="section-header">
                <div className="header-info">
                    <h1>Calendario del Día</h1>
                    <p>Encuentros oficiales MLB y probabilidades de mercado</p>
                </div>
                <div className="header-actions">
                    <button onClick={fetchGames} className="btn-secondary">
                        🔄 Sincronizar
                    </button>
                    <button onClick={handleIngest} disabled={ingesting} className="btn-primary">
                        {ingesting ? '⏳ Ingestionando...' : '📥 Ingestar desde API'}
                    </button>
                </div>
            </header>

            {error && <div className="alert error">{error}</div>}
            {ingestMessage && (
                <div className={`alert ${ingestMessage.includes('✅') ? 'success' : 'error'}`}>
                    {ingestMessage}
                </div>
            )}

            {games.length === 0 ? (
                <div className="empty-state-card">
                    <p>No hay juegos programados para hoy.</p>
                </div>
            ) : (
                <div className="games-grid">
                    {games.map(game => {
                        const gameDate = new Date(game.startTime);

                        const options = {
                            weekday: 'short',
                            month: 'short',
                            day: 'numeric',
                            hour: '2-digit',
                            minute: '2-digit',
                            timeZone: 'America/Managua'
                        };

                        const formatted = gameDate.toLocaleString('es-NI', options);

                        return (
                            <div key={game.id} className="game-card">
                                <div className="card-top">
                                    <span className={`status-pill ${game.status}`}>
                                        {game.status === 'scheduled'
                                            ? 'Programado'
                                            : game.status === 'finished'
                                                ? 'Finalizado'
                                                : 'En Vivo'}
                                    </span>

                                    <span className="game-clock">
                                        {formatted}
                                    </span>
                                </div>

                                <div className="matchup-box">
                                    <div className="team-row">
                                        <span className="team-name">{game.awayTeam?.name}</span>
                                        {game.status === 'finished' && (
                                            <span className="team-score">{game.awayScore}</span>
                                        )}
                                    </div>

                                    <div className="vs-line">VS</div>

                                    <div className="team-row">
                                        <span className="team-name">{game.homeTeam?.name}</span>
                                        {game.status === 'finished' && (
                                            <span className="team-score">{game.homeScore}</span>
                                        )}
                                    </div>
                                </div>

                                <div className="card-actions">
                                    <button className="btn-ai-analyze">✨ IA Predictor</button>
                                    <button className="btn-details">Métricas</button>
                                </div>
                            </div>
                        );
                    })}
                </div>
            )}
        </div>
    );
};

export default GamesToday;