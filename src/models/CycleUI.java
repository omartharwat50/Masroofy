import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import java.time.LocalDate;

public class CycleUI {

    private final CycleController cycleController;

    public CycleUI(CycleController cycleController) {
        this.cycleController = cycleController;
    }

    public void displayDailyLimit() throws Exception {
        double dailyLimit = cycleController.logDailyLimit();

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Daily Spending Limit");
        alert.setHeaderText("💡 Daily Limit");
        alert.setContentText("You can spend up to:  $" + String.format("%.2f", dailyLimit) + " per day");
        alert.showAndWait();
    }

    public void showSetupCycleDialog() {
        TextField budgetField = new TextField();
        budgetField.setPromptText("Enter total budget");

        DatePicker startDatePicker = new DatePicker();
        DatePicker endDatePicker = new DatePicker();

        Button submitBtn = new Button("Create Budget Cycle");
        Label statusLabel = new Label();

        submitBtn.setOnAction(e -> {
            try {
                double budget = Double.parseDouble(budgetField.getText().trim());
                LocalDate start = startDatePicker.getValue();
                LocalDate end = endDatePicker.getValue();

                if (budget <= 0) {
                    statusLabel.setText("❌ Budget must be positive");
                    return;
                }
                if (start == null || end == null) {
                    statusLabel.setText("❌ Please select dates");
                    return;
                }
                if (!end.isAfter(start)) {
                    statusLabel.setText("❌ End date must be after start date");
                    return;
                }

                cycleController.createCycle(budget, start, end);
                statusLabel.setText("✅ Cycle created successfully!");
                statusLabel.setStyle("-fx-text-fill: green;");

            } catch (Exception ex) {
                statusLabel.setText("❌ " + ex.getMessage());
            }
        });

        VBox layout = new VBox(12);
        layout.setAlignment(Pos.CENTER);
        layout.setStyle("-fx-padding: 20;");

        layout.getChildren().addAll(
                new Label("💰 Create New Budget Cycle"),
                new Label("Total Budget:"), budgetField,
                new Label("Start Date:"), startDatePicker,
                new Label("End Date:"), endDatePicker,
                submitBtn, statusLabel
        );

        Stage dialog = new Stage();
        dialog.setTitle("New Budget Cycle");
        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.setScene(new Scene(layout, 400, 380));
        dialog.showAndWait();
    }
}