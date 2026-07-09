package dao;

import model.Index_Rates;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Updates;
import com.mongodb.client.result.UpdateResult;
import org.bson.Document;
import org.bson.conversions.Bson;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Gerencia as operações de persistência para as taxas dos índices.
 * Taxas são embarcadas como array "rates" dentro do documento do índice na coleção "indexes".
 *
 * Estrutura do documento na coleção indexes:
 * {
 *   "_id": 1,
 *   "nmindex": "IPCA",
 *   "rates": [
 *     { "cdrate": 1, "refmonth": 4, "refyear": 2024, "vlrate": 0.0038 },
 *     { "cdrate": 2, "refmonth": 5, "refyear": 2024, "vlrate": 0.0046 }
 *   ]
 * }
 */
public class IndexRateDAO {

    private static final String COLLECTION_NAME = "indexes";
    private static final String RATES_SEQUENCE = "index_rates";

    /**
     * Obtém a coleção MongoDB de índices (onde as taxas estão embarcadas).
     */
    private MongoCollection<Document> getCollection() {
        return Conexao.getCollection(COLLECTION_NAME);
    }

    /**
     * Converte um objeto Index_Rates para Document BSON (subdocumento do array rates).
     */
    private Document toDocument(Index_Rates rate) {
        Document doc = new Document();
        doc.append("cdrate", rate.getCdrate());
        doc.append("refmonth", rate.getRefmonth());
        doc.append("refyear", rate.getRefyear());
        doc.append("vlrate", rate.getVlrate());
        return doc;
    }

    /**
     * Converte um Document BSON (subdocumento) para objeto Index_Rates.
     *
     * @param doc Subdocumento BSON do array rates.
     * @param cdindex ID do índice pai.
     */
    private Index_Rates fromDocument(Document doc, int cdindex) {
        Index_Rates rate = new Index_Rates();
        rate.setCdrate(doc.getInteger("cdrate") != null ? doc.getInteger("cdrate") : 0);
        rate.setRefmonth(doc.getInteger("refmonth", 0));
        rate.setRefyear(doc.getInteger("refyear", 0));
        Object vlrateObj = doc.get("vlrate");
        if (vlrateObj instanceof Double) {
            rate.setVlrate((Double) vlrateObj);
        } else if (vlrateObj instanceof Integer) {
            rate.setVlrate(((Integer) vlrateObj).doubleValue());
        } else {
            rate.setVlrate(0.0);
        }
        rate.setFk_Indexes_cdindex(cdindex);
        return rate;
    }

    /**
     * Insere uma nova taxa de índice usando $push no array "rates" do documento do índice.
     * Gera um cdrate sequencial via SequenceGenerator.
     *
     * @param rate Objeto contendo os dados da taxa (fk_Indexes_cdindex identifica o índice).
     */
    public void insertRate(Index_Rates rate) {
        try {
            int cdrate = SequenceGenerator.getNextSequence(RATES_SEQUENCE);
            rate.setCdrate(cdrate);

            int cdindex = rate.getFk_Indexes_cdindex();
            Document rateDoc = toDocument(rate);

            UpdateResult result = getCollection().updateOne(
                Filters.eq("_id", cdindex),
                Updates.push("rates", rateDoc)
            );

            if (result.getMatchedCount() == 0) {
                System.err.println("Erro ao inserir taxa: índice com ID " + cdindex + " não encontrado.");
            } else {
                System.out.println("Taxa inserida com sucesso! (cdrate: " + cdrate + ")");
            }
        } catch (Exception e) {
            System.err.println("Erro ao inserir taxa de índice: " + e.getMessage());
        }
    }

