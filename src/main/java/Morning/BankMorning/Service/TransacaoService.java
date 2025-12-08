package Morning.BankMorning.Service;

import Morning.BankMorning.Dto.*;
import Morning.BankMorning.Enum.TipoDeTransacao;
import Morning.BankMorning.Exception.ArgumentoInvalidoException;
import Morning.BankMorning.Exception.RecursoNaoEncontradoException;
import Morning.BankMorning.Model.Conta;
import Morning.BankMorning.Model.Transacao;
import Morning.BankMorning.Repository.ContaRepository;
import Morning.BankMorning.Repository.TransacaoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class TransacaoService {

    @Autowired
    private ContaRepository contaRepository;

    @Autowired
    @Lazy
    private ContaService contaService;

    @Autowired
    private TransacaoRepository transacaoRepository;

    // --- CONVERSORES ---

    private TransacaoResponse converterParaResponse(Transacao transacao) {
        if (transacao == null) {
            return null;
        }
        String cpfContaOrigem = null;
        String cpfContaDestino = null;

        // Acessos a getUsuario() são seguros se este método for chamado dentro de um contexto @Transactional(readOnly = true)
        if (transacao.getContaOrigem() != null && transacao.getContaOrigem().getUsuario() != null) {
            cpfContaOrigem = transacao.getContaOrigem().getUsuario().getCpf();
        }

        if (transacao.getContaDestino() != null && transacao.getContaDestino().getUsuario() != null) {
            cpfContaDestino = transacao.getContaDestino().getUsuario().getCpf();
        }

        return new TransacaoResponse(cpfContaOrigem, cpfContaDestino, transacao.getValor(), transacao.getTipo(), transacao.getDataHora());
    }

    // --- DEPÓSITO ---
    @Transactional
    public TransacaoResponse depositar(Conta contaOrigem, DepositoRequest request) {
        if (request.valor().compareTo(BigDecimal.ZERO) <= 0) {
            throw new ArgumentoInvalidoException("O valor do depósito deve ser maior que zero.");
        }
        Conta contaDestino = contaRepository.findByNumeroConta(request.numeroContaDestino())
                .orElseThrow(() -> new RecursoNaoEncontradoException("Conta de destino não encontrada."));
        BigDecimal novoSaldo = contaDestino.getSaldo().add(request.valor());
        contaDestino.setSaldo(novoSaldo);
        contaRepository.save(contaDestino);
        return salvarTransacaoGen(request.valor(), TipoDeTransacao.DEPOSITO, null, contaDestino);
    }

    // --- SAQUE ---
    @Transactional
    public TransacaoResponse sacar(Conta contaOrigem, SaqueRequest request) {
        if (request.valor().compareTo(BigDecimal.ZERO) <= 0) {
            throw new ArgumentoInvalidoException("O valor do saque deve ser positivo.");
        }
        if (contaOrigem.getSaldo().compareTo(request.valor()) < 0) {
            throw new ArgumentoInvalidoException("Saldo insuficiente.");
        }
        BigDecimal novoSaldo = contaOrigem.getSaldo().subtract(request.valor());
        contaOrigem.setSaldo(novoSaldo);
        contaRepository.save(contaOrigem);
        return salvarTransacaoGen(request.valor(), TipoDeTransacao.SAQUE, contaOrigem, null);
    }


    // --- TRANSFERÊNCIA ---
    @Transactional
    public TransacaoResponse transferir(Conta contaOrigem, TransferenciaRequest request) {
        if (request.valor().compareTo(BigDecimal.ZERO) <= 0) {
            throw new ArgumentoInvalidoException("O valor da transferência deve ser positivo.");
        }
        Conta contaDestino = contaRepository.findByNumeroConta(request.numeroContaDestino())
                .orElseThrow(() -> new RecursoNaoEncontradoException("Conta de destino não encontrada."));
        BigDecimal valor = request.valor();
        if (contaOrigem.getSaldo().compareTo(valor) < 0) {
            throw new ArgumentoInvalidoException("Saldo insuficiente.");
        }
        if (contaOrigem.equals(contaDestino)) {
            throw new ArgumentoInvalidoException("Não é possível transferir para a mesma conta.");
        }
        contaOrigem.setSaldo(contaOrigem.getSaldo().subtract(valor));
        contaRepository.save(contaOrigem);
        contaDestino.setSaldo(contaDestino.getSaldo().add(valor));
        contaRepository.save(contaDestino);
        return salvarTransacaoGen(valor, TipoDeTransacao.TRANSFERENCIA, contaOrigem, contaDestino);
    }

    // --- EXTRATO (Lógica de leitura) ---
    /**
     * CORREÇÃO: Usando @Transactional(readOnly = true) para garantir que o acesso
     * às entidades LAZY (como o Usuario) no método converterParaResponse funcione,
     * prevenindo o erro 500 Internal Server Error.
     */
    @Transactional(readOnly = true)
    public List<TransacaoResponse> listarExtrato(Conta contaDoUsuario) {
        // Busca todas as transações onde a contaDoUsuario é a origem OU o destino
        // Esta chamada retorna entidades LAZY.
        List<Transacao> transacoes = transacaoRepository.findAllByContaOrigemOrContaDestinoOrderByDataHoraDesc(contaDoUsuario);

        // O mapeamento ocorre DENTRO da transação, garantindo que o Lazy Loading seja resolvido.
        return transacoes.stream()
                .map(this::converterParaResponse)
                .collect(Collectors.toList());
    }

    // --- MÉTODO AUXILIAR GENÉRICO PARA SALVAR NO BANCO ---
    private TransacaoResponse salvarTransacaoGen(BigDecimal valor, TipoDeTransacao tipo, Conta origem, Conta destino) {
        Transacao transacao = new Transacao();
        transacao.setValor(valor);
        transacao.setTipo(tipo);
        transacao.setContaOrigem(origem);
        transacao.setContaDestino(destino);

        Transacao transacaoSalva = transacaoRepository.save(transacao);
        return converterParaResponse(transacaoSalva);
    }
}