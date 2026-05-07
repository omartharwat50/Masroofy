import java.sql.ResultSet;
import java.util.ArrayList;

public class ExpenseService {
    DatabaseManager db = new DatabaseManager();
    public ArrayList<Category> getTransactionsCategorized() throws Exception {
        ResultSet trans = db.getTransactionsOrderedByCategory();
        ArrayList<Category> categories = new ArrayList<>();

        while (trans.next()) {

            // category details
            String categoryName = trans.getString("category_name");
            int categoryId = trans.getInt("category_id");

            // transaction details
            int transId = trans.getInt("id");
            double amount = trans.getDouble("amount");
            String date = trans.getString("date");

            Transaction transaction = new Transaction(transId, amount, date);

            // check if category already exists in the list
            Category existingCat = null;
            for (Category c : categories) {
                if (c.getId() == categoryId) {
                    existingCat = c;
                    break;
                }
            }

            // if category doesn't exist, create it
            if (existingCat == null) {
                existingCat = new Category( categoryName,categoryId);
                categories.add(existingCat);
            }

            existingCat.addTransaction(transaction);
        }

        return categories;
    }
    public boolean validateExpense(double amount) {

    return amount > 0;
}
}
