package dao;

import database.DBConnection;
import java.sql.*;

public class BudgetDAO {

    public double getBudgetByUserId(int userId) {
        // ✅ Utilisation du nom exact "total_budget" de ta DB
        String sql = "SELECT total_budget FROM budgets WHERE user_id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return rs.getDouble("total_budget");
            }
        } catch (SQLException e) {
            System.err.println("❌ Erreur BudgetDAO : " + e.getMessage());
        }
        return 0.0;
    }

    public void updateBudget(int userId, double newBudget) {
        String sql = "UPDATE budgets SET total_budget = ? WHERE user_id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setDouble(1, newBudget);
            stmt.setInt(2, userId);
            stmt.executeUpdate();
            System.out.println("✅ Budget SQL mis à jour.");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}