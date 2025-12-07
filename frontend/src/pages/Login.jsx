// frontend/src/pages/Login.jsx
import React, { useState } from 'react';
import axios from 'axios';
import { useNavigate } from 'react-router-dom';

// *IMPORTANTE: Ajuste a porta se sua API não estiver rodando em 3000!*
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
      // CORREÇÃO: Mapeando os campos para o padrão do DTO Java: login (email) e senha (password)
      const response = await axios.post(`${API_URL}/auth/login`, { 
        login: email, 
        senha: password // Usando o nome 'senha' que o DTO Java espera
      });
      
      // =========================================================================
      // CORREÇÃO: LOG E VERIFICAÇÃO ROBUSTA DA RESPOSTA
      // =========================================================================
      console.log("Resposta completa da API:", response.data); 

      // Tenta extrair o token (seja de {token: '...'} ou de outras estruturas)
      const token = response.data.token || (typeof response.data === 'string' ? response.data : null); 
      
      if (token) {
        // Sucesso: Salvar o token e redirecionar
        localStorage.setItem('bankmorning_token', token);
        alert('Login bem-sucedido!');
        console.log("Token salvo e redirecionando para /inicio.");
        navigate('/inicio'); // Dispara a navegação
      } else {
        setError('Sucesso na autenticação, mas o token não foi encontrado na resposta da API.');
      }
      
    } catch (err) {
      // Falha: Mostrar mensagem de erro
      const errorMessage = err.response?.data?.message || 'Credenciais inválidas. Tente novamente.';
      setError(errorMessage);
    }
  };

  return (
    // ESTRUTURA SEM ESTILO (PARA TESTE DE FUNCIONALIDADE)
    <div style={{ padding: '20px', maxWidth: '400px', margin: 'auto', border: '1px solid black' }}>
      <h2>Bank Morning - Login</h2>
      <form onSubmit={handleSubmit} style={{ display: 'flex', flexDirection: 'column', gap: '10px' }}>
        <input 
          type="email" 
          value={email} 
          onChange={(e) => setEmail(e.target.value)} 
          placeholder="E-mail" 
          required 
        />
        <input 
          type="password" 
          value={password} 
          onChange={(e) => setPassword(e.target.value)} 
          placeholder="Senha" 
          required 
        />
        <button type="submit">
          Entrar
        </button>
      </form>

      {error && <p style={{ color: 'red' }}>{error}</p>}
      
      <p>
        Não tem conta? <button onClick={() => navigate('/cadastro')} style={{ background: 'none', border: 'none', color: 'blue', cursor: 'pointer', padding: 0 }}>Cadastre-se aqui</button>
      </p>
    </div>
 );
}

export default Login;