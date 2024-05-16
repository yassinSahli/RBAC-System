import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;

class DatabaseHelper {
    final private String url = "jdbc:mysql://localhost:3306/db";
    final private String username = "root";
    final private String password = "";

    public Connection connection;
    public static Statement statement;

    public DatabaseHelper() throws Exception {
        this.connection = DriverManager.getConnection(url, username, password);
        DatabaseHelper.statement = connection.createStatement();

        statement.execute("CREATE TABLE IF NOT EXISTS actions ( \r\n" +
                "action_id INT PRIMARY KEY, \r\n" +
                "action_name VARCHAR(50) \r\n" +
                "); \r\n");

        statement.execute("CREATE TABLE IF NOT EXISTS permissions ( \r\n" +
                "perm_id INT, \r\n" +
                "permission_name VARCHAR(50), \r\n" +
                "action_id INT, \r\n" +
                "PRIMARY KEY(perm_id, permission_name, action_id)); \r\n");

        statement.execute("CREATE TABLE IF NOT EXISTS users ( \r\n" +
                "user_id VARCHAR(250) PRIMARY KEY, \r\n" +
                "username VARCHAR(50)  , \r\n" +
                "password VARCHAR(100)  \r\n" +
                "); \r\n");


        statement.execute("INSERT IGNORE INTO actions (action_id,action_name) VALUES " +
                "(0,'Add User'), " +
                "(1,'Edit User'), " +
                "(2,'Remove User'), " +
                "(3,'View Profile'), " +
                "(4,'Edit Profile'), " +
                "(5,'Change Password'), " +
                "(6,'View Public Content'), " +
                "(7,'Submit Appeal'), " +
                "(8,'Authenticate')");
        statement.execute("INSERT IGNORE INTO permissions (perm_id,permission_name, action_id) VALUES " +
                "(0,'DB_ADMIN',0), " +
                "(0,'DB_ADMIN',1), " +
                "(0,'DB_ADMIN',2), " +
                "(0,'DB_ADMIN',8), " +
                "(1,'USER',3), " +
                "(1,'USER',4), " +
                "(1,'USER',5), " +
                "(0,'DB_ADMIN',8), " +
                "(2,'ZERO-TRUSTED',6), " +
                "(2,'ZERO-TRUSTED',7), " +
                "(2,'ZERO-TRUSTED',8)");
    }

    public Statement getStatement() {
        return statement;
    }
}
