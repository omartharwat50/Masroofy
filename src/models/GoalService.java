

import java.time.LocalDate;
import java.util.List;

public class GoalService {

    private final GoalRepository goalRepository;

    public GoalService(GoalRepository goalRepository) {
        this.goalRepository = goalRepository;
    }

    public Goal createGoal(String title, double targetAmount, LocalDate deadline, String icon) throws Exception {
        validate(title, targetAmount, deadline);
        Goal goal = new Goal(0, title, targetAmount, 0, deadline, 0, icon);
        goalRepository.save(goal);
        return goal;
    }

    public void updateGoal(Goal goal) throws Exception {
        validate(goal.getTitle(), goal.getTargetAmount(), goal.getDeadline());
        goalRepository.update(goal);
    }

    public void addToSavings(int goalId, double amount) throws Exception {
        if (amount <= 0) throw new IllegalArgumentException("Amount must be positive.");
        goalRepository.findById(goalId).ifPresent(goal -> {
            goal.setSavedAmount(goal.getSavedAmount() + amount);
            try { goalRepository.update(goal); } catch (Exception ignored) {}
        });
    }

    public void deleteGoal(int id) throws Exception {
        goalRepository.delete(id);
    }

    public List<Goal> getAllGoals() throws Exception {
        return goalRepository.findAll();
    }

    private void validate(String title, double targetAmount, LocalDate deadline) {
        if (title == null || title.isBlank()) throw new IllegalArgumentException("Goal title cannot be empty.");
        if (targetAmount <= 0) throw new IllegalArgumentException("Target amount must be positive.");
        if (deadline == null) throw new IllegalArgumentException("Deadline cannot be null.");
        if (deadline.isBefore(LocalDate.now())) throw new IllegalArgumentException("Deadline cannot be in the past.");
    }
}
