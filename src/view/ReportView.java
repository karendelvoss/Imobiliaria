package view;

import dao.PropertyDAO;
import service.ReportService;

import static view.ConsoleIO.lerInt;
import static view.ConsoleIO.lerIntSeguro;
import java.util.Scanner;

/** Menu de relatórios. Delega a geração aos serviços especializados. */
public class ReportView {

    private final PropertyDAO propertyDAO;
    private final ReportService reportService;

    public ReportView(PropertyDAO propertyDAO, ReportService reportService) {
        this.propertyDAO = propertyDAO;
        this.reportService = reportService;
    }

    public void menu() {
        System.out.println("\n--- MÓDULO DE RELATÓRIOS ---");
        System.out.println("1. Imóveis por Bairro");
        System.out.println("2. Relatório Financeiro de Locação");
        System.out.println("3. Relatório Financeiro de Venda");
        System.out.println("4. Relatório de Partes do Contrato");
        System.out.println("5. Relatório de Reajustes do Mês");
        System.out.println("6. Listagem Geral (JOINS)");
        switch (lerIntSeguro("Escolha: ")) {
            case 1: propertyDAO.relatorioImoveisPorBairro(); break;
            case 2: 
                if (perguntarEListarContratos("locação")) {
                    reportService.gerarRelatorioFinanceiroLocacao(lerInt("ID Contrato: ")); 
                }
                break;
            case 3: 
                if (perguntarEListarContratos("venda")) {
                    reportService.gerarRelatorioFinanceiroVenda(lerInt("ID Contrato: ")); 
                }
                break;
            case 4: 
                if (perguntarEListarContratos("geral")) {
                    reportService.gerarRelatorioPartesContrato(lerInt("ID Contrato: ")); 
                }
                break;
            case 5:
                System.out.println("\nBuscando reajustes previstos para o mês atual...");
                reportService.gerarRelatorioReajustesDoMes();
                break;
            case 6: propertyDAO.relatorioCompletoImoveis(); break;
        }
    }

    private boolean perguntarEListarContratos(String tipo) {
        @SuppressWarnings("resource")
        Scanner sc = new Scanner(System.in);
        System.out.print("Ver contratos de " + tipo + " disponíveis para consulta? (s/n): ");
        String resp = sc.next().trim().toLowerCase();
        
        if (resp.equals("s")) {
            dao.ContractDAO contractDAO = new dao.ContractDAO();
            java.util.List<String> contratos = contractDAO.getActiveContractsList(tipo);
            
            System.out.println("\n--- CONTRATOS DISPONÍVEIS ---");
            if (contratos.isEmpty()) {
                System.out.println("Nenhum contrato encontrado no sistema.");
                System.out.println("-----------------------------\n");
                return false; // Aborta e volta pro menu imediatamente
            } else {
                for (String c : contratos) {
                    System.out.println(c);
                }
            }
            System.out.println("-----------------------------\n");
            return true;
        }
        
        return false; // Se a resposta for "n" (ou qualquer outra letra), cancela e volta.
    }
}
