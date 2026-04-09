import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';
import './Login.css';

const Login = () => {
    const navigate = useNavigate();
    const { login } = useAuth();

    const [username, setUsername] = useState('');
    const [password, setPassword] = useState('');
    const [error, setError] = useState('');
    const [loading, setLoading] = useState(false);

    const handleSubmit = async (e) => {
        e.preventDefault();

        if (loading) return; // 🚫 evita doble submit

        setError('');
        setLoading(true);

        try {
            const result = await login({ username, password });

            if (!result.success) {
                setError(result.error || 'Credenciales inválidas');
                setLoading(false);
                return;
            }

            // ✅ redirección limpia
            navigate('/dashboard', { replace: true });

        } catch (err) {
            console.error("Error inesperado:", err);
            setError('Error del servidor. Intenta nuevamente.');
            setLoading(false);
        }
    };

    return (
        <div className="login-layout">
            {/* Panel Izquierdo */}
            <div className="login-brand-panel">
                <div className="tech-overlay"></div>

                <div className="brand-content">
                    <div className="brand-logo-large">⚾ Diamond Analytics</div>

                    <h1 className="brand-headline">
                        Domina el juego con datos inteligentes
                    </h1>

                    <p className="brand-description">
                        Analiza estadísticas, predice resultados y optimiza tus decisiones
                        con inteligencia artificial aplicada al béisbol.
                    </p>

                    <div className="brand-stats-preview">
                        <span className="stat-pill">+95% Precisión</span>
                        <span className="stat-pill">MLB Insights</span>
                        <span className="stat-pill">AI Predictions</span>
                    </div>
                </div>
            </div>

            {/* Panel Derecho */}
            <div className="login-form-panel">
                <div className="auth-card">

                    <div className="mobile-logo">⚾ Diamond Analytics</div>

                    <div className="auth-header">
                        <h2>Iniciar sesión</h2>
                        <p>Accede a tu dashboard</p>
                    </div>

                    {/* Error */}
                    {error && (
                        <div className="error-message">
                            ⚠️ {error}
                        </div>
                    )}

                    <form onSubmit={handleSubmit}>
                        <div className="form-group">
                            <label>Usuario</label>
                            <input
                                type="text"
                                value={username}
                                onChange={(e) => setUsername(e.target.value)}
                                required
                                className={error ? 'input-error' : ''}
                            />
                        </div>

                        <div className="form-group">
                            <label>Contraseña</label>
                            <input
                                type="password"
                                value={password}
                                onChange={(e) => setPassword(e.target.value)}
                                required
                                className={error ? 'input-error' : ''}
                            />
                        </div>

                        <button
                            type="submit"
                            className="submit-btn"
                            disabled={loading}
                        >
                            {loading ? (
                                <>
                                    <span className="spinner-mini">⏳</span>
                                    Iniciando sesión...
                                </>
                            ) : (
                                'Iniciar sesión'
                            )}
                        </button>
                    </form>

                    <div className="auth-link">
                        ¿No tienes cuenta?{' '}
                        <span
                            style={{ cursor: 'pointer' }}
                            onClick={() => navigate('/register')}
                        >
                            Regístrate
                        </span>
                    </div>

                </div>
            </div>
        </div>
    );
};

export default Login;