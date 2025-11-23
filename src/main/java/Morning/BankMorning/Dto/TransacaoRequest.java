package Morning.BankMorning.Dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;
import java.math.BigDecimal;

@Data
public class TransacaoRequest {

    @NotNull(message = "ID da conta de origem é obrigatório")
    private Integer idContaOrigem;

    @NotNull(message = "ID da conta de destino é obrigatório")
    private Integer idContaDestino;

    @NotNull(message = "O valor é obrigatório")
    @Positive(message = "O valor da transferência deve ser maior que zero")
    private BigDecimal valor;
}