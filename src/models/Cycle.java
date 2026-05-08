import java.time.LocalDate;


public class Cycle {
    private int id;
    private LocalDate startDate,endDate;
    private boolean active;
    private double totalBudget;

    Cycle(int id, LocalDate startDate,LocalDate endDate,boolean active,double totalBudget){
        this.id=id;
        this.startDate=startDate;
        this.endDate=endDate;
        this.active=active;
        this.totalBudget=totalBudget;
    }
    public int getId(){
        return  id;
    }
    public double getTotalBudget(){
        return totalBudget;
    }
    public LocalDate getStartDate(){
        return startDate;
    }
    public LocalDate getEndDate(){
        return endDate;
    }

}
