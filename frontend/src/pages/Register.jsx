// frontend/src/pages/Register.jsx

import React, { useState } from 'react';
import axios from 'axios';
import { useNavigate } from 'react-router-dom';
import './Register.css'; 
import BankMorningLogo from '../assets/bank_morning_logo.png'; 
import VisualBackgroundImg from '../assets/cadastro.jpg'; 

const API_URL = 'http://localhost:8080/api'; 
// Lembre-se: O Controller Java deve ter @PostMapping("/register")

function Register() {
    const [name, setName] = useState('');
    const [email, setEmail] = useState('');
    const [cpf, setCpf] = useState(''); 
    const [birthDate, setBirthDate] = useState(''); 
    const [password, setPassword] = useState('');
    const [confirmPassword, setConfirmPassword] = useState('');
    const [error, setError] = useState('');
    const navigate = useNavigate();

    const handleSubmit = async (e) => {
        e.preventDefault();
        setError('');

        if (password !== confirmPassword) {
            setError('A senha e a confirmação de senha não coincidem.');
            return;
        }

        // Limpar o CPF para enviar apenas dígitos
        const cleanCpf = cpf.replace(/[.\-]/g, '');

        const registrationData = {
            nome: name,
            email: email,
            cpf: cleanCpf,
            dataNascimento: birthDate,
            senha: password 
        };

        try {
            const response = await axios.post(`${API_URL}/usuarios/cadastrar`, registrationData);
            
            alert('Cadastro realizado com sucesso! Faça login para continuar.');
            navigate('/');
            
        } catch (err) {
            const errorMessage = err.response?.data?.message || 'Erro ao registrar. Verifique os dados e tente outro e-mail.';
            setError(errorMessage);
            console.error('Erro de Cadastro:', err.response?.data || err.message);
        }
    };

    return (
       <div className="register-split-container"> 
            <div className="visual-area-cadastro">
                <div className="visual-content">
                    
                </div>
            </div>

            {/* Coluna 2: Área de Formulário (Ocupa 40% da tela) */}
            <div className="form-area">
                <div className="register-box"> 
                    
                    <img 
                        src={BankMorningLogo} 
                        alt="Bank Morning Logo" 
                        className="register-logo-small" 
                    />
                    
                    <h2 className="register-title">Criar sua Conta</h2>
                    
                    {/* Formulário de Cadastro */}
                    <form onSubmit={handleSubmit} className="register-form">
                        
                        {/* 1. Nome Completo */}
                        <div className="form-group"> 
                            <label htmlFor="nome">Nome Completo:</label>
                            <input type="text" id="nome" value={name} onChange={(e) => setName(e.target.value)} 
                                required className="form-input" placeholder="Seu nome"
                            />
                        </div>

                        {/* 2. E-mail */}
                        <div className="form-group"> 
                            <label htmlFor="email">E-mail:</label>
                            <input type="email" id="email" value={email} onChange={(e) => setEmail(e.target.value)} 
                                required className="form-input" placeholder="seu.email@exemplo.com"
                            />
                        </div>

                        {/* 3. CPF */}
                        <div className="form-group"> 
                            <label htmlFor="cpf">CPF (Apenas números):</label>
                            <input type="text" id="cpf" value={cpf} onChange={(e) => setCpf(e.target.value)} 
                                required className="form-input" maxLength="14" placeholder="000.000.000-00"
                            />
                        </div>

                        {/* 4. Data de Nascimento */}
                        <div className="form-group"> 
                            <label htmlFor="birthDate">Data de Nascimento:</label>
                            <input type="date" id="birthDate" value={birthDate} onChange={(e) => setBirthDate(e.target.value)} 
                                required className="form-input"
                            />
                        </div>

                        {/* 5. Senha */}
                        <div className="form-group"> 
                            <label htmlFor="password">Senha:</label>
                            <input type="password" id="password" value={password} onChange={(e) => setPassword(e.target.value)} 
                                required className="form-input" placeholder="Mínimo 8 caracteres"
                            />
                        </div>

                        {/* 6. Confirmar Senha */}
                        <div className="form-group"> 
                            <label htmlFor="confirmPassword">Confirmar Senha:</label>
                            <input type="password" id="confirmPassword" value={confirmPassword} onChange={(e) => setConfirmPassword(e.target.value)} 
                                required className="form-input"
                            />
                        </div>
                        
                        {/* Botão de Cadastro */}
                        <button type="submit" className="register-button">
                            Cadastrar
                        </button>
                    </form>

                    {error && 
                        <p className="login-message error">
                            {error}
                        </p>
                    }
                    
                    {/* Link para Login */}
                    <p className="login-link">
                        Já tem conta? 
                        <button onClick={() => navigate('/')}>
                            Faça Login
                        </button>
                    </p>
                </div>
            </div>
        </div>
    );
}

export default Register;