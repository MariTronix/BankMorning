package Morning.BankMorning.Service;

import Morning.BankMorning.Enum.Role;
import Morning.BankMorning.Exception.ArgumentoInvalidoException;
import Morning.BankMorning.Model.Conta;
import Morning.BankMorning.Model.Usuario;
import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;
import java.time.ZoneOffset;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class TokenServiceTest {

    @InjectMocks
    private TokenService tokenService;

    // Chave secreta de mentira para o teste
    private final String secret = "MinhaChaveSecretaDeTeste123!";

    @BeforeEach
    void setup() {
        // Injeta a chave na variável privada 'secret' do TokenService
        ReflectionTestUtils.setField(tokenService, "secret", secret);
    }

    @Test
    @DisplayName("Deve gerar token válido a partir de uma Conta")
    void gerarToken_Sucesso() {
        // Criando o usuário
        Usuario usuario = new Usuario();
        usuario.setCpf("123.456.789-00");
        usuario.setEmail("teste@email.com");
        usuario.setRole(Role.ROLE_USUARIO);

        Conta conta = new Conta();
        conta.setUsuario(usuario);

        String token = tokenService.gerarToken(conta);

        assertNotNull(token);
        assertFalse(token.isEmpty());

        String sujeito = JWT.require(Algorithm.HMAC256(secret))
                .withIssuer("BankMorningAPI")
                .build()
                .verify(token)
                .getSubject();

        assertEquals("123.456.789-00", sujeito);
    }

    @Test
    @DisplayName("Deve lançar erro ao tentar gerar token para Conta sem Usuário")
    void gerarToken_ErroSemUsuario() {
        Conta contaSemUsuario = new Conta();
        contaSemUsuario.setUsuario(null);

        assertThrows(ArgumentoInvalidoException.class, () -> {
            tokenService.gerarToken(contaSemUsuario);
        });
    }

    @Test
    @DisplayName("Deve validar token com sucesso")
    void validarToken_Sucesso() {
        // Gera um token válido primeiro
        Usuario usuario = new Usuario();
        usuario.setCpf("111.222.333-44");
        usuario.setEmail("validar@teste.com");
        usuario.setRole(Role.ROLE_USUARIO);
        Conta conta = new Conta();
        conta.setUsuario(usuario);

        String token = tokenService.gerarToken(conta);

        boolean valido = tokenService.validarToken(token);

        assertTrue(valido);
    }

    @Test
    @DisplayName("Deve lançar erro ao validar token inválido")
    void validarToken_Erro() {
        String tokenFalso = "token.falso.invalido";


        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            tokenService.validarToken(tokenFalso);
        });

        assertEquals("Erro interno ao validar Token.", exception.getMessage());
    }

    @Test
    @DisplayName("Deve recuperar o Subject (CPF) do token corretamente")
    void getSubject_Sucesso() {
        Usuario usuario = new Usuario();
        usuario.setCpf("999.888.777-66");
        usuario.setEmail("subject@teste.com");

        usuario.setRole(Role.ROLE_USUARIO);

        Conta conta = new Conta();
        conta.setUsuario(usuario);

        String token = tokenService.gerarToken(conta);

        String cpfRecuperado = tokenService.getSubject(token);

        assertEquals("999.888.777-66", cpfRecuperado);
    }

    @Test
    @DisplayName("Deve lançar erro ao tentar pegar Subject de token expirado")
    void getSubject_ErroExpirado() {
        // Cria um token expirado manualmente
        String tokenExpirado = JWT.create()
                .withIssuer("BankMorningAPI")
                .withSubject("123.123.123-12")
                .withExpiresAt(LocalDateTime.now().minusDays(1).toInstant(ZoneOffset.of("-03:00")))
                .sign(Algorithm.HMAC256(secret));


        assertThrows(RuntimeException.class, () -> {
            tokenService.getSubject(tokenExpirado);
        });
    }
}
