import React, { useState } from 'react';
import axios from 'axios';
import { useNavigate } from 'react-router-dom';

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
            navigate('/');
            return;
        }

        // 2. Validação básica (garante que os campos não estão vazios)
        if (!numeroContaDestino || !valor) {
            setMensagem('Preencha o número da conta de destino e o valor.');
            return;
        }

        // 3. Montar o DTO da requisição (deve bater com o TransferenciaRequest.java)
        const transferenciaRequest = {
            numeroContaDestino: numeroContaDestino,
            valor: parseFloat(valor) // Converte o valor para número
        };

        try {
            // 4. Chamar o endpoint de transferência
            // POST /api/transacoes/transferir
            const response = await axios.post(`${API_URL}/transacoes/transferir`, transferenciaRequest, {
                headers: {
                    Authorization: `Bearer ${token}`,
                }
            });
            
            // 5. Sucesso
            setSucesso(true);
            // Mensagem do backend ou padrão
            setMensagem(`Transferência de ${parseFloat(valor).toLocaleString('pt-BR', { style: 'currency', currency: 'BRL' })} realizada com sucesso para a conta ${numeroContaDestino}!`);

            // Limpar formulário
            setNumeroContaDestino('');
            setValor('');
            
        } catch (error) {
            // 6. Tratamento de Erro
            console.error('Erro na transferência:', error);
            
            // Tenta obter a mensagem de erro do backend ou usa uma mensagem genérica
            const erroMsg = error.response?.data?.message || 'Erro ao realizar a transferência. Verifique os dados.';
            setMensagem(`Erro: ${erroMsg}`);

            // Se for 401, força o logout
            if (error.response?.status === 401) {
                localStorage.removeItem('bankmorning_token');
                navigate('/');
            }
        }
    };

    return (
        <div style={{ padding: '20px', maxWidth: '400px', margin: 'auto' }}>
            <h2>Realizar Transação (Transferência)</h2>
            <button onClick={() => navigate('/inicio')}>Voltar</button>

            <form onSubmit={handleSubmit} style={{ marginTop: '20px' }}>
                {/* Campo Número da Conta Destino */}
                <div style={{ marginBottom: '15px' }}>
                    <label>Número da Conta de Destino:</label>
                    <input
                        type="text"
                        value={numeroContaDestino}
                        onChange={(e) => setNumeroContaDestino(e.target.value)}
                        placeholder="Ex: 123456"
                        required
                        style={{ width: '100%', padding: '8px', boxSizing: 'border-box' }}
                    />
                </div>

                {/* Campo Valor */}
                <div style={{ marginBottom: '15px' }}>
                    <label>Valor (ex: 50.00):</label>
                    <input
                        type="number"
                        step="0.01"
                        value={valor}
                        onChange={(e) => setValor(e.target.value)}
                        placeholder="Ex: 50.00"
                        required
                        style={{ width: '100%', padding: '8px', boxSizing: 'border-box' }}
                    />
                </div>

                {/* Botão de Confirmação */}
                <button type="submit" style={{ padding: '10px 15px', backgroundColor: '#007bff', color: 'white', border: 'none', cursor: 'pointer' }}>
                    Confirmar Transferência
                </button>
            </form>

            {/* Mensagem de Feedback */}
            {mensagem && (
                <p style={{ marginTop: '15px', color: sucesso ? 'green' : 'red', fontWeight: 'bold' }}>
                    {mensagem}
                </p>
            )}
        </div>
    );
}

export default Transferencia;