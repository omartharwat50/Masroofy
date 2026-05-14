
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Popup;
import javafx.stage.Stage;


import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class NotificationService {

    private static final double WARNING_THRESHOLD = 80.0;
    private static final double CRITICAL_THRESHOLD = 100.0;

    private final List<Notification> notificationLog = new ArrayList<>();

    public void triggerWarning(String message, double percentage) {
        String type = percentage >= CRITICAL_THRESHOLD ? "WARNING" : "INFO";
        Notification n = new Notification(message, percentage, type);
        notificationLog.add(n);
        System.out.println("🔔 " + message);
    }

    public void triggerSuccess(String message) {
        Notification n = new Notification(message, 0, "SUCCESS");
        notificationLog.add(n);
        System.out.println("✅ " + message);
    }

    public boolean isOverBudget(double percentage) {
        return percentage >= CRITICAL_THRESHOLD;
    }

    public boolean isNearBudget(double percentage) {
        return percentage >= WARNING_THRESHOLD;
    }

    public List<Notification> getRecentNotifications(int limit) {
        List<Notification> recent = new ArrayList<>();
        int start = Math.max(0, notificationLog.size() - limit);
        for (int i = notificationLog.size() - 1; i >= start; i--) {
            recent.add(notificationLog.get(i));
        }
        return recent;
    }

    public List<Notification> getAllNotifications() {
        return new ArrayList<>(notificationLog);
    }

    public void clearAll() {
        notificationLog.clear();
    }
}
