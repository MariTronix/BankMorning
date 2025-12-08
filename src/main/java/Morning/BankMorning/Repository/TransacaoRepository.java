package Morning.BankMorning.Repository;

import Morning.BankMorning.Model.Conta;
import Morning.BankMorning.Model.Transacao;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TransacaoRepository extends JpaRepository<Transacao, Integer> {

    /**
     * Busca todas as transações para a conta do usuário, sendo a conta Origem OU Destino.
     * O uso de JOIN FETCH carrega o relacionamento Conta -> Usuario na mesma consulta (EAGER),
     * evitando o erro de Lazy Loading (500 Internal Server Error) no serviço.
     * * Assinatura simplificada para aceitar apenas 1 parâmetro (a conta do usuário).
     */
    @Query("SELECT t FROM Transacao t " +
            // Carrega ContaOrigem e o Usuário associado
            "LEFT JOIN FETCH t.contaOrigem co LEFT JOIN FETCH co.usuario " +
            // Carrega ContaDestino e o Usuário associado
            "LEFT JOIN FETCH t.contaDestino cd LEFT JOIN FETCH cd.usuario " +
            "WHERE t.contaOrigem = :contaDoUsuario OR t.contaDestino = :contaDoUsuario " +
            "ORDER BY t.dataHora DESC")
    // Assinatura corrigida para 1 parâmetro:
    List<Transacao> findAllByContaOrigemOrContaDestinoOrderByDataHoraDesc(Conta contaDoUsuario);
}