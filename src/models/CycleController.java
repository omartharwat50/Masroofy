

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public class CycleController {

    private final CycleService cycleService;

    public CycleController(CycleService cycleService) {
        this.cycleService = cycleService;
    }

    public Cycle createCycle(double budget, LocalDate start, LocalDate end) throws Exception {
        return cycleService.createCycle(budget, start, end);
    }

    public void updateCycle(Cycle cycle) throws Exception {
        cycleService.updateCycle(cycle);
    }

    public void deleteCycle(int id) throws Exception {
        cycleService.deleteCycle(id);
    }

    public Optional<Cycle> getActiveCycle() throws Exception {
        return cycleService.getActiveCycle();
    }

    public List<Cycle> getAllCycles() throws Exception {
        return cycleService.getAllCycles();
    }

    public void activateCycle(int id) throws Exception {
        cycleService.activateCycle(id);
    }
}
