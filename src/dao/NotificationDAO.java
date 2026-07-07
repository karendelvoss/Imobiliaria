package dao;

import model.NotificationChannel;
import model.Notifications;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Sorts;
import org.bson.Document;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Gerencia as operações de persistência e lógica de deduplicação de notificações.
 * Utiliza a coleção independente "notifications" no MongoDB.
 */
public class NotificationDAO {

    private static final String COLLECTION_NAME = "notifications";
    private static final String TEMPLATES_COLLECTION = "notification_templates";

    /**
     * Converte um objeto Notifications para um Document BSON.
     *
     * @param n Objeto Notifications.
     * @return Document BSON representando a notificação.
     */
    private Document toDocument(Notifications n) {
        Document doc = new Document();
        doc.append("_id", n.getCdnotification());
        doc.append("dsmessage", n.getDsmessage());
        doc.append("dtsend", n.getDtsend() != null ? n.getDtsend().toString() : null);
        doc.append("cdcontract", n.getCdcontract());
        doc.append("cduser", n.getCduser());
        doc.append("cdnotificationtemplate", n.getCdnotificationtemplate());
        doc.append("fgchannel", n.getFgchannel());
        return doc;
    }

    /**
     * Converte um Document BSON para um objeto Notifications.
     *
     * @param doc Document BSON.
     * @return Objeto Notifications preenchido.
     */
    private Notifications fromDocument(Document doc) {
        Notifications n = new Notifications();
        n.setCdnotification(doc.getInteger("_id"));
        n.setDsmessage(doc.getString("dsmessage"));
        String dtsend = doc.getString("dtsend");
        if (dtsend != null && !dtsend.isEmpty()) {
            n.setDtsend(LocalDate.parse(dtsend));
        }
        n.setCdcontract(doc.getInteger("cdcontract", 0));
        n.setCduser(doc.getInteger("cduser", 0));
        n.setCdnotificationtemplate(doc.getInteger("cdnotificationtemplate", 0));
        n.setFgchannel(doc.getInteger("fgchannel", 0));
        return n;
    }

    /**
     * Insere uma nova notificação no banco de dados.
     *
     * @param n Objeto contendo os dados da notificação.
     */
    public void insert(Notifications n) {
        try {
            MongoCollection<Document> collection = Conexao.getCollection(COLLECTION_NAME);
            int id = SequenceGenerator.getNextSequence(COLLECTION_NAME);
            n.setCdnotification(id);
            if (n.getFgchannel() <= 0) {
                n.setFgchannel(NotificationChannel.EMAIL.getCode());
            }
            collection.insertOne(toDocument(n));
            System.out.println("Notificação inserida com sucesso! (ID: " + n.getCdnotification() + ")");
        } catch (Exception e) {
            System.err.println("Erro ao inserir notificação: " + e.getMessage());
        }
    }

    /**
     * Busca uma notificação pelo seu identificador.
     *
     * @param id Identificador da notificação.
     * @return Objeto Notifications ou null.
     */
    public Notifications findById(int id) {
        try {
            MongoCollection<Document> collection = Conexao.getCollection(COLLECTION_NAME);
            Document doc = collection.find(Filters.eq("_id", id)).first();
            if (doc != null) {
                return fromDocument(doc);
            }
        } catch (Exception e) {
            System.err.println("Erro ao buscar notificação: " + e.getMessage());
        }
        return null;
    }

    /**
     * Lista todas as notificações cadastradas, ordenadas pela data de envio (desc).
     *
     * @return Lista de objetos Notifications.
     */
    public List<Notifications> listAll() {
        List<Notifications> list = new ArrayList<>();
        try {
            MongoCollection<Document> collection = Conexao.getCollection(COLLECTION_NAME);
            for (Document doc : collection.find()
                    .sort(Sorts.orderBy(Sorts.descending("dtsend"), Sorts.descending("_id")))) {
                list.add(fromDocument(doc));
            }
        } catch (Exception e) {
            System.err.println("Erro ao listar notificações: " + e.getMessage());
        }
        return list;
    }

