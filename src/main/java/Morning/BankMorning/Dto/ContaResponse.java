package Morning.BankMorning.Dto;

import Morning.BankMorning.Model.Usuario;

import java.math.BigDecimal;

public record ContaResponse(Integer idConta, String agencia, BigDecimal saldo, UsuarioResponse usuario) {
}
//