import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Modality;
import javafx.stage.Stage;
import java.util.List;

public class ExpenseUI {

    private final ExpenseController expenseController;

    // ===== SHARED THEME =====
    private static final String DARK_NAVY   = "#0D1B2A";
    private static final String NAVY        = "#1B2A3B";
    private static final String ACCENT_BLUE = "#1E90FF";
    private static final String LIGHT_BLUE  = "#4DB8FF";
    private static final String TEXT_WHITE  = "#F0F8FF";
    private static final String TEXT_MUTED  = "#90B8D8";
    private static final String INPUT_BG    = "#162130";

    public ExpenseUI(ExpenseController expenseController) {
        this.expenseController = expenseController;
    }

    // ================= ADD EXPENSE DIALOG =================
    public void showAddExpenseDialog() {
        Stage dialog = new Stage();
        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.setTitle("Add Expense");

        // Header
        Label icon = new Label("➕");
        icon.setStyle("-fx-font-size: 34px;");
        Label header = new Label("Add New Expense");
        header.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-text-fill: " + TEXT_WHITE + ";");
        Label sub = new Label("Record a new spending transaction");
        sub.setStyle("-fx-font-size: 13px; -fx-text-fill: " + TEXT_MUTED + ";");
        VBox headerBox = new VBox(6, icon, header, sub);
        headerBox.setAlignment(Pos.CENTER);
        headerBox.setPadding(new Insets(24, 0, 16, 0));

        // Fields
        ComboBox<Category> categoryCombo = new ComboBox<>();
        categoryCombo.setMaxWidth(Double.MAX_VALUE);
        categoryCombo.setStyle(
            "-fx-background-color: " + INPUT_BG + ";" +
            "-fx-text-fill: " + TEXT_WHITE + ";" +
            "-fx-border-color: #1E3A5F;" +
            "-fx-border-radius: 6;" +
            "-fx-background-radius: 6;"
        );

        TextField amountField = styledTextField("e.g. 250.00");
        Label statusLabel = new Label();
        statusLabel.setWrapText(true);
        Button submitBtn = primaryButton("➕  Add Expense");

        try {
            List<Category> categories = expenseController.getTransactionsByCategory();
            categoryCombo.getItems().addAll(categories);
            if (!categories.isEmpty()) categoryCombo.setValue(categories.get(0));
        } catch (Exception e) {
            setError(statusLabel, "Error loading categories");
        }

        submitBtn.setOnAction(e -> {
            try {
                Category selected = categoryCombo.getValue();
                if (selected == null) { setError(statusLabel, "Please select a category"); return; }

                double amount = Double.parseDouble(amountField.getText().trim());
                if (amount <= 0) { setError(statusLabel, "Amount must be greater than 0"); return; }

                expenseController.addExpense(amount, selected.getId());
                setSuccess(statusLabel, "Expense added successfully!");
                amountField.clear();

            } catch (NumberFormatException ex) {
                setError(statusLabel, "Please enter a valid number");
            } catch (Exception ex) {
                setError(statusLabel, ex.getMessage());
            }
        });

        VBox form = new VBox(10,
            fieldGroup("Category", categoryCombo),
            fieldGroup("Amount ($)", amountField),
            submitBtn,
            statusLabel
        );
        form.setPadding(new Insets(10, 30, 24, 30));

        VBox root = new VBox(headerBox, form);
        root.setStyle("-fx-background-color: " + DARK_NAVY + ";");

        dialog.setScene(new Scene(root, 440, 400));
        dialog.showAndWait();
    }

    // ================= VIEW TRANSACTIONS DIALOG =================
    public void showCategorizedTransactions() throws Exception {
        List<Category> categories = expenseController.getTransactionsByCategory();

        Stage stage = new Stage();
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setTitle("Transactions Report");

        // Header
        Label icon = new Label("📁");
        icon.setStyle("-fx-font-size: 30px;");
        Label header = new Label("Transactions by Category");
        header.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-text-fill: " + TEXT_WHITE + ";");
        VBox headerBox = new VBox(6, icon, header);
        headerBox.setAlignment(Pos.CENTER);
        headerBox.setPadding(new Insets(24, 0, 14, 0));

        // Category cards
        VBox cardsBox = new VBox(14);
        cardsBox.setPadding(new Insets(0, 20, 20, 20));

        if (categories.isEmpty()) {
            Label empty = new Label("No transactions found.");
            empty.setStyle("-fx-text-fill: " + TEXT_MUTED + "; -fx-font-size: 14px;");
            cardsBox.getChildren().add(empty);
        } else {
            for (Category category : categories) {
                cardsBox.getChildren().add(buildCategoryCard(category));
            }
        }

        ScrollPane scroll = new ScrollPane(cardsBox);
        scroll.setFitToWidth(true);
        scroll.setStyle("-fx-background-color: " + DARK_NAVY + "; -fx-background: " + DARK_NAVY + ";");
        VBox.setVgrow(scroll, Priority.ALWAYS);

        VBox root = new VBox(headerBox, scroll);
        root.setStyle("-fx-background-color: " + DARK_NAVY + ";");

        stage.setScene(new Scene(root, 560, 680));
        stage.showAndWait();
    }

