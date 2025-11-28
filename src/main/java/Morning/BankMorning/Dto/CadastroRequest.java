package Morning.BankMorning.Dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.hibernate.validator.constraints.br.CPF;

import java.time.LocalDate;

public record CadastroRequest(@NotBlank(message = "O nome é obrigatório") String nome,
                              @NotBlank(message = "O CPF é obrigatório") @CPF(message = "Cpf Inválido") String cpf,
                              @NotBlank(message = "O Email é obrigatório") @Email(message = "Email Inválido") String email,
                              @NotNull(message = "A data de nascimento é obrigatória") LocalDate data_nascimento,
                              @NotBlank(message = "A senha é obrigatória") String senha) {
}
