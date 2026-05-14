
import java.time.LocalDate;

public class Notification {
    private int id;
    private String message;
    private double percentage;
    private LocalDate date;
    private boolean read;
    private String type; // "WARNING", "INFO", "SUCCESS"

    public Notification(int id, String message, double percentage, LocalDate date, boolean read, String type) {
        this.id = id;
        this.message = message;
        this.percentage = percentage;
        this.date = date;
        this.read = read;
        this.type = type != null ? type : "INFO";
    }

    public Notification(String message, double percentage, String type) {
        this(0, message, percentage, LocalDate.now(), false, type);
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
    public double getPercentage() { return percentage; }
    public void setPercentage(double percentage) { this.percentage = percentage; }
    public LocalDate getDate() { return date; }
    public void setDate(LocalDate date) { this.date = date; }
    public boolean isRead() { return read; }
    public void setRead(boolean read) { this.read = read; }
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public String getIcon() {
        return switch (type) {
            case "WARNING" -> "⚠️";
            case "SUCCESS" -> "✅";
            default -> "💡";
        };
    }
}
