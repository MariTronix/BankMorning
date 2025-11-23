package Morning.BankMorning.Service;

import Morning.BankMorning.Dto.TransferenciaRequest;
import Morning.BankMorning.Model.Conta;
import Morning.BankMorning.Model.Transacao;
import Morning.BankMorning.Repository.ContaRepository;
import Morning.BankMorning.Repository.TransacaoRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class TransacaoService {

    @Autowired
    private TransacaoRepository transacaoRepository;

    @Autowired
    private ContaRepository contaRepository;

    @Transactional
    public Transacao realizarTransferencia(TransferenciaRequest request) {

        Conta origem = contaRepository.findById(request.getIdContaOrigem())
                .orElseThrow(() -> new IllegalArgumentException("Conta de origem não encontrada"));

        Conta destino = contaRepository.findById(request.getIdContaDestino())
                .orElseThrow(() -> new IllegalArgumentException("Conta de destino não encontrada"));

        if (origem.getSaldo().compareTo(request.getValor()) < 0) {
            throw new IllegalArgumentException("Saldo insuficiente para realizar a transferência");
        }

        origem.setSaldo(origem.getSaldo().subtract(request.getValor()));
        destino.setSaldo(destino.getSaldo().add(request.getValor()));

        contaRepository.save(origem);
        contaRepository.save(destino);

        Transacao transacao = new Transacao();
        transacao.setContaOrigem(origem);
        transacao.setContaDestino(destino);
        transacao.setValor(request.getValor());
        transacao.setDataHora(LocalDateTime.now());

        return transacaoRepository.save(transacao);
    }
}