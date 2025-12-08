package Morning.BankMorning.Dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

public class TransferenciaRequest {

    // Mantemos a origem aqui para o Service usar.
    // O Controller pode preencher isso automaticamente via Token ou receber do JSON.
    @NotBlank(message = "O número da conta de origem é obrigatório.")
    private String numeroContaOrigem;

    @NotBlank(message = "O número da conta de destino é obrigatório.")
    private String numeroContaDestino;

    @NotNull(message = "O valor é obrigatório.")
    @DecimalMin(value = "0.01", message = "O valor deve ser positivo.")
    private BigDecimal valor;

    // --- 1. CONSTRUTOR VAZIO ---
    // Obrigatório para o Spring/Jackson converter o JSON
    public TransferenciaRequest() {}

    // --- 2. CONSTRUTOR COMPLETO ---
    // Usado nos Testes Unitários: new TransferenciaRequest("123", "456", valor);
    public TransferenciaRequest(String numeroContaOrigem, String numeroContaDestino, BigDecimal valor) {
        this.numeroContaOrigem = numeroContaOrigem;
        this.numeroContaDestino = numeroContaDestino;
        this.valor = valor;
    }

    // --- GETTERS E SETTERS ---
    public String getNumeroContaOrigem() {
        return numeroContaOrigem;
    }

    public void setNumeroContaOrigem(String numeroContaOrigem) {
        this.numeroContaOrigem = numeroContaOrigem;
    }

    public String getNumeroContaDestino() {
        return numeroContaDestino;
    }

    public void setNumeroContaDestino(String numeroContaDestino) {
        this.numeroContaDestino = numeroContaDestino;
    }

    public BigDecimal getValor() {
        return valor;
    }

    public void setValor(BigDecimal valor) {
        this.valor = valor;
    }
}