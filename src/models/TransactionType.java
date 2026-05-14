
public enum TransactionType {
    INCOME("Income", "#34C759"),
    EXPENSE("Expense", "#FF3B30");

    private final String displayName;
    private final String color;

    TransactionType(String displayName, String color) {
        this.displayName = displayName;
        this.color = color;
    }

    public String getDisplayName() { return displayName; }
    public String getColor() { return color; }

    @Override
    public String toString() { return displayName; }
}
