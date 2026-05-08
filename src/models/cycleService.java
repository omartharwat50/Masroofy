import java.sql.ResultSet;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

public class cycleService {
    DatabaseManager db = new DatabaseManager();

    public double calculateDailyLimit(Cycle c) {
        long totalDays = ChronoUnit.DAYS.between(c.getStartDate(), c.getEndDate());
        return c.getTotalBudget() / totalDays;
    }

    public double calculateRemainingBudget(Cycle c) throws Exception {
        // get total spent in this cycle
        ResultSet rs = db.getTransactionsForCycle(c);
        double totalSpent = 0;
        while (rs.next()) {
            totalSpent += rs.getDouble("amount");
        }
        rs.close();

        return c.getTotalBudget() - totalSpent;
    }

    public double calculateDailyRemaining(Cycle c) throws Exception {
        long daysLeft = ChronoUnit.DAYS.between(LocalDate.now(), c.getEndDate());
        double remainingBudget = calculateRemainingBudget(c);
        return remainingBudget / daysLeft;
    }

    public boolean validateBudget(double budget) {

    return budget > 0;
    }

    public boolean validateDates(LocalDate start,
                             LocalDate end) {

    return end.isAfter(start);
}
}