package Morning.BankMorning.Repository;

import Morning.BankMorning.Model.Conta;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface ContaRepository extends JpaRepository<Conta, Integer> {
    // Mantendo a assinatura original, sem @Query
    Optional<Conta> findByUsuario_Cpf(String cpf);
    Optional<Conta> findByUsuario_Email(String email);
    Optional<Conta> findByNumeroConta(String numeroConta);
}