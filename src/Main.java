import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        System.out.println("💰 PERSONAL BUDGET TRACKER");
        
        try {
            // Initialize dependencies (Dependency Injection)
            DatabaseManager databaseManager = new DatabaseManager();
            NotificationService notificationService = new NotificationService();
            
            // Initialize services with dependencies
            CycleService cycleService = new CycleService(databaseManager);
            ExpenseService expenseService = new ExpenseService(
                databaseManager, databaseManager, databaseManager, notificationService
            );
            
            // Initialize controllers
            CycleController cycleController = new CycleController(cycleService, databaseManager);
            ExpenseController expenseController = new ExpenseController(
                expenseService, databaseManager, databaseManager
            );
            
            // Initialize UI
            CycleUI cycleUI = new CycleUI(cycleController);
            ExpenseUI expenseUI = new ExpenseUI(expenseController);
            
            // Run application
            runApplication(cycleUI, expenseUI);
            
        } catch (Exception e) {
            System.err.println("Error initializing application: " + e.getMessage());
            e.printStackTrace();
        }
    }
    private static void runApplication(CycleUI cycleUI, ExpenseUI expenseUI) throws Exception {
        Scanner scanner = new Scanner(System.in);
        boolean running = true;
        
        while (running) {
            displayMenu();
            System.out.print("\nChoose an option (1-5): ");
            
            // Handle menu choice with validation
            int choice = 0;
            boolean validChoice = false;
            
            while (!validChoice) {
                try {
                    choice = scanner.nextInt();
                    scanner.nextLine(); // consume newline
                    
                    if (choice >= 1 && choice <= 5) {
                        validChoice = true;
                    } else {
                        System.out.print("Please enter a number between 1 and 5: ");
                    }
                } catch (Exception e) {
                    System.out.print("Invalid input! Please enter a number (1-5): ");
                    scanner.nextLine(); // clear invalid input
                }
            }
            
            System.out.println();
            
            switch (choice) {
                case 1:
                    System.out.println("CYCLE SETUP");
                    cycleUI.setupCycle();
                    break;
                    
                case 2:
                    System.out.println("DAILY LIMIT");
                    cycleUI.displayDailyLimit();
                    break;
                    
                case 3:
                    expenseUI.addExpenseUI(scanner);
                    break;
                    
                case 4:
                    expenseUI.displayCategorizedTransactions();
                    break;
                    
                case 5:
                    System.out.println("Thank you for using Budget Tracker!");
                    System.out.println("Goodbye!");
                    running = false;
                    break;
            }
            
            if (running && choice >= 1 && choice <= 4) {
                System.out.println("\nPress Enter to continue...");
                scanner.nextLine();
            }
        }
        scanner.close();
    }
    
    private static void displayMenu() {
        System.out.println();
        System.out.println("                    MAIN MENU");
        System.out.println();
        System.out.println("  1. Create New Budget Cycle");
        System.out.println("  2. View Daily Spending Limit");
        System.out.println("  3. Add Expense (with Notifications)");
        System.out.println("  4. View Transactions by Category");
        System.out.println("  5. Exit");
    }
}