import React, { useState } from 'react';
import { useNavigate, Link } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';

const Register = () => {
    const [username, setUsername] = useState('');
    const [email, setEmail] = useState('');
    const [password, setPassword] = useState('');
    const [acceptTerms, setAcceptTerms] = useState(false);
    const [error, setError] = useState('');
    const [loading, setLoading] = useState(false);
    const { register } = useAuth();
    const navigate = useNavigate();

    const handleSubmit = async (e) => {
        e.preventDefault();

        if (!acceptTerms) {
            setError('Debes aceptar los términos y condiciones de uso.');
            return;
        }

        setError('');
        setLoading(true);
        try {
            await register({ username, email, password });
            navigate('/dashboard');
        } catch (err) {
            setError(err.message || 'Error al procesar el registro. Verifica tus datos.');
        } finally {
            setLoading(false);
        }
    };

    return (
        <div className="login-layout">
            {/* Panel Izquierdo: Propuesta de valor para nuevos usuarios */}
            <div className="login-brand-panel">
                <div className="brand-content">
                    <div className="brand-logo-large">⚾ Diamond Analytics</div>
                    <h1 className="brand-headline">
                        Eleva tu nivel de juego con datos reales.
                    </h1>
                    <p className="brand-description">
                        Únete a la plataforma de inteligencia deportiva. Accede a modelos predictivos de la MLB, descubre oportunidades de valor (value bets) y gestiona tu bankroll como un profesional.
                    </p>
                    <div className="brand-stats-preview">
                        <div className="stat-pill">⚡ Setup en 1 minuto</div>
                        <div className="stat-pill">📊 Dashboard Personalizado</div>
                        <div className="stat-pill">🔒 Datos Seguros</div>
                    </div>
                </div>
                <div className="tech-overlay"></div>
            </div>

            {/* Panel Derecho: Formulario de Registro */}
            <div className="login-form-panel">
                <div className="auth-card">
                    <div className="mobile-logo">⚾ Diamond Analytics</div>

                    <div className="auth-header">
                        <h2>Solicitar Acceso</h2>
                        <p>Crea tu cuenta para acceder a la inteligencia de mercado.</p>
                    </div>

                    {error && (
                        <div className="error-message">
                            <span className="error-icon">⚠️</span>
                            {error}
                        </div>
                    )}

                    <form onSubmit={handleSubmit} className="auth-form">
                        <div className="form-group">
                            <label>Nombre de Usuario</label>
                            <input
                                type="text"
                                value={username}
                                onChange={(e) => setUsername(e.target.value)}
                                required
                                autoFocus
                                placeholder="ej. analistaMLB"
                                className={error && !username ? 'input-error' : ''}
                            />
                        </div>

                        <div className="form-group">
                            <label>Correo Electrónico</label>
                            <input
                                type="email"
                                value={email}
                                onChange={(e) => setEmail(e.target.value)}
                                required
                                placeholder="correo@ejemplo.com"
                                className={error && !email ? 'input-error' : ''}
                            />
                        </div>

                        <div className="form-group">
                            <label>Contraseña</label>
                            <input
                                type="password"
                                value={password}
                                onChange={(e) => setPassword(e.target.value)}
                                required
                                placeholder="Mínimo 8 caracteres"
                                className={error && !password ? 'input-error' : ''}
                            />
                        </div>

                        <div className="form-options terms-container">
                            <label className="remember-me">
                                <input
                                    type="checkbox"
                                    checked={acceptTerms}
                                    onChange={(e) => setAcceptTerms(e.target.checked)}
                                />
                                <span>Acepto los <a href="#" className="text-link">Términos de Servicio</a> y <a href="#" className="text-link">Políticas de Privacidad</a></span>
                            </label>
                        </div>

                        <button type="submit" className={`submit-btn ${loading ? 'loading' : ''}`} disabled={loading}>
                            {loading ? (
                                <>
                                    <span className="spinner-mini">⚾</span> Creando cuenta...
                                </>
                            ) : 'Crear Cuenta'}
                        </button>
                    </form>

                    <p className="auth-link">
                        ¿Ya tienes una cuenta? <Link to="/login">Inicia sesión aquí</Link>
                    </p>
                </div>
            </div>
        </div>
    );
};

export default Register;