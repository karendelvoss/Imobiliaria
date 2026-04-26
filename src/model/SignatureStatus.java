package model;

/**
 * Define os status possíveis de assinatura de um participante em um contrato.
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
     * @return SignatureStatus correspondente.
     */
    public static SignatureStatus fromCode(int code) {
        for (SignatureStatus status : SignatureStatus.values()) {
            if (status.getCode() == code) return status;
        }
        return PENDENTE;
    }
}