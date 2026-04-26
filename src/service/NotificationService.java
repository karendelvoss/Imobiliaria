package service;

import dao.ContractDAO;
import dao.InstallmentDAO;
import dao.NotificationDAO;
import dao.UserContractDAO;
import model.Contracts;
import model.Installments;
import model.InstallmentStatus;
import model.NotificationEventType;
import model.User_Contract;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * Serviço responsável por orquestrar a geração de notificações de negócio baseadas em eventos do sistema.
 */
public class NotificationService {

    private static final int ROLE_LOCATARIO = 1;
    private static final int ROLE_LOCADOR   = 2;

    private final NotificationDAO notificationDAO;
    private final InstallmentDAO  installmentDAO;
    private final ContractDAO     contractDAO;
    private final UserContractDAO userContractDAO;

    private final DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    public NotificationService() {
        this.notificationDAO  = new NotificationDAO();
        this.installmentDAO   = new InstallmentDAO();
        this.contractDAO      = new ContractDAO();
        this.userContractDAO  = new UserContractDAO();
    }

    /**
     * Gera lembretes de vencimento de aluguel para os inquilinos (Locatários).
     * Verifica parcelas pendentes com vencimento em 7, 3 ou 0 dias.
     */
    public void processarLembretesDeAluguel() {
        System.out.println("\n[JOB 1] Processando lembretes de vencimento de aluguel...");
        int criadas = 0;
        int[] diasAlerta = {7, 3, 0};
        NotificationEventType[] tipos = {
            NotificationEventType.ALUGUEL_LEMBRETE_7D,
            NotificationEventType.ALUGUEL_LEMBRETE_3D,
            NotificationEventType.ALUGUEL_LEMBRETE_HOJE
        };

        LocalDate hoje = LocalDate.now();

        for (int i = 0; i < diasAlerta.length; i++) {
            LocalDate dataAlvo = hoje.plusDays(diasAlerta[i]);
            List<Installments> parcelas = installmentDAO.findByDueDateAndStatus(dataAlvo, InstallmentStatus.PENDENTE.getCode());
            System.out.printf("  [JOB 1] Buscando parcelas pendentes para %s: %d encontrada(s).%n", dataAlvo.format(fmt), parcelas.size());

            for (Installments inst : parcelas) {
                int cdcontract = inst.getFk_Contracts_cdcontract();
                int cduser = getParticipantByRole(cdcontract, ROLE_LOCATARIO);
                if (cduser <= 0) {
                    System.out.printf("  [JOB 1] Parcela #%d (Contrato #%d): Nenhum usuário com papel Locatário (cdrole=%d) encontrado na tabela User_Contract.%n",
                        inst.getCdinstallment(), cdcontract, ROLE_LOCATARIO);
                    continue;
                }

                String msg = String.format(
                    "[LEMBRETE] Aluguel do Contrato #%d vence em %s (R$ %.2f). Por favor, efetue o pagamento.",
                    cdcontract, inst.getDtdue().format(fmt),
                    inst.getVladjusted() > 0 ? inst.getVladjusted() : inst.getVlbase()
                );

                boolean ok = notificationDAO.criarSeNaoExistir(cdcontract, cduser, tipos[i].getCode(), msg, hoje);
                if (ok) criadas++;
                else System.out.printf("  [JOB 1] Notificação já existia para Contrato #%d, Usuário #%d, Tipo %d.%n", cdcontract, cduser, tipos[i].getCode());
            }
        }
        System.out.printf("[JOB 1] Concluído: %d notificação(ões) criada(s).%n", criadas);
    }

    /**
     * Gera notificações de confirmação de pagamento para os locadores.
     * Verifica parcelas pagas na data atual.
     */
    public void processarConfirmacaoDePagamento() {
        System.out.println("\n[JOB 2] Processando confirmações de pagamento para locadores...");
        int criadas = 0;
        LocalDate hoje = LocalDate.now();
        List<Installments> pagas = installmentDAO.findByPaymentDate(hoje);
        System.out.printf("  [JOB 2] Parcelas pagas hoje (%s): %d encontrada(s).%n", hoje.format(fmt), pagas.size());

        for (Installments inst : pagas) {
            int cdcontract = inst.getFk_Contracts_cdcontract();
            int cdLocador = getParticipantByRole(cdcontract, ROLE_LOCADOR);
            if (cdLocador <= 0) {
                System.out.printf("  [JOB 2] Parcela #%d (Contrato #%d): Nenhum usuário com papel Locador (cdrole=%d) encontrado.%n",
                    inst.getCdinstallment(), cdcontract, ROLE_LOCADOR);
                continue;
            }

            String msg = String.format(
                "[PAGAMENTO CONFIRMADO] Parcela #%d do Contrato #%d foi paga em %s. Valor: R$ %.2f.",
                inst.getNrinstallment(), cdcontract,
                hoje.format(fmt),
                inst.getVladjusted() > 0 ? inst.getVladjusted() : inst.getVlbase()
            );

            boolean ok = notificationDAO.criarSeNaoExistir(
                cdcontract, cdLocador, NotificationEventType.ALUGUEL_CONFIRMACAO_PAGAMENTO.getCode(), msg, hoje
            );
            if (ok) criadas++;
        }
        System.out.printf("[JOB 2] Concluído: %d notificação(ões) criada(s).%n", criadas);
    }

