package models;
import java.util.ArrayList;
public class Category {
    private String name;
    private int id;
    private ArrayList<Transaction> transactions = new ArrayList<>();
    
    public Category(String name, int id) {
        this.name = name;
        this.id = id;
    }
    
    public void addTransaction(Transaction trans) {
        transactions.add(trans);
    }
    
    public int getId() { return id; }
    public String getName() { return name; }
    public ArrayList<Transaction> getTransactions() { return transactions; }
    
    public double getTotalSpent() {
        double total = 0;
        for (Transaction t : transactions) {
            total += t.getAmount();
        }
        return total;
    }
}