package model;

/**
 * Enum para representar os status de uma parcela de forma segura e legível.
 * Evita o uso de "magic numbers" no código.
 */
public enum InstallmentStatus {
    PENDENTE(1, "Pendente"),
    PAGO(2, "Pago"),
    ATRASADO(3, "Atrasado"), // Status lógico para relatórios, pode não existir no banco
    CANCELADO(4, "Cancelado");

    private final int code;
    private final String description;

    InstallmentStatus(int code, String description) {
        this.code = code;
        this.description = description;
    }

    public int getCode() { return code; }
    public String getDescription() { return description; }

    public static InstallmentStatus fromCode(int code) {
        for (InstallmentStatus status : InstallmentStatus.values()) {
            if (status.getCode() == code) return status;
        }
        // Retorna PENDENTE como padrão para evitar quebrar se um status inesperado for encontrado
        return PENDENTE;
    }
}