    private VBox buildCategoryCard(Category category) {
        Label catName = new Label("📁  " + category.getName().toUpperCase());
        catName.setStyle(
            "-fx-font-weight: bold; -fx-font-size: 14px; -fx-text-fill: " + LIGHT_BLUE + ";");

        VBox card = new VBox(8);
        card.setStyle(
            "-fx-background-color: #1B2A3B;" +
            "-fx-background-radius: 10;" +
            "-fx-border-color: #1E3A5F;" +
            "-fx-border-radius: 10;" +
            "-fx-border-width: 1;" +
            "-fx-padding: 14;"
        );
        card.getChildren().add(catName);

        List<Transaction> transactions = category.getTransactions();
        double total = 0;

        if (transactions.isEmpty()) {
            Label none = new Label("   No transactions yet");
            none.setStyle("-fx-text-fill: " + TEXT_MUTED + ";");
            card.getChildren().add(none);
        } else {
            for (Transaction t : transactions) {
                HBox row = new HBox();
                row.setAlignment(Pos.CENTER_LEFT);
                Label amtLabel = new Label(String.format("$%.2f", t.getAmount()));
                amtLabel.setStyle(
                    "-fx-text-fill: #6FCCA0; -fx-font-weight: bold; -fx-font-size: 13px; -fx-min-width: 90px;");
                Label dateLabel = new Label(t.getDate().toString());
                dateLabel.setStyle("-fx-text-fill: " + TEXT_MUTED + "; -fx-font-size: 12px;");
                Region spacer = new Region();
                HBox.setHgrow(spacer, Priority.ALWAYS);
                row.getChildren().addAll(new Label("  • "), amtLabel, spacer, dateLabel);
                card.getChildren().add(row);
                total += t.getAmount();
            }

            Separator sep = new Separator();
            sep.setStyle("-fx-background-color: #1E3A5F;");
            Label totalLabel = new Label(String.format("Category Total:   $%.2f", total));
            totalLabel.setStyle(
                "-fx-font-weight: bold; -fx-font-size: 13px; -fx-text-fill: " + ACCENT_BLUE + ";");
            card.getChildren().addAll(sep, totalLabel);
        }

        return card;
    }

    // ===== HELPERS =====
    private TextField styledTextField(String prompt) {
        TextField tf = new TextField();
        tf.setPromptText(prompt);
        tf.setStyle(
            "-fx-background-color: " + INPUT_BG + ";" +
            "-fx-text-fill: " + TEXT_WHITE + ";" +
            "-fx-prompt-text-fill: " + TEXT_MUTED + ";" +
            "-fx-border-color: #1E3A5F;" +
            "-fx-border-radius: 6;" +
            "-fx-background-radius: 6;" +
            "-fx-padding: 10 14 10 14;" +
            "-fx-font-size: 14px;"
        );
        return tf;
    }

    private Button primaryButton(String text) {
        Button btn = new Button(text);
        btn.setMaxWidth(Double.MAX_VALUE);
        btn.setStyle(
            "-fx-background-color: " + ACCENT_BLUE + ";" +
            "-fx-text-fill: white;" +
            "-fx-font-size: 14px;" +
            "-fx-font-weight: bold;" +
            "-fx-padding: 12 0 12 0;" +
            "-fx-background-radius: 8;" +
            "-fx-cursor: hand;"
        );
        btn.setOnMouseEntered(e -> btn.setStyle(
            "-fx-background-color: #1A7DE0;" +
            "-fx-text-fill: white;" +
            "-fx-font-size: 14px;" +
            "-fx-font-weight: bold;" +
            "-fx-padding: 12 0 12 0;" +
            "-fx-background-radius: 8;" +
            "-fx-cursor: hand;"
        ));
        btn.setOnMouseExited(e -> btn.setStyle(
            "-fx-background-color: " + ACCENT_BLUE + ";" +
            "-fx-text-fill: white;" +
            "-fx-font-size: 14px;" +
            "-fx-font-weight: bold;" +
            "-fx-padding: 12 0 12 0;" +
            "-fx-background-radius: 8;" +
            "-fx-cursor: hand;"
        ));
        return btn;
    }

    private VBox fieldGroup(String labelText, Control field) {
        Label lbl = new Label(labelText);
        lbl.setStyle("-fx-font-size: 12px; -fx-text-fill: " + TEXT_MUTED + "; -fx-font-weight: bold;");
        return new VBox(5, lbl, field);
    }

    private void setError(Label lbl, String msg) {
        lbl.setText("❌  " + msg);
        lbl.setStyle("-fx-text-fill: #E88080; -fx-font-size: 13px;");
    }

    private void setSuccess(Label lbl, String msg) {
        lbl.setText("✅  " + msg);
        lbl.setStyle("-fx-text-fill: #6FCCA0; -fx-font-size: 13px;");
    }
}