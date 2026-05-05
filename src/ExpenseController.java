import java.util.ArrayList;

public class ExpenseController {
    ExpenseService expenseService;
    public ArrayList getTransactionsByCat() throws Exception {
        ArrayList<Category>categories=new ArrayList<>();
       categories= expenseService.getTransactionsCategorized();
       return categories;

    }
}
