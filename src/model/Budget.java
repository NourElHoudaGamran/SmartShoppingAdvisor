package model;

public class Budget {
    private int id;
    private int userId;
    private double monthlyBudget;

    public Budget(int userId, double monthlyBudget) {
        this.userId = userId;
        this.monthlyBudget = monthlyBudget;
    }

    public int getUserId() { return userId; }
    public double getMonthlyBudget() { return monthlyBudget; }
}