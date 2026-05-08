import java.time.LocalDate;

public class CycleService {
    private final ICycleRepository cycleRepository;
    
    public CycleService(ICycleRepository cycleRepository) {
        this.cycleRepository = cycleRepository;
    }
    
    public double calculateDailyLimit(Cycle cycle) {
        return cycle.calculateDailyLimit();
    }
    
    public double calculateRemainingBudget(Cycle cycle) throws Exception {
        return cycle.getTotalBudget() - (cycle.calculateDailyLimit() * 
               (cycle.getTotalDays() - cycle.getDaysRemaining()));
    }
    
    public double calculateDailyRemaining(Cycle cycle) throws Exception {
        if (cycle.getDaysRemaining() <= 0) return 0;
        double remainingBudget = calculateRemainingBudget(cycle);
        return remainingBudget / cycle.getDaysRemaining();
    }
    
    public boolean validateBudget(double budget) {
        return budget > 0;
    }
    
    public boolean validateDates(LocalDate start, LocalDate end) {
        return end.isAfter(start);
    }
}