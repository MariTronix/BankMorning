Funcionalidade: Gestão de Contas Bancárias
  Como um cliente ou sistema administrativo
  Eu quero criar, visualizar e validar contas bancárias
  Para garantir a integridade dos dados financeiros

  Cenário: Criação de conta com valores padrão
    Dado que existe um usuário "Teste" com CPF "123.456.789-00" sem conta vinculada
    Quando eu solicito a criação de uma conta com a senha "senha123"
    Então a conta deve ser criada com sucesso
    E o saldo inicial deve ser 0 (Zero)
    E a agência padrão deve ser "777"
    E a senha deve ser salva criptografada
    E um número de conta deve ser gerado automaticamente

  Cenário: Impedir criação de conta duplicada
    Dado que o usuário com CPF "123.456.789-00" já possui uma conta ativa
    Quando eu tento criar uma nova conta para este mesmo usuário
    Então o sistema deve lançar um erro de "Argumento Inválido"
    E a nova conta não deve ser salva

  Cenário: Visualização de conta via API (Sucesso)
    Dado que existe uma conta com ID 1 e saldo 1500.00
    E que estou autenticado como o usuário dono da conta
    Quando eu envio uma requisição GET para "/api/account/id/1"
    Então a API deve retornar status 200 OK
    E deve exibir o número da conta "12345" e o saldo "1500.00"

  Cenário: Busca de conta inexistente
    Quando eu busco por uma conta com ID 999999 (que não existe)
    Então o sistema deve retornar um erro de "Recurso não encontrado" (404)

  Cenário: Segurança no acesso aos dados da conta
    Quando eu tento acessar "/api/account/1" sem enviar o Token de acesso
    Então a API deve negar o acesso com status 403 Forbidden

  Cenário: Regras de Domínio da Conta
    Quando uma conta é instanciada ou persistida
    Então a agência nunca pode ser nula ou vazia
    E o saldo nunca pode ser nulo
    E a data de criação deve ser preenchida automaticamente pelo sistema