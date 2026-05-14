
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public class GoalsScreen extends VBox {

    private final GoalController controller;
    private VBox goalsGrid;

    public GoalsScreen(GoalController controller) {
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

        Label title = Theme.makeLabel("Savings Goals", Theme.TEXT_PRIMARY, "24px", true);
        Label sub = Theme.makeLabel("Set targets and track your savings progress", Theme.TEXT_SECONDARY, "13px", false);
        VBox titles = new VBox(3, title, sub);
        Region spacer = new Region(); HBox.setHgrow(spacer, Priority.ALWAYS);

        Button newBtn = Theme.makePrimaryBtn("+ New Goal");
        newBtn.setOnAction(e -> { showAddDialog(); refresh(); });

        bar.getChildren().addAll(titles, spacer, newBtn);
        return bar;
    }

    private ScrollPane buildBody() {
        goalsGrid = new VBox(16);
        goalsGrid.setPadding(new Insets(24, 28, 24, 28));
        goalsGrid.setStyle("-fx-background-color: " + Theme.BG_DEEP + ";");

        try {
            List<Goal> goals = controller.getAllGoals();
            if (goals.isEmpty()) {
                VBox empty = new VBox(12);
                empty.setAlignment(Pos.CENTER);
                empty.setPadding(new Insets(60));
                empty.getChildren().addAll(
                    Theme.makeLabel("◇", Theme.ACCENT, "48px", false),
                    Theme.makeLabel("No savings goals yet", Theme.TEXT_PRIMARY, "18px", true),
                    Theme.makeLabel("Create your first goal to start tracking your progress", Theme.TEXT_SECONDARY, "14px", false)
                );
                goalsGrid.getChildren().add(empty);
            } else {
                // Summary row
                double totalTarget = goals.stream().mapToDouble(Goal::getTargetAmount).sum();
                double totalSaved  = goals.stream().mapToDouble(Goal::getSavedAmount).sum();
                long completed     = goals.stream().filter(Goal::isCompleted).count();
                goalsGrid.getChildren().add(buildSummaryRow(goals.size(), totalTarget, totalSaved, (int) completed));

                // Goal cards in a 2-col grid
                FlowPane flow = new FlowPane(16, 16);
                flow.setPrefWrapLength(900);
                for (Goal g : goals) {
                    flow.getChildren().add(buildGoalCard(g));
                }
                goalsGrid.getChildren().add(flow);
            }
        } catch (Exception e) {
            goalsGrid.getChildren().add(Theme.makeLabel("Error: " + e.getMessage(), Theme.ACCENT_RED, "14px", false));
        }

        ScrollPane scroll = new ScrollPane(goalsGrid);
        scroll.setFitToWidth(true);
        scroll.setStyle("-fx-background-color: transparent; -fx-background: transparent;" +
                        "-fx-border-color: transparent;");
        VBox.setVgrow(scroll, Priority.ALWAYS);
        return scroll;
    }

    private HBox buildSummaryRow(int total, double target, double saved, int completed) {
        HBox row = new HBox(16);
        row.getChildren().addAll(
            summaryCard("Total Goals", String.valueOf(total), Theme.ACCENT),
            summaryCard("Total Target", "$" + String.format("%.2f", target), Theme.ACCENT_ORANGE),
            summaryCard("Total Saved", "$" + String.format("%.2f", saved), Theme.ACCENT_GREEN),
            summaryCard("Completed", String.valueOf(completed), Theme.ACCENT_PURPLE)
        );
        for (javafx.scene.Node n : row.getChildren()) HBox.setHgrow(n, Priority.ALWAYS);
        return row;
    }

    private VBox summaryCard(String label, String value, String color) {
        VBox card = new VBox(6,
            Theme.makeLabel(label, Theme.TEXT_SECONDARY, "12px", false),
            Theme.makeLabel(value, color, "22px", true)
        );
        card.setPadding(new Insets(16));
        card.setStyle(Theme.card());
        return card;
    }

    private VBox buildGoalCard(Goal g) {
        VBox card = new VBox(14);
        card.setPadding(new Insets(20));
        card.setPrefWidth(340);
        String cardStyle = g.isCompleted() ? Theme.cardElevated() : Theme.card();
        card.setStyle(cardStyle);

        // Header
        HBox header = new HBox(10);
        header.setAlignment(Pos.CENTER_LEFT);
        Label icon = Theme.makeLabel(g.getIcon(), Theme.ACCENT, "22px", false);
        Label name = Theme.makeLabel(g.getTitle(), Theme.TEXT_PRIMARY, "15px", true);
        Region sp = new Region(); HBox.setHgrow(sp, Priority.ALWAYS);

        String statusText = g.isCompleted() ? "✓ Done" : g.isOverdue() ? "Overdue" : g.getDaysRemaining() + "d left";
        String statusColor = g.isCompleted() ? Theme.ACCENT_GREEN : g.isOverdue() ? Theme.ACCENT_RED : Theme.TEXT_SECONDARY;
        Label status = Theme.makeLabel(statusText, statusColor, "12px", true);

        header.getChildren().addAll(icon, name, sp, status);

        // Progress
        double pct = g.getProgressPercentage();
        ProgressBar bar = new ProgressBar(pct / 100.0);
        bar.setMaxWidth(Double.MAX_VALUE);
        bar.setPrefHeight(8);
        String barColor = g.isCompleted() ? Theme.ACCENT_GREEN : pct >= 70 ? Theme.ACCENT_ORANGE : Theme.ACCENT;
        bar.setStyle("-fx-accent: " + barColor + ";" +
                     "-fx-background-color: " + Theme.BG_INPUT + ";" +
                     "-fx-background-radius: 5;");

        HBox amounts = new HBox();
        Label savedLbl = Theme.makeLabel("$" + String.format("%.2f", g.getSavedAmount()), Theme.ACCENT_GREEN, "13px", true);
        Region amSp = new Region(); HBox.setHgrow(amSp, Priority.ALWAYS);
        Label targetLbl = Theme.makeLabel("/ $" + String.format("%.2f", g.getTargetAmount()), Theme.TEXT_MUTED, "13px", false);
        Label pctLbl = Theme.makeLabel(String.format("%.0f%%", pct), barColor, "13px", true);
        amounts.getChildren().addAll(savedLbl, amSp, targetLbl, new Label("  "), pctLbl);

        Label deadline = Theme.makeLabel("Deadline: " + g.getDeadline(), Theme.TEXT_MUTED, "11px", false);

        // Actions
        HBox actions = new HBox(8);
        if (!g.isCompleted()) {
            Button addBtn = Theme.makePrimaryBtn("+ Add Savings");
            addBtn.setOnAction(e -> showAddSavingsDialog(g));
            actions.getChildren().add(addBtn);
        }
        Button editBtn = Theme.makeGhostBtn("✎ Edit");
        editBtn.setOnAction(e -> { showEditDialog(g); refresh(); });
        Button delBtn = Theme.makeDangerBtn("✕");
        delBtn.setOnAction(e -> {
            try { controller.deleteGoal(g.getId()); refresh(); }
            catch (Exception ex) { /* ignore */ }
        });
        actions.getChildren().addAll(editBtn, delBtn);

        card.getChildren().addAll(header, bar, amounts, deadline, actions);
        return card;
    }

    private void showAddDialog() {
        new GoalDialog(null, controller, this::refresh).showAndWait();
    }

    private void showEditDialog(Goal g) {
        new GoalDialog(g, controller, this::refresh).showAndWait();
    }

    private void showAddSavingsDialog(Goal g) {
        StyledDialog dialog = new StyledDialog("Add Savings", "💰",
            "Add to your savings for \"" + g.getTitle() + "\"", 380, 300);

        TextField amtField = dialog.makeField("Amount to add...");
        Button btn = Theme.makePrimaryBtn("✓  Add Savings");
        btn.setMaxWidth(Double.MAX_VALUE);
        btn.setOnAction(e -> {
            try {
                double amt = Double.parseDouble(amtField.getText().trim());
                controller.addToSavings(g.getId(), amt);
                dialog.setSuccess("$" + String.format("%.2f", amt) + " added!");
                refresh();
            } catch (NumberFormatException ex) {
                dialog.setError("Enter a valid number.");
            } catch (Exception ex) {
                dialog.setError(ex.getMessage());
            }
        });

        dialog.addContent(Theme.fieldGroup("Amount ($)", amtField), btn);
        dialog.showAndWait();
    }

    private static class GoalDialog extends StyledDialog {
        GoalDialog(Goal existing, GoalController controller, Runnable onSuccess) {
            super(existing == null ? "New Goal" : "Edit Goal",
                  "◇",
                  existing == null ? "Set a new savings target" : "Update your goal",
                  440, 460);

            String[] icons = {"🎯", "🏠", "✈️", "🚗", "💻", "📚", "💒", "🌴", "💪", "🎓"};
            TextField titleField = makeField("Goal title (e.g. Emergency Fund)");
            TextField targetField = makeField("Target amount (e.g. 10000.00)");
            DatePicker deadlinePicker = makeDatePicker();

            ComboBox<String> iconCombo = new ComboBox<>();
            iconCombo.setMaxWidth(Double.MAX_VALUE);
            iconCombo.setStyle(Theme.inputField());
            iconCombo.getItems().addAll(icons);
            iconCombo.setValue("🎯");

            if (existing != null) {
                titleField.setText(existing.getTitle());
                targetField.setText(String.valueOf(existing.getTargetAmount()));
                deadlinePicker.setValue(existing.getDeadline());
                iconCombo.setValue(existing.getIcon());
            } else {
                deadlinePicker.setValue(LocalDate.now().plusMonths(6));
            }

            Button btn = Theme.makePrimaryBtn(existing == null ? "✓  Create Goal" : "✓  Save Changes");
            btn.setMaxWidth(Double.MAX_VALUE);
            btn.setOnAction(e -> {
                try {
                    String title = titleField.getText().trim();
                    double target = Double.parseDouble(targetField.getText().trim());
                    LocalDate deadline = deadlinePicker.getValue();
                    String icon = iconCombo.getValue();

                    if (title.isBlank()) { setError("Title required."); return; }
                    if (target <= 0) { setError("Target must be positive."); return; }
                    if (deadline == null) { setError("Select a deadline."); return; }

                    if (existing == null) {
                        controller.createGoal(title, target, deadline, icon);
                        setSuccess("Goal created!");
                    } else {
                        existing.setTitle(title);
                        existing.setTargetAmount(target);
                        existing.setDeadline(deadline);
                        existing.setIcon(icon);
                        controller.updateGoal(existing);
                        setSuccess("Goal updated!");
                    }
                    if (onSuccess != null) onSuccess.run();
                } catch (NumberFormatException ex) {
                    setError("Enter a valid number.");
                } catch (Exception ex) {
                    setError(ex.getMessage());
                }
            });

            addContent(
                Theme.fieldGroup("Icon", iconCombo),
                Theme.fieldGroup("Title", titleField),
                Theme.fieldGroup("Target Amount ($)", targetField),
                Theme.fieldGroup("Deadline", deadlinePicker),
                btn
            );
        }
    }
}
