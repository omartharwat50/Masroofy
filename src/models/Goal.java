
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

public class Goal {
    private int id;
    private String title;
    private double targetAmount;
    private double savedAmount;
    private LocalDate deadline;
    private int userId;
    private String icon;

    public Goal(int id, String title, double targetAmount, double savedAmount,
                LocalDate deadline, int userId, String icon) {
        this.id = id;
        this.title = title;
        this.targetAmount = targetAmount;
        this.savedAmount = savedAmount;
        this.deadline = deadline;
        this.userId = userId;
        this.icon = icon != null ? icon : "🎯";
    }

    public Goal(int id, String title, double targetAmount, double savedAmount,
                LocalDate deadline, int userId) {
        this(id, title, targetAmount, savedAmount, deadline, userId, "🎯");
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public double getTargetAmount() { return targetAmount; }
    public void setTargetAmount(double targetAmount) { this.targetAmount = targetAmount; }
    public double getSavedAmount() { return savedAmount; }
    public void setSavedAmount(double savedAmount) { this.savedAmount = savedAmount; }
    public LocalDate getDeadline() { return deadline; }
    public void setDeadline(LocalDate deadline) { this.deadline = deadline; }
    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }
    public String getIcon() { return icon; }
    public void setIcon(String icon) { this.icon = icon; }

    public double getProgressPercentage() {
        return targetAmount > 0 ? Math.min((savedAmount / targetAmount) * 100.0, 100.0) : 0;
    }

    public double getRemainingAmount() {
        return Math.max(targetAmount - savedAmount, 0);
    }

    public long getDaysRemaining() {
        long days = ChronoUnit.DAYS.between(LocalDate.now(), deadline);
        return Math.max(days, 0);
    }

    public boolean isCompleted() {
        return savedAmount >= targetAmount;
    }

    public boolean isOverdue() {
        return LocalDate.now().isAfter(deadline) && !isCompleted();
    }

    @Override
    public String toString() { return icon + " " + title; }
}
