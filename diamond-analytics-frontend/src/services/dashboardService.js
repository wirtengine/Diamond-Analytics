import apiClient from './apiClient';

const getSummary = () => {
    return apiClient('/dashboard/summary');
};

export default {
    getSummary
};