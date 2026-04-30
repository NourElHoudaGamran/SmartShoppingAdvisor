package dao;

import database.DBConnection;
import model.Expense;
import java.sql.*;

public class ExpenseDAO {

    public void addExpense(Expense e) {
        String sql = "INSERT INTO expenses (user_id, product_id, quantity, total_price) VALUES (?, ?, ?, ?)";

        try (Connection conn = DBConnection.getConnection()) {
            if (conn == null) return;

            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setInt(1, e.getUserId());
                stmt.setInt(2, e.getProductId());
                stmt.setInt(3, e.getQuantity());
                stmt.setDouble(4, e.getTotalPrice());
                stmt.executeUpdate();
                System.out.println("💸 Dépense ajoutée avec succès.");
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    public double getTotalExpenses(int userId) {
        String sql = "SELECT SUM(total_price) FROM expenses WHERE user_id = ?";
        double total = 0;

        try (Connection conn = DBConnection.getConnection()) {
            if (conn == null) return 0.0;

            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setInt(1, userId);
                ResultSet rs = stmt.executeQuery();
                if (rs.next()) {
                    total = rs.getDouble(1);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return total;
    }

    public void getExpensesByCategory(int userId) {
        String sql = """
            SELECT c.name, SUM(e.total_price) AS total
            FROM expenses e
            JOIN products p ON e.product_id = p.id
            JOIN categories c ON p.category_id = c.id
            WHERE e.user_id = ?
            GROUP BY c.name
        """;

        try (Connection conn = DBConnection.getConnection()) {
            if (conn == null) return;

            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setInt(1, userId);
                ResultSet rs = stmt.executeQuery();
                System.out.println("\n📊 --- RÉPARTITION PAR CATÉGORIE ---");
                while (rs.next()) {
                    System.out.println("📍 " + rs.getString("name") + " : " + rs.getDouble("total") + " DH");
                }
            }
        } catch (SQLException e) {
            System.err.println("❌ Erreur lors de la récupération par catégorie");
            e.printStackTrace();
        }
    }
}