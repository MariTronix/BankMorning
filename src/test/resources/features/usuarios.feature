Funcionalidade: Gestão de Usuários
  Como um administrador ou cliente do banco
  Eu quero cadastrar, atualizar e gerenciar minhas informações
  Para manter meus dados sempre corretos e seguros

  Cenário: Cadastro de usuário e conta realizado com sucesso
    Dado que não existe nenhum usuário com o CPF "111.222.333-44"
    E que não existe nenhum usuário com o email "teste@email.com"
    Quando eu solicito um novo cadastro com esses dados e a senha "senha123"
    Então o sistema deve criar o usuário no banco de dados
    E deve disparar a criação automática de uma conta bancária para ele
    E deve retornar os dados do novo usuário cadastrado

  Cenário: Bloqueio de cadastro duplicado
    Dado que já existe um usuário cadastrado com CPF "111.222.333-44" ou email "teste@email.com"
    Quando eu tento criar um novo cadastro usando esses mesmos dados
    Então o sistema deve impedir a operação
    E deve retornar um erro de "Argumento Inválido"

  Cenário: Atualização de dados cadastrais
    Dado que existe um usuário com ID 1
    E que estou autenticado com as permissões corretas
    Quando eu envio uma requisição PUT para atualizar o nome para "Nome Novo" e email para "novo@email.com"
    Então os dados do usuário devem ser atualizados no sistema
    E o sistema deve retornar os dados atualizados com status 200 OK

  Cenário: Segurança na atualização de dados
    Quando eu tento atualizar os dados de um usuário sem estar logado
    Então o sistema deve bloquear o acesso com status 403 Forbidden

  Cenário: Atualização com email já em uso por outro cliente
    Dado que o email "email.ocupado@teste.com" pertence ao Cliente A
    Quando o Cliente B tenta atualizar seu cadastro para usar esse mesmo email
    Então o sistema deve impedir a atualização
    E deve informar que o email já está em uso

  Cenário: Exclusão de usuário
    Dado que existe um usuário cadastrado
    Quando eu solicito a exclusão desse usuário (DELETE)
    Então o registro deve ser removido do banco de dados
    E o sistema deve confirmar a exclusão com sucesso

  Cenário: Exclusão de usuário inexistente
    Quando eu tento excluir um usuário com ID 999999 que não existe
    Então o sistema deve retornar erro 404 (Recurso Não Encontrado)

  Cenário: Regras de Domínio e Segurança do Usuário
    Dado um usuário com conta associada
    Quando o sistema consulta as credenciais desse usuário
    Então o "Username" deve corresponder ao E-mail (regra de login)
    E o "Password" deve ser recuperado da tabela de Contas associada
    E a "Role" (permissão) deve ser retornada corretamente