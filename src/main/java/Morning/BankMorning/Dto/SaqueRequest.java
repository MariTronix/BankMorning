package Morning.BankMorning.Dto;

import java.math.BigDecimal;

public class SaqueRequest {
    private String numeroConta;
    private BigDecimal valor;

    // Getters e Setters
    public String getNumeroConta() { return numeroConta; }
    public void setNumeroConta(String numeroConta) { this.numeroConta = numeroConta; }

    public BigDecimal getValor() { return valor; }
    public void setValor(BigDecimal valor) { this.valor = valor; }
}