package Morning.BankMorning.Controller;

import Morning.BankMorning.Dto.CadastroRequest;
import Morning.BankMorning.Dto.LoginRequest;
import Morning.BankMorning.Model.Conta;
import Morning.BankMorning.Model.Usuario;
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
    @Autowired private PasswordEncoder passwordEncoder;

    @Test
    @DisplayName("POST /auth/login - Deve retornar 403 se a senha estiver errada")
    void login_SenhaIncorreta() throws Exception {
        // 1. Cria usuário
        Usuario usuario = new Usuario();
        usuario.setNome("Hacker Teste");
        usuario.setCpf("111.111.111-11");
        usuario.setEmail("hacker@teste.com");
        usuario.setRole(Morning.BankMorning.Enum.Role.ROLE_USUARIO);
        usuario.setDataNascimento(LocalDate.of(1990, 1, 1));

        // 2. Cria a conta e define a senha NELA (Correção aqui!)
        Morning.BankMorning.Model.Conta conta = new Morning.BankMorning.Model.Conta();
        conta.setSenha(passwordEncoder.encode("senhaCerta123")); // Senha correta no banco
        conta.setUsuario(usuario);
        conta.setSaldo(java.math.BigDecimal.ZERO);
        conta.setAgencia("777");
        conta.setNumeroConta("99999");

        // Vínculo bidirecional
        usuario.setConta(conta);

        // Salva (O Cascade do usuário deve salvar a conta, ou salvamos a conta explicitamente)
        // Por garantia no teste, salvamos o usuario, que deve salvar a conta se estiver configurado com CascadeType.ALL
        usuarioRepository.save(usuario);
        // Se der erro que a conta não salvou, descomente a linha abaixo:
        // contaRepository.save(conta);

        // 3. Tenta logar com senha errada
        LoginRequest loginErrado = new LoginRequest("111.111.111-11", "senhaErrada!!!");

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginErrado)))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("POST /auth/cadastro - Deve retornar 400 Bad Request se o CPF for inválido")
    void cadastro_CpfInvalido() throws Exception {
        // CPF com dígitos a menos ou letras
        CadastroRequest request = new CadastroRequest(
                "Teste Invalido",
                "123.456.789-0", // CPF errado
                "email@teste.com",
                LocalDate.of(2000, 1, 1),
                "senha123"
        );

        mockMvc.perform(post("/auth/cadastro")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest()); // Espera erro de validação @Valid
    }

    @Test
    @DisplayName("POST /auth/cadastro - Deve retornar 400 Bad Request se campos obrigatórios forem nulos")
    void cadastro_CamposNulos() throws Exception {
        // Enviando JSON vazio/nulo
        CadastroRequest request = new CadastroRequest(null, null, null, null, null);

        mockMvc.perform(post("/auth/cadastro")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }
}