
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

import java.util.function.Consumer;

public class Sidebar extends VBox {

    private Button activeBtn = null;

    public Sidebar(Consumer<String> onNavigate, Runnable onExit) {
        setStyle("-fx-background-color: " + Theme.BG_SIDEBAR + ";" +
                 "-fx-border-color: " + Theme.BORDER + ";" +
                 "-fx-border-width: 0 1 0 0;");
        setPrefWidth(240);
        setSpacing(2);

        // Logo section
        VBox logoBox = buildLogo();

        // Nav items
        Button btnDashboard    = navBtn("⬡", "Dashboard");
        Button btnTransactions = navBtn("↕", "Transactions");
        Button btnCycle        = navBtn("◎", "Budget Cycle");
        Button btnGoals        = navBtn("◇", "Goals");

        // Set dashboard active by default
        setActive(btnDashboard);

        btnDashboard.setOnAction(e -> { setActive(btnDashboard); onNavigate.accept("dashboard"); });
        btnTransactions.setOnAction(e -> { setActive(btnTransactions); onNavigate.accept("transactions"); });
        btnCycle.setOnAction(e -> { setActive(btnCycle); onNavigate.accept("cycle"); });
        btnGoals.setOnAction(e -> { setActive(btnGoals); onNavigate.accept("goals"); });

        Separator sep = new Separator();
        sep.setStyle("-fx-background-color: " + Theme.BORDER + ";");
        sep.setPadding(new Insets(8, 0, 8, 0));

        Button btnExit = navBtn("⏻", "Exit");
        btnExit.setStyle(btnExit.getStyle() + " -fx-text-fill: " + Theme.ACCENT_RED + ";");
        btnExit.setOnAction(e -> onExit.run());

        Region spacer = new Region();
        VBox.setVgrow(spacer, Priority.ALWAYS);

        getChildren().addAll(
            logoBox,
            makeSeparator(),
            btnDashboard, btnTransactions, btnCycle, btnGoals,
            spacer,
            makeSeparator(),
            btnExit
        );

        setPadding(new Insets(0, 0, 16, 0));
    }

    private VBox buildLogo() {
        Label icon = new Label("◈");
        icon.setStyle("-fx-text-fill: " + Theme.ACCENT + "; -fx-font-size: 32px;");

        Label title = new Label("BudgetFlow");
        title.setStyle("-fx-text-fill: " + Theme.TEXT_PRIMARY + "; -fx-font-size: 18px;" +
                       "-fx-font-weight: bold; -fx-font-family: 'Segoe UI';");

        Label sub = new Label("Personal Finance");
        sub.setStyle("-fx-text-fill: " + Theme.TEXT_MUTED + "; -fx-font-size: 11px; -fx-font-family: 'Segoe UI';");

        VBox box = new VBox(4, icon, title, sub);
        box.setAlignment(Pos.CENTER);
        box.setPadding(new Insets(28, 16, 20, 16));
        return box;
    }

    private Button navBtn(String icon, String label) {
        Button btn = new Button("  " + icon + "  " + label);
        btn.setMaxWidth(Double.MAX_VALUE);
        btn.setAlignment(Pos.CENTER_LEFT);
        String base = "-fx-background-color: transparent;" +
                      "-fx-text-fill: " + Theme.TEXT_SECONDARY + ";" +
                      "-fx-font-size: 14px;" +
                      "-fx-font-family: 'Segoe UI';" +
                      "-fx-padding: 13 20;" +
                      "-fx-cursor: hand;" +
                      "-fx-background-radius: 0;";
        String hover = "-fx-background-color: " + Theme.BG_ELEVATED + ";" +
                       "-fx-text-fill: " + Theme.TEXT_PRIMARY + ";" +
                       "-fx-font-size: 14px;" +
                       "-fx-font-family: 'Segoe UI';" +
                       "-fx-padding: 13 20;" +
                       "-fx-cursor: hand;" +
                       "-fx-background-radius: 0;";
        btn.setStyle(base);
        btn.setUserData(base);
        btn.setOnMouseEntered(e -> { if (btn != activeBtn) btn.setStyle(hover); });
        btn.setOnMouseExited(e -> { if (btn != activeBtn) btn.setStyle(base); });
        return btn;
    }

    private void setActive(Button btn) {
        if (activeBtn != null) {
            activeBtn.setStyle((String) activeBtn.getUserData());
        }
        activeBtn = btn;
        btn.setStyle("-fx-background-color: " + Theme.ACCENT_DIM + ";" +
                     "-fx-text-fill: " + Theme.ACCENT + ";" +
                     "-fx-font-size: 14px;" +
                     "-fx-font-family: 'Segoe UI';" +
                     "-fx-font-weight: bold;" +
                     "-fx-padding: 13 20;" +
                     "-fx-border-color: " + Theme.ACCENT + ";" +
                     "-fx-border-width: 0 0 0 3;" +
                     "-fx-background-radius: 0;");
    }

    private Separator makeSeparator() {
        Separator s = new Separator();
        s.setStyle("-fx-background-color: " + Theme.BORDER + ";");
        s.setPadding(new Insets(4, 0, 4, 0));
        return s;
    }
}
