package model;

/**
 * Define os status possíveis para uma parcela (Installments).
 */
public enum InstallmentStatus {
    PENDENTE(1, "Pendente"),
    PAGO(2, "Pago"),
    ATRASADO(3, "Atrasado"),
    CANCELADO(4, "Cancelado");

    private final int code;
    private final String description;

    InstallmentStatus(int code, String description) {
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
     * @return InstallmentStatus correspondente.
     */
    public static InstallmentStatus fromCode(int code) {
        for (InstallmentStatus status : InstallmentStatus.values()) {
            if (status.getCode() == code) return status;
        }
        return PENDENTE;
    }
}