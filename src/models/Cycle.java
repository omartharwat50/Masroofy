import java.time.LocalDate;

public class Cycle {
    private int id;
    private LocalDate startDate, endDate;
    private boolean active;
    private double totalBudget;
    
    public Cycle(int id, LocalDate startDate, LocalDate endDate, boolean active, double totalBudget) {
        this.id = id;
        this.startDate = startDate;
        this.endDate = endDate;
        this.active = active;
        this.totalBudget = totalBudget;
    }
    
    public int getId() { return id; }
    public double getTotalBudget() { return totalBudget; }
    public LocalDate getStartDate() { return startDate; }
    public LocalDate getEndDate() { return endDate; }
    public boolean isActive() { return active; }
    
    public long getTotalDays() {
        return java.time.temporal.ChronoUnit.DAYS.between(startDate, endDate);
    }
    
    public long getDaysRemaining() {
        long days = java.time.temporal.ChronoUnit.DAYS.between(java.time.LocalDate.now(), endDate);
        return days > 0 ? days : 0;
    }
    
    public double calculateDailyLimit() {
        long totalDays = getTotalDays();
        return totalDays > 0 ? totalBudget / totalDays : 0;
    }
}