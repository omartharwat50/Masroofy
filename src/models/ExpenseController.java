package models;
import java.util.*;
public class ExpenseController {
    private final ExpenseService expenseService;
    private final ICycleRepository cycleRepository;
    private final ICategoryRepository categoryRepository;
    
    public ExpenseController(ExpenseService expenseService,
                             ICycleRepository cycleRepository,
                             ICategoryRepository categoryRepository) {
        this.expenseService = expenseService;
        this.cycleRepository = cycleRepository;
        this.categoryRepository = categoryRepository;
    }
    
    public List<Category> getTransactionsByCategory() throws Exception {
        return expenseService.getTransactionsCategorized();
    }
    
    public void addExpense(double amount, int categoryId) throws Exception {
        Category category = categoryRepository.getCategoryById(categoryId);
        if (category == null) {
            System.out.println("Invalid category ID");
            return;
        }
        
        Cycle currentCycle = cycleRepository.getCurrentCycle();
        if (currentCycle == null) {
            System.out.println("No active cycle found. Please create a cycle first.");
            return;
        }
        
        boolean success = expenseService.saveExpense(amount, categoryId, currentCycle.getId());
        
        if (success) {
            System.out.println("✅ Expense added successfully!");
            System.out.println("   Amount: $" + amount);
            System.out.println("   Category: " + category.getName());
            
            CycleService cycleService = new CycleService(cycleRepository);
            double dailyRemaining = cycleService.calculateDailyRemaining(currentCycle);
            System.out.println("   Remaining daily budget: $" + String.format("%.2f", dailyRemaining));
        }
    }
}