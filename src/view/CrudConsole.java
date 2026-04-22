package view;

import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.IntConsumer;
import java.util.function.IntFunction;
import java.util.function.Supplier;

/**
 * Menu CRUD genérico para qualquer entidade. Isola o fluxo repetitivo de
 * Novo/Listar/Atualizar/Excluir, permitindo que cada view auxiliar declare
 * apenas como criar, editar e imprimir sua entidade.
 */
public final class CrudConsole {

    private CrudConsole() {}

    /** Contrato mínimo que os DAOs auxiliares satisfazem. */
    public interface SimpleCrud<T> {
        void insert(T t);
        void update(T t);
        void delete(int id);
        List<T> listAll();
    }

    /** Adapta method references de um DAO concreto ao contrato {@link SimpleCrud}. */
    public static <T> SimpleCrud<T> adapt(Consumer<T> insert, Consumer<T> update,
                                          IntConsumer delete, Supplier<List<T>> listAll) {
        return new SimpleCrud<T>() {
            @Override public void insert(T t) { insert.accept(t); }
            @Override public void update(T t) { update.accept(t); }
            @Override public void delete(int id) { delete.accept(id); }
            @Override public List<T> listAll() { return listAll.get(); }
        };
    }

    /**
     * Executa o menu CRUD padrão para uma entidade.
     *
     * @param titulo       Título do submenu
     * @param fabricaNovo  Fornece uma nova entidade já preenchida via prompts
     * @param editor       Atualiza os campos da entidade existente via prompts
     * @param finder       Busca entidade por ID
     * @param impressora   Converte entidade em string para listagem
     * @param dao          Operações de persistência
     */
    public static <T> void run(String titulo,
                               Supplier<T> fabricaNovo,
                               Consumer<T> editor,
                               IntFunction<T> finder,
                               Function<T, String> impressora,
                               SimpleCrud<T> dao) {
        System.out.println("\n[" + titulo + "] 1.Novo 2.Listar 3.Atualizar 4.Excluir 0.Voltar");
        int op = ConsoleIO.lerIntSeguro("Escolha: ");
        try {
            switch (op) {
                case 1: dao.insert(fabricaNovo.get()); break;
                case 2: dao.listAll().forEach(e -> System.out.println(impressora.apply(e))); break;
                case 3: {
                    int id = ConsoleIO.lerInt("ID para atualizar: ");
                    T alvo = finder.apply(id);
                    if (alvo == null) { 
                        System.out.println("ID não encontrado. Opções disponíveis:");
                        dao.listAll().forEach(e -> System.out.println(impressora.apply(e)));
                        break; 
                    }
                    editor.accept(alvo);
                    dao.update(alvo);
                    break;
                }
                case 4: {
                    int id = ConsoleIO.lerInt("ID para excluir: ");
                    T alvo = finder.apply(id);
                    if (alvo == null) { 
                        System.out.println("ID não encontrado. Opções disponíveis:");
                        dao.listAll().forEach(e -> System.out.println(impressora.apply(e)));
                        break; 
                    }
                    dao.delete(id);
                    break;
                }
                case 0: break;
                default: System.out.println("Opção inválida.");
            }
        } catch (Exception e) {
            System.out.println("Operação cancelada: " + e.getMessage());
        }
    }
}
