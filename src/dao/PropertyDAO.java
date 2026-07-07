package dao;

import model.Addresses;
import model.ContractStatus;
import model.Properties;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Sorts;
import com.mongodb.client.model.Updates;
import org.bson.Document;
import org.bson.conversions.Bson;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

/**
 * Gerencia as operações de persistência para os imóveis (Properties).
 *
 * No modelo MongoDB, o documento de imóvel contém:
 * <ul>
 *   <li>Endereço embarcado como subdocumento {@code address}</li>
 *   <li>Tipo, finalidade e status como campos texto</li>
 *   <li>Array {@code owners} com IDs dos proprietários</li>
 * </ul>
 */
public class PropertyDAO {

    private static final String COLLECTION_NAME = "properties";
    private static final String CONTRACTS_COLLECTION = "contracts";
    private static final String USERS_COLLECTION = "users";

    /**
     * Obtém a coleção MongoDB de imóveis.
     */
    private MongoCollection<Document> getCollection() {
        return Conexao.getCollection(COLLECTION_NAME);
    }

    /**
     * Converte um objeto Properties para Document BSON.
     * Inclui subdocumento de endereço e campos texto para tipo/finalidade/status.
     *
     * @param prop Objeto do modelo.
     * @param address Objeto de endereço para embarcar (pode ser null).
     * @param typeName Nome do tipo de imóvel.
     * @param purposeName Nome da finalidade.
     * @param statusName Nome do status.
     * @param owners Lista de IDs dos proprietários.
     * @return Document BSON correspondente.
     */
    public static Document toDocument(Properties prop, Addresses address, String typeName, String purposeName, String statusName, List<Integer> owners) {
        Document doc = new Document();
        doc.append("_id", prop.getCdproperty());
        doc.append("nrregistration", prop.getNrregistration());
        doc.append("dsdescription", prop.getDsdescription());
        doc.append("vltotalarea", prop.getVltotalarea());
        doc.append("address", AddressDAO.toDocument(address));
        doc.append("type", typeName);
        doc.append("purpose", purposeName);
        doc.append("status", statusName);
        doc.append("owners", owners != null ? owners : new ArrayList<>());
        return doc;
    }

    /**
     * Converte um Document BSON para objeto Properties.
     * Extrai os dados básicos do documento (sem endereço detalhado).
     *
     * @param doc Document BSON.
     * @return Objeto Properties, ou null se doc for null.
     */
    public static Properties fromDocument(Document doc) {
        if (doc == null) return null;
        Properties p = new Properties();
        p.setCdproperty(doc.getInteger("_id"));
        p.setNrregistration(doc.getString("nrregistration"));
        p.setDsdescription(doc.getString("dsdescription"));
        p.setVltotalarea(doc.getDouble("vltotalarea") != null ? doc.getDouble("vltotalarea") : 0.0);
        // cdaddress stores the property's own _id (address is embedded)
        p.setCdaddress(doc.getInteger("_id"));
        // cdtype, cdpurpose, cdstatus are no longer FKs; set to 0 as placeholder
        p.setCdtype(0);
        p.setCdpurpose(0);
        p.setCdstatus(0);
        return p;
    }

    /**
     * Insere um novo imóvel no banco de dados.
     *
     * @param prop Objeto contendo os dados do imóvel.
     * @return O ID gerado para o imóvel ou -1 em caso de erro.
     */
    public int insertProperty(Properties prop) {
        try {
            int newId = SequenceGenerator.getNextSequence(COLLECTION_NAME);
            prop.setCdproperty(newId);

            // Buscar endereço para embarcar (se cdaddress foi definido)
            Addresses address = null;
            if (prop.getCdaddress() > 0) {
                AddressDAO addressDAO = new AddressDAO();
                address = addressDAO.findById(prop.getCdaddress());
            }

            // Buscar nomes de tipo/finalidade/status
            String typeName = resolveTypeName(prop.getCdtype());
            String purposeName = resolvePurposeName(prop.getCdpurpose());
            String statusName = resolveStatusName(prop.getCdstatus());

            Document doc = toDocument(prop, address, typeName, purposeName, statusName, new ArrayList<>());
            getCollection().insertOne(doc);
            System.out.println("Imóvel cadastrado com sucesso! (ID: " + newId + ")");
            return newId;
        } catch (com.mongodb.MongoWriteException e) {
            if (e.getMessage() != null && e.getMessage().contains("duplicate key")) {
                System.err.println("ERRO: Já existe um imóvel cadastrado com esta matrícula.");
            } else {
                System.err.println("Erro ao cadastrar imóvel: " + e.getMessage());
            }
        } catch (Exception e) {
            System.err.println("Erro ao cadastrar imóvel: " + e.getMessage());
        }
        return -1;
    }

