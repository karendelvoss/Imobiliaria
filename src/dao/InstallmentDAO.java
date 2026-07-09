package dao;

import model.Installments;
import model.InstallmentStatus;
import dto.CashFlowReportDTO;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Updates;
import com.mongodb.client.model.Aggregates;
import com.mongodb.client.model.Accumulators;
import com.mongodb.client.model.Sorts;
import org.bson.Document;
import org.bson.conversions.Bson;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Gerencia as operações de persistência para as parcelas (Installments).
 *
 * No modelo MongoDB, parcelas são documentos embarcados dentro do array
 * {@code installments} na coleção {@code contracts}. Portanto, todas as
 * operações desta classe operam sobre a coleção de contratos usando
 * operadores de array, positional operators e aggregation pipelines.
 */
public class InstallmentDAO {

    private static final String COLLECTION_NAME = "contracts";

    /**
     * Obtém a coleção MongoDB de contratos (onde as parcelas estão embarcadas).
     */
    private MongoCollection<Document> getCollection() {
        return Conexao.getCollection(COLLECTION_NAME);
    }

    // ========== Busca ==========

    /**
     * Busca uma parcela específica pelo seu identificador.
     * Usa aggregation pipeline com $unwind para localizar a parcela dentro de qualquer contrato.
     *
     * @param installmentId Identificador da parcela.
     * @return Objeto Installments ou null.
     */
    public Installments findById(int installmentId) {
        try {
            List<Bson> pipeline = Arrays.asList(
                Aggregates.unwind("$installments"),
                Aggregates.match(Filters.eq("installments.cdinstallment", installmentId)),
                Aggregates.limit(1)
            );

            try (MongoCursor<Document> cursor = getCollection().aggregate(pipeline).iterator()) {
                if (cursor.hasNext()) {
                    Document result = cursor.next();
                    int cdcontract = result.getInteger("_id");
                    Document instDoc = result.get("installments", Document.class);
                    return ContractDAO.installmentFromDocument(instDoc, cdcontract);
                }
            }
        } catch (Exception e) {
            System.err.println("Erro ao buscar parcela por ID: " + e.getMessage());
        }
        return null;
    }

    /**
     * Busca todas as parcelas associadas a um contrato.
     * Extrai o array {@code installments} do documento do contrato.
     *
     * @param contractId Identificador do contrato.
     * @return Lista de objetos Installments.
     */
    public List<Installments> findByContractId(int contractId) {
        List<Installments> installments = new ArrayList<>();
        try {
            Document doc = getCollection().find(Filters.eq("_id", contractId)).first();
            if (doc != null) {
                List<Document> instDocs = doc.getList("installments", Document.class);
                if (instDocs != null) {
                    for (Document instDoc : instDocs) {
                        installments.add(ContractDAO.installmentFromDocument(instDoc, contractId));
                    }
                }
                // Ordenar por nrinstallment
                installments.sort((a, b) -> Integer.compare(a.getNrinstallment(), b.getNrinstallment()));
            }
        } catch (Exception e) {
            System.err.println("Erro ao buscar parcelas por contrato: " + e.getMessage());
        }
        return installments;
    }

    // ========== Atualização ==========

    /**
     * Atualiza os dados de uma parcela existente.
     * Usa o positional operator ($) com elemMatch para atualizar a parcela específica
     * dentro do array embarcado no contrato.
     *
     * @param installment Objeto contendo os dados atualizados.
     */
    public void update(Installments installment) {
        try {
            Bson filter = Filters.and(
                Filters.eq("_id", installment.getFk_Contracts_cdcontract()),
                Filters.elemMatch("installments",
                    Filters.eq("cdinstallment", installment.getCdinstallment()))
            );

            Bson update = Updates.combine(
                Updates.set("installments.$.dtdue", installment.getDtdue() != null ? installment.getDtdue().toString() : null),
                Updates.set("installments.$.vlbase", installment.getVlbase()),
                Updates.set("installments.$.vladjusted", installment.getVladjusted()),
                Updates.set("installments.$.cdstatus", installment.getCdstatus()),
                Updates.set("installments.$.dtpayment", installment.getDtpayment() != null ? installment.getDtpayment().toString() : null),
                Updates.set("installments.$.vlpenalty", installment.getVlpenalty()),
                Updates.set("installments.$.vlinterest", installment.getVlinterest()),
                Updates.set("installments.$.dtlastadjustment", installment.getDtlastadjustment() != null ? installment.getDtlastadjustment().toString() : null)
            );

            getCollection().updateOne(filter, update);
        } catch (Exception e) {
            System.err.println("Erro ao atualizar parcela: " + e.getMessage());
        }
    }

