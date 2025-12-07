// frontend/src/pages/Saque.jsx
import React, { useState } from 'react';
import axios from 'axios';
import { useNavigate } from 'react-router-dom';
import './Saque.css'; // <-- Importe o novo CSS aqui!

const API_URL = 'http://localhost:8080/api';

function Saque() {
    const [valor, setValor] = useState('');
    const [mensagem, setMensagem] = useState('');
    const navigate = useNavigate();

    // Estado para verificar se foi sucesso (para a cor da mensagem)
    const [sucesso, setSucesso] = useState(false); 

    const handleSubmit = async (e) => {
        e.preventDefault();
        setMensagem('');
        setSucesso(false);
        
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

            // Sucesso
            setSucesso(true);
            setMensagem(`Saque de R$ ${valor.replace('.', ',')} realizado com sucesso!`);
            setValor(''); // Limpa o campo
            
            setTimeout(() => navigate('/inicio'), 1500); // Volta para a Dashboard

        } catch (error) {
            const erroMsg = error.response?.data?.message || 'Erro ao realizar saque. Verifique o saldo ou o valor.';
            setMensagem(`Erro: ${erroMsg}`);
            console.error('Erro de saque:', error);
        }
    };

    return (
        <div className="saque-container">
            <div className="saque-box">
                <h2>Saque</h2>
                
                {/* Mensagem de Feedback com classes CSS */}
                {mensagem && (
                    <p className={sucesso ? 'message-success' : 'message-error'}>
                        {mensagem.startsWith('Erro') ? mensagem : `Sucesso: ${mensagem}`}
                    </p>
                )}

                <form onSubmit={handleSubmit} className="saque-form">
                    <div className="form-group-saque">
                        <label>Valor a Sacar:</label>
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
                    </div>
                    <button type="submit" className="confirm-saque-button">
                        Confirmar Saque
                    </button>
                </form>
                <button onClick={() => navigate('/inicio')} className="back-button">Voltar</button>
            </div>
        </div>
    );
}

export default Saque;