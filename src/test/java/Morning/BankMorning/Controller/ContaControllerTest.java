package Morning.BankMorning.Controller;

import Morning.BankMorning.Enum.Role;
import Morning.BankMorning.Model.Conta;
import Morning.BankMorning.Model.Usuario;
import Morning.BankMorning.Repository.ContaRepository;
import Morning.BankMorning.Repository.UsuarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class ContaControllerTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private UsuarioRepository usuarioRepository;
    @Autowired private ContaRepository contaRepository;
    @Autowired private PasswordEncoder passwordEncoder;

    private Long idContaExistente;

    @BeforeEach
    void setup() {
        // Criando a conta
        Usuario usuario = new Usuario();
        usuario.setNome("Dono da Conta");
        usuario.setCpf("777.888.999-00");
        usuario.setEmail("conta@teste.com");
        usuario.setRole(Role.ROLE_USUARIO);
        usuario.setDataNascimento(LocalDate.of(1990, 1, 1));
        usuarioRepository.save(usuario);

        Conta conta = new Conta();
        conta.setUsuario(usuario);
        conta.setSaldo(new BigDecimal("1500.00"));
        conta.setNumeroConta("12345");
        conta.setAgencia("777");
        conta.setSenha(passwordEncoder.encode("123"));

        usuario.setConta(conta); // Vínculo importante!

        Conta salva = contaRepository.save(conta);
        idContaExistente = Long.valueOf(salva.getIdConta());
    }

    @Test
    @DisplayName("Deve retornar dados da conta quando autenticado")
    @WithMockUser(roles = "USUARIO") // Simula usuário logado
    void getConta_Sucesso() throws Exception {
        mockMvc.perform(get("/api/account/id/" + idContaExistente))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.numeroConta").value("12345"))
                .andExpect(jsonPath("$.saldo").value(1500.00));
    }

    @Test
    @DisplayName("Deve retornar 404 se a conta não existir")
    @WithMockUser(roles = "USUARIO")
    void getConta_NaoEncontrada() throws Exception {
        mockMvc.perform(get("/api/account/id/" + 999999)) // ID inexistente
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Deve retornar 403 se não estiver logado")
    void getConta_SemToken_DeveFalhar() throws Exception {
        mockMvc.perform(get("/api/account/" + idContaExistente))
                .andExpect(status().isForbidden());
    }
}
