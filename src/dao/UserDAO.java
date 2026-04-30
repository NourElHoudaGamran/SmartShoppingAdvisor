package dao;

import database.DBConnection;
import model.User;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UserDAO {

    /**
     * Hache le mot de passe pour la sécurité.
     * On utilise Integer.toHexString(password.hashCode()) comme tu l'as suggéré.
     */
    private String hashPassword(String password) {
        if (password == null) return null;
        return Integer.toHexString(password.hashCode());
    }

    /**
     * Vérifie les identifiants pour la connexion.
     */
    public User login(String email, String password) {
        // IMPORTANT : On hache le mot de passe saisi pour le comparer au hash en base
        String hashedPassword = hashPassword(password);

        String sql = "SELECT id, name, email, password, created_at FROM users WHERE email = ? AND password = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, email);
            stmt.setString(2, hashedPassword);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return new User(
                            rs.getInt("id"),
                            rs.getString("name"),
                            rs.getString("email"),
                            rs.getString("password"),
                            rs.getTimestamp("created_at")
                    );
                }
            }
        } catch (SQLException e) {
            System.err.println("❌ Erreur lors du login : " + e.getMessage());
        }
        return null;
    }

    /**
     * Inscrit un nouvel utilisateur avec mot de passe haché.
     */
    public boolean register(User user) {
        // IMPORTANT : On hache le mot de passe avant de l'insérer
        String hashedPassword = hashPassword(user.getPassword());

        String sql = "INSERT INTO users (name, email, password, created_at) VALUES (?, ?, ?, GETDATE())";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, user.getName());
            stmt.setString(2, user.getEmail());
            stmt.setString(3, hashedPassword);

            int rowsInserted = stmt.executeUpdate();
            if (rowsInserted > 0) {
                System.out.println("✅ Utilisateur inscrit avec succès (Mot de passe sécurisé) !");
                return true;
            }
        } catch (SQLException e) {
            System.err.println("❌ Erreur lors de l'inscription : " + e.getMessage());
        }
        return false;
    }

    /**
     * Vérifie si un email existe déjà pour éviter les doublons.
     */
    public boolean isEmailExists(String email) {
        String sql = "SELECT COUNT(*) FROM users WHERE email = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, email);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Récupère tous les utilisateurs (optionnel).
     */
    public List<User> getAllUsers() {
        List<User> users = new ArrayList<>();
        String sql = "SELECT * FROM users";

        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                users.add(new User(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getString("email"),
                        rs.getString("password"),
                        rs.getTimestamp("created_at")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return users;
    }
}