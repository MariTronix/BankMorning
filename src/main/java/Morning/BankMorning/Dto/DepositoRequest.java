package Morning.BankMorning.Dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

// Depósito: O usuário logado deposita na conta especificada (ou na própria, enviando o número)
public record DepositoRequest(
        @NotNull(message = "O valor é obrigatório.")
        @DecimalMin(value = "0.01", message = "O valor deve ser positivo.")
        BigDecimal valor,

        @NotBlank(message = "O número da conta de destino é obrigatório.")
        String numeroContaDestino) {}