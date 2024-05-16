import java.util.HashMap;
import java.util.LinkedList;
import java.sql.*;

public class permission {

    // RBAC system
public HashMap<String, LinkedList<String>> fetch(user user) throws Exception {

    Statement s2 = DriverManager.getConnection("jdbc:mysql://localhost:3306/db", "root", "").createStatement();
    ResultSet result_1 = DatabaseHelper.statement.executeQuery("SELECT * FROM permissions WHERE perm_id = " +  user.permission_id + ";");
    HashMap<String, LinkedList<String>> actPermMap = new HashMap<String, LinkedList<String>>();
    LinkedList<String> tmp = new LinkedList<String>(); 
    while (result_1.next()){
        ResultSet result_2 = s2.executeQuery("SELECT * FROM actions WHERE action_id ="+ result_1.getInt("action_id")+ ";");
        while (result_2.next()){
            tmp.add(result_2.getString("action_name"));
            
        }
        actPermMap.put(result_1.getString("permission_name"), tmp);

    }
    return actPermMap;
}

}