import java.util.ArrayList;

public class Category {
    private String name;
    private int id;
   private ArrayList<Transaction>Transactions=new ArrayList<>();
     Category(String name , int id){
         this.name=name;
         this.id=id;

     }
     public void addTransaction(Transaction trans){
         Transactions.add(trans);
     }
     public int getId(){
         return id;
     }
    public String getName(){
        return name;
    }
    public ArrayList getTransactions(){

       return Transactions;
    }


}
