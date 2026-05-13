import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.chart.*;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;
import java.util.List;
import javafx.collections.ObservableList;
/**
 * ChartsUI — renders Pie Chart and Bar Chart for spending analytics.
 * Relies on JavaFX Charts (bundled with JavaFX SDK ≥ 11).
 */
public class ChartsUI {

    private final ExpenseController expenseController;
    private final CycleController   cycleController;

    // ===== THEME =====
    private static final String DARK_NAVY   = "#0D1B2A";
    private static final String NAVY        = "#1B2A3B";
    private static final String ACCENT_BLUE = "#1E90FF";
    private static final String LIGHT_BLUE  = "#4DB8FF";
    private static final String TEXT_WHITE  = "#F0F8FF";
    private static final String TEXT_MUTED  = "#90B8D8";

    // Palette for chart slices / bars
    private static final String[] CHART_COLORS = {
        "#1E90FF", "#00BFFF", "#4169E1", "#6495ED",
        "#87CEEB", "#4682B4", "#5F9EA0", "#00CED1"
    };

    public ChartsUI(ExpenseController expenseController, CycleController cycleController) {
        this.expenseController = expenseController;
        this.cycleController   = cycleController;
    }

    // ================= MAIN CHARTS DIALOG =================
    public void showChartsDialog() {
        Stage dialog = new Stage();
        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.setTitle("📈 Charts & Analytics");

        // Header
        Label icon   = new Label("📊");
        icon.setStyle("-fx-font-size: 34px;");
        Label header = new Label("Spending Analytics");
        header.setStyle("-fx-font-size: 22px; -fx-font-weight: bold; -fx-text-fill: " + TEXT_WHITE + ";");
        Label sub    = new Label("Visual breakdown of your expenses by category");
        sub.setStyle("-fx-font-size: 13px; -fx-text-fill: " + TEXT_MUTED + ";");
        VBox headerBox = new VBox(6, icon, header, sub);
        headerBox.setAlignment(Pos.CENTER);
        headerBox.setPadding(new Insets(24, 0, 16, 0));

        // Tab pane for Pie and Bar charts
        TabPane tabPane = new TabPane();
        tabPane.setStyle(
            "-fx-background-color: " + DARK_NAVY + ";" +
            "-fx-tab-min-height: 36px;"
        );
        tabPane.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);

        Tab pieTab = new Tab("🥧  Pie Chart",  buildPieChartTab());
        Tab barTab = new Tab("📊  Bar Chart",   buildBarChartTab());
        Tab sumTab = new Tab("📋  Summary",     buildSummaryTab());

        tabPane.getTabs().addAll(pieTab, barTab, sumTab);
        VBox.setVgrow(tabPane, Priority.ALWAYS);

        VBox root = new VBox(headerBox, tabPane);
        root.setStyle("-fx-background-color: " + DARK_NAVY + ";");

