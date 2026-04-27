package dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Fornece conexões com o banco de dados PostgreSQL.
 */
public class Conexao {

    private static final String URL = "jdbc:postgresql://localhost:5433/postgres";
    private static final String USER = "postgres";
    private static final String SENHA = "postgres";

    private Conexao() {}

    /**
     * Obtém uma nova conexão com o banco de dados.
     * 
     * @return Connection estabelecida.
     * @throws SQLException Caso ocorra erro na conexão.
     */
    public static Connection getConexao() throws SQLException {
        try {
            Class.forName("org.postgresql.Driver");
        } catch (ClassNotFoundException e) {
            throw new SQLException("Driver PostgreSQL não encontrado!", e);
        }
        
        return DriverManager.getConnection(URL, USER, SENHA);
    }
}
