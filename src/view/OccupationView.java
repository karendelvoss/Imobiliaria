package view;

import dao.OccupationDAO;
import model.Occupations;
import static view.ConsoleIO.*;

/**
 * Tela responsável pela gestão do cadastro de profissões.
 */
public class OccupationView {

    private final OccupationDAO dao;

    public OccupationView(OccupationDAO dao) {
        this.dao = dao;
    }

    /**
     * Menu principal do cadastro de profissões.
     */
    public void menu() {
        System.out.println("\n--- SUBMENU: PROFISSÕES ---");
        System.out.println("1. Cadastrar 2. Listar 3. Atualizar 4. Excluir");
        switch (lerIntSeguro("Escolha: ")) {
            case 1: cadastrar(); break;
            case 2: listar(); break;
            case 3: atualizar(); break;
            case 4: excluir(); break;
            default: System.out.println("Opção inválida.");
        }
    }

    private void cadastrar() {
        Occupations occ = new Occupations();
        occ.setNmoccupation(ler("Nome da Profissão: "));
        dao.save(occ);
    }

    private void listar() {
        System.out.println("\n--- LISTA DE PROFISSÕES ---");
        dao.listAll().forEach(System.out::println);
    }

    private void atualizar() {
        System.out.println("\n--- ATUALIZAR PROFISSÃO ---");
        int id = lerIdValido("ID da Profissão para atualizar (0 para cancelar)",
                dao::findById,
                this::listar);
        if (id == -1) { System.out.println("Operação cancelada."); return; }

        Occupations occ = dao.findById(id);
        System.out.println("Pressione ENTER para manter o valor atual.");
        occ.setNmoccupation(lerOuManter("Nome", occ.getNmoccupation()));
        dao.update(occ);
    }

    private void excluir() {
        System.out.println("\n--- EXCLUIR PROFISSÃO ---");
        int id = lerIdValido("ID da Profissão para excluir (0 para cancelar)",
                dao::findById,
                this::listar);
        if (id == -1) { System.out.println("Operação cancelada."); return; }

        if (confirmar("Tem certeza que deseja excluir a profissão ID " + id + "? (s/n): ")) {
            dao.delete(id);
        } else {
            System.out.println("Exclusão cancelada.");
        }
    }
}