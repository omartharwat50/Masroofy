
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

public class Cycle {
    private int id;
    private double totalBudget;
    private LocalDate startDate;
    private LocalDate endDate;
    private boolean active;
    private int userId;

    public Cycle(int id, double totalBudget, LocalDate startDate, LocalDate endDate, boolean active, int userId) {
        this.id = id;
        this.totalBudget = totalBudget;
        this.startDate = startDate;
        this.endDate = endDate;
        this.active = active;
        this.userId = userId;
    }

    public Cycle(int id, LocalDate startDate, LocalDate endDate, boolean active, double totalBudget) {
        this(id, totalBudget, startDate, endDate, active, 0);
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public double getTotalBudget() { return totalBudget; }
    public void setTotalBudget(double totalBudget) { this.totalBudget = totalBudget; }
    public LocalDate getStartDate() { return startDate; }
    public void setStartDate(LocalDate startDate) { this.startDate = startDate; }
    public LocalDate getEndDate() { return endDate; }
    public void setEndDate(LocalDate endDate) { this.endDate = endDate; }
    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }
    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }

    public long getTotalDays() {
        return ChronoUnit.DAYS.between(startDate, endDate);
    }

    public long getRemainingDays() {
        long days = ChronoUnit.DAYS.between(LocalDate.now(), endDate);
        return Math.max(days, 0);
    }

    public long getElapsedDays() {
        long days = ChronoUnit.DAYS.between(startDate, LocalDate.now());
        return Math.max(Math.min(days, getTotalDays()), 0);
    }

    public double calculateDailyLimit() {
        long totalDays = getTotalDays();
        return totalDays > 0 ? totalBudget / totalDays : 0;
    }

    public double calculateRemaining(double totalSpent) {
        return totalBudget - totalSpent;
    }

    public double calculateUsagePercentage(double totalSpent) {
        return totalBudget > 0 ? (totalSpent / totalBudget) * 100.0 : 0;
    }

    public String getDateRange() {
        return startDate + " → " + endDate;
    }
}
