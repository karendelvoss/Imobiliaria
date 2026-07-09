package dao;

import model.*;
import com.mongodb.client.ClientSession;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Sorts;
import com.mongodb.client.model.Updates;
import org.bson.Document;
import org.bson.conversions.Bson;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Gerencia as operações de persistência e processos de negócio de contratos.
 *
 * No modelo MongoDB, o documento de contrato contém:
 * <ul>
 *   <li>Parcelas embarcadas como array {@code installments}</li>
 *   <li>Participantes embarcados como array {@code participants}</li>
 *   <li>Referências (IDs) para imóvel, template e índice</li>
 * </ul>
 */
public class ContractDAO {

    private static final String COLLECTION_NAME = "contracts";
    private static final String PROPERTIES_COLLECTION = "properties";
    private static final String NOTIFICATIONS_COLLECTION = "notifications";
    private static final String INDEXES_COLLECTION = "indexes";

    /**
     * Obtém a coleção MongoDB de contratos.
     */
    private MongoCollection<Document> getCollection() {
        return Conexao.getCollection(COLLECTION_NAME);
    }

    // ========== Conversão Model ↔ BSON ==========

    /**
     * Converte um objeto Contracts para Document BSON.
     * Inclui arrays de installments e participants quando fornecidos.
     *
     * @param contract Objeto do modelo.
     * @param participants Lista de participantes (pode ser null ou vazia).
     * @param installments Lista de parcelas (pode ser null ou vazia).
     * @return Document BSON correspondente.
     */
    public static Document toDocument(Contracts contract, List<User_Contract> participants, List<Installments> installments) {
        Document doc = new Document();
        doc.append("_id", contract.getCdcontract());
        doc.append("dtcreation", contract.getDtcreation() != null ? contract.getDtcreation().toString() : null);
        doc.append("dstitle", contract.getDstitle());
        doc.append("cdtemplate", contract.getCdtemplate() > 0 ? contract.getCdtemplate() : 1);
        doc.append("cdproperty", contract.getCdproperty() > 0 ? contract.getCdproperty() : 0);
        doc.append("cdindex", contract.getCdindex() > 0 ? contract.getCdindex() : null);
        doc.append("dtlimit", contract.getDtlimit() != null ? contract.getDtlimit().toString() : null);
        doc.append("cdstatus", contract.getCdstatus() > 0 ? contract.getCdstatus() : ContractStatus.ATIVO.getCode());
        doc.append("notary", contract.getCdnotary() > 0 ? new Document("cdnotary", contract.getCdnotary()) : null);

        // Participantes embarcados
        List<Document> participantDocs = new ArrayList<>();
        if (participants != null) {
            for (User_Contract uc : participants) {
                Document pDoc = new Document();
                pDoc.append("cduser", uc.getCduser());
                pDoc.append("cdrole", uc.getCdrole());
                pDoc.append("nmrole", resolveRoleName(uc.getCdrole()));
                participantDocs.add(pDoc);
            }
        }
        doc.append("participants", participantDocs);

        // Parcelas embarcadas
        List<Document> installmentDocs = new ArrayList<>();
        if (installments != null) {
            for (Installments inst : installments) {
                installmentDocs.add(installmentToDocument(inst));
            }
        }
        doc.append("installments", installmentDocs);

        return doc;
    }

    /**
     * Converte um Document BSON para objeto Contracts.
     *
     * @param doc Document BSON.
     * @return Objeto Contracts, ou null se doc for null.
     */
    public static Contracts fromDocument(Document doc) {
        if (doc == null) return null;
        Contracts c = new Contracts();
        c.setCdcontract(doc.getInteger("_id"));
        String dtcreation = doc.getString("dtcreation");
        if (dtcreation != null && !dtcreation.isEmpty()) {
            c.setDtcreation(LocalDate.parse(dtcreation));
        }
        c.setDstitle(doc.getString("dstitle"));
        c.setCdtemplate(doc.getInteger("cdtemplate", 0));
        c.setCdproperty(doc.getInteger("cdproperty", 0));
        c.setCdindex(doc.getInteger("cdindex") != null ? doc.getInteger("cdindex") : 0);
        String dtlimit = doc.getString("dtlimit");
        if (dtlimit != null && !dtlimit.isEmpty()) {
            c.setDtlimit(LocalDate.parse(dtlimit));
        }
        c.setCdstatus(doc.getInteger("cdstatus", 0));
        Document notaryDoc = doc.get("notary", Document.class);
        if (notaryDoc != null) {
            c.setCdnotary(notaryDoc.getInteger("cdnotary", 0));
        }
        return c;
    }

