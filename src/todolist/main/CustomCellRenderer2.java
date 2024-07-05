package todolist.main;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.ListCellRenderer;

import javax.swing.JCheckBox;


public class CustomCellRenderer2 implements ListCellRenderer<Task> {

    @Override
    public Component getListCellRendererComponent(JList<? extends Task> list, Task task, int index, boolean isSelected, boolean isRollover) {
        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

        JCheckBox checkbox = new JCheckBox();
        checkbox.setBackground(null);
        panel.add(checkbox, BorderLayout.WEST);

        JPanel titlePanel = new JPanel(new BorderLayout());
        titlePanel.setBackground(null);

        JLabel title = new JLabel(task.getTaskTitle());
        title.setBackground(null);
        titlePanel.add(title, BorderLayout.NORTH);
        JLabel description = new JLabel(getPriorityString(task.getPriorityLevel()));
        description.setBackground(null);
        titlePanel.add(description, BorderLayout.SOUTH);
        
        panel.add(titlePanel, BorderLayout.CENTER);


        if (isSelected) {
            panel.setBackground(list.getSelectionBackground());
        } else {
            panel.setBackground(list.getBackground());
        }
        
        checkbox.setSelected(task.getIsDone());
        
        if (task.getIsDone()) {
            // Add a strikethrough decoration to the label
            title.setText("<html><strike>" + title.getText() + "</strike></html>");
            title.setForeground(Color.GRAY);
        } else {
            // Remove the strikethrough decoration from the label
            title.setText(title.getText());
            title.setForeground(Color.BLACK);
        }

        return panel;
    }

    private String getPriorityString(int priorityLevel) {
        switch (priorityLevel) {
            case 0:
                return "No Priority";
            case 1:
                return "Low Priority";
            case 2:
                return "Medium Priority";
            case 3:
                return "High Priority";
            default:
                return "";
        }
    }
}
