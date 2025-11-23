package Morning.BankMorning.Dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.math.BigDecimal;

@Data
public class CriarContaRequest {

    @NotNull(message = "O ID do cliente é obrigatório")
    private Integer idCliente;

    private BigDecimal saldoInicial;
}