
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public class CycleScreen extends VBox {

    private final CycleController controller;
    private VBox contentArea;

    public CycleScreen(CycleController controller) {
        this.controller = controller;
        setStyle("-fx-background-color: " + Theme.BG_DEEP + ";");
        build();
    }

    public void refresh() {
        getChildren().clear();
        build();
    }

    private void build() {
        getChildren().addAll(buildTopBar(), buildBody());
    }

    private HBox buildTopBar() {
        HBox bar = new HBox();
        bar.setPadding(new Insets(20, 28, 20, 28));
        bar.setAlignment(Pos.CENTER_LEFT);
        bar.setStyle("-fx-background-color: " + Theme.BG_DARK + ";" +
                     "-fx-border-color: " + Theme.BORDER + ";" +
                     "-fx-border-width: 0 0 1 0;");

        Label title = Theme.makeLabel("Budget Cycle", Theme.TEXT_PRIMARY, "24px", true);
        Label sub = Theme.makeLabel("Manage your budgeting periods and limits", Theme.TEXT_SECONDARY, "13px", false);
        VBox titles = new VBox(3, title, sub);
        Region spacer = new Region(); HBox.setHgrow(spacer, Priority.ALWAYS);

        Button newBtn = Theme.makePrimaryBtn("+ New Cycle");
        newBtn.setOnAction(e -> { showAddDialog(); refresh(); });

        bar.getChildren().addAll(titles, spacer, newBtn);
        return bar;
    }

    private ScrollPane buildBody() {
        contentArea = new VBox(20);
        contentArea.setPadding(new Insets(24, 28, 24, 28));
        contentArea.setStyle("-fx-background-color: " + Theme.BG_DEEP + ";");

        try {
            // Active cycle card
            Optional<Cycle> active = controller.getActiveCycle();
            if (active.isPresent()) {
                contentArea.getChildren().add(buildActiveCycleCard(active.get()));
            } else {
                Label noCycle = Theme.makeLabel("No active budget cycle. Create one to get started!",
                    Theme.TEXT_SECONDARY, "14px", false);
                contentArea.getChildren().add(noCycle);
            }

            // History
            Label historyTitle = Theme.makeLabel("Cycle History", Theme.TEXT_PRIMARY, "16px", true);
            contentArea.getChildren().add(historyTitle);

            List<Cycle> all = controller.getAllCycles();
            for (Cycle c : all) {
                contentArea.getChildren().add(buildCycleCard(c));
            }

        } catch (Exception e) {
            contentArea.getChildren().add(Theme.makeLabel("Error: " + e.getMessage(), Theme.ACCENT_RED, "14px", false));
        }

        ScrollPane scroll = new ScrollPane(contentArea);
        scroll.setFitToWidth(true);
        scroll.setStyle("-fx-background-color: transparent; -fx-background: transparent;" +
                        "-fx-border-color: transparent;");
        VBox.setVgrow(scroll, Priority.ALWAYS);
        return scroll;
    }

    private VBox buildActiveCycleCard(Cycle c) {
        VBox card = new VBox(16);
        card.setPadding(new Insets(22));
        card.setStyle(Theme.cardElevated());

        HBox header = new HBox(10);
        Label badge = Theme.makeLabel("ACTIVE", Theme.ACCENT_GREEN, "11px", true);
        badge.setStyle(badge.getStyle() +
            "-fx-background-color: " + Theme.ACCENT_GREEN + "22;" +
            "-fx-padding: 3 8;" + "-fx-background-radius: 4;");
        Label title = Theme.makeLabel("Current Budget Cycle", Theme.TEXT_PRIMARY, "18px", true);
        Region sp = new Region(); HBox.setHgrow(sp, Priority.ALWAYS);
        Button editBtn = Theme.makeGhostBtn("✎ Edit");
        editBtn.setOnAction(e -> { showEditDialog(c); refresh(); });
        header.getChildren().addAll(title, sp, badge, editBtn);

        // Stats grid
        GridPane grid = new GridPane();
        grid.setHgap(20); grid.setVgap(12);

        addGridStat(grid, 0, 0, "Total Budget", "$" + String.format("%.2f", c.getTotalBudget()), Theme.ACCENT_GREEN);
        addGridStat(grid, 1, 0, "Daily Limit", "$" + String.format("%.2f", c.calculateDailyLimit()), Theme.ACCENT);
        addGridStat(grid, 0, 1, "Start Date", c.getStartDate().toString(), Theme.TEXT_SECONDARY);
        addGridStat(grid, 1, 1, "End Date", c.getEndDate().toString(), Theme.TEXT_SECONDARY);
        addGridStat(grid, 0, 2, "Total Days", c.getTotalDays() + " days", Theme.ACCENT_ORANGE);
        addGridStat(grid, 1, 2, "Days Remaining", c.getRemainingDays() + " days", Theme.ACCENT_PURPLE);

        card.getChildren().addAll(header, grid);
        return card;
    }

    private VBox buildCycleCard(Cycle c) {
        VBox card = new VBox(10);
        card.setPadding(new Insets(16));
        card.setStyle(Theme.card());

        HBox header = new HBox(10);
        header.setAlignment(Pos.CENTER_LEFT);
        Label date = Theme.makeLabel(c.getDateRange(), Theme.TEXT_PRIMARY, "14px", true);
        Region sp = new Region(); HBox.setHgrow(sp, Priority.ALWAYS);

        if (c.isActive()) {
            Label activeLbl = Theme.makeLabel("ACTIVE", Theme.ACCENT_GREEN, "11px", true);
            activeLbl.setStyle(activeLbl.getStyle() +
                "-fx-background-color: " + Theme.ACCENT_GREEN + "22;" +
                "-fx-padding: 3 8;" + "-fx-background-radius: 4;");
            header.getChildren().addAll(date, sp, activeLbl);
        } else {
            Button activateBtn = Theme.makeGhostBtn("Activate");
            activateBtn.setOnAction(e -> {
                try { controller.activateCycle(c.getId()); refresh(); }
                catch (Exception ex) { showAlert(ex.getMessage()); }
            });
            Button deleteBtn = Theme.makeDangerBtn("✕");
            deleteBtn.setOnAction(e -> {
                try { controller.deleteCycle(c.getId()); refresh(); }
                catch (Exception ex) { showAlert(ex.getMessage()); }
            });
            header.getChildren().addAll(date, sp, activateBtn, deleteBtn);
        }

        Label budget = Theme.makeLabel("Budget: $" + String.format("%.2f", c.getTotalBudget()),
            Theme.ACCENT, "13px", false);
        Label days = Theme.makeLabel(c.getTotalDays() + " day cycle", Theme.TEXT_MUTED, "12px", false);

        card.getChildren().addAll(header, new HBox(16, budget, days));
        return card;
    }

    private void addGridStat(GridPane grid, int col, int row, String label, String value, String color) {
        VBox cell = new VBox(4,
            Theme.makeLabel(label, Theme.TEXT_MUTED, "11px", false),
            Theme.makeLabel(value, color, "16px", true)
        );
        grid.add(cell, col, row);
    }

    private void showAddDialog() {
        new CycleDialog(null, controller, this::refresh).showAndWait();
    }

    private void showEditDialog(Cycle c) {
        new CycleDialog(c, controller, this::refresh).showAndWait();
    }

    private void showAlert(String msg) {
        Alert a = new Alert(Alert.AlertType.ERROR, msg);
        a.getDialogPane().setStyle("-fx-background-color: " + Theme.BG_DARK + ";");
        a.showAndWait();
    }

    private static class CycleDialog extends StyledDialog {
        CycleDialog(Cycle existing, CycleController controller, Runnable onSuccess) {
            super(existing == null ? "New Budget Cycle" : "Edit Cycle",
                  "◎",
                  existing == null ? "Set your budget and period" : "Update cycle details",
                  440, 460);

            TextField budgetField = makeField("e.g. 5000.00");
            DatePicker startPicker = makeDatePicker();
            DatePicker endPicker = makeDatePicker();

            if (existing != null) {
                budgetField.setText(String.valueOf(existing.getTotalBudget()));
                startPicker.setValue(existing.getStartDate());
                endPicker.setValue(existing.getEndDate());
            } else {
                startPicker.setValue(LocalDate.now());
                endPicker.setValue(LocalDate.now().plusMonths(1));
            }

            Button submitBtn = Theme.makePrimaryBtn(existing == null ? "✓  Create Cycle" : "✓  Save Changes");
            submitBtn.setMaxWidth(Double.MAX_VALUE);
            submitBtn.setOnAction(e -> {
                try {
                    double budget = Double.parseDouble(budgetField.getText().trim());
                    LocalDate start = startPicker.getValue();
                    LocalDate end = endPicker.getValue();

                    if (budget <= 0) { setError("Budget must be positive."); return; }
                    if (start == null || end == null) { setError("Please select dates."); return; }
                    if (!end.isAfter(start)) { setError("End must be after start."); return; }

                    if (existing == null) {
                        controller.createCycle(budget, start, end);
                        setSuccess("Cycle created! Daily limit: $" + String.format("%.2f", budget / start.until(end).getDays()));
                    } else {
                        existing.setTotalBudget(budget);
                        existing.setStartDate(start);
                        existing.setEndDate(end);
                        controller.updateCycle(existing);
                        setSuccess("Cycle updated!");
                    }
                    if (onSuccess != null) onSuccess.run();

                } catch (NumberFormatException ex) {
                    setError("Please enter a valid number.");
                } catch (Exception ex) {
                    setError(ex.getMessage());
                }
            });

            addContent(
                Theme.fieldGroup("Total Budget ($)", budgetField),
                Theme.fieldGroup("Start Date", startPicker),
                Theme.fieldGroup("End Date", endPicker),
                submitBtn
            );
        }
    }
}
