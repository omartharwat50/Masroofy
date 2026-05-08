import java.util.ArrayList;

public class ExpenseController {

    ExpenseService expenseService = new ExpenseService();
    DatabaseManager db = new DatabaseManager();

    // existing functionality
    public ArrayList<Category> getTransactionsByCat() throws Exception {

        ArrayList<Category> categories =
                expenseService.getTransactionsCategorized();

        return categories;
    }

    // Add new expense
    public void addExpense(double amount,
                           int categoryId)
                           throws Exception {

        // validate amount
        if (!expenseService.validateExpense(amount)) {

            System.out.println(
                    "Invalid Amount"
            );

            return;
        }

        // get active cycle
        Cycle c = db.getCurrentCycle();

        if (c == null) {

            System.out.println(
                    "No Active Cycle Found"
            );

            return;
        }

        // save transaction
        db.insertTransaction(
                c.getId(),
                categoryId,
                amount
        );

        // recalculate daily limit
        cycleService cs = new cycleService();

        double newLimit =
                cs.calculateDailyRemaining(c);

        System.out.println(
                "Expense Added Successfully"
        );

        System.out.println(
                "Updated Daily Limit: "
                        + newLimit
        );
    }
}