        dialog.setScene(new Scene(root, 760, 640));
        dialog.showAndWait();
    }

    // ================= PIE CHART TAB =================
    private VBox buildPieChartTab() {
        VBox container = new VBox(16);
        container.setPadding(new Insets(20));
        container.setStyle("-fx-background-color: " + DARK_NAVY + ";");

        try {
            List<Category> categories = expenseController.getTransactionsByCategory();

            if (categories.isEmpty()) {
                container.getChildren().add(emptyLabel("No expense data available yet."));
                return container;
            }

            ObservableList<PieChart.Data> pieData = FXCollections.observableArrayList();
            double grandTotal = 0;

            for (Category cat : categories) {
                double catTotal = cat.getTransactions().stream()
                        .mapToDouble(Transaction::getAmount).sum();
                if (catTotal > 0) {
                    pieData.add(new PieChart.Data(cat.getName(), catTotal));
                    grandTotal += catTotal;
                }
            }

            if (pieData.isEmpty()) {
                container.getChildren().add(emptyLabel("No amounts recorded yet."));
                return container;
            }

            PieChart chart = new PieChart(pieData);
            chart.setTitle("Spending by Category");
            chart.setLegendVisible(true);
            chart.setLabelsVisible(true);
            chart.setStyle("-fx-background-color: transparent;");
            chart.setPrefHeight(420);

            // Apply blue palette to slices
            int i = 0;
            for (PieChart.Data slice : chart.getData()) {
                String color = CHART_COLORS[i % CHART_COLORS.length];
                slice.getNode().setStyle("-fx-pie-color: " + color + ";");
                i++;
            }

            // Tooltip on hover
            final double total = grandTotal;
            for (PieChart.Data slice : chart.getData()) {
                Tooltip tip = new Tooltip(
                    String.format("%s\n$%.2f  (%.1f%%)",
                        slice.getName(),
                        slice.getPieValue(),
                        (slice.getPieValue() / total) * 100));
                tip.setStyle("-fx-font-size: 13px;");
                Tooltip.install(slice.getNode(), tip);
            }

            Label totalLabel = new Label(String.format("Grand Total Spending:  $%.2f", grandTotal));
            totalLabel.setStyle(
                "-fx-font-size: 15px; -fx-font-weight: bold; -fx-text-fill: " + LIGHT_BLUE + ";");

            container.getChildren().addAll(chart, totalLabel);

        } catch (Exception e) {
            container.getChildren().add(emptyLabel("Error loading chart data: " + e.getMessage()));
        }

        return container;
    }

    // ================= BAR CHART TAB =================
    private VBox buildBarChartTab() {
        VBox container = new VBox(16);
        container.setPadding(new Insets(20));
        container.setStyle("-fx-background-color: " + DARK_NAVY + ";");

        try {
            List<Category> categories = expenseController.getTransactionsByCategory();

            if (categories.isEmpty()) {
                container.getChildren().add(emptyLabel("No expense data available yet."));
                return container;
            }

            CategoryAxis xAxis = new CategoryAxis();
            xAxis.setLabel("Category");
            xAxis.setStyle("-fx-tick-label-fill: " + TEXT_MUTED + ";");

            NumberAxis yAxis = new NumberAxis();
            yAxis.setLabel("Amount ($)");
            yAxis.setStyle("-fx-tick-label-fill: " + TEXT_MUTED + ";");

            BarChart<String, Number> barChart = new BarChart<>(xAxis, yAxis);
            barChart.setTitle("Spending per Category");
            barChart.setLegendVisible(false);
            barChart.setStyle("-fx-background-color: transparent;");
            barChart.setPrefHeight(420);
            barChart.setAnimated(true);

            XYChart.Series<String, Number> series = new XYChart.Series<>();
            series.setName("Expenses");

            int colorIdx = 0;
            for (Category cat : categories) {
                double catTotal = cat.getTransactions().stream()
                        .mapToDouble(Transaction::getAmount).sum();
                if (catTotal > 0) {
                    XYChart.Data<String, Number> bar =
                        new XYChart.Data<>(cat.getName(), catTotal);
                    series.getData().add(bar);

                    // Style bar color after chart renders
                    final String color = CHART_COLORS[colorIdx % CHART_COLORS.length];
                    bar.nodeProperty().addListener((obs, oldNode, newNode) -> {
                        if (newNode != null) {
                            newNode.setStyle("-fx-bar-fill: " + color + ";");
                        }
                    });
                    colorIdx++;
                }
            }

            barChart.getData().add(series);
            container.getChildren().add(barChart);

        } catch (Exception e) {
            container.getChildren().add(emptyLabel("Error loading chart data: " + e.getMessage()));
        }

        return container;
    }

    // ================= SUMMARY TAB =================
    private ScrollPane buildSummaryTab() {
        VBox container = new VBox(12);
        container.setPadding(new Insets(20));
        container.setStyle("-fx-background-color: " + DARK_NAVY + ";");

        try {
            List<Category> categories = expenseController.getTransactionsByCategory();
            double grandTotal = 0;

            for (Category cat : categories) {
                double catTotal = cat.getTransactions().stream()
                        .mapToDouble(Transaction::getAmount).sum();
                grandTotal += catTotal;
            }

            for (Category cat : categories) {
                double catTotal = cat.getTransactions().stream()
                        .mapToDouble(Transaction::getAmount).sum();

                double pct = grandTotal > 0 ? (catTotal / grandTotal) * 100 : 0;

                Label name = new Label("📁  " + cat.getName());
                name.setStyle("-fx-font-weight: bold; -fx-font-size: 14px; -fx-text-fill: " + LIGHT_BLUE + ";");

                ProgressBar bar = new ProgressBar(pct / 100.0);
                bar.setMaxWidth(Double.MAX_VALUE);
                bar.setStyle("-fx-accent: " + ACCENT_BLUE + ";");
                bar.setPrefHeight(12);

                Label pctLabel = new Label(String.format("$%.2f   (%.1f%%)", catTotal, pct));
                pctLabel.setStyle("-fx-font-size: 13px; -fx-text-fill: " + TEXT_MUTED + ";");

                VBox row = new VBox(4, name, bar, pctLabel);
                row.setPadding(new Insets(12));
                row.setStyle(
                    "-fx-background-color: " + NAVY + ";" +
                    "-fx-background-radius: 8;" +
                    "-fx-border-color: #1E3A5F;" +
                    "-fx-border-radius: 8;" +
                    "-fx-border-width: 1;"
                );
                container.getChildren().add(row);
            }

            Label totalLabel = new Label(String.format("💰  Grand Total:  $%.2f", grandTotal));
            totalLabel.setStyle(
                "-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: " + ACCENT_BLUE + ";");
            container.getChildren().add(totalLabel);

        } catch (Exception e) {
            container.getChildren().add(emptyLabel("Error loading summary: " + e.getMessage()));
        }

        ScrollPane scroll = new ScrollPane(container);
        scroll.setFitToWidth(true);
        scroll.setStyle("-fx-background-color: " + DARK_NAVY + "; -fx-background: " + DARK_NAVY + ";");
        return scroll;
    }

    // ===== HELPERS =====
    private Label emptyLabel(String msg) {
        Label lbl = new Label(msg);
        lbl.setStyle("-fx-text-fill: " + TEXT_MUTED + "; -fx-font-size: 14px;");
        return lbl;
    }
}
