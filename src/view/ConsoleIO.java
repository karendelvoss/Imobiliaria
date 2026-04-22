package view;

import java.util.Scanner;
import java.util.function.IntFunction;

/**
 * Responsável por toda a interação de entrada/saída com o console.
 * Centraliza parsing, leitura com valor default e validação interativa de IDs.
 */
public final class ConsoleIO {

    private static final Scanner SC = new Scanner(System.in, "UTF-8");

    private ConsoleIO() {}

    public static String ler(String label) {
        System.out.print(label);
        return SC.nextLine();
    }

    public static int lerInt(String label) {
        return Integer.parseInt(ler(label));
    }

    public static int lerIntSeguro(String label) {
        try { return lerInt(label); } catch (Exception e) { return -1; }
    }

    public static double lerDouble(String label) {
        return Double.parseDouble(ler(label));
    }

    public static String lerOuManter(String label, String atual) {
        String s = ler(label + " (" + atual + "): ");
        return s.isEmpty() ? atual : s;
    }

    public static int lerIntOuManter(String label, int atual) {
        String s = ler(label + " (" + atual + "): ");
        return s.isEmpty() ? atual : Integer.parseInt(s);
    }

    public static boolean confirmar(String label) {
        return ler(label).equalsIgnoreCase("s");
    }

    /**
     * Lê um ID validando a existência via {@code finder}. Retorna -1 se o
     * usuário cancelar digitando 0 ou ENTER. Se o ID não existir, oferece
     * imprimir uma lista de apoio via {@code fallbackLista}.
     */
    public static <T> int lerIdValido(String label, IntFunction<T> finder, Runnable fallbackLista) {
        while (true) {
            String input = ler(label + " (0 para cancelar): ");
            if (input.isEmpty() || input.equals("0")) return -1;
            try {
                int id = Integer.parseInt(input);
                if (finder.apply(id) != null) return id;
                if (fallbackLista != null && confirmar("ERRO: não encontrado. Ver lista? (s/n): ")) {
                    fallbackLista.run();
                }
            } catch (NumberFormatException e) {
                System.out.println("Entrada inválida.");
            }
        }
    }

    /** Variante de leitura de ID que mantém o valor atual ao pressionar ENTER. */
    public static <T> int lerIdOuManter(String label, int atual,
                                        IntFunction<T> finder, Runnable fallbackLista) {
        while (true) {
            String s = ler(label + " (" + atual + "): ");
            if (s.isEmpty()) return atual;
            try {
                int id = Integer.parseInt(s);
                if (finder.apply(id) != null) return id;
                if (fallbackLista != null && confirmar("ERRO: não encontrado. Ver lista? (s/n): ")) {
                    fallbackLista.run();
                }
            } catch (NumberFormatException e) {
                System.out.println("Entrada inválida.");
            }
        }
    }
}
