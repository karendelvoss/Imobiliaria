package view;

import dao.BankAccountDAO;
import dao.CityDAO;
import dao.ClauseDAO;
import dao.ContractTemplateDAO;
import dao.CountryDAO;
import dao.TopicDAO;
import dao.VariableDAO;
import dao.CommissionDAO;
import dao.BrokerDataDAO;
import dao.DistrictDAO;
import dao.IndexDAO;
import dao.NotificationDAO;
import dao.PropertyPurposeDAO;
import dao.PropertyStatusDAO;
import dao.PropertyTypeDAO;
import dao.RoleDAO;
import model.Bank_Accounts;
import model.Cities;
import model.Clauses;
import model.Contract_Templates;
import model.Countries;
import model.Districts;
import model.Indexes;
import model.Notifications;
import model.Property_Purposes;
import model.Property_Status;
import model.Property_Types;
import model.Roles;
import model.Topics;
import model.Variables;
import model.Commissions;
import model.Broker_Data;

import java.time.LocalDate;

import static view.ConsoleIO.*;

/**
 * Submenus e CRUDs de todas as tabelas auxiliares/domínio.
 * Cada CRUD é uma declaração enxuta apoiada em {@link CrudConsole}.
 */
public class DomainCrudView {

    private final CountryDAO countryDAO;
    private final CityDAO cityDAO;
    private final DistrictDAO districtDAO;
    private final PropertyTypeDAO propertyTypeDAO;
    private final PropertyPurposeDAO propertyPurposeDAO;
    private final PropertyStatusDAO propertyStatusDAO;
    private final ContractTemplateDAO templateDAO;
    private final ClauseDAO clauseDAO;
    private final IndexDAO indexDAO;
    private final RoleDAO roleDAO;
    private final BankAccountDAO bankAccountDAO;
    private final NotificationDAO notificationDAO;
    private final TopicDAO topicDAO;
    private final VariableDAO variableDAO;
    private final CommissionDAO commissionDAO;
    private final BrokerDataDAO brokerDataDAO;

    public DomainCrudView(CountryDAO countryDAO, CityDAO cityDAO, DistrictDAO districtDAO,
                          PropertyTypeDAO propertyTypeDAO, PropertyPurposeDAO propertyPurposeDAO,
                          PropertyStatusDAO propertyStatusDAO, ContractTemplateDAO templateDAO,
                          ClauseDAO clauseDAO, IndexDAO indexDAO, RoleDAO roleDAO,
                          BankAccountDAO bankAccountDAO, NotificationDAO notificationDAO,
                          TopicDAO topicDAO, VariableDAO variableDAO, 
                          CommissionDAO commissionDAO, BrokerDataDAO brokerDataDAO) {
        this.countryDAO = countryDAO;
        this.cityDAO = cityDAO;
        this.districtDAO = districtDAO;
        this.propertyTypeDAO = propertyTypeDAO;
        this.propertyPurposeDAO = propertyPurposeDAO;
        this.propertyStatusDAO = propertyStatusDAO;
        this.templateDAO = templateDAO;
        this.clauseDAO = clauseDAO;
        this.indexDAO = indexDAO;
        this.roleDAO = roleDAO;
        this.bankAccountDAO = bankAccountDAO;
        this.notificationDAO = notificationDAO;
        this.topicDAO = topicDAO;
        this.variableDAO = variableDAO;
        this.commissionDAO = commissionDAO;
        this.brokerDataDAO = brokerDataDAO;
    }

    // --- Submenus ---

    public void menuLocalizacao() {
        System.out.println("\n--- LOCALIZAÇÃO ---");
        System.out.println("1. Países  2. Cidades  3. Bairros  0. Voltar");
        switch (lerIntSeguro("Escolha: ")) {
            case 1: crudCountry(); break;
            case 2: crudCity(); break;
            case 3: crudDistrict(); break;
        }
    }

    public void menuAtributosImoveis() {
        System.out.println("\n--- DOMÍNIOS DE IMÓVEIS ---");
        System.out.println("1. Tipos  2. Finalidades  3. Status  0. Voltar");
        switch (lerIntSeguro("Escolha: ")) {
            case 1: crudPropertyType(); break;
            case 2: crudPropertyPurpose(); break;
            case 3: crudPropertyStatus(); break;
        }
    }

