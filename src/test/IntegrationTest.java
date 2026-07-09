package test;

import dao.*;
import model.*;
import service.ContractPdfService;
import com.mongodb.client.ClientSession;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import org.bson.Document;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Testes de integração para operações cross-collection com MongoDB.
 *
 * Testa:
 * A) Registro completo de contrato (transação cross-collection)
 * B) Exclusão de contrato com cascata de notificações
 * C) Geração de PDF com dados do MongoDB
 *
 * NOTA: Os testes A e B requerem MongoDB configurado com replica set para transações.
 * Se transações não estiverem disponíveis (sem replica set), os testes detectam isso
 * e são ignorados com mensagem clara.
 *
 * **Validates: Requisitos 5.1, 5.2, 5.3, 9.1, 9.2, 9.3, 9.4**
 */
public class IntegrationTest {

    private static final List<Integer> createdPropertyIds = new ArrayList<>();
    private static final List<Integer> createdContractIds = new ArrayList<>();
    private static final List<Integer> createdNotificationIds = new ArrayList<>();
    private static boolean replicaSetAvailable = false;

    public static void main(String[] args) {
        System.out.println("=== Testes de Integração: Operações Cross-Collection ===");
        System.out.println("Validates: Requisitos 5.1, 5.2, 5.3, 9.1, 9.2, 9.3, 9.4");
        System.out.println();

        // Step 1: Check MongoDB connection with 3s timeout
        if (!checkMongoConnection()) {
            System.out.println("ERRO: MongoDB não está disponível na porta 27017.");
            System.out.println("Inicie o MongoDB antes de executar este teste:");
            System.out.println("  mongod --replSet rs0 --dbpath /data/db");
            System.out.println("  ou: docker run -d -p 27017:27017 mongo:7 --replSet rs0");
            System.exit(2);
        }
        System.out.println("✓ Conexão com MongoDB estabelecida.");
        System.out.println();

        // Check if replica set is available (required for transactions in Tests A and B)
        replicaSetAvailable = checkReplicaSet();
        if (replicaSetAvailable) {
            System.out.println("✓ Replica set detectado — transações multi-documento disponíveis.");
        } else {
            System.out.println("⚠ Replica set NÃO detectado — testes A e B serão IGNORADOS.");
            System.out.println("  Para habilitar transações, configure MongoDB como replica set:");
            System.out.println("    mongod --replSet rs0");
            System.out.println("    mongosh --eval \"rs.initiate()\"");
        }
        System.out.println();

        int passed = 0;
        int failed = 0;
        int skipped = 0;

        try {
            // Test A: Full Contract Registration (transaction)
            System.out.println("--- Teste A: Registro completo de contrato (transação cross-collection) ---");
            if (!replicaSetAvailable) {
                skipped++;
                System.out.println("⊘ IGNORADO: Requer replica set para transações.");
            } else if (testFullContractRegistration()) {
                passed++;
                System.out.println("✓ PASSOU: Contrato registrado com transação, parcelas embarcadas e status do imóvel atualizado.");
            } else {
                failed++;
                System.out.println("✗ FALHOU: Registro completo de contrato não funcionou como esperado.");
            }
            System.out.println();

            // Test B: Contract Deletion with Notification Cascade
            System.out.println("--- Teste B: Exclusão de contrato com cascata de notificações ---");
            if (!replicaSetAvailable) {
                skipped++;
                System.out.println("⊘ IGNORADO: Requer replica set para transações.");
            } else if (testDeleteContractWithNotificationCascade()) {
                passed++;
                System.out.println("✓ PASSOU: Contrato excluído e notificações associadas removidas em cascata.");
            } else {
                failed++;
                System.out.println("✗ FALHOU: Exclusão com cascata de notificações não funcionou como esperado.");
            }
            System.out.println();

            // Test C: PDF Generation
            System.out.println("--- Teste C: Geração de PDF com dados do MongoDB ---");
            if (testPdfGeneration()) {
                passed++;
                System.out.println("✓ PASSOU: Geração de PDF executada sem exceções.");
            } else {
                failed++;
                System.out.println("✗ FALHOU: Geração de PDF lançou exceção inesperada.");
            }
            System.out.println();

        } finally {
            cleanup();
        }

        // Print summary
        System.out.println("=== Resultado ===");
        System.out.println("Total:     " + (passed + failed + skipped));
        System.out.println("Passaram:  " + passed);
        System.out.println("Falharam:  " + failed);
        System.out.println("Ignorados: " + skipped);
        System.out.println();

        if (failed == 0) {
            System.out.println("✓ TODOS OS TESTES EXECUTADOS PASSARAM.");
            System.exit(0);
        } else {
            System.out.println("✗ " + failed + " teste(s) falharam.");
            System.exit(1);
        }
    }

