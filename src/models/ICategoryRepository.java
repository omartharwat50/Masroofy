import java.util.List;

public interface ICategoryRepository {
    void insertCategory(String name) throws Exception;
    List<Category> getAllCategories() throws Exception;
    Category getCategoryById(int id) throws Exception;
}