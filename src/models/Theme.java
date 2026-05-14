
import javafx.scene.control.Button;
import javafx.scene.control.Control;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;

public final class Theme {

    public static final String BG_DEEP       = "#080F1A";
    public static final String BG_DARK       = "#0D1B2A";
    public static final String BG_CARD       = "#111F30";
    public static final String BG_SIDEBAR    = "#0A1628";
    public static final String BG_ELEVATED   = "#162336";
    public static final String BG_INPUT      = "#0F1E2E";

    public static final String ACCENT        = "#00D4FF";
    public static final String ACCENT_HOVER  = "#00B8D9";
    public static final String ACCENT_DIM    = "#00D4FF33";
    public static final String ACCENT_GREEN  = "#00E5A0";
    public static final String ACCENT_ORANGE = "#FF9F43";
    public static final String ACCENT_RED    = "#FF4757";
    public static final String ACCENT_PURPLE = "#A55EEA";
    public static final String ACCENT_YELLOW = "#FFC312";

    public static final String TEXT_PRIMARY  = "#E8F4FD";
    public static final String TEXT_SECONDARY= "#7BAED0";
    public static final String TEXT_MUTED    = "#3D5E7C";
    public static final String TEXT_ACCENT   = "#00D4FF";

    public static final String BORDER        = "#162336";
    public static final String BORDER_ACCENT = "#00D4FF44";

    private Theme() {}

    public static String card() {
        return "-fx-background-color: " + BG_CARD + ";" +
               "-fx-background-radius: 12;" +
               "-fx-border-color: " + BORDER + ";" +
               "-fx-border-radius: 12;" +
               "-fx-border-width: 1;";
    }

    public static String cardElevated() {
        return "-fx-background-color: " + BG_ELEVATED + ";" +
               "-fx-background-radius: 16;" +
               "-fx-border-color: " + BORDER_ACCENT + ";" +
               "-fx-border-radius: 16;" +
               "-fx-border-width: 1;" +
               "-fx-effect: dropshadow(gaussian, #00D4FF22, 20, 0, 0, 4);";
    }

    public static String inputField() {
        return "-fx-background-color: " + BG_INPUT + ";" +
               "-fx-text-fill: " + TEXT_PRIMARY + ";" +
               "-fx-prompt-text-fill: " + TEXT_MUTED + ";" +
               "-fx-border-color: " + BORDER + ";" +
               "-fx-border-radius: 8;" +
               "-fx-background-radius: 8;" +
               "-fx-padding: 10 14;" +
               "-fx-font-size: 14px;" +
               "-fx-font-family: 'Segoe UI';";
    }

    public static String primaryBtn() {
        return "-fx-background-color: " + ACCENT + ";" +
               "-fx-text-fill: #080F1A;" +
               "-fx-font-size: 14px;" +
               "-fx-font-weight: bold;" +
               "-fx-font-family: 'Segoe UI';" +
               "-fx-padding: 11 20;" +
               "-fx-background-radius: 8;" +
               "-fx-cursor: hand;";
    }

    public static String primaryBtnHover() {
        return "-fx-background-color: " + ACCENT_HOVER + ";" +
               "-fx-text-fill: #080F1A;" +
               "-fx-font-size: 14px;" +
               "-fx-font-weight: bold;" +
               "-fx-font-family: 'Segoe UI';" +
               "-fx-padding: 11 20;" +
               "-fx-background-radius: 8;" +
               "-fx-cursor: hand;";
    }

    public static String dangerBtn() {
        return "-fx-background-color: " + ACCENT_RED + "22;" +
               "-fx-text-fill: " + ACCENT_RED + ";" +
               "-fx-font-size: 13px;" +
               "-fx-font-weight: bold;" +
               "-fx-padding: 8 16;" +
               "-fx-background-radius: 8;" +
               "-fx-border-color: " + ACCENT_RED + "44;" +
               "-fx-border-radius: 8;" +
               "-fx-cursor: hand;";
    }

    public static String ghostBtn() {
        return "-fx-background-color: transparent;" +
               "-fx-text-fill: " + TEXT_SECONDARY + ";" +
               "-fx-font-size: 13px;" +
               "-fx-padding: 8 16;" +
               "-fx-background-radius: 8;" +
               "-fx-border-color: " + BORDER + ";" +
               "-fx-border-radius: 8;" +
               "-fx-cursor: hand;";
    }

    public static String labelStyle(String color, String size, boolean bold) {
        return "-fx-text-fill: " + color + ";" +
               "-fx-font-size: " + size + ";" +
               (bold ? "-fx-font-weight: bold;" : "") +
               "-fx-font-family: 'Segoe UI';";
    }

    public static Button makeBtn(String text, String style) {
        Button btn = new Button(text);
        btn.setStyle(style);
        return btn;
    }

    public static Button makePrimaryBtn(String text) {
        Button btn = new Button(text);
        btn.setStyle(primaryBtn());
        btn.setOnMouseEntered(e -> btn.setStyle(primaryBtnHover()));
        btn.setOnMouseExited(e -> btn.setStyle(primaryBtn()));
        return btn;
    }

    public static Button makeDangerBtn(String text) {
        Button btn = new Button(text);
        String base = dangerBtn();
        String hover = base.replace(ACCENT_RED + "22", ACCENT_RED + "44");
        btn.setStyle(base);
        btn.setOnMouseEntered(e -> btn.setStyle(hover));
        btn.setOnMouseExited(e -> btn.setStyle(base));
        return btn;
    }

    public static Button makeGhostBtn(String text) {
        Button btn = new Button(text);
        String base = ghostBtn();
        String hover = base.replace("transparent", BG_ELEVATED);
        btn.setStyle(base);
        btn.setOnMouseEntered(e -> btn.setStyle(hover));
        btn.setOnMouseExited(e -> btn.setStyle(base));
        return btn;
    }

    public static Label makeLabel(String text, String color, String size, boolean bold) {
        Label lbl = new Label(text);
        lbl.setStyle(labelStyle(color, size, bold));
        return lbl;
    }

    public static VBox fieldGroup(String labelText, Control field) {
        Label lbl = new Label(labelText);
        lbl.setStyle(labelStyle(TEXT_SECONDARY, "12px", true));
        VBox group = new VBox(6, lbl, field);
        return group;
    }
}