    /**
     * Vincula um proprietário a um imóvel.
     * Usa $addToSet para evitar duplicatas no array owners.
     *
     * @param idProperty Identificador do imóvel.
     * @param idUser Identificador do usuário proprietário.
     */
    public void linkOwner(int idProperty, int idUser) {
        try {
            getCollection().updateOne(
                Filters.eq("_id", idProperty),
                Updates.addToSet("owners", idUser)
            );
            System.out.println("Proprietário vinculado ao imóvel com sucesso!");
        } catch (Exception e) {
            System.err.println("Erro ao vincular proprietário ao imóvel: " + e.getMessage());
        }
    }

    /**
     * Desvincula um proprietário de um imóvel.
     * Usa $pull para remover o ID do array owners.
     *
     * @param idProperty Identificador do imóvel.
     * @param idUser Identificador do usuário proprietário.
     */
    public void unlinkOwner(int idProperty, int idUser) {
        try {
            long modifiedCount = getCollection().updateOne(
                Filters.eq("_id", idProperty),
                Updates.pull("owners", idUser)
            ).getModifiedCount();

            if (modifiedCount > 0) {
                System.out.println("Proprietário desvinculado do imóvel com sucesso!");
            } else {
                System.out.println("Nenhum vínculo encontrado para remoção.");
            }
        } catch (Exception e) {
            System.err.println("Erro ao desvincular proprietário: " + e.getMessage());
        }
    }

    /**
     * Lista todos os imóveis cadastrados.
     *
     * @return Lista de objetos Properties.
     */
    public List<Properties> listAll() {
        List<Properties> list = new ArrayList<>();
        try (MongoCursor<Document> cursor = getCollection()
                .find()
                .sort(new Document("_id", 1))
                .iterator()) {
            while (cursor.hasNext()) {
                Document doc = cursor.next();
                list.add(fromDocument(doc));
            }
        } catch (Exception e) {
            System.err.println("Erro ao listar imóveis: " + e.getMessage());
        }
        return list;
    }

    /**
     * Exclui um imóvel pelo seu identificador.
     *
     * @param id Identificador do imóvel.
     */
    public void deleteProperty(int id) {
        try {
            getCollection().deleteOne(Filters.eq("_id", id));
        } catch (Exception e) {
            System.err.println("Erro ao excluir imóvel: " + e.getMessage());
        }
    }

    /**
     * Atualiza os dados de um imóvel existente.
     *
     * @param prop Objeto contendo os dados atualizados do imóvel.
     */
    public void updateProperty(Properties prop) {
        try {
            // Buscar nomes de tipo/finalidade/status
            String typeName = resolveTypeName(prop.getCdtype());
            String purposeName = resolvePurposeName(prop.getCdpurpose());
            String statusName = resolveStatusName(prop.getCdstatus());

            List<Bson> updates = new ArrayList<>();
            updates.add(Updates.set("nrregistration", prop.getNrregistration()));
            updates.add(Updates.set("dsdescription", prop.getDsdescription()));
            updates.add(Updates.set("vltotalarea", prop.getVltotalarea()));
            updates.add(Updates.set("type", typeName));
            updates.add(Updates.set("purpose", purposeName));
            updates.add(Updates.set("status", statusName));

            getCollection().updateOne(
                Filters.eq("_id", prop.getCdproperty()),
                Updates.combine(updates)
            );
        } catch (Exception e) {
            System.err.println("Erro ao atualizar imóvel: " + e.getMessage());
        }
    }

    /**
     * Busca um imóvel pelo seu identificador.
     *
     * @param id Identificador do imóvel.
     * @return Objeto Properties ou null.
     */
    public Properties findById(int id) {
        try {
            Document doc = getCollection().find(Filters.eq("_id", id)).first();
            return fromDocument(doc);
        } catch (Exception e) {
            System.err.println("Erro ao buscar imóvel por ID: " + e.getMessage());
        }
        return null;
    }

