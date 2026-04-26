package service;

import com.itextpdf.text.Document;
import com.itextpdf.text.Font;
import com.itextpdf.text.FontFactory;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfWriter;
import dao.*;
import model.*;

import java.io.File;
import java.io.FileOutputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;

/**
 * Serviço responsável pela geração de documentos PDF para modelos de contrato e contratos preenchidos.
 */
public class ContractPdfService {

    private final ContractTemplateDAO templateDAO;
    private final TopicDAO topicDAO;
    private final ClauseDAO clauseDAO;
    private final ContractDAO contractDAO;
    private final PropertyDAO propertyDAO;
    private final UserDAO userDAO;
    private final UserContractDAO userContractDAO;
    private final AddressDAO addressDAO;
    private final DistrictDAO districtDAO;
    private final CityDAO cityDAO;
    private final InstallmentDAO installmentDAO;
    private final BankAccountDAO bankAccountDAO;
    private final VariableDAO variableDAO;
    private final IndexDAO indexDAO;
    private final NotaryDAO notaryDAO;
    private final OccupationDAO occupationDAO;

    public ContractPdfService(ContractTemplateDAO templateDAO, TopicDAO topicDAO, ClauseDAO clauseDAO,
                                ContractDAO contractDAO, PropertyDAO propertyDAO, UserDAO userDAO, 
                                UserContractDAO userContractDAO, AddressDAO addressDAO, DistrictDAO districtDAO,
                                CityDAO cityDAO, InstallmentDAO installmentDAO, BankAccountDAO bankAccountDAO,
                                VariableDAO variableDAO, IndexDAO indexDAO, NotaryDAO notaryDAO, OccupationDAO occupationDAO) {
        this.templateDAO = templateDAO;
        this.topicDAO = topicDAO;
        this.clauseDAO = clauseDAO;
        this.contractDAO = contractDAO;
        this.propertyDAO = propertyDAO;
        this.userDAO = userDAO;
        this.userContractDAO = userContractDAO;
        this.addressDAO = addressDAO;
        this.districtDAO = districtDAO;
        this.cityDAO = cityDAO;
        this.installmentDAO = installmentDAO;
        this.bankAccountDAO = bankAccountDAO;
        this.variableDAO = variableDAO;
        this.indexDAO = indexDAO;
        this.notaryDAO = notaryDAO;
        this.occupationDAO = occupationDAO;
    }

    /**
     * Gera um PDF representando a estrutura de um modelo de contrato (Template).
     * 
     * @param templateId Identificador do modelo de contrato.
     */
    public void generateTemplatePdf(int templateId) {
        Contract_Templates template = templateDAO.findById(templateId);
        if (template == null) {
            System.out.println("Template não encontrado para gerar PDF.");
            return;
        }

        File pdfDir = new File("pdfs");
        if (!pdfDir.exists()) {
            pdfDir.mkdirs();
        }

        String fileName = "pdfs/modelo_contrato_" + templateId + ".pdf";
        Document document = new Document();

        try {
            PdfWriter.getInstance(document, new FileOutputStream(fileName));
            document.open();

            Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18);
            Font topicFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 14);
            Font textFont = FontFactory.getFont(FontFactory.HELVETICA, 12);

            Paragraph title = new Paragraph("Modelo de Contrato: " + template.getNmtemplate(), titleFont);
            title.setAlignment(com.itextpdf.text.Element.ALIGN_CENTER);
            title.setSpacingAfter(20);
            document.add(title);

            Paragraph version = new Paragraph("Versão: " + template.getDsversion(), textFont);
            version.setSpacingAfter(20);
            document.add(version);

            List<Topics> topics = topicDAO.findByTemplateId(templateId);
            for (Topics topic : topics) {
                Paragraph pTopic = new Paragraph(topic.getNrorder() + ". " + topic.getNmtopic(), topicFont);
                pTopic.setSpacingBefore(10);
                pTopic.setSpacingAfter(10);
                document.add(pTopic);

                List<Clauses> clauses = clauseDAO.findByTopicId(topic.getCdtopic());
                for (Clauses clause : clauses) {
                    Paragraph pClause = new Paragraph(topic.getNrorder() + "." + clause.getNrorder() + " - " + clause.getDstext(), textFont);
                    pClause.setSpacingAfter(5);
                    pClause.setIndentationLeft(20);
                    document.add(pClause);
                }
            }

