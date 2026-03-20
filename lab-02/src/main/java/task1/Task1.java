package task1;

// Product Interface
interface DBConnection {
    void executeQuery(String query);
}

// Concrete Products
class MySQLConnection implements DBConnection {
    public void executeQuery(String query) {
        System.out.println("Executing on MySQL: " + query);
    }
}

class PostgreSQLConnection implements DBConnection {
    public void executeQuery(String query) {
        System.out.println("Executing on PostgreSQL: " + query);
    }
}

// Factory Method
class ConnectionFactory {
    public static DBConnection createConnection(String type) {
        if ("MySQL".equalsIgnoreCase(type)) {
            return new MySQLConnection();
        } else if ("PostgreSQL".equalsIgnoreCase(type)) {
            return new PostgreSQLConnection();
        }
        throw new IllegalArgumentException("Loại database không hỗ trợ");
    }
}

// Singleton
class DatabaseManager {
    private static DatabaseManager instance;
    private DBConnection connection;

    private DatabaseManager() {
        // Cấu hình mặc định
        connection = ConnectionFactory.createConnection("MySQL");
    }

    public static synchronized DatabaseManager getInstance() {
        if (instance == null) {
            instance = new DatabaseManager();
        }
        return instance;
    }

    public void setConnectionType(String type) {
        this.connection = ConnectionFactory.createConnection(type);
    }

    public void runQuery(String query) {
        connection.executeQuery(query);
    }
}

public class Task1 {
    public static void main(String[] args) {
        DatabaseManager manager1 = DatabaseManager.getInstance();
        manager1.runQuery("SELECT * FROM users");

        DatabaseManager manager2 = DatabaseManager.getInstance();
        manager2.setConnectionType("PostgreSQL");
        manager2.runQuery("SELECT * FROM products");
        manager1.runQuery("SELECT * FROM orders");
    }
}
