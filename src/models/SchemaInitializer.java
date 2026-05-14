import java.sql.Connection;
import java.sql.Statement;

/**
 * Initializes the database schema on application startup.
 * Creates all required tables if they don't exist.
 */
public class SchemaInitializer {

    public static void initialize() {
        try (Connection conn = DatabaseManager.getConnection();
             Statement stmt = conn.createStatement()) {

            stmt.executeUpdate("""
                CREATE TABLE IF NOT EXISTS users (
                    id INT AUTO_INCREMENT PRIMARY KEY,
                    name VARCHAR(100) NOT NULL,
                    email VARCHAR(150) NOT NULL UNIQUE,
                    password_hash VARCHAR(255) NOT NULL,
                    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
                )
            """);

            stmt.executeUpdate("""
                CREATE TABLE IF NOT EXISTS categories (
                    id INT AUTO_INCREMENT PRIMARY KEY,
                    name VARCHAR(100) NOT NULL,
                    icon VARCHAR(20) DEFAULT '📁',
                    UNIQUE KEY unique_name (name)
                )
            """);

            stmt.executeUpdate("""
                CREATE TABLE IF NOT EXISTS cycles (
                    id INT AUTO_INCREMENT PRIMARY KEY,
                    total_budget DOUBLE NOT NULL,
                    start_date DATE NOT NULL,
                    end_date DATE NOT NULL,
                    active BOOLEAN DEFAULT FALSE,
                    user_id INT DEFAULT 0,
                    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
                )
            """);

            // Drop and recreate transactions to fix duplicate FK constraint
            stmt.executeUpdate("SET FOREIGN_KEY_CHECKS = 0");
            stmt.executeUpdate("DROP TABLE IF EXISTS transactions");
            stmt.executeUpdate("SET FOREIGN_KEY_CHECKS = 1");

            stmt.executeUpdate("""
                CREATE TABLE IF NOT EXISTS transactions (
                    id INT AUTO_INCREMENT PRIMARY KEY,
                    title VARCHAR(200) NOT NULL,
                    amount DOUBLE NOT NULL,
                    date DATE NOT NULL,
                    type ENUM('INCOME','EXPENSE') NOT NULL DEFAULT 'EXPENSE',
                    category_id INT,
                    note TEXT,
                    cycle_id INT,
                    user_id INT DEFAULT 0,
                    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                    CONSTRAINT fk_transactions_category
                        FOREIGN KEY (category_id) REFERENCES categories(id) ON DELETE SET NULL,
                    CONSTRAINT fk_transactions_cycle
                        FOREIGN KEY (cycle_id) REFERENCES cycles(id) ON DELETE SET NULL
                )
            """);

            stmt.executeUpdate("""
                CREATE TABLE IF NOT EXISTS goals (
                    id INT AUTO_INCREMENT PRIMARY KEY,
                    title VARCHAR(200) NOT NULL,
                    target_amount DOUBLE NOT NULL,
                    saved_amount DOUBLE DEFAULT 0,
                    deadline DATE NOT NULL,
                    user_id INT DEFAULT 0,
                    icon VARCHAR(20) DEFAULT '🎯',
                    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
                )
            """);

            stmt.executeUpdate("""
                CREATE TABLE IF NOT EXISTS notifications (
                    id INT AUTO_INCREMENT PRIMARY KEY,
                    message TEXT NOT NULL,
                    percentage DOUBLE DEFAULT 0,
                    date DATE NOT NULL,
                    is_read BOOLEAN DEFAULT FALSE,
                    type VARCHAR(20) DEFAULT 'INFO',
                    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
                )
            """);

            // Seed default categories if none exist
            stmt.executeUpdate("""
                INSERT IGNORE INTO categories (name, icon) VALUES
                ('Food & Dining', '🍔'),
                ('Transportation', '🚗'),
                ('Shopping', '🛍️'),
                ('Entertainment', '🎬'),
                ('Healthcare', '💊'),
                ('Housing', '🏠'),
                ('Education', '📚'),
                ('Savings', '💰'),
                ('Utilities', '⚡'),
                ('Other', '📦')
            """);

            System.out.println("✅ Database schema initialized successfully.");

        } catch (Exception e) {
            System.err.println("❌ Schema initialization failed: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Database schema initialization failed", e);
        }
    }
}