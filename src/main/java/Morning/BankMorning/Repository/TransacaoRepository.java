package Morning.BankMorning.Repository;

import Morning.BankMorning.Model.Conta;
import Morning.BankMorning.Model.Transacao;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface TransacaoRepository extends JpaRepository<Transacao, Integer> {
    List<Transacao> findAllByContaOrigemOrContaDestinoOrderByDataHoraDesc(Conta origem, Conta destino);
}

