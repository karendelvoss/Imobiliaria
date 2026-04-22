package view;

import dao.ContractDAO;
import dao.ContractTemplateDAO;
import dao.IndexDAO;
import dao.PropertyDAO;
import dao.UserDAO;
import model.Contracts;
import model.Installments;
import model.Properties;
import model.SignatureStatus;
import model.User_Contract;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static view.ConsoleIO.*;

/** Processos de negócio envolvendo contratos e vínculos proprietário/imóvel. */
public class ContractView {

    private final ContractDAO contractDAO;
    private final PropertyDAO propertyDAO;
    private final UserDAO userDAO;
    private final ContractTemplateDAO templateDAO;
    private final IndexDAO indexDAO;

    public ContractView(ContractDAO contractDAO, PropertyDAO propertyDAO, UserDAO userDAO,
                        ContractTemplateDAO templateDAO, IndexDAO indexDAO) {
        this.contractDAO = contractDAO;
        this.propertyDAO = propertyDAO;
        this.userDAO = userDAO;
        this.templateDAO = templateDAO;
        this.indexDAO = indexDAO;
    }

    public void menu() {
        System.out.println("\n--- PROCESSOS DE NEGÓCIO ---");
        System.out.println("1. EFETIVAR NOVO CONTRATO");
        System.out.println("2. VINCULAR PROPRIETÁRIO A IMÓVEL");
        System.out.println("3. DESVINCULAR PROPRIETÁRIO DE IMÓVEL");
        System.out.println("4. ALTERAR CONTRATO");
        System.out.println("5. EXCLUIR CONTRATO");
        switch (lerIntSeguro("Escolha: ")) {
            case 1: realizar(); break;
            case 2: vincularDono(); break;
            case 3: desvincularDono(); break;
            case 4: alterar(); break;
            case 5: excluir(); break;
        }
    }

    private void realizar() {
        System.out.println("\n--- NOVO CONTRATO ---");

        int idP = lerIdValido("ID Imóvel (apenas disponíveis)",
                id -> {
                    Properties p = propertyDAO.findById(id);
                    return (p != null && p.getCdstatus() == 1) ? p : null;
                },
                () -> propertyDAO.getAvailableOnly().forEach(System.out::println));
        if (idP == -1) return;

        int idU = lerIdValido("ID Cliente (Locatário/Comprador)",
                userDAO::findById,
                () -> userDAO.getAllUsersList().forEach(System.out::println));
        if (idU == -1) return;

        int idTpl = lerIdValido("ID do Modelo de Contrato (Template)",
                templateDAO::findById,
                () -> templateDAO.listAll().forEach(
                        t -> System.out.println("ID: " + t.getCdtemplate() + " | Nome: " + t.getNmtemplate())));
        if (idTpl == -1) return;

        int tipoNegocio;
        while (true) {
            tipoNegocio = lerIntSeguro("Tipo de Negócio (1=Locação/Parcelas Fixas, 2=Venda/Parcelas Personalizadas): ");
            if (tipoNegocio == 1 || tipoNegocio == 2) break;
            System.out.println("Opção inválida.");
        }

        int idIndex = lerIdValido("ID do Índice de Reajuste",
                indexDAO::findById,
                () -> indexDAO.listAll().forEach(
                        i -> System.out.println("ID: " + i.getCdindex() + " | Nome: " + i.getNmindex())));
        if (idIndex == -1) return;

        Contracts c = new Contracts();
        c.setDtcreation(LocalDate.now());
        c.setDstitle(ler("Título do Contrato: "));
        c.setCdtemplate(idTpl);
        c.setCdproperty(idP);
        c.setCdindex(idIndex);

        List<Installments> parcelas = coletarParcelas(tipoNegocio);
        List<User_Contract> partes = montarPartes(idP, idU, tipoNegocio);

        int novoStatus = (tipoNegocio == 1) ? 2 : 3; // 2=Alugado, 3=Vendido
        contractDAO.processFullContract(c, partes, parcelas, null, novoStatus);
    }

    private List<Installments> coletarParcelas(int tipoNegocio) {
        int qtd = lerInt("Quantidade de Parcelas: ");
        List<Installments> parcelas = new ArrayList<>();
        if (tipoNegocio == 1) {
            double v = lerDouble("Valor de cada parcela: ");
            for (int i = 1; i <= qtd; i++) parcelas.add(criarParcela(i, v));
        } else {
            System.out.println("Modo Venda/Personalizado. Preencha o valor de cada parcela:");
            for (int i = 1; i <= qtd; i++) {
                double v = lerDouble("Valor da Parcela " + i + ": ");
                parcelas.add(criarParcela(i, v));
            }
        }
        return parcelas;
    }

