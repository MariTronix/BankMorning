package Morning.BankMorning.Service;

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

    // GERAR TOKEN
    public String gerarToken(Usuario usuario) {
        try {
            Algorithm algorithm = Algorithm.HMAC256(secret);
            return JWT.create()
                    .withIssuer("BankMorningAPI")
                    .withSubject(usuario.getEmail()) // identificador
                    .withExpiresAt(gerarDataExpiracao())
                    .sign(algorithm);
        } catch (Exception e) {
            throw new RuntimeException("Erro ao gerar token", e);
        }
    }

    // VALIDAR TOKEN
    public boolean validarToken(String token) {
        try {
            Algorithm algorithm = Algorithm.HMAC256(secret);

            JWT.require(algorithm)
                    .withIssuer("BankMorningAPI")
                    .build()
                    .verify(token);

            return true;
        } catch (Exception e) {
            return false;
        }
    }

    // PEGAR EMAIL DO TOKEN
    public String getSubject(String token) {
        Algorithm algorithm = Algorithm.HMAC256(secret);

        return JWT.require(algorithm)
                .withIssuer("BankMorningAPI")
                .build()
                .verify(token)
                .getSubject();
    }

    private Instant gerarDataExpiracao() {
        return LocalDateTime.now()
                .plusHours(2)
                .toInstant(ZoneOffset.of("-03:00"));
    }
}
