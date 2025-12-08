package Morning.BankMorning.Model;

import Morning.BankMorning.Enum.TipoDeTransacao;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class TransacaoTest {

    @Test
    @DisplayName("Valor deve ser atribuído e recuperado corretamente")
    void valor_DeveSerAtribuidoCorretamente() {
        Transacao transacao = new Transacao();
        BigDecimal valorEsperado = new BigDecimal("500.00");

        transacao.setValor(valorEsperado);

        assertNotNull(transacao.getValor(), "O valor não deve ser nulo");
        assertEquals(valorEsperado, transacao.getValor());
    }

    @Test
    @DisplayName("Tipo de Transação deve ser atribuído corretamente")
    void tipo_DeveSerAtribuidoCorretamente() {
        Transacao transacao = new Transacao();
        TipoDeTransacao tipoEsperado = TipoDeTransacao.values()[0];

        transacao.setTipo(tipoEsperado);

        assertNotNull(transacao.getTipo(), "O tipo de transação não deve ser nulo");
        assertEquals(tipoEsperado, transacao.getTipo());
    }

    @Test
    @DisplayName("prePersist deve gerar DataHora automaticamente (não nula)")
    void prePersist_DeveGerarDataHora() {
        Transacao transacao = new Transacao();

        assertNull(transacao.getDataHora(), "Data deve ser nula antes de salvar");

        transacao.prePersist();

        assertNotNull(transacao.getDataHora(), "DataHora não pode vir vazia após persistir");
        assertTrue(transacao.getDataHora().isBefore(LocalDateTime.now().plusSeconds(1)));
    }

    @Test
    @DisplayName("Deve permitir associar contas de origem e destino")
    void deveAssociarContas() {
        Transacao transacao = new Transacao();
        Conta origem = new Conta();
        Conta destino = new Conta();

        origem.setAgencia("001");
        destino.setAgencia("002");

        transacao.setContaOrigem(origem);
        transacao.setContaDestino(destino);

        assertNotNull(transacao.getContaOrigem());
        assertNotNull(transacao.getContaDestino());
        assertEquals("001", transacao.getContaOrigem().getAgencia());
    }
}