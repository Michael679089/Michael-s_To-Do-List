package todolist.main;

import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JList;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.SwingConstants;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JTextField;
import javax.swing.ScrollPaneConstants;

import java.awt.GridLayout;
import java.awt.Point;
import java.awt.Component;
import java.awt.Dimension;

import javax.swing.Box;
import java.awt.Font;
import javax.swing.JComboBox;
import javax.swing.DefaultComboBoxModel;
import javax.swing.ImageIcon;
import javax.swing.JToggleButton;
import javax.swing.BoxLayout;
import javax.swing.border.LineBorder;
import java.awt.Color;
import java.awt.SystemColor;
import javax.swing.UIManager;

public class Homepage {

	protected String dbFile = "jdbc:sqlite:Tasks.db";

	private JFrame frame;
	private JTextField inputTabNameTextField;
	
	private String currentListView = "Tasks";
	private JLabel lblCurrentListTitle = new JLabel("<Dynamic List Title>");
	private JTextField inputTaskTextField;
	private JPanel innerListPanel = new JPanel();
	private JList<Task> currentTaskList = new JList<Task>();
	private JPanel sidebarPanel = new JPanel();
	private JPanel panel = new JPanel();
	private JToggleButton tglbtnSidebar;
	private JTextField searchTaskBar;

