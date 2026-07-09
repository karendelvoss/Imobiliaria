package dao;

import model.Addresses;
import model.Users;
import org.bson.Document;

import java.time.LocalDate;
import java.util.Objects;
import java.util.Random;

/**
 * Teste de propriedade para embedding de endereço completo.
 *
 * Propriedade 7: Embedding preserva estrutura — endereço completo embarcado.
 * Para qualquer usuário ou imóvel com dados de endereço válidos (incluindo
 * district, city, state, country), o documento BSON resultante deve conter
 * o endereço completo como subdocumento embarcado com TODOS os campos de localização.
 *
 * **Validates: Requirements 2.1, 2.2**
 */
public class AddressEmbeddingPropertyTest {

    private static final int NUM_ITERATIONS = 150;
    private static final Random random = new Random();

    public static void main(String[] args) {
        System.out.println("=== Teste de Propriedade 7: Embedding preserva estrutura — endereço completo ===");
        System.out.println("Iterações: " + NUM_ITERATIONS);
        System.out.println();

        int passed = 0;
        int failed = 0;

        // --- Parte 1: AddressDAO.toDocument → fromDocument round-trip ---
        System.out.println("--- Parte 1: Round-trip AddressDAO.toDocument/fromDocument ---");
        int part1Passed = 0;
        int part1Failed = 0;

        for (int i = 0; i < NUM_ITERATIONS; i++) {
            Addresses original = generateRandomAddress();

            // Converter para Document
            Document doc = AddressDAO.toDocument(original);

            // Verificar que TODOS os campos estão presentes no documento
            String missingField = checkAllFieldsPresent(doc, original);
            if (missingField != null) {
                part1Failed++;
                if (part1Failed <= 5) {
                    System.out.println("FALHA (campos ausentes) na iteração " + (i + 1) + ": " + missingField);
                    printAddress("  Original", original);
                }
                continue;
            }

            // Converter de volta para modelo
            Addresses restored = AddressDAO.fromDocument(doc);

            // Verificar equivalência de TODOS os campos
            String diff = findDifference(original, restored);
            if (diff != null) {
                part1Failed++;
                if (part1Failed <= 5) {
                    System.out.println("FALHA (round-trip) na iteração " + (i + 1) + ": " + diff);
                    printAddress("  Original", original);
                    printAddress("  Restaurado", restored);
                }
            } else {
                part1Passed++;
            }
        }

        System.out.println("Parte 1 - Passaram: " + part1Passed + " | Falharam: " + part1Failed);
        System.out.println();

        // --- Parte 2: Embedding dentro de documento de usuário (UserDAO.toDocument) ---
        System.out.println("--- Parte 2: Endereço embarcado em UserDAO.toDocument ---");
        int part2Passed = 0;
        int part2Failed = 0;

        for (int i = 0; i < NUM_ITERATIONS; i++) {
            Addresses address = generateRandomAddress();
            Users user = generateRandomUser();

            // Criar documento de usuário com endereço embarcado
            Document userDoc = UserDAO.toDocument(user, address, "Engenheiro", null);

            // Verificar que o subdocumento address existe
            Document addrSubDoc = userDoc.get("address", Document.class);
            if (addrSubDoc == null) {
                part2Failed++;
                if (part2Failed <= 5) {
                    System.out.println("FALHA na iteração " + (i + 1) + ": subdocumento 'address' é null no user document");
                }
                continue;
            }

            // Verificar que TODOS os campos de endereço estão no subdocumento
            String missingField = checkAllFieldsPresent(addrSubDoc, address);
            if (missingField != null) {
                part2Failed++;
                if (part2Failed <= 5) {
                    System.out.println("FALHA (campos ausentes) na iteração " + (i + 1) + ": " + missingField);
                }
                continue;
            }

            // Converter o subdocumento de volta e verificar equivalência
            Addresses restoredAddr = AddressDAO.fromDocument(addrSubDoc);
            String diff = findDifference(address, restoredAddr);
            if (diff != null) {
                part2Failed++;
                if (part2Failed <= 5) {
                    System.out.println("FALHA (embedding round-trip) na iteração " + (i + 1) + ": " + diff);
                    printAddress("  Original", address);
                    printAddress("  Restaurado", restoredAddr);
                }
            } else {
                part2Passed++;
            }
        }

        System.out.println("Parte 2 - Passaram: " + part2Passed + " | Falharam: " + part2Failed);
        System.out.println();

        // --- Resultado Final ---
        passed = part1Passed + part2Passed;
        failed = part1Failed + part2Failed;
        int total = NUM_ITERATIONS * 2;

        System.out.println("=== Resultado Final ===");
        System.out.println("Total:    " + total);
        System.out.println("Passaram: " + passed);
        System.out.println("Falharam: " + failed);
        System.out.println();

        if (failed == 0) {
            System.out.println("✓ PROPRIEDADE SATISFEITA: Embedding preserva estrutura completa do endereço.");
            System.exit(0);
        } else {
            System.out.println("✗ PROPRIEDADE VIOLADA: " + failed + " caso(s) falharam.");
            System.exit(1);
        }
    }