    /**
     * Converte uma parcela (Installments) para Document BSON embarcado.
     */
    public static Document installmentToDocument(Installments inst) {
        Document doc = new Document();
        doc.append("cdinstallment", inst.getCdinstallment());
        doc.append("nrinstallment", inst.getNrinstallment());
        doc.append("dtdue", inst.getDtdue() != null ? inst.getDtdue().toString() : null);
        doc.append("vlbase", inst.getVlbase());
        doc.append("vladjusted", inst.getVladjusted());
        doc.append("cdstatus", inst.getCdstatus());
        doc.append("dtpayment", inst.getDtpayment() != null ? inst.getDtpayment().toString() : null);
        doc.append("vlpenalty", inst.getVlpenalty());
        doc.append("vlinterest", inst.getVlinterest());
        doc.append("dtlastadjustment", inst.getDtlastadjustment() != null ? inst.getDtlastadjustment().toString() : null);
        return doc;
    }

    /**
     * Converte um Document BSON embarcado para objeto Installments.
     */
    public static Installments installmentFromDocument(Document doc, int cdcontract) {
        if (doc == null) return null;
        Installments inst = new Installments();
        inst.setCdinstallment(doc.getInteger("cdinstallment", 0));
        inst.setNrinstallment(doc.getInteger("nrinstallment", 0));
        String dtdue = doc.getString("dtdue");
        if (dtdue != null && !dtdue.isEmpty()) {
            inst.setDtdue(LocalDate.parse(dtdue));
        }
        inst.setVlbase(getDoubleValue(doc, "vlbase"));
        inst.setVladjusted(getDoubleValue(doc, "vladjusted"));
        inst.setCdstatus(doc.getInteger("cdstatus", 0));
        String dtpayment = doc.getString("dtpayment");
        if (dtpayment != null && !dtpayment.isEmpty()) {
            inst.setDtpayment(LocalDate.parse(dtpayment));
        }
        inst.setVlpenalty(getDoubleValue(doc, "vlpenalty"));
        inst.setVlinterest(getDoubleValue(doc, "vlinterest"));
        String dtlastadjustment = doc.getString("dtlastadjustment");
        if (dtlastadjustment != null && !dtlastadjustment.isEmpty()) {
            inst.setDtlastadjustment(LocalDate.parse(dtlastadjustment));
        }
        inst.setFk_Contracts_cdcontract(cdcontract);
        return inst;
    }

    /**
     * Extrai valor double de um Document de forma segura, tratando Integer, Double e Long.
     */
    private static double getDoubleValue(Document doc, String field) {
        Object value = doc.get(field);
        if (value == null) return 0.0;
        if (value instanceof Double) return (Double) value;
        if (value instanceof Integer) return ((Integer) value).doubleValue();
        if (value instanceof Long) return ((Long) value).doubleValue();
        return 0.0;
    }

    // ========== Operações CRUD ==========

