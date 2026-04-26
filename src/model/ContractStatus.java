package model;

/**
 * Define os possíveis status de um contrato.
 */
public enum ContractStatus {
    ATIVO(1, "Ativo"),
    FINALIZADO(2, "Finalizado"),
    INDETERMINADO(3, "Indeterminado");

    private final int code;
    private final String description;

    ContractStatus(int code, String description) {
        this.code = code;
        this.description = description;
    }

    public int getCode() {
        return code;
    }

    public String getDescription() {
        return description;
    }

    /**
     * Retorna o enum correspondente ao código informado.
     * 
     * @param code Código do status.
     * @return ContractStatus correspondente.
     */
    public static ContractStatus fromCode(int code) {
        for (ContractStatus status : ContractStatus.values()) {
            if (status.getCode() == code) return status;
        }
        return ATIVO;
    }
}
