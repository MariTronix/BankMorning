package Morning.BankMorning.Exception;

import Morning.BankMorning.Dto.MensagemErro;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.stream.Collectors;


@RestControllerAdvice
public class GerenciadorExcecoesGlobal {

    public record RespostaErro(String mensagem, int codigo, String timestamp) {
    }

    @ExceptionHandler(RecursoNaoEncontradoException.class)
    public ResponseEntity<RespostaErro> handleRecursoNaoEncontradoException(RecursoNaoEncontradoException e) {
        HttpStatus status = HttpStatus.NOT_FOUND;

        RespostaErro erro = new RespostaErro(e.getMessage(), status.value(), LocalDateTime.now().toString());

        return new ResponseEntity<RespostaErro>(erro, status);
    }

    @ExceptionHandler(AcessoNaoAutorizadoException.class)
    public ResponseEntity<RespostaErro> handleAcessoNaoAutorizadoException(AcessoNaoAutorizadoException e) {
        HttpStatus status = HttpStatus.FORBIDDEN;

        RespostaErro erro = new RespostaErro(e.getMessage(), status.value(), LocalDateTime.now().toString());

        return new ResponseEntity<RespostaErro>(erro, status);
    }

    @ExceptionHandler(CredencialInvalidaException.class)
    public ResponseEntity<RespostaErro> handleCredencialInvalidaException(CredencialInvalidaException e) {
        HttpStatus status = HttpStatus.UNAUTHORIZED;

        RespostaErro erro = new RespostaErro(e.getMessage(), status.value(), LocalDateTime.now().toString());

        return new ResponseEntity<RespostaErro>(erro, status);
    }

    @ExceptionHandler(ArgumentoInvalidoException.class)
    public ResponseEntity<RespostaErro> handleArgumentoInvalidoException(ArgumentoInvalidoException e) {
        HttpStatus status = HttpStatus.BAD_REQUEST;

        RespostaErro erro = new RespostaErro(e.getMessage(), status.value(), LocalDateTime.now().toString());

        return new ResponseEntity<RespostaErro>(erro, status);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<RespostaErro> handleErroInterno(Exception e) {
        HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;
        String mensagem = "Ocorreu um erro interno no servidor";

        RespostaErro erro = new RespostaErro(mensagem, status.value(), LocalDateTime.now().toString());

        return new ResponseEntity<RespostaErro>(erro, status);
    }
    // Trata erro de validação (@Valid) -> Retorna 400
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Object> handleValidationExceptions(MethodArgumentNotValidException ex) {
        // Pega a primeira mensagem de erro disponível
        String erro = ex.getBindingResult().getAllErrors().get(0).getDefaultMessage();

        // Retorne sua estrutura de erro padrão (ajuste conforme sua classe de erro)
        // Exemplo:
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new MensagemErro(erro, 400));
    }

    // Trata erro de Login (BadCredentials) -> Retorna 403 ou 401
    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<Object> handleBadCredentials(BadCredentialsException ex) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new MensagemErro("Credenciais inválidas", 403));
    }

}
