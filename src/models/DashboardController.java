

import java.util.List;
import java.util.Map;
import java.util.Optional;

public class DashboardController {

    private final CycleService cycleService;
    private final TransactionService transactionService;
    private final GoalService goalService;
    private final NotificationService notificationService;

    public DashboardController(CycleService cycleService, TransactionService transactionService,
                                GoalService goalService, NotificationService notificationService) {
        this.cycleService = cycleService;
        this.transactionService = transactionService;
        this.goalService = goalService;
        this.notificationService = notificationService;
    }

    public Optional<Cycle> getActiveCycle() throws Exception {
        return cycleService.getActiveCycle();
    }

    public double getTotalIncome(int cycleId) throws Exception {
        return transactionService.getTotalIncome(cycleId);
    }

    public double getTotalExpenses(int cycleId) throws Exception {
        return transactionService.getTotalExpenses(cycleId);
    }

    public double getRemainingBudget(Cycle cycle, double expenses) {
        return cycle.calculateRemaining(expenses);
    }

    public double getDailyLimit(Cycle cycle, double expenses) {
        return cycleService.calculateDailyBudget(cycle, expenses);
    }

    public double getBudgetUsagePercentage(Cycle cycle, double expenses) {
        return cycle.calculateUsagePercentage(expenses);
    }

    public List<Transaction> getRecentTransactions(int limit) throws Exception {
        return transactionService.getRecentTransactions(limit);
    }

    public Map<String, Double> getExpensesByCategory(int cycleId) throws Exception {
        return transactionService.getExpensesByCategory(cycleId);
    }

    public List<Goal> getAllGoals() throws Exception {
        return goalService.getAllGoals();
    }

    public List<Notification> getNotifications() {
        return notificationService.getRecentNotifications(10);
    }
}