    /**
     * Busca as taxas dos últimos 12 meses anteriores à data base para um índice específico.
     *
     * @param cdindex Identificador do índice.
     * @param baseDate Data base para o cálculo.
     * @return Lista de objetos Index_Rates ordenada por ano/mês ascendente.
     */
    public List<Index_Rates> findLast12MonthsRates(int cdindex, LocalDate baseDate) {
        List<Index_Rates> list = new ArrayList<>();
        try {
            Document doc = getCollection().find(Filters.eq("_id", cdindex)).first();
            if (doc == null) return list;

            List<Document> rates = doc.getList("rates", Document.class);
            if (rates == null) return list;

            LocalDate limitDate = baseDate.minusMonths(12);
            int limitYear = limitDate.getYear();
            int limitMonth = limitDate.getMonthValue();
            int baseYear = baseDate.getYear();
            int baseMonth = baseDate.getMonthValue();

            for (Document rateDoc : rates) {
                int refyear = rateDoc.getInteger("refyear");
                int refmonth = rateDoc.getInteger("refmonth");

                // Filtro: >= limitDate AND < baseDate (mês/ano)
                boolean afterLimit = (refyear > limitYear) || (refyear == limitYear && refmonth >= limitMonth);
                boolean beforeBase = (refyear < baseYear) || (refyear == baseYear && refmonth < baseMonth);

                if (afterLimit && beforeBase) {
                    list.add(fromDocument(rateDoc, cdindex));
                }
            }

            // Ordenar por ano e mês ascendente
            list.sort((a, b) -> {
                if (a.getRefyear() != b.getRefyear()) return Integer.compare(a.getRefyear(), b.getRefyear());
                return Integer.compare(a.getRefmonth(), b.getRefmonth());
            });

        } catch (Exception e) {
            System.err.println("Erro ao buscar taxas dos últimos 12 meses: " + e.getMessage());
        }
        return list;
    }

    /**
     * Busca a taxa mais recente para um determinado índice.
     *
     * @param cdindex Identificador do índice.
     * @return Objeto Index_Rates mais recente ou null.
     */
    public Index_Rates findLatestById(int cdindex) {
        try {
            Document doc = getCollection().find(Filters.eq("_id", cdindex)).first();
            if (doc == null) return null;

            List<Document> rates = doc.getList("rates", Document.class);
            if (rates == null || rates.isEmpty()) return null;

            // Encontra a taxa mais recente (maior ano, depois maior mês)
            Document latest = null;
            for (Document rateDoc : rates) {
                if (latest == null) {
                    latest = rateDoc;
                } else {
                    int curYear = rateDoc.getInteger("refyear");
                    int curMonth = rateDoc.getInteger("refmonth");
                    int latYear = latest.getInteger("refyear");
                    int latMonth = latest.getInteger("refmonth");
                    if (curYear > latYear || (curYear == latYear && curMonth > latMonth)) {
                        latest = rateDoc;
                    }
                }
            }

            return fromDocument(latest, cdindex);
        } catch (Exception e) {
            System.err.println("Erro ao buscar taxa mais recente: " + e.getMessage());
        }
        return null;
    }

    /**
     * Busca uma taxa de índice pelo cdrate (busca em todos os documentos de índice).
     *
     * @param cdrate Identificador da taxa.
     * @return Objeto Index_Rates ou null se não encontrado.
     */
    public Index_Rates findById(int cdrate) {
        try {
            // Busca o documento de índice que contém a taxa com o cdrate especificado
            Document indexDoc = getCollection().find(
                Filters.elemMatch("rates", Filters.eq("cdrate", cdrate))
            ).first();

            if (indexDoc == null) return null;

            int cdindex = indexDoc.getInteger("_id");
            List<Document> rates = indexDoc.getList("rates", Document.class);
            if (rates == null) return null;

            for (Document rateDoc : rates) {
                if (rateDoc.getInteger("cdrate") == cdrate) {
                    return fromDocument(rateDoc, cdindex);
                }
            }
        } catch (Exception e) {
            System.err.println("Erro ao buscar taxa por ID: " + e.getMessage());
        }
        return null;
    }

