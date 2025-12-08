import React, { useState } from 'react';
import axios from 'axios';
import { useNavigate, Link } from 'react-router-dom';
import { IoEyeOutline, IoLogOutOutline, IoHomeOutline, IoListOutline } from 'react-icons/io5';
import './Deposito.css';
import BankMorningLogo from '../assets/bank_morning_logo.png';

const API_URL = 'http://localhost:8080/api';

function Deposito() {
    const [valor, setValor] = useState('');
    const [numeroContaDestino, setNumeroContaDestino] = useState('');
    const [mensagem, setMensagem] = useState('');
    const navigate = useNavigate();

    const handleLogout = () => {
        localStorage.removeItem('bankmorning_token');
        navigate('/');
    };

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
            const response = await axios.post(`${API_URL}/transacoes/depositar`,
                { 
                    valor: parseFloat(valor),
                    numeroConta: numeroContaDestino 
                },
                {
                    headers: {
                        Authorization: `Bearer ${token}`,
                        'Content-Type': 'application/json' 
                    }
                }
            );

            setMensagem(`Depósito de R$ ${valor.replace('.', ',')} realizado com sucesso!`);
            setValor('');
            setNumeroContaDestino('');
            setTimeout(() => navigate('/inicio'), 1500);

        } catch (error) {
            const erroMsg = error.response?.data?.message || 'Erro ao realizar depósito. Verifique os dados.';
            setMensagem(`Erro: ${erroMsg}`);
            console.error('Erro de depósito:', error);
        }
    };

    return (
        <div className="app-layout">
            {/* SIDEBAR - Mesma do Home */}
            <div className="sidebar">
                <div className="sidebar-header">
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
                                <Link to="/extrato">
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

            {/* CONTEÚDO CENTRALIZADO */}
            <div className="deposito-content">
                <div className="deposito-box">
                    <h2>Depósito</h2>
                    
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
                    <button onClick={() => navigate('/inicio')} className="back-button">
                        Voltar
                    </button>
                </div>
            </div>
        </div>
    );
}

export default Deposito;