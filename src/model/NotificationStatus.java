package model;

/**
 * Enum para representar o status de ciclo de vida de uma notificação.
 */
public enum NotificationStatus {
    AGENDADA(1, "Agendada"),
    ENVIADA(2, "Enviada"),
    ERRO_AO_ENVIAR(3, "Erro ao Enviar");

    private final int code;
    private final String description;

    NotificationStatus(int code, String description) {
        this.code = code;
        this.description = description;
    }

    public int getCode() { return code; }
    public String getDescription() { return description; }

    public static NotificationStatus fromCode(int code) {
        for (NotificationStatus status : NotificationStatus.values()) {
            if (status.getCode() == code) return status;
        }
        return AGENDADA; // Padrão seguro
    }
}