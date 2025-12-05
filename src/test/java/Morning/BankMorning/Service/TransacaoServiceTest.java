package Morning.BankMorning.Service;

import Morning.BankMorning.Dto.DepositoRequest;
import Morning.BankMorning.Dto.SaqueRequest;
import Morning.BankMorning.Dto.TransacaoResponse;
import Morning.BankMorning.Dto.TransferenciaRequest;
import Morning.BankMorning.Enum.TipoDeTransacao;
import Morning.BankMorning.Exception.ArgumentoInvalidoException;
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
    @DisplayName("Deve depositar valor corretamente e atualizar saldo")
    void depositar_Sucesso() {
        // 1. ARRANGE
        String numeroConta = "12345";
        BigDecimal saldoInicial = new BigDecimal("100.00");
        BigDecimal valorDeposito = new BigDecimal("50.00");

        Conta conta = criarContaMock(numeroConta, saldoInicial);

        // ALTERNATIVA: Usando Setters em vez de construtor
        DepositoRequest request = new DepositoRequest();
        request.setNumeroConta(numeroConta);
        request.setValor(valorDeposito);

        when(contaRepository.findByNumeroConta(numeroConta)).thenReturn(Optional.of(conta));
        when(transacaoRepository.save(any(Transacao.class))).thenAnswer(i -> i.getArguments()[0]);

        // 2. ACT
        TransacaoResponse response = transacaoService.depositar(request);

        // 3. ASSERT
        assertEquals(new BigDecimal("150.00"), conta.getSaldo());
        // Lembre-se: Como o Response é um Record, o método tem o nome do campo
        assertEquals(TipoDeTransacao.DEPOSITO, response.tipoDeTransacao());

        verify(contaRepository).save(conta);
    }

    @Test
    @DisplayName("Deve lançar erro ao tentar depositar valor negativo")
    void depositar_ErroValorInvalido() {
        // Usando Setters
        DepositoRequest request = new DepositoRequest();
        request.setNumeroConta("12345");
        request.setValor(new BigDecimal("-10.00"));

        assertThrows(ArgumentoInvalidoException.class, () -> {
            transacaoService.depositar(request);
        });

        verify(contaRepository, never()).save(any());
    }

    // --- TESTES DE SAQUE ---

    @Test
    @DisplayName("Deve sacar valor com sucesso quando tem saldo")
    void sacar_Sucesso() {
        Conta conta = criarContaMock("12345", new BigDecimal("100.00"));

        // Usando Setters
        SaqueRequest request = new SaqueRequest();
        request.setNumeroConta("12345");
        request.setValor(new BigDecimal("40.00"));

        when(contaRepository.findByNumeroConta("12345")).thenReturn(Optional.of(conta));
        when(transacaoRepository.save(any(Transacao.class))).thenAnswer(i -> i.getArguments()[0]);

        transacaoService.sacar(request);

        assertEquals(new BigDecimal("60.00"), conta.getSaldo());
        verify(contaRepository).save(conta);
    }

    @Test
    @DisplayName("Deve impedir saque quando saldo é insuficiente")
    void sacar_ErroSaldoInsuficiente() {
        Conta conta = criarContaMock("12345", new BigDecimal("10.00"));

        // Usando Setters
        SaqueRequest request = new SaqueRequest();
        request.setNumeroConta("12345");
        request.setValor(new BigDecimal("50.00"));

        when(contaRepository.findByNumeroConta("12345")).thenReturn(Optional.of(conta));

        assertThrows(ArgumentoInvalidoException.class, () -> {
            transacaoService.sacar(request);
        });

        assertEquals(new BigDecimal("10.00"), conta.getSaldo());
        verify(contaRepository, never()).save(conta);
    }

    // --- TESTES DE TRANSFERÊNCIA ---

    @Test
    @DisplayName("Deve transferir valor corretamente entre duas contas")
    void transferir_Sucesso() {
        Conta origem = criarContaMock("11111", new BigDecimal("200.00"));
        Conta destino = criarContaMock("22222", new BigDecimal("50.00"));

        // Usando Setters (atenção aos nomes exatos dos seus campos)
        TransferenciaRequest request = new TransferenciaRequest();
        request.setNumeroContaOrigem("11111");
        request.setNumeroContaDestino("22222");
        request.setValor(new BigDecimal("100.00"));

        when(contaRepository.findByNumeroConta("11111")).thenReturn(Optional.of(origem));
        when(contaRepository.findByNumeroConta("22222")).thenReturn(Optional.of(destino));
        when(transacaoRepository.save(any(Transacao.class))).thenAnswer(i -> i.getArguments()[0]);

        transacaoService.transferir(request);

        assertEquals(new BigDecimal("100.00"), origem.getSaldo());
        assertEquals(new BigDecimal("150.00"), destino.getSaldo());

        verify(contaRepository).save(origem);
        verify(contaRepository).save(destino);
    }

    private Conta criarContaMock(String numero, BigDecimal saldo) {
        Conta c = new Conta();
        c.setNumeroConta(numero);
        c.setSaldo(saldo);
        Usuario u = new Usuario();
        u.setCpf("000.000.000-00");
        c.setUsuario(u);
        return c;
    }
}