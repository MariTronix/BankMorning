package Morning.BankMorning.Repository;

import Morning.BankMorning.Model.Transacao;
import Morning.BankMorning.Model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TransacaoRepository extends JpaRepository<Transacao, Integer> {
    List<Transacao> findByContaOrigem_IdContaOrContaDestino_IdConta(Integer idOrigem, Integer idDestino);
}

