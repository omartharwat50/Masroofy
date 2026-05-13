import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;
import java.time.LocalDate;

public class CycleUI {

    private final CycleController cycleController;

    // ===== SHARED THEME =====
    private static final String DARK_NAVY   = "#0D1B2A";
    private static final String NAVY        = "#1B2A3B";
    private static final String ACCENT_BLUE = "#1E90FF";
    private static final String LIGHT_BLUE  = "#4DB8FF";
    private static final String TEXT_WHITE  = "#F0F8FF";
    private static final String TEXT_MUTED  = "#90B8D8";
    private static final String INPUT_BG    = "#162130";

    public CycleUI(CycleController cycleController) {
        this.cycleController = cycleController;
    }

    // ================= DISPLAY DAILY LIMIT =================
    public void displayDailyLimit() throws Exception {
        double dailyLimit = cycleController.logDailyLimit();

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Daily Spending Limit");
        alert.setHeaderText("💡 Your Daily Limit");
        alert.setContentText(String.format("You can spend up to:  $%.2f per day", dailyLimit));
        styleAlert(alert);
        alert.showAndWait();
    }

    // ================= SETUP CYCLE DIALOG =================
    public void showSetupCycleDialog() {
        Stage dialog = new Stage();
        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.setTitle("New Budget Cycle");

        // --- Header ---
        Label icon = new Label("🗓");
        icon.setStyle("-fx-font-size: 36px;");

        Label header = new Label("Create Budget Cycle");
        header.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-text-fill: " + TEXT_WHITE + ";");

        Label sub = new Label("Set your budget and date range");
        sub.setStyle("-fx-font-size: 13px; -fx-text-fill: " + TEXT_MUTED + ";");

        VBox headerBox = new VBox(6, icon, header, sub);
        headerBox.setAlignment(Pos.CENTER);
        headerBox.setPadding(new Insets(24, 0, 16, 0));

        // --- Form fields ---
        TextField budgetField = styledTextField("e.g. 5000.00");

        DatePicker startDatePicker = styledDatePicker();
        DatePicker endDatePicker   = styledDatePicker();

        Label statusLabel = new Label();
        statusLabel.setStyle("-fx-font-size: 13px;");
        statusLabel.setWrapText(true);

        Button submitBtn = primaryButton("✅  Create Budget Cycle");

        submitBtn.setOnAction(e -> {
            try {
                double budget = Double.parseDouble(budgetField.getText().trim());
                LocalDate start = startDatePicker.getValue();
                LocalDate end   = endDatePicker.getValue();

                if (budget <= 0) {
                    setError(statusLabel, "Budget must be a positive number");
                    return;
                }
                if (start == null || end == null) {
                    setError(statusLabel, "Please select both start and end dates");
                    return;
                }
                if (!end.isAfter(start)) {
                    setError(statusLabel, "End date must be after start date");
                    return;
                }

                cycleController.createCycle(budget, start, end);
                setSuccess(statusLabel, "Cycle created successfully!");

            } catch (NumberFormatException ex) {
                setError(statusLabel, "Please enter a valid number for budget");
            } catch (Exception ex) {
                setError(statusLabel, ex.getMessage());
            }
        });

        // --- Layout ---
        VBox form = new VBox(10,
            fieldGroup("Total Budget ($)", budgetField),
            fieldGroup("Start Date", startDatePicker),
            fieldGroup("End Date", endDatePicker),
            submitBtn,
            statusLabel
        );
        form.setPadding(new Insets(10, 30, 24, 30));

        VBox root = new VBox(headerBox, form);
        root.setStyle("-fx-background-color: " + DARK_NAVY + ";");

        dialog.setScene(new Scene(root, 440, 480));
        dialog.showAndWait();
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

    private DatePicker styledDatePicker() {
        DatePicker dp = new DatePicker();
        dp.setMaxWidth(Double.MAX_VALUE);
        dp.setStyle(
            "-fx-background-color: " + INPUT_BG + ";" +
            "-fx-text-fill: " + TEXT_WHITE + ";" +
            "-fx-border-color: #1E3A5F;" +
            "-fx-border-radius: 6;" +
            "-fx-background-radius: 6;"
        );
        return dp;
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
        VBox group = new VBox(5, lbl, field);
        return group;
    }

    private void setError(Label lbl, String msg) {
        lbl.setText("❌  " + msg);
        lbl.setStyle("-fx-text-fill: #E88080; -fx-font-size: 13px;");
    }

    private void setSuccess(Label lbl, String msg) {
        lbl.setText("✅  " + msg);
        lbl.setStyle("-fx-text-fill: #6FCCA0; -fx-font-size: 13px;");
    }

    private void styleAlert(Alert alert) {
        alert.getDialogPane().setStyle(
            "-fx-background-color: #1B2A3B; -fx-font-size: 14px;");
    }
}