const API_URL = 'http://localhost:8080/api/auth';

const register = async (userData) => {
    const response = await fetch(`${API_URL}/register`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(userData),
    });

    if (!response.ok) {
        const error = await response.text();
        throw new Error(error || 'Error en registro');
    }

    const data = await response.json();

    localStorage.setItem('user', JSON.stringify(data));

    return data;
};

const login = async (credentials) => {
    const response = await fetch(`${API_URL}/login`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(credentials),
    });

    if (!response.ok) {
        const error = await response.text();
        throw new Error(error || 'Error en login');
    }

    const data = await response.json();

    localStorage.setItem('user', JSON.stringify(data));

    return data;
};

const logout = () => {
    localStorage.removeItem('user');
};

const getCurrentUser = () => {
    return JSON.parse(localStorage.getItem('user'));
};

export default {
    register,
    login,
    logout,
    getCurrentUser,
};