            document.close();
            System.out.println("PDF gerado com sucesso em: " + new File(fileName).getAbsolutePath());

        } catch (Exception e) {
            System.err.println("Erro ao gerar o PDF: " + e.getMessage());
        }
    }

    /**
     * Gera um PDF de contrato preenchido com todos os dados das partes, imóvel e financeiros.
     * 
     * @param contractId Identificador do contrato.
     */
    public void generateContractPdf(int contractId) {
        Contracts contract = contractDAO.findById(contractId);
        if (contract == null) {
            System.out.println("Contrato não encontrado para gerar PDF.");
            return;
        }

        Contract_Templates template = templateDAO.findById(contract.getCdtemplate());
        if (template == null) {
            System.out.println("Template do contrato não encontrado.");
            return;
        }

        Map<String, String> vars = new HashMap<>();
        DateTimeFormatter df = DateTimeFormatter.ofPattern("dd/MM/yyyy");

        vars.put("%data_inicio%", contract.getDtcreation().format(df));
        vars.put("%data_termino%", contract.getDtlimit() != null ? contract.getDtlimit().format(df) : "Indeterminado");
        if (contract.getDtlimit() != null) {
            long months = ChronoUnit.MONTHS.between(contract.getDtcreation(), contract.getDtlimit());
            vars.put("%prazo_meses%", String.valueOf(months));
        } else {
            vars.put("%prazo_meses%", "Indeterminado");
        }
        
        Indexes index = indexDAO.findById(contract.getCdindex());
        vars.put("%index%", index != null ? index.getNmindex() : "IGP-M");

        vars.put("%dia_assinatura%", String.valueOf(contract.getDtcreation().getDayOfMonth()));
        vars.put("%ano_assinatura%", String.valueOf(contract.getDtcreation().getYear()));
        String[] meses = {"Janeiro", "Fevereiro", "Março", "Abril", "Maio", "Junho", "Julho", "Agosto", "Setembro", "Outubro", "Novembro", "Dezembro"};
        vars.put("%mes_assinatura%", meses[contract.getDtcreation().getMonthValue() - 1]);

        Properties property = propertyDAO.findById(contract.getCdproperty());
        if (property != null) {
            vars.put("%matricula_imovel%", property.getNrregistration());
            vars.put("%area_privativa%", String.format("%.2f", property.getVltotalarea()));
            vars.put("%area_comum%", "0,00");
            
            Addresses addr = addressDAO.findById(property.getCdaddress());
            if (addr != null) {
                vars.put("%rua_imovel%", addr.getNmstreet());
                vars.put("%numero_imovel%", addr.getNraddress());
                Districts dist = districtDAO.findById(addr.getCddistrict());
                if (dist != null) {
                    vars.put("%bairro_imovel%", dist.getNmdistrict());
                    Cities city = cityDAO.findById(dist.getCdcity());
                    if (city != null) {
                        vars.put("%cidade_imovel%", city.getNmcity());
                    }
                }
            }
        }

        // 3. DADOS DAS PARTES (LOCATÁRIO = 1, LOCADOR = 2, TESTEMUNHA = 3, REPRESENTANTE = 4)
        List<User_Contract> partes = userContractDAO.findByContractId(contractId);
        Users locador = null;
        Users locatario = null;
        Users representante = null;
        java.util.List<Users> testemunhas = new java.util.ArrayList<>();

        for (User_Contract uc : partes) {
            if (uc.getCdrole() == 2) locador = userDAO.findById(uc.getCduser());
            if (uc.getCdrole() == 1) locatario = userDAO.findById(uc.getCduser());
            if (uc.getCdrole() == 4) representante = userDAO.findById(uc.getCduser());
            if (uc.getCdrole() == 3) {
                Users t = userDAO.findById(uc.getCduser());
                if (t != null) testemunhas.add(t);
            }
        }

        if (locador != null) {
            vars.put("%locador%", locador.getNmuser());
            vars.put("%nome_locador_assinatura%", locador.getNmuser());
            vars.put("%cpf%", locador.getDocument());
            vars.put("%identidade%", locador.getDocument());
            vars.put("%orgao_emissor%", locador.getDsissuingbody());
            vars.put("%telefone_locador%", locador.getNrcellphone());
            vars.put("%email_locador%", "nao_informado@email.com");
            
            Bank_Accounts ba = bankAccountDAO.findByUserId(locador.getCduser());
            if (ba != null) {
                vars.put("%titular_conta%", locador.getNmuser());
                vars.put("%cpf_titular%", locador.getDocument());
                vars.put("%agencia%", ba.getNragency());
                vars.put("%conta%", ba.getNraccount());
                vars.put("%pix%", ba.getNrpixkey());
            }
        }

        if (locatario != null) {
            vars.put("%nome_locatario%", locatario.getNmuser());
            vars.put("%nome_locatario_assinatura%", locatario.getNmuser());
            vars.put("%cpf_locatario%", locatario.getDocument());
            vars.put("%identidade_locatario%", locatario.getDocument());
            vars.put("%orgao_emissor_locatario%", locatario.getDsissuingbody());
            vars.put("%telefone_locatario%", locatario.getNrcellphone());
            vars.put("%email_locatario%", "nao_informado@email.com");
        }

        if (representante != null) {
            vars.put("%nome_filho_representante%", representante.getNmuser());
            vars.put("%nome_filho_assinatura%", representante.getNmuser());
            vars.put("%cpf_filho%", representante.getDocument());
            vars.put("%identidade_filho%", representante.getDocument());
            // Se o representante for o filho, preenchemos o marcador %filho% também
            vars.put("%filho%", representante.getNmuser());
            
            if (representante.getCdoccupation() > 0) {
                Occupations occ = occupationDAO.findById(representante.getCdoccupation());
                if (occ != null) {
                    vars.put("%estado_civil_e_profissao_filho%", "brasileiro, " + occ.getNmoccupation());
                } else {
                    vars.put("%estado_civil_e_profissao_filho%", "brasileiro, Profissão não informada");
                }
            } else {
                vars.put("%estado_civil_e_profissao_filho%", "brasileiro");
            }
        }

        for (int i = 0; i < testemunhas.size(); i++) {
            vars.put("%nome_testemunha_" + (i + 1) + "%", testemunhas.get(i).getNmuser());
        }

        if (contract.getCdnotary() > 0) {
            Notaries notary = notaryDAO.findById(contract.getCdnotary());
            if (notary != null) {
                vars.put("%livro_tabelionato%", notary.getBook());
                vars.put("%folha_livro_tabelionato%", notary.getLeaf());
                vars.put("%numero_tabelionato%", String.valueOf(notary.getNrnotary()));
                if (notary.getDt() != null) vars.put("%data_tabelionato%", notary.getDt().format(df));
                
                Cities notaryCity = cityDAO.findById(notary.getCdcity());
                if (notaryCity != null) {
                    vars.put("%cidade_tabelionato%", notaryCity.getNmcity());
                    vars.put("%cidade tabelionato%", notaryCity.getNmcity());
                }
            }
        }

        List<Installments> installments = installmentDAO.findByContractId(contractId);
        if (!installments.isEmpty()) {
            Installments first = installments.get(0);
            vars.put("%valor_aluguel%", String.format("R$ %.2f", first.getVlbase()));
            vars.put("%primeiro_vencimento%", first.getDtdue().format(df));
            // Trata como valor absoluto digitado pelo usuário (ex: 10 para 10%)
            vars.put("%multa_atraso%", String.format("%.2f%%", first.getVlpenalty()));
            vars.put("%juros_mensal%", String.format("%.2f%%", first.getVlinterest()));
        }

        List<Variables> contractVars = variableDAO.findByContractId(contractId);
        for (Variables v : contractVars) {
            vars.put("%" + v.getNmvariable() + "%", v.getVlvariable());
        }
        
        vars.putIfAbsent("%valor_caucao%", vars.getOrDefault("%valor_aluguel%", "R$ 0,00"));
        vars.putIfAbsent("%cidade_tabelionato%", "Joinville");
        vars.putIfAbsent("%cidade tabelionato%", "Joinville");
        vars.putIfAbsent("%nome_filho_representante%", "Representante Legal");
        vars.putIfAbsent("%filho%", "___________________________");
        vars.putIfAbsent("%cpf_filho%", "___________________________");
        vars.putIfAbsent("%identidade_filho%", "___________________________");
        vars.putIfAbsent("%estado_civil_e_profissao_filho%", "brasileiro, ___________________________");
        vars.putIfAbsent("%nome_testemunha_1%", "___________________________");
        vars.putIfAbsent("%nome_testemunha_2%", "___________________________");

        File pdfDir = new File("pdfs");
        if (!pdfDir.exists()) pdfDir.mkdirs();

        String fileName = "pdfs/contrato_preenchido_" + contractId + ".pdf";
        Document document = new Document();

        try {
            PdfWriter.getInstance(document, new FileOutputStream(fileName));
            document.open();

            Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18);
            Font topicFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 14);
            Font textFont = FontFactory.getFont(FontFactory.HELVETICA, 12);

            Paragraph title = new Paragraph("CONTRATO DE LOCAÇÃO", titleFont);
            title.setAlignment(com.itextpdf.text.Element.ALIGN_CENTER);
            title.setSpacingAfter(20);
            document.add(title);

            List<Topics> topics = topicDAO.findByTemplateId(template.getCdtemplate());
            for (Topics topic : topics) {
                Paragraph pTopic = new Paragraph(topic.getNrorder() + ". " + topic.getNmtopic(), topicFont);
                pTopic.setSpacingBefore(10);
                pTopic.setSpacingAfter(10);
                document.add(pTopic);

                List<Clauses> clauses = clauseDAO.findByTopicId(topic.getCdtopic());
                for (Clauses clause : clauses) {
                    String clauseText = clause.getDstext();
                    
                    // Lógica Condicional: Se não há representante, remove/pula trechos de representação
                    if (representante == null && (clauseText.contains("%filho%") || clauseText.contains("%nome_filho_representante%"))) {
                        if (clauseText.contains("%locador%") || clauseText.contains("%cpf%")) {
                            // Se a cláusula contém o Locador, removemos apenas o trecho da representação
                            clauseText = clauseText.replaceAll("(?i)\\.?\\s*Representado por.*?%identidade_filho%[^.]*\\.", ".");
                            clauseText = clauseText.replaceAll("(?i)\\.?\\s*Representado por.*?%cpf_filho%[^.]*\\.", ".");
                            // Caso o regex não pegue tudo, garantimos que os placeholders sumam
                            clauseText = clauseText.replace("%filho%", "").replace("%cpf_filho%", "").replace("%identidade_filho%", "").replace("%estado_civil_e_profissao_filho%", "");
                        } else {
                            // Se for uma cláusula dedicada apenas ao representante, pula ela
                            continue;
                        }
                    }

                    for (Map.Entry<String, String> entry : vars.entrySet()) {
                        if (entry.getValue() != null) {
                            clauseText = clauseText.replace(entry.getKey(), entry.getValue());
                        }
                    }

                    Paragraph pClause = new Paragraph(topic.getNrorder() + "." + clause.getNrorder() + " - " + clauseText, textFont);
                    pClause.setSpacingAfter(5);
                    pClause.setIndentationLeft(20);
                    document.add(pClause);
                }
            }

            document.close();
            System.out.println("Contrato em PDF gerado com sucesso em: " + new File(fileName).getAbsolutePath());

        } catch (Exception e) {
            System.err.println("Erro ao gerar o PDF do contrato: " + e.getMessage());
        }
    }
}
