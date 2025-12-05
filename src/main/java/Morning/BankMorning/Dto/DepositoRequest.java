package Morning.BankMorning.Dto;

import java.math.BigDecimal;

public class DepositoRequest {

    private String numeroConta; // Agora sim: String e Numero da Conta!
    private String agencia;     // Opcional: bom ter para validar
    private BigDecimal valor;

    // --- Getters e Setters ---
    public String getNumeroConta() { return numeroConta; }
    public void setNumeroConta(String numeroConta) { this.numeroConta = numeroConta; }

    public String getAgencia() { return agencia; }
    public void setAgencia(String agencia) { this.agencia = agencia; }

    public BigDecimal getValor() { return valor; }
    public void setValor(BigDecimal valor) { this.valor = valor; }




}