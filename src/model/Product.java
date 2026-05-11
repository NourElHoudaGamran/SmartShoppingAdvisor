package model;

/**
 * Représente un produit dans le système Smart Shopping Advisor.
 */
public class Product {

    private int id;
    private String name;
    private double price;
    private int categoryId;

    // Constructeur complet (utile pour la récupération depuis la base de données)
    public Product(int id, String name, double price, int categoryId) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.categoryId = categoryId;
    }

    // Constructeur sans ID (utile pour l'insertion de nouveaux produits)
    public Product(String name, double price, int categoryId) {
        this.name = name;
        this.price = price;
        this.categoryId = categoryId;
    }

    // --- Getters ---
    public int getId() { return id; }
    public String getName() { return name; }
    public double getPrice() { return price; }
    public int getCategoryId() { return categoryId; }

    // --- Setters (Optionnels mais recommandés) ---
    public void setId(int id) { this.id = id; }
    public void setName(String name) { this.name = name; }
    public void setPrice(double price) { this.price = price; }
    public void setCategoryId(int categoryId) { this.categoryId = categoryId; }

    /**
     * CRITIQUE : Cette méthode permet à JavaFX d'afficher
     * correctement le produit dans les listes déroulantes (ChoiceDialog).
     */
    @Override
    public String toString() {
        return name + " (" + String.format("%.2f", price) + " DH)";
    }
}