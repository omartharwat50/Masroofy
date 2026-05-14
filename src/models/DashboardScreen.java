
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.chart.*;
import javafx.scene.control.*;
import javafx.scene.layout.*;


import java.util.List;
import java.util.Map;
import java.util.Optional;

public class DashboardScreen extends VBox {

    private final DashboardController controller;

    public DashboardScreen(DashboardController controller) {
        this.controller = controller;
        setStyle("-fx-background-color: " + Theme.BG_DEEP + ";");
        setSpacing(0);
        build();
    }

    public void refresh() {
        getChildren().clear();
        build();
    }

    private void build() {
        // Top bar
        HBox topBar = buildTopBar();

        ScrollPane scroll = new ScrollPane();
        scroll.setFitToWidth(true);
        scroll.setStyle("-fx-background-color: transparent; -fx-background: transparent;" +
                        "-fx-border-color: transparent;");
        VBox.setVgrow(scroll, Priority.ALWAYS);

        VBox content = new VBox(24);
        content.setPadding(new Insets(28));
        content.setStyle("-fx-background-color: " + Theme.BG_DEEP + ";");

        try {
            Optional<Cycle> cycleOpt = controller.getActiveCycle();
            int cycleId = cycleOpt.map(Cycle::getId).orElse(0);

            double totalIncome   = cycleOpt.isPresent() ? controller.getTotalIncome(cycleId) : 0;
            double totalExpenses = cycleOpt.isPresent() ? controller.getTotalExpenses(cycleId) : 0;
            double remaining     = cycleOpt.map(c -> controller.getRemainingBudget(c, totalExpenses)).orElse(0.0);
            double dailyLimit    = cycleOpt.map(c -> controller.getDailyLimit(c, totalExpenses)).orElse(0.0);
            double usagePct      = cycleOpt.map(c -> controller.getBudgetUsagePercentage(c, totalExpenses)).orElse(0.0);

            // Stat cards row
            HBox statsRow = buildStatsRow(totalIncome, totalExpenses, remaining, dailyLimit, cycleOpt);

            // Middle: chart + recent + goals
            HBox middleRow = new HBox(20);
            VBox leftCol = new VBox(20);
            leftCol.setFillWidth(true);
            HBox.setHgrow(leftCol, Priority.ALWAYS);

            leftCol.getChildren().addAll(
                buildBudgetProgress(usagePct, totalExpenses, cycleOpt),
                buildCategoryChart(cycleId)
            );

            VBox rightCol = new VBox(20);
            rightCol.setPrefWidth(320);
            rightCol.setMinWidth(280);
            rightCol.getChildren().addAll(
                buildRecentTransactions(),
                buildGoalsSummary()
            );

            middleRow.getChildren().addAll(leftCol, rightCol);

            content.getChildren().addAll(statsRow, middleRow);

        } catch (Exception e) {
            Label err = Theme.makeLabel("Failed to load dashboard: " + e.getMessage(),
                Theme.ACCENT_RED, "14px", false);
            content.getChildren().add(err);
        }

        scroll.setContent(content);
        getChildren().addAll(topBar, scroll);
    }

    private HBox buildTopBar() {
        HBox bar = new HBox();
        bar.setPadding(new Insets(20, 28, 20, 28));
        bar.setAlignment(Pos.CENTER_LEFT);
        bar.setStyle("-fx-background-color: " + Theme.BG_DARK + ";" +
                     "-fx-border-color: " + Theme.BORDER + ";" +
                     "-fx-border-width: 0 0 1 0;");

        Label title = Theme.makeLabel("Dashboard", Theme.TEXT_PRIMARY, "24px", true);
        Label sub = Theme.makeLabel("Your financial overview at a glance", Theme.TEXT_SECONDARY, "13px", false);
        VBox titles = new VBox(3, title, sub);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Button refreshBtn = Theme.makeGhostBtn("↻  Refresh");
        refreshBtn.setOnAction(e -> refresh());

        bar.getChildren().addAll(titles, spacer, refreshBtn);
        return bar;
    }

    private HBox buildStatsRow(double income, double expenses, double remaining, double daily, Optional<Cycle> cycle) {
        HBox row = new HBox(16);
        row.setFillHeight(true);

        row.getChildren().addAll(
            statCard("Total Income", String.format("$%.2f", income), Theme.ACCENT_GREEN, "↑"),
            statCard("Total Expenses", String.format("$%.2f", expenses), Theme.ACCENT_RED, "↓"),
            statCard("Remaining", String.format("$%.2f", remaining),
                remaining >= 0 ? Theme.ACCENT : Theme.ACCENT_RED, "◈"),
            statCard("Daily Limit", String.format("$%.2f", daily), Theme.ACCENT_ORANGE, "◇"),
            statCard("Cycle Status",
                cycle.isPresent() ? (cycle.get().getRemainingDays() + " days left") : "No cycle",
                Theme.ACCENT_PURPLE, "◎")
        );

        for (javafx.scene.Node n : row.getChildren()) {
            HBox.setHgrow(n, Priority.ALWAYS);
        }
        return row;
    }