	/**
	 * SECTION: Launch the application.
	 */
	
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() { // Creating a new Thread.
			public void run() {
				try {
					Homepage window = new Homepage();
					window.frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				};
			};
		});
	};

	/**
	 * SECTION: Create the application.
	 */
	
	public Homepage() {
		initialize();
		
		{ // Over here, we will be creating a default table called "tasks" which stores rows of uncategorized tasks. 
			String dbFile = "jdbc:sqlite:Tasks.db";
			Connection conn = null;
			Statement statement = null;
			
			try {
				String sql = "CREATE TABLE IF NOT EXISTS `" + currentListView + "` ("
						+ "task_id INTEGER PRIMARY KEY AUTOINCREMENT,"
						+ "is_done INTEGER NOT NULL CHECK (is_done IN (0, 1)),"
						+ "task_title TEXT,"
						+ "task_priority INTEGER DEFAULT 0 CHECK (task_priority BETWEEN 0 AND 3),"
						+ "creation_date TEXT NOT NULL"
						+ ")";			
				conn = DriverManager.getConnection(dbFile);
				statement = conn.createStatement();
				statement.execute(sql);
				
				statement.close();
				conn.close();
				
				System.out.println("Default Table called `Tasks` in " + dbFile + " is created.");
			}
			catch (SQLException e) {
				e.printStackTrace();
			};
			
			// Next, I will be creating a database for the user's preferences.
			String dbFile2 = "jdbc:sqlite:User.db";
			
			Connection conn2 = null;
			Statement statement2 = null;
			try
			{
				String sql = "CREATE TABLE IF NOT EXISTS UserPreferences ("
						+ "isDarkMode INTEGER NOT NULL DEFAULT 0"
						+ ")";
				conn2 = DriverManager.getConnection(dbFile2);
				statement2 = conn2.createStatement();
				statement2.execute(sql);
				
				statement2.close();
				conn2.close();
				
				System.out.println("UserPreferences created in: " + dbFile2);
			}
			catch (SQLException e) {
				e.printStackTrace();
			};
			
		};
		
		updateCurrentListViewTitle(currentListView);
	};

	/**
	 * SECTION: Initialize the contents of the frame.
	 */
	
	private void initialize() {
		System.out.println("Starting the " + this);
		
		frame = new JFrame("Michael's To Do List");
		frame.setBounds(100, 100, 853, 498);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(new BorderLayout(0, 0));
		frame.addComponentListener(new ComponentAdapter() {
		    public void componentResized(ComponentEvent e) { // The sidebar will disappear if the window becomes horizontally less than 500.
		        int width = frame.getWidth();
		        if (width < 500) {
		        	sidebarPanel.setVisible(false);
		        	tglbtnSidebar.setVisible(true);
		        	tglbtnSidebar.setSelected(false);
	    		} 
		        else {
		        	sidebarPanel.setVisible(true);
		        	tglbtnSidebar.setVisible(false);
		        };
		    };
		});
		sidebarPanel.setBorder(new LineBorder(new Color(0, 0, 0)));
		
		
		frame.getContentPane().add(sidebarPanel, BorderLayout.WEST);
		sidebarPanel.setLayout(new BorderLayout(0, 0));
		
		JPanel topbarPanel = new JPanel();
		sidebarPanel.add(topbarPanel, BorderLayout.NORTH);
		topbarPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
		topbarPanel.setBackground(SystemColor.textHighlight);
		
		JButton btnNewButton = new JButton("Settings");
		btnNewButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				System.out.println("Settings is clicked.");
				
				@SuppressWarnings("unused")
				Settings newSettings = new Settings(frame);
			}
		});
		topbarPanel.add(btnNewButton);
		
		JPanel sideBarTitlePanel = new JPanel();
		topbarPanel.add(sideBarTitlePanel);
		sideBarTitlePanel.setBackground(null);
		
		JLabel lblSidebarTitle = new JLabel("Sidebar Area");
		lblSidebarTitle.setForeground(UIManager.getColor("Button.highlight"));
		lblSidebarTitle.setFont(new Font("Tahoma", Font.BOLD, 11));
		sideBarTitlePanel.add(lblSidebarTitle);
		
		JScrollPane scrollPane = new JScrollPane();
		sidebarPanel.add(scrollPane, BorderLayout.CENTER);
		
		JPanel paneViewHeader = new JPanel(new BorderLayout());
		scrollPane.setColumnHeaderView(paneViewHeader);

		JPanel searchbarPanel = new JPanel();
		paneViewHeader.add(searchbarPanel, BorderLayout.NORTH);
		searchbarPanel.setLayout(new BoxLayout(searchbarPanel, BoxLayout.Y_AXIS));
		searchTaskBar = new JTextField();
		searchTaskBar.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				System.out.println("Search Bar: User has pressed enter.");
				
				String searchStr = searchTaskBar.getText();
				
				Point homepageLocation = frame.getLocation(); // Get the location of the homepage window.
				
				@SuppressWarnings("unused")
				searchResultsQueryFrame searchResultFrame = new searchResultsQueryFrame(searchStr, homepageLocation);
			};
		});
		
		JLabel lblTaskListTitle = new JLabel("Task Lists");
		lblTaskListTitle.setHorizontalAlignment(SwingConstants.CENTER);
		paneViewHeader.add(lblTaskListTitle, BorderLayout.SOUTH);
		
		
		JPanel innerSearchTitlePanel = new JPanel();
		searchbarPanel.add(innerSearchTitlePanel);
		innerSearchTitlePanel.setLayout(new BoxLayout(innerSearchTitlePanel, BoxLayout.X_AXIS));
		JLabel lblSearchTitle = new JLabel("Searchbar (Press Enter to Search):");
		lblSearchTitle.setHorizontalAlignment(SwingConstants.CENTER);
		innerSearchTitlePanel.add(lblSearchTitle);
		
		searchbarPanel.add(searchTaskBar);
		searchTaskBar.setColumns(10);
		
		
		JPanel tableListPanel = new JPanel();
		scrollPane.setViewportView(tableListPanel);
		tableListPanel.setLayout(new BorderLayout(0, 0));
		
		JPanel listPanel = new JPanel();
		tableListPanel.add(listPanel, BorderLayout.CENTER);
		scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
		listPanel.setLayout(new BorderLayout(0, 0));
		
		listPanel.add(innerListPanel, BorderLayout.NORTH);
		innerListPanel.setLayout(new GridLayout(0, 1, 0, 0));
		populatePanelWithJButtons(innerListPanel);
		
		JPanel inputPanel = new JPanel();
		sidebarPanel.add(inputPanel, BorderLayout.SOUTH);
		inputPanel.setLayout(new BoxLayout(inputPanel, BoxLayout.Y_AXIS));
		
		JLabel lblNewLabel = new JLabel("Add new Table here. \\/");
		inputPanel.add(lblNewLabel);
		lblNewLabel.setHorizontalAlignment(SwingConstants.CENTER);
		
		JPanel panel_1 = new JPanel();
		inputPanel.add(panel_1);
		panel_1.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
		
		inputTabNameTextField = new JTextField();
		panel_1.add(inputTabNameTextField);
		inputTabNameTextField.setToolTipText("Add tables here");
		inputTabNameTextField.setColumns(10);
		inputTabNameTextField.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				System.out.println("User pressed enter on this textfield.");
				
				String tableName = inputTabNameTextField.getText();
				if (tableName.isBlank() || tableName.isBlank()) {
					System.out.println("ERROR: New Task Table is either blank or empty.");
				}
				else {
					addTable(tableName);
					updateCurrentListViewTitle(tableName);
				};
				
				inputTabNameTextField.setText(null);
				getListOfTables();
			};
		});
		
		JPanel currentListViewPanel = new JPanel();
		currentListViewPanel.setBackground(new Color(17, 101, 168));
		frame.getContentPane().add(currentListViewPanel, BorderLayout.CENTER);
		currentListViewPanel.setLayout(new BorderLayout(0, 0));
		
		JPanel titlePanel = new JPanel();
		currentListViewPanel.add(titlePanel, BorderLayout.NORTH);
		titlePanel.setLayout(new BorderLayout(0, 0));
		lblCurrentListTitle.setForeground(SystemColor.window);
		titlePanel.add(lblCurrentListTitle);
		titlePanel.setBackground(null);
		
		lblCurrentListTitle.setFont(new Font("Tahoma", Font.BOLD, 15));
		lblCurrentListTitle.setHorizontalAlignment(SwingConstants.CENTER);
		
		JButton btnDeleteTable = new JButton("Delete Table");
		titlePanel.add(btnDeleteTable, BorderLayout.EAST);
		btnDeleteTable.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				System.out.println("Remove Table called.");
				
				removeTable(currentListView); // This removes the currently selected Task Table.
				
				{ // Find JButton in innerListPanel. If Found, Remove JButton in JPanel.
					for (Component component : innerListPanel.getComponents()) {
			            if (component instanceof JButton) {
			                JButton button = (JButton) component;
			                if (button.getText().equals(currentListView)) {
			                	component.setVisible(false);
			                	innerListPanel.remove(component);
			                };
			            };
			        };
				};
			};
		});
		
		Component verticalStrut_2 = Box.createVerticalStrut(17);
		titlePanel.add(verticalStrut_2, BorderLayout.SOUTH);
		
		Component verticalStrut_3 = Box.createVerticalStrut(10);
		titlePanel.add(verticalStrut_3, BorderLayout.NORTH);
		
		titlePanel.add(panel, BorderLayout.WEST);
		panel.setBackground(null);
		
		ImageIcon icon = new ImageIcon("D:\\Java Finals\\ToDo_List\\icons\\sidebar.png");
		tglbtnSidebar = new JToggleButton(icon);
		tglbtnSidebar.setPreferredSize(new Dimension(30, 30));
		panel.add(tglbtnSidebar);
		tglbtnSidebar.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (tglbtnSidebar.isSelected()) {
					sidebarPanel.setVisible(true);
				}
				else {
					sidebarPanel.setVisible(false);					
				};
			};
		});
		
		JPanel inputTaskPanel = new JPanel();
		currentListViewPanel.add(inputTaskPanel, BorderLayout.SOUTH);
		inputTaskPanel.setLayout(new BorderLayout(0, 0));
		inputTaskPanel.setBackground(null);
		
		JPanel innerInputTaskPanel_1 = new JPanel();
		inputTaskPanel.add(innerInputTaskPanel_1);
		innerInputTaskPanel_1.setLayout(new BorderLayout(0, 0));
		
		JComboBox<String> comboBoxPriority = new JComboBox<String>();
		comboBoxPriority.setModel(new DefaultComboBoxModel<String>(new String[] {"No Priority", "Low Priority", "Medium Priority", "High Priority"}));
		innerInputTaskPanel_1.add(comboBoxPriority, BorderLayout.SOUTH);
		
		inputTaskTextField = new JTextField();
		innerInputTaskPanel_1.add(inputTaskTextField);
		inputTaskTextField.addActionListener(new ActionListener() {
		    public void actionPerformed(ActionEvent e) {
		        System.out.println("inputTaskField, user has pressed enter.");

		        String taskTitle = inputTaskTextField.getText();

		        int priorityLevel = 0;
		        { // Getting the Priority Levels.
		        	// Manually selecting the Priority Level.
		        	if (comboBoxPriority.getSelectedItem() == "No Priority") {
		        		priorityLevel = 0; 
		        	}
		        	else if (comboBoxPriority.getSelectedItem() == "Low Priority") {
		        		priorityLevel = 1;
		        	}
		        	else if (comboBoxPriority.getSelectedItem() == "Medium Priority") {
		        		priorityLevel = 2;
		        	}
		        	else {
		        		priorityLevel = 3;
		        	};
		        	
		        	// Search for priority level string preceded by hashtag.
		        	Pattern pattern = Pattern.compile("#(Low|Medium|High|LowPriority|MediumPriority|HighPriority)\\b");
		        	Matcher matcher = pattern.matcher(taskTitle);
		        	if (matcher.find()) {
		        		String priorityString = matcher.group(1);
		        		switch (priorityString) {
		        		case "Low":
		        			priorityLevel = 1;
		        			break;
		        		case "LowPriority":
		        			priorityLevel = 1;
		        			break;
		        		case "Medium":
		        			priorityLevel = 2;
		        			break;
		        		case "MediumPriority":
		        			priorityLevel = 2;
		        			break;
		        		case "High":
		        			priorityLevel = 3;
		        			break;
		        		case "HighPriority":
		        			priorityLevel = 3;
		        			break;
		        		};
		        		// Remove priority string from task title
		        		taskTitle = taskTitle.replaceAll("#(Low|Medium|High|LowPriority|MediumPriority|HighPriority)\\b", "").trim();
		        	};
		        };

		        if (taskTitle.isBlank() || taskTitle.isEmpty()) {
		            System.out.println("ERROR: The current task input is empty.");
		        } else {
		        	
		        	
		        	
		            addTask(currentListView, taskTitle, priorityLevel);
		            updateJListWithTasks();
		        }

		        inputTaskTextField.setText(null);
		    }
		});

		inputTaskTextField.setColumns(10);
		
		Component verticalStrut = Box.createVerticalStrut(23);
		inputTaskPanel.add(verticalStrut, BorderLayout.NORTH);
		
		Component verticalStrut_1 = Box.createVerticalStrut(10);
		inputTaskPanel.add(verticalStrut_1, BorderLayout.SOUTH);
		
		Component horizontalStrut_1 = Box.createHorizontalStrut(20);
		inputTaskPanel.add(horizontalStrut_1, BorderLayout.WEST);
		
		Component horizontalStrut_2 = Box.createHorizontalStrut(20);
		inputTaskPanel.add(horizontalStrut_2, BorderLayout.EAST);
		
		JScrollPane scrollPaneForCurrentTaskList = new JScrollPane();
		currentListViewPanel.add(scrollPaneForCurrentTaskList, BorderLayout.CENTER);
		scrollPaneForCurrentTaskList.setBackground(null);
		
		scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
		
		System.out.println(currentTaskList.getName() + " is being initialized.");
		scrollPaneForCurrentTaskList.setViewportView(currentTaskList);
		currentTaskList.setBackground(null);
		currentTaskList.setOpaque(false);
		
		
		JPanel taskSideBarPanel = new JPanel();
		frame.getContentPane().add(taskSideBarPanel, BorderLayout.EAST);
		taskSideBarPanel.setLayout(new BorderLayout(0, 0));
		
		JPanel panel_3 = new JPanel();
		taskSideBarPanel.add(panel_3, BorderLayout.NORTH);
		panel_3.setLayout(new BorderLayout(0, 0));
		taskSideBarPanel.setVisible(false);
		
		JLabel lblNewLabel_1 = new JLabel("Information about Current Selected Task.");
		panel_3.add(lblNewLabel_1, BorderLayout.NORTH);
		currentTaskList.addMouseListener(new MouseAdapter() { // Action 2.
			@Override
			public void mouseClicked(MouseEvent e) {
				if (e.getButton() == MouseEvent.BUTTON1) { // check if left mouse button was clicked
					int index = currentTaskList.locationToIndex(e.getPoint());
					Task task = currentTaskList.getModel().getElementAt(index);
					System.out.println("Row " + index + " was clicked. Task: " + task.getTaskID());
					
					if (!task.getIsDone()) {
						checkTask(currentListView, task.getTaskID());						
					}
					else {
						uncheckTask(currentListView, task.getTaskID());
					}
				}
				else if (e.getButton() == MouseEvent.BUTTON3) {
					int index = currentTaskList.locationToIndex(e.getPoint());
					Task task = currentTaskList.getModel().getElementAt(index);
					System.out.println("Row " + index + " was clicked. Task: " + task.getTaskID());
					
					deleteTask(currentListView, task.getTaskID());
				};
			};
		});
		updateJListWithTasks();
		
	};
	
	// Other Methods
	
	public void getListOfTables() {
		Connection conn = null;
		Statement statement = null;
		
		try {
			conn = DriverManager.getConnection(dbFile);
			statement = conn.createStatement();
			
			String sql="SELECT name FROM sqlite_schema "
					+ "WHERE "
					+ "type = 'table' AND "
					+ "name NOT LIKE 'sqlite_sequence%';";
			ResultSet rs = statement.executeQuery(sql);
			
			while (rs.next()) {
				System.out.println(rs.getString("name"));
			};
			
			rs.close();
			statement.close();
			conn.close();
		}
		catch (SQLException e) {
			e.printStackTrace();
		};
	};
	
	
	
	
	
	public boolean isThereTablesInDatabase() {
		boolean flag = false;
		Connection conn = null;
		Statement statement = null;
		
		try {
			conn = DriverManager.getConnection(dbFile);
			statement = conn.createStatement();
			
			String sql="SELECT name FROM sqlite_schema "
					+ "WHERE "
					+ "type = 'table' AND "
					+ "name NOT LIKE 'sqlite_sequence%';";
			ResultSet rs = statement.executeQuery(sql);
			
			if (rs.next()) {
				flag = true;
			} 
			else {
				flag = false;
			};
			
			rs.close();
			statement.close();
			conn.close();
		}
		catch (SQLException e) {
			e.printStackTrace();
		};
		
		return flag;
	};
	
	public boolean checkTableExists(String tableName) {
		boolean flag = false;
		
		Connection conn = null;
		Statement statement = null;
		
		System.out.println("Entered DBFile: " + dbFile + "\n" + "Entered Name: " + tableName);
		
		try {
			conn = DriverManager.getConnection(dbFile);
			statement = conn.createStatement();
			
			String sql = "SELECT name FROM sqlite_schema "
					+ "WHERE "
					+ "type = 'table' AND "
					+ "name = '" 
					+ tableName 
					+ "' AND "
					+ "name NOT LIKE 'sqlite_%'";
			ResultSet rs = statement.executeQuery(sql);
			
			if (!rs.next()) {
				flag = true;
				System.out.println("No duplicate table found.");
			}
			else {				
				System.out.println("ERROR: Table exists.");
			};
			
			rs.close();
			statement.close();
			conn.close();
		}
		catch (SQLException e) {
			e.printStackTrace();
		};
		
		return flag;
	};
	
	public void updateCurrentListViewTitle(String currentTitle) {
		lblCurrentListTitle.setText(currentTitle);
		updateJListWithTasks();
	};
	
	public void updateJListWithTasks() {
		DefaultListModel<Task> newDefaultListModel = new DefaultListModel<Task>();
		
		Connection conn = null;
		Statement statement = null;
		
		try {
			conn = DriverManager.getConnection(dbFile);
			statement = conn.createStatement();
			
			String sql = "SELECT * FROM `" + currentListView + "`";
			ResultSet rs = statement.executeQuery(sql);
			
			while (rs.next()) {
				int task_id = rs.getInt("task_id");
				boolean is_done = rs.getBoolean("is_done");
				String task_title = rs.getString("task_title");
				int task_priority = rs.getInt("task_priority");
				
				Task newTask = new Task(task_id, is_done, task_title, task_priority);
				newDefaultListModel.addElement(newTask);
			}				
			
			rs.close();
			statement.close();
			conn.close();
			
			currentTaskList.setModel(newDefaultListModel);
			currentTaskList.setCellRenderer(new CustomCellRenderer2());
		}
		catch (SQLException e) {
			e.printStackTrace();
		};
	};
	
	public void populatePanelWithJButtons(JPanel panel) {
		Connection conn = null;
		Statement statement = null;
		
		try {
			conn = DriverManager.getConnection(dbFile);
			statement = conn.createStatement();
			
			String sql="SELECT name FROM sqlite_master "
					+ "WHERE "
					+ "type = 'table' AND "
					+ "name NOT LIKE 'sqlite_sequence%';";
			ResultSet rs = statement.executeQuery(sql);
			
			while (rs.next()) { // If there are results in the database. Create JButtons for the names of each table.
				String name = rs.getString("name");
				
		        JButton newbtnTaskList = new JButton(name);
		        newbtnTaskList.addActionListener(new ActionListener() {
		        	@Override
		        	public void actionPerformed(ActionEvent e) {
		        		currentListView = newbtnTaskList.getText();
		        		System.out.println("The button " + name + " from the sidebar panel is clicked.");
		        		updateCurrentListViewTitle(currentListView);
		        	};
		        });
				
                panel.add(newbtnTaskList);
			};
			
			rs.close();
			statement.close();
			conn.close();
		}
		catch (SQLException e) {
			e.printStackTrace();
		};
		System.out.println("Current List: " + currentListView);
	};
	
	private void addTable(String tableName) {
		System.out.println("Add Table called: " + tableName);
		
		Connection conn = null;
		Statement statement = null;
		
		try {
			String sql = "CREATE TABLE `"
					+ tableName
					+ "`("
					+ "task_id INTEGER PRIMARY KEY AUTOINCREMENT,"
					+ "is_done INTEGER NOT NULL CHECK (is_done IN (0, 1)),"
					+ "task_title TEXT,"
					+ "task_priority INTEGER DEFAULT 0 CHECK (task_priority BETWEEN 0 AND 3),"
					+ "creation_date TEXT NOT NULL"
					+ ")";		
			conn = DriverManager.getConnection(dbFile);
			statement = conn.createStatement();
			statement.execute(sql);
			
			statement.close();
			conn.close();
			
			
			JButton newbtnTaskList = new JButton(tableName); // Adding the btn to the listPanel.
			newbtnTaskList.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					currentListView = newbtnTaskList.getText();
					System.out.println("The button " + tableName + " from the sidebar panel is clicked.");
					updateCurrentListViewTitle(currentListView);
				};
			});
			newbtnTaskList.setVisible(true);
			innerListPanel.updateUI(); // updates the GUI Panel, so that it will show the new btn list.
			innerListPanel.add(newbtnTaskList);
			
			System.out.println("- A new button " + tableName +" is added.");
		}
		catch (SQLException e) {
			System.out.println("ERROR: Table already exists. Can't add a new table of the same name.");
		};
		
	};
	
	private void removeTable(String tableName) {
		System.out.println("Remove Table is Called: " + tableName);
		
		Connection conn = null;
		Statement statement = null;
		
		try {
			String sql = "DROP TABLE '" + tableName + "'";
			conn = DriverManager.getConnection(dbFile);
			statement = conn.createStatement();
			
			statement.execute(sql);
			
			statement.close();
			conn.close();
		}
		catch (SQLException e) {
			System.out.println("ERROR: Can't Remove Table. Table doesn't exist.");
		};
	};
	
	private void addTask(String currentListView, String taskName, int priorityLevel) {
		System.out.println("Add Task is Called: " + taskName);
		
		Connection conn = null;
		PreparedStatement preparedStatement = null;
		
		try {
			conn = DriverManager.getConnection(dbFile);
			
			String sql = "INSERT INTO `" + currentListView + "` "
					+ "("
					+ "is_done, "
					+ "task_title, "
					+ "task_priority, "
					+ "creation_date"
					+ ")"
					+ "VALUES (?, ?, ?, ?)";
			preparedStatement = conn.prepareStatement(sql);
			
			// Replace values to preparedStatement.
			preparedStatement.setInt(1, 0);
			preparedStatement.setString(2, taskName);
			preparedStatement.setInt(3, priorityLevel);
			String creationDate;
			{
				LocalDateTime localDateTime = LocalDateTime.now();
		        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/dd/yyyy - HH:mm a");
		        String formattedDateTime = formatter.format(localDateTime);
		        creationDate = formattedDateTime;
			};
			preparedStatement.setString(4, creationDate);
			
			preparedStatement.executeUpdate();
			
			preparedStatement.close();
			conn.close();
		}
		catch (SQLException e) {
			e.printStackTrace();
		};
	};

	private void checkTask(String currentListView, int task_id) {
		System.out.println("complete Task is Called: " + task_id);

		String dbFile = "jdbc:sqlite:Tasks.db";
		Connection conn = null;
		Statement statement = null;
		 
		try {
			conn = DriverManager.getConnection(dbFile);
		    statement = conn.createStatement();
		    
		    String sql = "UPDATE '" + currentListView + "' SET is_done = 1 WHERE task_id = " + task_id;
		    statement.execute(sql);

		    statement.close();
		    conn.close();
	    } 
		catch (SQLException e) {
			e.printStackTrace();
		};
		
		updateJListWithTasks();
	};
	
	private void uncheckTask(String currentListView, int task_id) {
		System.out.println("complete Task is Called: " + task_id);

		String dbFile = "jdbc:sqlite:Tasks.db";
		Connection conn = null;
		Statement statement = null;
		 
		try {
			conn = DriverManager.getConnection(dbFile);
		    statement = conn.createStatement();
		    
		    String sql = "UPDATE '" + currentListView + "' SET is_done = 0 WHERE task_id = " + task_id;
		    statement.execute(sql);

		    statement.close();
		    conn.close();
	    } 
		catch (SQLException e) {
			e.printStackTrace();
		};
		
		updateJListWithTasks();
	};
	
	private void deleteTask(String currentListView, int task_id) {
	    System.out.println("deleteTask is called: " + task_id);

	    String dbFile = "jdbc:sqlite:Tasks.db";
	    Connection conn = null;
	    Statement statement = null;

	    try {
	        conn = DriverManager.getConnection(dbFile);
	        statement = conn.createStatement();

	        String sql = "DELETE FROM '" + currentListView + "' WHERE task_id = " + task_id;
	        statement.execute(sql);

	        statement.close();
	        conn.close();
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }

	    updateJListWithTasks();
	};
};