    /**
     * Gera avisos de reajuste anual para locadores e inquilinos.
     * Baseia-se no mês de aniversário do contrato.
     */
    public void processarAvisosDeReajuste() {
        System.out.println("\n[JOB 3] Processando avisos de reajuste anual...");
        int criadas = 0;
        LocalDate hoje = LocalDate.now();
        List<Contracts> contratos = contractDAO.findAllWithAdjustmentIndex();
        System.out.printf("  [JOB 3] Contratos com índice de reajuste: %d encontrado(s).%n", contratos.size());
        for (Contracts c : contratos) {
            if (c.getDtcreation() == null) {
                System.out.printf("  [JOB 3] Contrato #%d: sem data de criação, pulando.%n", c.getCdcontract());
                continue;
            }
            if (c.getDtcreation().getYear() >= hoje.getYear()) {
                System.out.printf("  [JOB 3] Contrato #%d: criado no ano atual (%d), ainda não fez 1 ano, pulando.%n",
                    c.getCdcontract(), c.getDtcreation().getYear());
                continue;
            }
            if (c.getDtcreation().getMonth() != hoje.getMonth()) {
                System.out.printf("  [JOB 3] Contrato #%d: aniversário em %s, mês atual é %s, pulando.%n",
                    c.getCdcontract(), c.getDtcreation().getMonth(), hoje.getMonth());
                continue;
            }

            // Busca o último valor ajustado
            Installments ultima = installmentDAO.findLastPendingInstallmentByContractId(c.getCdcontract());
            if (ultima == null) continue;

            double novoValor = ultima.getVladjusted() > 0 ? ultima.getVladjusted() : ultima.getVlbase();
            String dataVigencia = hoje.plusMonths(1).withDayOfMonth(1).format(fmt);

            String msg = String.format(
                "[REAJUSTE ANUAL] Contrato #%d: O valor do aluguel foi reajustado para R$ %.2f a partir de %s.",
                c.getCdcontract(), novoValor, dataVigencia
            );

            for (int role : new int[]{ROLE_LOCADOR, ROLE_LOCATARIO}) {
                int cduser = getParticipantByRole(c.getCdcontract(), role);
                if (cduser <= 0) continue;
                boolean ok = notificationDAO.criarSeNaoExistir(
                    c.getCdcontract(), cduser, NotificationEventType.REAJUSTE_ANUAL.getCode(), msg, hoje
                );
                if (ok) criadas++;
            }
        }
        System.out.printf("[JOB 3] Concluído: %d notificação(ões) criada(s).%n", criadas);
    }

    /**
     * Gera avisos de proximidade do vencimento do contrato (fim do prazo determinado).
     * Notifica ambas as partes em 7, 3 ou 0 dias antes do vencimento.
     */
    public void processarAvisosDeVencimentoContrato() {
        System.out.println("\n[JOB 4] Processando avisos de vencimento de contratos...");
        int criadas = 0;
        int[] diasAlerta = {7, 3, 0};
        NotificationEventType[] tipos = {
            NotificationEventType.VENCIMENTO_CONTRATO_7D,
            NotificationEventType.VENCIMENTO_CONTRATO_3D,
            NotificationEventType.VENCIMENTO_CONTRATO_HOJE
        };

        LocalDate hoje = LocalDate.now();
        List<Contracts> todos = contractDAO.findAllActive();

        for (int i = 0; i < diasAlerta.length; i++) {
            LocalDate dataAlvo = hoje.plusDays(diasAlerta[i]);
            final NotificationEventType tipo = tipos[i];

            for (Contracts c : todos) {
                if (c.getDtlimit() == null) continue;
                if (!c.getDtlimit().equals(dataAlvo)) continue;

                // BUG CORRIGIDO: FINALIZADO = código 2, INDETERMINADO = código 3
                // Contratos FINALIZADOS não precisam de aviso de vencimento.
                // Contratos INDETERMINADOS não têm dtlimit, então nunca chegam aqui, mas por segurança:
                if (c.getCdstatus() == model.ContractStatus.FINALIZADO.getCode()) {
                    System.out.printf("  [JOB 4] Contrato #%d: já finalizado, pulando.%n", c.getCdcontract());
                    continue;
                }

                String msg = String.format(
                    "[VENCIMENTO DE CONTRATO] O Contrato #%d vence em %s. Entre em contato para definir a renovação ou encerramento.",
                    c.getCdcontract(), c.getDtlimit().format(fmt)
                );

                for (int role : new int[]{ROLE_LOCADOR, ROLE_LOCATARIO}) {
                    int cduser = getParticipantByRole(c.getCdcontract(), role);
                    if (cduser <= 0) continue;
                    boolean ok = notificationDAO.criarSeNaoExistir(
                        c.getCdcontract(), cduser, tipo.getCode(), msg, hoje
                    );
                    if (ok) criadas++;
                }
            }
        }
        System.out.printf("[JOB 4] Concluído: %d notificação(ões) criada(s).%n", criadas);
    }

    // =========================================================================
    // Utilitário: Rodar todos os jobs de uma só vez
    // =========================================================================
    public void processarTodosOsJobs() {
        System.out.println("\n==================================================");
        System.out.println("  PROCESSANDO TODOS OS JOBS DE NOTIFICAÇÃO");
        System.out.println("==================================================");
        processarLembretesDeAluguel();
        processarConfirmacaoDePagamento();
        processarAvisosDeReajuste();
        processarAvisosDeVencimentoContrato();
        System.out.println("\n[CONCLUÍDO] Todos os jobs de notificação foram processados.");
    }

    // =========================================================================
    // Método auxiliar interno
    // =========================================================================
    /**
     * Busca o ID do primeiro usuário com o papel (cdrole) especificado no contrato.
     * @return o cduser encontrado, ou 0 se não houver ninguém com esse papel.
     */
    private int getParticipantByRole(int cdcontract, int cdrole) {
        List<User_Contract> partes = userContractDAO.findByContractId(cdcontract);
        for (User_Contract uc : partes) {
            if (uc.getCdrole() == cdrole) {
                return uc.getCduser();
            }
        }
        return 0;
    }
}
