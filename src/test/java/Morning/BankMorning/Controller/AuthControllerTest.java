package Morning.BankMorning.Controller;

import Morning.BankMorning.Dto.CadastroRequest;
import Morning.BankMorning.Dto.LoginRequest;
import Morning.BankMorning.Enum.Role;
import Morning.BankMorning.Model.Conta;
import Morning.BankMorning.Model.Usuario;
import Morning.BankMorning.Repository.ContaRepository;
import Morning.BankMorning.Repository.UsuarioRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class AuthControllerTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;
    @Autowired private UsuarioRepository usuarioRepository;
    @Autowired private ContaRepository contaRepository;
    @Autowired private PasswordEncoder passwordEncoder;

    @Test
    @DisplayName("POST /auth/login - Deve retornar 401 se a senha estiver errada")
    void login_SenhaIncorreta() throws Exception {
        // Criando a conta
        Usuario usuario = new Usuario();
        usuario.setNome("Hacker Teste");
        usuario.setCpf("111.111.111-11");
        usuario.setEmail("hacker@teste.com");
        usuario.setRole(Role.ROLE_USUARIO);
        usuario.setDataNascimento(LocalDate.of(1990, 1, 1));

        usuario = usuarioRepository.save(usuario); // Garante ID gerado

        // Criando a conta
        Conta conta = new Conta();
        conta.setSenha(passwordEncoder.encode("senhaCerta123"));
        conta.setUsuario(usuario);
        conta.setSaldo(BigDecimal.ZERO);
        conta.setAgencia("777");
        conta.setNumeroConta("99999");

        conta = contaRepository.save(conta);

        usuario.setConta(conta);
        usuarioRepository.save(usuario);

        // Teste
        LoginRequest loginErrado = new LoginRequest("111.111.111-11", "senhaErrada!!!");

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginErrado)))
                .andExpect(status().isUnauthorized()); // 401
    }

    @Test
    @DisplayName("POST /auth/login - Deve retornar 200 OK se login for correto")
    void login_Sucesso() throws Exception {
        Usuario usuario = new Usuario();
        usuario.setNome("Cliente Real");
        usuario.setCpf("222.222.222-22");
        usuario.setEmail("cliente@teste.com");
        usuario.setRole(Role.ROLE_USUARIO);
        usuario.setDataNascimento(LocalDate.of(2000, 1, 1));
        usuario = usuarioRepository.save(usuario);

        Conta conta = new Conta();
        conta.setSenha(passwordEncoder.encode("minhaSenhaSecreta"));
        conta.setUsuario(usuario);
        conta.setSaldo(BigDecimal.ZERO);
        conta.setAgencia("777");
        conta.setNumeroConta("88888");
        conta = contaRepository.save(conta);

        usuario.setConta(conta);
        usuarioRepository.save(usuario);

        LoginRequest loginCorreto = new LoginRequest("222.222.222-22", "minhaSenhaSecreta");

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginCorreto)))
                .andExpect(status().isOk()); // 200
    }

    @Test
    @DisplayName("POST /auth/cadastro - Deve retornar 400 Bad Request se o CPF for inválido")
    void cadastro_CpfInvalido() throws Exception {
        CadastroRequest request = new CadastroRequest(
                "Teste Invalido",
                "123", // CPF inválido
                "email@teste.com",
                LocalDate.of(2000, 1, 1),
                "senha123"
        );

        mockMvc.perform(post("/api/auth/cadastro")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest()); // 400 (capturado pelo ExceptionHandler)
    }

    @Test
    @DisplayName("POST /auth/cadastro - Deve retornar 400 Bad Request se campos forem nulos")
    void cadastro_CamposNulos() throws Exception {
        CadastroRequest request = new CadastroRequest(null, null, null, null, null);

        mockMvc.perform(post("/api/auth/cadastro")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest()); // 400
    }
}
