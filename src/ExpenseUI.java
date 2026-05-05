import java.util.ArrayList;

public class ExpenseUI {
    ExpenseController expenseController = new ExpenseController(); // ← initialize it!
    ArrayList<Category> categoryArrayList = new ArrayList<>();

    public void getCategorizedTrans() throws Exception {
        categoryArrayList = expenseController.getTransactionsByCat();
    }

    public String displayTransactions() {
        StringBuilder sb = new StringBuilder();

        for (Category cat : categoryArrayList) {
            sb.append("Category: ").append(cat.getName()).append("\n");
            sb.append("─────────────────────\n");
            ArrayList <Transaction>store=cat.getTransactions();
            for (Transaction t : store) {
                sb.append("  ID: ").append(t.getId()).append("\n");
                sb.append("  Amount: ").append(t.getAmount()).append("\n");
                sb.append("  Date: ").append(t.getDate()).append("\n");
                sb.append("\n");
            }
        }

        return sb.toString();
    }
}