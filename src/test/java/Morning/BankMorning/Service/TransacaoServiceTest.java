package Morning.BankMorning.Service;

import Morning.BankMorning.Dto.DepositoRequest;
import Morning.BankMorning.Dto.SaqueRequest;
import Morning.BankMorning.Dto.TransacaoResponse;
import Morning.BankMorning.Dto.TransferenciaRequest;
import Morning.BankMorning.Enum.TipoDeTransacao;
import Morning.BankMorning.Exception.ArgumentoInvalidoException;
import Morning.BankMorning.Exception.RecursoNaoEncontradoException;
import Morning.BankMorning.Model.Conta;
import Morning.BankMorning.Model.Transacao;
import Morning.BankMorning.Model.Usuario;
import Morning.BankMorning.Repository.ContaRepository;
import Morning.BankMorning.Repository.TransacaoRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TransacaoServiceTest {

    @Mock
    private ContaRepository contaRepository;

    @Mock
    private TransacaoRepository transacaoRepository;

    @InjectMocks
    private TransacaoService transacaoService;

    // --- TESTES DE DEPÓSITO ---

    @Test
    @DisplayName("DEPOSITO: Deve funcionar com valor positivo e conta existente")
    void depositar_Sucesso() {
        // Cenário
        Conta conta = criarContaComUsuario(1, "12345", new BigDecimal("100.00"));
        DepositoRequest request = new DepositoRequest("12345", new BigDecimal("50.00"));

        when(contaRepository.findByNumeroConta("12345")).thenReturn(Optional.of(conta));

        // Mock do salvamento da transação para retornar algo consistente
        when(transacaoRepository.save(any(Transacao.class))).thenAnswer(invocation -> {
            Transacao t = invocation.getArgument(0);
            t.setContaDestino(conta); // Garante que a transação salva tem a conta destino
            return t;
        });

        // Ação
        TransacaoResponse response = transacaoService.depositar(request);

        // Verificação
        assertNotNull(response);

        // CORREÇÃO: Usando os nomes do seu Record
        assertEquals(TipoDeTransacao.DEPOSITO, response.tipoDeTransacao());
        assertEquals(new BigDecimal("50.00"), response.valor());
        assertEquals("123.456.789-00", response.cpfContaDestino()); // Verifica se o CPF veio certo

        // Verifica atualização de saldo
        assertEquals(new BigDecimal("150.00"), conta.getSaldo());
        verify(contaRepository).save(conta);
    }

    @Test
    @DisplayName("DEPOSITO: Deve falhar com valor negativo ou zero")
    void depositar_ErroValorInvalido() {
        DepositoRequest requestNegativo = new DepositoRequest("12345", new BigDecimal("-10.00"));
        DepositoRequest requestZero = new DepositoRequest("12345", BigDecimal.ZERO);

        assertThrows(ArgumentoInvalidoException.class, () -> transacaoService.depositar(requestNegativo));
        assertThrows(ArgumentoInvalidoException.class, () -> transacaoService.depositar(requestZero));

        verify(contaRepository, never()).save(any());
    }

    @Test
    @DisplayName("DEPOSITO: Deve falhar se a conta não existir")
    void depositar_ErroContaNaoEncontrada() {
        DepositoRequest request = new DepositoRequest("99999", new BigDecimal("50.00"));

        when(contaRepository.findByNumeroConta("99999")).thenReturn(Optional.empty());

        assertThrows(RecursoNaoEncontradoException.class, () -> transacaoService.depositar(request));
    }

    // --- TESTES DE SAQUE ---

    @Test
    @DisplayName("SAQUE: Deve funcionar com saldo suficiente")
    void sacar_Sucesso() {
        Conta conta = criarContaComUsuario(1, "12345", new BigDecimal("100.00"));
        SaqueRequest request = new SaqueRequest("12345", new BigDecimal("40.00"));

        when(contaRepository.findByNumeroConta("12345")).thenReturn(Optional.of(conta));
        when(transacaoRepository.save(any(Transacao.class))).thenAnswer(invocation -> {
            Transacao t = invocation.getArgument(0);
            t.setContaOrigem(conta);
            return t;
        });

        TransacaoResponse response = transacaoService.sacar(request);

        assertEquals(TipoDeTransacao.SAQUE, response.tipoDeTransacao()); // Correção do nome
        assertEquals(new BigDecimal("60.00"), conta.getSaldo());
    }

    @Test
    @DisplayName("SAQUE: Deve falhar se o saldo for insuficiente")
    void sacar_ErroSaldoInsuficiente() {
        Conta conta = criarContaComUsuario(1, "12345", new BigDecimal("10.00"));
        SaqueRequest request = new SaqueRequest("12345", new BigDecimal("50.00"));

        when(contaRepository.findByNumeroConta("12345")).thenReturn(Optional.of(conta));

        assertThrows(ArgumentoInvalidoException.class, () -> transacaoService.sacar(request));

        assertEquals(new BigDecimal("10.00"), conta.getSaldo());
    }

    @Test
    @DisplayName("SAQUE: Deve falhar com valor negativo")
    void sacar_ErroValorNegativo() {
        SaqueRequest request = new SaqueRequest("12345", new BigDecimal("-50.00"));
        assertThrows(ArgumentoInvalidoException.class, () -> transacaoService.sacar(request));
    }

    // --- TESTES DE TRANSFERÊNCIA ---

    @Test
    @DisplayName("TRANSFERENCIA: Deve funcionar entre contas existentes com saldo")
    void transferir_Sucesso() {
        Conta origem = criarContaComUsuario(1, "ORIGEM", new BigDecimal("100.00"));
        Conta destino = criarContaComUsuario(2, "DESTINO", new BigDecimal("0.00"));

        TransferenciaRequest request = new TransferenciaRequest("ORIGEM", "DESTINO", new BigDecimal("50.00"));

        when(contaRepository.findByNumeroConta("ORIGEM")).thenReturn(Optional.of(origem));
        when(contaRepository.findByNumeroConta("DESTINO")).thenReturn(Optional.of(destino));

        when(transacaoRepository.save(any(Transacao.class))).thenAnswer(invocation -> {
            Transacao t = invocation.getArgument(0);
            t.setContaOrigem(origem);
            t.setContaDestino(destino);
            return t;
        });

        TransacaoResponse response = transacaoService.transferir(request);

        assertNotNull(response);
        // CORREÇÃO: Usando os nomes do Record
        assertEquals(TipoDeTransacao.TRANSFERENCIA, response.tipoDeTransacao());
        assertEquals("123.456.789-00", response.cpfContaOrigem());
        assertEquals("123.456.789-00", response.cpfContaDestino());

        assertEquals(new BigDecimal("50.00"), origem.getSaldo());
        assertEquals(new BigDecimal("50.00"), destino.getSaldo());

        verify(contaRepository).save(origem);
        verify(contaRepository).save(destino);
    }

    @Test
    @DisplayName("TRANSFERENCIA: Deve falhar se conta origem não existe")
    void transferir_ErroOrigemInexistente() {
        TransferenciaRequest request = new TransferenciaRequest("ORIGEM", "DESTINO", BigDecimal.TEN);
        when(contaRepository.findByNumeroConta("ORIGEM")).thenReturn(Optional.empty());

        assertThrows(RecursoNaoEncontradoException.class, () -> transacaoService.transferir(request));
    }

    @Test
    @DisplayName("TRANSFERENCIA: Deve falhar se conta destino não existe")
    void transferir_ErroDestinoInexistente() {
        Conta origem = criarContaComUsuario(1, "ORIGEM", new BigDecimal("100.00"));
        TransferenciaRequest request = new TransferenciaRequest("ORIGEM", "DESTINO", BigDecimal.TEN);

        when(contaRepository.findByNumeroConta("ORIGEM")).thenReturn(Optional.of(origem));
        when(contaRepository.findByNumeroConta("DESTINO")).thenReturn(Optional.empty());

        assertThrows(RecursoNaoEncontradoException.class, () -> transacaoService.transferir(request));
    }

    @Test
    @DisplayName("TRANSFERENCIA: Deve falhar por saldo insuficiente na origem")
    void transferir_ErroSaldoInsuficiente() {
        Conta origem = criarContaComUsuario(1, "ORIGEM", new BigDecimal("10.00"));
        Conta destino = criarContaComUsuario(2, "DESTINO", new BigDecimal("0.00"));

        TransferenciaRequest request = new TransferenciaRequest("ORIGEM", "DESTINO", new BigDecimal("50.00"));

        when(contaRepository.findByNumeroConta("ORIGEM")).thenReturn(Optional.of(origem));
        when(contaRepository.findByNumeroConta("DESTINO")).thenReturn(Optional.of(destino));

        assertThrows(ArgumentoInvalidoException.class, () -> transacaoService.transferir(request));

        assertEquals(new BigDecimal("10.00"), origem.getSaldo());
        assertEquals(new BigDecimal("0.00"), destino.getSaldo());
    }

    // --- MÉTODOS AUXILIARES ---

    private Conta criarContaComUsuario(Integer idConta, String numeroConta, BigDecimal saldo) {
        Usuario usuario = new Usuario();
        usuario.setNome("Usuario Teste");
        usuario.setCpf("123.456.789-00"); // Importante estar preenchido para o response não quebrar

        Conta conta = new Conta();
        conta.setIdConta(idConta);
        conta.setNumeroConta(numeroConta);
        conta.setSaldo(saldo);
        conta.setUsuario(usuario);

        return conta;
    }
}