    private VBox statCard(String title, String value, String color, String icon) {
        Label iconLbl = new Label(icon);
        iconLbl.setStyle("-fx-text-fill: " + color + "; -fx-font-size: 20px;");

        Label titleLbl = Theme.makeLabel(title, Theme.TEXT_SECONDARY, "12px", false);
        Label valueLbl = Theme.makeLabel(value, color, "22px", true);

        VBox card = new VBox(8, iconLbl, titleLbl, valueLbl);
        card.setPadding(new Insets(18));
        card.setStyle(Theme.card());
        return card;
    }

    private VBox buildBudgetProgress(double usagePct, double expenses, Optional<Cycle> cycle) {
        VBox card = new VBox(14);
        card.setPadding(new Insets(20));
        card.setStyle(Theme.card());

        Label title = Theme.makeLabel("Budget Usage", Theme.TEXT_PRIMARY, "16px", true);

        // Progress bar
        ProgressBar bar = new ProgressBar(Math.min(usagePct / 100.0, 1.0));
        bar.setMaxWidth(Double.MAX_VALUE);
        bar.setPrefHeight(10);
        String barColor = usagePct >= 100 ? Theme.ACCENT_RED :
                          usagePct >= 80  ? Theme.ACCENT_ORANGE : Theme.ACCENT_GREEN;
        bar.setStyle("-fx-accent: " + barColor + ";" +
                     "-fx-background-color: " + Theme.BG_INPUT + ";" +
                     "-fx-background-radius: 6;" +
                     "-fx-border-radius: 6;");

        String pctText = String.format("%.1f%% used", usagePct);
        String budgetStr = cycle.map(c -> "$" + String.format("%.2f", c.getTotalBudget()) + " budget").orElse("No active cycle");

        HBox row = new HBox();
        Label pctLbl = Theme.makeLabel(pctText, barColor, "13px", true);
        Region sp = new Region(); HBox.setHgrow(sp, Priority.ALWAYS);
        Label budLbl = Theme.makeLabel(budgetStr, Theme.TEXT_SECONDARY, "13px", false);
        row.getChildren().addAll(pctLbl, sp, budLbl);

        if (usagePct >= 80) {
            String warningMsg = usagePct >= 100 ? "⚠ Budget exceeded!" : "⚠ Approaching budget limit";
            Label warn = Theme.makeLabel(warningMsg, Theme.ACCENT_ORANGE, "12px", false);
            card.getChildren().addAll(title, bar, row, warn);
        } else {
            card.getChildren().addAll(title, bar, row);
        }

        return card;
    }

    private VBox buildCategoryChart(int cycleId) {
        VBox card = new VBox(14);
        card.setPadding(new Insets(20));
        card.setStyle(Theme.card());
        VBox.setVgrow(card, Priority.ALWAYS);

        Label title = Theme.makeLabel("Expenses by Category", Theme.TEXT_PRIMARY, "16px", true);
        card.getChildren().add(title);

        try {
            Map<String, Double> data = controller.getExpensesByCategory(cycleId);
            if (data.isEmpty()) {
                Label empty = Theme.makeLabel("No expense data for current cycle.", Theme.TEXT_SECONDARY, "13px", false);
                card.getChildren().add(empty);
            } else {
                PieChart pie = new PieChart();
                pie.setLegendVisible(false);
                pie.setLabelsVisible(true);
                pie.setStyle("-fx-background-color: transparent;");
                pie.setPrefHeight(220);

                for (Map.Entry<String, Double> entry : data.entrySet()) {
                    PieChart.Data slice = new PieChart.Data(
                        entry.getKey() + " $" + String.format("%.0f", entry.getValue()), entry.getValue());
                    pie.getData().add(slice);
                }
                card.getChildren().add(pie);
            }
        } catch (Exception e) {
            card.getChildren().add(Theme.makeLabel("Error loading chart.", Theme.ACCENT_RED, "13px", false));
        }

        return card;
    }

