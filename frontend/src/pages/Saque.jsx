import React, { useState } from 'react';
import axios from 'axios';
import { useNavigate } from 'react-router-dom';

const API_URL = 'http://localhost:8080/api';

function Saque() {
    const [valor, setValor] = useState('');
    const [mensagem, setMensagem] = useState('');
    const navigate = useNavigate();

    const handleSubmit = async (e) => {
        e.preventDefault();
        setMensagem('');
        
        const token = localStorage.getItem('bankmorning_token');
        if (!token) {
            setMensagem('Erro: Não autenticado.');
            navigate('/');
            return;
        }

        const saqueRequest = {
            valor: parseFloat(valor) // Converte para número
        };

        try {
            // POST /api/transacoes/sacar
            const response = await axios.post(`${API_URL}/transacoes/sacar`, saqueRequest, {
                headers: {
                    Authorization: `Bearer ${token}`,
                    'Content-Type': 'application/json' 
                }
            });

            setMensagem(`Saque de R$ ${valor.replace('.', ',')} realizado com sucesso!`);
            alert('Saque realizado! Verifique seu saldo e extrato.');
            navigate('/inicio'); // Volta para a Dashboard

        } catch (error) {
            // Se for erro 400 (Bad Request), a mensagem deve vir do Service (Saldo Insuficiente)
            const erroMsg = error.response?.data?.message || 'Erro ao realizar saque. Verifique o saldo ou o valor.';
            setMensagem(`Erro: ${erroMsg}`);
            console.error('Erro de saque:', error);
        }
    };

    return (
        <div style={{ padding: '20px', maxWidth: '400px', margin: 'auto', border: '1px solid black' }}>
            <h2>Realizar Saque</h2>
            {mensagem && <p style={{ color: mensagem.startsWith('Erro') ? 'red' : 'green' }}>{mensagem}</p>}
            
            <form onSubmit={handleSubmit} style={{ display: 'flex', flexDirection: 'column', gap: '10px' }}>
                <input
                    type="number"
                    value={valor}
                    onChange={(e) => setValor(e.target.value)}
                    placeholder="Valor a Sacar"
                    min="0.01"
                    step="0.01"
                    required
                />
                <button type="submit">
                    Confirmar Saque
                </button>
            </form>
            <button onClick={() => navigate('/inicio')} style={{ marginTop: '10px' }}>Voltar</button>
        </div>
    );
}

export default Saque;