const API_BASE = 'http://localhost:8080/api';

const apiClient = async (endpoint, options = {}) => {
    const user = JSON.parse(localStorage.getItem('user'));

    const headers = {
        'Content-Type': 'application/json',
        ...(user?.token && {
            Authorization: `Bearer ${user.token}`
        }),
        ...options.headers
    };

    const response = await fetch(`${API_BASE}${endpoint}`, {
        ...options,
        headers
    });

    if (!response.ok) {
        if (response.status === 401) {
            localStorage.removeItem('user');
            window.location.href = '/login';
        }
        const errorText = await response.text();
        throw new Error(errorText || 'Error en la API');
    }

    // Intentar parsear como JSON, si falla devolver texto
    const contentType = response.headers.get('content-type');
    if (contentType && contentType.includes('application/json')) {
        return response.json();
    } else {
        return response.text();
    }
};

export default apiClient;