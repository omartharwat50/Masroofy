import java.sql.*;
import java.time.LocalDate;

public class DatabaseManager {
    private static final String URL = "jdbc:mysql://tramway.proxy.rlwy.net:58901/railway";
    private static final String USER = "root";
    private static final String PASSWORD = "JlqhhpkJiFQbyXlZOvywevZWgEsLHVXi";

    public static Connection connect() {
        Connection conn = null;
        try {
            conn = DriverManager.getConnection(URL, USER, PASSWORD);
            System.out.println("Connected to MySQL!");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return conn;
    }



    public void insertCategory(String s) throws Exception {
        Connection conn = connect();
        Statement sttm = conn.createStatement();
        sttm.execute("INSERT INTO Category (name) VALUES ('" + s + "')");
        conn.close();
        sttm.close();

    }
    public void viewCategories() throws Exception{
        Connection conn = connect();
        Statement sttm = conn.createStatement();
        ResultSet rs = sttm.executeQuery("SELECT * FROM Category");

        while (rs.next()) {
            System.out.println(rs.getInt("id") + " | " + rs.getString("name"));
        }

        rs.close();
        conn.close();
        sttm.close();

    }
    public ResultSet getTransactionsOrderedByCategory() throws Exception {
        Connection conn=connect();
        Statement sttm = conn.createStatement();
        ResultSet rs = sttm.executeQuery("""
             SELECT Transactions.id, Transactions.amount, Transactions.date,
            Category.id AS category_id,
           Category.name AS category_name
             FROM Transactions
             JOIN Category ON Transactions.category_id = Category.id
            ORDER BY Category.name
        """);
        return rs;
    }
    public double getTotalBudgetForCycle(Cycle c) throws Exception {
        Connection conn = connect();
        Statement sttm = conn.createStatement();
        ResultSet rs = sttm.executeQuery("SELECT totalBudget FROM Cycle WHERE id = " + c.getId());

        double totalBudget = 0;
        if (rs.next()) {
            totalBudget = rs.getDouble("totalBudget");
        }

        rs.close();
        sttm.close();
        conn.close();

        return totalBudget;
    }
    public ResultSet getTransactionsForCycle(Cycle c) throws Exception {
        Connection conn = connect();
        Statement sttm = conn.createStatement();
        ResultSet rs = sttm.executeQuery(
                "SELECT * FROM Transactions WHERE cycle_id = " + c.getId()
        );
        return rs;
    }
    public Cycle getCurrentCycle() throws Exception {
        Connection conn = connect();
        Statement sttm = conn.createStatement();
        ResultSet rs = sttm.executeQuery(
                "SELECT * FROM Cycle WHERE active = 1"
        );

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

    public void insertCycle(Cycle c) throws Exception {

        Connection conn = connect();

        String sql =
                "INSERT INTO Cycle " +
                "(startDate, endDate, active, totalBudget) " +
                "VALUES (?, ?, ?, ?)";

        PreparedStatement stmt =
                conn.prepareStatement(sql);

        stmt.setDate(1, Date.valueOf(c.getStartDate()));
        stmt.setDate(2, Date.valueOf(c.getEndDate()));
        stmt.setBoolean(3, true);
        stmt.setDouble(4, c.getTotalBudget());

        stmt.executeUpdate();

        stmt.close();
        conn.close();
    }

    public void insertTransaction(int cycleId, int categoryId, double amount) throws Exception {
    Connection conn = connect();

String sql = "INSERT INTO Transactions " +"(amount, date, cycle_id, category_id) " +
        "VALUES (?, NOW(), ?, ?)";

PreparedStatement stmt = conn.prepareStatement(sql);

stmt.setDouble(1, amount);
stmt.setInt(2, cycleId);
stmt.setInt(3, categoryId);

stmt.executeUpdate();

stmt.close();
conn.close();
    }
}