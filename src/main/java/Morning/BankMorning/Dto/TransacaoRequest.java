package Morning.BankMorning.Dto;

import Morning.BankMorning.Enum.TipoDeTransacao;
import Morning.BankMorning.Model.Conta;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record TransacaoRequest(
        String cpfContaOrigem,
        String cpfContaDestino,
        @NotNull(message = "Valor é obrigatório") BigDecimal valor,
        @NotNull(message = "Tipo de Transação é obrigatório") TipoDeTransacao tipoDeTransacao) {
}