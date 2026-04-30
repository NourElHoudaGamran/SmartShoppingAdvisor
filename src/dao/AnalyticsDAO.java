package dao;

import database.DBConnection;
import java.sql.*;
import java.util.LinkedHashMap;
import java.util.Map;

public class AnalyticsDAO {

    /**
     * Récupère les dépenses groupées par jour pour l'utilisateur.
     */
    public Map<String, Double> getDailySpending(int userId) {
        Map<String, Double> dailyData = new LinkedHashMap<>();

        // Utilisation du nom de colonne 'expense_date' et 'total_price' vus sur tes images
        String sql = "SELECT CAST(expense_date AS DATE) as jour, SUM(total_price) as total " +
                "FROM expenses " +
                "WHERE user_id = ? " +
                "GROUP BY CAST(expense_date AS DATE) " +
                "ORDER BY jour ASC";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                dailyData.put(rs.getString("jour"), rs.getDouble("total"));
            }
        } catch (SQLException e) {
            System.err.println("❌ Erreur lors de la récupération des tendances.");
            e.printStackTrace();
        }
        return dailyData;
    }
}