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

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UsuarioServiceTest {

    @Mock
    private UsuarioRepository usuarioRepository;
//
    @Mock
    private ContaService contaService;

    @InjectMocks
    private UsuarioService usuarioService;

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

        when(usuarioRepository.save(any(Usuario.class))).thenAnswer(invocation -> {
            Usuario u = invocation.getArgument(0);
            u.setIdUsuario(1);
            return u;
        });

        UsuarioResponse response = usuarioService.cadastrarNovoUsuarioeConta(request);

        assertNotNull(response);
        assertEquals("Teste Silva", response.nome());
        verify(usuarioRepository, times(1)).save(any(Usuario.class));
        verify(contaService, times(1)).criarConta(any(Usuario.class), any(ContaRequest.class));
    }

    // --- TESTES DE ATUALIZAÇÃO (Usam UsuarioRequest - 3 argumentos) ---

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

        when(usuarioRepository.findById(id)).thenReturn(Optional.of(usuarioAntigo));

        when(usuarioRepository.findByEmail(request.email())).thenReturn(Optional.empty());

        when(usuarioRepository.save(any(Usuario.class))).thenAnswer(i -> i.getArguments()[0]);

        UsuarioResponse response = usuarioService.atualizarUsuario(id, request);

        assertEquals("Nome Novo", response.nome());
        assertEquals("novo@email.com", response.email());

        verify(usuarioRepository).save(any(Usuario.class));
    }

    @Test
    @DisplayName("Deve lançar erro ao tentar atualizar para um email que já existe em OUTRA conta")
    void atualizar_ErroEmailDuplicado() {

        Integer meuId = 1;

        UsuarioRequest request = new UsuarioRequest(
                "Nome",
                "email.ocupado@teste.com",
                null
        );

        Usuario eu = new Usuario();
        eu.setIdUsuario(meuId);

        Usuario outroUsuario = new Usuario();
        outroUsuario.setIdUsuario(2); // Outra pessoa dona do email

        when(usuarioRepository.findById(meuId)).thenReturn(Optional.of(eu));

        // O email que eu quero JÁ EXISTE no banco (pertence ao 'outroUsuario')
        when(usuarioRepository.findByEmail(request.email())).thenReturn(Optional.of(outroUsuario));

        // 2. ACT & ASSERT
        assertThrows(ArgumentoInvalidoException.class, () -> {
            usuarioService.atualizarUsuario(meuId, request);
        });

        verify(usuarioRepository, never()).save(any());
    }

    @Test
    @DisplayName("Deve lançar erro ao tentar atualizar usuário inexistente")
    void atualizar_ErroUsuarioNaoEncontrado() {
        Integer id = 99;

        // CORREÇÃO AQUI: Passando apenas 3 argumentos
        UsuarioRequest request = new UsuarioRequest("Nome", "email@teste.com", null);

        when(usuarioRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(RecursoNaoEncontradoException.class, () -> {
            usuarioService.atualizarUsuario(id, request);
        });
    }

    @Test
    @DisplayName("Deve deletar usuário corretamente")
    void deletar_Sucesso() {
        Integer id = 1;
        Usuario usuario = new Usuario();
        usuario.setIdUsuario(id);

        when(usuarioRepository.findById(id)).thenReturn(Optional.of(usuario));

        usuarioService.deletarUsuario(id);

        verify(usuarioRepository, times(1)).delete(usuario);
    }
}