// frontend/src/pages/Home.jsx
import React, { useState, useEffect } from 'react';
import axios from 'axios';
import { useNavigate } from 'react-router-dom';
import { IoEyeOutline, IoEyeOffOutline } from 'react-icons/io5'; 
import { RiMoneyDollarCircleLine, RiBankLine, RiSwapBoxLine, RiArrowRightUpLine, RiHistoryLine, RiUserAddLine } from 'react-icons/ri'; 

const API_URL = 'http://localhost:8080/api'; 

function Home() {
  // ESTADOS ATUALIZADOS: Adicionado numeroConta
  const [balance, setBalance] = useState(null);
  const [numeroConta, setNumeroConta] = useState(null); 
  const [showBalance, setShowBalance] = useState(true);
  const [isLoading, setIsLoading] = useState(true);
  const navigate = useNavigate();

  // Função para buscar os DETALHES COMPLETOS na API
  useEffect(() => {
    const fetchDetails = async () => {
      const token = localStorage.getItem('bankmorning_token');
      if (!token) {
        navigate('/'); 
        return;
      }
      
      try {
        // CORREÇÃO CRÍTICA: Chama o endpoint /account/detalhes
        const response = await axios.get(`${API_URL}/account/detalhes`, {
            headers: {
                Authorization: `Bearer ${token}` 
            }
        });
        
        // EXTRAI SALDO E NÚMERO DA CONTA
        setBalance(response.data.saldo); // Saldo
        setNumeroConta(response.data.numeroConta); // Número da Conta
        
      } catch (error) {
        console.error('Erro ao buscar dados da conta:', error.response?.data || error.message);
        
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

  const formatBalance = (value) => {
    if (value === null) return 'R$ --,--';
    // Formata o valor para Real Brasileiro
    return parseFloat(value).toLocaleString('pt-BR', { style: 'currency', currency: 'BRL' });
  };
  
  const handleLogout = () => {
    localStorage.removeItem('bankmorning_token');
    navigate('/');
  };


  if (isLoading) {
    return <div className="home-container">Carregando dados da conta...</div>;
  }

  return (
    <div className="home-container">
      <header className="header">
        <h1 className="logo">Bank Morning</h1>
        <button className="logout-button" onClick={handleLogout}>Sair</button>
      </header>

      <div className="balance-area">
        <p className="balance-label">Seu Saldo</p>
        <div className="balance-display">
          <span className="balance-value">
            {showBalance ? formatBalance(balance) : 'R$ *****'}
          </span>
          <button 
            className="eye-icon"
            onClick={() => setShowBalance(!showBalance)}
          >
            {showBalance ? <IoEyeOffOutline size={24} /> : <IoEyeOutline size={24} />}
          </button>
        </div>
        
        {/* NOVO: EXIBIÇÃO DO NÚMERO DA CONTA */}
        {numeroConta && <p className="account-number">Conta: <strong>{numeroConta}</strong></p>}
        
      </div>

      <section className="quick-actions">
        <ActionItem icon={<RiBankLine size={30} />} label="Depósito" onClick={() => navigate('/deposito')} />
        <ActionItem icon={<RiSwapBoxLine size={30} />} label="Transação" onClick={() => navigate('/transacao')} />
        <ActionItem icon={<RiArrowRightUpLine size={30} />} label="Saque" onClick={() => navigate('/saque')} />
      </section>

      <section className="utility-actions">
        <UtilityItem icon={<RiHistoryLine size={20} />} label="Ver Extrato" onClick={() => navigate('/extrato')} />
        <UtilityItem icon={<RiMoneyDollarCircleLine size={20} />} label="Pegar Empréstimo" onClick={() => alert('Empréstimo em breve!')} />
        <UtilityItem icon={<RiUserAddLine size={20} />} label="Convidar Amigos" onClick={() => alert('Convite em breve!')} />
      </section>
    </div>
  );
}

// ... (ActionItem e UtilityItem components)
const ActionItem = ({ icon, label, onClick }) => (
    <button className="action-item" onClick={onClick}>
        {icon}
        <span className="action-label">{label}</span>
    </button>
);

const UtilityItem = ({ icon, label, onClick }) => (
    <button className="utility-item" onClick={onClick}>
        <div className="utility-content">
          {icon}
          <span className="utility-label">{label}</span>
        </div>
        <RiArrowRightUpLine size={16} style={{ transform: 'rotate(45deg)' }} />
    </button>
);


export default Home;