    /**
     * Retorna uma lista de imóveis disponíveis para vinculação.
     *
     * @return Lista de Strings formatadas com ID e Matrícula.
     */
    public List<String> getAvailableProperties() {
        List<String> list = new ArrayList<>();
        try (MongoCursor<Document> cursor = getCollection()
                .find()
                .sort(new Document("_id", 1))
                .iterator()) {
            while (cursor.hasNext()) {
                Document doc = cursor.next();
                list.add("ID: " + doc.getInteger("_id") + " | Reg: " + doc.getString("nrregistration"));
            }
        } catch (Exception e) {
            System.err.println("Erro ao listar imóveis disponíveis: " + e.getMessage());
        }
        return list;
    }

    /**
     * Conta quantos proprietários estão vinculados a um imóvel.
     *
     * @param idProperty Identificador do imóvel.
     * @return Quantidade de proprietários.
     */
    public int countOwners(int idProperty) {
        try {
            Document doc = getCollection().find(Filters.eq("_id", idProperty)).first();
            if (doc != null) {
                List<Integer> owners = doc.getList("owners", Integer.class);
                return owners != null ? owners.size() : 0;
            }
        } catch (Exception e) {
            System.err.println("Erro ao contar proprietários: " + e.getMessage());
        }
        return 0;
    }

    /**
     * Verifica se o imóvel possui ao menos um contrato vigente vinculado
     * (qualquer status diferente de FINALIZADO — inclui ATIVO e INDETERMINADO).
     *
     * @param idProperty Identificador do imóvel.
     * @return {@code true} se existir pelo menos um contrato vigente.
     */
    public boolean hasActiveContract(int idProperty) {
        try {
            MongoCollection<Document> contracts = Conexao.getCollection(CONTRACTS_COLLECTION);
            Document doc = contracts.find(
                Filters.and(
                    Filters.eq("cdproperty", idProperty),
                    Filters.ne("cdstatus", ContractStatus.FINALIZADO.getCode())
                )
            ).first();
            return doc != null;
        } catch (Exception e) {
            System.err.println("Erro ao verificar contratos ativos do imóvel: " + e.getMessage());
            return false;
        }
    }

    /**
     * Lista apenas os imóveis com status "Disponível".
     *
     * @return Lista de Strings formatadas.
     */
    public List<String> getAvailableOnly() {
        List<String> list = new ArrayList<>();
        try (MongoCursor<Document> cursor = getCollection()
                .find(Filters.eq("status", "Disponível"))
                .sort(new Document("_id", 1))
                .iterator()) {
            while (cursor.hasNext()) {
                Document doc = cursor.next();
                list.add("ID: " + doc.getInteger("_id") + " | Reg: " + doc.getString("nrregistration") + " | " + doc.getString("dsdescription"));
            }
        } catch (Exception e) {
            System.err.println("Erro ao listar imóveis disponíveis: " + e.getMessage());
        }
        return list;
    }

    /**
     * Verifica se um usuário já é proprietário de um imóvel.
     *
     * @param idProperty Identificador do imóvel.
     * @param idUser Identificador do usuário.
     * @return true se o vínculo já existe.
     */
    public boolean hasAlreadyThisOwner(int idProperty, int idUser) {
        try {
            Document doc = getCollection().find(
                Filters.and(
                    Filters.eq("_id", idProperty),
                    Filters.in("owners", idUser)
                )
            ).first();
            return doc != null;
        } catch (Exception e) {
            System.err.println("Erro ao verificar proprietário: " + e.getMessage());
        }
        return false;
    }

