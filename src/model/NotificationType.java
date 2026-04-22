package model;

/**
 * Enum para representar os tipos de notificação.
 */
public enum NotificationType {
    EMAIL(1, "E-mail"),
    SMS(2, "SMS"),
    PUSH(3, "Push");

    private final int code;
    private final String description;

    NotificationType(int code, String description) {
        this.code = code;
        this.description = description;
    }

    public int getCode() { return code; }
    public String getDescription() { return description; }

    public static NotificationType fromCode(int code) {
        for (NotificationType type : NotificationType.values()) {
            if (type.getCode() == code) return type;
        }
        return EMAIL; // Padrão seguro
    }
}