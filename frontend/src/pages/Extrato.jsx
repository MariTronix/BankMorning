import React, { useState, useEffect } from 'react';
import axios from 'axios';
import { useNavigate, Link } from 'react-router-dom';
import { IoLogOutOutline, IoHomeOutline, IoListOutline } from 'react-icons/io5';
import './Extrato.css';
import BankMorningLogo from '../assets/bank_morning_logo.png';

const API_URL = 'http://localhost:8080/api';

function Extrato() {
    const [transacoes, setTransacoes] = useState([]);
    const [isLoading, setIsLoading] = useState(true);
    const [mensagem, setMensagem] = useState('');
    const navigate = useNavigate();

    const handleLogout = () => {
        localStorage.removeItem('bankmorning_token');
        navigate('/');
    };

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
        return (
            <div className="app-layout">
                <div className="sidebar">
                    <div className="sidebar-header">
                        <img src={BankMorningLogo} alt="Logo" className="sidebar-logo" />
                        <h1>Bank Morning</h1>
                    </div>
                </div>
                <div className="extrato-content">
                    <div className="extrato-container">Carregando Extrato...</div>
                </div>
            </div>
        );
    }

    return (
        <div className="app-layout">
            {/* SIDEBAR */}
            <div className="sidebar">
                <div className="sidebar-header">
                    <img src={BankMorningLogo} alt="Logo" className="sidebar-logo" />
                    <h1>Bank Morning</h1>
                </div>
                
                <nav className="sidebar-nav-container">
                    <nav className="sidebar-nav">
                        <ul>
                            <li>
                                <Link to="/inicio">
                                    <IoHomeOutline size={20} />
                                    <span>Página Inicial</span>
                                </Link>
                            </li>
                            <li>
                                <Link to="/extrato" className="active">
                                    <IoListOutline size={20} />
                                    <span>Ver Extrato</span>
                                </Link>
                            </li>
                        </ul>
                    </nav>
                </nav>
                
                <button className="logout-button" onClick={handleLogout}>
                    <IoLogOutOutline size={18} />
                    <span className="logout-text">Sair da Conta</span>
                </button>
            </div>

            {/* CONTEÚDO DO EXTRATO */}
            <div className="extrato-content">
                <div className="extrato-container">
                    <h2>Extrato da Conta</h2>
                    <button onClick={() => navigate('/inicio')} className="back-button">
                        Voltar
                    </button>

                    {mensagem && <p className="error-message">{mensagem}</p>}
                    
                    {transacoes.length === 0 ? (
                        <p>Nenhuma transação encontrada.</p>
                    ) : (
                        <div className="transaction-table-wrapper">
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
                                        <td>{t.descricao || '-'}</td>
                                        <td className={`amount-cell ${getAmountClass(t.tipoDeTransacao)}`}>
                                            {getAmountClass(t.tipoDeTransacao) === 'amount-debit' ? '- ' : '+ '}
                                            {formatarValor(t.valor)}
                                        </td>
                                    </tr>
                                ))}
                                </tbody>
                            </table>
                        </div>
                    )}
                </div>
            </div>
        </div>
    );
}

export default Extrato;