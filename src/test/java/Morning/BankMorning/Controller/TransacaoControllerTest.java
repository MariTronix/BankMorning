package Morning.BankMorning.Controller;

import Morning.BankMorning.Dto.DepositoRequest;
import Morning.BankMorning.Dto.SaqueRequest;
import Morning.BankMorning.Dto.TransferenciaRequest;
import Morning.BankMorning.Enum.Role;
import Morning.BankMorning.Model.Conta;
import Morning.BankMorning.Model.Usuario;
import Morning.BankMorning.Repository.ContaRepository;
import Morning.BankMorning.Repository.UsuarioRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class TransacaoControllerTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;
    @Autowired private UsuarioRepository usuarioRepository;
    @Autowired private ContaRepository contaRepository;
    @Autowired private PasswordEncoder passwordEncoder;

    private Conta contaPrincipal;
    private Conta contaSecundaria;

    @BeforeEach
    void setup() {
        // Criando a conta no banco H2
        Usuario u1 = criarUsuario("User 1", "111.111.111-11", "u1@test.com");
        contaPrincipal = criarConta(u1, "10001");

        Usuario u2 = criarUsuario("User 2", "222.222.222-22", "u2@test.com");
        contaSecundaria = criarConta(u2, "20002");
    }

    @Test
    @DisplayName("POST - Deve depositar com sucesso")
    @WithMockUser(username = "u1@test.com", roles = "USUARIO")
    void depositar_Sucesso() throws Exception {
        // Criando o objeto
        DepositoRequest request = new DepositoRequest();
        request.setNumeroConta(contaPrincipal.getNumeroConta());
        request.setValor(new BigDecimal("500.00"));

        mockMvc.perform(post("/api/transacoes/depositar")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.tipoDeTransacao").value("DEPOSITO")) // Verifica o JSON de resposta
                .andExpect(jsonPath("$.valor").value(500.00));

        // Verificando se o saldo mudou no banco
        Conta contaAtualizada = contaRepository.findById(contaPrincipal.getIdConta()).get();
        assertEquals(0, new BigDecimal("500.00").compareTo(contaAtualizada.getSaldo()));
    }

    @Test
    @DisplayName("POST - Deve sacar com sucesso")
    @WithMockUser(username = "u1@test.com", roles = "USUARIO")
    void sacar_Sucesso() throws Exception {
        // Injetando saldo primeiro (simulação direta no banco)
        contaPrincipal.setSaldo(new BigDecimal("1000.00"));
        contaRepository.save(contaPrincipal);

        // Preparando o Request de Saque
        SaqueRequest request = new SaqueRequest();
        request.setValor(new BigDecimal("200.00"));

        // Executando o teste
        mockMvc.perform(post("/api/transacoes/sacar")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.tipoDeTransacao").value("SAQUE"));

        // Verifica Saldo Final (1000 - 200 = 800)
        Conta contaAtualizada = contaRepository.findById(contaPrincipal.getIdConta()).get();
        assertEquals(0, new BigDecimal("800.00").compareTo(contaAtualizada.getSaldo()));
    }

    @Test
    @DisplayName("POST - Deve retornar ERRO se saldo for insuficiente")
    @WithMockUser(username = "u1@test.com", roles = "USUARIO")
    void sacar_SemSaldo() throws Exception {
        // Conta começa com zero
        SaqueRequest request = new SaqueRequest();
        request.setValor(new BigDecimal("50.00"));

        mockMvc.perform(post("/api/transacoes/sacar")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("POST - Deve transferir entre contas")
    @WithMockUser(username = "u1@test.com", roles = "USUARIO")
    void transferir_Sucesso() throws Exception {
        contaPrincipal.setSaldo(new BigDecimal("1000.00"));
        contaRepository.save(contaPrincipal);

        // Preparando a Transferência
        TransferenciaRequest request = new TransferenciaRequest();
        request.setNumeroContaDestino(contaSecundaria.getNumeroConta());
        request.setValor(new BigDecimal("300.00"));

        // Executando o teste
        mockMvc.perform(post("/api/transacoes/transferir")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.tipoDeTransacao").value("TRANSFERENCIA"));

        // Verifica Saldos
        Conta origem = contaRepository.findById(contaPrincipal.getIdConta()).get();
        Conta destino = contaRepository.findById(contaSecundaria.getIdConta()).get();

        assertEquals(0, new BigDecimal("700.00").compareTo(origem.getSaldo()));
        assertEquals(0, new BigDecimal("300.00").compareTo(destino.getSaldo()));
    }

    // Populando o banco antes do teste
    private Usuario criarUsuario(String nome, String cpf, String email) {
        Usuario u = new Usuario();
        u.setNome(nome);
        u.setCpf(cpf);
        u.setEmail(email);
        u.setDataNascimento(LocalDate.of(1990, 1, 1));
        u.setRole(Role.ROLE_USUARIO);
        return usuarioRepository.save(u);
    }

    private Conta criarConta(Usuario usuario, String numeroConta) {
        Conta c = new Conta();
        c.setUsuario(usuario);
        c.setSaldo(BigDecimal.ZERO);
        c.setSenha(passwordEncoder.encode("123"));
        c.setNumeroConta(numeroConta);
        c.setAgencia("777");
        usuario.setConta(c);
        return contaRepository.save(c);
    }
}
