package model;

/**
 * Enum para representar os status de assinatura de um participante no contrato.
 */
public enum SignatureStatus {
    PENDENTE(1, "Pendente"),
    ASSINADO(2, "Assinado"),
    RECUSADO(3, "Recusado");

    private final int code;
    private final String description;

    SignatureStatus(int code, String description) {
        this.code = code;
        this.description = description;
    }

    public int getCode() { return code; }
    public String getDescription() { return description; }

    public static SignatureStatus fromCode(int code) {
        for (SignatureStatus status : SignatureStatus.values()) {
            if (status.getCode() == code) return status;
        }
        return PENDENTE; // Retorna um padrão seguro
    }
}