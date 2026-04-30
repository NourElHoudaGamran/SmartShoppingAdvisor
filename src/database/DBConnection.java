package database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnection {

    private static final String URL = "jdbc:sqlserver://localhost\\SQLEXPRESS;databaseName=SmartShoppingDB;encrypt=true;trustServerCertificate=true;";
    private static final String USER = "sa";
    private static final String PASSWORD = "1234";

    public static Connection getConnection() {
        try {
            // Force le chargement du driver
            Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
            return DriverManager.getConnection(URL, USER, PASSWORD);
        } catch (ClassNotFoundException e) {
            System.err.println("❌ Driver SQL Server manquant ! Vérifie ton pom.xml");
            return null;
        } catch (SQLException e) {
            System.err.println("❌ Erreur SQL : " + e.getMessage() + " | State: " + e.getSQLState());
            return null;
        }
    }
}