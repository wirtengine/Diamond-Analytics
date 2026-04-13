const API_BASE = 'http://localhost:8080/api';

const fetchJson = async (url, options = {}) => {
    try {
        const response = await fetch(url, {
            headers: {
                'Content-Type': 'application/json',
                ...options.headers,
            },
            ...options,
        });

        if (!response.ok) {
            const errorText = await response.text();
            throw new Error(errorText || 'Request failed');
        }

        return await response.json();
    } catch (error) {
        console.error('API ERROR:', error.message);
        throw error;
    }
};

// =======================
// TEAMS
// =======================
export const fetchTeams = () => fetchJson(`${API_BASE}/teams`);

// =======================
// BULLPEN
// =======================
export const fetchBullpenStats = (teamId, fromDate, toDate) => {
    const url = new URL(`${API_BASE}/teams/${teamId}/bullpen-stats`);
    if (fromDate) url.searchParams.append('from', fromDate);
    if (toDate) url.searchParams.append('to', toDate);
    return fetchJson(url);
};

// =======================
// PITCHERS
// =======================
export const fetchPitchersByTeam = (teamId) =>
    fetchJson(`${API_BASE}/pitchers/team/${teamId}`);

export const fetchWorkload = (pitcherId, days = 7) =>
    fetchJson(`${API_BASE}/pitchers/${pitcherId}/workload?days=${days}`);

export const fetchPitcherAppearances = (pitcherId, days = 7) =>
    fetchJson(`${API_BASE}/pitchers/${pitcherId}/appearances?days=${days}`);

export const fetchPitcherStats = (pitcherId, days = 7) =>
    fetchJson(`${API_BASE}/pitchers/${pitcherId}/stats?days=${days}`);

// =======================
// BATTERS (NUEVO)
// =======================

// Bateadores por equipo
export const fetchBattersByTeam = (teamId) => {
    return fetchJson(`${API_BASE}/batters/team/${teamId}`);
};

// Estadísticas de un bateador
export const fetchBatterStats = (batterId, days = 7) => {
    return fetchJson(`${API_BASE}/batters/${batterId}/stats?days=${days}`);
};

// Apariciones del bateador (para gráficos)
export const fetchBatterAppearances = (batterId, days = 7) => {
    return fetchJson(`${API_BASE}/batters/${batterId}/appearances?days=${days}`);
};