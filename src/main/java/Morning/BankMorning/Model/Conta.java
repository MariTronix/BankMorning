package Morning.BankMorning.Model;

import jakarta.persistence.*;
import lombok.Data;
import java.math.BigDecimal;

@Data
@Entity
@Table(name = "conta")
public class Conta {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idConta;

    @Column(nullable = false, length = 10)
    private String agencia;

    @Column(nullable = false, unique = true, length = 20)
    private String numeroConta;

    @Column(nullable = false)
    private BigDecimal saldo;

    @ManyToOne
    @JoinColumn(name = "id_cliente", nullable = false)
    private Cliente cliente;
}