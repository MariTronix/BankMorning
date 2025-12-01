package Morning.BankMorning.Service;

import Morning.BankMorning.Dto.*;
import Morning.BankMorning.Enum.TipoDeTransacao;
import Morning.BankMorning.Exception.ArgumentoInvalidoException;
import Morning.BankMorning.Exception.RecursoNaoEncontradoException;
import Morning.BankMorning.Model.Conta;
import Morning.BankMorning.Model.Transacao;
import Morning.BankMorning.Repository.ContaRepository;
import Morning.BankMorning.Repository.TransacaoRepository;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class TransacaoService {

    @Autowired
    private ContaRepository contaRepository;

    @Autowired
    @Lazy // @Lazy resolve o ciclo de dependência se o ContaService também chamar o TransacaoService
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

        if (transacao.getContaOrigem() != null) {
            cpfContaOrigem = transacao.getContaOrigem().getUsuario().getCpf();
        }

        if (transacao.getContaDestino() != null) {
            cpfContaDestino = transacao.getContaDestino().getUsuario().getCpf();
        }

        return new TransacaoResponse(cpfContaOrigem, cpfContaDestino, transacao.getValor(), transacao.getTipo(), transacao.getDataHora());
    }

    // --- DEPÓSITO (Agora usa NUMERO DA CONTA) ---

    @Transactional
    public TransacaoResponse depositar(DepositoRequest request) { // Recebe DepositoRequest

        // 1. Validação
        if (request.getValor() == null || request.getValor().compareTo(BigDecimal.ZERO) <= 0) {
            throw new ArgumentoInvalidoException("O valor do depósito deve ser maior que zero.");
        }

        // 2. Busca por Numero da Conta
        Conta contaDestino = contaRepository.findByNumeroConta(request.getNumeroConta())
                .orElseThrow(() -> new RecursoNaoEncontradoException("Conta de destino não encontrada (Número: " + request.getNumeroConta() + ")"));

        // 3. Lógica
        BigDecimal novoSaldo = contaDestino.getSaldo().add(request.getValor());
        contaDestino.setSaldo(novoSaldo);
        contaRepository.save(contaDestino);

        // 4. Salvar Histórico (Usa o método genérico abaixo)
        return salvarTransacaoGen(request.getValor(), TipoDeTransacao.DEPOSITO, null, contaDestino);
    }

    // --- SAQUE (Mantive usando CPF via TransacaoRequest) ---

    @Transactional
    public TransacaoResponse sacar(SaqueRequest request) { // <--- Mudou para SaqueRequest

        // 1. Validar Valor
        if (request.getValor().compareTo(BigDecimal.ZERO) <= 0) {
            throw new ArgumentoInvalidoException("O valor do saque deve ser positivo.");
        }

        // 2. Buscar Conta pelo NÚMERO
        Conta contaOrigem = contaRepository.findByNumeroConta(request.getNumeroConta())
                .orElseThrow(() -> new RecursoNaoEncontradoException("Conta não encontrada."));

        // 3. Verificar Saldo
        if (contaOrigem.getSaldo().compareTo(request.getValor()) < 0) {
            throw new ArgumentoInvalidoException("Saldo insuficiente.");
        }

        // 4. Retirar o dinheiro
        BigDecimal novoSaldo = contaOrigem.getSaldo().subtract(request.getValor());
        contaOrigem.setSaldo(novoSaldo);
        contaRepository.save(contaOrigem);

        // 5. Salvar Histórico
        return salvarTransacaoGen(request.getValor(), TipoDeTransacao.SAQUE, contaOrigem, null);
    }


    @Transactional
    public TransacaoResponse transferir(TransferenciaRequest request) { // <--- MUDANÇA 1: Aceita o DTO novo

        // 1. Validação do valor
        if (request.getValor().compareTo(BigDecimal.ZERO) <= 0) {
            throw new ArgumentoInvalidoException("O valor da transferência deve ser positivo.");
        }

        // 2. Buscar Conta ORIGEM pelo NÚMERO (Mudança 2)
        Conta contaOrigem = contaRepository.findByNumeroConta(request.getNumeroContaOrigem())
                .orElseThrow(() -> new RecursoNaoEncontradoException("Conta de origem não encontrada."));

        // 3. Buscar Conta DESTINO pelo NÚMERO (Mudança 3)
        Conta contaDestino = contaRepository.findByNumeroConta(request.getNumeroContaDestino())
                .orElseThrow(() -> new RecursoNaoEncontradoException("Conta de destino não encontrada."));

        // 4. Validar Saldo
        BigDecimal valor = request.getValor();
        if (contaOrigem.getSaldo().compareTo(valor) < 0) {
            throw new ArgumentoInvalidoException("Saldo insuficiente.");
        }

        // 5. Atualizar saldos
        contaOrigem.setSaldo(contaOrigem.getSaldo().subtract(valor));
        contaRepository.save(contaOrigem);

        contaDestino.setSaldo(contaDestino.getSaldo().add(valor));
        contaRepository.save(contaDestino);

        // 6. Salvar e Retornar
        return salvarTransacaoGen(valor, TipoDeTransacao.TRANSFERENCIA, contaOrigem, contaDestino);
    }

    public List<TransacaoResponse> listarExtrato(Conta contaDoUsuario) {
        // Passamos a mesma conta duas vezes porque queremos saber onde ela entrou OU onde ela saiu
        List<Transacao> transacoes = transacaoRepository.findAllByContaOrigemOrContaDestinoOrderByDataHoraDesc(contaDoUsuario, contaDoUsuario);

        // Transforma a lista de Transacao (Banco) em TransacaoResponse (DTO)
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