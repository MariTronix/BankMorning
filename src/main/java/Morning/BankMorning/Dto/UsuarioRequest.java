package Morning.BankMorning.Dto;

import jakarta.validation.constraints.Email;

import java.time.LocalDate;

public record UsuarioRequest(
        String nome,
        @Email(message = "Email Inválido") String email,
        LocalDate data_nascimento) {
}