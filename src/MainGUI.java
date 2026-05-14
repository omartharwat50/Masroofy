import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.Screen;
import javafx.stage.Stage;

public class MainGUI extends Application {

    private CycleUI cycleUI;
    private ExpenseUI expenseUI;

    private CycleController cycleController;
    private ExpenseController expenseController;

    // ===== THEME COLORS =====
    private static final String DARK_NAVY    = "#0D1B2A";
    private static final String NAVY         = "#1B2A3B";
    private static final String SIDEBAR_BG   = "#102030";
    private static final String ACCENT_BLUE  = "#1E90FF";
    private static final String LIGHT_BLUE   = "#4DB8FF";
    private static final String TEXT_WHITE   = "#F0F8FF";
    private static final String TEXT_MUTED   = "#90B8D8";
    private static final String BTN_HOVER    = "#2563EB";

    @Override
    public void start(Stage stage) {
        try {
            // ================= INITIALIZE BACKEND =================
            DatabaseManager db = new DatabaseManager();
            NotificationService notificationService = new NotificationService();

            CycleService cycleService = new CycleService(db);
            ExpenseService expenseService = new ExpenseService(db, db, db, notificationService);

            cycleController = new CycleController(cycleService, db);
            expenseController = new ExpenseController(expenseService, db, db);

            cycleUI = new CycleUI(cycleController);
            expenseUI = new ExpenseUI(expenseController);

            // ================= LAYOUT =================
            VBox sidebar = buildSidebar(stage);
            VBox contentArea = buildDynamicDashboard();

            HBox root = new HBox(sidebar, contentArea);
            HBox.setHgrow(contentArea, Priority.ALWAYS);
            root.setStyle("-fx-background-color: " + DARK_NAVY + ";");

            Scene scene = new Scene(root, 1250, 720);
            stage.setTitle("💰 Budget Tracker");
            stage.setScene(scene);
            stage.setMaximized(true);
            stage.show();

        } catch (Exception e) {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR, "Failed to start application:\n" + e.getMessage());
            alert.showAndWait();
        }
    }

    // ================= DYNAMIC DASHBOARD =================
    private VBox buildDynamicDashboard() {
        VBox content = new VBox(25);
        content.setPadding(new Insets(30));
        content.setStyle("-fx-background-color: " + DARK_NAVY + ";");

        // Header
        Label greeting = new Label("Welcome back 👋");
        greeting.setStyle("-fx-font-size: 14px; -fx-text-fill: " + TEXT_MUTED + ";");

        Label title = new Label("Personal Budget Dashboard");
        title.setStyle("-fx-font-size: 28px; -fx-font-weight: bold; -fx-text-fill: " + TEXT_WHITE + ";");

        Button refreshBtn = new Button("🔄 Refresh Data");
        refreshBtn.setStyle("-fx-background-color: #1E3A5F; -fx-text-fill: white; -fx-font-size: 13px;");
        refreshBtn.setOnAction(e -> refreshDashboard(content));

        HBox header = new HBox(15, greeting, title, new Region(), refreshBtn);
        HBox.setHgrow(header.getChildren().get(2), Priority.ALWAYS);

        content.getChildren().addAll(header, createSummaryCards());

        return content;
    }

    private HBox createSummaryCards() {
        HBox cards = new HBox(20);

        try {
            Cycle currentCycle = cycleController.getCurrentCycle();
            double totalSpent = expenseController.getTotalSpentThisCycle();

            double budget = (currentCycle != null) ? currentCycle.getTotalBudget() : 0.0;
            double remaining = budget - totalSpent;

            cards.getChildren().addAll(
                createStatCard("💰 Total Budget", String.format("$%.2f", budget), ACCENT_BLUE),
                createStatCard("📤 Total Spent", String.format("$%.2f", totalSpent), "#FF9500"),
                createStatCard("📉 Remaining", String.format("$%.2f", remaining), 
                    remaining >= 0 ? "#34C759" : "#FF3B30")
            );
        } catch (Exception e) {
            Label error = new Label("Error loading dashboard data: " + e.getMessage());
            error.setStyle("-fx-text-fill: #ff6b6b; -fx-font-size: 14px;");
            cards.getChildren().add(error);
        }

        return cards;
    }

    private VBox createStatCard(String title, String value, String color) {
        VBox card = new VBox(8);
        card.setPadding(new Insets(20));
        card.setStyle(
            "-fx-background-color: " + NAVY + ";" +
            "-fx-background-radius: 12;" +
            "-fx-border-color: #1E3A5F;" +
            "-fx-border-radius: 12;"
        );

        Label titleLbl = new Label(title);
        titleLbl.setStyle("-fx-font-size: 13px; -fx-text-fill: " + TEXT_MUTED + ";");

        Label valueLbl = new Label(value);
        valueLbl.setStyle("-fx-font-size: 26px; -fx-font-weight: bold; -fx-text-fill: " + color + ";");

        card.getChildren().addAll(titleLbl, valueLbl);
        return card;
    }

    private void refreshDashboard(VBox content) {
        content.getChildren().remove(1); // remove old cards
        content.getChildren().add(1, createSummaryCards());
    }

    // ================= SIDEBAR =================
    private VBox buildSidebar(Stage stage) {
        // Logo
        Label logo = new Label("💰");
        logo.setStyle("-fx-font-size: 42px;");

        Label appName = new Label("Budget\nTracker");
        appName.setStyle("-fx-font-size: 22px; -fx-font-weight: bold; -fx-text-fill: " + TEXT_WHITE + "; -fx-text-alignment: center;");

        Label version = new Label("Personal Finance Hub");
        version.setStyle("-fx-font-size: 11px; -fx-text-fill: " + TEXT_MUTED + ";");

        VBox logoBox = new VBox(6, logo, appName, version);
        logoBox.setAlignment(Pos.CENTER);
        logoBox.setPadding(new Insets(30, 20, 30, 20));

        Separator sep1 = styledSeparator();

        // Navigation Buttons
        Button btnCycle   = sidebarBtn("🗓", "New Budget Cycle");
        Button btnLimit   = sidebarBtn("📊", "Daily Spending Limit");
        Button btnExpense = sidebarBtn("➕", "Add Expense");
        Button btnReport  = sidebarBtn("📁", "Transactions by Category");

        Separator sep2 = styledSeparator();

        Button btnExit = sidebarBtn("🚪", "Exit");
        btnExit.setStyle(btnExit.getStyle() + " -fx-text-fill: #E88080;");

        // Actions
        btnCycle.setOnAction(e -> cycleUI.showSetupCycleDialog());

        btnLimit.setOnAction(e -> {
            try { cycleUI.displayDailyLimit(); } 
            catch (Exception ex) { showAlert("Error", ex.getMessage()); }
        });

        btnExpense.setOnAction(e -> expenseUI.showAddExpenseDialog());

        btnReport.setOnAction(e -> {
            try { expenseUI.showCategorizedTransactions(); } 
            catch (Exception ex) { showAlert("Error", ex.getMessage()); }
        });


        btnExit.setOnAction(e -> stage.close());

        VBox spacer = new VBox();
        VBox.setVgrow(spacer, Priority.ALWAYS);

        VBox sidebar = new VBox(5,
            logoBox, sep1,
            btnCycle, btnLimit, btnExpense, btnReport,
            sep2, spacer, btnExit
        );

        sidebar.setStyle("-fx-background-color: " + SIDEBAR_BG + "; -fx-padding: 0;");
        sidebar.setPrefWidth(260);

        return sidebar;
    }

    private Button sidebarBtn(String icon, String text) {
        Button btn = new Button("   " + icon + "   " + text);
        btn.setMaxWidth(Double.MAX_VALUE);
        btn.setAlignment(Pos.CENTER_LEFT);
        btn.setStyle(
            "-fx-background-color: transparent;" +
            "-fx-text-fill: " + TEXT_WHITE + ";" +
            "-fx-font-size: 14px;" +
            "-fx-padding: 14 20;" +
            "-fx-cursor: hand;"
        );

        btn.setOnMouseEntered(e -> btn.setStyle(
            "-fx-background-color: " + BTN_HOVER + "33;" +
            "-fx-text-fill: " + LIGHT_BLUE + ";" +
            "-fx-font-size: 14px;" +
            "-fx-padding: 14 20;" +
            "-fx-cursor: hand;"
        ));

        btn.setOnMouseExited(e -> btn.setStyle(
            "-fx-background-color: transparent;" +
            "-fx-text-fill: " + TEXT_WHITE + ";" +
            "-fx-font-size: 14px;" +
            "-fx-padding: 14 20;" +
            "-fx-cursor: hand;"
        ));

        return btn;
    }

    private Separator styledSeparator() {
        Separator sep = new Separator();
        sep.setStyle("-fx-background-color: #1E3A5F;");
        sep.setPadding(new Insets(8, 0, 8, 0));
        return sep;
    }

    private void showAlert(String title, String msg) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setContentText(msg);
        alert.showAndWait();
    }

    public static void main(String[] args) {
        launch(args);
    }
}