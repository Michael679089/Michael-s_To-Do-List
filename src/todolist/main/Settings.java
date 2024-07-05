package todolist.main;

import javax.swing.JFrame;
import java.awt.BorderLayout;
import javax.swing.JPanel;
import java.awt.FlowLayout;
import javax.swing.JLabel;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Point;

import javax.swing.JCheckBox;
import javax.swing.SwingConstants;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowFocusListener;
import java.awt.event.ActionEvent;

public class Settings {

    private JFrame frame = new JFrame("Settings Area.");

    public Settings(JFrame homepageFrame) {
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.getContentPane().setLayout(new BorderLayout(0, 0));
        frame.addWindowFocusListener(new WindowFocusListener() {
            @Override
            public void windowGainedFocus(WindowEvent e) {
            	frame.setAlwaysOnTop(true);
            }

			@Override
			public void windowLostFocus(WindowEvent e) {}
        });

        JPanel titlePanel = new JPanel();
        frame.getContentPane().add(titlePanel, BorderLayout.NORTH);
        titlePanel.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));

        JLabel lblNewLabel = new JLabel("Settings Panel");
        lblNewLabel.setFont(new Font("Tahoma", Font.BOLD, 11));
        titlePanel.add(lblNewLabel);

        JPanel panel_1 = new JPanel();
        frame.getContentPane().add(panel_1, BorderLayout.CENTER);
        panel_1.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));

        JPanel panel = new JPanel();
        panel_1.add(panel);
        panel.setLayout(new GridLayout(0, 2, 0, 0));

        JLabel lblDarkMode = new JLabel("dark Mode (not available)");
        panel.add(lblDarkMode);

        JCheckBox checkboxDarkMode = new JCheckBox("Activate");
        checkboxDarkMode.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                System.out.println("Checkbox For Darkmode is clicked: Checkbox Status: " + checkboxDarkMode.isSelected());
            };
        });
        checkboxDarkMode.setHorizontalAlignment(SwingConstants.TRAILING);
        panel.add(checkboxDarkMode);
        
        JLabel lblHomePageAlwaysOnTop = new JLabel("Home Page is always on top");
        panel.add(lblHomePageAlwaysOnTop);
        
        JCheckBox checkboxAlwaysOnTop = new JCheckBox("Activate");
        checkboxAlwaysOnTop.addActionListener(new ActionListener() {
        	public void actionPerformed(ActionEvent e) {
        		System.out.println("Checkbox For Always On Top is clicked: Checkbox Status: " + checkboxDarkMode.isSelected());
        		
        		homepageFrame.setAlwaysOnTop(checkboxAlwaysOnTop.isSelected());
        	};
        });
        checkboxAlwaysOnTop.setHorizontalAlignment(SwingConstants.TRAILING);
        panel.add(checkboxAlwaysOnTop);

        // Get the location of the homepage window.
        Point homepageLocation = homepageFrame.getLocation();

        // Set the location of the settings window to the same location.
        frame.setLocation(homepageLocation);

        frame.pack();
        frame.setVisible(true);
    }

}