    /**
     * Realiza o registro completo de um contrato, incluindo participantes, parcelas e comissões.
     * Atualiza o status do imóvel vinculado em uma única transação multi-documento.
     *
     * @param contract Objeto contendo os dados do contrato.
     * @param participants Lista de participantes e seus papéis.
     * @param installments Lista de parcelas financeiras.
     * @param novoStatus Novo código de status para o imóvel.
     */
    public void processFullContract(Contracts contract, List<User_Contract> participants, List<Installments> installments, int novoStatus) {
        try (ClientSession session = Conexao.getClient().startSession()) {
            session.startTransaction();
            try {
                int generatedContractId = SequenceGenerator.getNextSequence(COLLECTION_NAME);
                contract.setCdcontract(generatedContractId);

                // Gerar IDs para as parcelas
                if (installments != null) {
                    for (Installments inst : installments) {
                        if (inst.getCdinstallment() <= 0) {
                            inst.setCdinstallment(SequenceGenerator.getNextSequence("installments"));
                        }
                    }
                }

                Document doc = toDocument(contract, participants, installments);
                getCollection().insertOne(session, doc);

                // Atualizar status do imóvel
                if (novoStatus > 0 && contract.getCdproperty() > 0) {
                    MongoCollection<Document> properties = Conexao.getCollection(PROPERTIES_COLLECTION);
                    properties.updateOne(session,
                        Filters.eq("_id", contract.getCdproperty()),
                        Updates.set("status", resolvePropertyStatusName(novoStatus))
                    );
                }

                session.commitTransaction();
                System.out.println("Sucesso: Contrato #" + generatedContractId + " registrado e imóvel atualizado!");

            } catch (Exception e) {
                session.abortTransaction();
                System.err.println("Erro no Processo de Contrato (Rollback aplicado): " + e.getMessage());
            }
        }
    }

    /**
     * Insere um contrato no banco de dados.
     *
     * @param contract Objeto contendo os dados do contrato.
     * @return O ID gerado para o contrato ou -1 em caso de erro.
     */
    public int insertContract(Contracts contract) {
        try {
            int newId = SequenceGenerator.getNextSequence(COLLECTION_NAME);
            contract.setCdcontract(newId);

            Document doc = toDocument(contract, null, null);
            getCollection().insertOne(doc);
            return newId;
        } catch (Exception e) {
            System.err.println("Erro ao inserir contrato parcial: " + e.getMessage());
        }
        return -1;
    }

    /**
     * Busca um contrato pelo seu identificador.
     *
     * @param contractId Identificador do contrato.
     * @return Objeto Contracts ou null.
     */
    public Contracts findById(int contractId) {
        try {
            Document doc = getCollection().find(Filters.eq("_id", contractId)).first();
            return fromDocument(doc);
        } catch (Exception e) {
            System.err.println("Erro ao buscar contrato por ID: " + e.getMessage());
        }
        return null;
    }

    /**
     * Lista todos os contratos cadastrados no sistema (não-finalizados para queries de serviço).
     *
     * @return Lista de objetos Contracts.
     */
    public List<Contracts> findAllActive() {
        List<Contracts> list = new ArrayList<>();
        try {
            Bson filter = Filters.ne("cdstatus", ContractStatus.FINALIZADO.getCode());
            try (MongoCursor<Document> cursor = getCollection()
                    .find(filter)
                    .sort(new Document("_id", 1))
                    .iterator()) {
                while (cursor.hasNext()) {
                    list.add(fromDocument(cursor.next()));
                }
            }
        } catch (Exception e) {
            System.err.println("Erro ao buscar contratos ativos: " + e.getMessage());
        }
        return list;
    }

    /**
     * Busca todos os contratos que possuem índice de reajuste.
     *
     * @return Lista de objetos Contracts.
     */
    public List<Contracts> findAllWithAdjustmentIndex() {
        List<Contracts> contracts = new ArrayList<>();
        try {
            Bson filter = Filters.and(
                Filters.ne("cdindex", null),
                Filters.exists("cdindex", true)
            );
            try (MongoCursor<Document> cursor = getCollection()
                    .find(filter)
                    .sort(new Document("_id", 1))
                    .iterator()) {
                while (cursor.hasNext()) {
                    contracts.add(fromDocument(cursor.next()));
                }
            }
        } catch (Exception e) {
            System.err.println("Erro ao buscar contratos com índice de reajuste: " + e.getMessage());
        }
        return contracts;
    }

