package view;

import java.util.Scanner;
import java.util.function.IntFunction;

/**
 * Utilitário responsável por centralizar a interação de entrada e saída via console.
 */
public final class ConsoleIO {

    private static final Scanner SC = new Scanner(System.in, "UTF-8");

    private ConsoleIO() {}

    /**
     * Lê uma string do console.
     * 
     * @param label Texto de prompt.
     * @return String digitada.
     */
    public static String ler(String label) {
        System.out.print(label);
        return SC.nextLine();
    }

    /**
     * Lê um inteiro do console.
     * 
     * @param label Texto de prompt.
     * @return Inteiro digitado.
     */
    public static int lerInt(String label) {
        return Integer.parseInt(ler(label));
    }

    /**
     * Tenta ler um inteiro do console de forma segura.
     * 
     * @param label Texto de prompt.
     * @return Inteiro digitado ou -1 em caso de erro.
     */
    public static int lerIntSeguro(String label) {
        try {
            return lerInt(label);
        } catch (Exception e) {
            return -1;
        }
    }

    /**
     * Lê um double do console.
     * 
     * @param label Texto de prompt.
     * @return Double digitado.
     */
    public static double lerDouble(String label) {
        return Double.parseDouble(ler(label));
    }

    /**
     * Lê uma string do console ou mantém o valor atual se o usuário pressionar ENTER.
     * 
     * @param label Texto de prompt.
     * @param atual Valor atual.
     * @return Valor digitado ou atual.
     */
    public static String lerOuManter(String label, String atual) {
        String s = ler(label + " (" + atual + "): ");
        return s.isEmpty() ? atual : s;
    }

    /**
     * Lê um inteiro do console ou mantém o valor atual se o usuário pressionar ENTER.
     * 
     * @param label Texto de prompt.
     * @param atual Valor atual.
     * @return Valor digitado ou atual.
     */
    public static int lerIntOuManter(String label, int atual) {
        String s = ler(label + " (" + atual + "): ");
        return s.isEmpty() ? atual : Integer.parseInt(s);
    }

    /**
     * Solicita uma confirmação sim/não do usuário.
     * 
     * @param label Texto de prompt.
     * @return True se digitado 's', false caso contrário.
     */
    public static boolean confirmar(String label) {
        return ler(label).equalsIgnoreCase("s");
    }

    /**
     * Lê um ID e valida sua existência no banco de dados.
     * Permite comandos de listar, novo registro ou cancelar.
     * 
     * @param label Texto de prompt.
     * @param finder Função de busca do objeto pelo ID.
     * @param fallbackLista Ação para listar registros existentes.
     * @return ID válido ou -1 se cancelado.
     */
    public static <T> int lerIdValido(String label, IntFunction<T> finder, Runnable fallbackLista) {
        return lerIdValido(label, finder, fallbackLista, null);
    }

    /**
     * Versão estendida do lerIdValido com suporte a criação de novo registro em linha.
     * 
     * @param label Texto de prompt.
     * @param finder Função de busca do objeto pelo ID.
     * @param fallbackLista Ação para listar registros existentes.
     * @param creator Função para criar um novo registro.
     * @return ID válido ou -1 se cancelado.
     */
    public static <T> int lerIdValido(String label, IntFunction<T> finder, Runnable fallbackLista, java.util.function.Supplier<Integer> creator) {
        while (true) {
            System.out.println("\n[L] Listar | [N] Novo | [0] Cancelar");
            String input = ler(label + " (ou digite L/N/0/ID): ");
            
            if (input.isEmpty() || input.equals("0")) return -1;
            
            if (input.equalsIgnoreCase("L")) {
                if (fallbackLista != null) {
                    fallbackLista.run();
                } else {
                    System.out.println("Lista indisponível.");
                }
                continue;
            }
            
            if (input.equalsIgnoreCase("N")) {
                if (creator != null) {
                    int newId = creator.get();
                    if (newId > 0) {
                        return newId;
                    } else {
                        System.out.println("Criação cancelada ou falhou.");
                    }
                } else {
                    System.out.println("Criação de novos registros não disponível nesta tela.");
                }
                continue;
            }
            
            try {
                int id = Integer.parseInt(input);
                if (finder.apply(id) != null) return id;
                System.out.println("ERRO: ID não encontrado no sistema.");
            } catch (NumberFormatException e) {
                System.out.println("Entrada inválida. Digite um número, L, N ou 0.");
            }
        }
    }

    /**
     * Lê um ID ou mantém o atual, validando a existência se um novo for digitado.
     * 
     * @param label Texto de prompt.
     * @param atual ID atual.
     * @param finder Função de busca.
     * @param fallbackLista Ação para listar.
     * @return ID válido, atual ou novo ID criado.
     */
    public static <T> int lerIdOuManter(String label, int atual,
                                        IntFunction<T> finder, Runnable fallbackLista) {
        return lerIdOuManter(label, atual, finder, fallbackLista, null);
    }

    /**
     * Versão estendida do lerIdOuManter com suporte a criação de novo registro.
     */
    public static <T> int lerIdOuManter(String label, int atual,
                                        IntFunction<T> finder, Runnable fallbackLista, java.util.function.Supplier<Integer> creator) {
        while (true) {
            System.out.println("\n[L] Listar | [N] Novo | [ENTER] Manter atual (" + atual + ")");
            String s = ler(label + " (ou digite L/N/ID): ");
            
            if (s.isEmpty()) return atual;
            
            if (s.equalsIgnoreCase("L")) {
                if (fallbackLista != null) fallbackLista.run();
                else System.out.println("Lista indisponível.");
                continue;
            }
            
            if (s.equalsIgnoreCase("N")) {
                if (creator != null) {
                    int newId = creator.get();
                    if (newId > 0) return newId;
                } else {
                    System.out.println("Criação de novos registros não disponível nesta tela.");
                }
                continue;
            }
            
            try {
                int id = Integer.parseInt(s);
                if (finder.apply(id) != null) return id;
                System.out.println("ERRO: ID não encontrado no sistema.");
            } catch (NumberFormatException e) {
                System.out.println("Entrada inválida. Digite um número, L, N ou ENTER.");
            }
        }
    }
}
