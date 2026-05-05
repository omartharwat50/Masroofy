import java.sql.*;

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

}