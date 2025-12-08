import React, { useState } from 'react';
import axios from 'axios';
import { useNavigate, Link } from 'react-router-dom';
import { IoLogOutOutline, IoHomeOutline, IoListOutline } from 'react-icons/io5';
import './Transacao.css';
import BankMorningLogo from '../assets/bank_morning_logo.png';

const API_URL = 'http://localhost:8080/api';

function Transferencia() {
    const [numeroContaDestino, setNumeroContaDestino] = useState('');
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
            setMensagem('Erro: Usuário não autenticado.');
            return;
        }

        if (!numeroContaDestino || !valor) {
            setMensagem('Erro: Preencha o número da conta de destino e o valor.');
            return;
        }

        const transferenciaRequest = {
            numeroContaDestino: numeroContaDestino,
            valor: parseFloat(valor)
        };

        try {
            const response = await axios.post(`${API_URL}/transacoes/transferir`, transferenciaRequest, {
                headers: { Authorization: `Bearer ${token}` }
            });
            
            setSucesso(true);
            setMensagem(`Transferência de ${parseFloat(valor).toLocaleString('pt-BR', { style: 'currency', currency: 'BRL' })} realizada com sucesso!`);
            setNumeroContaDestino('');
            setValor('');
            setTimeout(() => navigate('/inicio'), 1500);
            
        } catch (error) {
            console.error('Erro na transferência:', error);
            const erroMsg = error.response?.data?.message || 'Erro ao realizar a transferência. Verifique os dados.';
            setMensagem(`Erro: ${erroMsg}`);

            if (error.response?.status === 401) {
                localStorage.removeItem('bankmorning_token');
                navigate('/');
            }
        }
    };

    return (
        <div className="app-layout">
            {/* SIDEBAR - Mesma do Home */}
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
            <div className="transferencia-content">
                <div className="transferencia-box">
                    <h2>Transferência</h2>
                    
                    {mensagem && (
                        <p className={mensagem.startsWith('Erro') ? 'message-error' : 'message-success'}>
                            {mensagem}
                        </p>
                    )}

                    <form onSubmit={handleSubmit} className="transferencia-form">
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

                        <button type="submit" className="confirm-transfer-button">
                            Confirmar Transferência
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

export default Transferencia;