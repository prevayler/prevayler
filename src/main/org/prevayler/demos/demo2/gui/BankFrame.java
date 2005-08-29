package org.prevayler.demos.demo2.gui;

import org.prevayler.Prevayler;
import org.prevayler.foundation.Cool;

import javax.swing.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

public class BankFrame extends JFrame {

	private static final long serialVersionUID = 936051923275473259L;
	private final Prevayler _prevayler;

	public BankFrame(Prevayler prevayler) {
    		super("Bank");
    		_prevayler = prevayler;
    		
		setDefaultCloseOperation(EXIT_ON_CLOSE);

		JDesktopPane desktop = new JDesktopPane();
		setContentPane(desktop);
		new AllAccountsFrame(prevayler, desktop);
		desktop.add(new RobustnessFrame());
		desktop.add(new MatchFrame(prevayler));
		setBounds(40,40,550,420);
		setVisible(true);

		refreshClock();
	}


	private void refreshClock() {
		Thread clockRefresher = new Thread() {
			public void run() {
				while (true) {
					DateFormat format = new SimpleDateFormat("hh:mm:ss");
					setTitle("Bank - " + format.format(_prevayler.clock().time()));
					Cool.sleep(500);
				}
			}
		};
		clockRefresher.setDaemon(true);
		clockRefresher.start();
	}

	
	private static class RobustnessFrame extends JInternalFrame {

		private static final long serialVersionUID = -3548833894524605027L;

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
