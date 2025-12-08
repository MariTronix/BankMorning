package Morning.BankMorning.Dto;

import java.math.BigDecimal;

public class TransferenciaRequest {
    private String numeroContaOrigem;  // <--- ISSO BATE COM O POSTMAN
    private String numeroContaDestino; // <--- ISSO BATE COM O POSTMAN
    private BigDecimal valor;

    // Getters e Setters
    public String getNumeroContaOrigem() { return numeroContaOrigem; }
    public void setNumeroContaOrigem(String numeroContaOrigem) { this.numeroContaOrigem = numeroContaOrigem; }
    public String getNumeroContaDestino() { return numeroContaDestino; }
    public void setNumeroContaDestino(String numeroContaDestino) { this.numeroContaDestino = numeroContaDestino; }
    public BigDecimal getValor() { return valor; }
    public void setValor(BigDecimal valor) { this.valor = valor; }
}//