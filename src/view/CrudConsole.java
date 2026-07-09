package view;

import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.IntConsumer;
import java.util.function.IntFunction;
import java.util.function.Supplier;

/**
 * Utilitário genérico para execução de operações CRUD via console.
 */
public final class CrudConsole {

    private CrudConsole() {}

    /**
     * Interface simplificada para operações CRUD.
     */
    public interface SimpleCrud<T> {
        void insert(T t);
        void update(T t);
        void delete(int id);
        List<T> listAll();
    }

    /**
     * Adapta referências de métodos para a interface SimpleCrud.
     * 
     * @param insert Função de inserção.
     * @param update Função de atualização.
     * @param delete Função de exclusão.
     * @param listAll Função de listagem.
     * @return Implementação de SimpleCrud.
     */
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
     * Executa o fluxo de menu CRUD para uma determinada entidade.
     * 
     * @param titulo Título do menu.
     * @param fabricaNovo Fornecedor de nova instância da entidade.
     * @param editor Consumidor para edição da entidade.
     * @param finder Função de busca por ID.
     * @param impressora Função de formatação para exibição.
     * @param dao Implementação de SimpleCrud.
     */
    public static <T> void run(String titulo,
                               Supplier<T> fabricaNovo,
                               Consumer<T> editor,
                               IntFunction<T> finder,
                               Function<T, String> impressora,
                               SimpleCrud<T> dao
                            ) {
        int op = -1;
        while (op != 0) {
            System.out.println("\n[" + titulo + "] 1.Novo 2.Listar 3.Atualizar 4.Excluir 0.Voltar");
            op = ConsoleIO.lerIntSeguro("Escolha: ");
            try {
                switch (op) {
                    case 1: dao.insert(fabricaNovo.get()); break;
                    case 2: {
                        List<T> items = dao.listAll();
                        if (items.isEmpty()) {
                            System.out.println("\nNenhum registro cadastrado.");
                            if (ConsoleIO.confirmar("Deseja cadastrar um novo? (s/n): ")) {
                                dao.insert(fabricaNovo.get());
                            }
                        } else {
                            items.forEach(e -> System.out.println(impressora.apply(e)));
                        }
                        break;
                    }
                    case 3: {
                        int id = ConsoleIO.lerInt("ID para atualizar: ");
                        T alvo = finder.apply(id);
                        if (alvo == null) { 
                            System.out.println("ID não encontrado. Opções disponíveis:");
                            List<T> items = dao.listAll();
                            if (items.isEmpty()) {
                                System.out.println("Nenhum registro cadastrado.");
                            } else {
                                items.forEach(e -> System.out.println(impressora.apply(e)));
                            }
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
                            List<T> items = dao.listAll();
                            if (items.isEmpty()) {
                                System.out.println("Nenhum registro cadastrado.");
                            } else {
                                items.forEach(e -> System.out.println(impressora.apply(e)));
                            }
                            break; 
                        }
                        if (ConsoleIO.confirmar("Confirmar exclusão? (s/n): ")) {
                            dao.delete(id);
                            System.out.println("Registro excluído!");
                        }
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
}
