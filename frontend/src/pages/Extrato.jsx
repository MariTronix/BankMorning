// frontend/src/pages/Extrato.jsx
import React, { useState, useEffect } from 'react';
import axios from 'axios';
import { useNavigate } from 'react-router-dom';
import './Extrato.css'; // <-- Importe o novo CSS aqui!

const API_URL = 'http://localhost:8080/api';

function Extrato() {
    const [transacoes, setTransacoes] = useState([]);
    const [isLoading, setIsLoading] = useState(true);
    const [mensagem, setMensagem] = useState('');
    const navigate = useNavigate();

    useEffect(() => {
        const token = localStorage.getItem('bankmorning_token');
        if (!token) {
            navigate('/');
            return;
        }

        const fetchExtrato = async () => {
            try {
                const response = await axios.get(`${API_URL}/transacoes/extrato`, {
                    headers: { Authorization: `Bearer ${token}` }
                });
                
                setTransacoes(response.data);
                
            } catch (error) {
                console.error('Erro ao buscar extrato:', error);
                const erroMsg = error.response?.data?.message || 'Erro ao carregar extrato.';
                setMensagem(`Erro: ${erroMsg}`);

                if (error.response?.status === 401) {
                    localStorage.removeItem('bankmorning_token');
                    navigate('/');
                }
            } finally {
                setIsLoading(false);
            }
        };
        fetchExtrato();
    }, [navigate]);

    const formatarData = (data) => {
        return new Date(data).toLocaleString('pt-BR');
    };

    const formatarValor = (valor) => {
        return parseFloat(valor).toLocaleString('pt-BR', { style: 'currency', currency: 'BRL' });
    };

    // Função para determinar a classe CSS (Crédito ou Débito)
    const getAmountClass = (tipo) => {
        const debitTypes = ['SAQUE', 'TRANSFERENCIA_ENVIADA'];
        const creditTypes = ['DEPOSITO', 'TRANSFERENCIA_RECEBIDA'];

        if (debitTypes.includes(tipo)) {
            return 'amount-debit';
        }
        if (creditTypes.includes(tipo)) {
            return 'amount-credit';
        }
        return '';
    };

    if (isLoading) {
        return <div className="extrato-container">Carregando Extrato...</div>;
    }

    return (
        <div className="extrato-container">
            <h2>Extrato da Conta</h2>
            <button onClick={() => navigate('/inicio')} className="back-button">Voltar</button>

            {mensagem && <p className="error-message">{mensagem}</p>}
            
            {transacoes.length === 0 ? (
                <p style={{ marginTop: '20px' }}>Nenhuma transação encontrada.</p>
            ) : (
                <table className="transaction-table">
                    <thead>
                        <tr>
                            <th>Data</th>
                            <th>Tipo</th>
                            <th>Descrição</th>
                            <th>Valor</th>
                        </tr>
                    </thead>
                    <tbody>
                    {transacoes.map((t, index) => (
                        <tr key={index}>
                            <td>{formatarData(t.dataTransacao)}</td>
                            <td>{t.tipoDeTransacao}</td>
                            <td>{t.descricao || '-'}</td> {/* Adicionado campo Descrição, se existir */}
                            <td className={`amount-cell ${getAmountClass(t.tipoDeTransacao)}`}>
                                {/* Aplica o sinal de + ou - baseado no tipo, se necessário */}
                                {getAmountClass(t.tipoDeTransacao) === 'amount-debit' ? '-' : '+'}
                                {formatarValor(t.valor)}
                            </td>
                        </tr>
                    ))}
                    </tbody>
                </table>
            )}
        </div>
    );
}

export default Extrato;