    private VBox buildRecentTransactions() {
        VBox card = new VBox(10);
        card.setPadding(new Insets(18));
        card.setStyle(Theme.card());

        Label title = Theme.makeLabel("Recent Transactions", Theme.TEXT_PRIMARY, "15px", true);
        card.getChildren().add(title);

        try {
            List<Transaction> recent = controller.getRecentTransactions(6);
            if (recent.isEmpty()) {
                card.getChildren().add(Theme.makeLabel("No transactions yet.", Theme.TEXT_SECONDARY, "13px", false));
            } else {
                for (Transaction t : recent) {
                    card.getChildren().add(buildTransactionRow(t));
                }
            }
        } catch (Exception e) {
            card.getChildren().add(Theme.makeLabel("Error loading transactions.", Theme.ACCENT_RED, "13px", false));
        }

        return card;
    }

    private HBox buildTransactionRow(Transaction t) {
        HBox row = new HBox(10);
        row.setAlignment(Pos.CENTER_LEFT);
        row.setPadding(new Insets(6, 0, 6, 0));
        row.setStyle("-fx-border-color: " + Theme.BORDER + "; -fx-border-width: 0 0 1 0;");

        String typeIcon = t.getType() == TransactionType.INCOME ? "↑" : "↓";
        String typeColor = t.getType() == TransactionType.INCOME ? Theme.ACCENT_GREEN : Theme.ACCENT_RED;
        Label icon = Theme.makeLabel(typeIcon, typeColor, "14px", true);

        VBox info = new VBox(2);
        Label titleLbl = Theme.makeLabel(t.getTitle(), Theme.TEXT_PRIMARY, "13px", false);
        Label dateLbl = Theme.makeLabel(t.getDate().toString(), Theme.TEXT_MUTED, "11px", false);
        info.getChildren().addAll(titleLbl, dateLbl);
        HBox.setHgrow(info, Priority.ALWAYS);

        String sign = t.getType() == TransactionType.INCOME ? "+" : "-";
        Label amtLbl = Theme.makeLabel(sign + "$" + String.format("%.2f", t.getAmount()),
            typeColor, "13px", true);

        row.getChildren().addAll(icon, info, amtLbl);
        return row;
    }

    private VBox buildGoalsSummary() {
        VBox card = new VBox(10);
        card.setPadding(new Insets(18));
        card.setStyle(Theme.card());

        Label title = Theme.makeLabel("Savings Goals", Theme.TEXT_PRIMARY, "15px", true);
        card.getChildren().add(title);

        try {
            List<Goal> goals = controller.getAllGoals();
            if (goals.isEmpty()) {
                card.getChildren().add(Theme.makeLabel("No goals yet.", Theme.TEXT_SECONDARY, "13px", false));
            } else {
                for (Goal g : goals.stream().limit(4).toList()) {
                    card.getChildren().add(buildGoalRow(g));
                }
            }
        } catch (Exception e) {
            card.getChildren().add(Theme.makeLabel("Error loading goals.", Theme.ACCENT_RED, "13px", false));
        }

        return card;
    }

    private VBox buildGoalRow(Goal g) {
        VBox box = new VBox(6);
        box.setPadding(new Insets(6, 0, 6, 0));
        box.setStyle("-fx-border-color: " + Theme.BORDER + "; -fx-border-width: 0 0 1 0;");

        HBox header = new HBox(8);
        Label icon = Theme.makeLabel(g.getIcon(), Theme.ACCENT, "14px", false);
        Label name = Theme.makeLabel(g.getTitle(), Theme.TEXT_PRIMARY, "13px", false);
        Region sp = new Region(); HBox.setHgrow(sp, Priority.ALWAYS);
        Label pct = Theme.makeLabel(String.format("%.0f%%", g.getProgressPercentage()),
            g.isCompleted() ? Theme.ACCENT_GREEN : Theme.ACCENT_ORANGE, "12px", true);
        header.getChildren().addAll(icon, name, sp, pct);

        ProgressBar bar = new ProgressBar(g.getProgressPercentage() / 100.0);
        bar.setMaxWidth(Double.MAX_VALUE);
        bar.setPrefHeight(6);
        bar.setStyle("-fx-accent: " + (g.isCompleted() ? Theme.ACCENT_GREEN : Theme.ACCENT_ORANGE) + ";" +
                     "-fx-background-color: " + Theme.BG_INPUT + ";" +
                     "-fx-background-radius: 4;");

        Label amounts = Theme.makeLabel(
            "$" + String.format("%.0f", g.getSavedAmount()) + " / $" + String.format("%.0f", g.getTargetAmount()),
            Theme.TEXT_MUTED, "11px", false);

        box.getChildren().addAll(header, bar, amounts);
        return box;
    }
}
