package Morning.BankMorning.Service;

import Morning.BankMorning.Dto.ContaRequest;
import Morning.BankMorning.Dto.ContaResponse;
import Morning.BankMorning.Dto.UsuarioResponse;
import Morning.BankMorning.Exception.ArgumentoInvalidoException;
import Morning.BankMorning.Exception.RecursoNaoEncontradoException;
import Morning.BankMorning.Model.Conta;
import Morning.BankMorning.Model.Usuario;
import Morning.BankMorning.Repository.ContaRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ContaServiceTest {

    @Mock
    private ContaRepository contaRepository;

    @Mock
    private UsuarioService usuarioService;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private ContaService contaService;

    // --- TESTES DE CRIAÇÃO (CRIAR CONTA) ---

    @Test
    @DisplayName("Deve criar conta com sucesso: Senha criptografada, Saldo 0, Agência 777")
    void criarConta_Sucesso() {
        // Criando o usuário
        Usuario usuario = new Usuario();
        usuario.setCpf("123.456.789-00");
        usuario.setNome("Teste");

        ContaRequest request = new ContaRequest("senha123", usuario);

        when(contaRepository.findByUsuario_Cpf(usuario.getCpf())).thenReturn(Optional.empty());

        when(passwordEncoder.encode("senha123")).thenReturn("HASH_SENHA_FORTE");

        when(contaRepository.save(any(Conta.class))).thenAnswer(invocation -> {
            Conta c = invocation.getArgument(0);
            c.setIdConta(1);
            return c;
        });

        // Simula a conversão para DTO de resposta
        when(usuarioService.converterParaResponse(any(Usuario.class)))
                .thenReturn(new UsuarioResponse("Teste", "123.456.789-00", "email@teste.com"));

        ContaResponse response = contaService.criarConta(usuario, request);

        assertNotNull(response);
        assertEquals(BigDecimal.ZERO, response.saldo());
        assertEquals("777", response.agencia());

        ArgumentCaptor<Conta> contaCaptor = ArgumentCaptor.forClass(Conta.class);
        verify(contaRepository).save(contaCaptor.capture());

        Conta contaSalva = contaCaptor.getValue();
        assertEquals("HASH_SENHA_FORTE", contaSalva.getSenha());
        assertNotNull(contaSalva.getNumeroConta());
    }

    @Test
    @DisplayName("Deve lançar erro ao tentar criar conta para usuário que JÁ POSSUI conta")
    void criarConta_ErroDuplicidade() {
        Usuario usuario = new Usuario();
        usuario.setCpf("123.456.789-00");
        ContaRequest request = new ContaRequest("senha", usuario);

        // Mock: Já existe uma conta retornada para esse CPF
        when(contaRepository.findByUsuario_Cpf(usuario.getCpf())).thenReturn(Optional.of(new Conta()));

        assertThrows(ArgumentoInvalidoException.class, () -> {
            contaService.criarConta(usuario, request);
        });

        verify(contaRepository, never()).save(any());
    }

    // --- TESTES DE BUSCA ---

    @Test
    @DisplayName("Deve buscar conta por ID com sucesso")
    void buscarPorId_Sucesso() {
        Integer id = 1;
        Conta conta = criarContaMock(id);

        when(contaRepository.findById(id)).thenReturn(Optional.of(conta));
        mockConversaoUsuario();

        ContaResponse response = contaService.buscarContaPorId(id);

        assertNotNull(response);
        assertEquals(id, response.idConta());
    }

    @Test
    @DisplayName("Deve lançar erro ao buscar conta por ID inexistente")
    void buscarPorId_NaoEncontrado() {
        when(contaRepository.findById(99)).thenReturn(Optional.empty());

        assertThrows(RecursoNaoEncontradoException.class, () -> {
            contaService.buscarContaPorId(99);
        });
    }

    @Test
    @DisplayName("Deve buscar conta por CPF com sucesso")
    void buscarPorCpf_Sucesso() {
        String cpf = "123.456.789-00";
        Conta conta = criarContaMock(1);

        when(contaRepository.findByUsuario_Cpf(cpf)).thenReturn(Optional.of(conta));
        mockConversaoUsuario();

        ContaResponse response = contaService.buscarContaPorCpfUsuario(cpf);

        assertNotNull(response);
    }

    @Test
    @DisplayName("Deve lançar erro ao buscar conta por CPF inexistente")
    void buscarPorCpf_NaoEncontrado() {
        String cpf = "000.000.000-00";
        when(contaRepository.findByUsuario_Cpf(cpf)).thenReturn(Optional.empty());

        assertThrows(RecursoNaoEncontradoException.class, () -> {
            contaService.buscarContaPorCpfUsuario(cpf);
        });
    }

    @Test
    @DisplayName("Deve buscar conta por Email com sucesso")
    void buscarPorEmail_Sucesso() {
        String email = "teste@email.com";
        Conta conta = criarContaMock(1);

        when(contaRepository.findByUsuario_Email(email)).thenReturn(Optional.of(conta));
        mockConversaoUsuario();

        ContaResponse response = contaService.buscarContaPorEmailUsuario(email);

        assertNotNull(response);
    }


    private Conta criarContaMock(Integer id) {
        Conta conta = new Conta();
        conta.setIdConta(id);
        conta.setAgencia("777");
        conta.setSaldo(BigDecimal.TEN);

        Usuario u = new Usuario();
        u.setCpf("123");
        u.setEmail("abc@test.com");
        u.setNome("User Test");
        conta.setUsuario(u);

        return conta;
    }

    private void mockConversaoUsuario() {
        // Sempre que o contaService chamar o usuarioService para converter, retorna um DTO padrão
        when(usuarioService.converterParaResponse(any(Usuario.class)))
                .thenReturn(new UsuarioResponse("User Test", "123", "abc@test.com"));
    }
}
