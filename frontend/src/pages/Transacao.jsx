// frontend/src/pages/Transferencia.jsx
import React, { useState } from 'react';
import axios from 'axios';
import { useNavigate } from 'react-router-dom';
import './Transacao.css';

// URL base da sua API
const API_URL = 'http://localhost:8080/api';

function Transferencia() {
    // Estados para os campos do formulário
    const [numeroContaDestino, setNumeroContaDestino] = useState('');
    const [valor, setValor] = useState('');
    
    // Estados para feedback do usuário
    const [mensagem, setMensagem] = useState('');
    const [sucesso, setSucesso] = useState(false);
    
    // Hook para navegação
    const navigate = useNavigate();

    // Função para lidar com o envio do formulário
    const handleSubmit = async (e) => {
        e.preventDefault();
        setMensagem('');
        setSucesso(false);

        // 1. Obter o token
        const token = localStorage.getItem('bankmorning_token');
        if (!token) {
            setMensagem('Erro: Usuário não autenticado.');
            // Navegação imediata removida, a mensagem é mostrada primeiro
            return;
        }

        // 2. Validação básica (garante que os campos não estão vazios)
        if (!numeroContaDestino || !valor) {
            setMensagem('Erro: Preencha o número da conta de destino e o valor.');
            return;
        }

        // 3. Montar o DTO da requisição (deve bater com o TransferenciaRequest.java)
        const transferenciaRequest = {
            numeroContaDestino: numeroContaDestino,
            valor: parseFloat(valor) // Converte o valor para número
        };

        try {
            // 4. Chamar o endpoint de transferência
            const response = await axios.post(`${API_URL}/transacoes/transferir`, transferenciaRequest, {
                headers: { Authorization: `Bearer ${token}` }
            });
            
            // 5. Sucesso
            setSucesso(true);
            
            // Mensagem de sucesso
            setMensagem(`Transferência de ${parseFloat(valor).toLocaleString('pt-BR', { style: 'currency', currency: 'BRL' })} realizada com sucesso!`);

            // Limpar formulário e voltar após um pequeno delay
            setNumeroContaDestino('');
            setValor('');
            setTimeout(() => navigate('/inicio'), 1500);
            
        } catch (error) {
            // 6. Tratamento de Erro
            console.error('Erro na transferência:', error);
            
            const erroMsg = error.response?.data?.message || 'Erro ao realizar a transferência. Verifique os dados.';
            setMensagem(`Erro: ${erroMsg}`);

            // Se for 401, forçar o logout
            if (error.response?.status === 401) {
                localStorage.removeItem('bankmorning_token');
                navigate('/');
            }
        }
    };

    return (
        <div className="transferencia-container">
            <div className="transferencia-box">
                <h2>Transferência</h2>
                
                {/* Mensagem de Feedback */}
                {mensagem && (
                    <p className={mensagem.startsWith('Erro') ? 'message-error' : 'message-success'}>
                        {mensagem}
                    </p>
                )}

                <form onSubmit={handleSubmit} className="transferencia-form">
                    
                    {/* Campo Número da Conta Destino */}
                    <div className="form-group-transfer">
                        <label>Número da Conta de Destino:</label>
                        <input
                            className="form-input"
                            type="text"
                            value={numeroContaDestino}
                            onChange={(e) => setNumeroContaDestino(e.target.value)}
                            placeholder="Ex: 123456"
                            required
                        />
                    </div>

                    {/* Campo Valor */}
                    <div className="form-group-transfer">
                        <label>Valor (ex: 50.00):</label>
                        <input
                            className="form-input"
                            type="number"
                            step="0.01"
                            value={valor}
                            onChange={(e) => setValor(e.target.value)}
                            placeholder="Ex: 50.00"
                            required
                        />
                    </div>

                    {/* Botão de Confirmação */}
                    <button type="submit" className="confirm-transfer-button">
                        Confirmar Transferência
                    </button>
                </form>

                {/* Botão Voltar */}
                <button onClick={() => navigate('/inicio')} className="back-button">Voltar</button>
            </div>
        </div>
    );
}

export default Transferencia;