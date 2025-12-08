package Morning.BankMorning.Service;

import Morning.BankMorning.Dto.CadastroRequest;
import Morning.BankMorning.Dto.ContaRequest;
import Morning.BankMorning.Dto.UsuarioRequest;
import Morning.BankMorning.Dto.UsuarioResponse;
import Morning.BankMorning.Enum.Role;
import Morning.BankMorning.Exception.ArgumentoInvalidoException;
import Morning.BankMorning.Exception.RecursoNaoEncontradoException;
import Morning.BankMorning.Model.Usuario;
import Morning.BankMorning.Repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UsuarioService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private ContaService contaService;

    /**
     * Converte Model para DTO de Resposta.
     * REMOVIDO "static" para permitir que o Mockito funcione corretamente nos testes.
     */
    public UsuarioResponse converterParaResponse(Usuario usuario) {
        if (usuario == null) {
            return null;
        }

        // A ordem deve respeitar o Record: (Nome, CPF, Email)
        return new UsuarioResponse(
                usuario.getNome(),
                usuario.getCpf(),
                usuario.getEmail()
        );
    }

    @Transactional
    public UsuarioResponse cadastrarNovoUsuarioeConta(CadastroRequest request) {

        usuarioRepository.findByCpf(request.cpf()).ifPresent(usuario -> {
            throw new ArgumentoInvalidoException("Já existe um usuário com este CPF");
        });

        usuarioRepository.findByEmail(request.email()).ifPresent(usuario -> {
            throw new ArgumentoInvalidoException("Já existe um usuário com este EMAIL");
        });

        Usuario usuarioSendoCadastrado = new Usuario();
        usuarioSendoCadastrado.setCpf(request.cpf());
        usuarioSendoCadastrado.setEmail(request.email());
        usuarioSendoCadastrado.setNome(request.nome());
        usuarioSendoCadastrado.setDataNascimento(request.data_nascimento());
        usuarioSendoCadastrado.setRole(Role.ROLE_USUARIO);

        Usuario usuarioCadastrado = usuarioRepository.save(usuarioSendoCadastrado);

        // Cria o request de conta com a senha crua (o ContaService criptografará)
        ContaRequest contaRequest = new ContaRequest(request.senha(), usuarioCadastrado);

        // Chama o serviço de conta para criar a conta vinculada
        contaService.criarConta(usuarioCadastrado, contaRequest);

        return converterParaResponse(usuarioCadastrado);
    }

    @Transactional
    public UsuarioResponse atualizarUsuario(Integer id, UsuarioRequest usuarioRequest) {

        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Usuário não encontrado com ID: " + id));

        // Validação de Email único na atualização
        if (usuarioRequest.email() != null && !usuarioRequest.email().isEmpty()) {
            usuarioRepository.findByEmail(usuarioRequest.email()).ifPresent(usuarioEncontrado -> {
                if (!usuarioEncontrado.getIdUsuario().equals(id)) {
                    throw new ArgumentoInvalidoException("Já existe um usuário com este Email: " + usuarioRequest.email());
                }
            });
            usuario.setEmail(usuarioRequest.email());
        }

        if (usuarioRequest.nome() != null && !usuarioRequest.nome().isEmpty()) {
            usuario.setNome(usuarioRequest.nome());
        }

        if (usuarioRequest.data_nascimento() != null) {
            usuario.setDataNascimento(usuarioRequest.data_nascimento());
        }

        Usuario usuarioAtualizado = usuarioRepository.save(usuario);

        return converterParaResponse(usuarioAtualizado);
    }

    @Transactional
    public UsuarioResponse deletarUsuario(Integer id) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Usuário não encontrado."));

        usuarioRepository.delete(usuario);

        return converterParaResponse(usuario);
    }
}