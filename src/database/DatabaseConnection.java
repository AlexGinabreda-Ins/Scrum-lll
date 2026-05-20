package database;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseConnection {
    private static final String DATABASE_DIRECTORY = "database";
    private static final String DEFAULT_DATABASE_PATH = DATABASE_DIRECTORY + "/app.db";

    private DatabaseConnection() {
    }

    public static Connection getConnection() throws SQLException {
        ensureDatabaseDirectoryExists();
        Connection connection = DriverManager.getConnection("jdbc:sqlite:" + getDatabasePath());

        try (Statement statement = connection.createStatement()) {
            statement.execute("PRAGMA busy_timeout = 5000");
            statement.execute("PRAGMA foreign_keys = ON");
        }

        return connection;
    }

    public static void initializeDatabase() throws SQLException {
        try (Connection connection = getConnection();
             Statement statement = connection.createStatement()) {

            statement.execute("PRAGMA journal_mode = WAL");

            statement.execute("CREATE TABLE IF NOT EXISTS \"user\" (" +
                    "dni TEXT PRIMARY KEY, " +
                    "name TEXT NOT NULL" +
                    ")");

            statement.execute("CREATE TABLE IF NOT EXISTS client (" +
                    "dni TEXT PRIMARY KEY, " +
                    "name TEXT NOT NULL, " +
                    "email TEXT, " +
                    "phone TEXT" +
                    ")");

            statement.execute("CREATE TABLE IF NOT EXISTS supermarket (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "name TEXT NOT NULL, " +
                    "location TEXT NOT NULL, " +
                    "UNIQUE(name, location)" +
                    ")");

            statement.execute("CREATE TABLE IF NOT EXISTS products (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "name TEXT NOT NULL, " +
                    "price REAL NOT NULL, " +
                    "quantity INTEGER NOT NULL DEFAULT 1, " +
                    "type_product TEXT NOT NULL" +
                    ")");

            statement.execute("CREATE TABLE IF NOT EXISTS ticket (" +
                    "id INTEGER PRIMARY KEY, " +
                    "date TEXT NOT NULL, " +
                    "user_dni TEXT, " +
                    "supermarket_id INTEGER, " +
                    "FOREIGN KEY(user_dni) REFERENCES \"user\"(dni) ON UPDATE CASCADE ON DELETE SET NULL, " +
                    "FOREIGN KEY(supermarket_id) REFERENCES supermarket(id) ON UPDATE CASCADE ON DELETE SET NULL" +
                    ")");

            statement.execute("CREATE TABLE IF NOT EXISTS ticket_products (" +
                    "ticket_id INTEGER NOT NULL, " +
                    "product_id INTEGER NOT NULL, " +
                    "quantity INTEGER NOT NULL DEFAULT 1, " +
                    "price REAL NOT NULL, " +
                    "PRIMARY KEY(ticket_id, product_id), " +
                    "FOREIGN KEY(ticket_id) REFERENCES ticket(id) ON UPDATE CASCADE ON DELETE CASCADE, " +
                    "FOREIGN KEY(product_id) REFERENCES products(id) ON UPDATE CASCADE ON DELETE CASCADE" +
                    ")");

            statement.execute("CREATE TABLE IF NOT EXISTS user_supermarkets (" +
                    "user_dni TEXT NOT NULL, " +
                    "supermarket_id INTEGER NOT NULL, " +
                    "PRIMARY KEY(user_dni, supermarket_id), " +
                    "FOREIGN KEY(user_dni) REFERENCES \"user\"(dni) ON UPDATE CASCADE ON DELETE CASCADE, " +
                    "FOREIGN KEY(supermarket_id) REFERENCES supermarket(id) ON UPDATE CASCADE ON DELETE CASCADE" +
                    ")");
        }
    }

    public static boolean databaseExists() {
        return new File(getDatabasePath()).exists();
    }

    public static boolean isDatabaseLocked(SQLException exception) {
        String message = exception.getMessage();
        return message != null && (message.contains("SQLITE_BUSY") || message.contains("database is locked"));
    }

    private static void ensureDatabaseDirectoryExists() throws SQLException {
        File databaseFile = new File(getDatabasePath());
        File directory = databaseFile.getParentFile();

        if (directory != null && !directory.exists() && !directory.mkdirs()) {
            throw new SQLException("Could not create database directory: " + directory.getAbsolutePath());
        }
    }

    private static String getDatabasePath() {
        return System.getProperty("database.path", DEFAULT_DATABASE_PATH);
    }
}
