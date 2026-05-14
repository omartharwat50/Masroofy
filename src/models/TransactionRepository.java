
import java.time.LocalDate;
import java.util.List;

public interface TransactionRepository {
    void save(Transaction transaction) throws Exception;
    void update(Transaction transaction) throws Exception;
    void delete(int id) throws Exception;
    List<Transaction> findAll() throws Exception;
    List<Transaction> findByCycle(int cycleId) throws Exception;
    List<Transaction> findByCategory(int categoryId) throws Exception;
    List<Transaction> findByType(TransactionType type) throws Exception;
    List<Transaction> findByDateRange(LocalDate from, LocalDate to) throws Exception;
    List<Transaction> search(String keyword) throws Exception;
    double getTotalByTypeAndCycle(TransactionType type, int cycleId) throws Exception;
}
