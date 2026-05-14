

import java.time.LocalDate;
import java.util.List;

public class GoalController {

    private final GoalService goalService;

    public GoalController(GoalService goalService) {
        this.goalService = goalService;
    }

    public Goal createGoal(String title, double targetAmount, LocalDate deadline, String icon) throws Exception {
        return goalService.createGoal(title, targetAmount, deadline, icon);
    }

    public void updateGoal(Goal goal) throws Exception {
        goalService.updateGoal(goal);
    }

    public void addToSavings(int goalId, double amount) throws Exception {
        goalService.addToSavings(goalId, amount);
    }

    public void deleteGoal(int id) throws Exception {
        goalService.deleteGoal(id);
    }

    public List<Goal> getAllGoals() throws Exception {
        return goalService.getAllGoals();
    }
}
