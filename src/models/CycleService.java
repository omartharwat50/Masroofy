

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public class CycleService {

    private final CycleRepository cycleRepository;

    public CycleService(CycleRepository cycleRepository) {
        this.cycleRepository = cycleRepository;
    }

    public Cycle createCycle(double budget, LocalDate start, LocalDate end) throws Exception {
        validateBudget(budget);
        validateDates(start, end);

        cycleRepository.deactivateAll();

        Cycle cycle = new Cycle(0, budget, start, end, true, 0);
        cycleRepository.save(cycle);
        return cycle;
    }

    public void updateCycle(Cycle cycle) throws Exception {
        validateBudget(cycle.getTotalBudget());
        validateDates(cycle.getStartDate(), cycle.getEndDate());
        cycleRepository.update(cycle);
    }

    public void deleteCycle(int id) throws Exception {
        cycleRepository.delete(id);
    }

    public Optional<Cycle> getActiveCycle() throws Exception {
        return cycleRepository.findActive();
    }

    public List<Cycle> getAllCycles() throws Exception {
        return cycleRepository.findAll();
    }

    public void activateCycle(int id) throws Exception {
        Optional<Cycle> cycleOpt = cycleRepository.findById(id);
        if (cycleOpt.isPresent()) {
            cycleRepository.deactivateAll();
            Cycle cycle = cycleOpt.get();
            cycle.setActive(true);
            cycleRepository.update(cycle);
        }
    }

    public double calculateDailyBudget(Cycle cycle, double totalSpent) {
        long remaining = cycle.getRemainingDays();
        if (remaining <= 0) return 0;
        double remainingBudget = cycle.calculateRemaining(totalSpent);
        return remainingBudget / remaining;
    }

    private void validateBudget(double budget) {
        if (budget <= 0) throw new IllegalArgumentException("Budget must be a positive number.");
    }

    private void validateDates(LocalDate start, LocalDate end) {
        if (start == null || end == null) throw new IllegalArgumentException("Dates cannot be null.");
        if (!end.isAfter(start)) throw new IllegalArgumentException("End date must be after start date.");
        if (start.isBefore(LocalDate.now().minusDays(1))) throw new IllegalArgumentException("Start date cannot be in the past.");
    }
}
