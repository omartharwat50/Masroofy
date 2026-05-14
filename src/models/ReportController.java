
import java.util.Map;
import java.util.Optional;

public class ReportController {

    private final ReportService reportService;
    private final CycleService cycleService;
    private final TransactionService transactionService;

    public ReportController(ReportService reportService, CycleService cycleService,
                             TransactionService transactionService) {
        this.reportService = reportService;
        this.cycleService = cycleService;
        this.transactionService = transactionService;
    }

    public Map<String, Double> getCategoryBreakdown() throws Exception {
        Optional<Cycle> cycle = cycleService.getActiveCycle();
        if (cycle.isEmpty()) return Map.of();
        return reportService.getCategoryBreakdown(cycle.get().getId());
    }

    public Map<String, Double> getMonthlyExpenses(int year) throws Exception {
        return reportService.getMonthlyExpenses(year);
    }

    public Map<String, Double> getMonthlyIncome(int year) throws Exception {
        return reportService.getMonthlyIncome(year);
    }

    public double getNetBalance() throws Exception {
        Optional<Cycle> cycle = cycleService.getActiveCycle();
        if (cycle.isEmpty()) return 0;
        return reportService.getNetBalance(cycle.get().getId());
    }

    public String getSummaryText() throws Exception {
        Optional<Cycle> cycleOpt = cycleService.getActiveCycle();
        if (cycleOpt.isEmpty()) return "No active budget cycle.";
        Cycle cycle = cycleOpt.get();
        double income = transactionService.getTotalIncome(cycle.getId());
        double expenses = transactionService.getTotalExpenses(cycle.getId());
        return reportService.generateSummary(cycle, income, expenses);
    }
}
