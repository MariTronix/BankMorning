package Morning.BankMorning.Model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class ContaTest {

    @Test
    @DisplayName("Agência deve nascer preenchida (não nula/vazia) e com valor padrão 777")
    void agencia_DeveTerValorPadraoValido() {
        Conta conta = new Conta();

        // Verifica se não é nulo
        assertNotNull(conta.getAgencia(), "A agência não deve ser nula");

        // Verifica se não está vazia ""
        assertFalse(conta.getAgencia().isEmpty(), "A agência não deve estar vazia");

        // Verifica se é o valor esperado "777"
        assertEquals("777", conta.getAgencia(), "A agência padrão deve ser 777");
    }

    @Test
    @DisplayName("Saldo deve iniciar zerado (0) e não nulo")
    void saldo_DeveIniciarZerado() {
        Conta conta = new Conta();

        // Verifica se o saldo não é nulo (graças ao ajuste no Model)
        assertNotNull(conta.getSaldo(), "O saldo inicial não deve ser nulo");

        // Verifica se o valor é 0. O compareTo é mais seguro para BigDecimal que o equals
        assertEquals(0, conta.getSaldo().compareTo(BigDecimal.ZERO), "O saldo inicial deve ser 0");
    }

    @Test
    @DisplayName("Data de criação deve ser gerada e não nula após prePersist")
    void dataCriacao_NaoDeveSerVaziaAposPersistir() {
        Conta conta = new Conta();

        // Antes de persistir, deve ser null (pois o banco ainda não tocou nela)
        assertNull(conta.getDataCriacao(), "Data deve ser nula antes de salvar");

        // Simula o evento do banco de dados
        conta.prePersist();

        // Agora valida se foi preenchida
        assertNotNull(conta.getDataCriacao(), "Data de criação não pode vir vazia/nula após persistir");
    }
}