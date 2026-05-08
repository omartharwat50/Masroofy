package models;
import java.util.List;

public interface ITransactionRepository {
    void saveTransaction(int cycleId, int categoryId, double amount) throws Exception;
    List<Transaction> getTransactionsByCategory(int categoryId) throws Exception;
    List<Transaction> getAllTransactions() throws Exception;
}