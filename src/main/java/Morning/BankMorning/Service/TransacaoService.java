package Morning.BankMorning.Service;

import Morning.BankMorning.Model.Conta;
import Morning.BankMorning.Model.Transacao;
import Morning.BankMorning.Repository.ContaRepository;
import Morning.BankMorning.Repository.TransacaoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Service
public class TransacaoService {

    @Autowired
    private ContaRepository contaRepository;

    @Autowired
    private TransacaoRepository transacaoRepository;

    // --- DEPÓSITO ---
    @Transactional
    public void depositar(Integer idConta, BigDecimal valor) {
        if (valor.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("O valor do depósito deve ser maior que zero.");
        }

        Conta conta = contaRepository.findById(idConta)
                .orElseThrow(() -> new IllegalArgumentException("Conta não encontrada."));

        // Adiciona saldo
        conta.setSaldo(conta.getSaldo().add(valor));
        contaRepository.save(conta);

        // Salva histórico
        salvarHistorico(null, conta, valor, "DEPOSITO");
    }

    // --- TRANSFERÊNCIA ---
    @Transactional
    public void transferir(Integer idOrigem, Integer idDestino, BigDecimal valor) {
        if (valor.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("O valor da transferência deve ser maior que zero.");
        }

        Conta origem = contaRepository.findById(idOrigem)
                .orElseThrow(() -> new IllegalArgumentException("Conta de origem não encontrada."));

        Conta destino = contaRepository.findById(idDestino)
                .orElseThrow(() -> new IllegalArgumentException("Conta de destino não encontrada."));

        if (origem.getSaldo().compareTo(valor) < 0) {
            throw new IllegalArgumentException("Saldo insuficiente.");
        }

        origem.setSaldo(origem.getSaldo().subtract(valor));
        destino.setSaldo(destino.getSaldo().add(valor));

        contaRepository.save(origem);
        contaRepository.save(destino);

        salvarHistorico(origem, destino, valor, "TRANSFERENCIA");
    }

    // Método auxiliar privado
    private void salvarHistorico(Conta origem, Conta destino, BigDecimal valor, String tipo) {
        Transacao t = new Transacao();
        t.setDataHora(LocalDateTime.now());
        t.setValor(valor);
        t.setTipo(tipo);
        t.setContaOrigem(origem);
        t.setContaDestino(destino);
        transacaoRepository.save(t);
    }
}