    /**
     * Lista os nomes dos proprietários vinculados a um imóvel.
     * Faz lookup na coleção users para obter os nomes.
     *
     * @param idProp Identificador do imóvel.
     * @return Lista de nomes dos proprietários.
     */
    public List<String> getOwnersByProperty(int idProp) {
        List<String> owners = new ArrayList<>();
        try {
            Document propDoc = getCollection().find(Filters.eq("_id", idProp)).first();
            if (propDoc != null) {
                List<Integer> ownerIds = propDoc.getList("owners", Integer.class);
                if (ownerIds != null && !ownerIds.isEmpty()) {
                    MongoCollection<Document> users = Conexao.getCollection(USERS_COLLECTION);
                    try (MongoCursor<Document> cursor = users.find(Filters.in("_id", ownerIds)).iterator()) {
                        while (cursor.hasNext()) {
                            Document userDoc = cursor.next();
                            owners.add(userDoc.getString("nmuser"));
                        }
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("Erro ao buscar proprietários: " + e.getMessage());
        }
        return owners;
    }

    /**
     * Lista os IDs dos proprietários vinculados a um imóvel.
     *
     * @param idProp Identificador do imóvel.
     * @return Lista de IDs dos proprietários.
     */
    public List<Integer> getOwnerIdsByProperty(int idProp) {
        List<Integer> owners = new ArrayList<>();
        try {
            Document doc = getCollection().find(Filters.eq("_id", idProp)).first();
            if (doc != null) {
                List<Integer> ownerIds = doc.getList("owners", Integer.class);
                if (ownerIds != null) {
                    owners.addAll(ownerIds);
                }
            }
        } catch (Exception e) {
            System.err.println("Erro ao buscar IDs dos proprietários: " + e.getMessage());
        }
        return owners;
    }

    /**
     * Lista os imóveis que possuem pelo menos um proprietário vinculado.
     *
     * @return Lista de Strings formatadas.
     */
    public List<String> getPropertiesWithOwners() {
        List<String> list = new ArrayList<>();
        try (MongoCursor<Document> cursor = getCollection()
                .find(Filters.and(
                    Filters.exists("owners"),
                    Filters.not(Filters.size("owners", 0))
                ))
                .sort(new Document("_id", 1))
                .iterator()) {
            while (cursor.hasNext()) {
                Document doc = cursor.next();
                list.add("ID: " + doc.getInteger("_id") + " | Reg: " + doc.getString("nrregistration"));
            }
        } catch (Exception e) {
            System.err.println("Erro ao listar imóveis com proprietários: " + e.getMessage());
        }
        return list;
    }

    /**
     * Verifica se um usuário é proprietário de um imóvel.
     *
     * @param idProp Identificador do imóvel.
     * @param idUser Identificador do usuário.
     * @return true se o usuário é proprietário.
     */
    public boolean isUserOwner(int idProp, int idUser) {
        return hasAlreadyThisOwner(idProp, idUser);
    }

    /**
     * Lista imóveis que podem ser vinculados (não estão com status "Vendido").
     *
     * @return Lista de Strings formatadas.
     */
    public List<String> getLinkableProperties() {
        List<String> list = new ArrayList<>();
        try (MongoCursor<Document> cursor = getCollection()
                .find(Filters.ne("status", "Vendido"))
                .sort(new Document("_id", 1))
                .iterator()) {
            while (cursor.hasNext()) {
                Document doc = cursor.next();
                list.add("ID: " + doc.getInteger("_id") + " | Reg: " + doc.getString("nrregistration"));
            }
        } catch (Exception e) {
            System.err.println("Erro ao listar imóveis vinculáveis: " + e.getMessage());
        }
        return list;
    }

    /**
     * Busca os detalhes completos de um imóvel extraindo dados do documento embarcado.
     *
     * @param id Identificador do imóvel.
     * @return String formatada com os detalhes ou null.
     */
    public String findByIdDetalhado(int id) {
        try {
            Document doc = getCollection().find(Filters.eq("_id", id)).first();
            if (doc != null) {
                Document addrDoc = doc.get("address", Document.class);
                String endereco = "N/A";
                if (addrDoc != null) {
                    String street = addrDoc.getString("nmstreet") != null ? addrDoc.getString("nmstreet") : "";
                    String number = addrDoc.getString("nraddress") != null ? addrDoc.getString("nraddress") : "";
                    endereco = street + ", nº " + number;
                }
                double area = doc.getDouble("vltotalarea") != null ? doc.getDouble("vltotalarea") : 0.0;

                return String.format(
                    "\n--- DETALHES DO IMÓVEL ---\n" +
                    "ID: %d | Matrícula: %s\n" +
                    "Descrição: %s\n" +
                    "Tipo: %s | Finalidade: %s\n" +
                    "Área: %.2fm² | Status: %s\n" +
                    "Endereço: %s",
                    doc.getInteger("_id"), doc.getString("nrregistration"),
                    doc.getString("dsdescription"),
                    doc.getString("type") != null ? doc.getString("type") : "N/A",
                    doc.getString("purpose") != null ? doc.getString("purpose") : "N/A",
                    area,
                    doc.getString("status") != null ? doc.getString("status") : "N/A",
                    endereco
                );
            }
        } catch (Exception e) {
            System.err.println("Erro na consulta detalhada: " + e.getMessage());
        }
        return null;
    }

    /**
     * Exibe no console um relatório completo de todos os imóveis cadastrados,
     * com opção de filtragem por bairro usando regex case-insensitive.
     *
     * @param filtroBairro Nome (ou parte) do bairro para filtrar. Use {@code null}
     *                     ou string vazia para listar todos os imóveis.
     */
    public void relatorioCompletoImoveis(String filtroBairro) {
        boolean comFiltro = filtroBairro != null && !filtroBairro.isBlank();

        try {
            Bson filter = comFiltro
                ? Filters.regex("address.district", Pattern.compile(filtroBairro, Pattern.CASE_INSENSITIVE))
                : new Document();

            String titulo = comFiltro
                ? "RELATÓRIO: IMÓVEIS (Bairro contém \"" + filtroBairro + "\")"
                : "RELATÓRIO: LISTAGEM GERAL DE IMÓVEIS";
            System.out.println("\n--- " + titulo + " ---");
            System.out.printf("%-3s | %-9s | %-14s | %-10s | %-18s | %-15s | %s%n",
                    "ID", "MATRÍCULA", "TIPO", "STATUS", "BAIRRO", "CIDADE", "ENDEREÇO");
            System.out.println("--------------------------------------------------------------------------------------------------------");

            boolean encontrou = false;
            try (MongoCursor<Document> cursor = getCollection()
                    .find(filter)
                    .sort(Sorts.ascending("address.district", "_id"))
                    .iterator()) {
                while (cursor.hasNext()) {
                    encontrou = true;
                    Document doc = cursor.next();
                    Document addrDoc = doc.get("address", Document.class);

                    String bairro = "N/A";
                    String cidade = "N/A";
                    String enderecoCompleto = "N/A";

                    if (addrDoc != null) {
                        bairro = addrDoc.getString("district") != null ? addrDoc.getString("district") : "N/A";
                        cidade = addrDoc.getString("city") != null ? addrDoc.getString("city") : "N/A";
                        String street = addrDoc.getString("nmstreet") != null ? addrDoc.getString("nmstreet") : "";
                        String number = addrDoc.getString("nraddress") != null ? addrDoc.getString("nraddress") : "";
                        enderecoCompleto = street + ", nº " + number;
                    }

                    System.out.printf("%-3d | %-9s | %-14s | %-10s | %-18s | %-15s | %s%n",
                            doc.getInteger("_id"), doc.getString("nrregistration"),
                            doc.getString("type") != null ? doc.getString("type") : "N/A",
                            doc.getString("status") != null ? doc.getString("status") : "N/A",
                            bairro, cidade, enderecoCompleto);
                }
            }

            if (!encontrou) {
                System.out.println(comFiltro
                    ? "Nenhum imóvel encontrado para o bairro informado."
                    : "Nenhum imóvel cadastrado.");
            }
        } catch (Exception e) {
            System.err.println("Erro no relatório: " + e.getMessage());
        }
    }

    /**
     * Sobrecarga conveniente — lista todos os imóveis sem filtro de bairro.
     */
    public void relatorioCompletoImoveis() {
        relatorioCompletoImoveis(null);
    }

    // ----- Métodos auxiliares para resolver nomes de tipo/finalidade/status -----

    // Mapeamento legado ID → texto (baseado nos dados originais do insert.sql)
    private static final java.util.Map<Integer, String> TYPE_MAP = java.util.Map.of(
        1, "Casa", 2, "Apartamento", 3, "Terreno", 4, "Sala Comercial", 5, "Galpão"
    );
    private static final java.util.Map<Integer, String> PURPOSE_MAP = java.util.Map.of(
        1, "Residencial", 2, "Comercial", 3, "Industrial"
    );
    private static final java.util.Map<Integer, String> STATUS_MAP = java.util.Map.of(
        1, "Alugado", 2, "Disponível", 3, "Vendido"
    );

    /**
     * Resolve o nome do tipo de imóvel a partir do código legado.
     */
    private String resolveTypeName(int cdtype) {
        return TYPE_MAP.getOrDefault(cdtype, "Outro");
    }

    /**
     * Resolve o nome da finalidade a partir do código legado.
     */
    private String resolvePurposeName(int cdpurpose) {
        return PURPOSE_MAP.getOrDefault(cdpurpose, "Outro");
    }

    /**
     * Resolve o nome do status a partir do código legado.
     */
    private String resolveStatusName(int cdstatus) {
        return STATUS_MAP.getOrDefault(cdstatus, "Disponível");
    }
}
