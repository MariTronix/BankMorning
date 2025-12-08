import React, { useState } from 'react';
import axios from 'axios';
import { useNavigate, Link } from 'react-router-dom';
import { IoLogOutOutline, IoHomeOutline, IoListOutline } from 'react-icons/io5';
import './Saque.css';
import BankMorningLogo from '../assets/bank_morning_logo.png';

const API_URL = 'http://localhost:8080/api';

function Saque() {
    const [valor, setValor] = useState('');
    const [mensagem, setMensagem] = useState('');
    const [sucesso, setSucesso] = useState(false);
    const navigate = useNavigate();

    const handleLogout = () => {
        localStorage.removeItem('bankmorning_token');
        navigate('/');
    };

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
            valor: parseFloat(valor)
        };

        try {
            const response = await axios.post(`${API_URL}/transacoes/sacar`, saqueRequest, {
                headers: {
                    Authorization: `Bearer ${token}`,
                    'Content-Type': 'application/json' 
                }
            });

            setSucesso(true);
            setMensagem(`Saque de R$ ${valor.replace('.', ',')} realizado com sucesso!`);
            setValor('');
            setTimeout(() => navigate('/inicio'), 1500);

        } catch (error) {
            const erroMsg = error.response?.data?.message || 'Erro ao realizar saque. Verifique o saldo ou o valor.';
            setMensagem(`Erro: ${erroMsg}`);
            console.error('Erro de saque:', error);
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
            <div className="saque-content">
                <div className="saque-box">
                    <h2>Saque</h2>
                    
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
                    <button onClick={() => navigate('/inicio')} className="back-button">
                        Voltar
                    </button>
                </div>
            </div>
        </div>
    );
}

export default Saque;