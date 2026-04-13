import React, { useState, useEffect } from 'react';
import TeamSelector from './components/TeamSelector';
import PitcherList from './components/PitcherList';
import PitcherWorkload from './components/PitcherWorkload';
import BatterList from './components/BatterList';
import BatterStats from './components/BatterStats';
import BullpenStats from './components/BullpenStats';
import './App.css';

function App() {
    const [teamId, setTeamId] = useState(null);
    const [pitcherId, setPitcherId] = useState(null);
    const [batterId, setBatterId] = useState(null);
    const [mode, setMode] = useState('pitchers'); // 'pitchers' o 'batters'

    // Limpiar selecciones cuando cambiamos de equipo para evitar datos cruzados
    useEffect(() => {
        setPitcherId(null);
        setBatterId(null);
    }, [teamId]);

    return (
        <div className="app-container">
            <header className="main-header">
                <div className="brand">
                    <span className="logo">⚾</span>
                    <h1>MoundMetrics <span className="version">v2.0</span></h1>
                </div>

                <nav className="mode-nav">
                    <button
                        className={`nav-btn ${mode === 'pitchers' ? 'active' : ''}`}
                        onClick={() => setMode('pitchers')}
                    >
                        Lanzadores
                    </button>
                    <button
                        className={`nav-btn ${mode === 'batters' ? 'active' : ''}`}
                        onClick={() => setMode('batters')}
                    >
                        Bateadores
                    </button>
                </nav>
            </header>

            <main className="dashboard-layout">
                {/* Barra superior de control */}
                <section className="control-bar">
                    <TeamSelector onSelectTeam={setTeamId} selectedTeamId={teamId} />
                </section>

                {!teamId ? (
                    <div className="welcome-screen">
                        <div className="welcome-card">
                            <h2>Bienvenido a MoundMetrics</h2>
                            <p>Selecciona un equipo para comenzar el análisis de rendimiento.</p>
                        </div>
                    </div>
                ) : (
                    <div className="data-view">
                        {mode === 'pitchers' ? (
                            <div className="pitcher-view-grid">
                                {/* Nueva sección: Resumen grupal del Bullpen */}
                                <div className="full-width-section">
                                    <BullpenStats teamId={teamId} />
                                </div>

                                <aside className="sidebar">
                                    <PitcherList
                                        teamId={teamId}
                                        selectedId={pitcherId}
                                        onSelectPitcher={setPitcherId}
                                    />
                                </aside>

                                <article className="main-detail">
                                    <PitcherWorkload pitcherId={pitcherId} />
                                </article>
                            </div>
                        ) : (
                            <div className="batter-view-grid">
                                <aside className="sidebar">
                                    <BatterList
                                        teamId={teamId}
                                        selectedId={batterId}
                                        onSelectBatter={setBatterId}
                                    />
                                </aside>

                                <article className="main-detail">
                                    <BatterStats batterId={batterId} />
                                </article>
                            </div>
                        )}
                    </div>
                )}
            </main>
        </div>
    );
}

export default App;