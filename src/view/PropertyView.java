package view;

import dao.PropertyDAO;
import model.Properties;

import static view.ConsoleIO.*;

/** Fluxos de UI para a entidade Imóvel. */
public class PropertyView {

    private final PropertyDAO propertyDAO;

    public PropertyView(PropertyDAO propertyDAO) {
        this.propertyDAO = propertyDAO;
    }

    public void menu() {
        System.out.println("\n--- SUBMENU: IMÓVEIS ---");
        System.out.println("1. Cadastrar 2. Consultar 3. Atualizar 4. Excluir");
        switch (lerIntSeguro("Escolha: ")) {
            case 1: cadastrar(); break;
            case 2: consultar(); break;
            case 3: atualizar(); break;
            case 4: excluir(); break;
            default: System.out.println("Opção inválida.");
        }
    }

    private void cadastrar() {
        Properties p = new Properties();
        p.setNrregistration(ler("Matrícula: "));
        p.setDsdescription(ler("Descrição: "));
        p.setVltotalarea(lerDouble("Área Total (m²): "));
        p.setCdaddress(lerInt("ID Endereço: "));
        p.setCdtype(lerInt("ID Tipo: "));
        p.setCdpurpose(lerInt("ID Finalidade: "));
        p.setCdstatus(lerInt("ID Status: "));
        propertyDAO.insertProperty(p);
    }

    private void consultar() {
        String info = propertyDAO.findByIdDetalhado(lerInt("ID do Imóvel: "));
        System.out.println(info != null ? info : "ID não localizado.");
    }

    private void atualizar() {
        int id = lerInt("ID do imóvel: ");
        Properties p = propertyDAO.findById(id);
        if (p == null) { System.out.println("Imóvel não encontrado."); return; }
        p.setNrregistration(lerOuManter("Nova Matrícula", p.getNrregistration()));
        p.setDsdescription(lerOuManter("Nova Descrição", p.getDsdescription()));
        propertyDAO.updateProperty(p);
        System.out.println("Atualizado!");
    }

    private void excluir() {
        while (true) {
            String input = ler("\nID do imóvel para EXCLUIR: ");
            if (input.isEmpty()) return;
            int id = Integer.parseInt(input);
            Properties p = propertyDAO.findById(id);
            if (p == null) {
                System.out.println("ERRO: O ID " + id + " não existe no sistema.");
                if (confirmar("Deseja ver a lista de imóveis que PODEM ser excluídos? (s/n): ")) {
                    mostrarExcluiveis();
                }
                return;
            }
            if (p.getCdstatus() != 1) {
                System.out.println("\nAVISO: O imóvel ID " + id + " faz parte de um contrato, exclusão não permitida!");
                if (confirmar("Ver lista de imóveis aptos para exclusão? (s/n): ")) mostrarExcluiveis();
                return;
            }
            if (confirmar("Confirmar exclusão de '" + p.getNrregistration() + "'? (s/n): ")) {
                propertyDAO.deleteProperty(id);
                System.out.println("Imóvel removido!");
            }
            return;
        }
    }

    private void mostrarExcluiveis() {
        propertyDAO.getAvailableOnly().forEach(System.out::println);
    }
}
