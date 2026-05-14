
import java.util.ArrayList;
import java.util.List;

public class Category {
    private int id;
    private String name;
    private String icon;
    private List<Transaction> transactions = new ArrayList<>();

    public Category(int id, String name, String icon) {
        this.id = id;
        this.name = name;
        this.icon = icon;
    }

    public Category(int id, String name) {
        this(id, name, "📁");
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getIcon() { return icon; }
    public void setIcon(String icon) { this.icon = icon; }
    public List<Transaction> getTransactions() { return transactions; }
    public void addTransaction(Transaction t) { transactions.add(t); }
    public void clearTransactions() { transactions.clear(); }

    public double getTotalSpent() {
        return transactions.stream().mapToDouble(Transaction::getAmount).sum();
    }

    @Override
    public String toString() { return icon + " " + name; }
}
