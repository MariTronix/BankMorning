package Morning.BankMorning.Dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

public class TransferenciaRequest {

    @NotBlank(message = "O número da conta de destino é obrigatório.")
    private String numeroContaDestino;

    @NotNull(message = "O valor é obrigatório.")
    @DecimalMin(value = "0.01", message = "O valor deve ser positivo.")
    private BigDecimal valor;

    // Construtor vazio para conseguir converter o JSON
    public TransferenciaRequest() {}

    // COnstrutor para testes
    public TransferenciaRequest(String numeroContaDestino, BigDecimal valor) {
        this.numeroContaDestino = numeroContaDestino;
        this.valor = valor;
    }

    // Getters/Setters
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
