import React, { useState } from 'react';
import axios from 'axios';
import { useNavigate } from 'react-router-dom';

const API_URL = 'http://localhost:8080/api';

function Deposito() {
    const [valor, setValor] = useState('');
    const [numeroContaDestino, setNumeroContaDestino] = useState('');
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

        try {
            // POST /api/transacoes/depositar
            const response = await axios.post(`${API_URL}/transacoes/depositar`, 
                { 
                    valor: parseFloat(valor), // Converter para número
                    numeroContaDestino: numeroContaDestino 
                },
                {
                    headers: {
                        Authorization: `Bearer ${token}`,
                        'Content-Type': 'application/json' 
                    }
                }
            );

            setMensagem(`Depósito de R$ ${valor.replace('.', ',')} realizado com sucesso!`);
            alert('Depósito realizado! Verifique seu saldo.');
            navigate('/inicio'); // Volta para a Dashboard para ver o saldo atualizado

        } catch (error) {
            const erroMsg = error.response?.data?.message || 'Erro ao realizar depósito. Verifique os dados.';
            setMensagem(`Erro: ${erroMsg}`);
            console.error('Erro de depósito:', error);
        }
    };

    return (
        <div style={{ padding: '20px', maxWidth: '400px', margin: 'auto', border: '1px solid black' }}>
            <h2>Realizar Depósito</h2>
            {mensagem && <p style={{ color: mensagem.startsWith('Erro') ? 'red' : 'green' }}>{mensagem}</p>}
            
            <form onSubmit={handleSubmit} style={{ display: 'flex', flexDirection: 'column', gap: '10px' }}>
                <input
                    type="text"
                    value={numeroContaDestino}
                    onChange={(e) => setNumeroContaDestino(e.target.value)}
                    placeholder="Número da Conta de Destino"
                    required
                />
                <input
                    type="number"
                    value={valor}
                    onChange={(e) => setValor(e.target.value)}
                    placeholder="Valor (ex: 50.00)"
                    min="0.01"
                    step="0.01"
                    required
                />
                <button type="submit">
                    Confirmar Depósito
                </button>
            </form>
            <button onClick={() => navigate('/inicio')} style={{ marginTop: '10px' }}>Voltar</button>
        </div>
    );
}

export default Deposito;