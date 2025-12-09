Funcionalidade: Movimentação Financeira e Transações
  Como um correntista do BankMorning
  Eu quero depositar, sacar e transferir valores
  Para movimentar meu dinheiro com segurança

  Cenário: Depósito realizado com sucesso
    Dado que existe uma conta "12345" com saldo inicial de R$ 100,00
    Quando eu solicito um depósito de R$ 50,00 para a conta "12345"
    Então o saldo da conta deve ser atualizado para R$ 150,00
    E uma transação do tipo "DEPOSITO" deve ser registrada no sistema

  Cenário: Depósito bloqueado com valor negativo
    Quando eu tento depositar um valor negativo de R$ -10,00
    Então o sistema deve impedir a operação
    E deve retornar um erro de "Argumento Inválido"

  Cenário: Saque realizado com sucesso
    Dado que minha conta tem saldo de R$ 100,00
    E que estou autenticado corretamente
    Quando eu solicito um saque de R$ 40,00
    Então o saldo da minha conta deve diminuir para R$ 60,00
    E uma transação do tipo "SAQUE" deve ser registrada

  Cenário: Saque bloqueado por falta de saldo
    Dado que minha conta tem apenas R$ 10,00 de saldo
    Quando eu tento sacar R$ 50,00
    Então o sistema deve negar a transação
    E o saldo deve permanecer inalterado em R$ 10,00
    E uma mensagem de "Saldo insuficiente" deve ser retornada

  Cenário: Transferência entre contas com sucesso
    Dado que a minha conta (origem) tem R$ 200,00
    E que a conta de destino "22222" tem R$ 50,00
    Quando eu transfiro R$ 100,00 para a conta "22222"
    Então o meu saldo deve cair para R$ 100,00
    E o saldo da conta destino deve subir para R$ 150,00
    E uma transação do tipo "TRANSFERENCIA" deve ser salva

  Cenário: Auditoria e Integridade da Transação
    Quando uma transação é salva no banco de dados
    Então a data e hora devem ser geradas automaticamente (não nulas)
    E o valor e o tipo da transação devem ser armazenados corretamente