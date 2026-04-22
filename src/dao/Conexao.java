package dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Classe utilitária para fornecer conexões com o banco de dados PostgreSQL.
 * Utiliza um método estático para que não seja necessário instanciar a classe.
 */
public class Conexao {

    // 1. Dados da conexão são constantes e privados
    private static final String URL = "jdbc:postgresql://localhost:5432/imobiliaria3";
    private static final String USER = "postgres";
    private static final String SENHA = "postgres";

    // 2. Construtor privado para impedir que alguém crie um `new Conexao()`
    private Conexao() {}

    /**
     * Obtém uma nova conexão com o banco de dados a cada chamada.
     * A responsabilidade de fechar a conexão é de quem a solicitou.
     * 
     * @return um objeto Connection com a conexão estabelecida.
     * @throws SQLException se a conexão falhar.
     */
    public static Connection getConexao() throws SQLException {
        // Opcional, mas boa prática: garantir que o driver está carregado
        try {
            Class.forName("org.postgresql.Driver");
        } catch (ClassNotFoundException e) {
            throw new SQLException("Driver PostgreSQL não encontrado!", e);
        }
        
        // Retorna uma nova conexão
        return DriverManager.getConnection(URL, USER, SENHA);
    }
}

//C:\Users\kriba\Music\ExemploUsoAPI\ExemploUsoAPI\dist\lib\postgresql-42.7.4.jar
