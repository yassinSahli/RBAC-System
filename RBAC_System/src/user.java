public class user {
    public String username;
    public String password;
    public String user_id;
    public int permission_id;

    // Constructor
    public user(String username, String password, String user_id, int permission_id) throws Exception {
        this.username = username;
        this.password = password;
        this.user_id = user_id;
        this.permission_id = permission_id;
        DatabaseHelper.statement.execute(String.format("INSERT IGNORE INTO USERS (username, password, user_id, permission_id) VALUES ('%s', '%s', %d, %d) ;", username, password, user_id, permission_id));
    }

    // Getters and Setters
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getUserId() {
        return user_id;
    }

    public void setUserId(String user_id) {
        this.user_id = user_id;
    }

    public int getPermissionId() {
        return permission_id;
    }

    public void setPermissionId(int permission_id) {
        this.permission_id = permission_id;
    }
}