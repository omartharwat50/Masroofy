
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class StyledDialog extends Stage {

    protected VBox root;
    protected Label statusLabel;

    public StyledDialog(String title, String icon, String subtitle, double width, double height) {
        initModality(Modality.APPLICATION_MODAL);
        setTitle(title);

        // Header
        Label iconLbl = new Label(icon);
        iconLbl.setStyle("-fx-font-size: 32px;");
        Label headerLbl = Theme.makeLabel(title, Theme.TEXT_PRIMARY, "20px", true);
        Label subLbl = Theme.makeLabel(subtitle, Theme.TEXT_SECONDARY, "13px", false);
        VBox header = new VBox(6, iconLbl, headerLbl, subLbl);
        header.setAlignment(Pos.CENTER);
        header.setPadding(new Insets(28, 0, 20, 0));
        header.setStyle("-fx-background-color: " + Theme.BG_DARK + ";" +
                        "-fx-border-color: " + Theme.BORDER + ";" +
                        "-fx-border-width: 0 0 1 0;");

        statusLabel = new Label();
        statusLabel.setWrapText(true);
        statusLabel.setStyle("-fx-font-size: 13px; -fx-font-family: 'Segoe UI';");

        root = new VBox();
        root.setStyle("-fx-background-color: " + Theme.BG_DARK + ";");
        root.getChildren().add(header);

        Scene scene = new Scene(root, width, height);
        setScene(scene);
    }

    protected void addContent(javafx.scene.Node... nodes) {
        VBox content = new VBox(12);
        content.setPadding(new Insets(20, 28, 24, 28));
        content.getChildren().addAll(nodes);
        content.getChildren().add(statusLabel);
        root.getChildren().add(content);
    }

    protected void setError(String msg) {
        statusLabel.setText("✗  " + msg);
        statusLabel.setStyle("-fx-text-fill: " + Theme.ACCENT_RED + "; -fx-font-size: 13px;");
    }

    protected void setSuccess(String msg) {
        statusLabel.setText("✓  " + msg);
        statusLabel.setStyle("-fx-text-fill: " + Theme.ACCENT_GREEN + "; -fx-font-size: 13px;");
    }

    protected TextField makeField(String prompt) {
        TextField tf = new TextField();
        tf.setPromptText(prompt);
        tf.setStyle(Theme.inputField());
        return tf;
    }

    protected DatePicker makeDatePicker() {
        DatePicker dp = new DatePicker();
        dp.setMaxWidth(Double.MAX_VALUE);
        dp.setStyle(Theme.inputField());
        return dp;
    }
}
