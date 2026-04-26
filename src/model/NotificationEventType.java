package model;

/**
 * Define os tipos de eventos de notificação do sistema.
 */
public enum NotificationEventType {
    ALUGUEL_LEMBRETE_7D(10, "Lembrete de aluguel - 7 dias"),
    ALUGUEL_LEMBRETE_3D(11, "Lembrete de aluguel - 3 dias"),
    ALUGUEL_LEMBRETE_HOJE(12, "Lembrete de aluguel - dia do vencimento"),
    ALUGUEL_CONFIRMACAO_PAGAMENTO(20, "Confirmação de pagamento recebido"),
    REAJUSTE_ANUAL(30, "Aviso de reajuste anual do valor"),
    VENCIMENTO_CONTRATO_7D(40, "Vencimento de contrato - 7 dias"),
    VENCIMENTO_CONTRATO_3D(41, "Vencimento de contrato - 3 dias"),
    VENCIMENTO_CONTRATO_HOJE(42, "Vencimento de contrato - dia do vencimento");

    private final int code;
    private final String description;

    NotificationEventType(int code, String description) {
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
     * @param code Código do tipo de evento.
     * @return NotificationEventType correspondente.
     */
    public static NotificationEventType fromCode(int code) {
        for (NotificationEventType type : NotificationEventType.values()) {
            if (type.getCode() == code) return type;
        }
        return ALUGUEL_LEMBRETE_HOJE;
    }
}
