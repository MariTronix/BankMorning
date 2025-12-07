package Morning.BankMorning.Service;

import Morning.BankMorning.Dto.ContaRequest;
import Morning.BankMorning.Dto.ContaResponse;
import Morning.BankMorning.Dto.UsuarioResponse;
import Morning.BankMorning.Exception.ArgumentoInvalidoException;
import Morning.BankMorning.Exception.RecursoNaoEncontradoException;
import Morning.BankMorning.Model.Conta;
import Morning.BankMorning.Model.Usuario;
import Morning.BankMorning.Repository.ContaRepository;
import Morning.BankMorning.Repository.UsuarioRepository;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Random;


@Service
public class ContaService {

    @Autowired
    private ContaRepository contaRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Lazy
    @Autowired
    private UsuarioService usuarioService;

    public Conta converterParaModel(ContaRequest request) {
        if (request == null) {
            return null;
        }
        Conta conta = new Conta();
        BeanUtils.copyProperties(request, conta);

        return conta;
    }

    public ContaResponse converterParaResponse(Conta conta) {
        if (conta == null) {
            return null;
        }

        // A potencial falha (500) está aqui. Garante que se a conta existir, o usuário seja carregado.
        UsuarioResponse usuarioResponse = conta.getUsuario() != null
                ? usuarioService.converterParaResponse(conta.getUsuario())
                : null;

        // CORREÇÃO: Garantir que o numeroConta seja incluído no DTO de resposta
        return new ContaResponse(
                conta.getIdConta(),
                conta.getAgencia(),
                conta.getSaldo(),
                conta.getNumeroConta(), // <--- INCLUSÃO DO NUMERO DA CONTA
                usuarioResponse
        );
    }

    @Transactional(readOnly = true)
    public ContaResponse buscarContaPorId(Integer id) {
        Conta conta = contaRepository.findById(id).orElseThrow(() -> new RecursoNaoEncontradoException("Conta não encontrada"));

        return converterParaResponse(conta);
    }

    /**
     * CORREÇÃO FINAL PARA O DASHBOARD: Usa o E-mail (identificador do JWT) para buscar a Conta.
     */
    @Transactional(readOnly = true)
    public BigDecimal buscarSaldoPorCpf(String identificador) { // Nome alterado para refletir que não é necessariamente CPF

        // CORRIGIDO: Usando o método que busca pelo E-mail do usuário (findByUsuario_Email)
        Conta conta = contaRepository.findByUsuario_Email(identificador)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Conta não encontrada para o usuário logado."));

        return conta.getSaldo();
    }

    // Métodos existentes...
    @Transactional(readOnly = true)
    public ContaResponse buscarContaPorCpfUsuario(String cpf) {

        Conta conta = contaRepository.findByUsuario_Cpf(cpf).orElseThrow(() -> new RecursoNaoEncontradoException("Conta não encontrada"));

        return converterParaResponse(conta);
    }

    @Transactional(readOnly = true)
    public ContaResponse buscarContaPorEmailUsuario(String email) {

        Conta conta = contaRepository.findByUsuario_Email(email).orElseThrow(() -> new RecursoNaoEncontradoException("Conta não encontrada"));

        return converterParaResponse(conta);
    }

    @Transactional
    public ContaResponse criarConta(Usuario usuario, ContaRequest contaRequest) {
        if (contaRepository.findByUsuario_Cpf(usuario.getCpf()).isPresent()) {
            throw new ArgumentoInvalidoException("Usuario já possui uma conta");
        }

        Conta conta = new Conta();

        // A senha JÁ vem criptografada do UsuarioService. Apenas salve.
        conta.setSenha(contaRequest.senha());

        conta.setUsuario(usuario);
        conta.setSaldo(BigDecimal.ZERO);
        conta.setAgencia("777");
        conta.setNumeroConta(Integer.toString(new Random().nextInt(90000) + 10000));

        Conta contaSalva = contaRepository.save(conta);

        return converterParaResponse(contaSalva);
    }
}