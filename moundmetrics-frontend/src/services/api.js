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

export const fetchTeams = () => fetchJson(`${API_BASE}/teams`);

export const fetchBullpenStats = (teamId, fromDate, toDate) => {
    const url = new URL(`${API_BASE}/teams/${teamId}/bullpen-stats`);
    if (fromDate) url.searchParams.append('from', fromDate);
    if (toDate) url.searchParams.append('to', toDate);
    return fetchJson(url);
};

export const fetchPitchersByTeam = (teamId) => fetchJson(`${API_BASE}/pitchers/team/${teamId}`);

export const fetchWorkload = (pitcherId, days = 7) => fetchJson(`${API_BASE}/pitchers/${pitcherId}/workload?days=${days}`);

export const fetchPitcherAppearances = (pitcherId, days = 7) => fetchJson(`${API_BASE}/pitchers/${pitcherId}/appearances?days=${days}`);

// ✅ Nueva función agregada
export const fetchPitcherStats = (pitcherId, days = 7) => fetchJson(`${API_BASE}/pitchers/${pitcherId}/stats?days=${days}`);