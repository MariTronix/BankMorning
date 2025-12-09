Funcionalidade: Gestão de Tokens de Segurança (JWT)
  Como o sistema de segurança do BankMorning
  Eu quero gerar, validar e ler tokens JWT
  Para garantir que apenas usuários autenticados acessem a API

  Cenário: Geração de Token JWT com sucesso
    Dado que existe uma conta vinculada a um usuário com CPF "123.456.789-00" e perfil "USUARIO"
    Quando o sistema solicita a geração de um token para esta conta
    Então um token JWT não nulo deve ser retornado
    E o "Subject" (assunto) do token deve conter o CPF "123.456.789-00"

  Cenário: Bloqueio na geração de token para conta sem usuário
    Dado que existe uma conta sem nenhum usuário associado (nulo)
    Quando tento gerar um token para esta conta
    Então o sistema deve lançar uma exceção de "Argumento Inválido"
    E o token não deve ser gerado

  Cenário: Validação de Token autêntico
    Dado um token JWT gerado legitimamente pelo sistema com a chave secreta correta
    Quando solicito a validação deste token
    Então o sistema deve confirmar que o token é válido (True)

  Cenário: Rejeição de Token falso ou malformado
    Quando tento validar um token inválido "token.falso.invalido"
    Então o sistema deve lançar um erro interno de validação
    E o acesso deve ser negado

  Cenário: Recuperação de dados do usuário via Token
    Dado um token válido pertencente ao usuário com CPF "999.888.777-66"
    Quando o sistema extrai o "Subject" deste token
    Então o resultado deve ser exatamente o CPF "999.888.777-66"

  Cenário: Bloqueio de Token expirado
    Dado um token cuja data de validade expirou no passado (ex: ontem)
    Quando tento extrair informações ou usar este token
    Então o sistema deve lançar uma exceção de execução (Runtime Exception)
    E o acesso deve ser bloqueado