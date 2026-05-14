
import java.util.List;
import java.util.Optional;

public interface CategoryRepository {
    void save(Category category) throws Exception;
    void update(Category category) throws Exception;
    void delete(int id) throws Exception;
    List<Category> findAll() throws Exception;
    Optional<Category> findById(int id) throws Exception;
}