    // ========== Inserção em Lote ==========

    /**
     * Insere várias parcelas em lote dentro de um contrato existente.
     * Usa $push com $each para adicionar múltiplas parcelas ao array embarcado.
     *
     * @param contractId Identificador do contrato.
     * @param installments Lista de parcelas a serem inseridas.
     */
    public void insertBatch(int contractId, List<Installments> installments) {
        try {
            List<Document> instDocs = new ArrayList<>();
            for (Installments inst : installments) {
                if (inst.getCdinstallment() <= 0) {
                    inst.setCdinstallment(SequenceGenerator.getNextSequence("installments"));
                }
                inst.setFk_Contracts_cdcontract(contractId);
                instDocs.add(ContractDAO.installmentToDocument(inst));
            }

            Bson filter = Filters.eq("_id", contractId);
            Bson update = Updates.pushEach("installments", instDocs);

            getCollection().updateOne(filter, update);
        } catch (Exception e) {
            System.err.println("Erro ao inserir parcelas: " + e.getMessage());
        }
    }

    // ========== Consultas de Parcelas Pendentes ==========

    /**
     * Busca a última parcela com status pendente de um contrato.
     * Filtra pelo contrato, extrai as parcelas pendentes e retorna a de maior nrinstallment.
     *
     * @param contractId Identificador do contrato.
     * @return Objeto Installments ou null.
     */
    public Installments findLastPendingInstallmentByContractId(int contractId) {
        try {
            Document doc = getCollection().find(Filters.eq("_id", contractId)).first();
            if (doc != null) {
                List<Document> instDocs = doc.getList("installments", Document.class);
                if (instDocs != null) {
                    Installments last = null;
                    for (Document instDoc : instDocs) {
                        if (instDoc.getInteger("cdstatus", 0) == InstallmentStatus.PENDENTE.getCode()) {
                            Installments inst = ContractDAO.installmentFromDocument(instDoc, contractId);
                            if (last == null || inst.getNrinstallment() > last.getNrinstallment()) {
                                last = inst;
                            }
                        }
                    }
                    return last;
                }
            }
        } catch (Exception e) {
            System.err.println("Erro ao buscar última parcela pendente: " + e.getMessage());
        }
        return null;
    }

    /**
     * Busca todas as parcelas pendentes de um contrato.
     *
     * @param contractId Identificador do contrato.
     * @return Lista de parcelas pendentes ordenadas por nrinstallment.
     */
    public List<Installments> findPendingInstallmentsByContractId(int contractId) {
        List<Installments> result = new ArrayList<>();
        try {
            Document doc = getCollection().find(Filters.eq("_id", contractId)).first();
            if (doc != null) {
                List<Document> instDocs = doc.getList("installments", Document.class);
                if (instDocs != null) {
                    for (Document instDoc : instDocs) {
                        if (instDoc.getInteger("cdstatus", 0) == InstallmentStatus.PENDENTE.getCode()) {
                            result.add(ContractDAO.installmentFromDocument(instDoc, contractId));
                        }
                    }
                    result.sort((a, b) -> Integer.compare(a.getNrinstallment(), b.getNrinstallment()));
                }
            }
        } catch (Exception e) {
            System.err.println("Erro ao buscar parcelas pendentes: " + e.getMessage());
        }
        return result;
    }

    // ========== Consultas com Aggregation Pipeline ==========