    /**
     * Verifica se TODOS os campos esperados estão presentes no Document BSON.
     * Retorna null se tudo OK, ou uma descrição do campo ausente/incorreto.
     */
    private static String checkAllFieldsPresent(Document doc, Addresses addr) {
        if (!doc.containsKey("cdzipcode"))
            return "campo 'cdzipcode' ausente no documento";
        if (!doc.containsKey("nmstreet"))
            return "campo 'nmstreet' ausente no documento";
        if (!doc.containsKey("nraddress"))
            return "campo 'nraddress' ausente no documento";
        if (!doc.containsKey("dscomplement"))
            return "campo 'dscomplement' ausente no documento";
        if (!doc.containsKey("district"))
            return "campo 'district' ausente no documento";
        if (!doc.containsKey("city"))
            return "campo 'city' ausente no documento";
        if (!doc.containsKey("state"))
            return "campo 'state' ausente no documento";
        if (!doc.containsKey("country"))
            return "campo 'country' ausente no documento";
        return null;
    }

    /**
     * Compara dois objetos Addresses campo a campo.
     * Retorna null se equivalentes, ou descrição da diferença encontrada.
     */
    private static String findDifference(Addresses a, Addresses b) {
        if (!Objects.equals(a.getCdzipcode(), b.getCdzipcode()))
            return "cdzipcode diverge: '" + a.getCdzipcode() + "' vs '" + b.getCdzipcode() + "'";
        if (!Objects.equals(a.getNmstreet(), b.getNmstreet()))
            return "nmstreet diverge: '" + a.getNmstreet() + "' vs '" + b.getNmstreet() + "'";
        if (!Objects.equals(a.getNraddress(), b.getNraddress()))
            return "nraddress diverge: '" + a.getNraddress() + "' vs '" + b.getNraddress() + "'";
        if (!Objects.equals(a.getDscomplement(), b.getDscomplement()))
            return "dscomplement diverge: '" + a.getDscomplement() + "' vs '" + b.getDscomplement() + "'";
        if (!Objects.equals(a.getDistrict(), b.getDistrict()))
            return "district diverge: '" + a.getDistrict() + "' vs '" + b.getDistrict() + "'";
        if (!Objects.equals(a.getCity(), b.getCity()))
            return "city diverge: '" + a.getCity() + "' vs '" + b.getCity() + "'";
        if (!Objects.equals(a.getState(), b.getState()))
            return "state diverge: '" + a.getState() + "' vs '" + b.getState() + "'";
        if (!Objects.equals(a.getCountry(), b.getCountry()))
            return "country diverge: '" + a.getCountry() + "' vs '" + b.getCountry() + "'";
        return null;
    }

    /**
     * Imprime os campos de um endereço para debug.
     */
    private static void printAddress(String prefix, Addresses addr) {
        System.out.println(prefix + ": cdzipcode=\"" + addr.getCdzipcode()
                + "\", nmstreet=\"" + addr.getNmstreet()
                + "\", nraddress=\"" + addr.getNraddress()
                + "\", dscomplement=\"" + addr.getDscomplement()
                + "\", district=\"" + addr.getDistrict()
                + "\", city=\"" + addr.getCity()
                + "\", state=\"" + addr.getState()
                + "\", country=\"" + addr.getCountry() + "\"");
    }

    /**
     * Gera um objeto Addresses com dados aleatórios variados.
     * Cobre: strings normais, unicode, caracteres especiais, strings vazias, strings longas.
     */
    private static Addresses generateRandomAddress() {
        Addresses addr = new Addresses();
        addr.setCdaddress(random.nextInt(10000) + 1);
        addr.setCdzipcode(generateRandomZipcode());
        addr.setNmstreet(generateRandomStreet());
        addr.setNraddress(generateRandomNumber());
        addr.setDscomplement(generateRandomComplement());
        addr.setDistrict(generateRandomDistrict());
        addr.setCity(generateRandomCity());
        addr.setState(generateRandomState());
        addr.setCountry(generateRandomCountry());
        return addr;
    }

