package Morning.BankMorning.Model;

import Morning.BankMorning.Enum.Role;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;

import static org.junit.jupiter.api.Assertions.*;

class UsuarioTest {

    @Test
    @DisplayName("getUsername deve retornar o CPF")
    void getUsername_DeveRetornarCpf() {
        Usuario usuario = new Usuario();
        usuario.setCpf("123.456.789-00");
//
        assertEquals("123.456.789-00", usuario.getUsername());
    }

    @Test
    @DisplayName("getPassword deve retornar a senha da Conta associada")
    void getPassword_ComConta_DeveRetornarSenha() {
        Usuario usuario = new Usuario();
        Conta conta = new Conta();
        conta.setSenha("senhaSegura123");

        usuario.setConta(conta);

        assertEquals("senhaSegura123", usuario.getPassword());
    }

    @Test
    @DisplayName("getPassword deve retornar null se não houver Conta (evitar NullPointer)")
    void getPassword_SemConta_DeveRetornarNull() {
        Usuario usuario = new Usuario();
        usuario.setConta(null);

        assertNull(usuario.getPassword());
    }

    @Test
    @DisplayName("getAuthorities deve retornar a Role correta")
    void getAuthorities_DeveRetornarRoleUsuario() {
        Usuario usuario = new Usuario();
        usuario.setRole(Role.ROLE_USUARIO);

        Collection<? extends GrantedAuthority> authorities = usuario.getAuthorities();

        assertFalse(authorities.isEmpty());
        assertEquals("ROLE_USUARIO", authorities.iterator().next().getAuthority());
    }
}