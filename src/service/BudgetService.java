package service;

import dao.*;
import java.util.Map;

public class BudgetService {

    private ExpenseDAO expenseDAO = new ExpenseDAO();
    private BudgetDAO budgetDAO = new BudgetDAO();
    private CategoryDAO categoryDAO = new CategoryDAO();

    public String getAIAdviceForGUI(int userId, String language) {
        double budget = budgetDAO.getBudgetByUserId(userId);
        double totalExpenses = expenseDAO.getTotalExpenses(userId);
        double remaining = budget - totalExpenses;

        Map<String, Double> categories = categoryDAO.getSpendingByCategory(userId);
        StringBuilder details = new StringBuilder();
        categories.forEach((name, total) -> details.append("- ").append(name).append(": ").append(total).append(" DH\n"));

        String prompt = String.format(
                "Analyse ce budget : Total %.2f DH, Dépensé %.2f DH, Reste %.2f DH.\nDetails par catégories :\n%s\nRéponds en %s.",
                budget, totalExpenses, remaining, details.toString(), language
        );

        // Appelle ton AIService existant
        return AIService.askAI(prompt, language);
    }

    public void updateAndRefreshBudget(int userId, double newAmount) {
        budgetDAO.updateBudget(userId, newAmount);
    }
}