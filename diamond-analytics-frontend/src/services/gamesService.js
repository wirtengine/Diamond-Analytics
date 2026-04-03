// src/services/gamesService.js
import apiClient from './apiClient';

const getTodaysGames = async () => {
    return apiClient('/games/today');
};

const getGamesByDate = async (date) => {
    return apiClient(`/games/date?date=${date}`);
};

const gamesService = {
    getTodaysGames,
    getGamesByDate,
};

export default gamesService;