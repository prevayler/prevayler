package org.prevayler.demos.demo2.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.text.DecimalFormat;
import java.text.ParseException;

import javax.swing.Box;
import javax.swing.JInternalFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import org.prevayler.Prevayler;


abstract class AccountFrame extends JInternalFrame {

	protected final Prevayler _prevayler;
	protected final JTextField holderField = new JTextField();

	protected String holderText() {
		return holderField.getText();
	}
		
	AccountFrame(String title, Prevayler prevayler, Container container) {
		super(title, false, true); //Not resizable. Closable.
		_prevayler = prevayler;

		container.add(this);
		this.setBackground(new Color(204, 204, 204));

		getContentPane().add(Box.createVerticalStrut(4), BorderLayout.NORTH);
		getContentPane().add(Box.createHorizontalStrut(4), BorderLayout.EAST);
		getContentPane().add(Box.createHorizontalStrut(4), BorderLayout.WEST);
		getContentPane().add(fieldBox(), BorderLayout.CENTER);
		getContentPane().add(buttonPanel(), BorderLayout.SOUTH);
		
		show();
		holderField.requestFocus();
	}
	
	private Box fieldBox() {
		Box fieldBox = Box.createVerticalBox();
		addFields(fieldBox);
		return fieldBox;
	}
	
	protected void addFields(Box fieldBox) {
		fieldBox.add(labelContainer("Holder"));
		fieldBox.add(holderField);
	}
		
	private JPanel buttonPanel() {
		JPanel buttonPanel = new JPanel();
		addButtons(buttonPanel);
		return buttonPanel;
	}
	
	abstract protected void addButtons(JPanel buttonPanel);

	protected Container labelContainer(String text)	{	
		Box box = Box.createHorizontalBox();
		JLabel label = new JLabel(text);
		box.add(label);
		box.add(Box.createHorizontalGlue());
		return box;
	}

    protected Component gap() {
		return Box.createVerticalStrut(3);
	}

    protected long parse(String numericString) throws ParseException {
        return new DecimalFormat("#").parse(numericString).longValue(); //The exception thrown has a better message than Long.parseLong(String).
    }

}
