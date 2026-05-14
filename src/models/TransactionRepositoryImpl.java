
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class TransactionRepositoryImpl implements TransactionRepository {

    @Override
    public void save(Transaction t) throws Exception {
        String sql = """
            INSERT INTO transactions (title, amount, date, type, category_id, note, cycle_id, user_id)
            VALUES (?, ?, ?, ?, ?, ?, ?, ?)
        """;
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, t.getTitle());
            ps.setDouble(2, t.getAmount());
            ps.setDate(3, Date.valueOf(t.getDate()));
            ps.setString(4, t.getType().name());
            ps.setObject(5, t.getCategoryId() > 0 ? t.getCategoryId() : null);
            ps.setString(6, t.getNote());
            ps.setObject(7, t.getCycleId() > 0 ? t.getCycleId() : null);
            ps.setInt(8, t.getUserId());
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) t.setId(rs.getInt(1));
            }
        }
    }

    @Override
    public void update(Transaction t) throws Exception {
        String sql = """
            UPDATE transactions SET title=?, amount=?, date=?, type=?, category_id=?, note=?
            WHERE id=?
        """;
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, t.getTitle());
            ps.setDouble(2, t.getAmount());
            ps.setDate(3, Date.valueOf(t.getDate()));
            ps.setString(4, t.getType().name());
            ps.setObject(5, t.getCategoryId() > 0 ? t.getCategoryId() : null);
            ps.setString(6, t.getNote());
            ps.setInt(7, t.getId());
            ps.executeUpdate();
        }
    }

    @Override
    public void delete(int id) throws Exception {
        String sql = "DELETE FROM transactions WHERE id=?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        }
    }

    @Override
    public List<Transaction> findAll() throws Exception {
        String sql = """
            SELECT t.*, c.name as cat_name FROM transactions t
            LEFT JOIN categories c ON t.category_id = c.id
            ORDER BY t.date DESC, t.id DESC
        """;
        return query(sql);
    }

    @Override
    public List<Transaction> findByCycle(int cycleId) throws Exception {
        String sql = """
            SELECT t.*, c.name as cat_name FROM transactions t
            LEFT JOIN categories c ON t.category_id = c.id
            WHERE t.cycle_id = ? ORDER BY t.date DESC
        """;
        return query(sql, cycleId);
    }

    @Override
    public List<Transaction> findByCategory(int categoryId) throws Exception {
        String sql = """
            SELECT t.*, c.name as cat_name FROM transactions t
            LEFT JOIN categories c ON t.category_id = c.id
            WHERE t.category_id = ? ORDER BY t.date DESC
        """;
        return query(sql, categoryId);
    }

    @Override
    public List<Transaction> findByType(TransactionType type) throws Exception {
        String sql = """
            SELECT t.*, c.name as cat_name FROM transactions t
            LEFT JOIN categories c ON t.category_id = c.id
            WHERE t.type = ? ORDER BY t.date DESC
        """;
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, type.name());
            try (ResultSet rs = ps.executeQuery()) {
                return mapRows(rs);
            }
        }
    }

    @Override
    public List<Transaction> findByDateRange(LocalDate from, LocalDate to) throws Exception {
        String sql = """
            SELECT t.*, c.name as cat_name FROM transactions t
            LEFT JOIN categories c ON t.category_id = c.id
            WHERE t.date BETWEEN ? AND ? ORDER BY t.date DESC
        """;
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setDate(1, Date.valueOf(from));
            ps.setDate(2, Date.valueOf(to));
            try (ResultSet rs = ps.executeQuery()) {
                return mapRows(rs);
            }
        }
    }

    @Override
    public List<Transaction> search(String keyword) throws Exception {
        String sql = """
            SELECT t.*, c.name as cat_name FROM transactions t
            LEFT JOIN categories c ON t.category_id = c.id
            WHERE t.title LIKE ? OR t.note LIKE ? ORDER BY t.date DESC
        """;
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            String k = "%" + keyword + "%";
            ps.setString(1, k);
            ps.setString(2, k);
            try (ResultSet rs = ps.executeQuery()) {
                return mapRows(rs);
            }
        }
    }

    @Override
    public double getTotalByTypeAndCycle(TransactionType type, int cycleId) throws Exception {
        String sql = "SELECT COALESCE(SUM(amount), 0) FROM transactions WHERE type=? AND cycle_id=?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, type.name());
            ps.setInt(2, cycleId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? rs.getDouble(1) : 0;
            }
        }
    }

    private List<Transaction> query(String sql, Object... params) throws Exception {
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            for (int i = 0; i < params.length; i++) {
                if (params[i] instanceof Integer) ps.setInt(i + 1, (Integer) params[i]);
                else ps.setObject(i + 1, params[i]);
            }
            try (ResultSet rs = ps.executeQuery()) {
                return mapRows(rs);
            }
        }
    }

    private List<Transaction> mapRows(ResultSet rs) throws SQLException {
        List<Transaction> list = new ArrayList<>();
        while (rs.next()) list.add(mapRow(rs));
        return list;
    }

    private Transaction mapRow(ResultSet rs) throws SQLException {
        String typeStr = rs.getString("type");
        TransactionType type = typeStr != null ? TransactionType.valueOf(typeStr) : TransactionType.EXPENSE;
        Date dateVal = rs.getDate("date");
        LocalDate date = dateVal != null ? dateVal.toLocalDate() : LocalDate.now();

        return new Transaction(
            rs.getInt("id"),
            rs.getString("title") != null ? rs.getString("title") : "Transaction",
            rs.getDouble("amount"),
            date,
            type,
            rs.getInt("category_id"),
            rs.getString("cat_name") != null ? rs.getString("cat_name") : "Uncategorized",
            rs.getString("note"),
            rs.getInt("cycle_id"),
            rs.getInt("user_id")
        );
    }
}
