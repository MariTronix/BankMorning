package Morning.BankMorning.Service;

import Morning.BankMorning.Dto.ContaRequest;
import Morning.BankMorning.Dto.ContaResponse;
import Morning.BankMorning.Exception.ArgumentoInvalidoException;
import Morning.BankMorning.Model.Conta;
import Morning.BankMorning.Model.Usuario;
import Morning.BankMorning.Repository.ContaRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ContaServiceTest {

    @Mock
    private ContaRepository contaRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private ContaService contaService;

    @Test
    @DisplayName("Deve criar conta com sucesso (Saldo zero, senha codificada, número gerado)")
    void criarConta_Sucesso() {
        // 1. ARRANGE
        Usuario usuario = new Usuario();
        usuario.setCpf("123.456.789-00");
        usuario.setNome("Cliente Teste");

        // Usando o Record ou Classe ContaRequest (depende da sua implementação, ajustei para Record que é o padrão novo)
        // Se for classe, use o construtor: new ContaRequest("senha123");
        ContaRequest request = new ContaRequest("senha123", null);

        // Simula que o usuário ainda NÃO tem conta
        when(contaRepository.findByUsuario_Cpf(usuario.getCpf())).thenReturn(Optional.empty());

        // Mock do encoder de senha
        when(passwordEncoder.encode("senha123")).thenReturn("HASH_SENHA_SEGURA");

        // Simula o salvamento e retorna a conta criada
        when(contaRepository.save(any(Conta.class))).thenAnswer(invocation -> {
            Conta c = invocation.getArgument(0);
            c.setIdConta(1); // Simula ID gerado
            return c;
        });

        // 2. ACT
        ContaResponse response = contaService.criarConta(usuario, request);

        // 3. ASSERT
        assertNotNull(response);

        // Verifica se a conta foi salva com os valores padrão corretos
        verify(contaRepository).save(argThat(conta ->
                conta.getSaldo().compareTo(BigDecimal.ZERO) == 0 && // Saldo deve ser 0
                        conta.getAgencia().equals("777") &&                 // Agência padrão
                        conta.getSenha().equals("HASH_SENHA_SEGURA") &&     // Senha criptografada
                        conta.getNumeroConta() != null                      // Número gerado aleatoriamente
        ));

        // Verifica se o vinculo bidirecional foi feito (Correção crítica que fizemos antes)
        assertNotNull(usuario.getConta());
        assertEquals(usuario.getConta().getSenha(), "HASH_SENHA_SEGURA");
    }

    @Test
    @DisplayName("Deve lançar erro ao tentar criar segunda conta para mesmo usuário")
    void criarConta_ErroDuplicidade() {
        // 1. ARRANGE
        Usuario usuario = new Usuario();
        usuario.setCpf("111.222.333-44");

        ContaRequest request = new ContaRequest("senha123", null);

        // Simula que JÁ EXISTE uma conta para este CPF
        when(contaRepository.findByUsuario_Cpf(usuario.getCpf())).thenReturn(Optional.of(new Conta()));

        // 2. ACT & ASSERT
        assertThrows(ArgumentoInvalidoException.class, () -> {
            contaService.criarConta(usuario, request);
        });

        // Garante que não salvou nada novo
        verify(contaRepository, never()).save(any());
    }
}