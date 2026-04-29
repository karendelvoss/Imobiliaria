package view;

import dao.PropertyDAO;
import service.ReportService;
import static view.ConsoleIO.*;
import dao.ContractDAO;

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
        System.out.println("\n--- MÓDULO DE RELATÓRIOS ---");
        System.out.println("1. Relatório Financeiro de Locação");
        System.out.println("2. Relatório Financeiro de Venda");
        System.out.println("3. Relatório de Partes do Contrato");
        System.out.println("4. Relatório de Reajustes do Ano");
        System.out.println("5. Listagem Geral de Imóveis (JOINS, com filtro opcional por bairro)");
        System.out.println("6. Fluxo de Caixa Mensal e Adimplência");

        switch (lerIntSeguro("Escolha: ")) {
            case 1:
                int idLoc = lerIdValido("ID do Contrato de Locação", 
                    contractDAO::findById, 
                    () -> contractDAO.getActiveContractsList("locação").forEach(System.out::println));
                if (idLoc > 0) reportService.gerarRelatorioFinanceiroLocacao(idLoc);
                break;
            case 2:
                int idVen = lerIdValido("ID do Contrato de Venda", 
                    contractDAO::findById, 
                    () -> contractDAO.getActiveContractsList("venda").forEach(System.out::println));
                if (idVen > 0) reportService.gerarRelatorioFinanceiroVenda(idVen);
                break;
            case 3:
                int idPart = lerIdValido("ID do Contrato (Partes)", 
                    contractDAO::findById, 
                    () -> contractDAO.getActiveContractsList("geral").forEach(System.out::println));
                if (idPart > 0) reportService.gerarRelatorioPartesContrato(idPart);
                break;
            case 4:
                System.out.println("\nBuscando reajustes previstos para o mês atual...");
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
                    contractId = lerIdValido("ID do Contrato para análise", 
                        contractDAO::findById, 
                        () -> contractDAO.getActiveContractsList("geral").forEach(System.out::println));
                    if (contractId <= 0) break;
                }
                
                int year = lerIntSeguro("Digite o ano de referência (ex: 2026): ");
                if (year > 0) {
                    reportService.gerarRelatorioFluxoCaixa(year, contractId);
                } else {
                    System.out.println("Ano inválido.");
                }
                break;
        }
    }
}