    /**
     * Busca parcelas por data de vencimento e status.
     * Usa aggregation pipeline com $unwind para buscar parcelas em todos os contratos.
     *
     * @param dtdue Data de vencimento.
     * @param cdstatus Código do status.
     * @return Lista de parcelas encontradas.
     */
    public List<Installments> findByDueDateAndStatus(LocalDate dtdue, int cdstatus) {
        List<Installments> result = new ArrayList<>();
        try {
            List<Bson> pipeline = Arrays.asList(
                Aggregates.unwind("$installments"),
                Aggregates.match(Filters.and(
                    Filters.eq("installments.dtdue", dtdue.toString()),
                    Filters.eq("installments.cdstatus", cdstatus)
                ))
            );

            try (MongoCursor<Document> cursor = getCollection().aggregate(pipeline).iterator()) {
                while (cursor.hasNext()) {
                    Document doc = cursor.next();
                    int cdcontract = doc.getInteger("_id");
                    Document instDoc = doc.get("installments", Document.class);
                    result.add(ContractDAO.installmentFromDocument(instDoc, cdcontract));
                }
            }
        } catch (Exception e) {
            System.err.println("Erro ao buscar parcelas por vencimento: " + e.getMessage());
        }
        return result;
    }

    /**
     * Busca parcelas que foram pagas em uma determinada data.
     * Usa aggregation pipeline com $unwind para buscar em todos os contratos.
     *
     * @param dtpayment Data de pagamento.
     * @return Lista de parcelas pagas nesta data.
     */
    public List<Installments> findByPaymentDate(LocalDate dtpayment) {
        List<Installments> result = new ArrayList<>();
        try {
            List<Bson> pipeline = Arrays.asList(
                Aggregates.unwind("$installments"),
                Aggregates.match(Filters.and(
                    Filters.eq("installments.dtpayment", dtpayment.toString()),
                    Filters.eq("installments.cdstatus", InstallmentStatus.PAGO.getCode())
                ))
            );

            try (MongoCursor<Document> cursor = getCollection().aggregate(pipeline).iterator()) {
                while (cursor.hasNext()) {
                    Document doc = cursor.next();
                    int cdcontract = doc.getInteger("_id");
                    Document instDoc = doc.get("installments", Document.class);
                    result.add(ContractDAO.installmentFromDocument(instDoc, cdcontract));
                }
            }
        } catch (Exception e) {
            System.err.println("Erro ao buscar parcelas pagas por data: " + e.getMessage());
        }
        return result;
    }

