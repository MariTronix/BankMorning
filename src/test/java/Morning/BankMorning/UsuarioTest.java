package Morning.BankMorning.Service;

import Morning.BankMorning.Dto.CadastroRequest;
import Morning.BankMorning.Dto.ContaRequest;
import Morning.BankMorning.Dto.UsuarioResponse;
import Morning.BankMorning.Exception.ArgumentoInvalidoException;
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
class UsuarioTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private ContaService contaService;

    @InjectMocks
    private UsuarioService usuarioService;

    // --- TESTE 1: SUCESSO ---
    @Test
    @DisplayName("Deve cadastrar usuário e criar conta com sucesso")
    void deveCadastrarComSucesso() {
        // ARRANGE
        CadastroRequest request = new CadastroRequest("Maria", "m@m.com", "123.456.789-00", LocalDate.now(), "123");
        Usuario usuarioSalvo = new Usuario();
        usuarioSalvo.setNome("Maria");
        usuarioSalvo.setEmail("m@m.com");

        when(usuarioRepository.findByCpf(any())).thenReturn(Optional.empty());
        when(usuarioRepository.findByEmail(any())).thenReturn(Optional.empty());
        when(passwordEncoder.encode(any())).thenReturn("SENHA_HASH");
        when(usuarioRepository.save(any())).thenReturn(usuarioSalvo);

        // ACT
        UsuarioResponse res = usuarioService.cadastrarNovoUsuarioeConta(request);

        // ASSERT
        assertNotNull(res);
        assertEquals("Maria", res.nome());
        verify(contaService, times(1)).criarConta(any(), any());
    }

    // --- TESTE 2: ERRO CPF ---
    @Test
    @DisplayName("Deve falhar quando CPF já existe")
    void deveFalharCpfDuplicado() {
        CadastroRequest request = new CadastroRequest("João", "j@j.com", "111.111.111-11", LocalDate.now(), "123");

        when(usuarioRepository.findByCpf(request.cpf())).thenReturn(Optional.of(new Usuario()));

        assertThrows(ArgumentoInvalidoException.class, () -> {
            usuarioService.cadastrarNovoUsuarioeConta(request);
        });
    }

    // --- TESTE 3: ERRO EMAIL ---
    @Test
    @DisplayName("Deve falhar quando Email já existe")
    void deveFalharEmailDuplicado() {
        CadastroRequest request = new CadastroRequest("Ana", "ana@email.com", "222.222.222-22", LocalDate.now(), "123");

        when(usuarioRepository.findByCpf(any())).thenReturn(Optional.empty());
        when(usuarioRepository.findByEmail(request.email())).thenReturn(Optional.of(new Usuario()));

        assertThrows(ArgumentoInvalidoException.class, () -> {
            usuarioService.cadastrarNovoUsuarioeConta(request);
        });
    }
}