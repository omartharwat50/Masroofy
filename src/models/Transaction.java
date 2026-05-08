import java.util.Date;

public class Transaction {
    private int id;
    private double amount;
    private String date;
    Transaction (int id, double amount,String Date){
        this.id=id;
        this.amount=amount;
        date=Date;
    }
    public int getId(){
        return id;
    }
    public double getAmount(){
        return amount;
    }
    public String getDate(){
        return date;
    }
}
