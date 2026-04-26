package model;

/**
 * Define os canais de comunicação para notificações.
 */
public enum NotificationChannel {
    EMAIL(1, "E-mail"),
    WHATSAPP(2, "WhatsApp");

    private final int code;
    private final String description;

    NotificationChannel(int code, String description) {
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
     * @param code Código do canal.
     * @return NotificationChannel correspondente.
     */
    public static NotificationChannel fromCode(int code) {
        for (NotificationChannel ch : values()) {
            if (ch.getCode() == code) return ch;
        }
        return EMAIL;
    }
}
