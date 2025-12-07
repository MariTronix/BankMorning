// frontend/src/pages/Deposito.jsx
import React, { useState } from 'react';
import axios from 'axios';
import { useNavigate } from 'react-router-dom';
import './Deposito.css'; // <-- Importe o novo CSS aqui!

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
            
            // Limpeza e navegação após sucesso
            setValor('');
            setNumeroContaDestino('');
            setTimeout(() => navigate('/inicio'), 1500); // Volta para a Dashboard após 1.5s
            

        } catch (error) {
            const erroMsg = error.response?.data?.message || 'Erro ao realizar depósito. Verifique os dados.';
            setMensagem(`Erro: ${erroMsg}`);
            console.error('Erro de depósito:', error);
        }
    };

    return (
        <div className="deposito-container"> 
            <div className="deposito-box">
                <h2>Depósito</h2>
                
                {/* Mensagem de Feedback com classes CSS */}
                {mensagem && (
                    <p className={mensagem.startsWith('Erro') ? 'message-error' : 'message-success'}>
                        {mensagem}
                    </p>
                )}
                
                <form onSubmit={handleSubmit} className="deposito-form">
                    <input
                        className="form-input"
                        type="text"
                        value={numeroContaDestino}
                        onChange={(e) => setNumeroContaDestino(e.target.value)}
                        placeholder="Número da Conta de Destino"
                        required
                    />
                    <input
                        className="form-input"
                        type="number"
                        value={valor}
                        onChange={(e) => setValor(e.target.value)}
                        placeholder="Valor (ex: 50.00)"
                        min="0.01"
                        step="0.01"
                        required
                    />
                    <button type="submit" className="confirm-button">
                        Confirmar Depósito
                    </button>
                </form>
                <button onClick={() => navigate('/inicio')} className="back-button">Voltar</button>
            </div>
        </div>
    );
}

export default Deposito;