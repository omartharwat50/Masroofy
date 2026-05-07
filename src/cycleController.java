public class cycleController {
cycleService cycleService=new cycleService();
DatabaseManager db =new DatabaseManager();



public double logDailyLimit() throws Exception{
    Cycle c=db.getCurrentCycle();
    double dl=cycleService.calculateDailyRemaining(c);
    return dl;
}
}
