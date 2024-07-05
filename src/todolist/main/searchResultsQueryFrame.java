package todolist.main;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.Point;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.BoxLayout;

public class searchResultsQueryFrame {
	String searchStr;
	
	public searchResultsQueryFrame(String searchStr, Point point) {
		JFrame frame = new JFrame();
		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		frame.getContentPane().setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
		
		JPanel listPanel = new JPanel(); // The Panel.
		frame.getContentPane().add(listPanel);
		listPanel.setLayout(new BorderLayout(0, 0));
		
		JPanel innerListPanel = new JPanel();
		listPanel.add(innerListPanel, BorderLayout.NORTH);
		
		searchTaskInDatabase(innerListPanel, searchStr);
		
		innerListPanel.setLayout(new GridLayout(0, 1, 0, 0));
		
		frame.setLocation(point); // Set the location of the settings window to the same location.
		frame.pack();
		frame.setVisible(true);
	};
	
	private void searchTaskInDatabase(JPanel panel, String searchStr) {
		try {
			for (Component MyPanel : panel.getComponents()) {
			    if (MyPanel instanceof JPanel) {
			        // do something with the JButton
			    	MyPanel.setVisible(false);
			    	panel.remove(MyPanel);
			    };
			};
			
        	Connection connection = DriverManager.getConnection("jdbc:sqlite:Tasks.db");
            Statement statement = connection.createStatement();

            String sql = "SELECT name FROM sqlite_master "
                    + "WHERE "
                    + "type = 'table' " 
                    + "AND "
                    + "name NOT LIKE 'sqlite_%'";
            ResultSet resultSet = statement.executeQuery(sql);
            
            while (resultSet.next()) {
                String tableName = resultSet.getString("name");
                System.out.println(tableName);

                // Create a new statement for the inner query
                Statement innerStatement = connection.createStatement();

                // Get a list of all the rows in the table
                ResultSet rows = innerStatement.executeQuery("SELECT * FROM `" + tableName + "` WHERE task_title LIKE '" + searchStr + "'; ");

                // Iterate over the rows
                while (rows.next()) {
            		JPanel outerPanel = new JPanel(new FlowLayout());
            		JCheckBox taskCheckBox = new JCheckBox();
            		outerPanel.add(taskCheckBox);
            		
            		JPanel innerPanel = new JPanel(new BorderLayout());
            		JLabel taskTitle = new JLabel(rows.getString("task_title"));
            		innerPanel.add(taskTitle, BorderLayout.NORTH);
            		JLabel descriptionLabel = new JLabel();
            		descriptionLabel.setText("Task ID: " + rows.getInt("task_id") + " "
            				+ "Is Complete: " + rows.getInt("is_done") + " "
            				+ "Task Title: " + rows.getString("task_title") + " "
            				+ "Task Priority Level: " + rows.getInt("task_priority") + " "
            				+ "Task Creation Date: " + rows.getString("creation_date") + " "
            				);
            		innerPanel.add(descriptionLabel, BorderLayout.SOUTH);
            		outerPanel.add(innerPanel);
            		
            		panel.add(outerPanel);
            		panel.updateUI();
                };

                // Close the inner statement
                rows.close();
                innerStatement.close();
            };

            // Close the connection
            resultSet.close();
            statement.close();
            connection.close();
        }
        catch (SQLException e) {
        	e.printStackTrace();
        };
	};

}

