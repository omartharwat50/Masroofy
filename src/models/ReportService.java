
import java.time.LocalDate;
import java.time.Month;
import java.time.format.TextStyle;
import java.util.*;
import java.util.stream.Collectors;

public class ReportService {

    private final TransactionService transactionService;

    public ReportService(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    public Map<String, Double> getCategoryBreakdown(int cycleId) throws Exception {
        List<Category> cats = transactionService.getTransactionsCategorized();
        Map<String, Double> result = new LinkedHashMap<>();
        for (Category c : cats) {
            double total = c.getTransactions().stream()
                .filter(t -> t.getType() == TransactionType.EXPENSE && t.getCycleId() == cycleId)
                .mapToDouble(Transaction::getAmount).sum();
            if (total > 0) result.put(c.getName(), total);
        }
        return result;
    }

    public Map<String, Double> getMonthlyExpenses(int year) throws Exception {
        List<Transaction> all = transactionService.getAllTransactions();
        Map<String, Double> result = new LinkedHashMap<>();

        for (Month month : Month.values()) {
            String label = month.getDisplayName(TextStyle.SHORT, Locale.ENGLISH);
            double total = all.stream()
                .filter(t -> t.getDate().getYear() == year &&
                             t.getDate().getMonth() == month &&
                             t.getType() == TransactionType.EXPENSE)
                .mapToDouble(Transaction::getAmount).sum();
            result.put(label, total);
        }
        return result;
    }

    public Map<String, Double> getMonthlyIncome(int year) throws Exception {
        List<Transaction> all = transactionService.getAllTransactions();
        Map<String, Double> result = new LinkedHashMap<>();

        for (Month month : Month.values()) {
            String label = month.getDisplayName(TextStyle.SHORT, Locale.ENGLISH);
            double total = all.stream()
                .filter(t -> t.getDate().getYear() == year &&
                             t.getDate().getMonth() == month &&
                             t.getType() == TransactionType.INCOME)
                .mapToDouble(Transaction::getAmount).sum();
            result.put(label, total);
        }
        return result;
    }

    public double getNetBalance(int cycleId) throws Exception {
        double income = transactionService.getTotalIncome(cycleId);
        double expenses = transactionService.getTotalExpenses(cycleId);
        return income - expenses;
    }

    public String generateSummary(Cycle cycle, double totalIncome, double totalExpenses) {
        double remaining = cycle.calculateRemaining(totalExpenses);
        double pct = cycle.calculateUsagePercentage(totalExpenses);
        return String.format(
            "Budget: $%.2f | Income: $%.2f | Expenses: $%.2f | Remaining: $%.2f (%.1f%% used)",
            cycle.getTotalBudget(), totalIncome, totalExpenses, remaining, pct
        );
    }
}
