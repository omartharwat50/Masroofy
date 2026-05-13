import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class DatabaseManager implements ITransactionRepository, ICycleRepository, ICategoryRepository {

    private static final HikariDataSource dataSource;

    static {
        HikariConfig config = new HikariConfig();
        
        config.setJdbcUrl("jdbc:mysql://tramway.proxy.rlwy.net:58901/railway");
        config.setUsername("root");
        config.setPassword("JlqhhpkJiFQbyXlZOvywevZWgEsLHVXi");

        // إعدادات الأداء المهمة
        config.setMaximumPoolSize(10);           // عدد الاتصالات الأقصى
        config.setMinimumIdle(2);                // عدد الاتصالات الجاهزة دايماً
        config.setIdleTimeout(300000);           // 5 دقائق
        config.setMaxLifetime(600000);           // 10 دقائق
        config.setConnectionTimeout(20000);      // 20 ثانية
        
        // تحسينات MySQL
        config.addDataSourceProperty("cachePrepStmts", "true");
        config.addDataSourceProperty("prepStmtCacheSize", "250");
        config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");
        config.addDataSourceProperty("useServerPrepStmts", "true");

        dataSource = new HikariDataSource(config);
    }

    // لا نحتاج connect() قديم بعد كده
    private Connection getConnection() throws SQLException {
        return dataSource.getConnection();
    }

    // ====================== ICycleRepository ======================

    @Override
    public Cycle getCurrentCycle() throws SQLException {
        String sql = "SELECT * FROM Cycle WHERE active = 1";

        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            if (rs.next()) {
                return new Cycle(
                    rs.getInt("id"),
                    rs.getDate("startDate").toLocalDate(),
                    rs.getDate("endDate").toLocalDate(),
                    rs.getBoolean("active"),
                    rs.getDouble("totalBudget")
                );
            }
            return null;
        }
    }

    @Override
    public void insertCycle(Cycle cycle) throws SQLException {
        String deactivateSql = "UPDATE Cycle SET active = 0 WHERE active = 1";
        String insertSql = "INSERT INTO Cycle (startDate, endDate, active, totalBudget) VALUES (?, ?, 1, ?)";

        try (Connection conn = getConnection()) {
            conn.setAutoCommit(false);

            try (PreparedStatement ps1 = conn.prepareStatement(deactivateSql);
                 PreparedStatement ps2 = conn.prepareStatement(insertSql)) {

                ps1.executeUpdate();

                ps2.setDate(1, Date.valueOf(cycle.getStartDate()));
                ps2.setDate(2, Date.valueOf(cycle.getEndDate()));
                ps2.setDouble(3, cycle.getTotalBudget());
                ps2.executeUpdate();
            }

            conn.commit();
        }
    }

    @Override
    public double getTotalBudgetForCycle(Cycle cycle) throws SQLException {
        String sql = "SELECT totalBudget FROM Cycle WHERE id = ?";

        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, cycle.getId());
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? rs.getDouble("totalBudget") : 0.0;
            }
        }
    }

    // ====================== ITransactionRepository ======================

    @Override
    public void saveTransaction(int cycleId, int categoryId, double amount) throws SQLException {
        String sql = "INSERT INTO Transactions (amount, date, category_id, cycle_id) VALUES (?, ?, ?, ?)";

        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setDouble(1, amount);
            ps.setDate(2, Date.valueOf(LocalDate.now()));
            ps.setInt(3, categoryId);
            ps.setInt(4, cycleId);
            ps.executeUpdate();
        }
    }

    @Override
    public List<Transaction> getTransactionsByCategory(int categoryId) throws SQLException {
        String sql = "SELECT * FROM Transactions WHERE category_id = ?";
        List<Transaction> transactions = new ArrayList<>();

        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, categoryId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    transactions.add(new Transaction(
                        rs.getInt("id"),
                        rs.getDouble("amount"),
                        rs.getDate("date").toString()
                    ));
                }
            }
        }
        return transactions;
    }

    @Override
    public List<Transaction> getAllTransactions() throws SQLException {
        String sql = "SELECT * FROM Transactions";
        List<Transaction> transactions = new ArrayList<>();

        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                transactions.add(new Transaction(
                    rs.getInt("id"),
                    rs.getDouble("amount"),
                    rs.getDate("date").toString()
                ));
            }
        }
        return transactions;
    }

    // ====================== ICategoryRepository ======================

    @Override
    public void insertCategory(String name) throws SQLException {
        String sql = "INSERT INTO Category (name) VALUES (?)";

        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, name);
            ps.executeUpdate();
        }
    }

    @Override
    public List<Category> getAllCategories() throws SQLException {
        String sql = "SELECT * FROM Category";
        List<Category> categories = new ArrayList<>();

        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                categories.add(new Category(
                    rs.getString("name"),
                    rs.getInt("id")
                ));
            }
        }
        return categories;
    }

    @Override
    public Category getCategoryById(int id) throws SQLException {
        String sql = "SELECT * FROM Category WHERE id = ?";

        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new Category(rs.getString("name"), rs.getInt("id"));
                }
            }
        }
        return null;
    }

    public static void shutdown() {
        if (dataSource != null && !dataSource.isClosed()) {
            dataSource.close();
        }
    }
}