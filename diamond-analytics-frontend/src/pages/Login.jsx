import React, { useState } from 'react';
import { useNavigate, Link } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';

const Login = () => {
    const [username, setUsername] = useState('');
    const [password, setPassword] = useState('');
    const [error, setError] = useState('');
    const [loading, setLoading] = useState(false);
    const { login } = useAuth();
    const navigate = useNavigate();

    const handleSubmit = async (e) => {
        e.preventDefault();
        setError('');
        setLoading(true);
        // Simulamos un pequeño retraso de red si no lo hay,
        // para que la transición se sienta "procesando datos"
        try {
            await login({ username, password });
            navigate('/dashboard');
        } catch (err) {
            setError(err.message || 'Credenciales incorrectas. Verifica tus datos.');
        } finally {
            setLoading(false);
        }
    };

    return (
        <div className="login-layout">
            {/* Panel Izquierdo: Branding y Visión (Estilo Fintech) */}
            <div className="login-brand-panel">
                <div className="brand-content">
                    <div className="brand-logo-large">⚾ Diamond Analytics</div>
                    <h1 className="brand-headline">
                        Inteligencia deportiva para decisiones precisas.
                    </h1>
                    <p className="brand-description">
                        Plataforma avanzada de análisis predictivo y gestión de value bets para la MLB.
                        Optimiza tu ROI con modelos matemáticos y la potencia de la Inteligencia Artificial.
                    </p>
                    <div className="brand-stats-preview">
                        <div className="stat-pill">📈 Algoritmos Predictivos</div>
                        <div className="stat-pill">🤖 Análisis IA (Gemini)</div>
                        <div className="stat-pill">💰 Detección de Value Bets</div>
                    </div>
                </div>
                {/* Efecto de fondo tecnológico (opcional) */}
                <div className="tech-overlay"></div>
            </div>

            {/* Panel Derecho: Formulario Limpio */}
            <div className="login-form-panel">
                <div className="auth-card">
                    <div className="mobile-logo">⚾ Diamond Analytics</div>

                    <div className="auth-header">
                        <h2>Acceso a la plataforma</h2>
                        <p>Ingresa tus credenciales para acceder al dashboard.</p>
                    </div>

                    {error && (
                        <div className="error-message">
                            <span className="error-icon">⚠️</span>
                            {error}
                        </div>
                    )}

                    <form onSubmit={handleSubmit} className="auth-form">
                        <div className="form-group">
                            <label>Usuario / Correo</label>
                            <input
                                type="text"
                                value={username}
                                onChange={(e) => setUsername(e.target.value)}
                                required
                                autoFocus
                                placeholder="tuusuario"
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
                                placeholder="••••••••"
                                className={error ? 'input-error' : ''}
                            />
                        </div>

                        <div className="form-options">
                            <label className="remember-me">
                                <input type="checkbox" /> Recordarme
                            </label>
                            <a href="#" className="forgot-password">¿Olvidaste tu contraseña?</a>
                        </div>

                        <button type="submit" className={`submit-btn ${loading ? 'loading' : ''}`} disabled={loading}>
                            {loading ? (
                                <>
                                    <span className="spinner-mini">⚾</span> Procesando...
                                </>
                            ) : 'Iniciar Sesión'}
                        </button>
                    </form>

                    <p className="auth-link">
                        ¿No tienes una cuenta? <Link to="/register">Solicita acceso aquí</Link>
                    </p>
                </div>
            </div>
        </div>
    );
};

export default Login;