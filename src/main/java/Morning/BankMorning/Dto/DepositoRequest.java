package Morning.BankMorning.Dto;

import java.math.BigDecimal;

public class DepositoRequest {

    private Integer idConta;
    private BigDecimal valor;

    // --- GETTERS (Para o Controller conseguir ler) ---
    public Integer getIdConta() {
        return idConta;
    }

    public void setIdConta(Integer idConta) {
        this.idConta = idConta;
    }

    public BigDecimal getValor() {
        return valor;
    }

    public void setValor(BigDecimal valor) {
        this.valor = valor;
    }
}