    private List<User_Contract> montarPartes(int idP, int idU, int tipoNegocio) {
        List<User_Contract> partes = new ArrayList<>();
        for (int idDono : propertyDAO.getOwnerIdsByProperty(idP)) {
            User_Contract ud = new User_Contract();
            ud.setCduser(idDono);
            ud.setCdrole(1); // Proprietário
            ud.setFgsignaturestatus(SignatureStatus.PENDENTE.getCode());
            partes.add(ud);
        }
        User_Contract uc = new User_Contract();
        uc.setCduser(idU);
        uc.setCdrole(tipoNegocio == 1 ? 2 : 4); // 2=Locatário, 4=Comprador
        uc.setVlparticipation(100.0);
        uc.setFgsignaturestatus(SignatureStatus.PENDENTE.getCode());
        partes.add(uc);
        return partes;
    }

    private static Installments criarParcela(int num, double valor) {
        Installments inst = new Installments();
        inst.setNrinstallment(num);
        inst.setVlbase(valor);
        inst.setDtdue(LocalDate.now().plusMonths(num));
        inst.setCdstatus(1);
        return inst;
    }

    private void vincularDono() {
        System.out.println("\n--- VINCULAR PROPRIETÁRIO A IMÓVEL ---");
        int idP = lerIdValido("ID Imóvel",
                propertyDAO::findById,
                () -> propertyDAO.getAvailableProperties().forEach(System.out::println));
        if (idP == -1) return;

        int idU = lerIdValido("ID Usuário Proprietário",
                userDAO::findById,
                () -> userDAO.getAllUsersList().forEach(System.out::println));
        if (idU == -1) return;

        if (propertyDAO.hasAlreadyThisOwner(idP, idU)) {
            System.out.println("\nAVISO: Este usuário já está vinculado como proprietário deste imóvel.");
            return;
        }
        propertyDAO.linkOwner(idP, idU);
    }

    private void desvincularDono() {
        System.out.println("\n--- DESVINCULAR PROPRIETÁRIO DE IMÓVEL ---");
        int idP = lerIdValido("ID Imóvel",
                propertyDAO::findById,
                () -> {
                    List<String> imoveis = propertyDAO.getPropertiesWithOwners();
                    if (imoveis.isEmpty()) System.out.println("AVISO: Nenhum imóvel possui proprietários vinculados.");
                    else imoveis.forEach(System.out::println);
                });
        if (idP == -1) return;

        final int finalIdP = idP;
        int idU = lerIdValido("ID Usuário Proprietário",
                id -> propertyDAO.hasAlreadyThisOwner(finalIdP, id) ? Integer.valueOf(id) : null,
                () -> {
                    List<String> donos = propertyDAO.getOwnersByProperty(finalIdP);
                    if (donos.isEmpty()) System.out.println("AVISO: Nenhum proprietário vinculado.");
                    else donos.forEach(System.out::println);
                });
        if (idU == -1) return;

        if (confirmar("Confirmar desvinculação? (s/n): ")) {
            propertyDAO.unlinkOwner(idP, idU);
        } else {
            System.out.println("Operação cancelada.");
        }
    }

    private void alterar() {
        System.out.println("\n--- ALTERAR CONTRATO ---");
        int idC = lerIdValido("ID Contrato",
                contractDAO::findById,
                () -> contractDAO.getActiveContractsList().forEach(System.out::println));
        if (idC == -1) return;

        Contracts c = contractDAO.findById(idC);
        System.out.println("Pressione ENTER para manter o valor atual.");
        c.setDstitle(lerOuManter("Título", c.getDstitle()));
        c.setCdtemplate(lerIdOuManter("ID Modelo", c.getCdtemplate(),
                templateDAO::findById,
                () -> templateDAO.listAll().forEach(
                        t -> System.out.println("ID: " + t.getCdtemplate() + " | Nome: " + t.getNmtemplate()))));
        c.setCdindex(lerIdOuManter("ID Índice de Reajuste", c.getCdindex(),
                indexDAO::findById,
                () -> indexDAO.listAll().forEach(
                        i -> System.out.println("ID: " + i.getCdindex() + " | Nome: " + i.getNmindex()))));
        contractDAO.updateContract(c);
    }

    private void excluir() {
        System.out.println("\n--- EXCLUIR CONTRATO ---");
        int idC = lerIdValido("ID Contrato",
                contractDAO::findById,
                () -> contractDAO.getActiveContractsList().forEach(System.out::println));
        if (idC == -1) return;

        Contracts c = contractDAO.findById(idC);
        System.out.println("ATENÇÃO: Excluir o contrato apagará TODAS as parcelas, notificações e comissões vinculadas.");
        if (!confirmar("Confirmar exclusão do contrato '" + c.getDstitle() + "'? (s/n): ")) {
            System.out.println("Operação cancelada.");
            return;
        }
        if (contractDAO.deleteContract(idC)) {
            System.out.println("Contrato excluído com sucesso!");
            if (confirmar("Voltar status do Imóvel ID " + c.getCdproperty() + " para 'Disponível' (1)? (s/n): ")) {
                Properties p = propertyDAO.findById(c.getCdproperty());
                if (p != null) { p.setCdstatus(1); propertyDAO.updateProperty(p); }
            }
        }
    }
}
