package view;

import dao.PropertyDAO;
import service.ReportService;
import static view.ConsoleIO.*;
import dao.ContractDAO;
import java.util.List;

/**
 * Tela de acesso aos diversos relatórios do sistema.
 */
public class ReportView {

    private final PropertyDAO propertyDAO;
    private final ReportService reportService;
    private final ContractDAO contractDAO;

    public ReportView(PropertyDAO propertyDAO, ReportService reportService) {
        this.propertyDAO = propertyDAO;
        this.reportService = reportService;
        this.contractDAO = new ContractDAO();
    }

    /**
     * Menu de seleção de relatórios.
     */
    public void menu() {
        int op = -1;
        while (op != 0) {
            System.out.println("\n--- MÓDULO DE RELATÓRIOS ---");
            System.out.println("1. Relatório Financeiro de Locação");
            System.out.println("2. Relatório Financeiro de Venda");
            System.out.println("3. Relatório de Partes do Contrato");
            System.out.println("4. Relatório de Reajustes do Ano");
            System.out.println("5. Listagem Geral de Imóveis (com filtro opcional por bairro)");
            System.out.println("6. Fluxo de Caixa Mensal e Adimplência");
            System.out.println("0. Voltar");
            op = lerIntSeguro("Escolha: ");
            switch (op) {
            case 1:
                int idLoc = lerIdValidoSomenteLeitura("ID do Contrato de Locação", 
                    contractDAO::findById, 
                    () -> {
                        List<String> lista = contractDAO.getActiveContractsList("locação");
                        if (lista.isEmpty()) {
                            System.out.println("\n  Nenhum contrato de locação encontrado.");
                            System.out.println("  Dica: Cadastre um contrato no módulo de Processos primeiro.\n");
                        } else {
                            System.out.println("\n--- CONTRATOS DE LOCAÇÃO ---");
                            lista.forEach(System.out::println);
                            System.out.println();
                        }
                    });
                if (idLoc > 0) reportService.gerarRelatorioFinanceiroLocacao(idLoc);
                break;
            case 2:
                int idVen = lerIdValidoSomenteLeitura("ID do Contrato de Venda", 
                    contractDAO::findById, 
                    () -> {
                        List<String> lista = contractDAO.getActiveContractsList("venda");
                        if (lista.isEmpty()) {
                            System.out.println("\n  Nenhum contrato de venda encontrado.");
                            System.out.println("  Dica: Cadastre um contrato no módulo de Processos primeiro.\n");
                        } else {
                            System.out.println("\n--- CONTRATOS DE VENDA ---");
                            lista.forEach(System.out::println);
                            System.out.println();
                        }
                    });
                if (idVen > 0) reportService.gerarRelatorioFinanceiroVenda(idVen);
                break;
            case 3:
                int idPart = lerIdValidoSomenteLeitura("ID do Contrato (Partes)", 
                    contractDAO::findById, 
                    () -> {
                        List<String> comPartes = contractDAO.getContractsWithParticipantsList();
                        if (comPartes.isEmpty()) {
                            System.out.println("\n  Nenhum contrato com partes vinculadas.");
                        } else {
                            System.out.println("\n--- CONTRATOS COM PARTICIPANTES ---");
                            comPartes.forEach(System.out::println);
                            System.out.println();
                        }
                    });
                if (idPart > 0) reportService.gerarRelatorioPartesContrato(idPart);
                break;
            case 4:
                System.out.println("\nBuscando reajustes previstos para o ano atual...");
                reportService.gerarRelatorioReajustesDoMes();
                break;
            case 5: {
                String filtroBairro = ler("Filtrar por bairro (ou [ENTER] para listar todos): ");
                propertyDAO.relatorioCompletoImoveis(filtroBairro);
                break;
            }
            case 6:
                System.out.println("\nMODO DO RELATÓRIO DE FLUXO DE CAIXA:");
                System.out.println("1. Visão Geral (Todos os Contratos)");
                System.out.println("2. Visão de Contrato Específico");
                int modo = lerIntSeguro("Escolha o modo: ");
                
                int contractId = 0;
                if (modo == 2) {
                    contractId = lerIdValidoSomenteLeitura("ID do Contrato para análise", 
                        contractDAO::findById, 
                        () -> {
                            List<String> lista = contractDAO.getActiveContractsList("geral");
                            if (lista.isEmpty()) {
                                System.out.println("\n  Nenhum contrato encontrado.");
                            } else {
                                System.out.println("\n--- TODOS OS CONTRATOS ---");
                                lista.forEach(System.out::println);
                                System.out.println();
                            }
                        });
                    if (contractId <= 0) break;
                }
                
                int year = lerIntSeguro("Digite o ano de referência (ex: 2026): ");
                if (year > 0) {
                    reportService.gerarRelatorioFluxoCaixa(year, contractId);
                } else {
                    System.out.println("Ano inválido.");
                }
                break;
                case 0: break;
                default: System.out.println("Opção inválida.");
            }
        }
    }
}
