package Morning.BankMorning.Repository;

import Morning.BankMorning.Model.Conta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ContaRepository extends JpaRepository<Conta, Integer> {
    boolean existsByNumeroConta(String numeroConta);

    Optional<Conta> findByNumeroConta(String numeroConta);
}