package models;
import java.util.*;

public class ExpenseService {
    private final ITransactionRepository transactionRepository;
    private final ICategoryRepository categoryRepository;
    private final ICycleRepository cycleRepository;
    private final NotificationService notificationService;
    
    public ExpenseService(ITransactionRepository transactionRepository,
                          ICategoryRepository categoryRepository,
                          ICycleRepository cycleRepository,
                          NotificationService notificationService) {
        this.transactionRepository = transactionRepository;
        this.categoryRepository = categoryRepository;
        this.cycleRepository = cycleRepository;
        this.notificationService = notificationService;
    }
    
    public List<Category> getTransactionsCategorized() throws Exception {
        List<Category> allCategories = categoryRepository.getAllCategories();
        Map<Integer, Category> categoryMap = new HashMap<>();
        
        for (Category category : allCategories) {
            categoryMap.put(category.getId(), category);
        }
        
        for (Category category : allCategories) {
            List<Transaction> transactions = transactionRepository.getTransactionsByCategory(category.getId());
            for (Transaction transaction : transactions) {
                category.addTransaction(transaction);
            }
        }
        
        return new ArrayList<>(categoryMap.values());
    }
    
    public boolean saveExpense(double amount, int categoryId, int cycleId) throws Exception {
        if (!validateExpense(amount)) {
            notificationService.showAlert("Invalid expense amount: " + amount);
            return false;
        }
        
        transactionRepository.saveTransaction(cycleId, categoryId, amount);
        checkBudgetThreshold(cycleId);
        
        return true;
    }
    
    private void checkBudgetThreshold(int cycleId) throws Exception {
        Cycle currentCycle = cycleRepository.getCurrentCycle();
        if (currentCycle != null && currentCycle.getId() == cycleId) {
            double percentage = calculateUsagePercentage(currentCycle);
            
            if (notificationService.isThresholdExceeded(percentage)) {
                String message = String.format(
                    "⚠️ WARNING: You have used %.1f%% of your monthly budget!",
                    percentage
                );
                notificationService.triggerNotification(message);
            }
        }
    }
    
    public double calculateUsagePercentage(Cycle cycle) throws Exception {
        List<Transaction> allTransactions = transactionRepository.getAllTransactions();
        double totalSpent = 0;
        for (Transaction t : allTransactions) {
            totalSpent += t.getAmount();
        }
        
        double budget = cycle.getTotalBudget();
        return budget > 0 ? (totalSpent / budget) * 100 : 0;
    }
    
    public boolean validateExpense(double amount) {
        return amount > 0;
    }
}