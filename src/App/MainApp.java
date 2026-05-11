package App;

import dao.*;
import model.*;
import service.BudgetService;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.chart.PieChart;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class MainApp extends Application {

    // --- DAOs & Services ---
    private UserDAO userDAO = new UserDAO();
    private BudgetDAO budgetDAO = new BudgetDAO();
    private ExpenseDAO expenseDAO = new ExpenseDAO();
    private CategoryDAO categoryDAO = new CategoryDAO();
    private ProductDAO productDAO = new ProductDAO(); // Nouveau DAO pour les produits
    private BudgetService budgetService = new BudgetService();

    // --- Éléments d'interface ---
    private Label lblBudget, lblExpenses, lblRemaining, lblWelcome;
    private ProgressBar progressBar;
    private PieChart pieChart;
    private TextArea aiResultArea;
    private ComboBox<String> comboLanguage;

    private Stage primaryStage;

    @Override
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;
        showLoginScene();
    }

    // --- ÉCRAN DE CONNEXION ---
    public void showLoginScene() {
        VBox root = new VBox(15);
        root.setAlignment(Pos.CENTER);
        root.setPadding(new Insets(50));
        root.setStyle("-fx-background-color: #ecf0f1;");

        Label title = new Label("Connexion");
        title.setStyle("-fx-font-size: 26px; -fx-font-weight: bold; -fx-text-fill: #2c3e50;");

        TextField emailField = new TextField();
        emailField.setPromptText("Email");
        emailField.setMaxWidth(300);

        PasswordField passField = new PasswordField();
        passField.setPromptText("Mot de passe");
        passField.setMaxWidth(300);

        Button loginBtn = new Button("Se connecter");
        loginBtn.setStyle("-fx-background-color: #3498db; -fx-text-fill: white; -fx-font-weight: bold;");
        loginBtn.setMinWidth(300);

        Hyperlink registerLink = new Hyperlink("Pas encore de compte ? S'inscrire ici");

        loginBtn.setOnAction(e -> {
            User user = userDAO.login(emailField.getText(), passField.getText());
            if (user != null) {
                Session.setLoggedInUser(user);
                showDashboard();
            } else {
                showError("Email ou mot de passe incorrect.");
            }
        });

        registerLink.setOnAction(e -> showRegisterScene());

        root.getChildren().addAll(title, emailField, passField, loginBtn, registerLink);
        primaryStage.setScene(new Scene(root, 1150, 850));
        primaryStage.setTitle("Smart Shopping Advisor - Login");
        primaryStage.show();
    }

    // --- ÉCRAN D'INSCRIPTION ---
    public void showRegisterScene() {
        VBox root = new VBox(15);
        root.setAlignment(Pos.CENTER);
        root.setPadding(new Insets(50));
        root.setStyle("-fx-background-color: #f4f7f6;");

        Label title = new Label("Créer un compte");
        title.setStyle("-fx-font-size: 24px; -fx-font-weight: bold;");

        TextField nameField = new TextField();
        nameField.setPromptText("Nom complet");
        nameField.setMaxWidth(300);

        TextField emailField = new TextField();
        emailField.setPromptText("Email");
        emailField.setMaxWidth(300);

        PasswordField passField = new PasswordField();
        passField.setPromptText("Mot de passe");
        passField.setMaxWidth(300);

        Button regBtn = new Button("S'inscrire");
        regBtn.setStyle("-fx-background-color: #2ecc71; -fx-text-fill: white; -fx-font-weight: bold;");
        regBtn.setMinWidth(300);

        Hyperlink backLink = new Hyperlink("Déjà un compte ? Se connecter");

        regBtn.setOnAction(e -> {
            String name = nameField.getText();
            String email = emailField.getText();
            String password = passField.getText();

            if (name.isEmpty() || email.isEmpty() || password.isEmpty()) {
                showError("Veuillez remplir tous les champs.");
                return;
            }

            if (userDAO.isEmailExists(email)) {
                showError("Cet email est déjà utilisé !");
                return;
            }

            User newUser = new User(name, email, password);
            if (userDAO.register(newUser)) {
                showLoginScene();
            } else {
                showError("Erreur lors de l'inscription.");
            }
        });

        backLink.setOnAction(e -> showLoginScene());
        root.getChildren().addAll(title, nameField, emailField, passField, regBtn, backLink);
        primaryStage.setScene(new Scene(root, 1150, 850));
    }

    // --- TABLEAU DE BORD (DASHBOARD) ---
    public void showDashboard() {
        User currentUser = Session.getLoggedInUser();
        if (currentUser == null) { showLoginScene(); return; }

        lblWelcome = new Label("Bienvenue, " + currentUser.getName() + " !");
        Label titleLabel = new Label("📊 Smart Shopping Advisor - Dashboard");
        titleLabel.setStyle("-fx-font-size: 28px; -fx-font-weight: bold; -fx-text-fill: #2c3e50;");

        Button logoutBtn = new Button("🚪 Déconnexion");
        logoutBtn.setOnAction(e -> { Session.logout(); showLoginScene(); });

        // --- Cartes Statistiques ---
        lblBudget = new Label();
        lblExpenses = new Label();
        lblRemaining = new Label();

        Button btnEditBudget = new Button("✏️ Modifier");
        btnEditBudget.setOnAction(e -> handleUpdateBudget());

        VBox budgetCard = createStatCard("Budget Total", lblBudget, "#3498db");
        budgetCard.getChildren().add(btnEditBudget);

        HBox statsLayout = new HBox(20,
                budgetCard,
                createStatCard("Dépenses", lblExpenses, "#e74c3c"),
                createStatCard("Solde Restant", lblRemaining, "#2ecc71")
        );
        statsLayout.setAlignment(Pos.CENTER);

        // --- Bouton d'ajout de dépense ---
        Button btnAddExpense = new Button("➕ Ajouter une dépense");
        btnAddExpense.setStyle("-fx-background-color: #e67e22; -fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 14px;");
        btnAddExpense.setOnAction(e -> handleAddExpense());

        progressBar = new ProgressBar(0);
        progressBar.setPrefWidth(700);

        // --- Graphique & IA ---
        pieChart = new PieChart();
        pieChart.setPrefWidth(450);

        VBox aiBox = new VBox(15);
        aiBox.setAlignment(Pos.CENTER);

        comboLanguage = new ComboBox<>();
        comboLanguage.getItems().addAll("Français", "Arabe", "Darija", "English");
        comboLanguage.setValue("Français");

        aiResultArea = new TextArea();
        aiResultArea.setPrefSize(450, 300);
        aiResultArea.setEditable(false);
        aiResultArea.setWrapText(true);

        Button aiButton = new Button("🤖 Lancer l'Analyse IA");
        aiButton.setStyle("-fx-background-color: #9b59b6; -fx-text-fill: white; -fx-font-weight: bold;");

        ProgressIndicator loading = new ProgressIndicator();
        loading.setVisible(false);

        aiButton.setOnAction(e -> {
            loading.setVisible(true);
            aiButton.setDisable(true);
            Task<String> task = new Task<>() {
                @Override protected String call() {
                    return budgetService.getAIAdviceForGUI(currentUser.getId(), comboLanguage.getValue());
                }
            };
            task.setOnSucceeded(ev -> {
                aiResultArea.setText(task.getValue());
                loading.setVisible(false);
                aiButton.setDisable(false);
            });
            new Thread(task).start();
        });

        aiBox.getChildren().addAll(new Label("Choisir la langue :"), comboLanguage, aiButton, loading, aiResultArea);

        HBox centralLayout = new HBox(40, pieChart, aiBox);
        centralLayout.setAlignment(Pos.CENTER);

        refreshData();

        VBox mainLayout = new VBox(20, logoutBtn, lblWelcome, titleLabel, statsLayout, btnAddExpense, progressBar, centralLayout);
        mainLayout.setPadding(new Insets(20));
        mainLayout.setAlignment(Pos.TOP_CENTER);

        primaryStage.setScene(new Scene(mainLayout, 1150, 850));
    }

    // --- LOGIQUE D'AJOUT DE DÉPENSE ---
    private void handleAddExpense() {
        List<Product> products = productDAO.getAllProducts();
        if (products.isEmpty()) {
            showError("Aucun produit disponible en base.");
            return;
        }

        ChoiceDialog<Product> dialog = new ChoiceDialog<>(products.get(0), products);
        dialog.setTitle("Nouvelle Dépense");
        dialog.setHeaderText("Sélectionnez un article");
        dialog.setContentText("Produit :");

        Optional<Product> result = dialog.showAndWait();
        result.ifPresent(product -> {
            TextInputDialog qtyDialog = new TextInputDialog("1");
            qtyDialog.setTitle("Quantité");
            qtyDialog.setHeaderText("Combien d'unités de " + product.getName() + " ?");
            qtyDialog.setContentText("Quantité :");

            qtyDialog.showAndWait().ifPresent(qtyStr -> {
                try {
                    int qty = Integer.parseInt(qtyStr);
                    double total = product.getPrice() * qty;

                    expenseDAO.addExpense(new Expense(
                            Session.getLoggedInUser().getId(),
                            product.getId(),
                            qty,
                            total
                    ));
                    refreshData();
                } catch (NumberFormatException e) {
                    showError("Quantité invalide.");
                }
            });
        });
    }

    private void handleUpdateBudget() {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Budget");
        dialog.setHeaderText("Nouveau budget mensuel (DH) :");
        dialog.showAndWait().ifPresent(amount -> {
            try {
                budgetService.updateAndRefreshBudget(Session.getLoggedInUser().getId(), Double.parseDouble(amount));
                refreshData();
            } catch (Exception e) { showError("Montant invalide."); }
        });
    }

    private void refreshData() {
        int uid = Session.getLoggedInUser().getId();
        double budgetTotal = budgetDAO.getBudgetByUserId(uid);
        double depensesTotales = expenseDAO.getTotalExpenses(uid);
        double reste = budgetTotal - depensesTotales;

        lblBudget.setText(String.format("%.2f DH", budgetTotal));
        lblExpenses.setText(String.format("%.2f DH", depensesTotales));
        lblRemaining.setText(String.format("%.2f DH", reste));
        progressBar.setProgress(budgetTotal > 0 ? depensesTotales / budgetTotal : 0);

        updatePieChartData();
    }

    private void updatePieChartData() {
        Map<String, Double> data = categoryDAO.getSpendingByCategory(Session.getLoggedInUser().getId());
        ObservableList<PieChart.Data> pieChartData = FXCollections.observableArrayList();
        data.forEach((cat, val) -> pieChartData.add(new PieChart.Data(cat, val)));
        pieChart.setData(pieChartData);
    }

    private VBox createStatCard(String title, Label valueLabel, String color) {
        VBox card = new VBox(5, new Label(title), valueLabel);
        card.setPadding(new Insets(15));
        card.setPrefWidth(250);
        card.setAlignment(Pos.CENTER);
        card.setStyle("-fx-background-color: " + color + "; -fx-background-radius: 10; -fx-text-fill: #ffffff; -fx-font-weight: bold;");
        return card;
    }

    private void showError(String msg) {
        new Alert(Alert.AlertType.ERROR, msg).show();
    }

    public static void main(String[] args) { launch(args); }
}