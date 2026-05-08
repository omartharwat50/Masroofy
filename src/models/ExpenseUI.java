import java.util.*;

public class ExpenseUI {
    private final ExpenseController expenseController;
    private List<Category> categorizedTransactions = new ArrayList<>();
    
    public ExpenseUI(ExpenseController expenseController) {
        this.expenseController = expenseController;
    }
    
    private void printLine(int length) {
        for (int i = 0; i < length; i++) System.out.print("=");
        System.out.println();
    }
    
    private void printDash(int length) {
        for (int i = 0; i < length; i++) System.out.print("─");
        System.out.println();
    }
    
    public void displayCategorizedTransactions() throws Exception {
        categorizedTransactions = expenseController.getTransactionsByCategory();
        
        if (categorizedTransactions.isEmpty()) {
            System.out.println("No transactions found.");
            return;
        }
        
        System.out.println();
        printLine(60);
        System.out.println("📊 TRANSACTIONS SORTED BY CATEGORY");
        printLine(60);
        
        for (Category category : categorizedTransactions) {
            System.out.println("\n📁 " + category.getName().toUpperCase());
            printDash(40);
            
            ArrayList<Transaction> transactions = category.getTransactions();
            if (transactions.isEmpty()) {
                System.out.println("  No transactions in this category");
            } else {
                double categoryTotal = 0;
                for (Transaction transaction : transactions) {
                    System.out.printf("  • $%-8.2f | %s%n", 
                        transaction.getAmount(), 
                        transaction.getDate()
                    );
                    categoryTotal += transaction.getAmount();
                }
                printDash(40);
                System.out.printf("  Category Total: $%.2f%n", categoryTotal);
            }
        }
    }
    
    public void addExpenseUI(Scanner scanner) throws Exception {
        System.out.println("\n💰 ADD NEW EXPENSE");
        printDash(30);
        
        // Display available categories
        List<Category> categories = expenseController.getTransactionsByCategory();
        System.out.println("\nAvailable Categories:");
        for (Category cat : categories) {
            System.out.println("  " + cat.getId() + ". " + cat.getName());
        }
        
        // Handle category ID input with validation
        int categoryId = 0;
        boolean validCategory = false;
        
        while (!validCategory) {
            try {
                System.out.print("\nEnter category ID: ");
                categoryId = scanner.nextInt();
                scanner.nextLine(); // consume newline
                
                // Check if category exists
                boolean found = false;
                for (Category cat : categories) {
                    if (cat.getId() == categoryId) {
                        found = true;
                        break;
                    }
                }
                
                if (found) {
                    validCategory = true;
                } else {
                    System.out.println("❌ Invalid category ID. Please choose from the list above.");
                }
            } catch (Exception e) {
                System.out.println("❌ Invalid input! Please enter a number for category ID.");
                scanner.nextLine(); // clear invalid input
            }
        }
        
        // Handle amount input with validation
        double amount = 0;
        boolean validAmount = false;
        
        while (!validAmount) {
            try {
                System.out.print("Enter amount: $");
                amount = scanner.nextDouble();
                scanner.nextLine(); // consume newline
                
                if (amount <= 0) {
                    System.out.println("❌ Amount must be greater than $0. Please try again.");
                } else {
                    validAmount = true;
                }
            } catch (Exception e) {
                System.out.println("❌ Invalid input! Please enter a valid amount (e.g., 45.50).");
                scanner.nextLine(); // clear invalid input
            }
        }
        
        expenseController.addExpense(amount, categoryId);
    }
}