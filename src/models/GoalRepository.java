
import java.util.List;
import java.util.Optional;

public interface GoalRepository {
    void save(Goal goal) throws Exception;
    void update(Goal goal) throws Exception;
    void delete(int id) throws Exception;
    List<Goal> findAll() throws Exception;
    List<Goal> findByUser(int userId) throws Exception;
    Optional<Goal> findById(int id) throws Exception;
}
