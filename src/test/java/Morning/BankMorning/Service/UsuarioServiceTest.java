package Morning.BankMorning.Service;

import Morning.BankMorning.Dto.CadastroRequest;
import Morning.BankMorning.Dto.ContaRequest;
import Morning.BankMorning.Dto.UsuarioRequest;
import Morning.BankMorning.Dto.UsuarioResponse;
import Morning.BankMorning.Exception.ArgumentoInvalidoException;
import Morning.BankMorning.Exception.RecursoNaoEncontradoException;
import Morning.BankMorning.Model.Usuario;
import Morning.BankMorning.Repository.UsuarioRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UsuarioServiceTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private ContaService contaService;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UsuarioService usuarioService;

    // --- TESTES DE CADASTRO ---

    @Test
    @DisplayName("Deve cadastrar usuário e solicitar criação de conta com sucesso")
    void cadastrar_Sucesso() {
        CadastroRequest request = new CadastroRequest(
                "Teste Silva",
                "111.222.333-44",
                "teste@email.com",
                LocalDate.of(2000, 1, 1),
                "senha123"
        );

        when(usuarioRepository.findByCpf(request.cpf())).thenReturn(Optional.empty());
        when(usuarioRepository.findByEmail(request.email())).thenReturn(Optional.empty());

        // Mock: Quando salvar, retorna o usuário com ID simulado
        when(usuarioRepository.save(any(Usuario.class))).thenAnswer(invocation -> {
            Usuario u = invocation.getArgument(0);
            u.setIdUsuario(1); // Simula o banco gerando ID
            return u;
        });

        UsuarioResponse response = usuarioService.cadastrarNovoUsuarioeConta(request);

        assertNotNull(response);
        assertEquals("Teste Silva", response.nome());
        assertEquals("teste@email.com", response.email());

        // Verifica se o repository salvou
        verify(usuarioRepository, times(1)).save(any(Usuario.class));

        // Verifica se chamou o ContaService para criar a conta
        verify(contaService, times(1)).criarConta(any(Usuario.class), any(ContaRequest.class));
    }

    @Test
    @DisplayName("Deve falhar ao cadastrar usuário com CPF já existente")
    void cadastrar_ErroCpfDuplicado() {
        CadastroRequest request = new CadastroRequest(
                "Teste", "111.222.333-44", "email@teste.com", LocalDate.now(), "123"
        );

        // Mock: Encontra um usuário existente com esse CPF
        when(usuarioRepository.findByCpf(request.cpf())).thenReturn(Optional.of(new Usuario()));

        assertThrows(ArgumentoInvalidoException.class, () -> {
            usuarioService.cadastrarNovoUsuarioeConta(request);
        });

        // Garante que não tentou salvar nem criar conta
        verify(usuarioRepository, never()).save(any());
        verify(contaService, never()).criarConta(any(), any());
    }

    @Test
    @DisplayName("Deve falhar ao cadastrar usuário com Email já existente")
    void cadastrar_ErroEmailDuplicado() {
        CadastroRequest request = new CadastroRequest(
                "Teste", "111.222.333-44", "email@teste.com", LocalDate.now(), "123"
        );

        when(usuarioRepository.findByCpf(request.cpf())).thenReturn(Optional.empty());
        when(usuarioRepository.findByEmail(request.email())).thenReturn(Optional.of(new Usuario()));

        assertThrows(ArgumentoInvalidoException.class, () -> {
            usuarioService.cadastrarNovoUsuarioeConta(request);
        });

        verify(usuarioRepository, never()).save(any());
    }

    // --- TESTES DE ATUALIZAÇÃO ---

    @Test
    @DisplayName("Deve atualizar usuário com sucesso")
    void atualizar_Sucesso() {

        Integer id = 1;

        UsuarioRequest request = new UsuarioRequest(
                "Nome Novo",
                "novo@email.com",
                LocalDate.of(1990, 1, 1)
        );

        Usuario usuarioAntigo = new Usuario();
        usuarioAntigo.setIdUsuario(id);
        usuarioAntigo.setNome("Nome Antigo");
        usuarioAntigo.setEmail("antigo@email.com");
        usuarioAntigo.setCpf("000.000.000-00");

        when(usuarioRepository.findById(id)).thenReturn(Optional.of(usuarioAntigo));

        when(usuarioRepository.findByEmail(request.email())).thenReturn(Optional.empty());

        // Garante que o objeto salvo é o retornado
        when(usuarioRepository.save(any(Usuario.class))).thenAnswer(i -> i.getArguments()[0]);

        UsuarioResponse response = usuarioService.atualizarUsuario(id, request);

        assertNotNull(response);
        assertEquals("Nome Novo", response.nome());

        assertEquals("novo@email.com", response.email());

        verify(usuarioRepository).save(any(Usuario.class));
    }

    @Test
    @DisplayName("Deve lançar erro ao tentar atualizar para um email que já existe em OUTRA conta")
    void atualizar_ErroEmailDuplicado() {
        Integer meuId = 1;
        UsuarioRequest request = new UsuarioRequest("Nome", "email.ocupado@teste.com", null);

        Usuario eu = new Usuario();
        eu.setIdUsuario(meuId);

        Usuario outroUsuario = new Usuario();
        outroUsuario.setIdUsuario(2);

        when(usuarioRepository.findById(meuId)).thenReturn(Optional.of(eu));
        when(usuarioRepository.findByEmail(request.email())).thenReturn(Optional.of(outroUsuario));

        assertThrows(ArgumentoInvalidoException.class, () -> {
            usuarioService.atualizarUsuario(meuId, request);
        });

        verify(usuarioRepository, never()).save(any());
    }

    @Test
    @DisplayName("Deve lançar erro ao tentar atualizar usuário inexistente")
    void atualizar_ErroUsuarioNaoEncontrado() {
        Integer id = 99;
        UsuarioRequest request = new UsuarioRequest("Nome", "email@teste.com", null);

        when(usuarioRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(RecursoNaoEncontradoException.class, () -> {
            usuarioService.atualizarUsuario(id, request);
        });
    }

    // --- TESTES DE DELETE ---

    @Test
    @DisplayName("Deve deletar usuário corretamente")
    void deletar_Sucesso() {
        Integer id = 1;
        Usuario usuario = new Usuario();
        usuario.setIdUsuario(id);
        usuario.setNome("Deletado");
        usuario.setCpf("000");
        usuario.setEmail("del@test.com");

        when(usuarioRepository.findById(id)).thenReturn(Optional.of(usuario));

        UsuarioResponse response = usuarioService.deletarUsuario(id);

        assertNotNull(response);
        verify(usuarioRepository, times(1)).delete(usuario);
    }

    @Test
    @DisplayName("Deve falhar ao deletar usuário inexistente")
    void deletar_ErroNaoEncontrado() {
        Integer id = 99;
        when(usuarioRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(RecursoNaoEncontradoException.class, () -> {
            usuarioService.deletarUsuario(id);
        });

        verify(usuarioRepository, never()).delete(any());
    }
}