    /**
     * Busca contratos que expiram em um determinado mês e ano.
     * Filtra pela dtlimit no intervalo [primeiro dia do mês, primeiro dia do mês seguinte).
     *
     * @param month Mês de expiração.
     * @param year Ano de expiração.
     * @return Lista de contratos expirando.
     */
    public List<Contracts> findExpiringContracts(int month, int year) {
        List<Contracts> contracts = new ArrayList<>();
        try {
            LocalDate inicio = LocalDate.of(year, month, 1);
            LocalDate fim = inicio.plusMonths(1);
            Bson filter = Filters.and(
                Filters.ne("cdstatus", ContractStatus.FINALIZADO.getCode()),
                Filters.ne("dtlimit", null),
                Filters.gte("dtlimit", inicio.toString()),
                Filters.lt("dtlimit", fim.toString())
            );
            try (MongoCursor<Document> cursor = getCollection()
                    .find(filter)
                    .sort(new Document("_id", 1))
                    .iterator()) {
                while (cursor.hasNext()) {
                    contracts.add(fromDocument(cursor.next()));
                }
            }
        } catch (Exception e) {
            System.err.println("Erro ao buscar contratos expirando: " + e.getMessage());
        }
        return contracts;
    }

    // ========== Listagens formatadas ==========

    /**
     * Lista todos os contratos ativos (modo geral).
     *
     * @return Lista de Strings com ID e Título dos contratos.
     */
    public List<String> getActiveContractsList() {
        return getActiveContractsList("geral");
    }

