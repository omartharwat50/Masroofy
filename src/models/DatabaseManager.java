import java.sql.*;
import java.time.LocalDate;
import java.util.*;

public class DatabaseManager implements ITransactionRepository, ICycleRepository, ICategoryRepository {

    private static final String URL = "jdbc:mysql://tramway.proxy.rlwy.net:58901/railway";
    private static final String USER = "root";
    private static final String PASSWORD = "JlqhhpkJiFQbyXlZOvywevZWgEsLHVXi";

    public Connection connect() {
        Connection conn = null;
        try {
            conn = DriverManager.getConnection(URL, USER, PASSWORD);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return conn;
    }

    // ===== ICycleRepository Implementation =====
    @Override
    public Cycle getCurrentCycle() throws Exception {
        Connection conn = connect();
        Statement sttm = conn.createStatement();
        ResultSet rs = sttm.executeQuery("SELECT * FROM Cycle WHERE active = 1");

        Cycle cycle = null;
        if (rs.next()) {
            int id = rs.getInt("id");
            LocalDate startDate = rs.getDate("startDate").toLocalDate();
            LocalDate endDate = rs.getDate("endDate").toLocalDate();
            boolean active = rs.getBoolean("active");
            double totalBudget = rs.getDouble("totalBudget");
            cycle = new Cycle(id, startDate, endDate, active, totalBudget);
        }

        rs.close();
        sttm.close();
        conn.close();
        return cycle;
    }

    @Override
    public void insertCycle(Cycle cycle) throws Exception {
        Connection conn = connect();
        Statement sttm = conn.createStatement();

        // Deactivate any existing active cycle
        sttm.execute("UPDATE Cycle SET active = 0 WHERE active = 1");

        // Insert new cycle
        sttm.execute("INSERT INTO Cycle (startDate, endDate, active, totalBudget) VALUES ('"
                + cycle.getStartDate() + "', '"
                + cycle.getEndDate() + "', 1, "
                + cycle.getTotalBudget() + ")");

        System.out.println("Cycle inserted successfully!");
        sttm.close();
        conn.close();
    }

    @Override
    public double getTotalBudgetForCycle(Cycle cycle) throws Exception {
        Connection conn = connect();
        Statement sttm = conn.createStatement();
        ResultSet rs = sttm.executeQuery("SELECT totalBudget FROM Cycle WHERE id = " + cycle.getId());

        double totalBudget = 0;
        if (rs.next()) {
            totalBudget = rs.getDouble("totalBudget");
        }

        rs.close();
        sttm.close();
        conn.close();
        return totalBudget;
    }

    // ===== ITransactionRepository Implementation =====
    @Override
    public void saveTransaction(int cycleId, int categoryId, double amount) throws Exception {
        Connection conn = connect();
        Statement sttm = conn.createStatement();
        sttm.execute("INSERT INTO Transactions (amount, date, category_id, cycle_id) VALUES ("
                + amount + ", '" + LocalDate.now() + "', " + categoryId + ", " + cycleId + ")");

        System.out.println("Transaction saved: $" + amount);
        sttm.close();
        conn.close();
    }

    @Override
    public List<Transaction> getTransactionsByCategory(int categoryId) throws Exception {
        Connection conn = connect();
        Statement sttm = conn.createStatement();
        ResultSet rs = sttm.executeQuery(
                "SELECT * FROM Transactions WHERE category_id = " + categoryId);

        List<Transaction> transactionList = new ArrayList<>();
        while (rs.next()) {
            int id = rs.getInt("id");
            double amount = rs.getDouble("amount");
            LocalDate date = rs.getDate("date").toLocalDate();
            transactionList.add(new Transaction(id, amount, date.toString()));
        }

        rs.close();
        sttm.close();
        conn.close();
        return transactionList;
    }

    @Override
    public List<Transaction> getAllTransactions() throws Exception {
        Connection conn = connect();
        Statement sttm = conn.createStatement();
        ResultSet rs = sttm.executeQuery("SELECT * FROM Transactions");

        List<Transaction> transactionList = new ArrayList<>();
        while (rs.next()) {
            int id = rs.getInt("id");
            double amount = rs.getDouble("amount");
            LocalDate date = rs.getDate("date").toLocalDate();
            transactionList.add(new Transaction(id, amount, date.toString()));
        }

        rs.close();
        sttm.close();
        conn.close();
        return transactionList;
    }

    // ===== ICategoryRepository Implementation =====
    @Override
    public void insertCategory(String name) throws Exception {
        Connection conn = connect();
        Statement sttm = conn.createStatement();
        sttm.execute("INSERT INTO Category (name) VALUES ('" + name + "')");

        System.out.println("Category added: " + name);
        sttm.close();
        conn.close();
    }

    @Override
    public List<Category> getAllCategories() throws Exception {
        Connection conn = connect();
        Statement sttm = conn.createStatement();
        ResultSet rs = sttm.executeQuery("SELECT * FROM Category");

        List<Category> categoryList = new ArrayList<>();
        while (rs.next()) {
            int id = rs.getInt("id");
            String name = rs.getString("name");
            categoryList.add(new Category(name, id));
        }

        rs.close();
        sttm.close();
        conn.close();
        return categoryList;
    }

    @Override
    public Category getCategoryById(int id) throws Exception {
        Connection conn = connect();
        Statement sttm = conn.createStatement();
        ResultSet rs = sttm.executeQuery("SELECT * FROM Category WHERE id = " + id);

        Category category = null;
        if (rs.next()) {
            category = new Category(rs.getString("name"), rs.getInt("id"));
        }

        rs.close();
        sttm.close();
        conn.close();
        return category;
    }

    // ===== Helper/Debug Methods =====
    public void displayAllCycles() throws Exception {
        Connection conn = connect();
        Statement sttm = conn.createStatement();
        ResultSet rs = sttm.executeQuery("SELECT * FROM Cycle");

        System.out.println("\n--- All Cycles ---");
        while (rs.next()) {
            System.out.println("Cycle " + rs.getInt("id") + ": "
                    + rs.getDate("startDate") + " to " + rs.getDate("endDate")
                    + ", Active: " + rs.getBoolean("active")
                    + ", Budget: $" + rs.getDouble("totalBudget"));
        }

        rs.close();
        sttm.close();
        conn.close();
    }

    public void displayAllTransactions() throws Exception {
        Connection conn = connect();
        Statement sttm = conn.createStatement();
        ResultSet rs = sttm.executeQuery("SELECT * FROM Transactions");

        System.out.println("\n--- All Transactions ---");
        while (rs.next()) {
            System.out.println("Transaction " + rs.getInt("id")
                    + ": $" + rs.getDouble("amount")
                    + " on " + rs.getDate("date"));
        }

        rs.close();
        sttm.close();
        conn.close();
    }
}