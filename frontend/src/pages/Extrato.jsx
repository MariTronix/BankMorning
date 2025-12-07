import React, { useState, useEffect } from 'react';
import axios from 'axios';
import { useNavigate } from 'react-router-dom';

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
                // GET /api/transacoes/extrato
                const response = await axios.get(`${API_URL}/transacoes/extrato`, {
                    headers: {
                        Authorization: `Bearer ${token}`,
                    }
                });
                
                setTransacoes(response.data);
                
            } catch (error) {
                console.error('Erro ao buscar extrato:', error);
                const erroMsg = error.response?.data?.message || 'Erro ao carregar extrato.';
                setMensagem(`Erro: ${erroMsg}`);

                // Em caso de 401, forçar logout
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

    if (isLoading) {
        return <div style={{ padding: '20px' }}>Carregando Extrato...</div>;
    }

    return (
        <div style={{ padding: '20px', maxWidth: '600px', margin: 'auto' }}>
            <h2>Extrato da Conta</h2>
            <button onClick={() => navigate('/inicio')}>Voltar</button>

            {mensagem && <p style={{ color: 'red', marginTop: '10px' }}>{mensagem}</p>}
            
            {transacoes.length === 0 ? (
                <p style={{ marginTop: '20px' }}>Nenhuma transação encontrada.</p>
            ) : (
                <ul style={{ listStyleType: 'none', padding: 0, marginTop: '20px' }}>
                    {transacoes.map((t, index) => (
                        <li key={index} style={{ border: '1px solid #ccc', marginBottom: '10px', padding: '10px' }}>
                            {/* CORREÇÃO: Usar o nome exato da propriedade enviada pelo back-end (t.tipoDeTransacao ou t.tipo) */}
                            <p><strong>Tipo:</strong> {t.tipoDeTransacao}</p> 
                            <p><strong>Valor:</strong> {formatarValor(t.valor)}</p>
                            <p><strong>Data:</strong> {formatarData(t.dataTransacao)}</p>
                            {/* ... */}
                        </li>
                    ))}
                </ul>
            )}
        </div>
    );
}

export default Extrato;