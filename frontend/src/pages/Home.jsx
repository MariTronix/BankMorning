import React, { useState, useEffect } from 'react';
import axios from 'axios';
import { useNavigate, Link } from 'react-router-dom';
// Ícones
import { IoEyeOutline, IoEyeOffOutline, IoLogOutOutline, IoHomeOutline, IoListOutline } from 'react-icons/io5'; 
import { RiBankLine, RiSwapBoxLine, RiHistoryLine, RiWalletLine } from 'react-icons/ri'; 

import './Home.css'; // <--- IMPORTAÇÃO DO CSS
import BankMorningLogo from '../assets/bank_morning_logo01.png'; 

const API_URL = 'http://localhost:8080/api'; 

function Home() {
    // ESTADOS
    const [userName, setUserName] = useState(null); 
    const [balance, setBalance] = useState(null);
    const [numeroConta, setNumeroConta] = useState(null); 
    const [showBalance, setShowBalance] = useState(true);
    const [isLoading, setIsLoading] = useState(true);
    const [recentTransactions, setRecentTransactions] = useState([]); 
    const navigate = useNavigate();

    // Função auxiliar para extrair o primeiro nome
    const getFirstName = (fullName) => {
        if (!fullName) return '';
        return fullName.split(' ')[0];
    };

    // Função para formatar o saldo para R$
    const formatBalance = (value) => {
        if (value === null) return 'R$ --,--';
        return parseFloat(value).toLocaleString('pt-BR', { style: 'currency', currency: 'BRL' });
    };

    // Função para formatar o valor da transação com sinal
    const formatTransactionAmount = (valor, tipo) => {
        const value = parseFloat(valor);
        const formatted = value.toLocaleString('pt-BR', { style: 'currency', currency: 'BRL' });
        
        // Tipos de débito que subtraem o saldo
        const debitTypes = ['SAQUE', 'TRANSFERENCIA_ENVIADA'];
        const prefix = debitTypes.includes(tipo) ? '-' : '+';

        return `${prefix} ${formatted}`;
    };

    // Função para determinar a classe CSS (Crédito ou Débito)
    const getTransactionClass = (tipo) => {
        const debitTypes = ['SAQUE', 'TRANSFERENCIA_ENVIADA'];
        return debitTypes.includes(tipo) ? 'amount-debit' : 'amount-credit';
    };

    // Função de Logout
    const handleLogout = () => {
        localStorage.removeItem('bankmorning_token');
        navigate('/');
    };

    // Efeito para buscar TODOS os detalhes (Conta, Nome e Extrato)
    useEffect(() => {
        const fetchDetails = async () => {
            const token = localStorage.getItem('bankmorning_token');
            if (!token) {
                navigate('/'); 
                return;
            }

            const config = {
                headers: { Authorization: `Bearer ${token}` }
            };
            
            try {
                // 1. Buscar Dados da Conta (Saldo, Número, Nome)
                const accountResponse = await axios.get(`${API_URL}/account/detalhes`, config);
                
                setBalance(accountResponse.data.saldo); 
                setNumeroConta(accountResponse.data.numeroConta); 
                
                // Tenta pegar o nome do usuário (priorizando 'usuario.nome')
                let nomeUsuario = accountResponse.data.usuario?.nome;
                
                // Fallback de nome original (caso o DTO não tenha objeto usuario)
                if (!nomeUsuario) {
                    nomeUsuario = accountResponse.data.nome;
                }
                
                // Fallback total: Buscar em /usuarios/profile
                if (!nomeUsuario) {
                    const profileResponse = await axios.get(`${API_URL}/usuarios/profile`, config); 
                    nomeUsuario = profileResponse.data.nome;
                }
                
                setUserName(nomeUsuario); 

                // 2. Buscar Extrato Resumido (últimas 5 transações)
                const extratoResponse = await axios.get(`${API_URL}/transacoes/extrato`, config);
                setRecentTransactions(extratoResponse.data.slice(0, 5)); 

            } catch (error) {
                console.error('Erro ao buscar dados da conta/extrato:', error.response?.data || error.message);
                
                if (error.response?.status === 401) {
                    localStorage.removeItem('bankmorning_token');
                    navigate('/');
                }
            } finally {
                setIsLoading(false);
            }
        };
        fetchDetails();
    }, [navigate]);


    if (isLoading) {
        return <div className="loading-container">Carregando dados da conta...</div>;
    }

    // ESTRUTURA PARA SIDEBAR ESQUERDA FIXA
    return (
        <div className="app-layout">
            
            {/* 1. SIDEBAR (FIXA À ESQUERDA) */}
            <div className="sidebar">
                
                {/* TOPO: NOME DO APLICATIVO */}
                <div className="sidebar-header">

                    <h1>Bank Morning</h1> 
                </div>
                
                {/* MEIO: NAVEGAÇÃO */}
                <nav className="sidebar-nav-container"> 
                    <nav className="sidebar-nav">
                        <ul>
                            <li>
                                <Link to="/inicio" className="active">
                                    <IoHomeOutline size={20} />
                                    <span>Página Inicial</span>
                                </Link>
                            </li>
                            <li>
                                <Link to="/extrato">
                                    <IoListOutline size={20} />
                                    <span>Ver Extrato</span>
                                </Link>
                            </li>
                        </ul>
                    </nav>
                    
                </nav>
                
                {/* RODAPÉ: BOTÃO SAIR */}
                <button className="logout-button" onClick={handleLogout}>
                    <IoLogOutOutline size={18} style={{ marginRight: '5px', verticalAlign: 'middle' }}/>
                    Sair da Conta
                </button>
            </div>


            {/* 2. CONTEÚDO PRINCIPAL (ROLÁVEL) */}
            <div className="main-content">
                {/* Saudação Personalizada */}
                <h2 className="greeting-header">Olá, {getFirstName(userName) || 'Usuário'}!</h2> 
                
                <div className="dashboard-grid">
                    
                    {/* CARD 1: SALDO E INFO DA CONTA */}
                    <div className="balance-card">
                        <h3>Seu Saldo Atual</h3>
                        <div className="balance-display">
                            <span className="balance-amount">
                                {showBalance ? formatBalance(balance) : 'R$ *****'}
                            </span>
                            <button 
                                className="eye-icon"
                                onClick={() => setShowBalance(!showBalance)}
                            >
                                {showBalance ? <IoEyeOffOutline size={24} /> : <IoEyeOutline size={24} />}
                            </button>
                        </div>
                        {numeroConta && <p className="account-info">Conta: <strong>{numeroConta}</strong></p>}
                    </div>

                    {/* WIDGET 2: AÇÕES RÁPIDAS (DEPÓSITO, SAQUE, TRANSFERÊNCIA) */}
                    <div className="actions-widget">
                        <h3>Ações Rápidas</h3>
                        <div className="actions-grid">
                            <Link to="/deposito" className="action-button">
                                <RiBankLine size={24} /> Depósito
                            </Link>
                            <Link to="/transacao" className="action-button">
                                <RiSwapBoxLine size={24} /> Transferir
                            </Link>
                            <Link to="/saque" className="action-button">
                                <RiWalletLine size={24} /> Saque
                            </Link>
                            <Link to="/extrato" className="action-button">
                                <RiHistoryLine size={24} /> Extrato
                            </Link>
                        </div>
                    </div>

                    {/* WIDGET 3: EXTRATO RECENTE (COM DADOS REAIS) */}
                    <div className="recent-transactions-widget">
                        <h3>Últimas Movimentações</h3>
                        {recentTransactions.length > 0 ? (
                            <ul className="transaction-list">
                                {recentTransactions.map((t, index) => (
                                    <li key={index} className="transaction-item">
                                        <div className="transaction-details">
                                            {/* Exibe o tipo de transação (ex: DEPOSITO) */}
                                            <p className="transaction-type">{t.tipoDeTransacao}</p>
                                            {/* Exibe a data formatada */}
                                            <p className="transaction-date">
                                                {new Date(t.dataTransacao).toLocaleDateString('pt-BR')}
                                            </p>
                                        </div>
                                        <div className={`transaction-amount ${getTransactionClass(t.tipoDeTransacao)}`}>
                                            {/* Exibe o valor formatado com sinal */}
                                            {formatTransactionAmount(t.valor, t.tipoDeTransacao)}
                                        </div>
                                    </li>
                                ))}
                            </ul>
                        ) : (
                            <p>Nenhuma transação recente encontrada.</p>
                        )}
                        <button 
                            className="action-button full-width-button" 
                            onClick={() => navigate('/extrato')}
                            style={{ marginTop: '20px' }}
                        >
                            Ver Extrato Completo
                        </button>
                    </div>

                </div>
            </div>
        </div>
    );
}

export default Home;