    // ========== Test A: Full Contract Registration ==========

    /**
     * Teste A — Registro completo de contrato (transação cross-collection).
     *
     * 1. Cria um imóvel via PropertyDAO.insertProperty
     * 2. Chama ContractDAO.processFullContract() com contrato, participantes, parcelas e status
     * 3. Verifica: contrato existe no DB, parcelas estão embarcadas, status do imóvel mudou
     *
     * Validates: Requisitos 5.1, 9.1, 9.2, 9.3
     */
    private static boolean testFullContractRegistration() {
        try {
            PropertyDAO propertyDAO = new PropertyDAO();
            ContractDAO contractDAO = new ContractDAO();

            // 1. Create a property
            Properties prop = new Properties();
            prop.setNrregistration("INTTEST-A-" + System.nanoTime());
            prop.setDsdescription("Imóvel para teste de integração A");
            prop.setVltotalarea(100.0);
            prop.setCdtype("Casa");
            prop.setCdpurpose("Residencial");
            prop.setCdstatus("Disponível");

            int propId = propertyDAO.insertProperty(prop);
            if (propId <= 0) {
                System.err.println("  Falha ao criar imóvel para o teste.");
                return false;
            }
            createdPropertyIds.add(propId);

            // 2. Prepare contract data
            Contracts contract = new Contracts();
            contract.setDtcreation(LocalDate.now());
            contract.setDstitle("Contrato IntegTest A");
            contract.setCdtemplate(1);
            contract.setCdproperty(propId);
            contract.setCdindex(1);
            contract.setDtlimit(LocalDate.now().plusYears(1));
            contract.setCdstatus(ContractStatus.ATIVO.getCode());

            // Participants
            List<User_Contract> participants = new ArrayList<>();
            User_Contract locador = new User_Contract();
            locador.setCduser(1);
            locador.setCdrole(2); // Locador
            participants.add(locador);

            User_Contract locatario = new User_Contract();
            locatario.setCduser(2);
            locatario.setCdrole(1); // Locatário
            participants.add(locatario);

            // Installments
            List<Installments> installments = new ArrayList<>();
            for (int i = 1; i <= 3; i++) {
                Installments inst = new Installments();
                inst.setNrinstallment(i);
                inst.setDtdue(LocalDate.now().plusMonths(i));
                inst.setVlbase(1500.00);
                inst.setVladjusted(0.0);
                inst.setCdstatus(InstallmentStatus.PENDENTE.getCode());
                inst.setVlpenalty(10.0);
                inst.setVlinterest(1.0);
                installments.add(inst);
            }

            // 3. Execute full contract registration (transaction: insert contract + update property status)
            int novoStatus = 1; // Alugado
            contractDAO.processFullContract(contract, participants, installments, novoStatus);

            int contractId = contract.getCdcontract();
            if (contractId <= 0) {
                System.err.println("  processFullContract não gerou ID para o contrato.");
                return false;
            }
            createdContractIds.add(contractId);

            // 4. Verify: contract exists in DB
            Contracts found = contractDAO.findById(contractId);
            if (found == null) {
                System.err.println("  Contrato não encontrado no banco após processFullContract.");
                return false;
            }

            // 5. Verify: installments are embedded
            Document contractDoc = Conexao.getCollection("contracts").find(Filters.eq("_id", contractId)).first();
            if (contractDoc == null) {
                System.err.println("  Documento do contrato não encontrado na coleção.");
                return false;
            }
            List<Document> embeddedInstallments = contractDoc.getList("installments", Document.class);
            if (embeddedInstallments == null || embeddedInstallments.size() != 3) {
                System.err.println("  Esperado 3 parcelas embarcadas, obteve: " +
                    (embeddedInstallments == null ? 0 : embeddedInstallments.size()));
                return false;
            }

            // 6. Verify: participants are embedded
            List<Document> embeddedParticipants = contractDoc.getList("participants", Document.class);
            if (embeddedParticipants == null || embeddedParticipants.size() != 2) {
                System.err.println("  Esperado 2 participantes embarcados, obteve: " +
                    (embeddedParticipants == null ? 0 : embeddedParticipants.size()));
                return false;
            }

            // 7. Verify: property status changed to "Alugado"
            Document propDoc = Conexao.getCollection("properties").find(Filters.eq("_id", propId)).first();
            if (propDoc == null) {
                System.err.println("  Documento do imóvel não encontrado após transação.");
                return false;
            }
            String statusAfter = propDoc.getString("status");
            if (!"Alugado".equals(statusAfter)) {
                System.err.println("  Esperado status 'Alugado', obteve: '" + statusAfter + "'");
                return false;
            }

            return true;

        } catch (Exception e) {
            System.err.println("  Exceção no Teste A: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    // ========== Test B: Contract Deletion with Notification Cascade ==========

    /**
     * Teste B — Exclusão de contrato com cascata de notificações.
     *
     * 1. Insere um contrato e algumas notificações referenciando-o
     * 2. Chama ContractDAO.deleteContract()
     * 3. Verifica: contrato foi removido, notificações associadas também foram removidas
     *
     * Validates: Requisitos 5.2, 5.3, 9.1
     */
    private static boolean testDeleteContractWithNotificationCascade() {
        try {
            ContractDAO contractDAO = new ContractDAO();

            // 1. Insert a contract directly
            Contracts contract = new Contracts();
            contract.setDtcreation(LocalDate.now());
            contract.setDstitle("Contrato IntegTest B - Cascade");
            contract.setCdtemplate(1);
            contract.setCdproperty(0); // No property for this test
            contract.setCdstatus(ContractStatus.ATIVO.getCode());

            int contractId = contractDAO.insertContract(contract);
            if (contractId <= 0) {
                System.err.println("  Falha ao inserir contrato para teste B.");
                return false;
            }
            createdContractIds.add(contractId);

            // 2. Insert notifications referencing this contract
            MongoCollection<Document> notifCollection = Conexao.getCollection("notifications");
            List<Integer> notifIds = new ArrayList<>();
            for (int i = 0; i < 3; i++) {
                int notifId = SequenceGenerator.getNextSequence("notifications");
                Document notifDoc = new Document()
                    .append("_id", notifId)
                    .append("dsmessage", "Notificação de teste #" + (i + 1) + " para contrato " + contractId)
                    .append("dtsend", LocalDate.now().toString())
                    .append("cdcontract", contractId)
                    .append("cduser", 1)
                    .append("cdnotificationtemplate", 1)
                    .append("fgchannel", 1);
                notifCollection.insertOne(notifDoc);
                notifIds.add(notifId);
                createdNotificationIds.add(notifId);
            }

            // Verify notifications exist before deletion
            long notifCountBefore = notifCollection.countDocuments(Filters.eq("cdcontract", contractId));
            if (notifCountBefore != 3) {
                System.err.println("  Esperado 3 notificações antes da exclusão, obteve: " + notifCountBefore);
                return false;
            }

            // 3. Delete contract (should cascade to notifications)
            boolean deleted = contractDAO.deleteContract(contractId);
            if (!deleted) {
                System.err.println("  deleteContract retornou false.");
                return false;
            }
            // Remove from cleanup list since already deleted
            createdContractIds.remove(Integer.valueOf(contractId));
            createdNotificationIds.removeAll(notifIds);

            // 4. Verify: contract is gone
            Contracts afterDelete = contractDAO.findById(contractId);
            if (afterDelete != null) {
                System.err.println("  Contrato ainda existe após deleteContract.");
                return false;
            }

            // 5. Verify: notifications are also gone
            long notifCountAfter = notifCollection.countDocuments(Filters.eq("cdcontract", contractId));
            if (notifCountAfter != 0) {
                System.err.println("  Esperado 0 notificações após exclusão em cascata, obteve: " + notifCountAfter);
                return false;
            }

            return true;

        } catch (Exception e) {
            System.err.println("  Exceção no Teste B: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    // ========== Test C: PDF Generation ==========

    /**
     * Teste C — Geração de PDF com dados do MongoDB.
     *
     * Verifica que ContractPdfService.generateContractPdf() não lança exceções
     * quando dados existem no banco. O PDF gerado é um efeito colateral (arquivo em disco).
     *
     * Validates: Requisito 9.4
     */
    private static boolean testPdfGeneration() {
        try {
            // First, ensure there's a contract in the DB to generate PDF for
            // We'll try to use an existing contract, or create one if needed
            ContractDAO contractDAO = new ContractDAO();
            int testContractId = -1;

            // Try to find an existing contract first
            MongoCollection<Document> contracts = Conexao.getCollection("contracts");
            Document existingContract = contracts.find().first();
            if (existingContract != null) {
                testContractId = existingContract.getInteger("_id");
            } else {
                // Create a minimal contract for this test
                Contracts c = new Contracts();
                c.setDtcreation(LocalDate.now());
                c.setDstitle("Contrato IntegTest C - PDF");
                c.setCdtemplate(1);
                c.setCdproperty(0);
                c.setCdstatus(ContractStatus.ATIVO.getCode());
                c.setDtlimit(LocalDate.now().plusYears(1));
                testContractId = contractDAO.insertContract(c);
                if (testContractId > 0) {
                    createdContractIds.add(testContractId);
                }
            }

            if (testContractId <= 0) {
                System.err.println("  Nenhum contrato disponível para gerar PDF.");
                return false;
            }

            // Create ContractPdfService with all dependencies
            ContractTemplateDAO templateDAO = new ContractTemplateDAO();
            TopicDAO topicDAO = new TopicDAO();
            ClauseDAO clauseDAO = new ClauseDAO();
            PropertyDAO propertyDAO = new PropertyDAO();
            UserDAO userDAO = new UserDAO();
            UserContractDAO userContractDAO = new UserContractDAO();
            AddressDAO addressDAO = new AddressDAO();
            InstallmentDAO installmentDAO = new InstallmentDAO();
            BankAccountDAO bankAccountDAO = new BankAccountDAO();
            IndexDAO indexDAO = new IndexDAO();
            NotaryDAO notaryDAO = new NotaryDAO();
            OccupationDAO occupationDAO = new OccupationDAO();

            ContractPdfService pdfService = new ContractPdfService(
                templateDAO, topicDAO, clauseDAO, contractDAO, propertyDAO,
                userDAO, userContractDAO, addressDAO, installmentDAO,
                bankAccountDAO, indexDAO, notaryDAO, occupationDAO
            );

            // Execute PDF generation — we just verify it doesn't throw
            pdfService.generateContractPdf(testContractId);

            // If we reach here without exception, the test passes
            return true;

        } catch (Exception e) {
            System.err.println("  Exceção no Teste C: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    // ========== Utility Methods ==========

    /**
     * Verifica a conexão com MongoDB com timeout de 3 segundos.
     */
    private static boolean checkMongoConnection() {
        try {
            com.mongodb.MongoClientSettings settings = com.mongodb.MongoClientSettings.builder()
                .applyConnectionString(new com.mongodb.ConnectionString("mongodb://localhost:27017"))
                .applyToClusterSettings(builder ->
                    builder.serverSelectionTimeout(3, TimeUnit.SECONDS))
                .applyToSocketSettings(builder ->
                    builder.connectTimeout(3, TimeUnit.SECONDS))
                .build();
            com.mongodb.client.MongoClient testClient = com.mongodb.client.MongoClients.create(settings);
            testClient.listDatabaseNames().first();
            testClient.close();
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Verifica se o MongoDB está configurado como replica set (necessário para transações).
     * Tenta iniciar uma sessão e uma transação como teste.
     */
    private static boolean checkReplicaSet() {
        try (ClientSession session = Conexao.getClient().startSession()) {
            session.startTransaction();
            session.abortTransaction();
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Limpa todos os documentos de teste criados durante a execução.
     */
    private static void cleanup() {
        System.out.println("[Cleanup] Removendo dados de teste...");
        int deleted = 0;
        try {
            // Clean up notifications
            MongoCollection<Document> notifCollection = Conexao.getCollection("notifications");
            for (int id : createdNotificationIds) {
                if (notifCollection.deleteOne(Filters.eq("_id", id)).getDeletedCount() > 0) {
                    deleted++;
                }
            }

            // Clean up contracts
            MongoCollection<Document> contractsCollection = Conexao.getCollection("contracts");
            for (int id : createdContractIds) {
                if (contractsCollection.deleteOne(Filters.eq("_id", id)).getDeletedCount() > 0) {
                    deleted++;
                }
            }

            // Clean up properties
            MongoCollection<Document> propsCollection = Conexao.getCollection("properties");
            for (int id : createdPropertyIds) {
                if (propsCollection.deleteOne(Filters.eq("_id", id)).getDeletedCount() > 0) {
                    deleted++;
                }
            }

            // Clean up test PDF file if generated
            java.io.File testPdf = new java.io.File("pdfs/contrato_preenchido_test.pdf");
            if (testPdf.exists()) {
                testPdf.delete();
            }

        } catch (Exception e) {
            System.err.println("[Cleanup] Erro durante limpeza: " + e.getMessage());
        } finally {
            System.out.println("[Cleanup] " + deleted + " documento(s) de teste removido(s).");
            Conexao.close();
        }
    }
}