    /**
     * Exclui uma notificação pelo seu identificador.
     *
     * @param id Identificador da notificação.
     * @return true se excluída com sucesso.
     */
    public boolean delete(int id) {
        try {
            MongoCollection<Document> collection = Conexao.getCollection(COLLECTION_NAME);
            return collection.deleteOne(Filters.eq("_id", id)).getDeletedCount() > 0;
        } catch (Exception e) {
            System.err.println("Erro ao excluir notificação: " + e.getMessage());
            return false;
        }
    }

    /**
     * Verifica se já existe uma notificação com os mesmos parâmetros básicos na data informada.
     * Usado para deduplicação — evita criar notificações duplicadas.
     *
     * @param cdcontract Identificador do contrato.
     * @param cduser Identificador do usuário.
     * @param cdtemplate Identificador do modelo de notificação.
     * @param dtsend Data de envio.
     * @return true se a notificação já existe.
     */
    public boolean jaExiste(int cdcontract, int cduser, int cdtemplate, LocalDate dtsend) {
        try {
            MongoCollection<Document> collection = Conexao.getCollection(COLLECTION_NAME);
            Document found = collection.find(Filters.and(
                Filters.eq("cdcontract", cdcontract),
                Filters.eq("cduser", cduser),
                Filters.eq("cdnotificationtemplate", cdtemplate),
                Filters.eq("dtsend", dtsend != null ? dtsend.toString() : null)
            )).first();
            return found != null;
        } catch (Exception e) {
            System.err.println("Erro ao verificar duplicata de notificação: " + e.getMessage());
            return false;
        }
    }

    /**
     * Busca o ID do modelo de notificação baseado no código do tipo de evento.
     * Consulta a coleção "notification_templates".
     *
     * @param tpcode Código do tipo de evento.
     * @return ID do modelo ou -1 se não encontrado.
     */
    public int findTemplateIdByCode(int tpcode) {
        try {
            MongoCollection<Document> templates = Conexao.getCollection(TEMPLATES_COLLECTION);
            Document doc = templates.find(Filters.eq("tpcode", tpcode)).first();
            if (doc != null) {
                return doc.getInteger("_id");
            }
        } catch (Exception e) {
            System.err.println("Erro ao buscar template de notificação: " + e.getMessage());
        }
        return -1;
    }

    /**
     * Registra uma nova notificação caso ela ainda não exista para os mesmos parâmetros e data.
     *
     * @param cdcontract Identificador do contrato.
     * @param cduser Identificador do usuário.
     * @param tpcode Código do tipo de evento.
     * @param mensagem Conteúdo da mensagem.
     * @param dtsend Data de envio.
     * @return true se a notificação foi criada com sucesso.
     */
    public boolean criarSeNaoExistir(int cdcontract, int cduser, int tpcode,
                                      String mensagem, LocalDate dtsend) {
        return criarSeNaoExistir(cdcontract, cduser, tpcode, mensagem, dtsend, NotificationChannel.EMAIL);
    }

    /**
     * Registra uma nova notificação via canal específico caso ela ainda não exista.
     *
     * @param cdcontract Identificador do contrato.
     * @param cduser Identificador do usuário.
     * @param tpcode Código do tipo de evento.
     * @param mensagem Conteúdo da mensagem.
     * @param dtsend Data de envio.
     * @param channel Canal de notificação desejado.
     * @return true se a notificação foi criada com sucesso.
     */
    public boolean criarSeNaoExistir(int cdcontract, int cduser, int tpcode,
                                      String mensagem, LocalDate dtsend,
                                      NotificationChannel channel) {
        int cdtemplate = findTemplateIdByCode(tpcode);
        if (cdtemplate < 0) {
            System.err.printf("Template não encontrado para tpcode=%d.%n", tpcode);
            return false;
        }

        if (jaExiste(cdcontract, cduser, cdtemplate, dtsend)) {
            return false;
        }

        Notifications n = new Notifications();
        n.setCdcontract(cdcontract);
        n.setCduser(cduser);
        n.setCdnotificationtemplate(cdtemplate);
        n.setDsmessage(mensagem);
        n.setDtsend(dtsend);
        n.setFgchannel(channel.getCode());
        insert(n);
        return true;
    }
}
