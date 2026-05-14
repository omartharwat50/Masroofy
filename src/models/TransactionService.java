
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

public class TransactionService {

    private final TransactionRepository transactionRepo;
    private final CategoryRepository categoryRepo;
    private final CycleRepository cycleRepo;
    private final NotificationService notificationService;

    public TransactionService(TransactionRepository transactionRepo,
                               CategoryRepository categoryRepo,
                               CycleRepository cycleRepo,
                               NotificationService notificationService) {
        this.transactionRepo = transactionRepo;
        this.categoryRepo = categoryRepo;
        this.cycleRepo = cycleRepo;
        this.notificationService = notificationService;
    }

    public Transaction addTransaction(String title, double amount, TransactionType type,
                                       int categoryId, String note, LocalDate date) throws Exception {
        validate(title, amount);

        int cycleId = cycleRepo.findActive().map(c -> c.getId()).orElse(0);

        Transaction t = new Transaction(0, title, amount, date, type, categoryId,
                null, note, cycleId, 0);
        transactionRepo.save(t);

        // Check budget threshold after every expense
        if (type == TransactionType.EXPENSE && cycleId > 0) {
            checkBudgetThreshold(cycleId);
        }

        return t;
    }

    public void updateTransaction(Transaction t) throws Exception {
        validate(t.getTitle(), t.getAmount());
        transactionRepo.update(t);
    }

    public void deleteTransaction(int id) throws Exception {
        transactionRepo.delete(id);
    }

    public List<Transaction> getAllTransactions() throws Exception {
        return transactionRepo.findAll();
    }

    public List<Transaction> getTransactionsByType(TransactionType type) throws Exception {
        return transactionRepo.findByType(type);
    }

    public List<Transaction> searchTransactions(String keyword) throws Exception {
        if (keyword == null || keyword.isBlank()) return getAllTransactions();
        return transactionRepo.search(keyword);
    }

    public List<Transaction> filterByDateRange(LocalDate from, LocalDate to) throws Exception {
        return transactionRepo.findByDateRange(from, to);
    }

    public List<Transaction> filterByCategory(int categoryId) throws Exception {
        return transactionRepo.findByCategory(categoryId);
    }

    public List<Category> getTransactionsCategorized() throws Exception {
        List<Category> categories = categoryRepo.findAll();
        for (Category cat : categories) {
            List<Transaction> txns = transactionRepo.findByCategory(cat.getId());
            txns.forEach(cat::addTransaction);
        }
        return categories;
    }

    public double getTotalIncome(int cycleId) throws Exception {
        return transactionRepo.getTotalByTypeAndCycle(TransactionType.INCOME, cycleId);
    }

    public double getTotalExpenses(int cycleId) throws Exception {
        return transactionRepo.getTotalByTypeAndCycle(TransactionType.EXPENSE, cycleId);
    }

    public Map<String, Double> getExpensesByCategory(int cycleId) throws Exception {
        List<Transaction> transactions = transactionRepo.findByCycle(cycleId)
                .stream()
                .filter(t -> t.getType() == TransactionType.EXPENSE)
                .collect(Collectors.toList());

        Map<String, Double> result = new LinkedHashMap<>();
        for (Transaction t : transactions) {
            String cat = t.getCategoryName() != null ? t.getCategoryName() : "Uncategorized";
            result.merge(cat, t.getAmount(), Double::sum);
        }
        return result;
    }

    public List<Transaction> getRecentTransactions(int limit) throws Exception {
        List<Transaction> all = transactionRepo.findAll();
        return all.stream().limit(limit).collect(Collectors.toList());
    }

    private void validate(String title, double amount) {
        if (title == null || title.isBlank()) throw new IllegalArgumentException("Title cannot be empty.");
        if (amount <= 0) throw new IllegalArgumentException("Amount must be greater than 0.");
    }

    private void checkBudgetThreshold(int cycleId) {
        try {
            cycleRepo.findById(cycleId).ifPresent(cycle -> {
                try {
                    double expenses = transactionRepo.getTotalByTypeAndCycle(TransactionType.EXPENSE, cycleId);
                    double pct = cycle.calculateUsagePercentage(expenses);
                    if (pct >= 80) {
                        notificationService.triggerWarning(
                            String.format("⚠️ You have used %.1f%% of your budget!", pct), pct);
                    }
                } catch (Exception ignored) {}
            });
        } catch (Exception ignored) {}
    }
}