    /**
     * Gera um objeto Users com dados aleatórios para o teste de embedding.
     */
    private static Users generateRandomUser() {
        Users user = new Users();
        user.setCduser(random.nextInt(10000) + 1);
        user.setNmuser(generateRandomAlphanumeric(random.nextInt(30) + 3));
        user.setDtbirth(LocalDate.of(1950 + random.nextInt(50), 1 + random.nextInt(12), 1 + random.nextInt(28)));
        user.setFgdocument(random.nextBoolean());
        user.setDocument(generateRandomAlphanumeric(11));
        user.setNrcellphone(generateRandomAlphanumeric(11));
        user.setDsissuingbody(generateRandomAlphanumeric(5));
        user.setCdaddress(0);
        user.setOccupation("Teste");
        return user;
    }

    private static String generateRandomZipcode() {
        int choice = random.nextInt(6);
        switch (choice) {
            case 0: return "";
            case 1: return "89200000";
            case 2: return "01001-000";
            case 3: return generateRandomAlphanumeric(8);
            case 4: return "CEP-" + random.nextInt(99999);
            default: return String.valueOf(random.nextInt(99999999));
        }
    }

    private static String generateRandomStreet() {
        int choice = random.nextInt(7);
        switch (choice) {
            case 0: return "";
            case 1: return "Rua XV de Novembro";
            case 2: return "Av. Brasil";
            case 3: return "Rua São José dos Campos, nº especial";
            case 4: return "Straße der Einheit";
            case 5: return "日本語の通り名";
            case 6: return generateRandomAlphanumeric(random.nextInt(100) + 1);
            default: return "Rua Teste";
        }
    }

    private static String generateRandomNumber() {
        int choice = random.nextInt(6);
        switch (choice) {
            case 0: return "";
            case 1: return "S/N";
            case 2: return String.valueOf(random.nextInt(9999) + 1);
            case 3: return "1000-A";
            case 4: return "Bloco B, Apt 123";
            default: return generateRandomAlphanumeric(random.nextInt(10) + 1);
        }
    }

    private static String generateRandomComplement() {
        int choice = random.nextInt(7);
        switch (choice) {
            case 0: return "";
            case 1: return "Sala 2";
            case 2: return "Apt 101, Bloco A";
            case 3: return "Fundos";
            case 4: return "Cobertura - Último andar";
            case 5: return "Loja #2 (térreo)";
            case 6: return generateRandomAlphanumeric(random.nextInt(50) + 1);
            default: return null;
        }
    }

    private static String generateRandomDistrict() {
        int choice = random.nextInt(7);
        switch (choice) {
            case 0: return "";
            case 1: return "Centro";
            case 2: return "Vila Madalena";
            case 3: return "Bairro São José";
            case 4: return "Distrito com Acentuação: ção, ã, é";
            case 5: return "District with special chars: @#$%";
            case 6: return generateRandomAlphanumeric(random.nextInt(40) + 1);
            default: return "Bairro Padrão";
        }
    }

    private static String generateRandomCity() {
        int choice = random.nextInt(7);
        switch (choice) {
            case 0: return "";
            case 1: return "Joinville";
            case 2: return "São Paulo";
            case 3: return "Florianópolis";
            case 4: return "München";
            case 5: return "東京";
            case 6: return generateRandomAlphanumeric(random.nextInt(30) + 1);
            default: return "Cidade";
        }
    }

    private static String generateRandomState() {
        int choice = random.nextInt(7);
        switch (choice) {
            case 0: return "";
            case 1: return "SC";
            case 2: return "SP";
            case 3: return "Rio Grande do Sul";
            case 4: return "Bayern";
            case 5: return "CA";
            case 6: return generateRandomAlphanumeric(random.nextInt(20) + 1);
            default: return "Estado";
        }
    }

    private static String generateRandomCountry() {
        int choice = random.nextInt(7);
        switch (choice) {
            case 0: return "";
            case 1: return "Brasil";
            case 2: return "Deutschland";
            case 3: return "日本";
            case 4: return "United States of America";
            case 5: return "Côte d'Ivoire";
            case 6: return generateRandomAlphanumeric(random.nextInt(30) + 1);
            default: return "País";
        }
    }

    /**
     * Gera uma string alfanumérica aleatória com o comprimento dado.
     */
    private static String generateRandomAlphanumeric(int length) {
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789 -_áéíóúàèìòùãõâêîôûçñü";
        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            sb.append(chars.charAt(random.nextInt(chars.length())));
        }
        return sb.toString();
    }
}
