package Morning.BankMorning.Dto;

import jakarta.validation.constraints.DecimalMin;
// import jakarta.validation.constraints.NotBlank; // REMOVIDO
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

public class SaqueRequest {
    @NotNull(message = "O valor é obrigatório.")
    @DecimalMin(value = "0.01", message = "O valor deve ser positivo.")
    private BigDecimal valor;

    // Construtor Vazio
    public SaqueRequest() {}

    // COnstrutor para os testes
    public SaqueRequest(BigDecimal valor) {
        this.valor = valor;
    }


    public BigDecimal getValor() {
        return valor;
    }

    public void setValor(BigDecimal valor) {
        this.valor = valor;
    }
}
