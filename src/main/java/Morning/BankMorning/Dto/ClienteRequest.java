package Morning.BankMorning.Dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.hibernate.validator.constraints.br.CPF;
import java.time.LocalDate;

@Data
public class ClienteRequest {
    @NotBlank(message = "O nome é obrigatório")
    private String nome;

    @NotBlank(message = "O CPF é obrigatório")
    private String cpf;

    private String email;

    @NotNull(message = "A data de nascimento é obrigatória")
    private LocalDate data_nascimento;

    @NotBlank(message = "A senha é obrigatória")
    private String senha;
}
