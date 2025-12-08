package Morning.BankMorning.Dto;

import java.math.BigDecimal;

public class SaqueRequest {

    private String numeroConta;
    private BigDecimal valor;

    // Construtor Vazio
    public SaqueRequest() {}

    // Construtor com Argumentos (Para o Teste)
    public SaqueRequest(String numeroConta, BigDecimal valor) {
        this.numeroConta = numeroConta;
        this.valor = valor;
    }

    // Getters e Setters
    public String getNumeroConta() { return numeroConta; }
    public void setNumeroConta(String numeroConta) { this.numeroConta = numeroConta; }

    public BigDecimal getValor() { return valor; }
    public void setValor(BigDecimal valor) { this.valor = valor; }
}