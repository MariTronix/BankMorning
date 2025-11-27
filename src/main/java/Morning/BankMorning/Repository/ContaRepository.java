package Morning.BankMorning.Repository;

import Morning.BankMorning.Model.Conta;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface ContaRepository extends JpaRepository<Conta, Integer> {
    Optional<Conta> findByNumeroConta(Integer numeroConta);
}
