package model;

/**
 * @deprecated Use {@link NotificationChannel} para o canal de envio.
 */
@Deprecated
public enum NotificationType {
    EMAIL(1, "E-mail"),
    WHATSAPP(2, "WhatsApp");

    private final int code;
    private final String description;

    NotificationType(int code, String description) {
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
     * @param code Código do tipo.
     * @return NotificationType correspondente.
     */
    public static NotificationType fromCode(int code) {
        for (NotificationType type : values()) {
            if (type.getCode() == code) return type;
        }
        return EMAIL;
    }
}