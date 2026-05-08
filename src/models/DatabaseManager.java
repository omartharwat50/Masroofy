import java.time.LocalDate;
import java.util.*;

public class DatabaseManager implements ITransactionRepository, ICycleRepository, ICategoryRepository {
    
    // In-memory storage (no database driver needed)
    private static List<Cycle> cycles = new ArrayList<>();
    private static List<Transaction> transactions = new ArrayList<>();
    private static List<Category> categories = new ArrayList<>();
    private static int nextCycleId = 1;
    private static int nextTransactionId = 1;
    private static int nextCategoryId = 6; // Start after default categories
    
    // Initialize default categories
    static {
        categories.add(new Category("Food", 1));
        categories.add(new Category("Transport", 2));
        categories.add(new Category("Entertainment", 3));
        categories.add(new Category("Shopping", 4));
        categories.add(new Category("Utilities", 5));
    }
    
    // ===== ICycleRepository Implementation =====
    @Override
    public Cycle getCurrentCycle() throws Exception {
        for (Cycle cycle : cycles) {
            if (cycle.isActive()) {
                return cycle;
            }
        }
        return null;
    }
    
    @Override
    public void insertCycle(Cycle cycle) throws Exception {
        // First, deactivate any existing active cycle
        for (Cycle c : cycles) {
            if (c.isActive()) {
                // Create new cycle object with active=false (immutable workaround)
                cycles.remove(c);
                Cycle deactivated = new Cycle(c.getId(), c.getStartDate(), c.getEndDate(), false, c.getTotalBudget());
                cycles.add(deactivated);
                break;
            }
        }
        
        // Create new cycle with new ID
        Cycle newCycle = new Cycle(nextCycleId++, cycle.getStartDate(), cycle.getEndDate(), true, cycle.getTotalBudget());
        cycles.add(newCycle);
        System.out.println("Cycle inserted successfully!");
    }
    
    @Override
    public double getTotalBudgetForCycle(Cycle cycle) throws Exception {
        return cycle.getTotalBudget();
    }
    
    // ===== ITransactionRepository Implementation =====
    @Override
    public void saveTransaction(int cycleId, int categoryId, double amount) throws Exception {
        Transaction transaction = new Transaction(nextTransactionId++, amount, LocalDate.now().toString());
        transactions.add(transaction);
        
        // Also add to category's transaction list
        for (Category cat : categories) {
            if (cat.getId() == categoryId) {
                cat.addTransaction(transaction);
                break;
            }
        }
        System.out.println("Transaction saved: $" + amount);
    }
    
    @Override
    public List<Transaction> getTransactionsByCategory(int categoryId) throws Exception {
        List<Transaction> categoryTransactions = new ArrayList<>();
        
        for (Category cat : categories) {
            if (cat.getId() == categoryId) {
                ArrayList<Transaction> catTransactions = cat.getTransactions();
                for (Transaction t : catTransactions) {
                    categoryTransactions.add(t);
                }
                break;
            }
        }
        return categoryTransactions;
    }
    
    @Override
    public List<Transaction> getAllTransactions() throws Exception {
        return new ArrayList<>(transactions);
    }
    
    // ===== ICategoryRepository Implementation =====
    @Override
    public void insertCategory(String name) throws Exception {
        Category newCategory = new Category(name, nextCategoryId++);
        categories.add(newCategory);
        System.out.println("Category added: " + name);
    }
    
    @Override
    public List<Category> getAllCategories() throws Exception {
        return new ArrayList<>(categories);
    }
    
    @Override
    public Category getCategoryById(int id) throws Exception {
        for (Category cat : categories) {
            if (cat.getId() == id) {
                return cat;
            }
        }
        return null;
    }
    
    // Helper method to view all data (for debugging)
    public void displayAllCycles() {
        System.out.println("\n--- All Cycles ---");
        for (Cycle c : cycles) {
            System.out.println("Cycle " + c.getId() + ": " + c.getStartDate() + " to " + c.getEndDate() + 
                               ", Active: " + c.isActive() + ", Budget: $" + c.getTotalBudget());
        }
    }
    
    public void displayAllTransactions() {
        System.out.println("\n--- All Transactions ---");
        for (Transaction t : transactions) {
            System.out.println("Transaction " + t.getId() + ": $" + t.getAmount() + " on " + t.getDate());
        }
    }
}