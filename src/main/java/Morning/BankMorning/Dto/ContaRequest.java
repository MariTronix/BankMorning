package Morning.BankMorning.Dto;

import Morning.BankMorning.Model.Usuario;
import jakarta.validation.constraints.NotBlank;

public record ContaRequest(@NotBlank(message = "A senha é obrigatória") String senha,
                           Usuario usuario) {
}
//