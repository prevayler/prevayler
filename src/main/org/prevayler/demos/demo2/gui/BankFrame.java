package org.prevayler.demos.demo2.gui;

import javax.swing.Box;
import javax.swing.JDesktopPane;
import javax.swing.JFrame;
import javax.swing.JInternalFrame;
import javax.swing.JLabel;

import org.prevayler.util.QueryExecuter;

public class BankFrame extends JFrame {

	private final QueryExecuter _prevayler;

	public BankFrame(QueryExecuter prevayler) {
    		super("Bank");
    		_prevayler = prevayler;
    		
		setDefaultCloseOperation(EXIT_ON_CLOSE);

		JDesktopPane desktop = new JDesktopPane();
		setContentPane(desktop);
		new AllAccountsFrame(prevayler, desktop);
		desktop.add(new RobustnessFrame());
		
		setBounds(40,40,550,420);
		setVisible(true);
	}
	
	private class RobustnessFrame extends JInternalFrame {
		RobustnessFrame() {
			super("Robustness Reminder", false, false, false, true);
			setContentPane(Box.createVerticalBox());
			
			addLine(" You can kill this process at any time. ");
			addLine(" When you run the application again, ");
            addLine(" you will see that nothing was lost. ");
			
			setBackground(new java.awt.Color(204,204,204));
			setBounds(300,300,235,90);
			setVisible(true);
		}
		
		private void addLine(String line) {
			JLabel label = new JLabel(line);
			label.setAlignmentX(0.5f);
			getContentPane().add(label);
		}
	}
}