    public void menuAtributosContratuais() {
        System.out.println("\n--- DOMÍNIOS CONTRATUAIS ---");
        System.out.println("1. Modelos (Templates)  2. Cláusulas  3. Índices  4. Papéis");
        System.out.println("5. Tópicos  6. Variáveis  7. Comissões  8. Corretores  0. Voltar");
        switch (lerIntSeguro("Escolha: ")) {
            case 1: crudContractTemplate(); break;
            case 2: crudClause(); break;
            case 3: crudIndex(); break;
            case 4: crudRole(); break;
            case 5: crudTopic(); break;
            case 6: crudVariable(); break;
            case 7: crudCommission(); break;
            case 8: crudBrokerData(); break;
        }
    }

    public void menuOutros() {
        System.out.println("\n--- OUTROS DOMÍNIOS ---");
        System.out.println("1. Contas Bancárias  2. Notificações  0. Voltar");
        switch (lerIntSeguro("Escolha: ")) {
            case 1: crudBankAccount(); break;
            case 2: crudNotification(); break;
        }
    }

    // --- CRUDs ---

    private void crudCountry() {
        CrudConsole.run("Países",
                () -> {
                    Countries c = new Countries();
                    c.setNmcountry(ler("Nome: "));
                    c.setSgcountry(ler("Sigla: "));
                    return c;
                },
                c -> {
                    c.setNmcountry(lerOuManter("Novo Nome", c.getNmcountry()));
                    c.setSgcountry(lerOuManter("Nova Sigla", c.getSgcountry()));
                },
                countryDAO::findById,
                c -> c.getCdcountry() + " - " + c.getNmcountry() + " (" + c.getSgcountry() + ")",
                CrudConsole.adapt(countryDAO::insert, countryDAO::update, countryDAO::delete, countryDAO::listAll));
    }

    private void crudCity() {
        CrudConsole.run("Cidades",
                () -> {
                    Cities c = new Cities();
                    c.setNmcity(ler("Nome: "));
                    c.setCdstate(lerInt("ID Estado: "));
                    return c;
                },
                c -> {
                    c.setNmcity(lerOuManter("Novo Nome", c.getNmcity()));
                    c.setCdstate(lerIntOuManter("Novo ID Estado", c.getCdstate()));
                },
                cityDAO::findById,
                c -> c.getCdcity() + " - " + c.getNmcity() + " (Est ID: " + c.getCdstate() + ")",
                CrudConsole.adapt(cityDAO::insert, cityDAO::update, cityDAO::delete, cityDAO::listAll));
    }

    private void crudDistrict() {
        CrudConsole.run("Bairros",
                () -> {
                    Districts d = new Districts();
                    d.setNmdistrict(ler("Nome: "));
                    d.setCdcity(lerInt("ID Cidade: "));
                    return d;
                },
                d -> {
                    d.setNmdistrict(lerOuManter("Novo Nome", d.getNmdistrict()));
                    d.setCdcity(lerIntOuManter("Novo ID Cidade", d.getCdcity()));
                },
                districtDAO::findById,
                d -> d.getCddistrict() + " - " + d.getNmdistrict() + " (Cid ID: " + d.getCdcity() + ")",
                CrudConsole.adapt(districtDAO::insert, districtDAO::update, districtDAO::delete, districtDAO::listAll));
    }

    private void crudPropertyType() {
        CrudConsole.run("Tipos de Imóvel",
                () -> { Property_Types t = new Property_Types(); t.setNmtype(ler("Tipo: ")); return t; },
                t -> t.setNmtype(lerOuManter("Tipo", t.getNmtype())),
                propertyTypeDAO::findById,
                t -> t.getCdtype() + " - " + t.getNmtype(),
                CrudConsole.adapt(propertyTypeDAO::insert, propertyTypeDAO::update, propertyTypeDAO::delete, propertyTypeDAO::listAll));
    }

    private void crudPropertyPurpose() {
        CrudConsole.run("Finalidades de Imóvel",
                () -> { Property_Purposes p = new Property_Purposes(); p.setNmpurpose(ler("Finalidade: ")); return p; },
                p -> p.setNmpurpose(lerOuManter("Finalidade", p.getNmpurpose())),
                propertyPurposeDAO::findById,
                p -> p.getCdpurpose() + " - " + p.getNmpurpose(),
                CrudConsole.adapt(propertyPurposeDAO::insert, propertyPurposeDAO::update, propertyPurposeDAO::delete, propertyPurposeDAO::listAll));
    }

