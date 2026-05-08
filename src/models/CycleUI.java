package models;
import java.time.LocalDate;
import java.util.Scanner;
public class CycleUI {
    private final CycleController cycleController;
    
    public CycleUI(CycleController cycleController) {
        this.cycleController = cycleController;
    }
    
    public void displayDailyLimit() throws Exception {
        double dailyLimit = cycleController.logDailyLimit();
        System.out.println("💡 Your daily spending limit: $" + String.format("%.2f", dailyLimit));
    }
    
    public void setupCycle() throws Exception {
        Scanner scanner = new Scanner(System.in);
        
        // Handle budget input with validation
        double budget = 0;
        boolean validBudget = false;
        
        while (!validBudget) {
            try {
                System.out.print("Enter total budget for the cycle: $");
                budget = scanner.nextDouble();
                scanner.nextLine(); // consume newline
                
                if (budget <= 0) {
                    System.out.println("❌ Budget must be a positive number. Please try again.");
                } else {
                    validBudget = true;
                }
            } catch (Exception e) {
                System.out.println("❌ Invalid input! Please enter a valid number (e.g., 5000).");
                scanner.nextLine(); // clear the invalid input
            }
        }
        
        // Handle start date input
        LocalDate startDate = null;
        boolean validStartDate = false;
        
        while (!validStartDate) {
            try {
                System.out.print("Enter start date (YYYY-MM-DD): ");
                startDate = LocalDate.parse(scanner.nextLine());
                validStartDate = true;
            } catch (Exception e) {
                System.out.println("❌ Invalid date format! Please use YYYY-MM-DD (e.g., 2026-06-10).");
            }
        }
        
        // Handle end date input
        LocalDate endDate = null;
        boolean validEndDate = false;
        
        while (!validEndDate) {
            try {
                System.out.print("Enter end date (YYYY-MM-DD): ");
                endDate = LocalDate.parse(scanner.nextLine());
                
                if (endDate.isAfter(startDate)) {
                    validEndDate = true;
                } else {
                    System.out.println("❌ End date must be after start date. Please try again.");
                }
            } catch (Exception e) {
                System.out.println("❌ Invalid date format! Please use YYYY-MM-DD (e.g., 2026-06-20).");
            }
        }
        
        cycleController.createCycle(budget, startDate, endDate);
    }
}