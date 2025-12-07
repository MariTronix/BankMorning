// frontend/src/main.jsx
import React from 'react'
import ReactDOM from 'react-dom/client'
import { createBrowserRouter, RouterProvider } from 'react-router-dom'
import './index.css' 
// Importar os componentes de página que criaremos:
import Login from './pages/Login.jsx' 
import Register from './pages/Register.jsx' 
import Home from './pages/Home.jsx' 
import Extrato from './pages/Extrato.jsx';
import Deposito from './pages/Deposito.jsx';
import Transacao from './pages/Transacao.jsx';
import Saque from './pages/Saque.jsx';

// Define as rotas principais da aplicação
const router = createBrowserRouter([
  {
    path: "/",
    element: <Login />, // Rota inicial
  },
  {
    path: "/cadastro",
    element: <Register />, 
  },
  {
    path: "/inicio",
    element: <Home />,
  },
  { 
    path: "/extrato", 
    element: <Extrato /> 
  },
  { 
    path: "/deposito", 
    element: <Deposito /> 
  },
  { 
    path: "/transacao", 
    element: <Transacao /> 
  },
  { 
    path: "/saque",
     element: <Saque /> 
    },
]);

ReactDOM.createRoot(document.getElementById('root')).render(
  <React.StrictMode>
    <RouterProvider router={router} />
  </React.StrictMode>,
)