    private void crudPropertyStatus() {
        CrudConsole.run("Status de Imóvel",
                () -> { Property_Status s = new Property_Status(); s.setNmstatus(ler("Status: ")); return s; },
                s -> s.setNmstatus(lerOuManter("Status", s.getNmstatus())),
                propertyStatusDAO::findById,
                s -> s.getCdstatus() + " - " + s.getNmstatus(),
                CrudConsole.adapt(propertyStatusDAO::insert, propertyStatusDAO::update, propertyStatusDAO::delete, propertyStatusDAO::listAll));
    }

    private void crudContractTemplate() {
        CrudConsole.run("Modelos de Contrato",
                () -> {
                    Contract_Templates t = new Contract_Templates();
                    t.setNmtemplate(ler("Nome: "));
                    t.setDsversion(ler("Versão: "));
                    t.setFgactive(ler("Ativo (S/N): "));
                    return t;
                },
                t -> {
                    t.setNmtemplate(lerOuManter("Nome", t.getNmtemplate()));
                    t.setDsversion(lerOuManter("Versão", t.getDsversion()));
                },
                templateDAO::findById,
                t -> t.getCdtemplate() + " - " + t.getNmtemplate() + " [Ver: " + t.getDsversion() + " Ativo: " + t.getFgactive() + "]",
                CrudConsole.adapt(templateDAO::insert, templateDAO::update, templateDAO::delete, templateDAO::listAll));
    }

    private void crudClause() {
        CrudConsole.run("Cláusulas",
                () -> {
                    Clauses c = new Clauses();
                    c.setDstext(ler("Texto: "));
                    c.setCdtopic(lerInt("ID Tópico: "));
                    return c;
                },
                c -> {
                    c.setDstext(lerOuManter("Texto", c.getDstext()));
                    c.setCdtopic(lerIntOuManter("ID Tópico", c.getCdtopic()));
                },
                clauseDAO::findById,
                c -> c.getCdclause() + " - Tópico: " + c.getCdtopic() + " - " + c.getDstext(),
                CrudConsole.adapt(clauseDAO::insert, clauseDAO::update, clauseDAO::delete, clauseDAO::listAll));
    }

    private void crudIndex() {
        CrudConsole.run("Índices Financeiros",
                () -> { Indexes i = new Indexes(); i.setNmindex(ler("Nome: ")); return i; },
                i -> i.setNmindex(lerOuManter("Nome", i.getNmindex())),
                indexDAO::findById,
                i -> i.getCdindex() + " - " + i.getNmindex(),
                CrudConsole.adapt(indexDAO::insert, indexDAO::update, indexDAO::delete, indexDAO::listAll));
    }

    private void crudRole() {
        CrudConsole.run("Papéis Contratuais",
                () -> { Roles r = new Roles(); r.setNmrole(ler("Papel: ")); return r; },
                r -> r.setNmrole(lerOuManter("Papel", r.getNmrole())),
                roleDAO::findById,
                r -> r.getCdrole() + " - " + r.getNmrole(),
                CrudConsole.adapt(roleDAO::insert, roleDAO::update, roleDAO::delete, roleDAO::listAll));
    }

    private void crudBankAccount() {
        CrudConsole.run("Contas Bancárias",
                () -> {
                    Bank_Accounts b = new Bank_Accounts();
                    b.setNragency(ler("Agência: "));
                    b.setNraccount(ler("Conta: "));
                    b.setNrpixkey(ler("PIX: "));
                    b.setCduser(lerInt("ID Usuário: "));
                    return b;
                },
                b -> {
                    b.setNragency(lerOuManter("Agência", b.getNragency()));
                    b.setNraccount(lerOuManter("Conta", b.getNraccount()));
                    b.setNrpixkey(lerOuManter("PIX", b.getNrpixkey()));
                    b.setCduser(lerIntOuManter("ID Usuário", b.getCduser()));
                },
                bankAccountDAO::findById,
                b -> b.getCdbankaccount() + " - Ag: " + b.getNragency() + " Cc: " + b.getNraccount()
                        + " PIX: " + b.getNrpixkey() + " [User: " + b.getCduser() + "]",
                CrudConsole.adapt(bankAccountDAO::insert, bankAccountDAO::update, bankAccountDAO::delete, bankAccountDAO::listAll));
    }

