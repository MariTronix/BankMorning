package Morning.BankMorning.Dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

// Saque: O usuário logado saca de sua própria conta (definida pelo JWT)
public record SaqueRequest(
        @NotNull(message = "O valor é obrigatório.")
        @DecimalMin(value = "0.01", message = "O valor deve ser positivo.")
        BigDecimal valor) {}