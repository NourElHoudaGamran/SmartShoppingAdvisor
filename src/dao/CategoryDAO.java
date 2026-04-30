package dao;

import database.DBConnection;
import java.sql.*;
import java.util.HashMap;
import java.util.Map;

public class CategoryDAO {
    /**
     * Récupère la somme des dépenses groupées par nom de catégorie.
     * Utilise les tables : expenses -> products -> categories
     */
    public Map<String, Double> getSpendingByCategory(int userId) {
        Map<String, Double> categoryData = new HashMap<>();

        String sql = "SELECT c.name, SUM(e.total_price) as total " +
                "FROM expenses e " +
                "JOIN products p ON e.product_id = p.id " +
                "JOIN categories c ON p.category_id = c.id " +
                "WHERE e.user_id = ? " +
                "GROUP BY c.name";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                categoryData.put(rs.getString("name"), rs.getDouble("total"));
            }
        } catch (SQLException e) {
            System.err.println("❌ Erreur CategoryDAO : " + e.getMessage());
        }
        return categoryData;
    }
}