    /**
     * Busca parcelas por mês e ano de vencimento.
     * Usa aggregation pipeline com $unwind para buscar em todos os contratos.
     *
     * @param mes Mês (1-12).
     * @param ano Ano.
     * @return Lista de parcelas com vencimento no mês/ano especificados.
     */
    public List<Installments> findByMonth(int mes, int ano) {
        List<Installments> list = new ArrayList<>();
        try {
            String monthPrefix = String.format("%d-%02d", ano, mes);
            List<Bson> pipeline = Arrays.asList(
                Aggregates.unwind("$installments"),
                Aggregates.match(Filters.regex("installments.dtdue", "^" + monthPrefix))
            );

            try (MongoCursor<Document> cursor = getCollection().aggregate(pipeline).iterator()) {
                while (cursor.hasNext()) {
                    Document doc = cursor.next();
                    int cdcontract = doc.getInteger("_id");
                    Document instDoc = doc.get("installments", Document.class);
                    Installments inst = ContractDAO.installmentFromDocument(instDoc, cdcontract);
                    if (inst != null) {
                        list.add(inst);
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("Erro ao buscar parcelas por mês: " + e.getMessage());
        }
        return list;
    }

    // ========== Relatório de Fluxo de Caixa ==========

    /**
     * Gera o relatório de fluxo de caixa mensal para um ano e contrato específicos.
     * Usa aggregation pipeline: $match contratos ativos → $unwind installments →
     * $match por ano → $group por mês com somas condicionais.
     *
     * @param year Ano de referência.
     * @param contractId Identificador do contrato (0 para todos).
     * @return Lista de DTOs do relatório.
     */
    public List<CashFlowReportDTO> getMonthlyCashFlowReport(int year, int contractId) {
        List<CashFlowReportDTO> report = new ArrayList<>();
        try {
            String yearStr = String.valueOf(year);

            // Construir filtro inicial (por contrato ou todos os ativos)
            List<Bson> pipeline = new ArrayList<>();
            if (contractId > 0) {
                pipeline.add(Aggregates.match(Filters.eq("_id", contractId)));
            }

            // Unwind parcelas
            pipeline.add(Aggregates.unwind("$installments"));

            // Filtrar parcelas do ano desejado (dtdue começa com o ano)
            pipeline.add(Aggregates.match(
                Filters.regex("installments.dtdue", "^" + yearStr)
            ));

            // Projetar campos necessários para o agrupamento
            pipeline.add(Aggregates.project(new Document()
                .append("installments", 1)
                .append("month", new Document("$substr", Arrays.asList("$installments.dtdue", 5, 2)))
            ));

            // Agrupar por mês
            pipeline.add(Aggregates.group("$month",
                Accumulators.sum("recebido",
                    new Document("$cond", Arrays.asList(
                        new Document("$eq", Arrays.asList("$installments.cdstatus", InstallmentStatus.PAGO.getCode())),
                        new Document("$cond", Arrays.asList(
                            new Document("$gt", Arrays.asList("$installments.vladjusted", 0)),
                            "$installments.vladjusted",
                            "$installments.vlbase"
                        )),
                        0
                    ))
                ),
                Accumulators.sum("pendente",
                    new Document("$cond", Arrays.asList(
                        new Document("$eq", Arrays.asList("$installments.cdstatus", InstallmentStatus.PENDENTE.getCode())),
                        new Document("$cond", Arrays.asList(
                            new Document("$gt", Arrays.asList("$installments.vladjusted", 0)),
                            "$installments.vladjusted",
                            "$installments.vlbase"
                        )),
                        0
                    ))
                ),
                Accumulators.sum("emAtraso",
                    new Document("$cond", Arrays.asList(
                        new Document("$and", Arrays.asList(
                            new Document("$eq", Arrays.asList("$installments.cdstatus", InstallmentStatus.PENDENTE.getCode())),
                            new Document("$lt", Arrays.asList("$installments.dtdue", LocalDate.now().toString()))
                        )),
                        new Document("$cond", Arrays.asList(
                            new Document("$gt", Arrays.asList("$installments.vladjusted", 0)),
                            "$installments.vladjusted",
                            "$installments.vlbase"
                        )),
                        0
                    ))
                )
            ));

            // Ordenar por mês
            pipeline.add(Aggregates.sort(Sorts.ascending("_id")));

            try (MongoCursor<Document> cursor = getCollection().aggregate(pipeline).iterator()) {
                while (cursor.hasNext()) {
                    Document doc = cursor.next();
                    CashFlowReportDTO dto = new CashFlowReportDTO();
                    String monthStr = doc.getString("_id");
                    dto.setMes(Integer.parseInt(monthStr));
                    dto.setAno(year);
                    dto.setValorRecebido(getDoubleValue(doc, "recebido"));
                    dto.setValorPendente(getDoubleValue(doc, "pendente"));
                    dto.setValorEmAtraso(getDoubleValue(doc, "emAtraso"));
                    report.add(dto);
                }
            }
        } catch (Exception e) {
            System.err.println("Erro ao gerar relatório de fluxo de caixa: " + e.getMessage());
        }
        return report;
    }

    // ========== Métodos Auxiliares ==========

    /**
     * Extrai valor double de um Document de forma segura, tratando Integer e Double.
     */
    private double getDoubleValue(Document doc, String field) {
        Object value = doc.get(field);
        if (value instanceof Double) {
            return (Double) value;
        } else if (value instanceof Integer) {
            return ((Integer) value).doubleValue();
        } else if (value instanceof Long) {
            return ((Long) value).doubleValue();
        }
        return 0.0;
    }
}
