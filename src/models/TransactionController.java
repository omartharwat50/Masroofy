

import java.time.LocalDate;
import java.util.List;

public class TransactionController {

    private final TransactionService transactionService;
    private final CategoryRepository categoryRepository;

    public TransactionController(TransactionService transactionService, CategoryRepository categoryRepository) {
        this.transactionService = transactionService;
        this.categoryRepository = categoryRepository;
    }

    public Transaction addTransaction(String title, double amount, TransactionType type,
                                       int categoryId, String note, LocalDate date) throws Exception {
        return transactionService.addTransaction(title, amount, type, categoryId, note, date);
    }

    public void updateTransaction(Transaction t) throws Exception {
        transactionService.updateTransaction(t);
    }

    public void deleteTransaction(int id) throws Exception {
        transactionService.deleteTransaction(id);
    }

    public List<Transaction> getAllTransactions() throws Exception {
        return transactionService.getAllTransactions();
    }

    public List<Transaction> searchTransactions(String keyword) throws Exception {
        return transactionService.searchTransactions(keyword);
    }

    public List<Transaction> filterByType(TransactionType type) throws Exception {
        return transactionService.getTransactionsByType(type);
    }

    public List<Transaction> filterByCategory(int categoryId) throws Exception {
        return transactionService.filterByCategory(categoryId);
    }

    public List<Transaction> filterByDateRange(LocalDate from, LocalDate to) throws Exception {
        return transactionService.filterByDateRange(from, to);
    }

    public List<Category> getAllCategories() throws Exception {
        return categoryRepository.findAll();
    }

    public void addCategory(String name, String icon) throws Exception {
        Category cat = new Category(0, name, icon);
        categoryRepository.save(cat);
    }

    public void updateCategory(Category cat) throws Exception {
        categoryRepository.update(cat);
    }

    public void deleteCategory(int id) throws Exception {
        categoryRepository.delete(id);
    }
}