    private void crudNotification() {
        CrudConsole.run("Notificações",
                () -> {
                    Notifications n = new Notifications();
                    n.setDsmessage(ler("Mensagem: "));
                    n.setDtsend(LocalDate.parse(ler("Data (AAAA-MM-DD): ")));
                    n.setCdcontract(lerInt("ID Contrato: "));
                    n.setCduser(lerInt("ID Usuário: "));
                    n.setFgstatus(lerInt("Status (int): "));
                    n.setTpnotification(lerInt("Tipo (int): "));
                    return n;
                },
                n -> {
                    n.setDsmessage(lerOuManter("Mensagem", n.getDsmessage()));
                    String d = ler("Data (" + n.getDtsend() + "): ");
                    if (!d.isEmpty()) n.setDtsend(LocalDate.parse(d));
                    n.setFgstatus(lerIntOuManter("Status", n.getFgstatus()));
                },
                notificationDAO::findById,
                n -> n.getCdnotification() + " - " + n.getDtsend() + " | Status: " + n.getFgstatus() + " | Msg: " + n.getDsmessage(),
                CrudConsole.adapt(notificationDAO::insert, notificationDAO::update, notificationDAO::delete, notificationDAO::listAll));
    }

    private void crudTopic() {
        CrudConsole.run("Tópicos de Contrato",
                () -> { Topics t = new Topics(); t.setNmtopic(ler("Nome do Tópico: ")); return t; },
                t -> t.setNmtopic(lerOuManter("Nome do Tópico", t.getNmtopic())),
                topicDAO::findById,
                t -> t.getCdtopic() + " - " + t.getNmtopic(),
                CrudConsole.adapt(topicDAO::insert, topicDAO::update, topicDAO::delete, topicDAO::listAll));
    }

    private void crudVariable() {
        CrudConsole.run("Variáveis do Contrato",
                () -> {
                    Variables v = new Variables();
                    v.setNmvariable(ler("Nome: "));
                    v.setVlvariable(ler("Valor: "));
                    v.setTpvariable(ler("Tipo (Texto, Moeda, Data): "));
                    v.setFgtriggeralert(confirmar("Gera Alerta? (s/n): "));
                    v.setCdcontract(lerInt("ID Contrato: "));
                    return v;
                },
                v -> {
                    v.setNmvariable(lerOuManter("Nome", v.getNmvariable()));
                    v.setVlvariable(lerOuManter("Valor", v.getVlvariable()));
                    v.setTpvariable(lerOuManter("Tipo", v.getTpvariable()));
                    v.setFgtriggeralert(confirmar("Gera Alerta? (s/n): "));
                    v.setCdcontract(lerIntOuManter("ID Contrato", v.getCdcontract()));
                },
                variableDAO::findById,
                v -> v.getCdvariable() + " - " + v.getNmvariable() + ": " + v.getVlvariable(),
                CrudConsole.adapt(variableDAO::insert, variableDAO::update, variableDAO::delete, variableDAO::listAll));
    }

    private void crudCommission() {
        CrudConsole.run("Comissões",
                () -> {
                    Commissions c = new Commissions();
                    c.setVlcommission(lerDouble("Valor (0.0): "));
                    String d = ler("Data Pagamento (AAAA-MM-DD) ou deixe vazio: ");
                    if (!d.isEmpty()) c.setDtpayment(LocalDate.parse(d));
                    c.setCdcontract(lerInt("ID Contrato: "));
                    return c;
                },
                c -> {
                    try {
                        c.setVlcommission(Double.parseDouble(lerOuManter("Valor Atual", String.valueOf(c.getVlcommission()))));
                    } catch (Exception e) { /* mantem */ }
                    String d = ler("Data Pagamento (" + c.getDtpayment() + "): ");
                    if (!d.isEmpty()) c.setDtpayment(LocalDate.parse(d));
                    c.setCdcontract(lerIntOuManter("ID Contrato", c.getCdcontract()));
                },
                commissionDAO::findById,
                c -> c.getCdcommission() + " - R$" + c.getVlcommission() + " (Contrato: " + c.getCdcontract() + ")",
                CrudConsole.adapt(commissionDAO::insert, commissionDAO::update, commissionDAO::delete, commissionDAO::listAll));
    }

    private void crudBrokerData() {
        CrudConsole.run("Dados de Corretores",
                () -> {
                    Broker_Data b = new Broker_Data();
                    b.setNrcreci(ler("CRECI: "));
                    b.setCduser(lerInt("ID Usuário: "));
                    return b;
                },
                b -> {
                    b.setNrcreci(lerOuManter("CRECI", b.getNrcreci()));
                    System.out.println("ID Usuário não pode ser alterado.");
                },
                brokerDataDAO::findById, // findById vai usar o cduser
                b -> "User ID: " + b.getCduser() + " - CRECI: " + b.getNrcreci(),
                CrudConsole.adapt(brokerDataDAO::insert, brokerDataDAO::update, brokerDataDAO::delete, brokerDataDAO::listAll));
    }
}
