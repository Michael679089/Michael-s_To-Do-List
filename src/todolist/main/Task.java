package todolist.main;

public class Task {
	private int task_id;
	private boolean is_done;
	private String task_title;
	private int priority_level = 0;
	private String due_date;
	
	
	public Task(int task_id, boolean is_done, String task_title, int priority){
		this.task_id = task_id;
		this.is_done = is_done;
		this.task_title = task_title;
		this.priority_level = priority; 		
	};
	
	public Task(int task_id, boolean is_done, String task_title, int priority, String due_date){
		this.task_id = task_id;
		this.is_done = is_done;
		this.task_title = task_title;
		this.priority_level = priority; 		
		this.due_date = due_date;
	};
	
	public int getTaskID() {
		return task_id;
	};
	
	public String getTaskTitle() {
		return task_title;
	};
	
	public boolean getIsDone() {
		return is_done;
	};
	
	public int getPriorityLevel() {
		return priority_level;
	};
	
	public String getDueDate() {
		return due_date;
	};
};
