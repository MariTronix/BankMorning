package Morning.BankMorning.Repository;

import Morning.BankMorning.Model.Usuario;
import jakarta.validation.constraints.NotBlank;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;


@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Integer>{
    Optional<Usuario> findByEmail(String login);

    boolean existsByCpf(@NotBlank(message = "O CPF é obrigatório") String cpf);

    boolean existsByEmail(String email);
}

