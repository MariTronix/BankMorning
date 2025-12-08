package Morning.BankMorning.Repository;

import Morning.BankMorning.Model.Transacao;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TransacaoRepository extends JpaRepository<Transacao, Integer> {
}

//