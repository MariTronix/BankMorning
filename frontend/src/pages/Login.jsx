// frontend/src/pages/Login.jsx

import React, { useState } from 'react';
import axios from 'axios';
import { useNavigate } from 'react-router-dom';
import './Login.css'; 
import BankMorningLogo from '../assets/bank_morning_logo.png'; 

const API_URL = 'http://localhost:8080/api'; 

function Login() {
    const [email, setEmail] = useState('');
    const [password, setPassword] = useState('');
    const [error, setError] = useState('');
    const navigate = useNavigate();

    const handleSubmit = async (e) => {
        e.preventDefault();
        setError('');
        try {
            const response = await axios.post(`${API_URL}/auth/login`, { login: email, senha: password });
            const token = response.data.token || (typeof response.data === 'string' ? response.data : null); 
            if (token) {
                localStorage.setItem('bankmorning_token', token);
                navigate('/inicio'); 
            } else {
                setError('Token não encontrado.');
            }
        } catch (err) {
            const errorMessage = err.response?.data?.message || 'Credenciais inválidas.';
            setError(errorMessage);
        }
    };

    return (
        <div className="login-split-container"> 
            <div className="visual-area-login">

            </div>

            <div className="form-area">
                <div className="login-box"> 
                    
                    {/* Logo menor no topo do formulário */}
                    <img 
                        src={BankMorningLogo} 
                        alt="Bank Morning Logo" 
                        className="login-logo-small" 
                    />
                    
                    <h2 className="login-title">Acesse sua conta</h2>
                    
                    <form onSubmit={handleSubmit} className="login-form">
                        
                        <div className="form-group"> 
                            <label htmlFor="email">E-mail:</label>
                            <input 
                                type="email" 
                                value={email} 
                                onChange={(e) => setEmail(e.target.value)} 
                                placeholder="E-mail" 
                                id="email"
                                required 
                                className="form-input" 
                            />
                        </div>

                        <div className="form-group">
                            <label htmlFor="password">Senha:</label>
                            <input 
                                type="password" 
                                id="password"
                                value={password} 
                                onChange={(e) => setPassword(e.target.value)} 
                                placeholder="Senha" 
                                required 
                                className="form-input" 
                            />
                        </div>
                        
                        <button type="submit" className="login-button">
                            Entrar
                        </button>
                    </form>

                    {error && 
                      <p className="login-message error">
                        {error}
                      </p>
                    }
                    
                    <p className="register-link">
                        Não tem conta? 
                        <button onClick={() => navigate('/cadastro')}>
                            Cadastre-se aqui
                        </button>
                    </p>
                </div>
            </div>
        </div>
    );
}

export default Login;