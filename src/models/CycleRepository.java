

import java.util.List;
import java.util.Optional;

public interface CycleRepository {
    void save(Cycle cycle) throws Exception;
    void update(Cycle cycle) throws Exception;
    void delete(int id) throws Exception;
    Optional<Cycle> findActive() throws Exception;
    List<Cycle> findAll() throws Exception;
    Optional<Cycle> findById(int id) throws Exception;
    void deactivateAll() throws Exception;
}
