package model;

import java.util.Date;

public class User {

    private int id;
    private String name;
    private String email;
    private String password;
    private Date createdAt;

    // Constructeur complet (pour charger depuis la DB)
    public User(int id, String name, String email, String password, Date createdAt) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.password = password;
        this.createdAt = createdAt;
    }

    // Constructeur pour l'inscription (sans ID ni date, générés par la DB)
    public User(String name, String email, String password) {
        this.name = name;
        this.email = email;
        this.password = password;
    }

    // Getters
    public int getId() { return id; }
    public String getName() { return name; }
    public String getEmail() { return email; }
    public String getPassword() { return password; }
    public Date getCreatedAt() { return createdAt; }

    // Setters (utiles pour la mise à jour du profil)
    public void setId(int id) { this.id = id; }
    public void setName(String name) { this.name = name; }
    public void setEmail(String email) { this.email = email; }
    public void setPassword(String password) { this.password = password; }

    // Utile pour afficher les infos de l'utilisateur dans la console ou logs
    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", email='" + email + '\'' +
                '}';
    }
}