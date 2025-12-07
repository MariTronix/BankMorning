// frontend/src/pages/Register.jsx
import React, { useState } from 'react';
import axios from 'axios';
import { useNavigate } from 'react-router-dom';

const API_URL = 'http://localhost:8080/api'; 
// Lembre-se: O Controller Java deve ter @PostMapping("/register")

function Register() {
    const [name, setName] = useState('');
    const [email, setEmail] = useState('');
    const [cpf, setCpf] = useState(''); // Estado para CPF
    const [birthDate, setBirthDate] = useState(''); // Estado para Data de Nascimento
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

        // 1. Remove a máscara do CPF (deixa apenas dígitos)
        const cleanCpf = cpf.replace(/[.\-]/g, '');

        const registrationData = {
            nome: name,
            email: email,
            cpf: cleanCpf, // Envia apenas dígitos, conforme padrão do validador @CPF
            dataNascimento: birthDate, // Deve corresponder ao nome do campo no DTO Java (corrigido para camelCase)
            senha: password 
        };

        try {
            // Endpoint de Cadastro: POST /auth/register
            const response = await axios.post(`${API_URL}/auth/register`, registrationData);
            
            // Sucesso
            alert('Cadastro realizado com sucesso! Faça login para continuar.');
            navigate('/'); 
            
        } catch (err) {
            // Se o backend retornar um erro 500 ou 400
            const errorMessage = err.response?.data?.message || 'Erro ao registrar. Verifique os dados e tente outro e-mail.';
            setError(errorMessage);
            console.error('Erro de Cadastro:', err.response?.data || err.message);
        }
    };

    return (
        // ESTRUTURA SEM ESTILO (PARA TESTE DE FUNCIONALIDADE)
        <div style={{ padding: '20px', maxWidth: '400px', margin: 'auto', border: '1px solid black' }}>
            <h2>Bank Morning - Cadastro</h2>
            <form onSubmit={handleSubmit} style={{ display: 'flex', flexDirection: 'column', gap: '10px' }}>
                <input type="text" value={name} onChange={(e) => setName(e.target.value)} placeholder="Nome Completo" required />
                <input type="email" value={email} onChange={(e) => setEmail(e.target.value)} placeholder="E-mail" required />
                <input type="text" value={cpf} onChange={(e) => setCpf(e.target.value)} placeholder="CPF (Apenas números)" required />
                <label style={{ fontSize: '12px', color: '#666' }}>Data de Nascimento:</label>
                <input type="date" value={birthDate} onChange={(e) => setBirthDate(e.target.value)} required />
                <input type="password" value={password} onChange={(e) => setPassword(e.target.value)} placeholder="Senha" required />
                <input type="password" value={confirmPassword} onChange={(e) => setConfirmPassword(e.target.value)} placeholder="Confirmar Senha" required />
                
                <button type="submit" style={{ padding: '10px', backgroundColor: '#FF7F00', color: 'white', border: 'none', cursor: 'pointer' }}>
                    Cadastrar
                </button>
            </form>

            {error && <p style={{ color: 'red' }}>{error}</p>}
            
            <p style={{ marginTop: '10px' }}>
                Já tem conta? <button onClick={() => navigate('/')} style={{ background: 'none', border: 'none', color: 'blue', cursor: 'pointer', padding: 0 }}>Faça Login</button>
            </p>
        </div>
    );
}

export default Register;