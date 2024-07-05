package todolist.testingArea;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class QueryAllTables {

    public static void main(String[] args) {
    	System.out.println("Starting the Query...");
    	
        try {
        	Connection connection = DriverManager.getConnection("jdbc:sqlite:Tasks.db");
            Statement statement = connection.createStatement();

            String sql = "SELECT name FROM sqlite_schema "
                    + "WHERE "
                    + "type = 'table' " 
                    + "AND "
                    + "name NOT LIKE 'sqlite_%'";
            ResultSet resultSet = statement.executeQuery(sql);
            
            while (resultSet.next()) {
                // Get the name of the table
                String tableName = resultSet.getString("name");
                System.out.println(tableName);

                // Create a new statement for the inner query
                Statement innerStatement = connection.createStatement();

                // Get a list of all the rows in the table
                ResultSet rows = innerStatement.executeQuery("SELECT * FROM `" + tableName + "`");

                // Iterate over the rows
                while (rows.next()) {
                	if (rows.getInt("is_done") == 1) {
                		System.out.println("- " + rows.getInt("task_id") + ": " + rows.getString("task_title"));            		
                	};
                };

                // Close the inner statement
                innerStatement.close();
            };

            // Close the connection
            connection.close();
        }
        catch (SQLException e) {
        	e.printStackTrace();
        };
    };
};

