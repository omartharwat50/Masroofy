import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class MainGUI extends Application {

    private CycleUI cycleUI;
    private ExpenseUI expenseUI;

    @Override
    public void start(Stage stage) {

        try {
            // ================= INIT BACKEND =================
            DatabaseManager databaseManager = new DatabaseManager();
            NotificationService notificationService = new NotificationService();

            CycleService cycleService = new CycleService(databaseManager);
            ExpenseService expenseService = new ExpenseService(
                    databaseManager, databaseManager, databaseManager, notificationService);

            CycleController cycleController = new CycleController(cycleService, databaseManager);
            ExpenseController expenseController = new ExpenseController(
                    expenseService, databaseManager, databaseManager);

            cycleUI = new CycleUI(cycleController);
            expenseUI = new ExpenseUI(expenseController);

            // ================= UI =================
            Label title = new Label("💰 PERSONAL BUDGET TRACKER");
            title.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;");

            Button btn1 = new Button("1. Create New Budget Cycle");
            Button btn2 = new Button("2. View Daily Spending Limit");
            Button btn3 = new Button("3. Add New Expense");
            Button btn4 = new Button("4. View Transactions by Category");
            Button btn5 = new Button("5. Exit");

            btn1.setPrefWidth(320);
            btn2.setPrefWidth(320);
            btn3.setPrefWidth(320);
            btn4.setPrefWidth(320);
            btn5.setPrefWidth(320);

            // ================= ACTIONS =================
            btn1.setOnAction(e -> cycleUI.showSetupCycleDialog());

            btn2.setOnAction(e -> {
                try {
                    cycleUI.displayDailyLimit();
                } catch (Exception ex) {
                    showAlert("Error", ex.getMessage());
                }
            });

            btn3.setOnAction(e -> expenseUI.showAddExpenseDialog());

            btn4.setOnAction(e -> {
                try {
                    expenseUI.showCategorizedTransactions();
                } catch (Exception ex) {
                    showAlert("Error", ex.getMessage());
                }
            });

            btn5.setOnAction(e -> stage.close());

            // ================= LAYOUT =================
            VBox root = new VBox(15);
            root.setStyle("-fx-padding: 30; -fx-alignment: center;");
            root.getChildren().addAll(title, btn1, btn2, btn3, btn4, btn5);

            Scene scene = new Scene(root, 460, 420);

            stage.setTitle("Budget Tracker");
            stage.setScene(scene);
            stage.show();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void showAlert(String title, String msg) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setContentText(msg);
        alert.showAndWait();
    }

    public static void main(String[] args) {
        launch(args);
    }
}