import apiClient from './apiClient';

const ingestGames = async () => {
    return apiClient('/admin/ingest', { method: 'POST' });
};

const adminService = {
    ingestGames,
};

export default adminService;