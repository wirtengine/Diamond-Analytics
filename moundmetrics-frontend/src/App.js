import React, { useState } from 'react';
import './App.css';
import TeamSelector from './components/TeamSelector';
import BullpenStats from './components/BullpenStats';
import PitcherList from './components/PitcherList';
import PitcherWorkload from './components/PitcherWorkload';

function App() {
  const [selectedTeamId, setSelectedTeamId] = useState(null);
  const [selectedPitcherId, setSelectedPitcherId] = useState(null);

  return (
      <div className="App">
        <header className="App-header">
          <h1>MoundMetrics</h1>
          <p>Análisis de Pitcheo MLB</p>
        </header>
        <main className="dashboard">
          <section className="team-section">
            <TeamSelector onSelectTeam={setSelectedTeamId} />
          </section>
          {selectedTeamId && (
              <>
                <section className="bullpen-section">
                  <BullpenStats teamId={selectedTeamId} />
                </section>
                <section className="pitchers-section">
                  <div className="pitcher-sidebar">
                    <PitcherList
                        teamId={selectedTeamId}
                        selectedPitcherId={selectedPitcherId}
                        onSelectPitcher={setSelectedPitcherId}
                    />
                  </div>
                  <div className="pitcher-detail">
                    {selectedPitcherId && <PitcherWorkload pitcherId={selectedPitcherId} />}
                  </div>
                </section>
              </>
          )}
        </main>
      </div>
  );
}

export default App;