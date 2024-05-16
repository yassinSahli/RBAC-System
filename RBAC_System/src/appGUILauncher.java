import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class appGUILauncher {
    @SuppressWarnings("unused")
    private DatabaseHelper dbHelper;
    private DefaultTableModel tableModel;
    private List<Object[]> userList = new ArrayList<>();
    private JTable table;
    private Map<String, List<String>> userPermissionsMap = new HashMap<>();
    private JLabel actionLabel;
    private JComboBox<String> actionComboBox;

    public appGUILauncher() {
        try {
            dbHelper = new DatabaseHelper();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void createAndShowGUI() {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }
    
        // Authentication
        String username = JOptionPane.showInputDialog("Enter username:");
        String password = JOptionPane.showInputDialog("Enter password:");
    
        // Check credentials
        if (username != null && password != null && username.equals(Administrator.USERNAME) && password.equals(Administrator.PASSWORD)) {
            // Proceed to launch GUI
            JFrame frame = new JFrame("RBAC Application");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    
            JPanel panel = new JPanel(new BorderLayout());
            panel.setBackground(Color.WHITE);
    
            JLabel headerLabel = new JLabel("Role-Based Access Control (RBAC) Application");
            headerLabel.setFont(new Font("Arial", Font.BOLD, 24));
            headerLabel.setHorizontalAlignment(JLabel.CENTER);
            panel.add(headerLabel, BorderLayout.NORTH);
    
            tableModel = new DefaultTableModel(new Object[]{"Username", "User ID", "Password", "Permissions"}, 0);
            table = new JTable(tableModel);
            table.getTableHeader().setFont(new Font("Arial", Font.BOLD, 16));
            table.setFont(new Font("Arial", Font.PLAIN, 16));
            table.setRowHeight(30);
            JScrollPane scrollPane = new JScrollPane(table);
    
            // User Management Section
            JPanel userManagementPanel = new JPanel(new BorderLayout());
            userManagementPanel.add(scrollPane, BorderLayout.CENTER);
            panel.add(userManagementPanel, BorderLayout.WEST);
    
            // Permission/Action Management Section
            JPanel permissionManagementPanel = new JPanel(new BorderLayout());
            actionLabel = new JLabel("Actions: ");
            actionLabel.setFont(new Font("Arial", Font.BOLD, 18));
            actionLabel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5)); // Add padding
            permissionManagementPanel.add(actionLabel, BorderLayout.NORTH);
            actionComboBox = new JComboBox<>();
            actionComboBox.setPreferredSize(new Dimension(250, 35)); // Increase size
            actionComboBox.setFont(new Font("Arial", Font.PLAIN, 16));
            actionComboBox.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5)); // Add padding
            permissionManagementPanel.add(actionComboBox, BorderLayout.CENTER);
            JLabel actionDescriptionLabel = new JLabel("Select action based on user permissions:");
            actionDescriptionLabel.setFont(new Font("Arial", Font.PLAIN, 16));
            permissionManagementPanel.add(actionDescriptionLabel, BorderLayout.SOUTH); // Add descriptive text
            panel.add(permissionManagementPanel, BorderLayout.EAST);
    
            JPanel buttonsPanel = new JPanel(new FlowLayout());
            buttonsPanel.setBackground(Color.WHITE);
            JButton grantButton = new JButton("Grant Permission");
            JButton revokeButton = new JButton("Revoke Permission");
            JButton addUserButton = new JButton("Add User");
            JButton removeUserButton = new JButton("Remove User");
            JButton exitButton = new JButton("Exit");
    
            buttonsPanel.add(grantButton);
            buttonsPanel.add(revokeButton);
            buttonsPanel.add(addUserButton);
            buttonsPanel.add(removeUserButton);
            buttonsPanel.add(exitButton);
    
            panel.add(buttonsPanel, BorderLayout.SOUTH);
    
            grantButton.addActionListener(e -> grantPermission());
            revokeButton.addActionListener(e -> revokePermission());
            addUserButton.addActionListener(e -> addUser());
            removeUserButton.addActionListener(e -> removeUser());
            exitButton.addActionListener(e -> {
                System.out.println("Exiting application. Goodbye!");
                frame.dispose();
            });
    
            table.getSelectionModel().addListSelectionListener(e -> {
                if (!e.getValueIsAdjusting()) {
                    String selectedUser = getSelectedUser();
                    if (selectedUser != null && userPermissionsMap.containsKey(selectedUser)) {
                        List<String> permissions = userPermissionsMap.get(selectedUser);
                        if (permissions != null && permissions.contains("DB_ADMIN")) {
                            actionLabel.setVisible(true);
                            actionComboBox.setVisible(true);
                            updateActionComboBox();
                        } else {
                            actionLabel.setVisible(false);
                            actionComboBox.setVisible(false);
                        }
                    } else {
                        actionLabel.setVisible(false);
                        actionComboBox.setVisible(false);
                    }
                }
                //  update the action combo box when a user is selected
                updateActionComboBox();
            });
    
    
            frame.getContentPane().add(panel);
            frame.pack();
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
        } else {
            // Incorrect credentials error msg
            JOptionPane.showMessageDialog(null, "Invalid username or password. Please try again.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    

    private void grantPermission() {
        String selectedUser = getSelectedUser();
        if (selectedUser != null) {
            String[] options = {"DB_Admin", "USER", "ZERO-TRUSTED"}; 
            int permissionChoice = JOptionPane.showOptionDialog(null, "Select permission to grant:", "Grant Permission", JOptionPane.DEFAULT_OPTION, JOptionPane.PLAIN_MESSAGE, null, options, options[0]);
            if (permissionChoice != JOptionPane.CLOSED_OPTION) {
                int permissionId = permissionChoice;
                List<String> userPermissions = userPermissionsMap.getOrDefault(selectedUser, new ArrayList<>());
                String permissionName = options[permissionId];
                userPermissions.add(permissionName.toUpperCase()); // Ensure uppercase
                userPermissionsMap.put(selectedUser, userPermissions);
                System.out.println("Permission granted to user: " + selectedUser);
                displayAllUsersAndPermissions(); // Update display after granting permission
            }
        }
    }
    

    private void revokePermission() {
        String selectedUser = getSelectedUser();
        if (selectedUser != null) {
            List<String> userPermissions = userPermissionsMap.getOrDefault(selectedUser, new ArrayList<>());
            if (!userPermissions.isEmpty()) {
                String[] options = userPermissions.toArray(new String[0]);
                String permissionToRevoke = (String) JOptionPane.showInputDialog(null, "Select permission to revoke:", "Revoke Permission", JOptionPane.PLAIN_MESSAGE, null, options, options[0]);
                if (permissionToRevoke != null) {
                    userPermissions.remove(permissionToRevoke);
                    userPermissionsMap.put(selectedUser, userPermissions);
                    System.out.println("Permission revoked from user: " + selectedUser + " - Permission: " + permissionToRevoke);
                    
                    // Remove the permission from the displayed table
                    displayAllUsersAndPermissions();
                }
            } else {
                JOptionPane.showMessageDialog(null, "No permissions to revoke for this user.", "Information", JOptionPane.INFORMATION_MESSAGE);
            }
        }
    }
    
    

    private void addUser() {
        // JOptionPane for the popup , and showInputDialog for the message
        String username = JOptionPane.showInputDialog("Enter username:");
        if (username != null && !username.isEmpty()) {
            String userId = UUID.randomUUID().toString();
            String password = JOptionPane.showInputDialog("Enter password:");

            try {
                DatabaseHelper.statement.execute(String.format("INSERT INTO users (username, password, user_id) VALUES ('%s', '%s', '%s');", username, password, userId));

                tableModel.addRow(new Object[]{username, userId, password, ""});

                Object[] userInfo = {username, userId, password};
                userList.add(userInfo);

                System.out.println("User added successfully.");
            } catch (SQLException e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(null, "Failed to add user to the database.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }



    private void removeUser() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow != -1) {
            String confirmationMessage = "Are you sure you want to remove this user?";
            int confirmOption = JOptionPane.showConfirmDialog(null, confirmationMessage, "Confirm Removal", JOptionPane.YES_NO_OPTION);
            if (confirmOption == JOptionPane.YES_OPTION) {
                String userId = (String) table.getValueAt(selectedRow, 1);
                try {
                    DatabaseHelper.statement.execute(String.format("DELETE FROM users WHERE user_id = '%s';" , userId));
                    System.out.println("User removed from the database.");

                    tableModel.removeRow(selectedRow);
                    userList.remove(selectedRow);
                } catch (SQLException e) {
                    e.printStackTrace();
                    JOptionPane.showMessageDialog(null, "Failed to remove user from the database.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        } else {
            JOptionPane.showMessageDialog(null, "Please select a user from the table.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void displayAllUsersAndPermissions() {
        tableModel.setRowCount(0);

        for (Object[] userData : userList) {
            String username = (String) userData[0];
            List<String> permissions = userPermissionsMap.getOrDefault(username, new ArrayList<>());
            StringBuilder permissionsString = new StringBuilder();
            for (String permission : permissions) {
                permissionsString.append(permission).append(", ");
            }
            if (permissionsString.length() > 0) {
                permissionsString.setLength(permissionsString.length() - 2);
            }
            tableModel.addRow(new Object[]{username, userData[1], userData[2], permissionsString.toString()});
        }
    }

    private String getSelectedUser() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow != -1) {
            return (String) table.getValueAt(selectedRow, 0);
        } else {
            return null;
        }
    }


    private void updateActionComboBox() {
        String selectedUser = getSelectedUser();
        if (selectedUser != null) {
            List<String> permissions = userPermissionsMap.getOrDefault(selectedUser, new ArrayList<>());
            String[] actions;
            if (permissions.contains("DB_ADMIN")) {
                actions = new String[]{"DB_ADMIN", "Add User", "Edit User", "Remove User"};
            } else if (permissions.contains("USER")) {
                actions = new String[]{"USER", "View Profile", "Edit Profile", "Change Password"};
            } else if (permissions.contains("ZERO-TRUSTED")) {
                actions = new String[]{"ZERO-TRUSTED", "View Public Content", "Submit Appeal"};
                
            } else {
                actions = new String[]{"No Permissions Granted!"};
            }
            actionComboBox.setModel(new DefaultComboBoxModel<>(actions));
            actionComboBox.setVisible(true);
        } else {
            actionComboBox.setVisible(false);
        }
    }
    


    public static void main(String[] args) {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            // init GUI
            SwingUtilities.invokeLater(() -> { 
                appGUILauncher app = new appGUILauncher();
                app.createAndShowGUI();
            });

            String url = "jdbc:mysql://localhost:3306/db";
            String username = "root";
            String password = "";
            // Establishing MySQLconnection
            Connection connection = DriverManager.getConnection(url, username, password);
            // closing the connection to free up ressources 
            connection.close();
        } catch (ClassNotFoundException e) {
            System.err.println("MySQL JDBC driver not found!");
            e.printStackTrace();
        } catch (SQLException e) {
            System.err.println("Failed to connect to the database!");
            e.printStackTrace();
        }
    }
}