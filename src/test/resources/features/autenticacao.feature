Funcionalidade: Autenticação Flexível (CPF ou E-mail)
  Como um usuário do BankMorning
  Eu quero poder logar usando meu CPF ou meu E-mail
  Para que eu tenha flexibilidade no acesso

  Cenário: Login realizado com sucesso via CPF
    Dado que existe um usuário com CPF "123.456.789-00" e senha "senha123"
    Quando eu envio uma requisição de login informando "123.456.789-00" no campo de usuário
    Então o sistema deve identificar o usuário pelo CPF
    E deve retornar o status 200 OK com o Token de acesso

  Cenário: Login realizado com sucesso via E-mail
    Dado que existe um usuário com email "teste@email.com" e senha "senha123"
    Quando eu envio uma requisição de login informando "teste@email.com" no campo de usuário
    Então o sistema deve tentar buscar por CPF (falhar) e depois encontrar por E-mail
    E deve retornar o status 200 OK com o Token de acesso

  Cenário: Usuário não encontrado (Nem CPF, nem E-mail)
    Quando eu tento logar com o usuário "fantasma"
    Então o sistema não deve encontrar nenhum registro
    E deve retornar o status 403 Forbidden (ou erro de credenciais inválidas)