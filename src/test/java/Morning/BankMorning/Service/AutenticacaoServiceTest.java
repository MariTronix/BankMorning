package Morning.BankMorning.Service;

import Morning.BankMorning.Model.Usuario;
import Morning.BankMorning.Repository.UsuarioRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AutenticacaoServiceTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    @InjectMocks
    private AutenticacaoService autenticacaoService;

    @Test
    @DisplayName("Deve autenticar com sucesso quando encontrar usuário pelo CPF")
    void loadUserByUsername_SucessoPorCpf() {
        // 1. CENÁRIO
        String loginCpf = "123.456.789-00";
        Usuario usuario = new Usuario();
        usuario.setCpf(loginCpf);
        usuario.setEmail("outro@email.com");

        // Mock: Encontra logo na primeira tentativa (CPF)
        when(usuarioRepository.findByCpf(loginCpf)).thenReturn(Optional.of(usuario));

        // 2. AÇÃO
        UserDetails resultado = autenticacaoService.loadUserByUsername(loginCpf);

        // 3. VERIFICAÇÃO
        assertNotNull(resultado);
        // Verifica se o objeto retornado é realmente o usuário esperado
        assertEquals(loginCpf, ((Usuario) resultado).getCpf());

        // OTIMIZAÇÃO: Se achou por CPF, NÃO deve nem tentar buscar por email
        verify(usuarioRepository, times(1)).findByCpf(loginCpf);
        verify(usuarioRepository, never()).findByEmail(any());
    }

    @Test
    @DisplayName("Deve autenticar com sucesso quando encontrar usuário pelo EMAIL (falha no CPF)")
    void loadUserByUsername_SucessoPorEmail() {
        // 1. CENÁRIO
        String loginEmail = "teste@email.com";
        Usuario usuario = new Usuario();
        usuario.setCpf("000.000.000-00");
        usuario.setEmail(loginEmail);

        // Mock: Não acha por CPF, mas acha por Email
        when(usuarioRepository.findByCpf(loginEmail)).thenReturn(Optional.empty());
        when(usuarioRepository.findByEmail(loginEmail)).thenReturn(Optional.of(usuario));

        // 2. AÇÃO
        UserDetails resultado = autenticacaoService.loadUserByUsername(loginEmail);

        // 3. VERIFICAÇÃO
        assertNotNull(resultado);
        assertEquals(loginEmail, ((Usuario) resultado).getEmail());

        // Verifica se chamou os dois métodos na ordem certa
        verify(usuarioRepository, times(1)).findByCpf(loginEmail);
        verify(usuarioRepository, times(1)).findByEmail(loginEmail);
    }

    @Test
    @DisplayName("Deve lançar UsernameNotFoundException quando não encontrar nem por CPF nem por Email")
    void loadUserByUsername_UsuarioNaoEncontrado() {
        // 1. CENÁRIO
        String loginInexistente = "fantasma";

        // Mock: Não acha nada em lugar nenhum
        when(usuarioRepository.findByCpf(loginInexistente)).thenReturn(Optional.empty());
        when(usuarioRepository.findByEmail(loginInexistente)).thenReturn(Optional.empty());

        // 2. AÇÃO & VERIFICAÇÃO
        UsernameNotFoundException exception = assertThrows(UsernameNotFoundException.class, () -> {
            autenticacaoService.loadUserByUsername(loginInexistente);
        });

        assertEquals("Usuário não encontrado com o login: fantasma", exception.getMessage());
    }
}