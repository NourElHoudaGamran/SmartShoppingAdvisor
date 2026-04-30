package model;

import java.util.Date;

public class Expense {
    private int id;
    private int userId;
    private int productId;
    private int quantity;
    private double totalPrice;
    private Date date;

    // Constructeur pour l'ajout
    public Expense(int userId, int productId, int quantity, double totalPrice) {
        this.userId = userId;
        this.productId = productId;
        this.quantity = quantity;
        this.totalPrice = totalPrice;
    }

    // Getters
    public int getUserId() { return userId; }
    public int getProductId() { return productId; }
    public int getQuantity() { return quantity; }
    public double getTotalPrice() { return totalPrice; }
}