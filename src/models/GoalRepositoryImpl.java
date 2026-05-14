
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class GoalRepositoryImpl implements GoalRepository {

    @Override
    public void save(Goal goal) throws Exception {
        String sql = "INSERT INTO goals (title, target_amount, saved_amount, deadline, user_id, icon) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, goal.getTitle());
            ps.setDouble(2, goal.getTargetAmount());
            ps.setDouble(3, goal.getSavedAmount());
            ps.setDate(4, Date.valueOf(goal.getDeadline()));
            ps.setInt(5, goal.getUserId());
            ps.setString(6, goal.getIcon());
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) goal.setId(rs.getInt(1));
            }
        }
    }

    @Override
    public void update(Goal goal) throws Exception {
        String sql = "UPDATE goals SET title=?, target_amount=?, saved_amount=?, deadline=?, icon=? WHERE id=?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, goal.getTitle());
            ps.setDouble(2, goal.getTargetAmount());
            ps.setDouble(3, goal.getSavedAmount());
            ps.setDate(4, Date.valueOf(goal.getDeadline()));
            ps.setString(5, goal.getIcon());
            ps.setInt(6, goal.getId());
            ps.executeUpdate();
        }
    }

    @Override
    public void delete(int id) throws Exception {
        String sql = "DELETE FROM goals WHERE id=?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        }
    }

    @Override
    public List<Goal> findAll() throws Exception {
        String sql = "SELECT * FROM goals ORDER BY deadline ASC";
        List<Goal> list = new ArrayList<>();
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) list.add(mapRow(rs));
        }
        return list;
    }

    @Override
    public List<Goal> findByUser(int userId) throws Exception {
        String sql = "SELECT * FROM goals WHERE user_id=? ORDER BY deadline ASC";
        List<Goal> list = new ArrayList<>();
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(mapRow(rs));
            }
        }
        return list;
    }

    @Override
    public Optional<Goal> findById(int id) throws Exception {
        String sql = "SELECT * FROM goals WHERE id=?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return Optional.of(mapRow(rs));
            }
        }
        return Optional.empty();
    }

    private Goal mapRow(ResultSet rs) throws SQLException {
        return new Goal(
            rs.getInt("id"),
            rs.getString("title"),
            rs.getDouble("target_amount"),
            rs.getDouble("saved_amount"),
            rs.getDate("deadline").toLocalDate(),
            rs.getInt("user_id"),
            rs.getString("icon")
        );
    }
}
