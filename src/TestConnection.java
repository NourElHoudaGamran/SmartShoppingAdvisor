import database.DBConnection;
import java.sql.SQLException;

public class TestConnection {
    public static void main(String[] args) {
        System.out.println("🚀 Démarrage du test de connexion...");

        // Le try(...) ferme automatiquement la ressource à la fin du bloc
        try (var connection = DBConnection.getConnection()) {

            if (connection != null && !connection.isClosed()) {
                System.out.println("✨ Test réussi : La base de données est prête !");
                System.out.println("📦 Catalogue actuel : " + connection.getCatalog());
            } else {
                System.out.println("⚠️ La connexion a échoué (objet null ou fermé).");
            }

        } catch (SQLException e) {
            System.err.println("❌ Erreur lors du test : " + e.getMessage());
        }
    }
}