    /**
     * Lista todas as taxas de índices cadastradas (de todos os índices).
     * Retorna ordenado por refyear DESC, refmonth DESC.
     *
     * @return Lista de objetos Index_Rates.
     */
    public List<Index_Rates> listAll() {
        List<Index_Rates> list = new ArrayList<>();
        try (MongoCursor<Document> cursor = getCollection().find().iterator()) {
            while (cursor.hasNext()) {
                Document indexDoc = cursor.next();
                int cdindex = indexDoc.getInteger("_id");
                List<Document> rates = indexDoc.getList("rates", Document.class);
                if (rates != null) {
                    for (Document rateDoc : rates) {
                        list.add(fromDocument(rateDoc, cdindex));
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("Erro ao listar taxas: " + e.getMessage());
        }

        // Ordenar por refyear DESC, refmonth DESC
        list.sort((a, b) -> {
            if (a.getRefyear() != b.getRefyear()) return Integer.compare(b.getRefyear(), a.getRefyear());
            return Integer.compare(b.getRefmonth(), a.getRefmonth());
        });
        return list;
    }

    /**
     * Lista todas as taxas de um índice específico.
     *
     * @param cdindex Identificador do índice.
     * @return Lista de objetos Index_Rates ordenada por refyear DESC, refmonth DESC.
     */
    public List<Index_Rates> listAll(int cdindex) {
        List<Index_Rates> list = new ArrayList<>();
        try {
            Document doc = getCollection().find(Filters.eq("_id", cdindex)).first();
            if (doc == null) return list;

            List<Document> rates = doc.getList("rates", Document.class);
            if (rates == null) return list;

            for (Document rateDoc : rates) {
                list.add(fromDocument(rateDoc, cdindex));
            }
        } catch (Exception e) {
            System.err.println("Erro ao listar taxas do índice " + cdindex + ": " + e.getMessage());
        }

        list.sort((a, b) -> {
            if (a.getRefyear() != b.getRefyear()) return Integer.compare(b.getRefyear(), a.getRefyear());
            return Integer.compare(b.getRefmonth(), a.getRefmonth());
        });
        return list;
    }

    /**
     * Atualiza uma taxa de índice existente no array embarcado.
     * Localiza a taxa pelo cdrate e atualiza refmonth, refyear e vlrate.
     *
     * @param rate Objeto contendo os dados atualizados.
     */
    public void update(Index_Rates rate) {
        try {
            int cdindex = rate.getFk_Indexes_cdindex();
            Bson filter = Filters.and(
                Filters.eq("_id", cdindex),
                Filters.elemMatch("rates", Filters.eq("cdrate", rate.getCdrate()))
            );

            Bson update = Updates.combine(
                Updates.set("rates.$.refmonth", rate.getRefmonth()),
                Updates.set("rates.$.refyear", rate.getRefyear()),
                Updates.set("rates.$.vlrate", rate.getVlrate())
            );

            UpdateResult result = getCollection().updateOne(filter, update);
            if (result.getMatchedCount() == 0) {
                System.err.println("Taxa com cdrate " + rate.getCdrate() + " não encontrada no índice " + cdindex + ".");
            }
        } catch (Exception e) {
            System.err.println("Erro ao atualizar taxa: " + e.getMessage());
        }
    }

    /**
     * Exclui uma taxa de índice pelo cdrate usando $pull no array embarcado.
     *
     * @param cdrate Identificador da taxa.
     * @return true se excluída com sucesso.
     */
    public boolean delete(int cdrate) {
        try {
            // Primeiro, encontra o índice que contém essa taxa
            Document indexDoc = getCollection().find(
                Filters.elemMatch("rates", Filters.eq("cdrate", cdrate))
            ).first();

            if (indexDoc == null) return false;

            int cdindex = indexDoc.getInteger("_id");

            // Remove a taxa do array usando $pull
            UpdateResult result = getCollection().updateOne(
                Filters.eq("_id", cdindex),
                Updates.pull("rates", new Document("cdrate", cdrate))
            );

            return result.getModifiedCount() > 0;
        } catch (Exception e) {
            System.err.println("Erro ao excluir taxa: " + e.getMessage());
            return false;
        }
    }

    /**
     * Exclui uma taxa de índice específica por cdindex, refmonth e refyear.
     *
     * @param cdindex Identificador do índice.
     * @param refmonth Mês de referência.
     * @param refyear Ano de referência.
     * @return true se excluída com sucesso.
     */
    public boolean delete(int cdindex, int refmonth, int refyear) {
        try {
            Bson pullFilter = new Document("refmonth", refmonth)
                .append("refyear", refyear);

            UpdateResult result = getCollection().updateOne(
                Filters.eq("_id", cdindex),
                Updates.pull("rates", pullFilter)
            );

            return result.getModifiedCount() > 0;
        } catch (Exception e) {
            System.err.println("Erro ao excluir taxa: " + e.getMessage());
            return false;
        }
    }
}
