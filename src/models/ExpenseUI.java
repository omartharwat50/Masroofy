import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import java.util.List;

public class ExpenseUI {

    private final ExpenseController expenseController;

    public ExpenseUI(ExpenseController expenseController) {
        this.expenseController = expenseController;
    }

    // ================= ADD EXPENSE GUI =================
    public void showAddExpenseDialog() {
        ComboBox<Category> categoryCombo = new ComboBox<>();
        TextField amountField = new TextField();
        amountField.setPromptText("Enter amount");

        Button submitBtn = new Button("Add Expense");
        Label statusLabel = new Label();

        try {
            List<Category> categories = expenseController.getTransactionsByCategory(); // افترض وجود هذه الدالة
            categoryCombo.getItems().addAll(categories);
            if (!categories.isEmpty()) {
                categoryCombo.setValue(categories.get(0));
            }
        } catch (Exception e) {
            statusLabel.setText("❌ Error loading categories");
        }

        submitBtn.setOnAction(e -> {
            try {
                Category selected = categoryCombo.getValue();
                if (selected == null) {
                    statusLabel.setText("❌ Please select a category");
                    return;
                }

                double amount = Double.parseDouble(amountField.getText().trim());
                if (amount <= 0) {
                    statusLabel.setText("❌ Amount must be greater than 0");
                    return;
                }

                expenseController.addExpense(amount, selected.getId());
                statusLabel.setText("✅ Expense added successfully!");
                statusLabel.setStyle("-fx-text-fill: green;");
                amountField.clear();

            } catch (NumberFormatException ex) {
                statusLabel.setText("❌ Please enter a valid number");
            } catch (Exception ex) {
                statusLabel.setText("❌ " + ex.getMessage());
            }
        });

        VBox layout = new VBox(12);
        layout.setAlignment(Pos.CENTER);
        layout.setStyle("-fx-padding: 25;");

        layout.getChildren().addAll(
                new Label("💰 Add New Expense"),
                new Label("Category:"), categoryCombo,
                new Label("Amount ($):"), amountField,
                submitBtn,
                statusLabel
        );

        Stage dialog = new Stage();
        dialog.setTitle("Add Expense");
        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.setScene(new Scene(layout, 380, 320));
        dialog.showAndWait();
    }

    // ================= VIEW TRANSACTIONS GUI =================
    public void showCategorizedTransactions() throws Exception {
        List<Category> categories = expenseController.getTransactionsByCategory();

        VBox mainLayout = new VBox(15);
        mainLayout.setStyle("-fx-padding: 20;");

        Label header = new Label("📊 TRANSACTIONS BY CATEGORY");
        header.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");

        mainLayout.getChildren().add(header);

        if (categories.isEmpty()) {
            mainLayout.getChildren().add(new Label("No transactions found."));
        } else {
            for (Category category : categories) {
                VBox catBox = new VBox(8);
                catBox.setStyle("-fx-border-color: #ddd; -fx-border-radius: 5; -fx-padding: 12;");

                Label catName = new Label("📁 " + category.getName().toUpperCase());
                catName.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");

                catBox.getChildren().add(catName);

                List<Transaction> transactions = category.getTransactions();
                double total = 0;

                if (transactions.isEmpty()) {
                    catBox.getChildren().add(new Label("   No transactions"));
                } else {
                    for (Transaction t : transactions) {
                        Label trans = new Label(String.format("   • $%.2f   |   %s", 
                            t.getAmount(), t.getDate()));
                        catBox.getChildren().add(trans);
                        total += t.getAmount();
                    }
                    Label totalLabel = new Label("   Category Total: $" + String.format("%.2f", total));
                    totalLabel.setStyle("-fx-font-weight: bold;");
                    catBox.getChildren().add(totalLabel);
                }
                mainLayout.getChildren().add(catBox);
            }
        }

        Stage stage = new Stage();
        stage.setTitle("Transactions Report");
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setScene(new Scene(mainLayout, 520, 600));
        stage.showAndWait();
    }
}