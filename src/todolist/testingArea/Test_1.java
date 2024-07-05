package todolist.testingArea;

import java.awt.EventQueue;
import java.awt.FlowLayout;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;

import javax.swing.JPanel;

import java.awt.BorderLayout;
import java.awt.Component;

import java.awt.GridLayout;

public class Test_1 {

	private JFrame frame;
	
	private String searchStr;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					Test_1 window = new Test_1();
					window.frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public Test_1() {
		initialize();
	}
	
	public Test_1(String searchStr) {
		initialize();
		
		this.searchStr = "Puss in Boots: The Last Wish";
	};

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frame = new JFrame();
		frame.setBounds(100, 100, 450, 300);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		JPanel panel = new JPanel();
		frame.getContentPane().add(panel, BorderLayout.WEST);
		panel.setLayout(new BorderLayout(0, 0));
		
		JPanel listPanel = new JPanel();
		panel.add(listPanel, BorderLayout.NORTH);
		
		
		searchTaskInDatabase(listPanel, searchStr);
		
		listPanel.setLayout(new GridLayout(0, 1, 0, 0));
		
	}
	
	
	
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
                		// String str = "- " + rows.getInt("task_id") + ": " + rows.getString("task_title");
                		
                		JPanel outerPanel = new JPanel(new FlowLayout());
                		JCheckBox taskCheckBox = new JCheckBox();
                		outerPanel.add(taskCheckBox);
                		
                		JPanel innerPanel = new JPanel(new BorderLayout());
                		JLabel taskTitle = new JLabel(rows.getString("task_title"));
                		innerPanel.add(taskTitle, BorderLayout.NORTH);
                		JLabel descriptionLabel = new JLabel();
                		descriptionLabel.setText(rows.getInt("task_id") + " "
                				+ rows.getInt("is_done") + " "
                				+ rows.getString("task_title") + " "
                				+ rows.getInt("task_priority") + " "
                				+ rows.getString("creation_date") + " "
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
