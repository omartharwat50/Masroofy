import java.time.LocalDate;
public class CycleController {
    private final CycleService cycleService;
    private final ICycleRepository cycleRepository;
    
    public CycleController(CycleService cycleService, ICycleRepository cycleRepository) {
        this.cycleService = cycleService;
        this.cycleRepository = cycleRepository;
    }

    public double logDailyLimit() throws Exception {
        Cycle currentCycle = cycleRepository.getCurrentCycle();
        if (currentCycle == null) {
            System.out.println("No active cycle found");
            return 0;
        }
        double dailyRemaining = cycleService.calculateDailyRemaining(currentCycle);
        System.out.println("Daily remaining budget: $" + String.format("%.2f", dailyRemaining));
        return dailyRemaining;
    }
    
    public void createCycle(double budget, LocalDate start, LocalDate end) throws Exception {
        if (!cycleService.validateBudget(budget)) {
            System.out.println("Invalid budget. Please enter a positive number.");
            return;
        }
        
        if (!cycleService.validateDates(start, end)) {
            System.out.println("Invalid dates. End date must be after start date.");
            return;
        }
        
        Cycle cycle = new Cycle(0, start, end, true, budget);
        cycleRepository.insertCycle(cycle);
        
        double dailyLimit = cycleService.calculateDailyLimit(cycle);
        System.out.println("Cycle Created Successfully!");
        System.out.println("Daily Limit: $" + String.format("%.2f", dailyLimit));
    }
}