package Morning.BankMorning.Model;

import jakarta.persistence.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "transacao")
public class Transacao {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idTransacao;

    @Column(nullable = false)
    private BigDecimal valor;

    @Column(nullable = false)
    private LocalDateTime dataHora;

    @ManyToOne
    @JoinColumn(name = "id_conta_origem", nullable = false)
    private Conta contaOrigem;

    @ManyToOne
    @JoinColumn(name = "id_conta_destino", nullable = false)
    private Conta contaDestino;
}