
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;



import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public class TransactionsScreen extends VBox {

    private final TransactionController controller;
    private final TableView<Transaction> table = new TableView<>();
    private final ObservableList<Transaction> data = FXCollections.observableArrayList();
    private TextField searchField;
    private ComboBox<String> typeFilter;
    private ComboBox<Category> categoryFilter;

    public TransactionsScreen(TransactionController controller) {
        this.controller = controller;
        setStyle("-fx-background-color: " + Theme.BG_DEEP + ";");
        build();
    }

    public void refresh() {
        getChildren().clear();
        build();
    }

    private void build() {
        getChildren().addAll(buildTopBar(), buildFilterBar(), buildContent());
        loadTransactions();
    }

    private HBox buildTopBar() {
        HBox bar = new HBox();
        bar.setPadding(new Insets(20, 28, 20, 28));
        bar.setAlignment(Pos.CENTER_LEFT);
        bar.setStyle("-fx-background-color: " + Theme.BG_DARK + ";" +
                     "-fx-border-color: " + Theme.BORDER + ";" +
                     "-fx-border-width: 0 0 1 0;");

        Label title = Theme.makeLabel("Transactions", Theme.TEXT_PRIMARY, "24px", true);
        Label sub = Theme.makeLabel("Track, add and manage your financial records", Theme.TEXT_SECONDARY, "13px", false);
        VBox titles = new VBox(3, title, sub);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Button addIncomeBtn = Theme.makePrimaryBtn("+ Add Income");
        addIncomeBtn.setStyle(addIncomeBtn.getStyle() + "-fx-background-color: " + Theme.ACCENT_GREEN + ";");
        addIncomeBtn.setOnAction(e -> showAddDialog(TransactionType.INCOME));

        Button addExpenseBtn = Theme.makePrimaryBtn("+ Add Expense");
        addExpenseBtn.setOnAction(e -> showAddDialog(TransactionType.EXPENSE));

        HBox btns = new HBox(10, addIncomeBtn, addExpenseBtn);
        bar.getChildren().addAll(titles, spacer, btns);
        return bar;
    }

    private HBox buildFilterBar() {
        HBox bar = new HBox(12);
        bar.setPadding(new Insets(14, 28, 14, 28));
        bar.setAlignment(Pos.CENTER_LEFT);
        bar.setStyle("-fx-background-color: " + Theme.BG_DARK + ";" +
                     "-fx-border-color: " + Theme.BORDER + ";" +
                     "-fx-border-width: 0 0 1 0;");

        searchField = new TextField();
        searchField.setPromptText("🔍  Search transactions...");
        searchField.setStyle(Theme.inputField());
        searchField.setPrefWidth(260);
        searchField.textProperty().addListener((obs, o, n) -> applyFilters());

        typeFilter = new ComboBox<>();
        typeFilter.setStyle(Theme.inputField());
        typeFilter.setPrefWidth(140);
        typeFilter.getItems().addAll("All Types", "Income", "Expense");
        typeFilter.setValue("All Types");
        typeFilter.setOnAction(e -> applyFilters());

        categoryFilter = new ComboBox<>();
        categoryFilter.setStyle(Theme.inputField());
        categoryFilter.setPrefWidth(180);
        categoryFilter.setPromptText("All Categories");

        try {
            List<Category> cats = controller.getAllCategories();
            categoryFilter.getItems().add(null);
            categoryFilter.getItems().addAll(cats);
            categoryFilter.setConverter(new javafx.util.StringConverter<>() {
                public String toString(Category c) { return c == null ? "All Categories" : c.toString(); }
                public Category fromString(String s) { return null; }
            });
        } catch (Exception ignored) {}

        categoryFilter.setValue(null);
        categoryFilter.setOnAction(e -> applyFilters());

        Button clearBtn = Theme.makeGhostBtn("✕ Clear");
        clearBtn.setOnAction(e -> {
            searchField.clear();
            typeFilter.setValue("All Types");
            categoryFilter.setValue(null);
            loadTransactions();
        });

        bar.getChildren().addAll(searchField, typeFilter, categoryFilter, clearBtn);
        return bar;
    }

    @SuppressWarnings("unchecked")
    private VBox buildContent() {
        VBox content = new VBox(0);
        VBox.setVgrow(content, Priority.ALWAYS);
        content.setPadding(new Insets(20, 28, 20, 28));
        content.setStyle("-fx-background-color: " + Theme.BG_DEEP + ";");

        // Table setup
        table.setStyle("-fx-background-color: " + Theme.BG_CARD + ";" +
                       "-fx-border-color: " + Theme.BORDER + ";" +
                       "-fx-border-radius: 10;" +
                       "-fx-background-radius: 10;");
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        table.setItems(data);
        VBox.setVgrow(table, Priority.ALWAYS);

        TableColumn<Transaction, String> typeCol = new TableColumn<>("Type");
        typeCol.setPrefWidth(90);
        typeCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getType().getDisplayName()));
        typeCol.setCellFactory(col -> new TableCell<>() {
            @Override protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) { setText(null); setGraphic(null); return; }
                String color = item.equals("Income") ? Theme.ACCENT_GREEN : Theme.ACCENT_RED;
                Label lbl = Theme.makeLabel(item, color, "12px", true);
                lbl.setStyle(lbl.getStyle() +
                    "-fx-background-color: " + color + "22;" +
                    "-fx-padding: 3 8;" +
                    "-fx-background-radius: 4;");
                setGraphic(lbl);
                setText(null);
            }
        });

        TableColumn<Transaction, String> titleCol = new TableColumn<>("Title");
        titleCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getTitle()));
        titleCol.setCellFactory(col -> makeCell(Theme.TEXT_PRIMARY));

        TableColumn<Transaction, String> amtCol = new TableColumn<>("Amount");
        amtCol.setPrefWidth(110);
        amtCol.setCellValueFactory(c -> {
            Transaction t = c.getValue();
            String sign = t.getType() == TransactionType.INCOME ? "+" : "-";
            return new SimpleStringProperty(sign + "$" + String.format("%.2f", t.getAmount()));
        });
        amtCol.setCellFactory(col -> new TableCell<>() {
            @Override protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) { setText(null); return; }
                String color = item.startsWith("+") ? Theme.ACCENT_GREEN : Theme.ACCENT_RED;
                setStyle("-fx-text-fill: " + color + "; -fx-font-weight: bold; -fx-font-size: 13px;");
                setText(item);
            }
        });

        TableColumn<Transaction, String> catCol = new TableColumn<>("Category");
        catCol.setPrefWidth(130);
        catCol.setCellValueFactory(c -> new SimpleStringProperty(
            c.getValue().getCategoryName() != null ? c.getValue().getCategoryName() : "—"));
        catCol.setCellFactory(col -> makeCell(Theme.TEXT_SECONDARY));

        TableColumn<Transaction, String> dateCol = new TableColumn<>("Date");
        dateCol.setPrefWidth(110);
        dateCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getDate().toString()));
        dateCol.setCellFactory(col -> makeCell(Theme.TEXT_SECONDARY));

        TableColumn<Transaction, String> noteCol = new TableColumn<>("Note");
        noteCol.setCellValueFactory(c -> new SimpleStringProperty(
            c.getValue().getNote() != null ? c.getValue().getNote() : ""));
        noteCol.setCellFactory(col -> makeCell(Theme.TEXT_MUTED));

        TableColumn<Transaction, Void> actionsCol = new TableColumn<>("Actions");
        actionsCol.setPrefWidth(130);
        actionsCol.setCellFactory(col -> new TableCell<>() {
            private final Button editBtn = Theme.makeGhostBtn("✎ Edit");
            private final Button delBtn = Theme.makeDangerBtn("✕");
            {
                editBtn.setOnAction(e -> {
                    Transaction t = getTableView().getItems().get(getIndex());
                    showEditDialog(t);
                });
                delBtn.setOnAction(e -> {
                    Transaction t = getTableView().getItems().get(getIndex());
                    showDeleteConfirm(t);
                });
            }
            @Override protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) { setGraphic(null); return; }
                HBox box = new HBox(6, editBtn, delBtn);
                box.setAlignment(Pos.CENTER);
                setGraphic(box);
            }
        });

        table.getColumns().addAll(typeCol, titleCol, amtCol, catCol, dateCol, noteCol, actionsCol);

        content.getChildren().add(table);
        return content;
    }

    private <T> TableCell<Transaction, T> makeCell(String color) {
        return new TableCell<>() {
            @Override protected void updateItem(T item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) { setText(null); return; }
                setStyle("-fx-text-fill: " + color + "; -fx-font-size: 13px;");
                setText(item.toString());
            }
        };
    }

    private void loadTransactions() {
        try {
            data.setAll(controller.getAllTransactions());
        } catch (Exception e) {
            showAlert("Error loading transactions: " + e.getMessage());
        }
    }

    private void applyFilters() {
        try {
            String keyword = searchField.getText();
            List<Transaction> result;

            if (keyword != null && !keyword.isBlank()) {
                result = controller.searchTransactions(keyword);
            } else {
                result = controller.getAllTransactions();
            }

            String type = typeFilter.getValue();
            if ("Income".equals(type)) result = result.stream().filter(t -> t.getType() == TransactionType.INCOME).toList();
            if ("Expense".equals(type)) result = result.stream().filter(t -> t.getType() == TransactionType.EXPENSE).toList();

            Category cat = categoryFilter.getValue();
            if (cat != null) result = result.stream().filter(t -> t.getCategoryId() == cat.getId()).toList();

            data.setAll(result);
        } catch (Exception e) {
            showAlert("Filter error: " + e.getMessage());
        }
    }

    private void showAddDialog(TransactionType defaultType) {
        TransactionDialog dialog = new TransactionDialog(null, defaultType, controller);
        dialog.setOnSuccess(this::loadTransactions);
        dialog.showAndWait();
    }

    private void showEditDialog(Transaction t) {
        TransactionDialog dialog = new TransactionDialog(t, t.getType(), controller);
        dialog.setOnSuccess(this::loadTransactions);
        dialog.showAndWait();
    }

    private void showDeleteConfirm(Transaction t) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Delete Transaction");
        alert.setHeaderText("Delete \"" + t.getTitle() + "\"?");
        alert.setContentText("This action cannot be undone.");
        alert.getDialogPane().setStyle("-fx-background-color: " + Theme.BG_DARK + "; -fx-font-size: 14px;");
        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                controller.deleteTransaction(t.getId());
                loadTransactions();
            } catch (Exception e) {
                showAlert("Error deleting transaction: " + e.getMessage());
            }
        }
    }

    private void showAlert(String msg) {
        Alert alert = new Alert(Alert.AlertType.ERROR, msg);
        alert.getDialogPane().setStyle("-fx-background-color: " + Theme.BG_DARK + ";");
        alert.showAndWait();
    }

    // ===== Inner dialog =====
    private static class TransactionDialog extends StyledDialog {
        private Runnable onSuccess;
        private final Transaction existing;
        private final TransactionController controller;

        TransactionDialog(Transaction existing, TransactionType defaultType, TransactionController controller) {
            super(existing == null ? "Add Transaction" : "Edit Transaction",
                  existing == null ? "+" : "✎",
                  existing == null ? "Record a new transaction" : "Update transaction details",
                  460, 560);
            this.existing = existing;
            this.controller = controller;

            TextField titleField = makeField("Transaction title (e.g. Lunch)");
            TextField amountField = makeField("Amount (e.g. 150.00)");
            TextField noteField = makeField("Optional note...");
            DatePicker datePicker = makeDatePicker();
            ComboBox<TransactionType> typeCombo = new ComboBox<>();
            typeCombo.setMaxWidth(Double.MAX_VALUE);
            typeCombo.setStyle(Theme.inputField());
            typeCombo.getItems().addAll(TransactionType.values());
            typeCombo.setValue(defaultType);

            ComboBox<Category> categoryCombo = new ComboBox<>();
            categoryCombo.setMaxWidth(Double.MAX_VALUE);
            categoryCombo.setStyle(Theme.inputField());
            categoryCombo.setPromptText("Select category...");
            try {
                categoryCombo.getItems().addAll(controller.getAllCategories());
                categoryCombo.setConverter(new javafx.util.StringConverter<>() {
                    public String toString(Category c) { return c == null ? "" : c.toString(); }
                    public Category fromString(String s) { return null; }
                });
            } catch (Exception ignored) {}

            if (existing != null) {
                titleField.setText(existing.getTitle());
                amountField.setText(String.valueOf(existing.getAmount()));
                noteField.setText(existing.getNote() != null ? existing.getNote() : "");
                datePicker.setValue(existing.getDate());
                typeCombo.setValue(existing.getType());
            } else {
                datePicker.setValue(LocalDate.now());
            }

            Button submitBtn = Theme.makePrimaryBtn(existing == null ? "✓  Add Transaction" : "✓  Save Changes");
            submitBtn.setMaxWidth(Double.MAX_VALUE);
            submitBtn.setOnAction(e -> handleSubmit(titleField, amountField, typeCombo,
                categoryCombo, noteField, datePicker));

            addContent(
                Theme.fieldGroup("Title", titleField),
                Theme.fieldGroup("Type", typeCombo),
                Theme.fieldGroup("Category", categoryCombo),
                Theme.fieldGroup("Amount ($)", amountField),
                Theme.fieldGroup("Date", datePicker),
                Theme.fieldGroup("Note", noteField),
                submitBtn
            );
        }

        void setOnSuccess(Runnable r) { this.onSuccess = r; }

        private void handleSubmit(TextField titleField, TextField amountField,
                                   ComboBox<TransactionType> typeCombo, ComboBox<Category> categoryCombo,
                                   TextField noteField, DatePicker datePicker) {
            try {
                String title = titleField.getText().trim();
                double amount = Double.parseDouble(amountField.getText().trim());
                TransactionType type = typeCombo.getValue();
                Category cat = categoryCombo.getValue();
                int catId = cat != null ? cat.getId() : 0;
                String note = noteField.getText().trim();
                LocalDate date = datePicker.getValue();

                if (title.isBlank()) { setError("Title cannot be empty."); return; }
                if (amount <= 0) { setError("Amount must be greater than 0."); return; }
                if (date == null) { setError("Please select a date."); return; }

                if (existing == null) {
                    controller.addTransaction(title, amount, type, catId, note, date);
                } else {
                    existing.setTitle(title);
                    existing.setAmount(amount);
                    existing.setType(type);
                    existing.setCategoryId(catId);
                    existing.setNote(note);
                    existing.setDate(date);
                    controller.updateTransaction(existing);
                }

                setSuccess(existing == null ? "Transaction added!" : "Transaction updated!");
                if (onSuccess != null) onSuccess.run();

            } catch (NumberFormatException ex) {
                setError("Please enter a valid number for amount.");
            } catch (Exception ex) {
                setError(ex.getMessage());
            }
        }
    }
}
