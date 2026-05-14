
import java.time.LocalDate;

public class Transaction {
    private int id;
    private String title;
    private double amount;
    private LocalDate date;
    private TransactionType type;
    private int categoryId;
    private String categoryName;
    private String note;
    private int cycleId;
    private int userId;

    public Transaction(int id, String title, double amount, LocalDate date,
                       TransactionType type, int categoryId, String categoryName,
                       String note, int cycleId, int userId) {
        this.id = id;
        this.title = title;
        this.amount = amount;
        this.date = date;
        this.type = type;
        this.categoryId = categoryId;
        this.categoryName = categoryName;
        this.note = note;
        this.cycleId = cycleId;
        this.userId = userId;
    }

    // Minimal constructor for legacy compatibility
    public Transaction(int id, double amount, String date) {
        this.id = id;
        this.amount = amount;
        this.date = LocalDate.parse(date.substring(0, 10));
        this.type = TransactionType.EXPENSE;
        this.title = "Transaction";
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public double getAmount() { return amount; }
    public void setAmount(double amount) { this.amount = amount; }
    public LocalDate getDate() { return date; }
    public void setDate(LocalDate date) { this.date = date; }
    public TransactionType getType() { return type; }
    public void setType(TransactionType type) { this.type = type; }
    public int getCategoryId() { return categoryId; }
    public void setCategoryId(int categoryId) { this.categoryId = categoryId; }
    public String getCategoryName() { return categoryName; }
    public void setCategoryName(String categoryName) { this.categoryName = categoryName; }
    public String getNote() { return note; }
    public void setNote(String note) { this.note = note; }
    public int getCycleId() { return cycleId; }
    public void setCycleId(int cycleId) { this.cycleId = cycleId; }
    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }

    @Override
    public String toString() {
        return String.format("%s | %s | $%.2f | %s", date, title, amount, type.getDisplayName());
    }
}
