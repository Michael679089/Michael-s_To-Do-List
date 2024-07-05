package todolist.main;

import java.awt.Color;
import java.awt.Component;

import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.ListCellRenderer;

class CustomCellRenderer extends JLabel implements ListCellRenderer<Task> {
	private static final long serialVersionUID = 1L;

	@Override
    public Component getListCellRendererComponent(JList<? extends Task> list, Task task, int index, boolean isSelected, boolean isRollover) {
        String taskTitle = task.getTaskTitle();
        String priority = "";
        switch (task.getPriorityLevel()) {
            case 0:
                priority = "No Priority";
                break;
            case 1:
                priority = "Low";
                break;
            case 2:
                priority = "Medium";
                break;
            case 3:
                priority = "High";
                break;
        }
        setText(taskTitle + " (" + priority + ")");
        
        if (task.getIsDone()) {
            // Add a strikethrough decoration to the label
            setText("<html><strike>" + taskTitle + "</strike></html> (" + priority + ")");
            setForeground(Color.GRAY);
        } else {
            // Remove the strikethrough decoration from the label
            setText(taskTitle + " (" + priority + ")");
            setForeground(Color.BLACK);
        }
        
        // Set the background color of the cell based on selection and hover state
        if (isSelected) {
            setBackground(list.getSelectionBackground());
        } else if (isRollover) {
            setBackground(Color.lightGray);
        } else {
            setBackground(list.getBackground());
        }
        
        setOpaque(true);
        return this;
    }
}
