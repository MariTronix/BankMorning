
CREATE TABLE Cliente(
                         ID_Cliente SERIAL PRIMARY KEY,
                         Nome VARCHAR(100) NOT NULL,
                         CPF VARCHAR(14) NOT NULL UNIQUE,
                         Email VARCHAR(100),
                         Data_Nascimento DATE NOT NULL
);
CREATE TABLE Conta(
                       ID_Conta SERIAL PRIMARY KEY,
                       Agencia VARCHAR(10) NOT NULL,
                       Numero_Conta VARCHAR(20) NOT NULL UNIQUE,
                       Saldo NUMERIC(15, 2) NOT NULL DEFAULT 0.00,
                       ID_Cliente INTEGER NOT NULL,
                       FOREIGN KEY(ID_Cliente) REFERENCES Cliente (ID_Cliente)
);
CREATE TABLE Usuario(
                         ID_Usuario SERIAL PRIMARY KEY,
                         Login VARCHAR(100) NOT NULL UNIQUE,
                         Senha VARCHAR(255) NOT NULL,
                         ID_Cliente INTEGER NOT NULL UNIQUE,
                         FOREIGN KEY(ID_Cliente) REFERENCES Cliente (ID_Cliente)
);
CREATE TABLE Transacao(
                           ID_Transacao BIGSERIAL PRIMARY KEY,
                           Valor NUMERIC(15, 2) NOT NULL,
                           Data_Hora TIMESTAMP NOT NULL,
                           ID_Conta_Origem INTEGER NOT NULL,
                           ID_Conta_Destino INTEGER NOT NULL,
                           FOREIGN KEY(ID_Conta_Origem) REFERENCES Conta (ID_Conta),
                           FOREIGN KEY(ID_Conta_Destino) REFERENCES Conta (ID_Conta)
);