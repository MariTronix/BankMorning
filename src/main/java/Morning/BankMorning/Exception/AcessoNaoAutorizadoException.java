package Morning.BankMorning.Exception;

public class AcessoNaoAutorizadoException extends RuntimeException {
    public AcessoNaoAutorizadoException(String mensagem) {
        super(mensagem);
    }
}
