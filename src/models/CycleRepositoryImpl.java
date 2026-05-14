

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class CycleRepositoryImpl implements CycleRepository {

    @Override
    public void save(Cycle cycle) throws Exception {
        String sql = "INSERT INTO cycles (total_budget, start_date, end_date, active, user_id) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setDouble(1, cycle.getTotalBudget());
            ps.setDate(2, Date.valueOf(cycle.getStartDate()));
            ps.setDate(3, Date.valueOf(cycle.getEndDate()));
            ps.setBoolean(4, cycle.isActive());
            ps.setInt(5, cycle.getUserId());
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) cycle.setId(rs.getInt(1));
            }
        }
    }

    @Override
    public void update(Cycle cycle) throws Exception {
        String sql = "UPDATE cycles SET total_budget=?, start_date=?, end_date=?, active=? WHERE id=?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setDouble(1, cycle.getTotalBudget());
            ps.setDate(2, Date.valueOf(cycle.getStartDate()));
            ps.setDate(3, Date.valueOf(cycle.getEndDate()));
            ps.setBoolean(4, cycle.isActive());
            ps.setInt(5, cycle.getId());
            ps.executeUpdate();
        }
    }

    @Override
    public void delete(int id) throws Exception {
        String sql = "DELETE FROM cycles WHERE id=?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        }
    }

    @Override
    public Optional<Cycle> findActive() throws Exception {
        String sql = "SELECT * FROM cycles WHERE active = 1 LIMIT 1";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) return Optional.of(mapRow(rs));
        }
        return Optional.empty();
    }

    @Override
    public List<Cycle> findAll() throws Exception {
        String sql = "SELECT * FROM cycles ORDER BY start_date DESC";
        List<Cycle> list = new ArrayList<>();
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) list.add(mapRow(rs));
        }
        return list;
    }

    @Override
    public Optional<Cycle> findById(int id) throws Exception {
        String sql = "SELECT * FROM cycles WHERE id=?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return Optional.of(mapRow(rs));
            }
        }
        return Optional.empty();
    }

    @Override
    public void deactivateAll() throws Exception {
        String sql = "UPDATE cycles SET active = 0";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.executeUpdate();
        }
    }

    private Cycle mapRow(ResultSet rs) throws SQLException {
        return new Cycle(
            rs.getInt("id"),
            rs.getDouble("total_budget"),
            rs.getDate("start_date").toLocalDate(),
            rs.getDate("end_date").toLocalDate(),
            rs.getBoolean("active"),
            rs.getInt("user_id")
        );
    }
}