    /**
     * Lista contratos ativos filtrados por tipo (venda/locação/geral).
     *
     * @param tipo Tipo de filtro desejado.
     * @return Lista de Strings com ID e Título dos contratos.
     */
    public List<String> getActiveContractsList(String tipo) {
        List<String> list = new ArrayList<>();
        boolean isGeral = tipo == null || "geral".equalsIgnoreCase(tipo);

        try {
            if (isGeral) {
                // Lista todos os contratos
                try (MongoCursor<Document> cursor = getCollection()
                        .find()
                        .sort(new Document("_id", 1))
                        .iterator()) {
                    while (cursor.hasNext()) {
                        Document doc = cursor.next();
                        list.add("ID: " + doc.getInteger("_id") + " - " + doc.getString("dstitle"));
                    }
                }
            } else {
                // Filtrar por status do imóvel referenciado
                // Primeiro obter os IDs dos imóveis com status correspondente
                MongoCollection<Document> properties = Conexao.getCollection(PROPERTIES_COLLECTION);
                Bson statusFilter;
                if ("venda".equalsIgnoreCase(tipo)) {
                    statusFilter = Filters.regex("status", java.util.regex.Pattern.compile("vend", java.util.regex.Pattern.CASE_INSENSITIVE));
                } else {
                    statusFilter = Filters.or(
                        Filters.regex("status", java.util.regex.Pattern.compile("alugad", java.util.regex.Pattern.CASE_INSENSITIVE)),
                        Filters.regex("status", java.util.regex.Pattern.compile("loca", java.util.regex.Pattern.CASE_INSENSITIVE))
                    );
                }

                List<Integer> propertyIds = new ArrayList<>();
                try (MongoCursor<Document> pCursor = properties.find(statusFilter).iterator()) {
                    while (pCursor.hasNext()) {
                        propertyIds.add(pCursor.next().getInteger("_id"));
                    }
                }

                if (!propertyIds.isEmpty()) {
                    Bson contractFilter = Filters.in("cdproperty", propertyIds);
                    try (MongoCursor<Document> cursor = getCollection()
                            .find(contractFilter)
                            .sort(new Document("_id", 1))
                            .iterator()) {
                        while (cursor.hasNext()) {
                            Document doc = cursor.next();
                            list.add("ID: " + doc.getInteger("_id") + " - " + doc.getString("dstitle"));
                        }
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("Erro ao listar contratos: " + e.getMessage());
        }
        return list;
    }

    /**
     * Lista apenas os contratos que possuem pelo menos um participante vinculado.
     * Filtra contratos cujo array "participants" não está vazio.
     *
     * @return Lista formatada "ID: X - Título".
     */
    public List<String> getContractsWithParticipantsList() {
        List<String> list = new ArrayList<>();
        try {
            Bson filter = Filters.and(
                Filters.exists("participants"),
                Filters.not(Filters.size("participants", 0))
            );
            try (MongoCursor<Document> cursor = getCollection()
                    .find(filter)
                    .sort(new Document("_id", 1))
                    .iterator()) {
                while (cursor.hasNext()) {
                    Document doc = cursor.next();
                    list.add("ID: " + doc.getInteger("_id") + " - " + doc.getString("dstitle"));
                }
            }
        } catch (Exception e) {
            System.err.println("Erro ao listar contratos com partes: " + e.getMessage());
        }
        return list;
    }

    // ========== Extrato Financeiro ==========

    /**
     * Exibe o extrato financeiro do contrato no console.
     * As parcelas estão embarcadas no documento do contrato.
     *
     * @param idContract Identificador do contrato.
     * @return true se o contrato foi encontrado e o extrato gerado.
     */
    public boolean gerarExtrato(int idContract) {
        try {
            Document doc = getCollection().find(Filters.eq("_id", idContract)).first();
            if (doc == null) {
                System.out.println("\nERRO: Nenhum contrato encontrado com o ID " + idContract);
                return false;
            }

            String dstitle = doc.getString("dstitle");
            Integer cdindex = doc.getInteger("cdindex");
            String nmindex = "N/A";
            if (cdindex != null && cdindex > 0) {
                Document indexDoc = Conexao.getCollection(INDEXES_COLLECTION).find(Filters.eq("_id", cdindex)).first();
                if (indexDoc != null) {
                    nmindex = indexDoc.getString("nmindex");
                }
            }

            List<Document> installments = doc.getList("installments", Document.class);
            if (installments == null || installments.isEmpty()) {
                System.out.println("\n--- EXTRATO FINANCEIRO DO CONTRATO ---");
                System.out.println("Contrato: " + dstitle);
                System.out.println("Nenhuma parcela encontrada.");
                return true;
            }

            System.out.println("\n--- EXTRATO FINANCEIRO DO CONTRATO ---");
            System.out.println("Contrato: " + dstitle);
            for (Document instDoc : installments) {
                System.out.printf("Parcela #%d | Vencimento: %s | Valor: R$ %.2f | Reajuste: %s\n",
                        instDoc.getInteger("nrinstallment", 0),
                        instDoc.getString("dtdue"),
                        instDoc.getDouble("vlbase") != null ? instDoc.getDouble("vlbase") : 0.0,
                        nmindex);
            }
            return true;

        } catch (Exception e) {
            System.err.println("Erro ao gerar relatório: " + e.getMessage());
            return false;
        }
    }

    // ========== Atualização e Exclusão ==========

    /**
     * Atualiza os dados de um contrato.
     *
     * @param c Objeto contendo os dados atualizados.
     */
    public void updateContract(Contracts c) {
        try {
            List<Bson> updates = new ArrayList<>();
            updates.add(Updates.set("dstitle", c.getDstitle()));
            updates.add(Updates.set("cdtemplate", c.getCdtemplate()));
            updates.add(Updates.set("cdindex", c.getCdindex() > 0 ? c.getCdindex() : null));
            updates.add(Updates.set("dtcreation", c.getDtcreation() != null ? c.getDtcreation().toString() : null));
            updates.add(Updates.set("dtlimit", c.getDtlimit() != null ? c.getDtlimit().toString() : null));
            updates.add(Updates.set("cdproperty", c.getCdproperty() > 0 ? c.getCdproperty() : 0));
            updates.add(Updates.set("cdstatus", c.getCdstatus() > 0 ? c.getCdstatus() : ContractStatus.ATIVO.getCode()));
            updates.add(Updates.set("notary", c.getCdnotary() > 0 ? new Document("cdnotary", c.getCdnotary()) : null));

            getCollection().updateOne(
                Filters.eq("_id", c.getCdcontract()),
                Updates.combine(updates)
            );
            System.out.println("Contrato atualizado com sucesso!");
        } catch (Exception e) {
            System.err.println("Erro ao atualizar contrato: " + e.getMessage());
        }
    }

    /**
     * Exclui um contrato e as notificações associadas em uma única transação.
     *
     * @param id Identificador do contrato.
     * @return true se a exclusão foi bem-sucedida.
     */
    public boolean deleteContract(int id) {
        try (ClientSession session = Conexao.getClient().startSession()) {
            session.startTransaction();
            try {
                // Remover notificações vinculadas ao contrato
                MongoCollection<Document> notifications = Conexao.getCollection(NOTIFICATIONS_COLLECTION);
                notifications.deleteMany(session, Filters.eq("cdcontract", id));

                // Remover o contrato
                long deletedCount = getCollection().deleteOne(session, Filters.eq("_id", id)).getDeletedCount();

                session.commitTransaction();
                return deletedCount > 0;
            } catch (Exception e) {
                session.abortTransaction();
                System.err.println("Erro ao excluir contrato (Rollback aplicado): " + e.getMessage());
                return false;
            }
        }
    }

    /**
     * Verifica se um contrato existe.
     *
     * @param idContract Identificador do contrato.
     * @return true se o contrato existe.
     */
    public boolean contractExists(int idContract) {
        try {
            Document doc = getCollection().find(Filters.eq("_id", idContract)).first();
            return doc != null;
        } catch (Exception e) {
            System.err.println("Erro ao verificar existência do contrato: " + e.getMessage());
        }
        return false;
    }

    /**
     * Busca todos os participantes embarcados de um contrato.
     *
     * @param contractId Identificador do contrato.
     * @return Lista de User_Contract.
     */
    public List<User_Contract> getParticipants(int contractId) {
        List<User_Contract> participants = new ArrayList<>();
        try {
            Document doc = getCollection().find(Filters.eq("_id", contractId)).first();
            if (doc != null) {
                List<Document> participantDocs = doc.getList("participants", Document.class);
                if (participantDocs != null) {
                    for (Document pDoc : participantDocs) {
                        User_Contract uc = new User_Contract();
                        uc.setCduser(pDoc.getInteger("cduser", 0));
                        uc.setCdrole(pDoc.getInteger("cdrole", 0));
                        uc.setCdcontract(contractId);
                        participants.add(uc);
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("Erro ao buscar participantes: " + e.getMessage());
        }
        return participants;
    }

    /**
     * Adiciona um participante ao array embarcado de um contrato.
     *
     * @param contractId Identificador do contrato.
     * @param participant Participante a adicionar.
     */
    public void addParticipant(int contractId, User_Contract participant) {
        try {
            Document pDoc = new Document();
            pDoc.append("cduser", participant.getCduser());
            pDoc.append("cdrole", participant.getCdrole());
            pDoc.append("nmrole", resolveRoleName(participant.getCdrole()));

            getCollection().updateOne(
                    Filters.eq("_id", contractId),
                    Updates.push("participants", pDoc));
            System.out.println("Participante adicionado ao contrato!");
        } catch (Exception e) {
            System.err.println("Erro ao adicionar participante: " + e.getMessage());
        }
    }

    /**
     * Remove um participante do array embarcado de um contrato.
     *
     * @param contractId Identificador do contrato.
     * @param userId ID do usuário a remover.
     */
    public void removeParticipant(int contractId, int userId) {
        try {
            getCollection().updateOne(
                    Filters.eq("_id", contractId),
                    Updates.pull("participants", new Document("cduser", userId)));
            System.out.println("Participante removido do contrato!");
        } catch (Exception e) {
            System.err.println("Erro ao remover participante: " + e.getMessage());
        }
    }

    /**
     * Busca todas as parcelas embarcadas de um contrato.
     *
     * @param contractId Identificador do contrato.
     * @return Lista de Installments ordenada por nrinstallment.
     */
    public List<Installments> getInstallments(int contractId) {
        List<Installments> installments = new ArrayList<>();
        try {
            Document doc = getCollection().find(Filters.eq("_id", contractId)).first();
            if (doc != null) {
                List<Document> instDocs = doc.getList("installments", Document.class);
                if (instDocs != null) {
                    for (Document instDoc : instDocs) {
                        Installments inst = installmentFromDocument(instDoc, contractId);
                        if (inst != null) {
                            installments.add(inst);
                        }
                    }
                    installments.sort((a, b) -> Integer.compare(a.getNrinstallment(), b.getNrinstallment()));
                }
            }
        } catch (Exception e) {
            System.err.println("Erro ao buscar parcelas: " + e.getMessage());
        }
        return installments;
    }

    /**
     * Adiciona múltiplas parcelas ao array embarcado de um contrato.
     *
     * @param contractId Identificador do contrato.
     * @param installments Lista de parcelas a adicionar.
     */
    public void addInstallments(int contractId, List<Installments> installments) {
        try {
            List<Document> instDocs = new ArrayList<>();
            for (Installments inst : installments) {
                if (inst.getCdinstallment() <= 0) {
                    inst.setCdinstallment(SequenceGenerator.getNextSequence("installments"));
                }
                inst.setFk_Contracts_cdcontract(contractId);
                instDocs.add(installmentToDocument(inst));
            }

            getCollection().updateOne(
                    Filters.eq("_id", contractId),
                    Updates.pushEach("installments", instDocs));
            System.out.println("Parcelas adicionadas ao contrato!");
        } catch (Exception e) {
            System.err.println("Erro ao adicionar parcelas: " + e.getMessage());
        }
    }

    /**
     * Busca todos os contratos vinculados a um imóvel.
     *
     * @param propertyId Identificador do imóvel.
     * @return Lista de contratos.
     */
    public List<Contracts> findContractsByProperty(int propertyId) {
        List<Contracts> list = new ArrayList<>();
        try (MongoCursor<Document> cursor = getCollection()
                .find(Filters.eq("cdproperty", propertyId))
                .sort(new Document("_id", 1))
                .iterator()) {
            while (cursor.hasNext()) {
                list.add(fromDocument(cursor.next()));
            }
        } catch (Exception e) {
            System.err.println("Erro ao buscar contratos por imóvel: " + e.getMessage());
        }
        return list;
    }

    /**
     * Busca todos os contratos em que um usuário é participante.
     *
     * @param userId Identificador do usuário.
     * @return Lista de contratos.
     */
    public List<Contracts> findContractsByUser(int userId) {
        List<Contracts> list = new ArrayList<>();
        try (MongoCursor<Document> cursor = getCollection()
                .find(Filters.eq("participants.cduser", userId))
                .sort(new Document("_id", 1))
                .iterator()) {
            while (cursor.hasNext()) {
                list.add(fromDocument(cursor.next()));
            }
        } catch (Exception e) {
            System.err.println("Erro ao buscar contratos por usuário: " + e.getMessage());
        }
        return list;
    }

    /**
     * Resolve o nome do papel (role) a partir do código.
     * Baseado nos dados originais do insert.sql.
     */
    private static String resolveRoleName(int cdrole) {
        switch (cdrole) {
            case 1: return "Locatário";
            case 2: return "Locador";
            case 3: return "Testemunha";
            case 4: return "Representante Legal";
            default: return "Outro";
        }
    }

    /**
     * Resolve o nome do status do imóvel a partir do código legado.
     */
    private static String resolvePropertyStatusName(int cdstatus) {
        switch (cdstatus) {
            case 1: return "Alugado";
            case 2: return "Disponível";
            case 3: return "Vendido";
            default: return "Disponível";
        }
    }
}
