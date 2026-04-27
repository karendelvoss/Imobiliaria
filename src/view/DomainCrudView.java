package view;

import dao.UserDAO;
import dao.BankAccountDAO;
import dao.CityDAO;
import dao.ClauseDAO;
import dao.ContractTemplateDAO;
import dao.CountryDAO;
import dao.ContractDAO;
import dao.TopicDAO;
import dao.BrokerDataDAO;
import dao.DistrictDAO;
import dao.IndexDAO;
import dao.NotificationDAO;
import dao.OccupationDAO;
import dao.PropertyPurposeDAO;
import dao.PropertyStatusDAO;
import dao.PropertyTypeDAO;
import dao.StateDAO;
import dao.AddressDAO;
import dao.IndexRateDAO;
import dao.RoleDAO;
import dao.NotaryDAO;
import dao.InstallmentDAO;
import dao.UserContractDAO;
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
import model.Broker_Data;
import model.States;
import model.Addresses;
import model.Index_Rates;

import java.time.LocalDate;

import static view.ConsoleIO.*;

/**
 * Agregador de interfaces de console para gestão de domínios auxiliares do sistema.
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
    private final BrokerDataDAO brokerDataDAO;
    private final StateDAO stateDAO;
    private final AddressDAO addressDAO;
    private final IndexRateDAO indexRateDAO;
    private final UserDAO userDAO;
    private final ContractDAO contractDAO;
    private final InstallmentDAO installmentDAO;
    private final UserContractDAO userContractDAO;
    private final NotaryDAO notaryDAO;
    private final OccupationDAO occupationDAO;

    public DomainCrudView(CountryDAO countryDAO, CityDAO cityDAO, DistrictDAO districtDAO,
                          PropertyTypeDAO propertyTypeDAO, PropertyPurposeDAO propertyPurposeDAO,
                          PropertyStatusDAO propertyStatusDAO, ContractTemplateDAO templateDAO,
                          ClauseDAO clauseDAO, IndexDAO indexDAO, RoleDAO roleDAO,
                          BankAccountDAO bankAccountDAO, NotificationDAO notificationDAO,
                          TopicDAO topicDAO, BrokerDataDAO brokerDataDAO, StateDAO stateDAO, 
                          AddressDAO addressDAO, IndexRateDAO indexRateDAO, UserDAO userDAO, ContractDAO contractDAO,
                          InstallmentDAO installmentDAO, UserContractDAO userContractDAO, NotaryDAO notaryDAO, OccupationDAO occupationDAO) {
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
        this.brokerDataDAO = brokerDataDAO;
        this.stateDAO = stateDAO;
        this.addressDAO = addressDAO;
        this.indexRateDAO = indexRateDAO;
        this.userDAO = userDAO;
        this.contractDAO = contractDAO;
        this.installmentDAO = installmentDAO;
        this.userContractDAO = userContractDAO;
        this.notaryDAO = notaryDAO;
        this.occupationDAO = occupationDAO;
    }

    /**
     * Menu para gestão de dados de localização.
     */
    public void menuLocalizacao() {
        System.out.println("\n--- LOCALIZAÇÃO ---");
        System.out.println("1. Países  2. Estados  3. Cidades  4. Bairros  5. Endereços  0. Voltar");
        switch (lerIntSeguro("Escolha: ")) {
            case 1: crudCountry(); break;
            case 2: crudState(); break;
            case 3: crudCity(); break;
            case 4: crudDistrict(); break;
            case 5: crudAddress(); break;
        }
    }

    /**
     * Menu para gestão de atributos de imóveis.
     */
    public void menuAtributosImoveis() {
        System.out.println("\n--- DOMÍNIOS DE IMÓVEIS ---");
        System.out.println("1. Tipos  2. Finalidades  3. Status  0. Voltar");
        switch (lerIntSeguro("Escolha: ")) {
            case 1: crudPropertyType(); break;
            case 2: crudPropertyPurpose(); break;
            case 3: crudPropertyStatus(); break;
        }
    }

    /**
     * Menu para gestão de domínios relacionados a contratos.
     */
    public void menuAtributosContratuais() {
        System.out.println("\n--- DOMÍNIOS CONTRATUAIS ---");
        System.out.println("1. Modelos (Templates)  2. Cláusulas  3. Índices  4. Taxas de Índice  5. Papéis");
        System.out.println("6. Tópicos  7. Corretores  8. Exportar Modelo para PDF  0. Voltar");
        switch (lerIntSeguro("Escolha: ")) {
            case 1: crudContractTemplate(); break;
            case 2: crudClause(); break;
            case 3: crudIndex(); break;
            case 4: crudIndexRate(); break;
            case 5: crudRole(); break;
            case 6: crudTopic(); break;
            case 7: crudBrokerData(); break;
            case 8: exportTemplatePdf(); break;
        }
    }

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

    private void crudState() {
        CrudConsole.run("Estados",
                () -> {
                    States s = new States();
                    s.setNmstate(ler("Nome: "));
                    s.setSgstate(ler("Sigla: "));
                    s.setCdcountry(lerInt("ID País: "));
                    return s;
                },
                s -> {
                    s.setNmstate(lerOuManter("Novo Nome", s.getNmstate()));
                    s.setSgstate(lerOuManter("Nova Sigla", s.getSgstate()));
                    s.setCdcountry(lerIntOuManter("Novo ID País", s.getCdcountry()));
                },
                stateDAO::findById,
                s -> s.getCdstate() + " - " + s.getNmstate() + " (" + s.getSgstate() + ") [País: " + s.getCdcountry() + "]",
                CrudConsole.adapt(stateDAO::insert, stateDAO::update, stateDAO::delete, stateDAO::listAll));
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

    private void crudAddress() {
        CrudConsole.run("Endereços",
                () -> {
                    Addresses a = new Addresses();
                    a.setCdzipcode(ler("CEP: "));
                    a.setNmstreet(ler("Rua: "));
                    a.setNraddress(ler("Número: "));
                    a.setDscomplement(ler("Complemento: "));
                    a.setCddistrict(lerInt("ID Bairro: "));
                    return a;
                },
                a -> {
                    a.setCdzipcode(lerOuManter("Novo CEP", a.getCdzipcode()));
                    a.setNmstreet(lerOuManter("Nova Rua", a.getNmstreet()));
                    a.setNraddress(lerOuManter("Novo Número", a.getNraddress()));
                    a.setDscomplement(lerOuManter("Novo Complemento", a.getDscomplement()));
                    a.setCddistrict(lerIntOuManter("Novo ID Bairro", a.getCddistrict()));
                },
                addressDAO::findById,
                a -> a.getCdaddress() + " - " + a.getNmstreet() + ", " + a.getNraddress() + " (" + a.getCdzipcode() + ")",
                CrudConsole.adapt(addressDAO::insert, addressDAO::update, addressDAO::delete, addressDAO::listAll));
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
                    t.setFgactive(confirmar("Ativo? (s/n): "));
                    return t;
                },
                t -> {
                    t.setNmtemplate(lerOuManter("Nome", t.getNmtemplate()));
                    t.setDsversion(lerOuManter("Versão", t.getDsversion()));
                },
                templateDAO::findById,
                t -> t.getCdtemplate() + " - " + t.getNmtemplate() + " [Ver: " + t.getDsversion() + " Ativo: " + (t.isFgactive() ? "Sim" : "Não") + "]",
                CrudConsole.adapt(templateDAO::insert, templateDAO::update, templateDAO::delete, templateDAO::listAll));
    }

    private void crudClause() {
        CrudConsole.run("Cláusulas",
                () -> {
                    Clauses c = new Clauses();
                    c.setDstext(ler("Texto: "));
                    c.setCdtopic(lerInt("ID Tópico: "));
                    c.setNrorder(lerInt("Ordem da Cláusula: "));
                    return c;
                },
                c -> {
                    c.setDstext(lerOuManter("Texto", c.getDstext()));
                    c.setCdtopic(lerIntOuManter("ID Tópico", c.getCdtopic()));
                    c.setNrorder(lerIntOuManter("Ordem da Cláusula", c.getNrorder()));
                },
                clauseDAO::findById,
                c -> c.getCdclause() + " - Tópico: " + c.getCdtopic() + " - Ordem: " + c.getNrorder() + " - " + c.getDstext(),
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

    private void crudIndexRate() {
        CrudConsole.run("Taxas de Índice",
                () -> {
                    Index_Rates ir = new Index_Rates();
                    ir.setRefmonth(lerInt("Mês Ref (MM): "));
                    ir.setRefyear(lerInt("Ano Ref (AAAA): "));
                    ir.setVlrate(lerDouble("Valor Taxa (%): "));
                    ir.setFk_Indexes_cdindex(lerInt("ID Índice: "));
                    return ir;
                },
                ir -> {
                    ir.setRefmonth(lerIntOuManter("Mês Ref", ir.getRefmonth()));
                    ir.setRefyear(lerIntOuManter("Ano Ref", ir.getRefyear()));
                    try { ir.setVlrate(Double.parseDouble(lerOuManter("Valor Taxa", String.valueOf(ir.getVlrate())))); } catch (Exception e) {}
                    ir.setFk_Indexes_cdindex(lerIntOuManter("ID Índice", ir.getFk_Indexes_cdindex()));
                },
                indexRateDAO::findById,
                ir -> ir.getCdrate() + " - " + ir.getRefmonth() + "/" + ir.getRefyear() + " | Taxa: " + ir.getVlrate() + "% [Índice ID: " + ir.getFk_Indexes_cdindex() + "]",
                CrudConsole.adapt(indexRateDAO::insertRate, indexRateDAO::update, indexRateDAO::delete, indexRateDAO::listAll));
    }

    private void crudRole() {
        CrudConsole.run("Papéis Contratuais",
                () -> { Roles r = new Roles(); r.setNmrole(ler("Papel: ")); return r; },
                r -> r.setNmrole(lerOuManter("Papel", r.getNmrole())),
                roleDAO::findById,
                r -> r.getCdrole() + " - " + r.getNmrole(),
                CrudConsole.adapt(roleDAO::insert, roleDAO::update, roleDAO::delete, roleDAO::listAll));
    }

    public void crudBankAccount() {
        CrudConsole.run("Contas Bancárias",
                () -> {
                    Bank_Accounts b = new Bank_Accounts();
                    b.setNragency(ler("Agência: "));
                    b.setNraccount(ler("Conta: "));
                    b.setNrpixkey(ler("PIX: "));
                    int idUser = lerIdValido("ID Usuário", userDAO::findById, () -> userDAO.getAllUsersList().forEach(System.out::println));
                    if (idUser == -1) return null;
                    b.setCduser(idUser);
                    return b;
                },
                b -> {
                    b.setNragency(lerOuManter("Agência", b.getNragency()));
                    b.setNraccount(lerOuManter("Conta", b.getNraccount()));
                    b.setNrpixkey(lerOuManter("PIX", b.getNrpixkey()));
                    b.setCduser(lerIdOuManter("ID Usuário", b.getCduser(), userDAO::findById, () -> userDAO.getAllUsersList().forEach(System.out::println)));
                },
                bankAccountDAO::findById,
                b -> b.getCdbankaccount() + " - Ag: " + b.getNragency() + " Cc: " + b.getNraccount()
                        + " PIX: " + b.getNrpixkey() + " [User: " + b.getCduser() + "]",
                CrudConsole.adapt(bankAccountDAO::insert, bankAccountDAO::update, bankAccountDAO::delete, bankAccountDAO::listAll));
    }

    public void crudNotification() {
        CrudConsole.run("Notificações",
                () -> {
                    Notifications n = new Notifications();
                    n.setDsmessage(ler("Mensagem: "));
                    n.setDtsend(LocalDate.parse(ler("Data (AAAA-MM-DD): ")));
                    int idContrato = lerIdValido("ID Contrato", contractDAO::findById, () -> contractDAO.getActiveContractsList().forEach(System.out::println));
                    if (idContrato <= 0) return null;
                    n.setCdcontract(idContrato);
                    int idUser = lerIdValido("ID Usuário", userDAO::findById, () -> userDAO.getAllUsersList().forEach(System.out::println));
                    if (idUser <= 0) return null;
                    n.setCduser(idUser);
                    n.setCdnotificationtemplate(lerInt("ID do Template: "));
                    n.setFgchannel(lerInt("Canal (1=Email, 2=WhatsApp): "));
                    return n;
                },
                n -> {
                    n.setDsmessage(lerOuManter("Mensagem", n.getDsmessage()));
                    String d = ler("Data (" + n.getDtsend() + "): ");
                    if (!d.isEmpty()) n.setDtsend(LocalDate.parse(d));
                    n.setFgchannel(lerIntOuManter("Canal", n.getFgchannel()));
                },
                notificationDAO::findById,
                n -> n.getCdnotification() + " - " + n.getDtsend()
                    + " | Canal: " + model.NotificationChannel.fromCode(n.getFgchannel()).getDescription()
                    + " | Msg: " + n.getDsmessage(),
                CrudConsole.adapt(notificationDAO::insert, n -> System.out.println("Edição desabilitada para notificações."), notificationDAO::delete, notificationDAO::listAll));
    }

    private void crudTopic() {
        CrudConsole.run("Tópicos de Contrato",
                () -> { 
                    Topics t = new Topics(); 
                    t.setNmtopic(ler("Nome: "));
                    t.setNrorder(lerInt("Ordem: "));
                    return t;
                },
                t -> {
                    t.setNmtopic(lerOuManter("Nome", t.getNmtopic()));
                    t.setNrorder(lerIntOuManter("Ordem", t.getNrorder()));
                },
                topicDAO::findById,
                t -> t.getCdtopic() + " - Ordem: " + t.getNrorder() + " - " + t.getNmtopic(),
                CrudConsole.adapt(
                        t -> {
                            topicDAO.insert(t);
                            if (confirmar("Vincular a um template agora? (s/n): ")) {
                                int idTemplate = lerIdValido("ID Template", templateDAO::findById, 
                                        () -> templateDAO.listAll().forEach(tmpl -> System.out.println(tmpl.getCdtemplate() + " - " + tmpl.getNmtemplate())));
                                if (idTemplate > 0) {
                                    templateDAO.linkTopic(idTemplate, t.getCdtopic());
                                }
                            }
                        }, 
                        topicDAO::update, 
                        topicDAO::delete, 
                        topicDAO::listAll
                ));
    }

    private void exportTemplatePdf() {
        System.out.println("\n--- EXPORTAR MODELO PARA PDF ---");
        int idTemplate = lerIdValido("ID do Template", templateDAO::findById, 
            () -> templateDAO.listAll().forEach(tmpl -> System.out.println(tmpl.getCdtemplate() + " - " + tmpl.getNmtemplate())));
        if (idTemplate > 0) {
            service.ContractPdfService pdfService = new service.ContractPdfService(
                templateDAO, topicDAO, clauseDAO, contractDAO, new dao.PropertyDAO(), userDAO, userContractDAO,
                addressDAO, districtDAO, cityDAO, installmentDAO, bankAccountDAO, indexDAO, notaryDAO, occupationDAO
            );
            pdfService.generateTemplatePdf(idTemplate);
        }
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
                brokerDataDAO::findById,
                b -> "User ID: " + b.getCduser() + " - CRECI: " + b.getNrcreci(),
                CrudConsole.adapt(brokerDataDAO::insert, brokerDataDAO::update, brokerDataDAO::delete, brokerDataDAO::listAll));
    }
}
