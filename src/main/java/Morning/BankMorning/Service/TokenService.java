package Morning.BankMorning.Service;

import Morning.BankMorning.Exception.ArgumentoInvalidoException;
import Morning.BankMorning.Exception.CredencialInvalidaException;
import Morning.BankMorning.Model.Conta;
import Morning.BankMorning.Model.Usuario;
import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

@Service
public class TokenService {

    @Value("${api.security.token.secret}")
    private String secret;
//
    public String gerarToken(Conta conta) {
        try {
            Algorithm algorithm = Algorithm.HMAC256(secret);

            Usuario usuario = conta.getUsuario();

            if (usuario == null) {
                throw new ArgumentoInvalidoException("Conta Não Possui Usuário Associado.");
            }

            return JWT.create().withIssuer("BankMorningAPI").withSubject(usuario.getCpf()).withClaim("email", usuario.getEmail()).withClaim("role", usuario.getRole().name()).withExpiresAt(gerarDataExpiracao()).sign(algorithm);
        } catch (ArgumentoInvalidoException e) {
            throw new ArgumentoInvalidoException("Falha nos dados ao gerar Token");
        } catch (Exception e) {
            throw new RuntimeException("Erro interno ao gerar Token.");
        }
    }

    public boolean validarToken(String token) {
        try {
            Algorithm algorithm = Algorithm.HMAC256(secret);

            JWT.require(algorithm).withIssuer("BankMorningAPI").build().verify(token);

            return true;
        } catch (CredencialInvalidaException e) {
            throw new CredencialInvalidaException("Falha na validação de Token.");
        } catch (Exception e) {
            throw new RuntimeException("Erro interno ao validar Token.");
        }
    }

    public String getSubject(String token) {
        try {
            Algorithm algorithm = Algorithm.HMAC256(secret);

            return JWT.require(algorithm).withIssuer("BankMorningAPI").build().verify(token).getSubject();
        } catch (CredencialInvalidaException e) {
            throw new CredencialInvalidaException("Token inválido ou expirado.");
        } catch (Exception e) {
            throw new RuntimeException("Erro interno ao validar Token.");
        }
    }

    private Instant gerarDataExpiracao() {
        return LocalDateTime.now().plusHours(2).toInstant(ZoneOffset.of("-03:00"));
    }
}
