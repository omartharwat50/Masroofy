import java.time.LocalDate;

public class cycleController {
cycleService cycleService=new cycleService();
DatabaseManager db =new DatabaseManager();



    public double logDailyLimit() throws Exception{
        Cycle c=db.getCurrentCycle();
        double dl=cycleService.calculateDailyRemaining(c);
        return dl;
    }

    public void createCycle(double budget, LocalDate start, LocalDate end) throws Exception {
        if (!cycleService.validateBudget(budget)) {
            System.out.println("Invalid budget. Please enter a positive number.");
            return;
        }

        if (!cycleService.validateDates(start, end)) {
            System.out.println("Invalid dates. End date must be after start date.");
            return;
        }

        Cycle c = new Cycle(0, start, end, true, budget);
        db.insertCycle(c); //insert into DB 

        double limit = cycleService.calculateDailyLimit(c);
        //calculate daily limit 

        System.out.println("Cycle Created");
        System.out.println("Daily Limit: " + limit);
            
    }

}
