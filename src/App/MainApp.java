package App;

import dao.BudgetDAO;
import dao.ExpenseDAO;
import dao.CategoryDAO;
import dao.UserDAO;
import model.User;
import model.Session;
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
import java.util.Map;
import java.util.Optional;

public class MainApp extends Application {

    // --- DAOs & Services ---
    private UserDAO userDAO = new UserDAO();
    private BudgetDAO budgetDAO = new BudgetDAO();
    private ExpenseDAO expenseDAO = new ExpenseDAO();
    private CategoryDAO categoryDAO = new CategoryDAO();
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
            // userDAO.login() contient déjà la logique de hachage du mot de passe
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

            // 1. Validation de base
            if (name.isEmpty() || email.isEmpty() || password.isEmpty()) {
                showError("Veuillez remplir tous les champs.");
                return;
            }

            // 2. Vérification email existant (Amélioration UX)
            if (userDAO.isEmailExists(email)) {
                showError("Cet email est déjà utilisé par un autre compte !");
                return;
            }

            // 3. Enregistrement (le hachage se fait dans le DAO)
            User newUser = new User(name, email, password);
            if (userDAO.register(newUser)) {
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Succès");
                alert.setHeaderText(null);
                alert.setContentText("Votre compte a été créé avec succès ! Connectez-vous maintenant.");
                alert.showAndWait(); // On attend que l'utilisateur clique sur OK

                showLoginScene();
            } else {
                showError("Erreur lors de l'inscription. Réessayez plus tard.");
            }
        });

        backLink.setOnAction(e -> showLoginScene());

        root.getChildren().addAll(title, nameField, emailField, passField, regBtn, backLink);
        primaryStage.setScene(new Scene(root, 1150, 850));
    }

    // --- TABLEAU DE BORD (DASHBOARD) ---
    public void showDashboard() {
        User currentUser = Session.getLoggedInUser();
        if (currentUser == null) {
            showLoginScene();
            return;
        }

        lblWelcome = new Label("Bienvenue, " + currentUser.getName() + " !");
        lblWelcome.setStyle("-fx-font-size: 14px; -fx-text-fill: #7f8c8d;");

        Label titleLabel = new Label("📊 Smart Shopping Advisor - Dashboard");
        titleLabel.setStyle("-fx-font-size: 28px; -fx-font-weight: bold; -fx-text-fill: #2c3e50;");

        Button logoutBtn = new Button("🚪 Déconnexion");
        logoutBtn.setOnAction(e -> {
            Session.logout();
            showLoginScene();
        });

        // --- Stats Cards ---
        lblBudget = new Label();
        lblExpenses = new Label();
        lblRemaining = new Label();

        Button btnEditBudget = new Button("✏️ Modifier");
        btnEditBudget.setStyle("-fx-font-size: 10px;");
        btnEditBudget.setOnAction(e -> handleUpdateBudget());

        VBox budgetCard = createStatCard("Budget Total", lblBudget, "#3498db");
        budgetCard.getChildren().add(btnEditBudget);

        HBox statsLayout = new HBox(20,
                budgetCard,
                createStatCard("Dépenses", lblExpenses, "#e74c3c"),
                createStatCard("Solde Restant", lblRemaining, "#2ecc71")
        );
        statsLayout.setAlignment(Pos.CENTER);

        progressBar = new ProgressBar(0);
        progressBar.setPrefWidth(700);

        // --- Zone IA & Graphique ---
        pieChart = new PieChart();
        pieChart.setPrefWidth(450);

        VBox aiBox = new VBox(15);
        aiBox.setAlignment(Pos.CENTER);

        comboLanguage = new ComboBox<>();
        comboLanguage.getItems().addAll("Français", "Arabe", "Darija", "English");
        comboLanguage.setValue("Français");

        aiResultArea = new TextArea();
        aiResultArea.setPrefSize(450, 300);
        aiResultArea.setWrapText(true);
        aiResultArea.setEditable(false);

        Button aiButton = new Button("🤖 Lancer l'Analyse IA");
        aiButton.setStyle("-fx-background-color: #9b59b6; -fx-text-fill: white; -fx-font-weight: bold;");

        ProgressIndicator loading = new ProgressIndicator();
        loading.setVisible(false);

        aiBox.getChildren().addAll(new Label("Choisir la langue :"), comboLanguage, aiButton, loading, aiResultArea);

        HBox centralLayout = new HBox(40, pieChart, aiBox);
        centralLayout.setAlignment(Pos.CENTER);

        refreshData();

        aiButton.setOnAction(e -> {
            aiButton.setDisable(true);
            loading.setVisible(true);
            aiResultArea.setText("🔄 Analyse en cours...");

            Task<String> aiTask = new Task<>() {
                @Override
                protected String call() {
                    return budgetService.getAIAdviceForGUI(currentUser.getId(), comboLanguage.getValue());
                }
            };

            aiTask.setOnSucceeded(event -> {
                aiResultArea.setText(aiTask.getValue());
                loading.setVisible(false);
                aiButton.setDisable(false);
            });

            aiTask.setOnFailed(event -> {
                showError("Erreur lors de l'analyse IA.");
                loading.setVisible(false);
                aiButton.setDisable(false);
            });

            new Thread(aiTask).start();
        });

        VBox mainLayout = new VBox(20, logoutBtn, lblWelcome, titleLabel, statsLayout, progressBar, centralLayout);
        mainLayout.setPadding(new Insets(20));
        mainLayout.setAlignment(Pos.TOP_CENTER);

        primaryStage.setTitle("Smart Shopping Advisor v1.0 - " + currentUser.getName());
        primaryStage.setScene(new Scene(mainLayout, 1150, 850));
    }

    private void handleUpdateBudget() {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Mise à jour");
        dialog.setHeaderText("Nouveau budget mensuel (DH) :");
        Optional<String> result = dialog.showAndWait();
        result.ifPresent(amount -> {
            try {
                double val = Double.parseDouble(amount);
                budgetService.updateAndRefreshBudget(Session.getLoggedInUser().getId(), val);
                refreshData();
            } catch (NumberFormatException e) {
                showError("Montant invalide !");
            }
        });
    }

    private void refreshData() {
        if (Session.getLoggedInUser() == null) return;

        int uid = Session.getLoggedInUser().getId();
        double budgetTotal = budgetDAO.getBudgetByUserId(uid);
        double depensesTotales = expenseDAO.getTotalExpenses(uid);
        double reste = budgetTotal - depensesTotales;
        double ratio = (budgetTotal > 0) ? (depensesTotales / budgetTotal) : 0;

        lblBudget.setText(String.format("%.2f DH", budgetTotal));
        lblExpenses.setText(String.format("%.2f DH", depensesTotales));
        lblRemaining.setText(String.format("%.2f DH", reste));
        progressBar.setProgress(ratio);
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
        card.setStyle("-fx-background-color: " + color + "; -fx-background-radius: 10; -fx-text-fill: white; -fx-font-weight: bold;");
        return card;
    }

    private void showError(String msg) {
        Alert alert = new Alert(Alert.AlertType.ERROR, msg);
        alert.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}